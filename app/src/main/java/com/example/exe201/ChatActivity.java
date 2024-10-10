package com.example.exe201;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.MessageAdapter;
import com.example.exe201.DTO.ChatRealTime.Message;
import com.example.exe201.DTO.SupplierInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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
    private TextView textViewRestaurantName;
    private String supplierImgUrl = "";
    private String restaurantName = "";
    private int supplierIdChoseByCustomer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        View rootView = findViewById(R.id.root_view);
        // Thiết lập WindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                // Áp dụng padding để tránh bị thanh hệ thống che
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
                return insets.consumeSystemWindowInsets();
            }
        });
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

        SupplierInfo supplierChoseByCustomer = getIntent().getParcelableExtra("supplier");
        if(supplierChoseByCustomer!=null){
           int supplierChose = supplierChoseByCustomer.getId();
            supplierIdChoseByCustomer = supplierChose;
        }else if (chatId !=null) {
            String[] parts = chatId.split("_");
            // Kiểm tra số phần tử để đảm bảo tách thành công
            if (parts.length >= 4) {
                // Tách supplierIdChoseByCustomer và chuyển đổi sang int
                try {
                    supplierIdChoseByCustomer = Integer.parseInt(parts[1]); // phần tử thứ 1 là supplierIdChoseByCustomer

                } catch (NumberFormatException e) {
                    // Xử lý trường hợp không thể chuyển đổi sang int
                    System.out.println("Không thể chuyển đổi supplierIdChoseByCustomer sang kiểu int");
                }
            } else {
                // Xử lý trường hợp không tách được
                System.out.println("chatId không hợp lệ");
            }
        }
        if(chatId ==null||chatId.isEmpty()){
            chatId = "supplier_" + supplierIdChoseByCustomer + "_customer_" + userId;
        }


        textViewRestaurantName = findViewById(R.id.textViewRestaurantName);
        // Kiểm tra vai trò để gọi hàm lấy tên
        if ("ROLE_CUSTOMER".equals(role)) {
            getSupplierById(supplierIdChoseByCustomer);
        } else if ("ROLE_PARTNER".equals(role)) {
            getCustomerNameFromFirebase(supplierId,chatId);
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


//        // Tải tin nhắn cũ
//        loadOldMessages();

        // Lắng nghe tin nhắn mới
        listenForNewMessages();

        // Gửi tin nhắn
        findViewById(R.id.send_button).setOnClickListener(v -> {
            if (role.equals("ROLE_CUSTOMER")) {
                // Nếu là khách hàng, gửi tin nhắn bình thường
                sendMessage(supplierId, userId, fullName, imgUrl);
            } else if (role.equals("ROLE_PARTNER")) {
                // Nếu là đối tác, lấy userId từ chatRef
                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Lấy userId từ chatId (ví dụ: supplier_1_customer_3)
                            String[] parts = chatId.split("_");
                            if (parts.length == 4) {
                                // Lấy userId từ phần cuối của chuỗi
                                int extractedUserId = Integer.parseInt(parts[3]); // Lấy giá trị "3"
                                // Gọi hàm sendMessage với userId mới
                                sendMessage(supplierId, extractedUserId, restaurantName, supplierImgUrl);
                            }
                        } else {
                            Log.e("ChatActivity", "Chat reference does not exist.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ChatActivity", "Failed to retrieve data from chat reference", error.toException());
                    }
                });
            }
        });
    }

    private void getSupplierById(int supplierId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.GET_SUPPLIER_BY_ID + "/" + supplierId; // URL API với supplierId

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            restaurantName = response.getString("restaurant_name");
                            textViewRestaurantName.setText(restaurantName); // Thiết lập tên nhà hàng vào TextView
                            supplierImgUrl = response.getString("img_url");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error fetching data";
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            errorMessage += " - Status code: " + statusCode;
                        }
                        Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };
        // Thêm request vào hàng đợi
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getCustomerNameFromFirebase(int supplierId , String chatId) {
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("supplier_chats").child("supplier_" + supplierId).child(chatId);

        customerRef.child("customerName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String customerName = dataSnapshot.getValue(String.class);
                textViewRestaurantName.setText(customerName); // Thiết lập tên khách hàng vào TextView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to get customer name", databaseError.toException());
            }
        });
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
                        updateChatSummaryForSupplier(supplierId,customerId, fullName,imgUrl, text, timestamp);
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

    private void updateChatSummaryForSupplier(int supplierId,int customerId, String fullName, String imgUrl, String lastMessage, long timestamp) {
        String supplierID = "supplier_" + supplierId;  // ID của supplier
        String customerID = "customer_" + customerId;
        DatabaseReference supplierChatRef = FirebaseDatabase.getInstance()
                .getReference("supplier_chats")
                .child(supplierID)
                .child(chatRef.getKey());

        // Cập nhật thông tin chat
        Map<String, Object> updates = new HashMap<>();
        updates.put("senderName", fullName);
        updates.put("senderImageUrl", imgUrl);
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

        DatabaseReference customerChatRef = FirebaseDatabase.getInstance()
                .getReference("customer_chats")
                .child(customerID)
                .child(chatRef.getKey());

        // Cập nhật thông tin chat
        Map<String, Object> updateCustomer = new HashMap<>();
        updateCustomer.put("senderName", restaurantName);
        updateCustomer.put("senderImageUrl", supplierImgUrl);
        updateCustomer.put("lastMessage", "");
        updateCustomer.put("lastMessageTime", timestamp);
        updateCustomer.put("unreadCount", 0);
        customerChatRef.updateChildren(updateCustomer);
    }
    private void updateChatSummaryForCustomer(int customerId,String fullName, String imgUrl, String lastMessage, long timestamp) {
        String customerID = "customer_" + customerId;  // ID của supplier

        DatabaseReference customerChatRef = FirebaseDatabase.getInstance()
                .getReference("customer_chats")
                .child(customerID)
                .child(chatRef.getKey());

        // Cập nhật thông tin chat
        Map<String, Object> updates = new HashMap<>();
        updates.put("senderName", fullName);
        updates.put("senderImageUrl", imgUrl);
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
