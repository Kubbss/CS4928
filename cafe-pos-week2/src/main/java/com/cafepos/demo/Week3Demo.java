package com.cafepos.demo;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.payment.*;

public final class Week3Demo {
    public static void main(String[] args) {
        Catalog catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
// Cash payment
                Order order1 = new Order(OrderIds.next());
        order1.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
                order1.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
                        System.out.println("Order #" + order1.getId() + " Total: " + order1.totalWithTax(10));
        order1.pay(new CashPayment());
// Card payment
        Order order2 = new Order(OrderIds.next());
        order2.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
                order2.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
                        System.out.println("Order #" + order2.getId() + " Total: " + order2.totalWithTax(10));
        order2.pay(new CardPayment("1234567812341234"));
    }
}
