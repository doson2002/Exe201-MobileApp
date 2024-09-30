package com.example.exe201.DTO.ChatRealTime;

public class Chat {
    private int customerId;
    private int supplierId;

    public Chat() { }

    public Chat(int customerId, int supplierId) {
        this.customerId = customerId;
        this.supplierId = supplierId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getSupplierId() {
        return supplierId;
    }
}
