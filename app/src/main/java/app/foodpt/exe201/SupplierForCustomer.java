package app.foodpt.exe201;

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
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.SupplierInfoAdapter;
import app.foodpt.exe201.Adapter.SupplierTypeAdapter;
import app.foodpt.exe201.DTO.SupplierInfo;
import app.foodpt.exe201.DTO.SupplierType;
import app.foodpt.exe201.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SupplierForCustomer extends AppCompatActivity {

    private RecyclerView recyclerViewSupplierTypes;
    private RecyclerView recyclerViewSuppliers;
    private ImageView backArrow;
    private SupplierTypeAdapter supplierTypeAdapter;
    private SupplierInfoAdapter supplierInfoAdapter;
    private List<SupplierType> supplierTypeList = new ArrayList<>();
    private List<SupplierInfo> supplierInfoList = new ArrayList<>();
    private double distance = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_for_customer);
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
        String url = ApiEndpoints.GET_ALL_SUPPLIER_TYPES + "/" + 1; // Thay thế bằng URL API của bạn

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
        // Lấy latitude và longitude của người dùng từ SharedPreferences
        double userLatitude = sharedPreferences.getFloat("latitude", 0.0f);
        double userLongitude = sharedPreferences.getFloat("longitude", 0.0f);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            supplierInfoList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject supplierJson = response.getJSONObject(i);
                                int id = supplierJson.getInt("id");
                                String restaurantName = supplierJson.getString("restaurant_name");
                                String imageUrl = supplierJson.getString("img_url");
                                double totalStarRating = supplierJson.getDouble("total_star_rating");
                                int totalReviewCount = supplierJson.getInt("total_review_count");
                                double latitude = supplierJson.getDouble("latitude");
                                double longitude = supplierJson.getDouble("longitude");
                                // Lấy close_time và open_time từ JSON
                                JSONArray closeTimeArray = supplierJson.getJSONArray("close_time");
                                JSONArray openTimeArray = supplierJson.getJSONArray("open_time");

                                // Chuyển đổi mảng thời gian thành chuỗi theo định dạng hh:mm:ss
                                String closeTime = formatTime(closeTimeArray);
                                String openTime = formatTime(openTimeArray);
                                // Tính khoảng cách
                                 distance = calculateDistance(userLatitude, userLongitude, latitude, longitude);

                                // Lấy thông tin SupplierInfo
                                JSONObject supplierTypeJson = supplierJson.getJSONObject("supplier_type");
                                int supplierTypeId = supplierTypeJson.getInt("id");
                                String typeName = supplierTypeJson.getString("typeName");
                                String imgUrl = supplierTypeJson.getString("imgUrl");


                                SupplierType supplierType= new SupplierType(supplierTypeId, typeName, imgUrl);

                                SupplierInfo supplierInfo = new SupplierInfo(id, restaurantName,  imageUrl, totalStarRating, totalReviewCount, supplierType);
                                supplierInfo.setLatitude(latitude);
                                supplierInfo.setLongitude(longitude);
                                supplierInfo.setDistance(distance);
                                supplierInfo.setOpenTime(openTime);
                                supplierInfo.setCloseTime(closeTime);
                                // Thêm SupplierInfo vào danh sách
                                supplierInfoList.add(supplierInfo);
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
    // Hàm để chuyển đổi mảng [giờ, phút] thành chuỗi hh:mm:ss
    private String formatTime(JSONArray timeArray) throws JSONException {
        int hour = timeArray.getInt(0);
        int minute = timeArray.getInt(1);

        // Trả về chuỗi theo định dạng hh:mm:ss
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute); // Thêm giây là 0 nếu không có giây
    }
    // Hàm tính khoảng cách giữa hai điểm
    private double calculateDistance(double userLat, double userLng, double supplierLat, double supplierLng) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(supplierLat - userLat);
        double lonDistance = Math.toRadians(supplierLng - userLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(supplierLat)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to km

        return distance;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Áp dụng hoạt ảnh khi quay lại trang trước
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }


}