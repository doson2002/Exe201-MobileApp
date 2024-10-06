package com.example.exe201.DTO;

public class Banner {
    private int id;
    private String imageUrl;
    private int i;

    public Banner(int id, String imageUrl, int i) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.i = i;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getI() {
        return i;
    }
}