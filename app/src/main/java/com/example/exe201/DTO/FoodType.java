package com.example.exe201.DTO;

public class FoodType {
    private int id;
    private String typeName;
    private int order;
    private SupplierInfo supplierInfo;

    public FoodType(int id, String typeName, int order, SupplierInfo supplierInfo) {
        this.id = id;
        this.typeName = typeName;
        this.order = order;
        this.supplierInfo = supplierInfo;
    }

    public FoodType(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;

    }
    public FoodType() {

    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() { return id; }

    public void setTypeName(String typeName){
        this.typeName = typeName;
    }
    public String getTypeName() { return typeName; }

    public void setOrder(int order){
        this.order = order;
    }
    public int getOrder() { return order; }

    public void setSupplierInfo(SupplierInfo supplierInfo){
        this.supplierInfo = supplierInfo;
    }
    public SupplierInfo getSupplierInfo() { return supplierInfo; }


}