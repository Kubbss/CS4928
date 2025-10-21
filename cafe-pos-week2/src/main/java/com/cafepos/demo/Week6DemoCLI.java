package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.WalletPayment;
import com.cafepos.checkout.CheckoutService;
import com.cafepos.pricing.*;
import com.cafepos.view.CustomerNotifier;
import com.cafepos.view.DeliveryDesk;
import com.cafepos.view.KitchenDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Week6DemoCLI {

    static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Order order = new Order(OrderIds.next());
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());
        
        System.out.println("Welcome to Cafe POS (Week 6)");
        System.out.println("""
                    Choose base product
                      (1) Espresso
                      (2) Latte
                      (3) Cappuccino
                      (4) Done
                    """);

        String baseChoice = input.nextLine().trim();
        String recipe = "";
        switch (baseChoice) {
            case "1" -> recipe = "ESP";
            case "2" -> recipe = "LAT";
            case "3" -> recipe = "CAP";
            default -> {System.out.println("No items added. Goodbye."); return;}
        }

        boolean extras = true;
        while (extras) {
            System.out.println("""
                        Add any additives
                          (1) Extra Shot
                          (2) Oat Milk
                          (3) Syrup
                          (4) Large Size
                          (5) None
                        """);
            String extraChoice = input.nextLine().trim();
            switch (extraChoice) {
                case "1" -> recipe += "+SHOT";
                case "2" -> recipe += "+OAT";
                case "3" -> recipe += "+SYP";
                case "4" -> recipe += "+L";
                case "5" -> extras = false;
                default -> {}
            }
        }

        System.out.print("Quantity (default 1): ");
        int q = Integer.parseInt(input.nextLine().trim());

        System.out.println("""
                Do you have a discount?
                  1) None
                  2) Loyalty 5% (LOYAL5)
                  3) Coupon €1 (COUPON1)
                """);
        String d = input.nextLine().trim();
        DiscountPolicy discount = switch (d) {
            case "2" -> new LoyaltyPercentDiscount(5);
            case "3" -> new FixedCouponDiscount(Money.of(1.00));
            default -> new NoDiscount();
        };

        TaxPolicy tax = new FixedRateTaxPolicy(10);

        System.out.println("""
                Payment method:
                  1) Cash
                  2) Card
                  3) Wallet
                """);
        String pay = input.nextLine().trim();
        PaymentStrategy finalPayment = switch (pay) {
            case "2" -> new CardPayment(askCardNumber());
            case "3" -> new WalletPayment("cli-wallet");
            default  -> new CashPayment();
        };



        ProductFactory factory = new ProductFactory();
        PricingService pricing = new PricingService(discount, tax);
        ReceiptPrinter printer = new ReceiptPrinter();

        CheckoutService itemCheckout = new CheckoutService(factory, pricing, printer, finalPayment, 10);

        String newReceipt = itemCheckout.checkout(recipe, q);
        order.markReady();

        System.out.println("\nReceipt:\n" + newReceipt);


        System.out.println("Thanks! Order complete.");
    }
    public static String askCardNumber(){
        System.out.println("Please enter your card number: ");
        Scanner input = new Scanner(System.in);
        return input.nextLine().trim();
    }
}
