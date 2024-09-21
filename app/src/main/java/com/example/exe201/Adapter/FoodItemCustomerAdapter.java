package com.example.exe201.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exe201.DTO.FoodItem;
import com.example.exe201.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodItemCustomerAdapter extends RecyclerView.Adapter<FoodItemCustomerAdapter.FoodItemCustomerViewHolder> {

    private List<FoodItem> foodItemList;
    private Context context;
    private List<FoodItem> cartList = new ArrayList<>(); // Danh sách giỏ hàng
    private TextView basketItemCount;
    private TextView basketTotalPrice;
    private LinearLayout basketLayout;
    private int totalAmount = 0;
    private Map<FoodItem, Integer> cartMap = new HashMap<>();



    public FoodItemCustomerAdapter(List<FoodItem> foodItemList, Context context,LinearLayout basketLayout, TextView basketItemCount, TextView basketTotalPrice) {
        this.foodItemList = foodItemList;
        this.context = context;
        this.basketLayout = basketLayout;
        this.basketItemCount = basketItemCount;
        this.basketTotalPrice = basketTotalPrice;
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

        // Xử lý khi người dùng nhấn vào dấu +
        holder.imgAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem món ăn đã có trong giỏ chưa
                if (cartMap.containsKey(foodItem)) {
                    // Tăng số lượng món ăn nếu đã có trong giỏ
                    int currentQuantity = cartMap.get(foodItem);
                    cartMap.put(foodItem, currentQuantity + 1);
                } else {
                    // Thêm món ăn mới vào giỏ với số lượng 1
                    cartMap.put(foodItem, 1);
                }
                updateBasketUI();
                Toast.makeText(v.getContext(), foodItem.getFoodName() + " đã thêm thành công vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Cập nhật hiển thị giỏ hàng
    private void updateBasketUI() {
        // Tính lại tổng tiền và tổng số lượng món ăn
        totalAmount = 0; // Reset tổng tiền trước khi tính lại
        int itemCount = 0; // Tổng số lượng món ăn
        for (Map.Entry<FoodItem, Integer> entry : cartMap.entrySet()) {
            FoodItem item = entry.getKey();
            int quantity = entry.getValue();

            // Cộng tổng số tiền
            totalAmount += item.getPrice() * quantity;

            // Cộng tổng số lượng item
            itemCount += quantity;
        }

        // Cập nhật UI giỏ hàng
        if (itemCount > 0) {
            basketItemCount.setText("Basket • " + itemCount + " Items");
            basketTotalPrice.setText(totalAmount + "đ"); // Cập nhật tổng tiền
            basketLayout.setVisibility(View.VISIBLE); // Hiển thị giỏ hàng khi có món ăn
        } else {
            basketLayout.setVisibility(View.GONE); // Ẩn nếu không có món nào trong giỏ
        }
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
        ImageView imgAddFood;
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
            imgAddFood = itemView.findViewById(R.id.imgAddFood);
        }
    }
}
