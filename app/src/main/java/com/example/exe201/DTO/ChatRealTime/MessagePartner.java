package com.example.exe201.DTO.ChatRealTime;

public class MessagePartner {
    private String chatId;
    private String customerName;
    private String lastMessage;
    private long lastMessageTime;
    private int unreadCount;
    private String customerImageUrl;

    public MessagePartner() {
    }

    // Constructor, Getters, and Setters
    public MessagePartner( String chatId,String customerName, String lastMessage, long lastMessageTime, int unreadCount, String customerImageUrl) {
        this.chatId = chatId;
        this.customerName = customerName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.customerImageUrl = customerImageUrl;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setCustomerImageUrl(String customerImageUrl) {
        this.customerImageUrl = customerImageUrl;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getChatId() {
        return chatId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getCustomerImageUrl() {
        return customerImageUrl;
    }
}
