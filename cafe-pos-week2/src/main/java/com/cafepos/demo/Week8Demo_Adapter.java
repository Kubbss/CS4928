package com.cafepos.demo;
import com.cafepos.pricing.ReceiptPrinter;
import com.cafepos.printing.*;
import vendor.legacy.LegacyThermalPrinter;
public final class Week8Demo_Adapter {

    public static void main(String[] args) {

        String receipt = "Order (LAT+L) x2\nSubtotal: 7.80\nTax (10%): 0.78\nTotal: 8.58";

        Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());

        printer.print(receipt);

        System.out.println("[Demo] Sent receipt via adapter.");
    }
}