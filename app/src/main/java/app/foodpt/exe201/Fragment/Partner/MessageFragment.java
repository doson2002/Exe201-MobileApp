package app.foodpt.exe201.Fragment.Partner;

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

import app.foodpt.exe201.Adapter.ManageMessageSenderAdapter;
import app.foodpt.exe201.DTO.ChatRealTime.MessageSender;
import app.foodpt.exe201.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ManageMessageSenderAdapter messageAdapter;
    private List<MessageSender> messageList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        SharedPreferences sharedPreferences =  requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);
        String role = sharedPreferences.getString("role","");

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        messageAdapter = new ManageMessageSenderAdapter(messageList,  requireActivity(), role);
        recyclerView.setAdapter(messageAdapter);
        // Thay "supplier_3" bằng supplierId phù hợp
        String supplier = "supplier_" +supplierId;
        databaseReference = FirebaseDatabase.getInstance().getReference("supplier_chats").child(supplier);
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