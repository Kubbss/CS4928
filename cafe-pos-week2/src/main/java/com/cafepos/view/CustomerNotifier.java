package com.cafepos.view;

import com.cafepos.domain.Order;
import com.cafepos.domain.OrderObserver;

public final class CustomerNotifier implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        System.out.printf("[Customer] Dear customer, your Order #%d has been updated: %s.%n",
                order.getId(), eventType);
    }
}
