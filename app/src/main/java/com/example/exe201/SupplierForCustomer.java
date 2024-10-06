package com.example.exe201;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.SupplierInfoAdapter;
import com.example.exe201.Adapter.SupplierTypeAdapter;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.DTO.SupplierType;
import com.example.exe201.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierForCustomer extends AppCompatActivity {

    private RecyclerView recyclerViewSupplierTypes;
    private RecyclerView recyclerViewSuppliers;
    private ImageView backArrow;
    private SupplierTypeAdapter supplierTypeAdapter;
    private SupplierInfoAdapter supplierInfoAdapter;
    private List<SupplierType> supplierTypeList = new ArrayList<>();
    private List<SupplierInfo> supplierInfoList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_for_customer);

        int supplierTypeId = getIntent().getIntExtra("supplierTypeId", 0);
        loadSuppliersBySupplierTypeId(supplierTypeId);
//
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
        recyclerViewSupplierTypes = findViewById(R.id.recyclerSupplierTypes);
        recyclerViewSuppliers = findViewById(R.id.recyclerViewSupplier);

        // Cài đặt Layout Manager cho RecyclerView
        LinearLayoutManager supplierTypeLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSupplierTypes.setLayoutManager(supplierTypeLayoutManager);

        // Truyền listener cho FoodTypeAdapter
        supplierTypeAdapter = new SupplierTypeAdapter(supplierTypeList, this, new SupplierTypeAdapter.OnSupplierTypeClickListener() {
            @Override
            public void onSupplierTypeClick(SupplierType supplierType) {
                // Lấy foodTypeId được chọn và gọi phương thức loadFoodItemsByFoodTypeId
                int supplierTypeId = supplierType.getId(); // Lấy ID của FoodType được chọn
                loadSuppliersBySupplierTypeId(supplierTypeId); // Gọi hàm để load danh sách món ăn theo foodTypeId
            }
        });
        recyclerViewSupplierTypes.setAdapter(supplierTypeAdapter);


        LinearLayoutManager supplierInfoLayoutManager = new LinearLayoutManager(this);
        recyclerViewSuppliers.setLayoutManager(supplierInfoLayoutManager);

        supplierInfoAdapter  = new SupplierInfoAdapter(supplierInfoList, this, new SupplierInfoAdapter.OnSupplierInfoClickListener(){
            @Override
            public void onSupplierInfoClick(SupplierInfo supplierInfo) {
                // Lưu supplierInfo vào SharedPreferences
                Utils.saveSupplierInfo(SupplierForCustomer.this, supplierInfo);
                Intent intent = new Intent(SupplierForCustomer.this, ShowFoodItemActivity.class);
                intent.putExtra("supplier",supplierInfo); // Truyền Supplier qua Intent
                startActivity(intent);
            }
        });


        recyclerViewSuppliers.setAdapter(supplierInfoAdapter);

        // Load FoodType từ API
        loadAllSupplierTypes();
    }


    private void loadAllSupplierTypes() {
        String url = ApiEndpoints.GET_ALL_SUPPLIER_TYPES; // Thay thế bằng URL API của bạn

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            supplierTypeList.clear(); // Xóa dữ liệu cũ trong danh sách mà Adapter đang sử dụng                            for (int i = 0; i < response.length(); i++) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject foodTypeJson = response.getJSONObject(i);
                                int id = foodTypeJson.getInt("id");
                                String typeName = foodTypeJson.getString("typeName");
                                String imgUrl = foodTypeJson.getString("imgUrl");
                                supplierTypeList.add(new SupplierType(id, typeName, imgUrl)); // Thêm trực tiếp vào foodTypeList
                            }
                            supplierTypeAdapter.notifyDataSetChanged(); // Thông báo cho Adapter rằng dữ liệu đã thay đổi
                            // Cập nhật dữ liệu cho spinner
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SupplierForCustomer.this, "Error parsing supplier types data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Log.e("VolleyError", "Timeout Error: " + error.toString());
                        } else if (error instanceof NoConnectionError) {
                            Log.e("VolleyError", "No Connection Error: " + error.toString());
                        } else if (error instanceof AuthFailureError) {
                            Log.e("VolleyError", "Authentication Failure Error: " + error.toString());
                        } else if (error instanceof ServerError) {
                            Log.e("VolleyError", "Server Error: " + error.toString());
                        } else if (error instanceof NetworkError) {
                            Log.e("VolleyError", "Network Error: " + error.toString());
                        } else if (error instanceof ParseError) {
                            Log.e("VolleyError", "Parse Error: " + error.toString());
                        } else {
                            Log.e("VolleyError", "Unknown Error: " + error.toString());
                        }
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

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000, // Thời gian chờ tối đa (tính bằng milliseconds)
                1, // Số lần thử lại
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void loadSuppliersBySupplierTypeId(int supplierTypeId) {
        String url = ApiEndpoints.GET_SUPPLIER_BY_SUPPLIER_TYPE_ID + "/" + supplierTypeId; // Thay thế URL bằng API của bạn

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            supplierInfoList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject foodItemJson = response.getJSONObject(i);
                                int id = foodItemJson.getInt("id");
                                String restaurantName = foodItemJson.getString("restaurant_name");
                                String imageUrl = foodItemJson.getString("img_url");
                                double totalStarRating = foodItemJson.getDouble("total_star_rating");
                                int totalReviewCount = foodItemJson.getInt("total_review_count");
                                // Lấy thông tin SupplierInfo
                                JSONObject supplierTypeJson = foodItemJson.getJSONObject("supplier_type");
                                int supplierTypeId = supplierTypeJson.getInt("id");
                                String typeName = supplierTypeJson.getString("typeName");
                                String imgUrl = supplierTypeJson.getString("imgUrl");


                                SupplierType supplierType= new SupplierType(supplierTypeId, typeName, imgUrl);

                                // Thêm SupplierInfo vào danh sách
                                supplierInfoList.add(new SupplierInfo(id, restaurantName,  imageUrl, totalStarRating, totalReviewCount, supplierType));
                            }

                            // Cập nhật danh sách món ăn trong RecyclerView
                            supplierInfoAdapter.updateSupplierInfoList(supplierInfoList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SupplierForCustomer.this, "Error parsing suppliers data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching food items", error);
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

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Áp dụng hoạt ảnh khi quay lại trang trước
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }


}