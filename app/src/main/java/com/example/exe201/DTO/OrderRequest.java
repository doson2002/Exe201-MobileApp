package com.example.exe201.DTO;

public class OrderRequest {
    private int quantity;
    private int foodItemId;

    public OrderRequest(int quantity, int foodItemId) {
        this.quantity = quantity;
        this.foodItemId = foodItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getFoodItemId() {
        return foodItemId;
    }
}