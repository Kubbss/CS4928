package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.checkout.CheckoutService;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.WalletPayment;
import com.cafepos.pricing.*;

import java.util.ArrayList;
import java.util.Scanner;

import static com.cafepos.demo.Week6DemoCLI.askCardNumber;

public class Week6DemoCLIOrder {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ProductFactory factory = new ProductFactory();
        ArrayList<Product> products = new ArrayList<Product>();
        Order order = new Order(OrderIds.next());
        String orderMaker = "";
        int itemNo = -1;

        System.out.println("Welcome to Cafe Pos Demo");

        boolean exit = true;

        while (exit) {

            System.out.println("""
                     Choose base product
                     (1) Espresso    [2.50]
                     (2) Latte       [3.20]
                     (3) Cappuccino  [3.00]
                     (4) None
                     """);

            String baseChoice = input.nextLine();

            switch (baseChoice) {
                case "1":
                    orderMaker = "ESP";
                    break;
                case "2":
                    orderMaker = "LAT";
                    break;
                case "3":
                    orderMaker = "CAP";
                    break;
                case "4":
                    exit = false;
                    continue;
            }

            boolean extras = true;

            while (extras) {
                System.out.println("""
                         Add any additives
                         (1) Extra Shot     [0.80]
                         (2) Oat Milk       [0.50]
                         (3) Syrup          [0.40]
                         (4) Large Size     [0.70]
                         (5) None
                         """);

                String extraChoice = input.nextLine();

                switch (extraChoice) {
                    case "5":
                        extras = false;
                        break;
                    case "1":
                        orderMaker += "+SHOT";
                        break;
                    case "2":
                        orderMaker += "+OAT";
                        break;
                    case "3":
                        orderMaker += "+SYP";
                        break;
                    case "4":
                        orderMaker += "+L";
                        break;
                }
            }

            
            



            
            itemNo++;
            products.add(factory.create(orderMaker));
            order.addItem(new LineItem(products.get(itemNo), 1));

            System.out.println(orderMaker);
        }
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

        PricingService pricing = new PricingService(discount, tax);
        ReceiptPrinter printer = new ReceiptPrinter();

        CheckoutService itemCheckout = new CheckoutService(factory, pricing, printer, finalPayment, 10);
        System.out.println("===========Receipt==========");
        System.out.println(itemCheckout.checkout(order));
        System.out.println("=============================");
        System.out.println("Original Order below for comparison");
        System.out.println("=============================");

        System.out.println("Order #" + order.getId());
        for (LineItem li : order.getItems()) {
            System.out.println(" - " + li.product().name() + " x" + li.quantity() + " = " + li.lineTotal());
        }
        System.out.println("Subtotal: " + order.subtotal());
        System.out.println("Tax (10%): " + order.taxAtPercent(10));
        System.out.println("Total: " + order.totalWithTax(10));
    }
}
