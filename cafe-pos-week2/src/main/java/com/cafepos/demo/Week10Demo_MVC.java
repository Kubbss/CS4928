package com.cafepos.demo;
import com.cafepos.infra.Wiring;
import com.cafepos.ui.OrderController;
import com.cafepos.ui.ConsoleView;
public final class Week10Demo_MVC {
    public static void main(String[] args) {
        var c = Wiring.createDefault();
        var controller = new OrderController(c.repo(), c.checkout());
        var view = new ConsoleView();
        long id = 4101L;
        controller.createOrder(id);
        controller.addItem(id, "ESP+SHOT+OAT", 1);
        controller.addItem(id, "LAT+L", 2);
        String receipt = controller.checkout(id, 10);
        view.print(receipt);
    }
}