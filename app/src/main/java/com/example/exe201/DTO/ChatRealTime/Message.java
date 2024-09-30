package com.example.exe201.DTO.ChatRealTime;

public class Message {
    private String sender;
    private String text;
    private long timestamp;

    public Message() { }

    public Message(String sender, String text, long timestamp) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
