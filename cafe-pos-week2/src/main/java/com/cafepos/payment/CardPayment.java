package com.cafepos.payment;

import com.cafepos.domain.Order;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;
    public CardPayment(String cardNumber) {
        if (cardNumber.length() < 5) throw new IllegalArgumentException("Card Number is too short");
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(Order order) {
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        System.out.println("[Card] Customer paid with card ****" + last4);
    }
}
