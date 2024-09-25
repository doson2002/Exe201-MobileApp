package com.example.exe201;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.SupplierWithFoodItemAdapter;
import com.example.exe201.DTO.FoodItemResponseWithSupplier;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.DTO.SupplierWithFoodItems;
import com.example.exe201.Decorations.CustomItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodItemGroupedBySupplierActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageView backArrow;
    private RecyclerView recyclerViewGroupedBySupplier;
    private SupplierWithFoodItemAdapter supplierAdapter;
    private List<SupplierWithFoodItems> supplierWithFoodItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_grouped_by_supplier);

        String keyword = getIntent().getStringExtra("keyword");
        searchFoodItems(keyword);


        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });


        // Ánh xạ các thành phần UI
        searchEditText = findViewById(R.id.search);
        searchEditText.setText(keyword);

        recyclerViewGroupedBySupplier = findViewById(R.id.recyclerViewGroupedBySupplier);

        // Chuyển đổi 20dp thành pixel
        int spacingInPixels = (int) (20 * getResources().getDisplayMetrics().density);
        // Setup RecyclerView và Adapter
        recyclerViewGroupedBySupplier.setLayoutManager(new LinearLayoutManager(this));
        supplierAdapter = new SupplierWithFoodItemAdapter(supplierWithFoodItemsList, this);
        recyclerViewGroupedBySupplier.setAdapter(supplierAdapter);
        // Thêm khoảng cách giữa các item
        recyclerViewGroupedBySupplier.addItemDecoration(new CustomItemDecoration(spacingInPixels));

        // Lắng nghe sự thay đổi text trong EditText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Không cần xử lý gì trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Gọi hàm search khi text thay đổi
                searchFoodItems(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Không cần xử lý gì sau khi text thay đổi
            }
        });
    }

    // Hàm gọi API để tìm kiếm các món ăn theo từ khóa
    private void searchFoodItems(String keyword) {
        // Thực hiện lời gọi API tới backend để lấy danh sách món ăn theo từ khóa
        String url = ApiEndpoints.GET_FOOD_ITEM_GROUPED_BY_SUPPLIER_ID + keyword;
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        // Sử dụng thư viện Volley để gửi request đến API (hoặc thư viện khác)
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Xử lý dữ liệu JSON trả về
                        supplierWithFoodItemsList.clear(); // Xóa danh sách cũ trước khi thêm mới

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject supplierWithFoodItemsJson = response.getJSONObject(i);

                            // Lấy thông tin SupplierInfo
                            JSONObject supplierInfoJson = supplierWithFoodItemsJson.getJSONObject("supplierInfo");
                            SupplierInfo supplierInfo = new SupplierInfo(
                                    supplierInfoJson.getInt("id"),
                                    supplierInfoJson.getString("restaurantName"),
                                    supplierInfoJson.getString("imgUrl"),
                                    supplierInfoJson.getDouble("totalStarRating"),
                                    supplierInfoJson.getInt("totalReviewCount")
                            );

                            // Lấy danh sách FoodItems
                            JSONArray foodItemsJsonArray = supplierWithFoodItemsJson.getJSONArray("foodItems");
                            List<FoodItemResponseWithSupplier> foodItemResponses = new ArrayList<>();
                            for (int j = 0; j < foodItemsJsonArray.length(); j++) {
                                JSONObject foodItemJson = foodItemsJsonArray.getJSONObject(j);
                                FoodItemResponseWithSupplier foodItemResponse = new FoodItemResponseWithSupplier(
                                        foodItemJson.getLong("id"),
                                        foodItemJson.getString("food_name"),
                                        foodItemJson.getDouble("price"),
                                        foodItemJson.getString("image_url")
                                );
                                foodItemResponses.add(foodItemResponse);
                            }

                            // Tạo đối tượng SupplierWithFoodItems và thêm vào danh sách
                            SupplierWithFoodItems supplierWithFoodItems = new SupplierWithFoodItems(supplierInfo, foodItemResponses);
                            supplierWithFoodItemsList.add(supplierWithFoodItems);
                        }

                        // Cập nhật Adapter sau khi dữ liệu mới được tải về
                        supplierAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Xử lý lỗi nếu có
                    error.printStackTrace();
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Thêm request vào hàng đợi
        queue.add(jsonArrayRequest);
    }
}