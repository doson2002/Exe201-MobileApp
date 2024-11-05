package app.foodpt.exe201.DTO;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FoodItem  implements Serializable {
    private int id;
    private String foodName;
    private String description;
    private int quantity;
    private double price;
    private String imageUrl;
    private int isOffered;
    private SupplierInfo supplierInfo;
    private List<FoodType> foodTypes;

    // Constructor, getters, and setters
    public FoodItem(int id, String foodName, int quantity, double price, String imageUrl,int isOffered, SupplierInfo supplierInfo, List<FoodType> foodTypes) {
        this.id = id;
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isOffered = isOffered;
        this.supplierInfo = supplierInfo;
        this.foodTypes = foodTypes;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsOffered() {
        return isOffered;
    }

    public void setIsOffered(int isOffered) {
        this.isOffered = isOffered;
    }

    public FoodItem(int id, String foodName, double price, String imageUrl, String description, SupplierInfo supplierInfo) {
        this.id = id;
        this.foodName = foodName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.supplierInfo = supplierInfo;
    }
    public FoodItem(int id, String foodName, double price, String imageUrl,  String description) {
        this.id = id;
        this.foodName = foodName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getFoodName() { return foodName; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public SupplierInfo getSupplierInfo() { return supplierInfo; }
    public List<FoodType> getFoodTypes() { return foodTypes; }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSupplierInfo(SupplierInfo supplierInfo) {
        this.supplierInfo = supplierInfo;
    }

    public void setFoodTypes(List<FoodType> foodTypes) {
        this.foodTypes = foodTypes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return id == foodItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
