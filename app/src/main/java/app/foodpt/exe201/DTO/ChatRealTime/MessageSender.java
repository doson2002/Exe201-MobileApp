package app.foodpt.exe201.DTO.ChatRealTime;

public class MessageSender {
    private String chatId;
    private String senderName;
    private String lastMessage;
    private long lastMessageTime;
    private int unreadCount;
    private String senderImageUrl;

    public MessageSender() {
    }

    // Constructor, Getters, and Setters
    public MessageSender(String chatId, String senderName, String lastMessage, long lastMessageTime, int unreadCount, String senderImageUrl) {
        this.chatId = chatId;
        this.senderName = senderName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.senderImageUrl = senderImageUrl;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        this.senderImageUrl = senderImageUrl;
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



    public String getChatId() {
        return chatId;
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


}
