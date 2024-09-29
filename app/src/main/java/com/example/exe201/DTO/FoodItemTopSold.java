package com.example.exe201.DTO;

public class FoodItemTopSold {
    private String name;
    private int quantitySold;
    private SupplierInfo supplierInfo;

    public FoodItemTopSold(String name, int quantitySold, SupplierInfo supplierInfo) {
        this.name = name;
        this.quantitySold = quantitySold;
        this.supplierInfo = supplierInfo;
    }

    public FoodItemTopSold() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public SupplierInfo getSupplierInfo() {
        return supplierInfo;
    }

    public void setSupplierInfo(SupplierInfo supplierInfo) {
        this.supplierInfo = supplierInfo;
    }
}
