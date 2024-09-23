package com.example.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.FoodItemAdapter;
import com.example.exe201.Adapter.FoodItemCustomerAdapter;
import com.example.exe201.DTO.CartFoodItem;
import com.example.exe201.DTO.FoodItem;
import com.example.exe201.DTO.FoodType;
import com.example.exe201.DTO.Menu;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.Decorations.CustomDividerItemDecoration;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowFoodItemActivity extends AppCompatActivity {

    private LinearLayout parentLinearLayout, addToCart;
    private TextView basketItemCount, basketTotalPrice;
    // Khai báo cartMap để lưu các món ăn và số lượng
    private FoodItemCustomerAdapter foodAdapter;


    private  List<Menu> cartList = new ArrayList<>();
    private List<FoodType> foodTypeList = new ArrayList<>();
    private List<Menu> foodItemList = new ArrayList<>();
    private ImageView backArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_food_item);


        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
        // Bước 1: Lấy TextView cần hiển thị tên nhà cung cấp
        TextView textViewRestaurantName = findViewById(R.id.textViewRestaurantName);
        TextView textViewReviews = findViewById(R.id.textViewReviews);


        // Nhận đối tượng SupplierInfo từ Intent
        SupplierInfo supplierInfo = getIntent().getParcelableExtra("supplier");
        int supplierId = supplierInfo.getId();
        if(supplierId != -1){
            getFoodTypeSupplierId(supplierId);
        }

        // Nếu SupplierInfo không null, hiển thị tên nhà cung cấp
        if (supplierInfo != null) {
            // Bước 3: Lấy tên nhà cung cấp từ SupplierInfo và thiết lập cho TextView
            String supplierName = supplierInfo.getRestaurantName();
            int totalRating = supplierInfo.getTotalReviewCount();
            textViewReviews.setText(String.format("(%d ratings)", totalRating));
            textViewRestaurantName.setText(supplierName);
        } else {
            // Nếu supplierInfo null, bạn có thể hiển thị thông báo lỗi hoặc tên mặc định
            textViewRestaurantName.setText("Unknown Supplier");
        }
        parentLinearLayout = findViewById(R.id.parentLinearLayout);

        addToCart = findViewById(R.id.addToCart);
        basketItemCount = findViewById(R.id.basketItemCount);
        if (basketItemCount == null) {
            Log.e("Error", "basketItemCount is null");
        }
         basketTotalPrice = findViewById(R.id.basketTotalPrice);

         // Tạo FoodItemCustomerAdapter và gán cartList cho nó
        foodAdapter = new FoodItemCustomerAdapter(foodItemList, this,cartList,addToCart, basketItemCount , basketTotalPrice);

        // Thiết lập sự kiện OnClickListener cho basketLayout
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy danh sách giỏ hàng từ adapter
                cartList = foodAdapter.getCartList();  // Lấy giỏ hàng từ adapter

                // Chuyển sang trang khác khi click vào basketLayout
                Intent intent = new Intent(ShowFoodItemActivity.this, OrderActivity.class);
                intent.putExtra("cart_list", (Serializable) cartList);  // Sử dụng Serializable để truyền dữ liệu  // Truyền List<CartFoodItem> qua Intent
                startActivityForResult(intent, 1); // Gọi startActivityForResult với requestCode
            }
        });
    }

    private void getFoodTypeSupplierId(int supplierId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        String url = ApiEndpoints.GET_FOOD_TYPES_BY_SUPPLIER_ID + "/"+ supplierId; // Thay thế bằng URL của bạn

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String typeName = jsonObject.getString("typeName");
                                int order = jsonObject.getInt("i");

                                // Tạo đối tượng FoodType từ JSON
                                FoodType foodType = new FoodType();
                                foodType.setId(id);
                                foodType.setTypeName(typeName);
                                foodType.setOrder(order);
                                // Bạn cũng có thể thêm các thuộc tính khác từ JSON vào FoodType

                                foodTypeList.add(foodType);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        displayFoodTypes();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void displayFoodTypes() {
        for (final FoodType foodType : foodTypeList) {
            // Tạo TextView cho FoodType
            TextView textView = new TextView(this);
            textView.setText(foodType.getTypeName());
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK); // Đặt màu đen cho text
            textView.setTypeface(null, Typeface.BOLD);
            textView.setAllCaps(true); // Đặt tất cả chữ viết hoa
            // Thêm marginBottom cho TextView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 30, 0, 30);
            textView.setLayoutParams(params);
            // Thêm TextView vào LinearLayout chính
            parentLinearLayout.addView(textView);

            // Tạo RecyclerView cho FoodType
            final RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            // Sử dụng CustomDividerItemDecoration
            CustomDividerItemDecoration itemDecoration = new CustomDividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
            itemDecoration.setDrawable(dividerDrawable);
            recyclerView.addItemDecoration(itemDecoration);

            // Thêm RecyclerView vào parentLinearLayout
            parentLinearLayout.addView(recyclerView);

            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String jwtToken = sharedPreferences.getString("JwtToken", null);
            // Gọi API để lấy danh sách FoodItem theo FoodTypeId
            String url = ApiEndpoints.GET_FOOD_ITEM_BY_FOOD_TYPE_ID +"/"+ foodType.getId();
            JsonArrayRequest foodItemRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                             foodItemList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String name = jsonObject.getString("food_name");
                                    double price = jsonObject.getDouble("price");
                                    String imageUrl = jsonObject.getString("image_url");
                                    String description = jsonObject.getString("description");

                                    Menu foodItem = new Menu(id, name, description, price,imageUrl);
                                    foodItemList.add(foodItem);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            // Đặt Adapter cho RecyclerView
                            foodAdapter = new FoodItemCustomerAdapter(
                                    foodItemList,
                                    ShowFoodItemActivity.this,
                                    cartList,
                                    addToCart,
                                    basketItemCount,
                                    basketTotalPrice);
                            recyclerView.setAdapter(foodAdapter);
                            // Đặt chiều cao cho RecyclerView dựa trên số lượng mục
                            setRecyclerViewHeight(recyclerView, foodItemList.size());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", error.toString());
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(foodItemRequest);        }
    }

