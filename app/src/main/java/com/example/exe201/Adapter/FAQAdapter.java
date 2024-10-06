package com.example.exe201.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe201.DTO.FAQ;
import com.example.exe201.R;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private List<FAQ> faqList;

    public FAQAdapter(List<FAQ> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_item, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        FAQ faq = faqList.get(position);
        holder.titleTextView.setText(faq.getTitle());
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }
}