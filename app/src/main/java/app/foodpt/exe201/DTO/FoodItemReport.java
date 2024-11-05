package app.foodpt.exe201.DTO;

public class FoodItemReport {
    private int id;
    private String imageUrl;
    private String name;
    private double quantitySold;

    public FoodItemReport(int id, String imageUrl, double quantitySold, String name) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.quantitySold = quantitySold;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(double quantitySold) {
        this.quantitySold = quantitySold;
    }
}
