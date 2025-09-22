package com.cafepos.domain;

public final class OrderIds {
    private static long orderCounter = 0;

    private OrderIds() {
    }

    public static long next() {
        return orderCounter++;
    }
}
