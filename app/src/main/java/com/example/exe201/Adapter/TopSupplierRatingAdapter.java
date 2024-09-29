package com.example.exe201.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.R;
import com.example.exe201.ShowFoodItemActivity;

import java.util.List;

public class TopSupplierRatingAdapter extends RecyclerView.Adapter<TopSupplierRatingAdapter.SupplierViewHolder> {
    private List<SupplierInfo> supplierList;
    private Context context;

    public TopSupplierRatingAdapter(List<SupplierInfo> supplierList, Context context) {
        this.supplierList = supplierList;
        this.context = context;
    }

    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_supplier, parent, false);
        return new SupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierViewHolder holder, int position) {
        SupplierInfo supplier = supplierList.get(position);

        // Thiết lập dữ liệu cho các view
        Glide.with(holder.imageView.getContext())
                .load(supplier.getImgUrl())
                .placeholder(R.drawable.loading)  // Placeholder
                .into(holder.imageView);

        holder.nameTextView.setText(supplier.getRestaurantName());
        holder.reviewCountTextView.setText("Số lượng đánh giá: " + supplier.getTotalReviewCount());
        float numberStars = (float)supplier.getTotalStarRating();
        holder.ratingBar.setRating(numberStars);

        // Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowFoodItemActivity.class);
            intent.putExtra("supplier", supplier); // Gửi supplierInfo qua Intent
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public static class SupplierViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView reviewCountTextView;
        RatingBar ratingBar;

        public SupplierViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewSupplier);
            nameTextView = itemView.findViewById(R.id.textViewSupplierName);
            reviewCountTextView = itemView.findViewById(R.id.textViewReviewCount);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}