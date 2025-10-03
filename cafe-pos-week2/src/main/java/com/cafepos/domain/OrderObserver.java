package com.cafepos.domain;

public interface OrderObserver {
    void updated(Order order, String eventType);
}