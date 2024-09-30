package com.example.exe201.Fragment.Partner;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe201.Adapter.ManageMessagePartnerAdapter;
import com.example.exe201.DTO.ChatRealTime.MessagePartner;
import com.example.exe201.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ManageMessagePartnerAdapter messageAdapter;
    private List<MessagePartner> messageList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        messageAdapter = new ManageMessagePartnerAdapter(messageList, getContext());
        recyclerView.setAdapter(messageAdapter);
        // Thay "supplier_3" bằng supplierId phù hợp
        String supplier = "supplier_" +supplierId;
        databaseReference = FirebaseDatabase.getInstance().getReference("supplier_chats").child(supplier);
        loadMessages();

        return view;
    }

    private void loadMessages() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessagePartner message = dataSnapshot.getValue(MessagePartner.class);
                    // Lấy chatId từ Firebase key (ví dụ supplier_3_customer_2)
                    String chatId = dataSnapshot.getKey();
                    assert message != null;
                    message.setChatId(chatId);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}