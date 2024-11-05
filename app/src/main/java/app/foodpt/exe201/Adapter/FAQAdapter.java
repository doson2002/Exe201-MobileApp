package app.foodpt.exe201.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.foodpt.exe201.DTO.FAQ;
import app.foodpt.exe201.FaqDetailActivity;
import app.foodpt.exe201.R;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private List<FAQ> faqList;
    private Context context;

    public FAQAdapter(List<FAQ> faqList, Context context) {
        this.faqList = faqList;
        this.context = context;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FaqDetailActivity.class);
                intent.putExtra("FAQ_ID", faq.getId());  // Pass the ID of the clicked FAQ
                context.startActivity(intent);
            }
        });
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