//    private void displayFoodTypes() {
//        // Lấy RecyclerView từ XML
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        CustomDividerItemDecoration itemDecoration = new CustomDividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
//        itemDecoration.setDrawable(dividerDrawable);
//        recyclerView.addItemDecoration(itemDecoration);
//
//        // Danh sách chung để chứa toàn bộ FoodItems
//        final List<Menu> allFoodItems = new ArrayList<>();
//
//        // Vòng lặp qua từng FoodType và thêm các FoodItem vào danh sách chung
//        for (final FoodType foodType : foodTypeList) {
//            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//            String jwtToken = sharedPreferences.getString("JwtToken", null);
//
//            // Gọi API để lấy danh sách FoodItem theo FoodTypeId
//            String url = ApiEndpoints.GET_FOOD_ITEM_BY_FOOD_TYPE_ID + "/" + foodType.getId();
//            JsonArrayRequest foodItemRequest = new JsonArrayRequest(Request.Method.GET, url, null,
//                    new Response.Listener<JSONArray>() {
//                        @Override
//                        public void onResponse(JSONArray response) {
//                            for (int i = 0; i < response.length(); i++) {
//                                try {
//                                    JSONObject jsonObject = response.getJSONObject(i);
//                                    int id = jsonObject.getInt("id");
//                                    String name = jsonObject.getString("food_name");
//                                    double price = jsonObject.getDouble("price");
//                                    String imageUrl = jsonObject.getString("image_url");
//                                    String description = jsonObject.getString("description");
//
//                                    Menu foodItem = new Menu(id, name, description, price, imageUrl);
//                                    allFoodItems.add(foodItem);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            // Đặt Adapter cho RecyclerView với danh sách tổng hợp các FoodItem
//                            foodAdapter = new FoodItemCustomerAdapter(
//                                    allFoodItems,
//                                    ShowFoodItemActivity.this);
//                            recyclerView.setAdapter(foodAdapter);
//
//                            // Đặt chiều cao cho RecyclerView dựa trên số lượng mục
//                            setRecyclerViewHeight(recyclerView, allFoodItems.size());
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("Volley Error", error.toString());
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
//                    return headers;
//                }
//            };
//
//            // Thêm yêu cầu vào hàng đợi
//            Volley.newRequestQueue(this).add(foodItemRequest);
//        }
//    }



    private void setRecyclerViewHeight(RecyclerView recyclerView, int itemCount) {
        int itemHeight = 100; // Chiều cao trung bình của một mục, đơn vị: dp
        float density = getResources().getDisplayMetrics().density;
        int totalHeight = (int) (itemCount * itemHeight * density);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight;
        recyclerView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.hasExtra("updated_cart_list")) {
                cartList = (List<Menu>) data.getSerializableExtra("updated_cart_list"); // Nhận giỏ hàng đã cập nhật
                updateCartInfo(); // Cập nhật thông tin giỏ hàng trên UI
            }
        }
    }
    private void updateCartInfo() {
        // Cập nhật thông tin giỏ hàng trên UI nếu cần
        double totalPrice = 0;
        for (Menu item : cartList) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        basketTotalPrice.setText(String.format("%,.0fđ ", totalPrice)); // Cập nhật tổng giá
        basketItemCount.setText(String.valueOf(cartList.size()) + " Món"); // Cập nhật số lượng
    }

}