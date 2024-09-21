package com.example.exe201.DTO;

public class SupplierInfo {
    private int id;
    private String restaurantName;
    private String imgUrl;
    private double totalStarRating;
    private int totalReviewCount;
    private SupplierType supplierType;


    public SupplierInfo(int id, String restaurantName, String imgUrl
            , double totalStarRating, int totalReviewCount, SupplierType supplierType) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.imgUrl = imgUrl;
        this.totalStarRating = totalStarRating;
        this.totalReviewCount = totalReviewCount;
        this.supplierType = supplierType;

    }
    public SupplierInfo(int id, String restaurantName, String imgUrl) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.imgUrl = imgUrl;

    }

    public int getId() { return id; }
    public String getRestaurantName() { return restaurantName; }
    public String getImgUrl() { return imgUrl; }
    public double getTotalStarRating() { return totalStarRating; }
    public int getTotalReviewCount() { return totalReviewCount; }
    public SupplierType getSupplierType() { return supplierType; }

}