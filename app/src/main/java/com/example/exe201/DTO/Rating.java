package com.example.exe201.DTO;

public class Rating {
    private int id;
    private String fullName;
    private int ratingStar;
    private String responseMessage;
    private String avatarUserUrl;

    public Rating(int id, String fullName, int ratingStar, String responseMessage, String avatarUserUrl) {
        this.id = id;
        this.fullName = fullName;
        this.ratingStar = ratingStar;
        this.responseMessage = responseMessage;
        this.avatarUserUrl = avatarUserUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRatingStar(int ratingStar) {
        this.ratingStar = ratingStar;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getAvatarUserUrl() {
        return avatarUserUrl;
    }

    public void setAvatarUserUrl(String avatarUserUrl) {
        this.avatarUserUrl = avatarUserUrl;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public int getRatingStar() {
        return ratingStar;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}