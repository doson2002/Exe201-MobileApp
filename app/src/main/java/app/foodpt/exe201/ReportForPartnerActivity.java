package app.foodpt.exe201;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.TopFoodItemAdapter;
import app.foodpt.exe201.DTO.FoodItemReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportForPartnerActivity extends AppCompatActivity {

    private RadioGroup radioGroupReportType;
    private RadioButton radioByDay, radioByRange;
    private Button btnSelectDay, btnSelectStartDate, btnSelectEndDate, btnGenerateReport;
    private EditText etTotalOrderCount, etTotalRevenue;
    private RecyclerView rvTopSellingProducts;
    private TopFoodItemAdapter topFoodItemAdapter;
    private String selectedDay = "", startDate = "", endDate = "";
    // TextView để hiển thị ngày đã chọn
    private TextView tvSelectedDay, tvSelectedStartDate, tvSelectedEndDate,textViewTopSellingTitle ;
    private List<FoodItemReport> productList;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_for_partner);

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
        radioGroupReportType = findViewById(R.id.radioGroupReportType);
        radioByDay = findViewById(R.id.radioByDay);
        radioByRange = findViewById(R.id.radioByRange);
        btnSelectDay = findViewById(R.id.btnSelectDay);
        btnSelectStartDate = findViewById(R.id.btnSelectStartDate);
        btnSelectEndDate = findViewById(R.id.btnSelectEndDate);
        etTotalOrderCount = findViewById(R.id.etTotalOrderCount);
        etTotalRevenue = findViewById(R.id.etTotalRevenue);
        btnGenerateReport = findViewById(R.id.btnGenerateReport);
        rvTopSellingProducts =findViewById(R.id.rvTopSellingProducts);
        tvSelectedDay = findViewById(R.id.tvSelectedDay);
        tvSelectedStartDate = findViewById(R.id.tvSelectedStartDate);
        tvSelectedEndDate = findViewById(R.id.tvSelectedEndDate);
        textViewTopSellingTitle = findViewById(R.id.textViewTopSellingTitle);

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
        // Cài đặt RecyclerView
        rvTopSellingProducts.setLayoutManager(new LinearLayoutManager(this));
        topFoodItemAdapter = new TopFoodItemAdapter(new ArrayList<>(),ReportForPartnerActivity.this);
        rvTopSellingProducts.setAdapter(topFoodItemAdapter);
        // Hide day or range picker based on selection
        radioGroupReportType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioByDay) {
                findViewById(R.id.dayPickerContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.rangePickerContainer).setVisibility(View.GONE);
            } else if (checkedId == R.id.radioByRange) {
                findViewById(R.id.dayPickerContainer).setVisibility(View.GONE);
                findViewById(R.id.rangePickerContainer).setVisibility(View.VISIBLE);
            }
        });

        // Chọn ngày hoặc khoảng thời gian
        btnSelectDay.setOnClickListener(v -> showDatePicker(date -> {
            selectedDay = date;
            tvSelectedDay.setText("Selected Day: " + selectedDay);
        }));

        btnSelectStartDate.setOnClickListener(v -> showDatePicker(date -> {
            startDate = date;
            tvSelectedStartDate.setText("Start Date: " + startDate);
        }));

        btnSelectEndDate.setOnClickListener(v -> showDatePicker(date -> {
            endDate = date;
            tvSelectedEndDate.setText("End Date: " + endDate);
        }));

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int supplierId = sharedPreferences.getInt("supplier_id", 0);
        // Generate report
        btnGenerateReport.setOnClickListener(v -> {
            if (radioByDay.isChecked()) {
                if (!selectedDay.isEmpty()) {
                    callReportByDayApi( selectedDay, supplierId);
                } else {
                    Toast.makeText(ReportForPartnerActivity.this, "Please select a day", Toast.LENGTH_SHORT).show();
                }
            } else if (radioByRange.isChecked()) {
                if (!startDate.isEmpty() && !endDate.isEmpty()) {
                    callReportByRangeApi( startDate, endDate, supplierId);
                } else {
                    Toast.makeText(ReportForPartnerActivity.this, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    // Show DatePickerDialog
    private void showDatePicker(DatePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ReportForPartnerActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    callback.onDateSelected(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Interface for DatePicker callback
    interface DatePickerCallback {
        void onDateSelected(String date);
    }

    private void callReportByDayApi(String day, int supplierId) {
        String url = ApiEndpoints.GET_REPORT_BY_DATE + "date="+ day + "&supplierInfoId=" + supplierId;
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Xử lý response JSON
                    try {
                        int totalOrderCount = response.getInt("totalOrderCount");
                        double totalRevenue = response.getDouble("totalRevenue");

                        etTotalOrderCount.setVisibility(View.VISIBLE);
                        etTotalOrderCount.setText("Tổng Số Đơn Hàng: "+String.format("%,d", totalOrderCount)); // Đây là cách đúng
                        etTotalRevenue.setVisibility(View.VISIBLE);
                        etTotalRevenue.setText("Tổng Doanh Thu: "+String.format("%,.0fđ",totalRevenue));

                        // Xử lý danh sách sản phẩm
                        JSONArray productsArray = response.getJSONArray("topSellingProducts");
                         productList = new ArrayList<>();

                        for (int i = 0; i < productsArray.length(); i++) {
                            JSONObject productObj = productsArray.getJSONObject(i);
                            int id = productObj.getInt("id");
                            String foodName = productObj.getString("food_name");
                            String imageUrl = productObj.getString("image_url");
                            int quantitySold = productObj.getInt("quantity_sold");
                            productList.add(new FoodItemReport(id, imageUrl, quantitySold, foodName));
                        }

                        // Kiểm tra xem danh sách có dữ liệu hay không
                        if (productList != null && !productList.isEmpty()) {
                            // Thiết lập Adapter cho RecyclerView
                            rvTopSellingProducts.setAdapter(new TopFoodItemAdapter(productList,ReportForPartnerActivity.this));

                            // Hiển thị tiêu đề
                            textViewTopSellingTitle.setVisibility(View.VISIBLE);
                        } else {
                            // Ẩn tiêu đề nếu không có sản phẩm nào
                            textViewTopSellingTitle.setVisibility(View.GONE);
                        }
                        topFoodItemAdapter.updateFoodItemList(productList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
    private void callReportByRangeApi(String startDate, String endDate, int supplierId) {
        String url = ApiEndpoints.GET_REPORT_BY_DATE_RANGE+ "startDate=" + startDate + "&endDate=" + endDate + "&supplierInfoId=" + supplierId;
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Xử lý response JSON
                    try {
                        long totalOrderCount = response.getLong("totalOrderCount");
                        double totalRevenue = response.getDouble("totalRevenue");
                        etTotalOrderCount.setVisibility(View.VISIBLE);
                        etTotalOrderCount.setText("Số Lượng Đơn Hàng: "+String.valueOf(totalOrderCount));
                        etTotalRevenue.setVisibility(View.VISIBLE);
                        etTotalRevenue.setText("Tổng Doanh Thu: "+String.valueOf(totalRevenue) +"đ");

                        // Xử lý danh sách sản phẩm
                        JSONArray productsArray = response.getJSONArray("topSellingProducts");
                         productList = new ArrayList<>();

                        for (int i = 0; i < productsArray.length(); i++) {
                            JSONObject productObj = productsArray.getJSONObject(i);
                            int id = productObj.getInt("id");
                            String foodName = productObj.getString("food_name");
                            String imageUrl = productObj.getString("image_url");
                            int quantitySold = productObj.getInt("quantity_sold");
                            productList.add(new FoodItemReport(id, imageUrl, quantitySold, foodName));
                        }

                        // Kiểm tra xem danh sách có dữ liệu hay không
                        if (productList != null && !productList.isEmpty()) {
                            // Thiết lập Adapter cho RecyclerView
                            rvTopSellingProducts.setAdapter(new TopFoodItemAdapter(productList,ReportForPartnerActivity.this));

                            // Hiển thị tiêu đề
                            textViewTopSellingTitle.setVisibility(View.VISIBLE);
                        } else {
                            // Ẩn tiêu đề nếu không có sản phẩm nào
                            textViewTopSellingTitle.setVisibility(View.GONE);
                        }
                        topFoodItemAdapter.updateFoodItemList(productList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


}