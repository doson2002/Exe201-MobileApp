package com.example.exe201.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.exe201.DTO.FoodItemResponseWithSupplier;
import com.example.exe201.R;

import java.util.List;

public class FoodItemGroupedBySupplierAdapter extends RecyclerView.Adapter<FoodItemGroupedBySupplierAdapter.MyViewHolder> {

    private List<FoodItemResponseWithSupplier> foodItemList;
    private Context context; // Để load ảnh từ URL

    public FoodItemGroupedBySupplierAdapter(List<FoodItemResponseWithSupplier> foodItemList,Context context) {
        this.foodItemList = foodItemList;
        this.context = context;
    }
    // Phương thức để cập nhật danh sách món ăn
    public void updateFoodItemList(List<FoodItemResponseWithSupplier> newFoodItemList) {
        this.foodItemList.clear();  // Xóa danh sách cũ
        this.foodItemList.addAll(newFoodItemList);  // Thêm danh sách mới
        notifyDataSetChanged();  // Thông báo dữ liệu thay đổi để RecyclerView cập nhật
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_grouped_by_supplier, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FoodItemResponseWithSupplier foodItem = foodItemList.get(position);
        holder.foodName.setText(foodItem.getFoodName());
        holder.foodPrice.setText(String.valueOf(foodItem.getPrice()) + "đ");
        // Thiết lập thêm các thông tin khác nếu cần

        // Load ảnh món ăn từ URL

        Glide.with(context)
                .load(foodItem.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_downloading_24) // Ảnh mặc định khi đang tải
                        .error(R.drawable.baseline_downloading_24) // Ảnh mặc định khi URL rỗng hoặc lỗi
                        .fitCenter() // Cắt ảnh cho vừa vặn với ImageView hình vuông
                        .transform(new RoundedCorners(30))) // Bo tròn 4 góc
                .into(holder.foodImage);

        // Sự kiện click cho nút "Add to Cart"
        holder.btnAddToCart.setOnClickListener(v -> {
            // Xử lý thêm món ăn vào giỏ hàng tại đây
            Toast.makeText(context, "Đã thêm " + foodItem.getFoodName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice;
        ImageView foodImage, btnAddToCart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.tvFoodName);
            foodPrice = itemView.findViewById(R.id.tvFoodPrice);
            foodImage = itemView.findViewById(R.id.ivFoodImage);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}