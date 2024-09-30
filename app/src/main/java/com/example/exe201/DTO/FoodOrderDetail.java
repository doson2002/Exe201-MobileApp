package com.example.exe201.DTO;


import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class FoodOrderDetail implements Parcelable {
    private int id;
    private List<FoodOrderItemResponse> foodOrderItemResponseList;
    private long pickupTime;
    private String pickupLocation;
    private String status;
    private String paymentMethod;
    private int paymentStatus;
    private SupplierInfo supplierInfo;

    // Constructor
    public FoodOrderDetail(int id, List<FoodOrderItemResponse> foodOrderItemResponseList, long pickupTime,
                           String pickupLocation, String status, String paymentMethod, int paymentStatus) {
        this.id = id;
        this.foodOrderItemResponseList = foodOrderItemResponseList;
        this.pickupTime = pickupTime;
        this.pickupLocation = pickupLocation;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<FoodOrderItemResponse> getFoodOrderItemResponseList() {
        return foodOrderItemResponseList;
    }

    public void setFoodOrderItemResponseList(List<FoodOrderItemResponse> foodOrderItemResponseList) {
        this.foodOrderItemResponseList = foodOrderItemResponseList;
    }

    public long getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(long pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // Parcelable implementation
    protected FoodOrderDetail(Parcel in) {
        id = in.readInt();
        foodOrderItemResponseList = in.createTypedArrayList(FoodOrderItemResponse.CREATOR);
        pickupTime = in.readLong();
        pickupLocation = in.readString();
        status = in.readString();
        paymentMethod = in.readString();
        paymentStatus = in.readInt();
    }

    public static final Creator<FoodOrderDetail> CREATOR = new Creator<FoodOrderDetail>() {
        @Override
        public FoodOrderDetail createFromParcel(Parcel in) {
            return new FoodOrderDetail(in);
        }

        @Override
        public FoodOrderDetail[] newArray(int size) {
            return new FoodOrderDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeTypedList(foodOrderItemResponseList);
        dest.writeLong(pickupTime);
        dest.writeString(pickupLocation);
        dest.writeString(status);
        dest.writeString(paymentMethod);
        dest.writeInt(paymentStatus);
    }
}
