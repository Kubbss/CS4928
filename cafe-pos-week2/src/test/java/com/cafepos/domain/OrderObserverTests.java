package com.cafepos.domain;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.payment.PaymentStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderObserverTests {

    @Test
    void observers_notified_on_item_add() {
        var product = new SimpleProduct("A", "A", Money.of(2.00));
        var order = new Order(1);

        List<String> events = new ArrayList<>();
        order.register((o, evt) -> events.add(evt));

        order.addItem(new LineItem(product, 1));

        assertTrue(events.contains("itemAdded"));
    }

    @Test
    void multiple_observers_receive_ready_event() {
        var order = new Order(2);

        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        OrderObserver obsA = (o, evt) -> a.add(evt);
        OrderObserver obsB = (o, evt) -> b.add(evt);

        order.register(obsA);
        order.register(obsB);

        order.markReady();

        assertTrue(a.contains("ready"));
        assertTrue(b.contains("ready"));
    }

    @Test
    void paid_event_delivered_after_pay() {
        var order = new Order(3);
        List<String> events = new ArrayList<>();
        order.register((o, evt) -> events.add(evt));

        PaymentStrategy silentPayment = new PaymentStrategy() {
            @Override public void pay(Order o) { /* no console output for tests */ }
        };

        order.pay(silentPayment);

        assertTrue(events.contains("paid"));
    }

    @Test
    void unregister_stops_notifications() {
        var order = new Order(4);
        List<String> events = new ArrayList<>();
        OrderObserver obs = (o, evt) -> events.add(evt);

        order.register(obs);
        order.unregister(obs);

        order.markReady();

        assertTrue(events.isEmpty(), "Observer should not receive events after unregister");
    }

    @Test
    void registering_same_observer_twice_does_not_duplicate_delivery() {
        var product = new SimpleProduct("B", "B", Money.of(1.50));
        var order = new Order(5);

        List<String> events = new ArrayList<>();
        OrderObserver obs = (o, evt) -> events.add(evt);

        order.register(obs);
        order.register(obs); // should be ignored as a duplicate

        order.addItem(new LineItem(product, 1));

        assertEquals(1, events.size(), "Expected exactly one notification");
        assertEquals("itemAdded", events.getFirst());
    }
}

