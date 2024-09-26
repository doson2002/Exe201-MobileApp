package com.example.exe201;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PartnerHomePageActivity extends AppCompatActivity {

    private TextView openTimeTextView, closeTimeTextView;
    private LocalTime openTime, closeTime;
    private LinearLayout timeIcon, reportIcon,menuIcon;
    private ImageView accountIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_partner_home_page);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);
        fetchSupplierByUserId(userId);
        accountIcon = findViewById(R.id.account_icon);
        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartnerHomePageActivity.this, ProfileActivity.class); // Thay bằng activity của bạn
                startActivity(intent);
            }
        });

        menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartnerHomePageActivity.this, FoodItemActivity.class); // Thay bằng activity của bạn
                startActivity(intent);
            }
        });
        reportIcon = findViewById(R.id.reportIcon);
        reportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartnerHomePageActivity.this, ReportForPartnerActivity.class); // Thay bằng activity của bạn
                startActivity(intent);
            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        View timePickerView = inflater.inflate(R.layout.activity_time_picker, null);
        Button saveButton = timePickerView.findViewById(R.id.save_button_time_picker);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int supplierId = sharedPreferences.getInt("supplier_id", 0);

                // Lấy thông tin giờ mở và đóng cửa từ các TextView hoặc EditText
                String openTime = openTimeTextView.getText().toString();
                String closeTime = closeTimeTextView.getText().toString();

                // Kiểm tra nếu người dùng chưa chọn giờ mở và đóng cửa
                if (openTime.isEmpty() || closeTime.isEmpty()) {
                    Toast.makeText(PartnerHomePageActivity.this, "Vui lòng chọn giờ mở cửa và đóng cửa", Toast.LENGTH_SHORT).show();
                    return; // Không thực hiện cập nhật nếu chưa chọn đủ thông tin
                }
                updateRestaurantTime(supplierId, openTime, closeTime);
            }
        });
        // Ánh xạ ImageView
        timeIcon = findViewById(R.id.time_icon); // Sử dụng ID thực tế của ImageView trong layout
        // Thiết lập sự kiện nhấn cho ImageView
        timeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(); // Gọi hàm hiển thị Dialog trực tiếp từ PartnerActivity
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_partner);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Handle Home action
                        return true;
                    case R.id.nav_promotion:
                        // Handle Search action
                        return true;
                    case R.id.nav_account:
                        // Handle Notifications action
                        return true;

                }
                return false;
            }
        });
    }

    // Hàm hiển thị Dialog chỉnh sửa thời gian
    private void showTimePickerDialog() {
        // Tạo Dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_time_picker); // Gán layout của dialog

        // Ánh xạ các thành phần trong layout của dialog
        Button openTimeButton = dialog.findViewById(R.id.open_time_button);
        Button closeTimeButton = dialog.findViewById(R.id.close_time_button);
        Button saveButton = dialog.findViewById(R.id.save_button_time_picker);
        ImageView closeButton = dialog.findViewById(R.id.close_button);
        TextView openTimeTextView = dialog.findViewById(R.id.open_time_text_view); // Thêm TextView hiển thị giờ mở cửa
        TextView closeTimeTextView = dialog.findViewById(R.id.close_time_text_view); // Thêm TextView hiển thị giờ đóng cửa

        // Thiết lập sự kiện cho các nút trong dialog
        openTimeButton.setOnClickListener(v -> showTimePicker(true, openTimeTextView));
        closeTimeButton.setOnClickListener(v -> showTimePicker(false, closeTimeTextView));
        closeButton.setOnClickListener(v -> dialog.dismiss()); // Đóng dialog khi nhấn nút X

        saveButton.setOnClickListener(v -> {
            // Lấy thông tin giờ mở và đóng cửa từ các TextView hoặc EditText
            String openTime = openTimeTextView.getText().toString();
            String closeTime = closeTimeTextView.getText().toString();
            if (openTime.isEmpty() || closeTime.isEmpty()) {
                Toast.makeText(PartnerHomePageActivity.this, "Vui lòng chọn giờ mở cửa và đóng cửa", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PartnerHomePageActivity.this, "Giờ mở cửa: " + openTimeTextView.getText() + "\nGiờ đóng cửa: " + closeTimeTextView.getText(), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                int supplierId = sharedPreferences.getInt("supplier_id", 0);
                // Kiểm tra nếu người dùng chưa chọn giờ mở và đóng cửa
                updateRestaurantTime(supplierId, openTime, closeTime);
                dialog.dismiss(); // Đóng dialog sau khi lưu
            }
        });

        // Thiết lập kích thước và vị trí của dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER; // Hiển thị giữa màn hình
            params.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt chiều rộng tự động điều chỉnh
            params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Đặt chiều cao tự động điều chỉnh
            window.setAttributes(params);
        }

        dialog.show();
    }

    // Hiển thị TimePickerDialog
    private void showTimePicker(boolean isOpenTime, TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                PartnerHomePageActivity.this,
                (view, selectedHour, selectedMinute) -> {
                    timeTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                },
                hour, minute, true);

        timePickerDialog.show();
    }
    private void fetchSupplierByUserId(int userId) {

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.GET_SUPPLIER_BY_USER_ID+"/" + userId; // Thay thế bằng URL API của bạn
        RequestQueue queue = Volley.newRequestQueue(PartnerHomePageActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy supplierId từ phản hồi
                            int supplierId = response.getInt("id"); // Lấy id của restaurant
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("supplier_id", supplierId);
                            editor.apply();
                            // Sau khi lấy được supplierId, gọi hàm getFoodItemBySupplierId


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PartnerHomePageActivity.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi khi gọi API
                Toast.makeText(PartnerHomePageActivity.this, "Failed to fetch supplier data", Toast.LENGTH_SHORT).show();
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

    private void updateRestaurantTime(int supplierId, String openTime, String closeTime) {
        // URL của API
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        String url = ApiEndpoints.UPDATE_OPEN_CLOSE_TIME +"/" +supplierId; // Thay thế bằng URL thực tế của bạn


        // Tạo đối tượng JSON để gửi yêu cầu
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("open_time", openTime); // Thời gian mở cửa
            jsonRequest.put("close_time", closeTime); // Thời gian đóng cửa
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Tạo yêu cầu PUT với thư viện Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý phản hồi khi thành công
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(PartnerHomePageActivity.this, "Status: " + status + "\nMessage: " + message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi khi gọi API

                        Toast.makeText(PartnerHomePageActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Thêm header nếu cần thiết, ví dụ như Authorization
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + jwtToken); // Thay thế bằng JWT Token thực tế của bạn
                return headers;
            }
        };

        // Thêm yêu cầu vào hàng đợi của Volley
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}