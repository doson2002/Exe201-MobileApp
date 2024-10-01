package com.example.exe201.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FoodItemCustomerAdapter extends RecyclerView.Adapter<FoodItemCustomerAdapter.FoodItemCustomerViewHolder> {

    private List<Menu> foodItemList;
    private HashMap<Integer, List<Menu>> cartMap = new HashMap<>();
    private Context context;
    private ImageView imgShowCart;
    private MenuListClickListener clickListener;


    public FoodItemCustomerAdapter(List<Menu> foodItemList, Context context, HashMap<Integer, List<Menu>> cartMap,
                                   ImageView imgShowCart) {
        this.foodItemList = foodItemList;
        this.context = context;
        this.cartMap = cartMap;
        this.imgShowCart = imgShowCart;

    }

    // Cập nhật giỏ hàng với cartMap
    public void updateCartMap(int supplierId, List<Menu> updatedCartList) {
        cartMap.put(supplierId, updatedCartList);
        notifyDataSetChanged();
    }

    // Lấy danh sách món ăn trong giỏ hàng cho nhà cung cấp cụ thể
    public List<Menu> getCartList(int supplierId) {
        return cartMap.getOrDefault(supplierId, new ArrayList<>());
    }
    // Phương thức để lấy cartMap
    public Map<Integer, List<Menu>> getCartMap() {
        return this.cartMap;
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
        // Kiểm tra và hiển thị imgShowCart nếu cartMap.size() > 1
//        if (cartMap.size() > 1 && imgShowCart != null) {  // Kiểm tra holder.imgShowCart không null
//            imgShowCart.setVisibility(View.VISIBLE);
//        } else if (imgShowCart != null) {
//            imgShowCart.setVisibility(View.GONE);
//        }

        holder.imgAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int supplierId = foodItem.getSupplierId(); // Giả sử Menu có supplierId

                // Lấy cartMap từ SharedPreferences
                HashMap<Integer, List<Menu>> savedCartMap = loadCartMapFromPreferences();
                if (savedCartMap == null) {
                    savedCartMap = new HashMap<>(); // Nếu không có dữ liệu, khởi tạo cartMap mới
                }

                // Lấy giỏ hàng hiện tại của nhà cung cấp từ savedCartMap
                List<Menu> currentCartList = savedCartMap.getOrDefault(supplierId, new ArrayList<>());

                boolean itemExists = false;
                // Kiểm tra xem món ăn đã có trong giỏ hàng chưa
                for (Menu item : currentCartList) {
                    if (item.getId() == foodItem.getId()) {
                        itemExists = true;
                        // Nếu có, tăng số lượng món ăn
                        item.setQuantity(item.getQuantity() + 1);
                        break;
                    }
                }

                // Nếu món ăn chưa có trong giỏ hàng, thêm mới
                if (!itemExists) {
                    foodItem.setQuantity(1);
                    currentCartList.add(foodItem);
                }

                // Cập nhật giỏ hàng của supplier trong savedCartMap
                savedCartMap.put(supplierId, currentCartList);

                // Lưu cartMap mới vào SharedPreferences
                saveCartMapToPreferences(savedCartMap);

                // Cập nhật cartMap trong adapter
                cartMap = savedCartMap;

                // Hiển thị hoặc ẩn imgShowCart
                if (cartMap != null && !cartMap.isEmpty()) {
                    imgShowCart.setVisibility(View.VISIBLE); // Hiển thị imgShowCart nếu cartMap không rỗng
                } else {
                    imgShowCart.setVisibility(View.GONE); // Ẩn imgShowCart nếu cartMap rỗng
                }

                Toast.makeText(v.getContext(), foodItem.getName() + " đã thêm thành công vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void saveCartMapToPreferences(HashMap<Integer, List<Menu>> cartMap) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(cartMap); // Chuyển đổi cartMap thành JSON

        editor.putString("cart_map", json); // Lưu vào SharedPreferences
        editor.apply();
    }
    private HashMap<Integer, List<Menu>> loadCartMapFromPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("cart_map", null);

        Type type = new TypeToken<HashMap<Integer, List<Menu>>>() {}.getType();
        return new Gson().fromJson(json, type); // Chuyển đổi JSON về HashMap
    }


    @Override
    public int getItemCount() {
        return foodItemList.size();
    }



    // ViewHolder ánh xạ các thành phần UI từ layout item_food_item_customer.xml
    public static class FoodItemCustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFoodItem;
        ImageView imgAddFood;
        TextView textViewFoodItemName;
        TextView textViewDescription;
        TextView textViewFoodItemOriginalPrice;
        TextView textViewFoodItemDiscountPrice;
        ImageView imgShowCart; // Biến cho icon giỏ hàng



        public FoodItemCustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFoodItem = itemView.findViewById(R.id.imageViewFoodItem);
            textViewFoodItemName = itemView.findViewById(R.id.textViewFoodItemName);
            textViewFoodItemOriginalPrice = itemView.findViewById(R.id.textViewFoodItemOriginalPrice);
            textViewFoodItemDiscountPrice = itemView.findViewById(R.id.textViewFoodItemDiscountPrice);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            imgShowCart = itemView.findViewById(R.id.imgShowCart);
            imgAddFood = itemView.findViewById(R.id.imgAddFood);
        }


    }
    public interface MenuListClickListener{
        public void onAddToCartClick(Menu menu);
    }
}
