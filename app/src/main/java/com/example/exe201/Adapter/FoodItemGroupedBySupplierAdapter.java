package com.example.exe201.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.exe201.DTO.Menu;
import com.example.exe201.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodItemGroupedBySupplierAdapter extends RecyclerView.Adapter<FoodItemGroupedBySupplierAdapter.MyViewHolder> {

    private List<FoodItemResponseWithSupplier> foodItemList;
    private Context context; // Để load ảnh từ URL
    private HashMap<Integer, List<Menu>> cartMap = new HashMap<>();
    private ImageView imgShowCart;

    public FoodItemGroupedBySupplierAdapter(List<FoodItemResponseWithSupplier> foodItemList,Context context,
                                            HashMap<Integer, List<Menu>> cartMap,
                                            ImageView imgShowCart) {
        this.foodItemList = foodItemList;
        this.context = context;
        this.cartMap = cartMap;
        this.imgShowCart = imgShowCart;
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
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
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

                // Nếu món ăn chưa có trong giỏ hàng, chuyển đổi và thêm mới
                if (!itemExists) {
                    Menu menuItem = mapFoodItemToMenu(foodItem);
                    menuItem.setQuantity(1); // Đặt số lượng ban đầu là 1
                    currentCartList.add(menuItem);
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

                Toast.makeText(v.getContext(), foodItem.getFoodName() + " đã thêm thành công vào giỏ hàng", Toast.LENGTH_SHORT).show();
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

    private Menu mapFoodItemToMenu(FoodItemResponseWithSupplier foodItem) {
        Menu menu = new Menu();
        menu.setId(foodItem.getId()); // Giả sử cả 2 class đều có id
        menu.setName(foodItem.getFoodName());
        menu.setPrice(foodItem.getPrice());
        menu.setQuantity(foodItem.getQuantity()); // Số lượng sẽ cần được set khi thêm vào giỏ hàng
        menu.setImgUrl(foodItem.getImageUrl());
        menu.setSupplierId(foodItem.getSupplierId());
        // Thêm các thuộc tính khác nếu cần thiết
        return menu;
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