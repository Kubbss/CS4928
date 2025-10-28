package com.cafepos.checkout;

// Adapt to your Week-3 signature; if your strategy expects an Order,pass the real one here.
// If your strategy prints based on totals, wrap in a tiny adapter andcall after pricing.

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.pricing.PricingService;
import com.cafepos.pricing.ReceiptPrinter;

import javax.lang.model.type.UnionType;
import javax.xml.transform.Result;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public final class CheckoutService {
    private final ProductFactory factory;
    private final PricingService pricing;
    private final ReceiptPrinter printer;
    private final PaymentStrategy paymentStrategy;
    private final int taxPercent;
    
    public CheckoutService(ProductFactory factory, PricingService pricing, ReceiptPrinter printer,PaymentStrategy paymentStrategy, int taxPercent) {
        this.factory = factory;
        this.pricing = pricing;
        this.printer = printer;
        this.paymentStrategy = paymentStrategy;
        this.taxPercent = taxPercent;
    }
    public String checkout(String recipe, int qty) {
        Product product = factory.create(recipe);
        if (qty <= 0) qty = 1;
        Money unit = (product instanceof com.cafepos.decorator.Priced p)
                ? p.price() : product.basePrice();
        Money subtotal = unit.multiply(qty);
        var result = pricing.price(subtotal);
        Order payOrder = new Order(System.currentTimeMillis()%100000);
        payOrder.addItem(new LineItem(product, qty));
        paymentStrategy.pay(payOrder);
        return printer.format(recipe, qty, result, taxPercent);
    }
    
    public String checkout(Order order){
        int qty = order.getItems().size();
        List<LineItem> items = order.getItems();
        Money unit = Money.zero(); 
        
        for(int i = 0; i < qty; i++){
            Money productPrice = (items.get(i).product() instanceof com.cafepos.decorator.Priced p)
                    ? p.price() : items.get(i).product().basePrice();
            
            unit =  unit.add(productPrice);
        }
        Money subtotal = unit;
        var result = pricing.price(subtotal);
        StringBuilder orderString = new StringBuilder();
        orderString.append(STR."ID : \{order.getId()}\n");
        for (LineItem li : order.getItems()) {
            orderString.append(STR." - \{li.product().name()} x\{li.quantity()} = \{li.lineTotal()}\n");
        }
        
        return printer.format(orderString.toString(), qty, result, taxPercent);
    }
}
