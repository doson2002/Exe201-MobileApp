package com.example.exe201.DTO;

public class FAQ {
    private int id;
    private String title;
    private String article;

    public FAQ(int id, String title, String article) {
        this.id = id;
        this.title = title;
        this.article = article;
    }

    public FAQ() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getArticle() {
        return article;
    }

    @Override
    public String toString() {
        return title; // Dùng cho ArrayAdapter
    }
}
