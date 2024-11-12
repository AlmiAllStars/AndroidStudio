package com.almi.juegaalmiapp;

public class CartItem {
    private String name;
    private double price;
    private int quantity;
    private int imageResId;

    public CartItem(String name, double price, int quantity, int imageResId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageResId() {
        return imageResId;
    }
}
