package app.foodpt.exe201;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.OrderForPartnerAdapter;
import app.foodpt.exe201.DTO.FoodOrder;

public class OrderForPartnerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewList;
    private ImageView back_arrow;
    private  RequestQueue requestQueue;
    private OrderForPartnerAdapter orderAdapter;
    private List<FoodOrder> orderList;
    private String status = "";
    private int currentPage = 0;
    private int totalPages = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_for_partner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);
        recyclerViewList = findViewById(R.id.recyclerViewList);
        recyclerViewList.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderForPartnerAdapter(this,orderList);
        recyclerViewList.setAdapter(orderAdapter);

        requestQueue= Volley.newRequestQueue(this);
        fetchOrders(supplierId, token, status);

        recyclerViewList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) { // 1 để kiểm tra cuộn xuống
                    if (currentPage < totalPages) {
                        fetchOrders(supplierId, token, status); // Tải trang tiếp theo
                    }
                }
            }
        });
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView filterIcon = findViewById(R.id.filter_icon);
        filterIcon.setOnClickListener(v -> {
            // Tạo PopupWindow từ layout tùy chỉnh
            View popupView = LayoutInflater.from(v.getContext()).inflate(R.layout.popup_menu, null);
            PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

            // Thiết lập các thuộc tính cho PopupWindow
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Nếu bạn muốn viền bo góc
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);


            // Hiển thị PopupWindow với khoảng cách từ bên phải
            int xOffset = 20; // Số pixel tạo khoảng cách từ bên phải
            int yOffset = 0; // Khoảng cách theo chiều dọc (có thể điều chỉnh nếu cần)

            // Lưu ý: Sử dụng v.getWidth() để xác định vị trí
            popupWindow.showAsDropDown(v, -v.getWidth() + popupWindow.getWidth() + xOffset, yOffset);


            // Thiết lập sự kiện click cho các TextView trong popup
            popupView.findViewById(R.id.filter_by_status).setOnClickListener(view -> {
                showStatusFilterPopup(v, token, supplierId);
                popupWindow.dismiss(); // Đóng popup sau khi chọn
            });

            popupView.findViewById(R.id.filter_by_date).setOnClickListener(view -> {
                // Xử lý khi chọn filter theo ngày (tuỳ chỉnh thêm nếu cần)
                // Ví dụ: showDatePickerDialog();
                popupWindow.dismiss(); // Đóng popup sau khi chọn
            });
        });


    }
    // Hàm để hiển thị PopupMenu chọn trạng thái
    private void showStatusFilterPopup(View anchorView, String token, int supplierId) {
        // Tạo PopupWindow từ layout đã định nghĩa
        View popupView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.popup_order_status, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        // Thiết lập các thuộc tính cho PopupWindow
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Nếu bạn muốn viền bo góc
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Hiển thị PopupWindow
        popupWindow.showAsDropDown(anchorView);

        // Xử lý sự kiện click cho các TextView trong popup
        popupView.findViewById(R.id.tvStatusInProgress).setOnClickListener(v -> {
            status = "đang giao";
            currentPage = 0;
            orderList.clear();
            fetchOrders(supplierId, token, status);
            popupWindow.dismiss(); // Đóng popup sau khi chọn
        });

        popupView.findViewById(R.id.tvStatusCompleted).setOnClickListener(v -> {
            status = "hoàn thành";
            currentPage = 0;
            orderList.clear();
            fetchOrders(supplierId, token, status);
            popupWindow.dismiss(); // Đóng popup sau khi chọn
        });

        popupView.findViewById(R.id.tvStatusFailed).setOnClickListener(v -> {
            status = "thất bại";
            currentPage = 0;
            orderList.clear();
            fetchOrders(supplierId, token, status);
            popupWindow.dismiss(); // Đóng popup sau khi chọn
        });


    }
    private void fetchOrders(int supplierId, String token, String status) {
        String url = ApiEndpoints.GET_ORDER_BY_SUPPLIER_ID_AND_STATUS + "/" + supplierId +
                "?page=" + currentPage + "&size=10&sortDirection=desc&status=" + status;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Xóa danh sách cũ trước khi thêm dữ liệu mới
                            JSONArray contentArray = response.getJSONArray("content");
                            for (int i = 0; i < contentArray.length(); i++) {
                                JSONObject orderObject = contentArray.getJSONObject(i);

                                FoodOrder order = new FoodOrder();
                                order.setId(orderObject.getInt("id"));
                                // Chuyển đổi order_time từ long sang String
                                long orderTimeLong = orderObject.getLong("order_time");
                                String formattedOrderTime = formatTimestampToDateString(orderTimeLong);
                                order.setOrderTime(formattedOrderTime);
                                order.setCustomerName(orderObject.getString("customer_name"));
                                order.setOrderStatus(orderObject.getString("status"));
                                order.setTotalPrice(orderObject.getDouble("total_price"));
                                order.setShippingFee(orderObject.getDouble("shipping_fee"));
                                order.setDiscount(orderObject.getDouble("discount"));
                                order.setPickupLocation(orderObject.getString("pickup_location"));

                                orderList.add(order);
                                // Cập nhật thông tin phân trang
                                currentPage = response.getJSONObject("pageable").getInt("pageNumber") + 1;
                                totalPages = response.getInt("totalPages");
                            }
                            orderAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(OrderForPartnerActivity.this, "Opssssss! Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderForPartnerActivity.this, "Opssssss! Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        }){
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }
    // Hàm chuyển đổi timestamp sang định dạng ngày giờ
    private String formatTimestampToDateString(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}