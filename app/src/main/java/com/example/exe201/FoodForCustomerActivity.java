//package com.example.exe201;
//
//import static android.content.ContentValues.TAG;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.exe201.API.ApiEndpoints;
//import com.example.exe201.Adapter.FoodItemAdapter;
//import com.example.exe201.Adapter.FoodItemCustomerAdapter;
//import com.example.exe201.Adapter.FoodTypeAdapter;
//import com.example.exe201.DTO.FoodItem;
//import com.example.exe201.DTO.FoodType;
//import com.example.exe201.DTO.SupplierInfo;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class FoodForCustomerActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerViewFoodTypes;
//    private RecyclerView recyclerViewFoodItems;
//    private FoodTypeAdapter foodTypeAdapter;
//    private FoodItemCustomerAdapter foodItemCustomerAdapter;
//    private List<FoodType> foodTypeList = new ArrayList<>();
//    private List<FoodItem> foodItemList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_food_for_customer);
//
//        recyclerViewFoodTypes = findViewById(R.id.recyclerViewFoodTypes);
//        recyclerViewFoodItems = findViewById(R.id.recyclerViewFoodItems);
//
//        // Cài đặt Layout Manager cho RecyclerView
//        LinearLayoutManager foodTypeLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerViewFoodTypes.setLayoutManager(foodTypeLayoutManager);
//
//        // Truyền listener cho FoodTypeAdapter
//        foodTypeAdapter = new FoodTypeAdapter(foodTypeList, this, new FoodTypeAdapter.OnFoodTypeClickListener() {
//            @Override
//            public void onFoodTypeClick(FoodType foodType) {
//                // Lấy foodTypeId được chọn và gọi phương thức loadFoodItemsByFoodTypeId
//                int foodTypeId = foodType.getId(); // Lấy ID của FoodType được chọn
//                loadFoodItemsByFoodTypeId(foodTypeId); // Gọi hàm để load danh sách món ăn theo foodTypeId
//            }
//        });
//        recyclerViewFoodTypes.setAdapter(foodTypeAdapter);
//
//
//        LinearLayoutManager foodItemLayoutManager = new LinearLayoutManager(this);
//        recyclerViewFoodItems.setLayoutManager(foodItemLayoutManager);
//
//        foodItemCustomerAdapter  = new FoodItemCustomerAdapter(foodItemList, this);
//        recyclerViewFoodItems.setAdapter(foodItemCustomerAdapter);
//
//        // Load FoodType từ API
//        loadAllFoodTypes();
//    }
//
//    private void loadAllFoodTypes() {
//        String url = ApiEndpoints.GET_ALL_FOOD_TYPES; // Thay thế bằng URL API của bạn
//
//        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        String jwtToken = sharedPreferences.getString("JwtToken", null);
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET, url, null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                                foodTypeList.clear(); // Xóa dữ liệu cũ trong danh sách mà Adapter đang sử dụng                            for (int i = 0; i < response.length(); i++) {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject foodTypeJson = response.getJSONObject(i);
//                                int id = foodTypeJson.getInt("id");
//                                String typeName = foodTypeJson.getString("typeName");
//                                String imgUrl = foodTypeJson.getString("imgUrl");
//                                foodTypeList.add(new FoodType(id, typeName, imgUrl)); // Thêm trực tiếp vào foodTypeList
//                            }
//                            foodTypeAdapter.notifyDataSetChanged(); // Thông báo cho Adapter rằng dữ liệu đã thay đổi
//                            // Cập nhật dữ liệu cho spinner
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(FoodForCustomerActivity.this, "Error parsing food types data", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "Error fetching food types", error);
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
//                return headers;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(jsonArrayRequest);
//    }
//
//    private void loadFoodItemsByFoodTypeId(int foodTypeId) {
//        String url = ApiEndpoints.GET_FOOD_ITEM_BY_SUPPLIER_ID + "/" + foodTypeId; // Thay thế URL bằng API của bạn
//
//        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        String jwtToken = sharedPreferences.getString("JwtToken", null);
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET, url, null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                            foodItemList.clear();
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject foodItemJson = response.getJSONObject(i);
//                                int id = foodItemJson.getInt("id");
//                                String foodName = foodItemJson.getString("food_name");
//                                String imageUrl = foodItemJson.getString("image_url");
//                                double price = foodItemJson.getDouble("price");
//                                // Lấy thông tin SupplierInfo
//                                JSONObject supplierJson = foodItemJson.getJSONObject("supplier_info");
//                                int supplierId = supplierJson.getInt("id");
//                                String restaurantName = supplierJson.getString("restaurantName");
//                                String imgUrl = supplierJson.getString("imgUrl");
//
//
//                                SupplierInfo supplierInfo = new SupplierInfo(supplierId, restaurantName, imgUrl);
//
//                                // Thêm FoodItem vào danh sách
//                                foodItemList.add(new FoodItem(id, foodName,  price, imageUrl, supplierInfo));
//                            }
//
//                            // Cập nhật danh sách món ăn trong RecyclerView
//                            foodItemCustomerAdapter.updateFoodItemList(foodItemList);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(FoodForCustomerActivity.this, "Error parsing food items data", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "Error fetching food items", error);
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
//                return headers;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(jsonArrayRequest);
//    }
//
//
//}