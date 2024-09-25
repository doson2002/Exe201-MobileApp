package com.example.exe201.DTO;

public class FoodItemResponseWithSupplier {
    private Long id;
    private String foodName;
    private double price;
    private String imageUrl;


    public FoodItemResponseWithSupplier(Long id, String foodName, double price, String imageUrl) {
        this.id = id;
        this.foodName = foodName;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
