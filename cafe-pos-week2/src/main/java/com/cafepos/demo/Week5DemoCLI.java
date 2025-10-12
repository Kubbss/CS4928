package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;

import java.util.ArrayList;
import java.util.Scanner;

public class Week5DemoCLI {
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
                     (1) Espresso
                     (2) Latte
                     (3) Cappuccino
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
                         (1) Extra Shot
                         (2) Oat Milk
                         (3) Syrup
                         (4) Large Size
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
        System.out.println("Order #" + order.getId());
        for (LineItem li : order.getItems()) {
            System.out.println(" - " + li.product().name() + " x" + li.quantity() + " = " + li.lineTotal());
        }
        System.out.println("Subtotal: " + order.subtotal());
        System.out.println("Tax (10%): " + order.taxAtPercent(10));
        System.out.println("Total: " + order.totalWithTax(10));
    }
}
