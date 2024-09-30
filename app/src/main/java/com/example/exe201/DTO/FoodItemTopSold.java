package com.example.exe201.DTO;

public class FoodItemTopSold {
    private int foodItemId;
    private String name;
    private int quantitySold;
    private SupplierInfo supplierInfo;

    public FoodItemTopSold(int foodItemId,String name, int quantitySold, SupplierInfo supplierInfo) {
        this.foodItemId = foodItemId;
        this.name = name;
        this.quantitySold = quantitySold;
        this.supplierInfo = supplierInfo;
    }

    public FoodItemTopSold() {
    }

    public String getName() {
        return name;
    }

    public int getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(int foodItemId) {
        this.foodItemId = foodItemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public SupplierInfo getSupplierInfo() {
        return supplierInfo;
    }

    public void setSupplierInfo(SupplierInfo supplierInfo) {
        this.supplierInfo = supplierInfo;
    }
}
