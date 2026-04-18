package com.sdet.sdet_practice.models;

import java.math.BigDecimal;

public class CartItem {

    private final String Name;
    private final BigDecimal unitPrice;
    private int quantity;

    public CartItem(String name, BigDecimal unitPrice, int quantity) {
        Name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getName() {
        return Name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
