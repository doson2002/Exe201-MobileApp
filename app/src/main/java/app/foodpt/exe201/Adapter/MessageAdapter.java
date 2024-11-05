package app.foodpt.exe201.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import app.foodpt.exe201.DTO.ChatRealTime.Message;
import app.foodpt.exe201.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<Message> messageList;
    private String currentUserId;  // ID của người hiện tại (người gửi)
    private String receiverImageUrl;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId, String receiverImageUrl) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;  // Nhận từ `ChatActivity`
        this.receiverImageUrl = receiverImageUrl;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        // Nếu người gửi tin nhắn là người dùng hiện tại, trả về VIEW_TYPE_SENT
        if (message.getSender().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        Log.d("MessageAdapter", "Binding message at position: " + position + ", viewType: " + getItemViewType(position));

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);



        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);

//            // Kiểm tra nếu đây là tin nhắn nhận đầu tiên hoặc tin nhắn sau khi người nhận đã trả lời
//            if (position == 0 ||
//                    (message.getSender().equals(currentUserId) && !messageList.get(position - 1).getSender().equals(currentUserId)) ||
//                    (message.getSender().equals(currentUserId) && messageList.get(position - 1).getSender().equals(currentUserId))) {

//                // Hiển thị hình ảnh nếu là tin nhắn đầu tiên hoặc là tin nhắn thứ 6
//                ((ReceivedMessageViewHolder) holder).receiverImage.setVisibility(View.VISIBLE);
//                // Tải ảnh người nhận (bạn cần thêm logic để tải hình ảnh ở đây)
//                Glide.with(context).load(receiverImageUrl).into(((ReceivedMessageViewHolder) holder).receiverImage);
//            } else {
                // Ẩn hình ảnh nếu không phải là tin nhắn đầu tiên
                ((ReceivedMessageViewHolder) holder).receiverImage.setVisibility(View.GONE);
//            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder cho tin nhắn gửi
    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            timeText.setText(DateFormat.format("HH:mm", message.getTimestamp()));
        }
    }

    // ViewHolder cho tin nhắn nhận
    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView receiverImage; // Thêm ImageView

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            receiverImage = itemView.findViewById(R.id.receiver_image);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            timeText.setText(DateFormat.format("HH:mm", message.getTimestamp()));
        }
    }
}
