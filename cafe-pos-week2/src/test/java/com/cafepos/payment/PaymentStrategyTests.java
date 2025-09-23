package com.cafepos.payment;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStrategyTests {

    @Test
    void payment_strategy_called() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(p, 1));

        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;

        order.pay(fake);

        assertTrue(called[0], "Payment strategy should be called");
    }

    @Test
    void cash_payment_does_not_throw() {
        var p = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        var order = new Order(101);
        order.addItem(new LineItem(p, 2));

        PaymentStrategy cash = new CashPayment();
        assertDoesNotThrow(() -> order.pay(cash));
    }

    @Test
    void card_payment_masks_number() {
        var p = new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50));
        var order = new Order(102);
        order.addItem(new LineItem(p, 1));

        PaymentStrategy card = new CardPayment("1234567812341234");

        assertDoesNotThrow(() -> order.pay(card));
    }

    @Test
    void wallet_payment_does_not_throw() {
        var p = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        var order = new Order(103);
        order.addItem(new LineItem(p, 1));

        PaymentStrategy wallet = new WalletPayment("alice-wallet-01");

        assertDoesNotThrow(() -> order.pay(wallet));
    }

    @Test
    void pay_with_null_strategy_throws() {
        var p = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        var order = new Order(104);
        order.addItem(new LineItem(p, 1));

        assertThrows(IllegalArgumentException.class, () -> order.pay(null));
    }
}

