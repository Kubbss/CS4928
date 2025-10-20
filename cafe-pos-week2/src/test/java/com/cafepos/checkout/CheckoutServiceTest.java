package com.cafepos.checkout;

import com.cafepos.catalog.Product;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.pricing.*;
import com.cafepos.smells.OrderManagerGod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutServiceTest {

    static final class FakePayment implements PaymentStrategy {
        boolean called;
        Order last;
        @Override public void pay(Order order) { called = true; last = order; }
    }

    @Test
    void receipt_matches_god_for_lat_l_qty2_loyal5_tax10() {
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var payment = new FakePayment();
        var service = new CheckoutService(new ProductFactory(), pricing, printer, payment, 10);

        String newReceipt = service.checkout("LAT+L", 2);
        String oldReceipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);

        assertEquals(oldReceipt, newReceipt);
        assertTrue(payment.called);
        assertNotNull(payment.last);
        assertEquals(1, payment.last.getItems().size());
    }

    @Test
    void receipt_no_discount_matches_god_for_esp_shot_oat() {
        var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var payment = new FakePayment();
        var service = new CheckoutService(new ProductFactory(), pricing, printer, payment, 10);

        String newReceipt = service.checkout("ESP+SHOT+OAT", 1);
        String oldReceipt = OrderManagerGod.process("ESP+SHOT+OAT", 1, "CASH", "NONE", false);

        assertEquals(oldReceipt, newReceipt);
        assertTrue(newReceipt.contains("Subtotal: 3.80"));
        assertTrue(newReceipt.contains("Tax (10%): 0.38"));
        assertTrue(newReceipt.contains("Total: 4.18"));
    }

    @Test
    void quantity_is_clamped_to_one() {
        var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var payment = new FakePayment();
        var service = new CheckoutService(new ProductFactory(), pricing, printer, payment, 10);

        String receipt = service.checkout("ESP+SHOT", 0);
        assertTrue(receipt.startsWith("Order (ESP+SHOT) x1"));
        assertTrue(payment.called);
    }

    @Test
    void tax_label_matches_injected_policy_percent() {
        var tax = new FixedRateTaxPolicy(12);
        var pricing = new PricingService(new NoDiscount(), tax);
        var printer = new ReceiptPrinter();
        var payment = new FakePayment();
        var service = new CheckoutService(new ProductFactory(), pricing, printer, payment, 12);

        String receipt = service.checkout("LAT+L", 2);
        assertTrue(receipt.contains("Tax (12%):"));
    }

    @Test
    void priced_unit_comes_from_decorators() {
        var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var payment = new FakePayment();
        var service = new CheckoutService(new ProductFactory(), pricing, printer, payment, 10);

        String receipt = service.checkout("LAT+L", 2);
        assertTrue(receipt.contains("Subtotal: 7.80"));
    }
}
