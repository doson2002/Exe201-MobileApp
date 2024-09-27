package com.example.exe201.Fragment.Partner;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.SupplierTypeAdapter;
import com.example.exe201.DTO.SupplierType;
import com.example.exe201.FoodItemActivity;
import com.example.exe201.FoodItemGroupedBySupplierActivity;
import com.example.exe201.ProfileActivity;
import com.example.exe201.R;
import com.example.exe201.ReportForPartnerActivity;
import com.example.exe201.UpdateAddressActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PartnerHomeFragment extends Fragment {
    private TextView openTimeTextView, closeTimeTextView;
    private LocalTime openTime, closeTime;
    private LinearLayout timeIcon, reportIcon, menuIcon, addressIcon;
    private ImageView accountIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_partner_home_page, container, false);

        // Enable edge-to-edge (if necessary, or move it to the activity)
        EdgeToEdge.enable(getActivity());

        // Initialize shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);

        // Fetch supplier data
        fetchSupplierByUserId(userId);

        // Bind views
        accountIcon = view.findViewById(R.id.account_icon);
        menuIcon = view.findViewById(R.id.menu_icon);
        reportIcon = view.findViewById(R.id.reportIcon);
        addressIcon = view.findViewById(R.id.address_icon);
        timeIcon = view.findViewById(R.id.time_icon);

        // Account icon click listener
        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Menu icon click listener
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FoodItemActivity.class);
                startActivity(intent);
            }
        });

        // Report icon click listener
        reportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportForPartnerActivity.class);
                startActivity(intent);
            }
        });

        // Address icon click listener
        addressIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateAddressActivity.class);
                startActivity(intent);
            }
        });

        // Time picker click listener
        timeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Setup save button in time picker
        LayoutInflater dialogInflater = LayoutInflater.from(getActivity());
        View timePickerView = dialogInflater.inflate(R.layout.activity_time_picker, null);
        Button saveButton = timePickerView.findViewById(R.id.save_button_time_picker);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int supplierId = sharedPreferences.getInt("supplier_id", 0);
                String openTime = openTimeTextView.getText().toString();
                String closeTime = closeTimeTextView.getText().toString();

                if (openTime.isEmpty() || closeTime.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng chọn giờ mở cửa và đóng cửa", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateRestaurantTime(supplierId, openTime, closeTime);
            }
        });

        return view;
    }

    // Hàm hiển thị Dialog chỉnh sửa thời gian
    private void showTimePickerDialog() {
        // Tạo Dialog
        Dialog dialog = new Dialog(getActivity());
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
                Toast.makeText(getActivity(), "Vui lòng chọn giờ mở cửa và đóng cửa", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Giờ mở cửa: " + openTimeTextView.getText() + "\nGiờ đóng cửa: " + closeTimeTextView.getText(), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
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
                getActivity(),
                (view, selectedHour, selectedMinute) -> {
                    timeTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                },
                hour, minute, true);

        timePickerDialog.show();
    }
    private void fetchSupplierByUserId(int userId) {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.GET_SUPPLIER_BY_USER_ID+"/" + userId; // Thay thế bằng URL API của bạn
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy supplierId từ phản hồi
                            int supplierId = response.getInt("id"); // Lấy id của restaurant
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("supplier_id", supplierId);
                            editor.apply();
                            // Sau khi lấy được supplierId, gọi hàm getFoodItemBySupplierId


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi khi gọi API
                Toast.makeText(getActivity(), "Failed to fetch supplier data", Toast.LENGTH_SHORT).show();
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
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
                            Toast.makeText(getActivity(), "Status: " + status + "\nMessage: " + message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi khi gọi API

                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
    }
}