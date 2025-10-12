package com.cafepos.factory;

import com.cafepos.catalog.Product;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.decorator.ExtraShot;
import com.cafepos.decorator.OatMilk;
import com.cafepos.decorator.Priced;
import com.cafepos.decorator.SizeLarge;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductFactoryTest {

    private static Money priceOf(Product p) {
        return (p instanceof Priced priced) ? priced.price() : p.basePrice();
    }

    @Test
    void builds_espresso_with_shot_and_oat() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+SHOT+OAT");
        assertEquals("3.80", priceOf(p).toString());
        String name = p.name().toLowerCase();
        assertTrue(name.contains("espresso"));
        assertTrue(name.contains("extra shot"));
        assertTrue(name.contains("oat"));
        assertEquals("P-ESP", p.id());
    }

    @Test
    void builds_latte_large_and_line_item_uses_decorated_price() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("LAT+L");
        assertEquals("3.90", priceOf(p).toString());
        LineItem li = new LineItem(p, 2);
        assertEquals("7.80", li.lineTotal().toString());
    }

    @Test
    void rejects_unknown_base() {
        ProductFactory f = new ProductFactory();
        assertThrows(IllegalArgumentException.class, () -> f.create("MOCHA+SHOT"));
    }

    @Test
    void rejects_unknown_addon() {
        ProductFactory f = new ProductFactory();
        assertThrows(IllegalArgumentException.class, () -> f.create("ESP+XYZ"));
    }

    @Test
    void null_and_blank_recipe_rejected() {
        ProductFactory f = new ProductFactory();
        assertThrows(IllegalArgumentException.class, () -> f.create(null));
        assertThrows(IllegalArgumentException.class, () -> f.create("  "));
    }

    @Test
    void chaining_multiple_addons_sums_surcharges() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+OAT+SHOT+SYP");
        assertEquals("4.20", priceOf(p).toString());
    }

    @Test
    void simple_product_price_equals_baseprice() {
        SimpleProduct base = new SimpleProduct("X", "X", Money.of(1.23));
        assertEquals("1.23", base.basePrice().toString());
        assertTrue(base instanceof Priced);
        assertEquals("1.23", ((Priced) base).price().toString());
    }

    @Test void decorator_single_addon() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product withShot = new ExtraShot(espresso);
        assertEquals("Espresso + Extra Shot", withShot.name());
// if using Priced interface:
        assertEquals(Money.of(3.30), ((Priced) withShot).price());
    }
    @Test void decorator_stacks() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product decorated = new SizeLarge(new OatMilk(new
                ExtraShot(espresso)));
        assertEquals("Espresso + Extra Shot + Oat Milk (Large)",
                decorated.name());
        assertEquals(Money.of(4.50), ((Priced) decorated).price());
    }
    @Test void factory_parses_recipe() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+SHOT+OAT");
        assertTrue(p.name().contains("Espresso") &&
                p.name().contains("Oat Milk"));
    }
    @Test void order_uses_decorated_price() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso",
                Money.of(2.50));
        Product withShot = new ExtraShot(espresso); // 3.30
        Order o = new Order(1);
        o.addItem(new LineItem(withShot, 2));
        assertEquals(Money.of(6.60), o.subtotal());
    }
}
