package com.boozefy.android.model;

/**
 * Created by Mauricio Giordano on 1/22/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class OrderBeverage {

    private long id;
    private long orderId;
    private long beverageId;
    private int amount;

    public OrderBeverage() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getBeverageId() {
        return beverageId;
    }

    public void setBeverageId(long beverageId) {
        this.beverageId = beverageId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
