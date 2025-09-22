package com.cafepos.domain;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTests {

    @Test
    void new_order_has_zero_subtotal() {
        Order o = new Order(1);
        assertEquals("0.00", o.subtotal().toString());
    }

    @Test
    void addItem_increases_subtotal() {
        Order o = new Order(2);
        LineItem li1 = new LineItem(new SimpleProduct("P-CF", "Coffee", Money.of(2.50)), 2);
        o.addItem(li1);

        assertEquals("5.00", o.subtotal().toString());
    }

    @Test
    void add_multiple_items_accumulates_subtotal() {
        Order o = new Order(3);
        o.addItem(new LineItem(new SimpleProduct("P-CF", "Coffee", Money.of(2.50)), 2));
        o.addItem(new LineItem(new SimpleProduct("P-BG", "Bagel", Money.of(3.00)), 1));

        assertEquals("8.00", o.subtotal().toString());
    }

    @Test
    void addItem_with_invalid_quantity_throws() {
        Order o = new Order(4);
        assertThrows(IllegalArgumentException.class, () ->
                o.addItem(new LineItem(new SimpleProduct("P-T", "Tea", Money.of(1.50)), 0))
        );
    }

    @Test
    void addItem_null_throws() {
        Order o = new Order(5);
        assertThrows(IllegalArgumentException.class, () -> o.addItem(null));
    }

    @Test
    void taxAtPercent_calculates_correctly() {
        Order o = new Order(6);
        o.addItem(new LineItem(new SimpleProduct("P-CF", "Coffee", Money.of(2.00)), 3));

        assertEquals("0.60", o.taxAtPercent(10).toString());
    }

    @Test
    void taxAtPercent_negative_throws() {
        Order o = new Order(7);
        o.addItem(new LineItem(new SimpleProduct("P-T", "Tea", Money.of(1.00)), 1));

        assertThrows(IllegalArgumentException.class, () -> o.taxAtPercent(-5));
    }

    @Test
    void totalWithTax_includes_subtotal_and_tax() {
        Order o = new Order(8);
        o.addItem(new LineItem(new SimpleProduct("P-LT", "Latte", Money.of(4.00)), 2));

        assertEquals("8.80", o.totalWithTax(10).toString());
    }

    @Test
    void order_totals() {
        var p1 = new SimpleProduct("A", "A", Money.of(2.50));
        var p2 = new SimpleProduct("B", "B", Money.of(3.50));
        var o = new Order(9);

        o.addItem(new LineItem(p1, 2));
        o.addItem(new LineItem(p2, 1));

        assertEquals("8.50", o.subtotal().toString());
        assertEquals("0.85", o.taxAtPercent(10).toString());
        assertEquals("9.35", o.totalWithTax(10).toString());
    }
}
