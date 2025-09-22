package com.cafepos.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTests {

    @Test
    void createMoney_and_zero() {
        assertEquals("2.50", Money.of(2.50).toString());
        assertEquals("0.00", Money.zero().toString());
    }

    @Test
    void add_two_moneys() {
        Money m1 = Money.of(2.00);
        Money m2 = Money.of(3.50);
        Money sum = m1.add(m2);

        assertEquals("5.50", sum.toString());
    }

    @Test
    void multiply_money_by_quantity() {
        Money m = Money.of(2.50);
        Money result = m.multiply(3);

        assertEquals("7.50", result.toString());
    }

    @Test
    void multiply_with_zero_results_in_zero() {
        Money m = Money.of(9.99);
        Money result = m.multiply(0);

        assertEquals("0.00", result.toString());
    }

    @Test
    void multiply_negative_quantity_throws_exception() {
        Money m = Money.of(1.00);
        assertThrows(IllegalArgumentException.class, () -> m.multiply(-1));
    }

    @Test
    void negative_amount_not_allowed() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(-5.00));
    }

    @Test
    void rounding_to_two_decimals() {
        Money m = Money.of(2.555); // should round half up
        assertEquals("2.56", m.toString());
    }

    @Test
    void compareTo_orders_correctly() {
        Money smaller = Money.of(2.00);
        Money larger = Money.of(3.00);

        assertTrue(smaller.compareTo(larger) < 0);
        assertTrue(larger.compareTo(smaller) > 0);
        assertEquals(0, smaller.compareTo(Money.of(2.00)));
    }
}