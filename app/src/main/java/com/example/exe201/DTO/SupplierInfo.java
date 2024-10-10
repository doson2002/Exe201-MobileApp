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
    private double latitude;
    private double longitude;
    private double distance;


    public SupplierInfo(int id, String restaurantName, String imgUrl
            , double totalStarRating, int totalReviewCount, SupplierType supplierType) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.imgUrl = imgUrl;
        this.totalStarRating = totalStarRating;
        this.totalReviewCount = totalReviewCount;
        this.supplierType = supplierType;

    }

    public SupplierInfo() {
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setTotalStarRating(double totalStarRating) {
        this.totalStarRating = totalStarRating;
    }

    public void setTotalReviewCount(int totalReviewCount) {
        this.totalReviewCount = totalReviewCount;
    }

    public void setSupplierType(SupplierType supplierType) {
        this.supplierType = supplierType;
    }

    public SupplierInfo(int id, String restaurantName, String imgUrl) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.imgUrl = imgUrl;

    }
    public SupplierInfo(int id, String restaurantName, String imgUrl
            , double totalStarRating, int totalReviewCount) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.imgUrl = imgUrl;
        this.totalStarRating = totalStarRating;
        this.totalReviewCount = totalReviewCount;

    }

    protected SupplierInfo(Parcel in) {
        id = in.readInt();
        restaurantName = in.readString();
        imgUrl = in.readString();
        totalStarRating = in.readDouble();
        totalReviewCount = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        distance = in.readDouble();
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
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeDouble(distance);
    }
}