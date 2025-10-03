package com.cafepos.view;

import com.cafepos.domain.Order;
import com.cafepos.domain.OrderObserver;

public final class DeliveryDesk implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("ready".equals(eventType)) {
            System.out.printf("[Delivery] Order #%d is ready for delivery%n", order.getId());
        }
    }
}
