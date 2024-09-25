package com.example.exe201;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.FoodOrderItemAdapter;
import com.example.exe201.DTO.FoodOrder;
import com.example.exe201.DTO.FoodOrderItemResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodOrderDetailActivity extends AppCompatActivity {

    private TextView tvPickupTime, tvBookingID, tvPickupLocation, tvPaymentMethod, tvTotalPrice;
    private RecyclerView rvFoodOrderItems;
    private FoodOrderItemAdapter foodOrderItemAdapter;
    private List<FoodOrderItemResponse> foodOrderItemList;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_order_detail);
        // Ánh xạ các View
        tvPickupTime = findViewById(R.id.tvPickupTime);
        tvBookingID = findViewById(R.id.tvBookingID);
        tvPickupLocation = findViewById(R.id.tvPickupLocation);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rvFoodOrderItems = findViewById(R.id.rvFoodOrderItems);

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });

        // Khởi tạo danh sách món ăn và Adapter cho RecyclerView
        foodOrderItemList = new ArrayList<>();
        foodOrderItemAdapter = new FoodOrderItemAdapter(foodOrderItemList);
        rvFoodOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvFoodOrderItems.setAdapter(foodOrderItemAdapter);


        // Nhận đối tượng FoodOrder từ Intent
        FoodOrder foodOrder = getIntent().getParcelableExtra("foodOrder");

        if (foodOrder != null) {
            // Lấy FoodOrderId từ đối tượng FoodOrder
            int foodOrderId = foodOrder.getId();
            double totalPrice = foodOrder.getTotalPrice();

            // Gọi API get_food_order_by_id để lấy chi tiết đơn hàng
            fetchFoodOrderDetails(foodOrderId, totalPrice);
        }
        // Nhận đối tượng FoodOrder từ Intent


    }

    // Hàm gọi API lấy chi tiết đơn hàng
    private void fetchFoodOrderDetails(int foodOrderId, double totalPrice) {
        String url = ApiEndpoints.GET_ORDER_DETAIL + "/" + foodOrderId;
        // Call the API to fetch orders
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        // Tạo request để lấy dữ liệu từ API
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy thông tin chi tiết từ API và cập nhật UI
                            long pickupTime = response.getLong("pickup_time");
                            String pickupLocation = response.getString("pickup_location");
                            String paymentMethod = response.optString("payment_method", "Tiền mặt");
                            JSONArray foodOrderItemsArray = response.getJSONArray("foodOrderItemResponseList");

                            // Cập nhật TextViews
                            tvPickupTime.setText(formatOrderTime(pickupTime));
                            tvBookingID.setText("#" +String.format("%,d", foodOrderId));
                            tvPickupLocation.setText(pickupLocation);
                            tvPaymentMethod.setText(paymentMethod);
                            tvTotalPrice.setText(String.format("%,.0fđ", totalPrice)); // Giả định giá trị tổng thanh toán (cập nhật lại từ API nếu có)

                            // Cập nhật danh sách món ăn trong RecyclerView
                            foodOrderItemList.clear();
                            for (int i = 0; i < foodOrderItemsArray.length(); i++) {
                                JSONObject item = foodOrderItemsArray.getJSONObject(i);
                                FoodOrderItemResponse foodOrderItem = new FoodOrderItemResponse(
                                        item.getInt("id"),
                                        item.getString("foodName"),
                                        item.getInt("quantity")
                                );
                                foodOrderItemList.add(foodOrderItem);
                            }
                            foodOrderItemAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (jwtToken != null) {
                    headers.put("Authorization", "Bearer " + jwtToken);
                }
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Thêm request vào hàng đợi
        queue.add(jsonObjectRequest);
    }

    // Hàm định dạng thời gian từ timestamp
    private String formatOrderTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}