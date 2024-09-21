package com.example.exe201.DTO;

public class TinhThanh {
    private String id;
    private String fullName;

    public TinhThanh(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }



    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return fullName;  // Hiển thị tên tỉnh trong Spinner
    }
}
