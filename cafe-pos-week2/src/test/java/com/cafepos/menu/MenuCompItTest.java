package com.cafepos.menu;

import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MenuCompItTest {

    @Test void depth_first_iteration_collects_all_nodes() {
        Menu root = new Menu("ROOT");
        Menu a = new Menu("A");
        Menu b = new Menu("B");
        root.add(a); root.add(b);
        a.add(new MenuItem("x", Money.of(1.0), true));
        b.add(new MenuItem("y", Money.of(2.0), false));
        List<String> names =
                root.allItems().stream().map(MenuComponent::name).toList();
        assertTrue(names.contains("x"));
        assertTrue(names.contains("y"));
    }

    @Test
    void vegetarianItems_returns_only_veg_items() {
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

        List<MenuItem> veg = root.vegetarianItems();

        assertEquals(3, veg.size());
        List<String> vegNames = veg.stream()
                .map(MenuItem::name)
                .toList();

        assertTrue(vegNames.contains("Espresso"));
        assertTrue(vegNames.contains("Latte (Large)"));
        assertTrue(vegNames.contains("Oat Cookie"));
    }

    @Test
    void integration_menu_item_with_productFactory_matches_order_subtotal() {
        ProductFactory factory = new ProductFactory();

        MenuItem espressoItem = new MenuItem("Espresso", Money.of(2.50), true);
        
        var product = factory.create("ESP");

        Order order = new Order(1L);
        int qty = 2;
        order.addItem(new LineItem(product, qty));

        Money expected = espressoItem.price().multiply(qty);
        
        assertEquals(expected.toString(), order.subtotal().toString());
    }
}

