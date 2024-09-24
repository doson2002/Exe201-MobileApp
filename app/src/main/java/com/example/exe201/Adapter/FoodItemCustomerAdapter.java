package com.example.exe201.Adapter;

import android.content.Context;
import android.util.Log;
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
import com.example.exe201.DTO.CartFoodItem;
import com.example.exe201.DTO.FoodItem;
import com.example.exe201.DTO.Menu;
import com.example.exe201.R;

import java.util.ArrayList;
import java.util.List;

public class FoodItemCustomerAdapter extends RecyclerView.Adapter<FoodItemCustomerAdapter.FoodItemCustomerViewHolder> {

    private List<Menu> foodItemList;
    private List<Menu> cartList;
    private Context context;
    private LinearLayout addToCart;
    private MenuListClickListener clickListener;
    private TextView  basketItemCount, basketTotalPrice;


    public FoodItemCustomerAdapter(List<Menu> foodItemList, Context context, List<Menu> cartList,
                                   LinearLayout addToCart,
                                   TextView  basketItemCount, TextView basketTotalPrice) {
        this.foodItemList = foodItemList;
        this.context = context;
        this.cartList = cartList;
        this.addToCart = addToCart;
        this.basketItemCount = basketItemCount;
        this.basketTotalPrice = basketTotalPrice;

    }

    public void updateCartList(List<Menu> updatedCartList){
        this.cartList = updatedCartList;
        notifyDataSetChanged();
    }

    public List<Menu> getCartList() {
        return cartList;
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

        int currentPosition = holder.getAdapterPosition();
        // Lấy FoodItem từ danh sách
        Menu foodItem = foodItemList.get(currentPosition);


        // Thiết lập dữ liệu cho các TextView và ImageView
        holder.textViewFoodItemName.setText(foodItem.getName());
        holder.textViewFoodItemOriginalPrice.setText(String.format("%,.0fđ", foodItem.getPrice()));
        holder.textViewFoodItemDiscountPrice.setText(String.format("%,.0fđ", foodItem.getPrice()));
        holder.textViewDescription.setText(foodItem.getDescription());
        Glide.with(context).load(foodItem.getImgUrl()).into(holder.imageViewFoodItem);

//        holder.imgAddFood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clickListener.onAddToCartClick(foodItemList.get(currentPosition));
//            }
//        });
        // Xử lý khi người dùng nhấn vào dấu +
        holder.imgAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra nếu món ăn đã có trong giỏ hàng
                boolean itemExists = false;
                for (Menu item : cartList) {
                    if (item.getId() == foodItem.getId()) { // So sánh ID
                        itemExists = true;
                        // Tăng số lượng món ăn trong giỏ hàng
                        item.setQuantity(item.getQuantity() + 1);
                        break; // Kết thúc vòng lặp khi đã tìm thấy
                    }
                }

                // Nếu món ăn chưa có trong giỏ hàng, thêm mới
                if (!itemExists) {
                    foodItem.setQuantity(1); // Đặt số lượng ban đầu là 1
                    cartList.add(foodItem); // Thêm vào giỏ hàng
                }

                updateBasketUI();
                // Cập nhật giao diện số lượng trên TextView
                Toast.makeText(v.getContext(), foodItem.getName() + " đã thêm thành công vào giỏ hàng", Toast.LENGTH_SHORT).show();
                Log.d("OrderActivity", "Số lượng món ăn: " + foodItem.getName() + " - " + foodItem.getQuantity());
                Log.d("OrderActivity", "List sau khi thêm: " + getCartList().toString());
            }
        });


    }


    @Override
    public int getItemCount() {
        return foodItemList.size();
    }


    // Cập nhật hiển thị giỏ hàng
    private void updateBasketUI() {
        if (cartList.size() > 0) {
            // Tính lại tổng tiền và tổng số lượng món ăn
            double totalAmount = 0; // Reset tổng tiền trước khi tính lại
            int itemCount = 0; // Tổng số lượng món ăn
            for (Menu cartItem : cartList) {
                totalAmount += cartItem.getPrice() * cartItem.getQuantity();
                itemCount += cartItem.getQuantity();
            }

            // Cập nhật số lượng và tổng tiền
            try {
                basketItemCount.setText("• " + String.format("%,d", itemCount) + " Món");
            } catch (NullPointerException e) {
                Log.e("Error", "basketItemCount is null", e);
            }
            basketTotalPrice.setText(String.format("%,.0fđ", totalAmount));
            addToCart.setVisibility(View.VISIBLE);
        } else {
            // Ẩn nút giỏ hàng nếu không có item nào
            addToCart.setVisibility(View.GONE);
        }
    }
    // ViewHolder ánh xạ các thành phần UI từ layout item_food_item_customer.xml
    public static class FoodItemCustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFoodItem;
        ImageView imgAddFood;
        LinearLayout addToCart;
        TextView textViewFoodItemName;
        TextView textViewDescription;
        TextView textViewFoodItemOriginalPrice;
        TextView textViewFoodItemDiscountPrice;
        TextView basketItemCount, basketTotalPrice;



        public FoodItemCustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFoodItem = itemView.findViewById(R.id.imageViewFoodItem);
            textViewFoodItemName = itemView.findViewById(R.id.textViewFoodItemName);
            textViewFoodItemOriginalPrice = itemView.findViewById(R.id.textViewFoodItemOriginalPrice);
            textViewFoodItemDiscountPrice = itemView.findViewById(R.id.textViewFoodItemDiscountPrice);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            addToCart = itemView.findViewById(R.id.addToCart);
            imgAddFood = itemView.findViewById(R.id.imgAddFood);
            basketTotalPrice = itemView.findViewById(R.id.basketTotalPrice);
            basketItemCount = itemView.findViewById((R.id.basketItemCount));
        }


    }
    public interface MenuListClickListener{
        public void onAddToCartClick(Menu menu);
    }
}
