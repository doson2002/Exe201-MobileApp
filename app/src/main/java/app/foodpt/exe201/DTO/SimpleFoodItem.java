package app.foodpt.exe201.DTO;

import java.util.Objects;

public class SimpleFoodItem {
    private int id;
    private String foodName;
    private double price;
    private String imageUrl;
    private String description;

    // Constructor
    public SimpleFoodItem(int id, String foodName, double price, String imageUrl, String description) {
        this.id = id;
        this.foodName = foodName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // Getters và Setters
    public int getId() { return id; }
    public String getFoodName() { return foodName; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleFoodItem that = (SimpleFoodItem) o;
        return id == that.id &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(foodName, that.foodName) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, foodName, price, imageUrl, description);
    }
}
