package com.cafepos.demo;

import com.cafepos.command.AddItemCommand;
import com.cafepos.command.OrderService;
import com.cafepos.command.PayOrderCommand;
import com.cafepos.command.PosRemote;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.menu.Menu;
import com.cafepos.menu.MenuItem;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import com.cafepos.state.OrderFSM;
import com.cafepos.view.CustomerNotifier;
import com.cafepos.view.DeliveryDesk;
import com.cafepos.view.KitchenDisplay;
import vendor.legacy.LegacyThermalPrinter;

public final class FinalDemo {

    public static void main(String[] args) {
        System.out.println("=== Cafe POS Final Demo ===");

        commandDemo();
        compositeDemo();
        stateDemo();
        adapterDemo();

        System.out.println("=== Final demo finished ===");
    }

    private static void commandDemo() {
        System.out.println();
        System.out.println("=== Command Pattern: PosRemote + Commands + Undo ===");

        Order order = new Order(OrderIds.next());
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());

        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(3);

        PaymentStrategy payment = new CardPayment("final-demo-card");

        var addEspresso = new AddItemCommand(service, "ESP+SHOT+OAT", 1);
        var addLatte = new AddItemCommand(service, "LAT+L", 2);
        var payCommand = new PayOrderCommand(service, payment, 10);

        remote.setSlot(0, addEspresso);
        remote.setSlot(1, addLatte);
        remote.setSlot(2, payCommand);

        System.out.println("-- Press slot 0: add ESP+SHOT+OAT x1");
        remote.press(0);

        System.out.println("-- Press slot 1: add LAT+L x2");
        remote.press(1);

        printOrderSummary(order, 10);

        System.out.println("-- Undo last action (should remove LAT+L x2)");
        remote.undo();
        printOrderSummary(order, 10);

        System.out.println("-- Press slot 2: pay order");
        remote.press(2);

        System.out.println("-- Mark order ready (observers will print)");
        order.markReady();
    }

    private static void printOrderSummary(Order order, int taxPercent) {
        System.out.println("Order #" + order.getId());
        for (LineItem li : order.getItems()) {
            System.out.println(" - " + li.product().name() + " x" + li.quantity()
                    + " = " + li.lineTotal());
        }
        System.out.println("Subtotal: " + order.subtotal());
        System.out.println("Tax (" + taxPercent + "%): " + order.taxAtPercent(taxPercent));
        System.out.println("Total: " + order.totalWithTax(taxPercent));
        System.out.println();
    }

    private static void compositeDemo() {
        System.out.println();
        System.out.println("=== Composite + Iterator: Menu & Vegetarian Filter ===");

        Menu root = new Menu("CAFÉ MENU");
        Menu drinks = new Menu(" Drinks ");
        Menu coffee = new Menu("  Coffee ");
        Menu desserts = new Menu(" Desserts ");

        coffee.add(new MenuItem("Espresso", Money.of(2.50), true));
        coffee.add(new MenuItem("Latte (Large)", Money.of(3.90), true));
        drinks.add(coffee);

        desserts.add(new MenuItem("Cheesecake", Money.of(3.50), false));
        desserts.add(new MenuItem("Oat Cookie", Money.of(1.20), true));

        root.add(drinks);
        root.add(desserts);

        System.out.println("-- Full menu (depth-first print):");
        root.print();

        System.out.println();
        System.out.println("-- Vegetarian items:");
        for (MenuItem mi : root.vegetarianItems()) {
            System.out.println(" - " + mi.name() + " (" + mi.price() + ")");
        }
    }

    private static void stateDemo() {
        System.out.println();
        System.out.println("=== State Pattern: OrderFSM Lifecycle ===");

        OrderFSM fsm = new OrderFSM();
        System.out.println("Initial state: " + fsm.status());

        System.out.println("-- pay() from NEW");
        fsm.pay();
        System.out.println("State now: " + fsm.status());

        System.out.println("-- markReady() from PREPARING");
        fsm.markReady();
        System.out.println("State now: " + fsm.status());

        System.out.println("-- deliver() from READY");
        fsm.deliver();
        System.out.println("State now: " + fsm.status());

        System.out.println("-- try prepare() after DELIVERED (should be ignored)");
        fsm.prepare();
        System.out.println("State now: " + fsm.status());
    }

    private static void adapterDemo() {
        System.out.println();
        System.out.println("=== Adapter Pattern: Legacy Printer ===");

        LegacyThermalPrinter legacy = new LegacyThermalPrinter();
        Printer printer = new LegacyPrinterAdapter(legacy);

        String body = "Final demo receipt body\nTotal: 12.34 EUR\n";
        printer.print(body);
    }
}
