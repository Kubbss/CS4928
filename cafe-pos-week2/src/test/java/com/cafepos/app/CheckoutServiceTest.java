package com.cafepos.app;

import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import com.cafepos.infra.InMemoryOrderRepository;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.pricing.LoyaltyPercentDiscount;
import com.cafepos.pricing.NoDiscount;
import com.cafepos.pricing.PricingService;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutServiceTest {

    @Test
    void checkout_receipt_matches_order_and_pricing_service() {
        var repo = new InMemoryOrderRepository();
        var pricing = new PricingService(
                new LoyaltyPercentDiscount(5),
                new FixedRateTaxPolicy(10)
        );
        var service = new CheckoutService(repo, pricing);
        var factory = new ProductFactory();

        long id = 4101L;
        int taxPercent = 10;

        Order order = new Order(id);
        order.addItem(new LineItem(factory.create("ESP+SHOT+OAT"), 1));
        order.addItem(new LineItem(factory.create("LAT+L"), 2));
        repo.save(order);

        String receipt = service.checkout(id, taxPercent);

        // 1) Header
        assertTrue(receipt.contains("Order #" + id));

        // 2) Each item’s name and price appear somewhere
        for (LineItem li : order.getItems()) {
            String name = li.product().name();
            String priceText = li.lineTotal().toString();

            assertTrue(receipt.contains(name),
                    "Receipt should contain product name: " + name);
            assertTrue(receipt.contains(priceText),
                    "Receipt should contain line total: " + priceText);
        }

        // 3) Numbers from PricingService
        Money subtotal = order.subtotal();
        var pr = pricing.price(subtotal);

        assertTrue(receipt.contains("Subtotal: " + pr.subtotal()),
                "Receipt should contain correct subtotal");

        if (pr.discount().asBigDecimal().signum() > 0) {
            // Just check presence of "Discount:" and the numeric value,
            // not exact spacing or minus formatting
            assertTrue(receipt.contains("Discount:"),
                    "Receipt should contain a discount line when discount > 0");
            assertTrue(receipt.contains(pr.discount().toString()),
                    "Receipt should contain the discount amount");
        } else {
            assertFalse(receipt.contains("Discount:"),
                    "Receipt should not contain discount line when discount = 0");
        }

        assertTrue(receipt.contains("Tax (" + taxPercent + "%): " + pr.tax()),
                "Receipt should contain correct tax line");
        assertTrue(receipt.contains("Total: " + pr.total()),
                "Receipt should contain correct total line");
    }

    @Test
    void checkout_without_discount_has_no_discount_line() {
        var repo = new InMemoryOrderRepository();
        var pricing = new PricingService(
                new NoDiscount(),
                new FixedRateTaxPolicy(10)
        );
        var service = new CheckoutService(repo, pricing);
        var factory = new ProductFactory();

        long id = 5001L;
        int taxPercent = 10;

        Order order = new Order(id);
        order.addItem(new LineItem(factory.create("ESP"), 1));
        repo.save(order);

        String receipt = service.checkout(id, taxPercent);

        // discount should be zero in this setup
        assertFalse(receipt.contains("Discount:"),
                "Receipt should not show a discount line when using NoDiscount");

        Money subtotal = order.subtotal();
        var pr = pricing.price(subtotal);

        assertTrue(receipt.contains("Subtotal: " + pr.subtotal()));
        assertTrue(receipt.contains("Tax (" + taxPercent + "%): " + pr.tax()));
        assertTrue(receipt.contains("Total: " + pr.total()));
    }

    @Test
    void checkout_throws_when_order_not_found() {
        var repo = new InMemoryOrderRepository();
        var pricing = new PricingService(
                new NoDiscount(),
                new FixedRateTaxPolicy(10)
        );
        var service = new CheckoutService(repo, pricing);

        assertThrows(NoSuchElementException.class,
                () -> service.checkout(9999L, 10),
                "Checkout should throw when order id is not in the repository");
    }
}
