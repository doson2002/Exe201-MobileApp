package com.example.exe201.DTO;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodOrderItemResponse implements Parcelable {
    private int id;
    private String foodName;
    private int quantity;

    // Constructor
    public FoodOrderItemResponse(int id, String foodName, int quantity) {
        this.id = id;
        this.foodName = foodName;
        this.quantity = quantity;
    }

    // Getters and Setters
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Parcelable implementation
    protected FoodOrderItemResponse(Parcel in) {
        id = in.readInt();
        foodName = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<FoodOrderItemResponse> CREATOR = new Creator<FoodOrderItemResponse>() {
        @Override
        public FoodOrderItemResponse createFromParcel(Parcel in) {
            return new FoodOrderItemResponse(in);
        }

        @Override
        public FoodOrderItemResponse[] newArray(int size) {
            return new FoodOrderItemResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(foodName);
        dest.writeInt(quantity);
    }
}
