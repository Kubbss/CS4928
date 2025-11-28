package com.cafepos.app.events;
public sealed interface OrderEvent permits OrderCreated, OrderPaid { }

