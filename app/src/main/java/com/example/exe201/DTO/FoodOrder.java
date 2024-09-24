package com.example.exe201.DTO;

public class FoodOrder {
    private String foodImage; // Ảnh đại diện của đơn hàng
    private String restaurantName; // Tên nhà hàng
    private double totalPrice; // Tổng giá trị đơn hàng
    private int totalItems; // Tổng số món
    private String orderStatus; // Trạng thái đơn hàng
    private String orderTime; // Thời gian đặt hàng

    public FoodOrder(String foodImage, String restaurantName, double totalPrice, int totalItems, String orderStatus, String orderTime) {
        this.foodImage = foodImage;
        this.restaurantName = restaurantName;
        this.totalPrice = totalPrice;
        this.totalItems = totalItems;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }
}
