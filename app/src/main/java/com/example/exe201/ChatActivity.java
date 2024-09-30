package com.example.exe201;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String chatId; // Chat ID
    private DatabaseReference chatRef; // Reference to the chat in Firebase
    private ImageView backArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
        // Lấy chatId từ intent
        chatId = "test1";

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        recyclerView.setAdapter(messageAdapter);

        // Khởi tạo Firebase Database
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

        // Lắng nghe sự thay đổi trong chat
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

        // Gửi tin nhắn
        findViewById(R.id.send_button).setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String sender = "customer_1"; // Hoặc "supplier_1" tùy thuộc vào vai trò
        String text = ((EditText) findViewById(R.id.message_input)).getText().toString();
        long timestamp = System.currentTimeMillis();

        if (!text.isEmpty()) {
            Message message = new Message(sender, text, timestamp);

            // Thêm log để kiểm tra tin nhắn trước khi gửi
            Log.d("ChatActivity", "Sending message: " + message.getText() + " from " + message.getSender());

            chatRef.child("messages").push().setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("ChatActivity", "Message sent successfully");
                } else {
                    Log.e("ChatActivity", "Failed to send message", task.getException());
                }
            });

            ((EditText) findViewById(R.id.message_input)).setText(""); // Xóa input
        } else {
            Log.w("ChatActivity", "Message text is empty, not sending.");
        }
    }
}