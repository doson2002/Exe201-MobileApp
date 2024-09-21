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
import com.example.exe201.DTO.FoodType;
import com.example.exe201.R;

import java.util.List;

public class FoodTypeAdapter extends RecyclerView.Adapter<FoodTypeAdapter.FoodTypeViewHolder> {

    private List<FoodType> foodTypeList;
    private Context context;
    private int selectedPosition = -1;
    private OnFoodTypeClickListener listener;

    public FoodTypeAdapter(List<FoodType> foodTypeList, Context context, OnFoodTypeClickListener listener) {
        this.foodTypeList = foodTypeList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public FoodTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_type, parent, false);
        return new FoodTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodTypeViewHolder holder, int position) {
        FoodType foodType = foodTypeList.get(position);
        holder.textViewFoodTypeName.setText(foodType.getTypeName());


        // Kiểm tra vị trí được chọn
        if (selectedPosition == holder.getAdapterPosition()) {
            holder.itemView.setBackgroundResource(R.drawable.circle_with_check); // Thay thế bằng background của bạn
        } else {
            holder.itemView.setBackgroundResource(R.drawable.image_view_background); // Thay thế bằng background của bạn

        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            listener.onFoodTypeClick(foodType);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return foodTypeList.size();
    }

    public interface OnFoodTypeClickListener {
        void onFoodTypeClick(FoodType foodType);
    }

    public static class FoodTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFoodType;
        TextView textViewFoodTypeName;

        public FoodTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFoodType = itemView.findViewById(R.id.imageViewFoodType);
            textViewFoodTypeName = itemView.findViewById(R.id.textViewFoodTypeName);
        }
    }
}
