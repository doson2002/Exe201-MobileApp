package com.example.exe201.DTO;

public class FoodItemResponseWithSupplier {
    private int id;
    private String foodName;
    private double price;
    private String imageUrl;
    private int quantity;
    private int supplierId;


    public FoodItemResponseWithSupplier(int id, String foodName, double price, String imageUrl, int quantity, int supplierId) {
        this.id = id;
        this.foodName = foodName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.supplierId = supplierId;

    }

    public FoodItemResponseWithSupplier() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
