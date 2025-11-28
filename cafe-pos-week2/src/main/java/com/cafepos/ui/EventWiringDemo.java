package com.cafepos.ui;
import com.cafepos.app.events.*;
import com.cafepos.infra.Wiring;
public final class EventWiringDemo {
    public static void main(String[] args) {
        var bus = new EventBus();
        var comp = Wiring.createDefault();
        var controller = new OrderController(comp.repo(),
                comp.checkout());
        bus.on(OrderCreated.class, e -> System.out.println("[UI] order created: " + e.orderId()));
        bus.on(OrderPaid.class, e -> System.out.println("[UI] order paid: " + e.orderId()));
        long id = 4201L;
        controller.createOrder(id);
        bus.emit(new OrderCreated(id));
// after a payment in your real flow:
        bus.emit(new OrderPaid(id));
    }
}