package com.example.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe201.Adapter.CartAdapter;
import com.example.exe201.DTO.CartFoodItem;
import com.example.exe201.DTO.FoodItem;
import com.example.exe201.DTO.Menu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {


    private List<Menu> cartList;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ImageView backArrow;
    private TextView textViewEditAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);

        textViewEditAddress= findViewById(R.id.textViewEditAddress);
        textViewEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });

        // Khởi tạo hoặc lấy dữ liệu giỏ hàng ở đây
        cartList = getCartItemsFromSharedPreferences();

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        // Lấy dữ liệu giỏ hàng từ Intent hoặc nguồn dữ liệu khác
        cartList = (ArrayList<Menu>) getIntent().getSerializableExtra("cart_list");

        if (cartList != null && !cartList.isEmpty()) {
            // Thiết lập Adapter và truyền dữ liệu
            cartAdapter = new CartAdapter(this, cartList);
            recyclerView.setAdapter(cartAdapter);

            // Gửi danh sách giỏ hàng đến Adapter
            cartAdapter.submitList(cartList);

            // Thiết lập lắng nghe sự kiện thay đổi số lượng trong Adapter
            cartAdapter.setCartUpdateListener(new CartAdapter.CartUpdateListener() {
                @Override
                public void onCartUpdated() {
                    updateCartList(); // Gọi hàm cập nhật lại tổng số tiền và số lượng
                }
            });
            // Cập nhật giá và số lượng ban đầu
            updateCartList();
        } else {
            // Xử lý khi cartList rỗng hoặc null
            Log.d("CartList", "No items in cart");
        }
//        // Tìm kiếm LinearLayout bằng ID
//        LinearLayout layoutCashPayment = findViewById(R.id.layoutCashPayment);
//
//        // Đặt OnClickListener cho LinearLayout
//        layoutCashPayment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Khi người dùng nhấn vào layout này, chuyển đến Activity mới
//                Intent intent = new Intent(OrderActivity.this, PaymentMethodActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    // Phương thức cập nhật lại tổng số tiền và số lượng sau khi thay đổi trong giỏ hàng
    private void updateCartList() {
        double totalAmount = 0;
        int totalItems = 0;

        for (Menu item : cartList) {
            totalAmount += item.getPrice() * item.getQuantity();
            totalItems += item.getQuantity();
        }

        // Cập nhật UI của Activity với thông tin tổng tiền và tổng số lượng
        TextView totalAmountView = findViewById(R.id.textViewTotal);
        TextView totalItemsView = findViewById(R.id.textViewItemAmount);

        totalAmountView.setText(String.format("%,.0fđ", totalAmount));
        totalItemsView.setText(String.format("%d món", totalItems));
    }
    private double totalPrice = 0.0;
    public void updateTotalPrice(double newTotalPrice) {
        Log.d("OrderActivity", "Updating total price in Activity: " + newTotalPrice);
        totalPrice = newTotalPrice;
        TextView totalPriceTextView = findViewById(R.id.textViewTotal);
        totalPriceTextView.setText(String.format("Tổng: %,.0fđ", totalPrice));
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Lưu dữ liệu giỏ hàng vào SharedPreferences khi Activity bị tạm dừng
        saveCartItemsToSharedPreferences();

    }
    private void saveCartItemsToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Chuyển đổi danh sách item sang JSON
        Gson gson = new Gson();
        String cartJson = gson.toJson(cartList);

        // Lưu JSON vào SharedPreferences
        editor.putString("cart_items", cartJson);
        editor.apply();
    }
    private ArrayList<Menu> getCartItemsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPreferences", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart_items", "");

        // Chuyển đổi JSON sang danh sách CartFoodItem
        if (!cartJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Menu>>() {}.getType();
            return gson.fromJson(cartJson, type);
        }

        return new ArrayList<>(); // Trả về danh sách rỗng nếu không có dữ liệu
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Dừng các tác vụ nếu cần
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy các tác vụ nếu cần
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_cart_list", (Serializable) cartList); // Trả lại giỏ hàng đã cập nhật
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }


}