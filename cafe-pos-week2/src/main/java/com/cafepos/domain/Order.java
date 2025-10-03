package com.cafepos.domain;

import com.cafepos.common.Money;
import com.cafepos.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class Order implements OrderPublisher {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    @Override
    public void register(OrderObserver o) {
        if (o == null) return;
        if (!observers.contains(o)) observers.add(o);
    }

    @Override
    public void unregister(OrderObserver o) {
        if (o == null) return;
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver obs : observers) {
            obs.updated(order, eventType);
        }
    }

    @Override
    private void notifyObservers(String eventType) {
        notifyObservers(this, eventType);
    }

    public void addItem(LineItem li) {
        if (li == null) {
            throw new IllegalArgumentException("LineItem is required");
        }
        if (li.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }
        items.add(li);
        notifyObservers("itemAdded");
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

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy is required");
        }

        strategy.pay(this);
        notifyObservers("paid");
    }

    public void markReady() {
        notifyObservers("ready");
    }

    public long getId() {
        return id;
    }

    public List<LineItem> getItems() {
        return items;
    }
}