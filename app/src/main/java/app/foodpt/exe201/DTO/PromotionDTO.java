package app.foodpt.exe201.DTO;

public class PromotionDTO {
    private Long supplierId;
    private double discountPercentage;
    private double fixedDiscountAmount;
    private String description;
    private boolean status;

    // Getters và Setters cho các trường
    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getFixedDiscountAmount() {
        return fixedDiscountAmount;
    }

    public void setFixedDiscountAmount(double fixedDiscountAmount) {
        this.fixedDiscountAmount = fixedDiscountAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
