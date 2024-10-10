package com.example.exe201.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FoodOrder implements Parcelable {
    private int id;
    private String foodImage; // Ảnh đại diện của đơn hàng
    private String restaurantName; // Tên nhà hàng
    private double totalPrice; // Tổng giá trị đơn hàng
    private int totalItems; // Tổng số món
    private String orderStatus; // Trạng thái đơn hàng
    private String orderTime; // Thời gian đặt hàng
    private double shippingFee;

    public FoodOrder(int id, String foodImage, String restaurantName, double totalPrice, int totalItems, String orderStatus, String orderTime) {
        this.id = id;
        this.foodImage = foodImage;
        this.restaurantName = restaurantName;
        this.totalPrice = totalPrice;
        this.totalItems = totalItems;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public int getId() {
        return id;
    }

    protected FoodOrder(Parcel in) {
        id = in.readInt();
        foodImage = in.readString();
        restaurantName = in.readString();
        totalPrice = in.readDouble();
        totalItems = in.readInt();
        orderStatus = in.readString();
        orderTime = in.readString();
        shippingFee = in.readDouble();
    }

    public static final Creator<FoodOrder> CREATOR = new Creator<FoodOrder>() {
        @Override
        public FoodOrder createFromParcel(Parcel in) {
            return new FoodOrder(in);
        }

        @Override
        public FoodOrder[] newArray(int size) {
            return new FoodOrder[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(foodImage);
        parcel.writeString(restaurantName);
        parcel.writeDouble(totalPrice);
        parcel.writeInt(totalItems);
        parcel.writeString(orderStatus);
        parcel.writeString(orderTime);
        parcel.writeDouble(shippingFee);
    }
}
