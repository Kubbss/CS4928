package com.cafepos.app.events;

public record OrderCreated(long orderId) implements OrderEvent { }
