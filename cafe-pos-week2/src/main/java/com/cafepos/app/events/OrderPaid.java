package com.cafepos.app.events;

public record OrderPaid(long orderId) implements OrderEvent {}
