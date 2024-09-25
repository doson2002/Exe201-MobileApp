package com.example.exe201.DTO;

import java.util.List;

public class SupplierWithFoodItems {
    private SupplierInfo supplierInfo;
    private List<FoodItemResponseWithSupplier> foodItems;

    // Constructor
    public SupplierWithFoodItems(SupplierInfo supplierInfo, List<FoodItemResponseWithSupplier> foodItems) {
        this.supplierInfo = supplierInfo;
        this.foodItems = foodItems;
    }

    // Getters and Setters
    public SupplierInfo getSupplierInfo() {
        return supplierInfo;
    }

    public void setSupplierInfo(SupplierInfo supplierInfo) {
        this.supplierInfo = supplierInfo;
    }

    public List<FoodItemResponseWithSupplier> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItemResponseWithSupplier> foodItems) {
        this.foodItems = foodItems;
    }
}
