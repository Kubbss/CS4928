package com.cafepos.smells;
import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.catalog.Product;

// God Class & Long Method
public class OrderManagerGod {
    //Global / Static States
    public static int TAX_PERCENT = 10;
    //Global / Static States
    public static String LAST_DISCOUNT_CODE = null;

    //Feature Envy / Shotgun Surgery (it constructs ProductFactory and makes products here instead of letting something else do it)
    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        ProductFactory factory = new ProductFactory();
        
        Product product = factory.create(recipe);
        
        //Duplicated Logic (Money and BigDecimal manipulation, if we need to do this again, we will need to re-write this, it should be in a reusable method)
        Money unitPrice;
        try {
            var priced = product instanceof com.cafepos.decorator.Priced
                    p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }
        
        //Primitive Obsession (raw primitive handling [qty] in the middle of business logic)
        if (qty <= 0) qty = 1;
        Money subtotal = unitPrice.multiply(qty);
        Money discount = Money.zero();
        
        //Primitive Obsession (discount types are plain strings with manual if-else checks)
        if (discountCode != null) {
            //Duplicated Logic (the discount calculation is done again later for tax)
            if (discountCode.equalsIgnoreCase("LOYAL5")) {
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5))
                        .divide(java.math.BigDecimal.valueOf(100)));
            } else if (discountCode.equalsIgnoreCase("COUPON1")) {
                discount = Money.of(1.00);
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            //Global static state (writes to a static variable)
            LAST_DISCOUNT_CODE = discountCode;
        }
        
        //Duplicated Logic (BigDecimal math inline here)
        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
        
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();
        
        //Duplicated Logic and Shotgun Surgery Risk (tax calculation (BigDecimal) inline and changing tax rules)
        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
                .divide(java.math.BigDecimal.valueOf(100)));
        
        var total = discounted.add(tax);
        
        //Primitive Obsession (payment type decided by strings)
        if (paymentType != null) {
            if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("[Cash] Customer paid " + total + " EUR");
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }
        }
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
                receipt.append("Subtotal: ").append(subtotal).append("\n");
        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
                receipt.append("Total: ").append(total);
        String out = receipt.toString();
        if (printReceipt) {
            System.out.println(out);
        }
        return out;
    }
}
