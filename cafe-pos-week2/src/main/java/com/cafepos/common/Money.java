package com.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money implements Comparable<Money> {
    private final BigDecimal amount;

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    private Money(BigDecimal a) {
        if (a == null) throw new IllegalArgumentException("amount required");
        BigDecimal scaled = a.setScale(2, RoundingMode.HALF_UP);
        if (scaled.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        this.amount = scaled;
    }

    public Money add(Money other) {
        if (other == null) throw new IllegalArgumentException("other required");
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("quantity must not be negative");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)));
    }

    public BigDecimal amount() {
        return amount;
    }

    @Override
    public String toString() {
        return amount.toString();
    }

    @Override
    public int compareTo(Money o) {
        return this.amount.compareTo(o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money m)) return false;
        return this.toString().equals(m.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}


