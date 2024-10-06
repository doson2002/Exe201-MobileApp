package com.example.exe201.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.exe201.DTO.FoodItemTopSold;
import com.example.exe201.R;
import com.example.exe201.ShowFoodItemActivity;

import java.util.List;

public class FoodItemTopSoldAdapter extends RecyclerView.Adapter<FoodItemTopSoldAdapter.FoodViewHolder> {
    private Context context;
    private List<FoodItemTopSold> foodItemList;

    public FoodItemTopSoldAdapter(Context context, List<FoodItemTopSold> foodItemList) {
        this.context = context;
        this.foodItemList = foodItemList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_food_sold, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItemTopSold foodItemTopSold = foodItemList.get(position);
        holder.textViewFoodName.setText(foodItemTopSold.getName());
        holder.textViewQuantitySold.setText("Đã bán: " + foodItemTopSold.getQuantitySold());

        // Load image using Glide or Picasso
        Glide.with(context)
                .load(foodItemTopSold.getSupplierInfo().getImgUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_downloading_24) // Ảnh mặc định khi đang tải
                        .error(R.drawable.baseline_downloading_24) // Ảnh mặc định khi URL rỗng hoặc lỗi
                        .fitCenter() // Cắt ảnh cho vừa vặn với ImageView hình vuông
                        .transform(new RoundedCorners(40))) // Bo tròn 4 góc
                .into(holder.imageViewFood);

        // Set click listener for each item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowFoodItemActivity.class);
            intent.putExtra("supplier", foodItemTopSold.getSupplierInfo()); // Gửi SupplierInfo
            intent.putExtra("selectedFoodItemId", foodItemTopSold.getFoodItemId()); // Gửi vị trí của foodItem
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFood;
        TextView textViewFoodName;
        TextView textViewQuantitySold;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
            textViewQuantitySold = itemView.findViewById(R.id.textViewQuantitySold);
        }
    }
}