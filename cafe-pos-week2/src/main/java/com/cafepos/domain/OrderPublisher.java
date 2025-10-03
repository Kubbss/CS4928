package com.cafepos.domain;

public interface OrderPublisher {
    void register(OrderObserver o);
    void unregister(OrderObserver o);
    void notifyObservers(Order order, String eventType);
}
