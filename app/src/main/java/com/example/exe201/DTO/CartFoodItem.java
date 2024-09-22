package com.example.exe201.DTO;


import java.io.Serializable;
import java.util.Objects;

public class CartFoodItem implements Serializable {
    private Menu foodItem;
    private int quantity;

    // Constructor
    public CartFoodItem(Menu foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    // Getters
    public Menu getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    // Triển khai equals() và hashCode() dựa trên các thuộc tính bạn muốn so sánh
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartFoodItem that = (CartFoodItem) o;
        return quantity == that.quantity && foodItem.equals(that.foodItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodItem, quantity);
    }
}
