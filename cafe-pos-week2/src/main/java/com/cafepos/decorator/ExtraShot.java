package com.cafepos.decorator;
import com.cafepos.catalog.Product;
import com.cafepos.common.Money;

public final class ExtraShot extends ProductDecorator implements Priced {
    private static final Money SURCHARGE = Money.of(0.80);
    public ExtraShot(Product base) { super(base); }
    @Override public String name() { return base.name() + " + Extra Shot"; }
    public Money price() { return (base instanceof Priced p
                ? p.price() : base.basePrice()).add(SURCHARGE); }
}
