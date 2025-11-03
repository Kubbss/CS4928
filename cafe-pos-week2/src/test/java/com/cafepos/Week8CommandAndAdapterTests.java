package com.cafepos;

import com.cafepos.command.*;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import vendor.legacy.LegacyThermalPrinter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Week8CommandAndAdapterTests {

    // ---- 1) Command → Receiver + Undo via PosRemote ----
    @Test
    void remote_calls_command_and_undo_reverts_last_add() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(3);

        remote.setSlot(0, new AddItemCommand(service, "ESP+SHOT", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));

        int before = service.order().getItems().size();
        remote.press(0); // add espresso+shot
        remote.press(1); // add latte large x2

        int afterTwoAdds = service.order().getItems().size();
        assertEquals(before + 2, afterTwoAdds);

        remote.undo(); // should remove last-added item
        int afterUndo = service.order().getItems().size();
        assertEquals(afterTwoAdds - 1, afterUndo);
    }

    // ---- 2) MacroCommand executes and undoes all steps (reverse order) ----
    @Test
    void macro_command_executes_all_and_undo_restores_state() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        Command addEsp = new AddItemCommand(service, "ESP", 1);
        Command addLat = new AddItemCommand(service, "LAT+L", 1);
        MacroCommand macro = new MacroCommand(addEsp, addLat);

        int before = service.order().getItems().size();
        macro.execute();
        int afterExec = service.order().getItems().size();
        assertEquals(before + 2, afterExec);

        macro.undo(); // should undo both adds in reverse order
        int afterUndo = service.order().getItems().size();
        assertEquals(before, afterUndo);
    }

    // ---- 3) Adapter: convert text to bytes ----
    static class FakeLegacy extends LegacyThermalPrinter {
        int lastLen = -1;
        @Override
        public void legacyPrint(byte[] payload) { lastLen = payload.length; }
    }

    @Test
    void adapter_converts_text_to_bytes() {
        FakeLegacy fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);

        p.print("ABC");
        assertTrue(fake.lastLen >= 3);
    }

    // ---- 4) Integration: two adds + pay via remote; subtotal matches Week5 pricing ----
    static final class FakePayment implements PaymentStrategy {
        boolean called = false;
        Order lastOrder = null;
        @Override
        public void pay(Order order) {
            called = true;
            lastOrder = order;
        }
    }

    @Test
    void integration_remote_adds_and_pay_order_subtotal_matches_week5_prices() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(3);

        // Fake payment so we can assert pay() was called
        FakePayment payment = new FakePayment();
        Command addEspShotOat = new AddItemCommand(service, "ESP+SHOT+OAT", 1);
        Command addLatLarge = new AddItemCommand(service, "LAT+L", 2);
        Command payCommand = new PayOrderCommand(service, payment, 10);

        remote.setSlot(0, addEspShotOat);
        remote.setSlot(1, addLatLarge);
        remote.setSlot(2, payCommand);

        remote.press(0); // ESP+SHOT+OAT x1
        remote.press(1); // LAT+L x2
        remote.press(2); // pay

        // Week 5 prices:
        // ESP+SHOT+OAT = 3.80
        // LAT+L        = 3.90
        // subtotal = 3.80 + 2*3.90 = 11.60
        String subtotalStr = service.order().subtotal().toString();
        assertEquals("11.60", subtotalStr);

        assertTrue(payment.called);
        assertNotNull(payment.lastOrder);
    }
}

