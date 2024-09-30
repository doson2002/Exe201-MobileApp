package com.example.exe201;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe201.Adapter.MessageAdapter;
import com.example.exe201.DTO.ChatRealTime.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private DatabaseReference chatRef; // Reference to the chat in Firebase
    private ImageView backArrow;
    private String role; // Role: customer or supplier
    private String chatId; // Chat ID
    private String receiverImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        // Lấy SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String fullName = sharedPreferences.getString("full_name", "");
        String imgUrl = sharedPreferences.getString("img_url", "");
        int supplierId = sharedPreferences.getInt("supplier_id", -1);
        role = sharedPreferences.getString("role", "ROLE_CUSTOMER"); // Mặc định là customer
        chatId = getIntent().getStringExtra("chatId");
        if(chatId ==null||chatId.isEmpty()){
            chatId = "supplier_" + supplierId + "_customer_" + userId;
        }
        // Khởi tạo Firebase Database với chatId
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

        chatRef.child("receiverImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                receiverImageUrl = dataSnapshot.getValue(String.class);
                // Lưu receiverImageUrl vào biến instance để sử dụng sau này
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to get receiverImageUrl", databaseError.toException());
            }
        });
        // Set up RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList, role.equals("ROLE_PARTNER") ? "supplier_" + supplierId : "customer_" + userId,receiverImageUrl);
        recyclerView.setAdapter(messageAdapter);


        // Tải tin nhắn cũ
        loadOldMessages();

        // Lắng nghe tin nhắn mới
        listenForNewMessages();

        // Gửi tin nhắn
        findViewById(R.id.send_button).setOnClickListener(v -> sendMessage(supplierId, userId, fullName, imgUrl));
    }

    private void loadOldMessages() {
        // Tải tin nhắn cũ
        chatRef.child("messages").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();  // Xóa danh sách cũ trước khi thêm tin nhắn
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();  // Cập nhật adapter
                recyclerView.scrollToPosition(messageList.size() - 1);  // Cuộn xuống cuối
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to load old messages", databaseError.toException());
            }
        });
    }

    private void listenForNewMessages() {
        // Lắng nghe tin nhắn mới
        chatRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1); // Cuộn xuống cuối
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void sendMessage(int supplierId, int customerId, String fullName, String imgUrl) {
        String text = ((EditText) findViewById(R.id.message_input)).getText().toString();
        long timestamp = System.currentTimeMillis();

        if (!text.isEmpty()) {
            // Tạo tin nhắn dựa trên role
            Message message = role.equals("ROLE_PARTNER") ?
                    new Message("supplier_" + supplierId, text, timestamp) :
                    new Message("customer_" + customerId, text, timestamp);

            // Gửi tin nhắn vào phần "messages"
            chatRef.child("messages").push().setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("ChatActivity", "Message sent successfully");

                    // Cập nhật thông tin chat cho supplier nếu là customer gửi
                    if (role.equals("ROLE_CUSTOMER")) {
                        updateChatSummaryForSupplier(supplierId,fullName,imgUrl, text, timestamp);
                    }else if(role.equals("ROLE_PARTNER")){
                        updateChatSummaryForCustomer(customerId,fullName, imgUrl, text, timestamp);
                    }
                    // Cập nhật receiverImageUrl ở cấp độ chat
                    chatRef.child("receiverImageUrl").setValue(imgUrl);

                } else {
                    Log.e("ChatActivity", "Failed to send message", task.getException());
                }
            });

            ((EditText) findViewById(R.id.message_input)).setText(""); // Xóa input
        } else {
            Log.w("ChatActivity", "Message text is empty, not sending.");
        }
    }

    private void updateChatSummaryForSupplier(int supplierId,String fullName, String imgUrl, String lastMessage, long timestamp) {
        String supplierID = "supplier_" + supplierId;  // ID của supplier

        DatabaseReference supplierChatRef = FirebaseDatabase.getInstance()
                .getReference("supplier_chats")
                .child(supplierID)
                .child(chatRef.getKey());

        // Cập nhật thông tin chat
        Map<String, Object> updates = new HashMap<>();
        updates.put("customerName", fullName);
        updates.put("customerImageUrl", imgUrl);
        updates.put("lastMessage", lastMessage);
        updates.put("lastMessageTime", timestamp);
        supplierChatRef.updateChildren(updates);

        // Tăng số lượng tin nhắn chưa đọc
        supplierChatRef.child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unreadCount = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
                supplierChatRef.child("unreadCount").setValue(unreadCount + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Failed to update unread count", error.toException());
            }
        });
    }
    private void updateChatSummaryForCustomer(int customerId,String fullName, String imgUrl, String lastMessage, long timestamp) {
        String customerID = "customer_" + customerId;  // ID của supplier

        DatabaseReference customerChatRef = FirebaseDatabase.getInstance()
                .getReference("customer_chats")
                .child(customerID)
                .child(chatRef.getKey());

        // Cập nhật thông tin chat
        Map<String, Object> updates = new HashMap<>();
        updates.put("supplierName", fullName);
        updates.put("supplierImageUrl", imgUrl);
        updates.put("lastMessage", lastMessage);
        updates.put("lastMessageTime", timestamp);
        customerChatRef.updateChildren(updates);

        // Tăng số lượng tin nhắn chưa đọc
        customerChatRef.child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unreadCount = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
                customerChatRef.child("unreadCount").setValue(unreadCount + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Failed to update unread count", error.toException());
            }
        });
    }
}
