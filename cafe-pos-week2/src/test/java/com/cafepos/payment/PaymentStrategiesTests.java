package com.cafepos.payment;

import com.cafepos.catalog.Catalog;
import com.cafepos.catalog.InMemoryCatalog;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

final class PaymentStrategiesTest {

    // 2x Espresso (2.50) + 1x Cookie (3.50) = 8.50; +10% tax => 9.35
    private Order sampleOrder() {
        Catalog catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));

        Order order = new Order(OrderIds.next());
        order.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
        order.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
        return order;
    }

    private String captureStdout(Runnable action) {
        PrintStream orig = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos)) {
            System.setOut(ps);
            action.run();
        } finally {
            System.setOut(orig);
        }
        return baos.toString().replace("\r\n", "\n").trim();
    }

    @Test
    void orderDelegatesToPaymentStrategy() {
        Order order = sampleOrder();
        final boolean[] called = {false};

        PaymentStrategy fake = o -> called[0] = true;

        order.pay(fake);

        assertTrue(called[0], "Order should delegate to the provided PaymentStrategy");
    }

    @Test
    void cashPayment_prints_expected_message_and_does_not_change_total() {
        Order order = sampleOrder();
        String before = order.totalWithTax(10).toString();

        String out = captureStdout(() -> new CashPayment().pay(order));

        assertTrue(out.contains("[Cash] Customer paid 9.35 EUR"),
                "CashPayment should print the expected confirmation");
        assertEquals(before, order.totalWithTax(10).toString(),
                "Payment strategies must not modify order totals");
    }

    @Test
    void cardPayment_prints_masked_last_4_and_does_not_change_total() {
        Order order = sampleOrder();
        String before = order.totalWithTax(10).toString();

        String out = captureStdout(() ->
                new CardPayment("1234567812341234").pay(order));

        assertTrue(out.contains("[Card] Customer paid 9.35 EUR with card ****1234"),
                "CardPayment should mask all but the last four digits with four asterisks");
        assertEquals(before, order.totalWithTax(10).toString(),
                "Payment strategies must not modify order totals");
    }

    @Test
    void cardPayment_rejects_too_short_numbers() {
        assertThrows(IllegalArgumentException.class,
                () -> new CardPayment("1234"),
                "Constructor should guard against unrealistically short card numbers");
    }

    @Test
    void walletPayment_prints_expected_message_and_does_not_change_total() {
        Order order = sampleOrder();
        String before = order.totalWithTax(10).toString();

        String out = captureStdout(() ->
                new WalletPayment("alice-wallet-01").pay(order));

        assertTrue(out.contains("[Wallet] Customer paid 9.35 EUR via wallet alice-wallet-01"),
                "WalletPayment should include the wallet id in the confirmation");
        assertEquals(before, order.totalWithTax(10).toString(),
                "Payment strategies must not modify order totals");
    }
}
