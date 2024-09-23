package com.example.exe201.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SupplierInfo implements Parcelable {
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

    protected SupplierInfo(Parcel in) {
        id = in.readInt();
        restaurantName = in.readString();
        imgUrl = in.readString();
        totalStarRating = in.readDouble();
        totalReviewCount = in.readInt();
    }

    public static final Creator<SupplierInfo> CREATOR = new Creator<SupplierInfo>() {
        @Override
        public SupplierInfo createFromParcel(Parcel in) {
            return new SupplierInfo(in);
        }

        @Override
        public SupplierInfo[] newArray(int size) {
            return new SupplierInfo[size];
        }
    };

    public int getId() { return id; }
    public String getRestaurantName() { return restaurantName; }
    public String getImgUrl() { return imgUrl; }
    public double getTotalStarRating() { return totalStarRating; }
    public int getTotalReviewCount() { return totalReviewCount; }
    public SupplierType getSupplierType() { return supplierType; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(restaurantName);
        parcel.writeString(imgUrl);
        parcel.writeDouble(totalStarRating);
        parcel.writeInt(totalReviewCount);
    }
}