package app.foodpt.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.SupplierWithFoodItemAdapter;
import app.foodpt.exe201.DTO.FoodItemResponseWithSupplier;
import app.foodpt.exe201.DTO.Menu;
import app.foodpt.exe201.DTO.SupplierInfo;
import app.foodpt.exe201.DTO.SupplierType;
import app.foodpt.exe201.DTO.SupplierWithFoodItems;
import app.foodpt.exe201.Decorations.CustomItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodItemGroupedBySupplierActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageView backArrow;
    private RecyclerView recyclerViewGroupedBySupplier;
    private SupplierWithFoodItemAdapter supplierAdapter;
    private List<SupplierWithFoodItems> supplierWithFoodItemsList = new ArrayList<>();
//    private ImageView imgShowCart;
    private CardView outerBasketLayout;
    private View redDot;
    private HashMap<Integer, List<Menu>> cartMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_grouped_by_supplier);

        String keyword = getIntent().getStringExtra("keyword");
        searchFoodItems(keyword);

        redDot = findViewById(R.id.redDot);
        outerBasketLayout= findViewById(R.id.outerBasketLayout);
        outerBasketLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodItemGroupedBySupplierActivity.this, SupplierCartActivity.class);
                intent.putExtra("cart_map",  cartMap); // Truyền cartMap
                startActivity(intent);
            }
        });

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });


        // Ánh xạ các thành phần UI
        searchEditText = findViewById(R.id.search);
        searchEditText.setText(keyword);

        recyclerViewGroupedBySupplier = findViewById(R.id.recyclerViewGroupedBySupplier);

        // Chuyển đổi 20dp thành pixel
        int spacingInPixels = (int) (20 * getResources().getDisplayMetrics().density);
        // Setup RecyclerView và Adapter
        recyclerViewGroupedBySupplier.setLayoutManager(new LinearLayoutManager(this));
        supplierAdapter = new SupplierWithFoodItemAdapter(supplierWithFoodItemsList, this, cartMap,outerBasketLayout,redDot);
        recyclerViewGroupedBySupplier.setAdapter(supplierAdapter);
        // Thêm khoảng cách giữa các item
        recyclerViewGroupedBySupplier.addItemDecoration(new CustomItemDecoration(spacingInPixels));

        // Lắng nghe sự thay đổi text trong EditText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Không cần xử lý gì trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Gọi hàm search khi text thay đổi
                searchFoodItems(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Không cần xử lý gì sau khi text thay đổi
            }
        });
    }


    // Hàm gọi API để tìm kiếm các món ăn theo từ khóa
    private void searchFoodItems(String keyword) {
        // Kiểm tra nếu keyword trống, không thực hiện tìm kiếm
        if (keyword == null || keyword.trim().isEmpty()) {
            return; // Có thể thông báo cho người dùng về từ khóa không hợp lệ
        }

        // Thực hiện lời gọi API tới backend để lấy danh sách món ăn theo từ khóa
        String url = ApiEndpoints.GET_FOOD_ITEM_GROUPED_BY_SUPPLIER_ID + keyword;
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        // Lấy latitude và longitude của người dùng từ SharedPreferences
        double userLatitude = sharedPreferences.getFloat("latitude", 0.0f);
        double userLongitude = sharedPreferences.getFloat("longitude", 0.0f);

        if (token == null) {
            Log.e("SearchFoodItems", "Token is null, user might not be logged in.");
            return;
        }

        // Chỉ tạo một request queue duy nhất trong ứng dụng
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Lấy mảng content từ response
                        JSONArray contentArray = response.getJSONArray("content");

                        // Xóa danh sách cũ trước khi thêm mới
                        supplierWithFoodItemsList.clear();

                        // Lặp qua từng đối tượng trong contentArray
                        for (int i = 0; i < contentArray.length(); i++) {
                            JSONObject supplierWithFoodItemsJson = contentArray.getJSONObject(i);

                            // Lấy thông tin SupplierInfo
                            JSONObject supplierInfoJson = supplierWithFoodItemsJson.getJSONObject("supplierInfo");
                            String restaurantName = supplierInfoJson.getString("restaurantName");
                            String imgUrl =   supplierInfoJson.getString("imgUrl");
                            double totalStarRating = supplierInfoJson.getDouble("totalStarRating");
                            int totalReviewCount = supplierInfoJson.getInt("totalReviewCount");
                            double latitude = supplierInfoJson.getDouble("latitude");
                            double longitude = supplierInfoJson.getDouble("longitude");
                            // Lấy close_time và open_time từ JSON
                            JSONArray closeTimeArray = supplierInfoJson.getJSONArray("closeTime");
                            JSONArray openTimeArray = supplierInfoJson.getJSONArray("openTime");

                            // Chuyển đổi mảng thời gian thành chuỗi theo định dạng hh:mm:ss
                            String closeTime = formatTime(closeTimeArray);
                            String openTime = formatTime(openTimeArray);
                            // Tính khoảng cách
                            double distance = calculateDistance(userLatitude, userLongitude, latitude, longitude);
                            // Lấy chi tiết SupplierInfo từ "user" và "supplierType"
                            JSONObject userJson = supplierInfoJson.getJSONObject("user");
                            JSONObject supplierTypeJson = supplierInfoJson.getJSONObject("supplierType");
                            int supplierTypeId = supplierTypeJson.getInt("id");
                            String typeName = supplierTypeJson.getString("typeName");
                            String typeImgUrl = supplierTypeJson.getString("imgUrl");

                            SupplierType supplierType = new SupplierType(supplierTypeId, typeName, typeImgUrl);
                            int supplierId = supplierInfoJson.getInt("id");
                            SupplierInfo supplierInfo = new SupplierInfo(
                                    supplierId,
                                    restaurantName,
                                    imgUrl,
                                    totalStarRating,
                                    totalReviewCount,
                                    supplierType
                            );

                            supplierInfo.setLatitude(latitude);
                            supplierInfo.setLongitude(longitude);
                            supplierInfo.setDistance(distance);
                            supplierInfo.setOpenTime(openTime);
                            supplierInfo.setCloseTime(closeTime);

                            // Lấy danh sách FoodItems
                            JSONArray foodItemsJsonArray = supplierWithFoodItemsJson.getJSONArray("foodItems");
                            List<FoodItemResponseWithSupplier> foodItemResponses = new ArrayList<>();
                            for (int j = 0; j < foodItemsJsonArray.length(); j++) {
                                JSONObject foodItemJson = foodItemsJsonArray.getJSONObject(j);

                                // Tạo FoodItemResponse từ các thông tin trả về
                                FoodItemResponseWithSupplier foodItemResponse = new FoodItemResponseWithSupplier(
                                        foodItemJson.getInt("id"),
                                        foodItemJson.getString("food_name"),
                                        foodItemJson.getDouble("price"),
                                        foodItemJson.getString("image_url"),
                                        foodItemJson.getInt("quantity_add"),
                                        supplierId
                                );
                                foodItemResponse.setQuantityInventory(foodItemJson.getInt("quantity_inventory"));
                                foodItemResponses.add(foodItemResponse);
                            }

                            // Tạo đối tượng SupplierWithFoodItems và thêm vào danh sách
                            SupplierWithFoodItems supplierWithFoodItems = new SupplierWithFoodItems(supplierInfo, foodItemResponses);
                            supplierWithFoodItemsList.add(supplierWithFoodItems);
                        }

                        // Cập nhật Adapter trong UI thread
                        runOnUiThread(() -> supplierAdapter.notifyDataSetChanged());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Có thể hiển thị thông báo lỗi cho người dùng
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Có thể hiển thị thông báo lỗi cho người dùng
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Thêm request vào hàng đợi
        queue.add(jsonObjectRequest);
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


}