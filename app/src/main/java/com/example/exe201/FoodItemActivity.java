package com.example.exe201;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.FoodItemAdapter;
import com.example.exe201.DTO.FoodItem;
import com.example.exe201.DTO.FoodType;
import com.example.exe201.DTO.SupplierInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodItemActivity extends AppCompatActivity {

    ImageView backArrow;
    private RecyclerView recyclerViewMenu;
    private FloatingActionButton fabAddFood;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> foodItems;
   Button viewDetailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_item);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc Activity hiện tại và quay về Activity trước đó
                finish();
            }
        });
// Ánh xạ các view
        fabAddFood = findViewById(R.id.fabAddFood);

        // Khởi tạo danh sách và adapter
        foodItems = new ArrayList<>(); // Đảm bảo khởi tạo danh sách ở đây
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);

        // Thiết lập LayoutManager cho RecyclerView
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));


        // Gọi API để lấy nhà cung cấp
        fetchSupplierByUserId(userId);

        // Xử lý sự kiện khi nhấn nút thêm món ăn mới
//        fabAddFood.setOnClickListener(v -> {
//            // Chuyển tới Activity hoặc Fragment thêm món ăn mới
//            Intent intent = new Intent(this, AddFoodItemActivity.class);
//            startActivity(intent);
//        });


    }
    private void fetchSupplierByUserId(int userId) {

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.GET_SUPPLIER_BY_USER_ID+"/" + userId; // Thay thế bằng URL API của bạn
        RequestQueue queue = Volley.newRequestQueue(FoodItemActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy supplierId từ phản hồi
                            int supplierId = response.getInt("id"); // Lấy id của restaurant

                            // Sau khi lấy được supplierId, gọi hàm getFoodItemBySupplierId
                            fetchFoodItemsBySupplierId(supplierId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FoodItemActivity.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi khi gọi API
                Toast.makeText(FoodItemActivity.this, "Failed to fetch supplier data", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
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

        queue.add(jsonObjectRequest);
    }

   //lấy thực đơn theo nhà cung cấp
   private void fetchFoodItemsBySupplierId(int supplierId) {
       String url = ApiEndpoints.GET_ALL_FOOD_ITEMS_BY_SUPPLIER + "/" + supplierId; // Thay bằng URL API của bạn
       SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
       String jwtToken = sharedPreferences.getString("JwtToken", null);

       // Sử dụng JsonArrayRequest để gọi API
       JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
               Request.Method.GET, url, null,
               new Response.Listener<JSONArray>() {
                   @Override
                   public void onResponse(JSONArray response) {
                       try {
                           List<FoodItem> foodItems = new ArrayList<>(); // Danh sách để lưu các món ăn

                           for (int i = 0; i < response.length(); i++) {
                               JSONObject foodItemJson = response.getJSONObject(i);

                               // Lấy thông tin food_types
                               JSONArray foodTypesJsonArray = foodItemJson.getJSONArray("food_types");
                               List<FoodType> foodTypes = new ArrayList<>();

                               for (int j = 0; j < foodTypesJsonArray.length(); j++) {
                                   JSONObject foodTypeJson = foodTypesJsonArray.getJSONObject(j);
                                   FoodType foodType = new FoodType(
                                           foodTypeJson.getInt("id"),
                                           foodTypeJson.getString("typeName")
                                   );
                                   foodTypes.add(foodType);
                               }

                               // Lấy thông tin supplier_info
                               JSONObject supplierInfoJson = foodItemJson.getJSONObject("supplier_info");
                               SupplierInfo supplierInfo = new SupplierInfo(
                                       supplierInfoJson.getInt("id"),
                                       supplierInfoJson.getString("restaurantName"),
                                       supplierInfoJson.getString("imgUrl")
                               );
                               FoodItem foodItem = new FoodItem(
                                       foodItemJson.getInt("id"),
                                       foodItemJson.getString("food_name"),
                                       foodItemJson.getInt("quantity_sold"),
                                       foodItemJson.getDouble("price"),
                                       foodItemJson.getString("image_url"),
                                       supplierInfo,
                                       foodTypes
                               );

                               foodItems.add(foodItem);
                           }

                           // Gọi hàm để cập nhật RecyclerView
                           updateRecyclerView(foodItems);
                       } catch (Exception e) {
                           Log.e(TAG, "Error parsing JSON", e);
                       }
                   }
               },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Log.e(TAG, "Error fetching data", error);
                   }
               }
       ) {
           @Override
           public Map<String, String> getHeaders() {
               Map<String, String> headers = new HashMap<>();
               headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
               return headers;
           }
       };

       // Thêm yêu cầu vào hàng đợi
       Volley.newRequestQueue(this).add(jsonArrayRequest);
   }




    private void updateRecyclerView(List<FoodItem> foodItems) {
        foodItemAdapter = new FoodItemAdapter(foodItems,this );
        recyclerViewMenu.setAdapter(foodItemAdapter);
        foodItemAdapter.notifyDataSetChanged();
    }
}