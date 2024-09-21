package com.example.exe201.DTO;

import java.util.Date;

public class Promotion {
    private Long id;
    private String code;
    private double discountPercentage;
    private Long fixedDiscountAmount;
    private Date startDate;
    private Date endDate;

    // Constructor mặc định
    public Promotion() {
    }

    // Constructor với tất cả các thuộc tính
    public Promotion(Long id, String code, double discountPercentage, Long fixedDiscountAmount, Date startDate, Date endDate) {
        this.id = id;
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.fixedDiscountAmount = fixedDiscountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter và Setter cho từng thuộc tính
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Long getFixedDiscountAmount() {
        return fixedDiscountAmount;
    }

    public void setFixedDiscountAmount(Long fixedDiscountAmount) {
        this.fixedDiscountAmount = fixedDiscountAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // toString method để hiển thị thông tin đối tượng
    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", fixedDiscountAmount=" + fixedDiscountAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
