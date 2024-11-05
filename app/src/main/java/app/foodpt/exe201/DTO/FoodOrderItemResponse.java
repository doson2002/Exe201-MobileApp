package app.foodpt.exe201.DTO;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodOrderItemResponse implements Parcelable {
    private int id;
    private String foodName;
    private int quantity;
    private double price;
    private int foodItemId;
    private int supplierId;

    // Constructor
    public FoodOrderItemResponse(int id, String foodName, int quantity,double price,int foodItemId, int supplierId) {
        this.id = id;
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
        this.foodItemId = foodItemId;
        this.supplierId = supplierId;
    }

    public double getPrice() {
        return price;
    }

    public int getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(int foodItemId) {
        this.foodItemId = foodItemId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
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
        price = in.readDouble();
        foodItemId = in.readInt();
        supplierId =in.readInt();
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
        dest.writeDouble(price);
        dest.writeInt(foodItemId);
        dest.writeInt(supplierId);
    }
}
