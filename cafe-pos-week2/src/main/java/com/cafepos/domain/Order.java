package com.cafepos.domain;

import com.cafepos.common.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class Order {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public void addItem(LineItem li) {
        if (li == null) {
            throw new IllegalArgumentException("LineItem is required");
        }
        if (li.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }
        items.add(li);
    }

    public Money subtotal() {
        return items.stream()
                .map(LineItem::lineTotal)
                .reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0) {
            throw new IllegalArgumentException("percent must be >= 0");
        }
        Money multiplied = subtotal().multiply(percent);
        BigDecimal numeric = BigDecimal.valueOf(Double.parseDouble(multiplied.toString()));

        BigDecimal divided = numeric.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return Money.of(divided.doubleValue());
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public long getId() {
        return id;
    }

    public List<LineItem> getItems() {
        return items;
    }
}