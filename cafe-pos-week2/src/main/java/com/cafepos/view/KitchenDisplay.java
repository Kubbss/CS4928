package com.cafepos.view;

import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderObserver;

import java.util.List;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("itemAdded".equals(eventType)) {
            List<LineItem> items = order.getItems();
            if (!items.isEmpty()) {
                LineItem last = items.get(items.size() - 1);
                System.out.printf("[Kitchen] Order #%d: %dx %s added%n",
                        order.getId(),
                        last.quantity(),
                        last.product().name()
                );
            }
        } else if ("paid".equals(eventType)) {
            System.out.printf("[Kitchen] Order #%d: Payment received%n", order.getId());
        }
    }
}
