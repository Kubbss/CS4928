package com.cafepos.pricing;

import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolicyTests {

    @Test
    void loyalty_discount_5_percent() {
        DiscountPolicy d = new LoyaltyPercentDiscount(5);
        assertEquals("0.39", d.discountOf(Money.of(7.80)).toString());
    }

    @Test
    void fixed_rate_tax_10_percent() {
        TaxPolicy t = new FixedRateTaxPolicy(10);
        assertEquals("0.74", t.taxOn(Money.of(7.41)).toString());
    }

    @Test
    void pricing_pipeline_matches_math() {
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
        var pr = pricing.price(Money.of(7.80));
        assertEquals("0.39", pr.discount().toString());
        assertEquals("0.74", pr.tax().toString());
        assertEquals("8.15", pr.total().toString());
    }

    @Test
    void fixed_coupon_is_capped_at_subtotal() {
        var d = new FixedCouponDiscount(Money.of(5.00));
        assertEquals("3.30", d.discountOf(Money.of(3.30)).toString());
    }
}

