package app.foodpt.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
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
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.FoodOrderItemAdapter;
import app.foodpt.exe201.DTO.FoodOrder;
import app.foodpt.exe201.DTO.FoodOrderItemResponse;
import app.foodpt.exe201.DTO.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
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
    private Button reOrderBtn;
    private int supplierId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_order_detail);
        View rootView = findViewById(R.id.root_view);
        // Thiết lập WindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                // Áp dụng padding để tránh bị thanh hệ thống che
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
                return insets.consumeSystemWindowInsets();
            }
        });
        // Ánh xạ các View
        tvPickupTime = findViewById(R.id.tvPickupTime);
        tvBookingID = findViewById(R.id.tvBookingID);
        tvPickupLocation = findViewById(R.id.tvPickupLocation);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rvFoodOrderItems = findViewById(R.id.rvFoodOrderItems);
        reOrderBtn = findViewById(R.id.reOrderBtn);
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
        reOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                Intent intent = new Intent(FoodOrderDetailActivity.this, OrderActivity.class);
                // Gửi danh sách `Parcelable` (FoodOrderItemResponse)
                // Map danh sách FoodOrderItemResponse sang Menu
                List<Menu> cartList = mapToMenuList(foodOrderItemList);
                intent.putExtra("cart_list", (Serializable) cartList);
                intent.putExtra("supplier_id",supplierId);
                startActivity(intent);
            }
        });

        // Khởi tạo danh sách món ăn và Adapter cho RecyclerView
        foodOrderItemList = new ArrayList<>();
        foodOrderItemAdapter = new FoodOrderItemAdapter(foodOrderItemList);
        rvFoodOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvFoodOrderItems.setAdapter(foodOrderItemAdapter);


        // Nhận đối tượng FoodOrder từ Intent
//        FoodOrder foodOrder = getIntent().getParcelableExtra("foodOrder");
        int foodOrderId = getIntent().getIntExtra("orderId",0);
        if (foodOrderId != 0) {
//            double totalPrice = foodOrder.getTotalPrice();
            // Gọi API get_food_order_by_id để lấy chi tiết đơn hàng
            fetchFoodOrderDetails(foodOrderId);
        }

    }

    // Hàm gọi API lấy chi tiết đơn hàng
    private void fetchFoodOrderDetails(int foodOrderId) {
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
                            JSONObject supplier = response.getJSONObject("supplier_info");
                            supplierId = supplier.getInt("id");
                            // Lấy thông tin chi tiết từ API và cập nhật UI
                            long pickupTime = response.getLong("pickup_time");
                            String pickupLocation = response.getString("pickup_location");
                            String paymentMethod = response.optString("payment_method", "Tiền mặt");
                            String totalPrice = response.getString("total_price");

                            JSONArray foodOrderItemsArray = response.getJSONArray("foodOrderItemResponseList");

                            // Cập nhật TextViews
                            tvPickupTime.setText(formatOrderTime(pickupTime));
                            tvBookingID.setText("#" +String.format("%,d", foodOrderId));
                            tvPickupLocation.setText(pickupLocation);
                            tvPaymentMethod.setText(paymentMethod);
                            tvTotalPrice.setText(totalPrice+"đ"); // Giả định giá trị tổng thanh toán (cập nhật lại từ API nếu có)

                            // Cập nhật danh sách món ăn trong RecyclerView
                            foodOrderItemList.clear();
                            for (int i = 0; i < foodOrderItemsArray.length(); i++) {
                                JSONObject item = foodOrderItemsArray.getJSONObject(i);
                                FoodOrderItemResponse foodOrderItem = new FoodOrderItemResponse(
                                        item.getInt("id"),
                                        item.getString("foodName"),
                                        item.getInt("quantity"),
                                        item.getDouble("price"),
                                        item.getInt("foodItemId"),
                                        supplierId
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

    public List<Menu> mapToMenuList(List<FoodOrderItemResponse> reOrderList) {
        List<Menu> cartList = new ArrayList<>();

        // Lặp qua từng phần tử trong reOrderList
        for (FoodOrderItemResponse item : reOrderList) {
            // Tạo đối tượng Menu từ FoodOrderItemResponse
            Menu menu = new Menu();
            menu.setId(item.getFoodItemId());
            menu.setName(item.getFoodName());
            menu.setQuantity(item.getQuantity());
            menu.setPrice(item.getPrice());
            menu.setSupplierId(item.getSupplierId());
            // Thêm đối tượng Menu vào cartList
            cartList.add(menu);
        }

        return cartList;
    }
}