package com.example.exe201.Fragment.Customer;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe201.Adapter.ManageMessageSenderAdapter;
import com.example.exe201.DTO.ChatRealTime.MessageSender;
import com.example.exe201.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private ManageMessageSenderAdapter messageAdapter;
    private List<MessageSender> messageList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list_for_customer, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int customerId = sharedPreferences.getInt("user_id", -1);
        String role = sharedPreferences.getString("role","");

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        messageAdapter = new ManageMessageSenderAdapter(messageList, requireActivity(), role);
        recyclerView.setAdapter(messageAdapter);
        // Thay "supplier_3" bằng supplierId phù hợp
        String customer = "customer_" + customerId;
        databaseReference = FirebaseDatabase.getInstance().getReference("customer_chats").child(customer);
        if (databaseReference != null) {
            loadMessages();
        } else {
            Log.e("FirebaseError", "Database reference is null");
        }

        return view;
    }

    private void loadMessages() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageSender message = dataSnapshot.getValue(MessageSender.class);
                    // Lấy chatId từ Firebase key (ví dụ supplier_3_customer_2)
                    String chatId = dataSnapshot.getKey();
                    message.setChatId(chatId);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}