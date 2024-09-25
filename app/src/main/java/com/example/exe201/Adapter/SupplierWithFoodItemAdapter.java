package com.example.exe201.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.DTO.SupplierWithFoodItems;
import com.example.exe201.R;

import java.util.List;

public class SupplierWithFoodItemAdapter extends RecyclerView.Adapter<SupplierWithFoodItemAdapter.MyViewHolder> {

    private List<SupplierWithFoodItems> supplierList;
    private Context context;

    public SupplierWithFoodItemAdapter(List<SupplierWithFoodItems> supplierList,Context context) {
        this.supplierList = supplierList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SupplierWithFoodItems supplierWithFoodItems = supplierList.get(position);
        SupplierInfo supplierInfo = supplierWithFoodItems.getSupplierInfo();

        // Thiết lập dữ liệu cho các TextView và ImageView
        holder.textViewRestaurantName.setText(supplierInfo.getRestaurantName());
        holder.textViewTotalStar.setText(String.format("%.1f", supplierInfo.getTotalStarRating()));
        holder.textViewTotalStar.setText(String.valueOf(supplierInfo.getTotalStarRating()));
        holder.textViewCountReview.setText("("+ String.valueOf(supplierInfo.getTotalReviewCount()) + ")");
        holder.textViewPriceDelivery.setText("From " +supplierInfo.getTotalReviewCount() + " mins");
        holder.textViewDeliveryTime.setText(String.valueOf(supplierInfo.getTotalReviewCount()));
        holder.textViewDiscountInfo.setText(supplierInfo.getRestaurantName());
        holder.textViewPromo1.setText(supplierInfo.getRestaurantName());
        holder.textViewPromo2.setText(supplierInfo.getRestaurantName());
        Glide.with(context).load(supplierInfo.getImgUrl()).into(holder.imageViewSupplier);

        // Setup RecyclerView cho danh sách món ăn của Supplier này
        FoodItemGroupedBySupplierAdapter foodItemAdapter = new FoodItemGroupedBySupplierAdapter(supplierWithFoodItems.getFoodItems(),holder.itemView.getContext());
        holder.recyclerViewFoodItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerViewFoodItems.setAdapter(foodItemAdapter);
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewSupplier;
        TextView textViewRestaurantName;
        TextView textViewTotalStar;
        TextView textViewCountReview;
        TextView textViewPriceDelivery;
        TextView textViewDiscountInfo;
        TextView textViewDeliveryTime;
        TextView textViewPromo1;
        TextView textViewPromo2;
        RecyclerView recyclerViewFoodItems;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSupplier = itemView.findViewById(R.id.imageViewSupplier);
            textViewRestaurantName = itemView.findViewById(R.id.textViewRestaurantName);
            textViewTotalStar = itemView.findViewById(R.id.textViewTotalStar);
            textViewCountReview = itemView.findViewById(R.id.textViewCountReview);
            textViewPriceDelivery = itemView.findViewById(R.id.textViewPriceDelivery);
            textViewDiscountInfo = itemView.findViewById(R.id.textViewDiscountInfo);
            textViewDeliveryTime = itemView.findViewById(R.id.textViewDeliveryTime);
            textViewPromo1 = itemView.findViewById(R.id.textViewPromo1);
            textViewPromo2 = itemView.findViewById(R.id.textViewPromo2);
            recyclerViewFoodItems = itemView.findViewById(R.id.recyclerViewFoodItems);
        }
    }
}
