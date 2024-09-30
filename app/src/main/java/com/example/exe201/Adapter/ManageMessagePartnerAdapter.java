package com.example.exe201.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exe201.ChatActivity;
import com.example.exe201.DTO.ChatRealTime.MessagePartner;
import com.example.exe201.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ManageMessagePartnerAdapter extends RecyclerView.Adapter<ManageMessagePartnerAdapter.MessageViewHolder> {

    private List<MessagePartner> messageList;
    private Context context;

    public ManageMessagePartnerAdapter(List<MessagePartner> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessagePartner message = messageList.get(position);

        holder.customerName.setText(message.getCustomerName());
        holder.lastMessage.setText(message.getLastMessage());
        holder.messageTime.setText(formatTime(message.getLastMessageTime()));

        if (message.getUnreadCount() > 0) {
            holder.unreadCount.setVisibility(View.VISIBLE);
            holder.unreadCount.setText(String.valueOf(message.getUnreadCount()));
        } else {
            holder.unreadCount.setVisibility(View.GONE);
        }

        // Load image using Picasso or Glide
        Glide.with(context)
                .load(message.getCustomerImageUrl())
                .into(holder.customerImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            // Lấy chatId của cuộc trò chuyện
            String chatId = message.getChatId();
            // Reset unreadCount về 0 khi nhấn vào chat của customer
            resetUnreadCount(chatId);
            intent.putExtra("chatId", chatId); // Gửi chatId đến ChatActivity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView customerImage;
        TextView customerName, lastMessage, messageTime, unreadCount;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            customerImage = itemView.findViewById(R.id.imageCustomer);
            customerName = itemView.findViewById(R.id.textCustomerName);
            lastMessage = itemView.findViewById(R.id.textLastMessage);
            messageTime = itemView.findViewById(R.id.textMessageTime);
            unreadCount = itemView.findViewById(R.id.textUnreadCount);
        }
    }

    private String formatTime(long time) {
        // Format thời gian (ví dụ: 4 giờ trước)
        // Bạn có thể sử dụng SimpleDateFormat hoặc các thư viện tiện ích khác
        return DateUtils.getRelativeTimeSpanString(time).toString();
    }
    // Hàm reset unreadCount về 0 trong Firebase
    private void resetUnreadCount(String chatId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("supplier_chats")
                .child("supplier_" + supplierId) // SUPPLIER_ID là supplier hiện tại
                .child(chatId);

        chatRef.child("unreadCount").setValue(0).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ManageMessagePartnerAdapter", "Unread count reset successfully");
            } else {
                Log.e("ManageMessagePartnerAdapter", "Failed to reset unread count", task.getException());
            }
        });
    }
}