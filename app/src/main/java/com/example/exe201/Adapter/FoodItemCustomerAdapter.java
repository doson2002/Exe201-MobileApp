package com.example.exe201.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exe201.DTO.FoodItem;
import com.example.exe201.R;

import java.util.ArrayList;
import java.util.List;

public class FoodItemCustomerAdapter extends RecyclerView.Adapter<FoodItemCustomerAdapter.FoodItemCustomerViewHolder> {

    private List<FoodItem> foodItemList;
    private Context context;

    public FoodItemCustomerAdapter(List<FoodItem> foodItemList, Context context) {
        this.foodItemList = foodItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodItemCustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout item_food_item_customer.xml cho ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_customer, parent, false);
        return new FoodItemCustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemCustomerViewHolder holder, int position) {
        // Lấy FoodItem từ danh sách
        FoodItem foodItem = foodItemList.get(position);

        // Thiết lập dữ liệu cho các TextView và ImageView
        holder.textViewFoodItemName.setText(foodItem.getFoodName());
        holder.textViewFoodItemOriginalPrice.setText(String.format("%,.0fđ", foodItem.getPrice()));
        holder.textViewFoodItemDiscountPrice.setText(String.format("%,.0fđ", foodItem.getPrice()));
        holder.textViewDescription.setText(foodItem.getDescription());
        Glide.with(context).load(foodItem.getImageUrl()).into(holder.imageViewFoodItem);
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    // Cập nhật danh sách món ăn trong Adapter
    public void updateFoodItemList(List<FoodItem> newFoodItemList) {
        this.foodItemList = new ArrayList<>(newFoodItemList);
        notifyDataSetChanged();
    }

    // ViewHolder ánh xạ các thành phần UI từ layout item_food_item_customer.xml
    public static class FoodItemCustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFoodItem;
        TextView textViewFoodItemName;
        TextView textViewDescription;
        TextView textViewFoodItemOriginalPrice;
        TextView textViewFoodItemDiscountPrice;


        public FoodItemCustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFoodItem = itemView.findViewById(R.id.imageViewFoodItem);
            textViewFoodItemName = itemView.findViewById(R.id.textViewFoodItemName);
            textViewFoodItemOriginalPrice = itemView.findViewById(R.id.textViewFoodItemOriginalPrice);
            textViewFoodItemDiscountPrice = itemView.findViewById(R.id.textViewFoodItemDiscountPrice);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
