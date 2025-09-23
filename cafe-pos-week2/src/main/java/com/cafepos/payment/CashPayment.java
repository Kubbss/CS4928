package com.cafepos.payment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.domain.Order;

public final class CashPayment implements PaymentStrategy {

    @Override
    public void pay(Order order) {
        System.out.println("[Cash] Customer paid " + order.totalWithTax(10) + " EUR");
    }
}
