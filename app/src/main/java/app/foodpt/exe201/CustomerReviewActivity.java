package app.foodpt.exe201;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.ReviewAdapter;
import app.foodpt.exe201.DTO.Rating;
import app.foodpt.exe201.DTO.SupplierInfo;
import app.foodpt.exe201.Fragment.Customer.PlaceFragment;
import app.foodpt.exe201.Fragment.Customer.ReviewFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerReviewActivity extends AppCompatActivity {

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Rating> ratingList;
    private RequestQueue requestQueue;
    private ImageView avatarImage, supplierImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_review);
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
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
        Button placeButton = findViewById(R.id.placeButton);
        Button reviewButton = findViewById(R.id.reviewButton);


        // Load default fragment (e.g., ReviewFragment)
        loadFragment(new ReviewFragment());
        // Tạo GradientDrawable cho button
        setButtonBackground(placeButton, R.color.light_hint);
        setButtonBackground(reviewButton, R.color.light_orange1);

        // Đặt màu chữ mặc định cho cả hai nút
        placeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        reviewButton.setTextColor(ContextCompat.getColor(this, R.color.white));

        placeButton.setOnClickListener(v -> {
            loadFragment(new PlaceFragment());
            // Đổi màu nút
            setButtonBackground(placeButton, R.color.light_orange1); // Màu cam khi nhấn
            setButtonBackground(reviewButton, R.color.light_hint); // Trở lại màu mặc định
            reviewButton.setTextColor(ContextCompat.getColor(this, R.color.black));
            placeButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        });

        reviewButton.setOnClickListener(v -> {
            loadFragment(new ReviewFragment());
            // Đổi màu nút
            setButtonBackground(reviewButton, R.color.light_orange1); // Màu cam khi nhấn
            setButtonBackground(placeButton, R.color.light_hint); // Trở lại màu mặc định
            placeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
            reviewButton.setTextColor(ContextCompat.getColor(this, R.color.white)); // Màu chữ trắng cho nút review
        });
//        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
//        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        RatingBar ratingBarInput = findViewById(R.id.ratingBarInput); // Giả sử bạn đã có ratingBar trong layout
//        ratingBarInput.setRating(0);
//
//        EditText messageInput = findViewById(R.id.editReviewText); // Giả sử bạn có EditText để nhập bình luận


        supplierImage = findViewById(R.id.supplierImgUrl);


        // Initialize the request queue
        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);
        SupplierInfo supplierInfo = getIntent().getParcelableExtra("supplier");
        int supplierId = supplierInfo.getId();
        getSupplierById(supplierId);
        // Call the API to fetch ratings
//        getRatingsBySupplierId(supplierId);



//        Button submitReviewButton = findViewById(R.id.submitReviewButton); // Giả sử bạn có nút submit
//        submitReviewButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                float ratingStar = ratingBarInput.getRating(); // Lấy số sao từ rating bar
//                if (ratingStar == 0.0f) {
//                    // Thông báo cho người dùng chọn sao
//                    Toast.makeText(CustomerReviewActivity.this, "Vui lòng chọn số sao trước khi gửi đánh giá!", Toast.LENGTH_SHORT).show();
//                } else {
//                    String responseMessage = messageInput.getText().toString(); // Lấy bình luận từ người dùng
//                    addRating(userId, supplierId, ratingStar, responseMessage, new ReviewAdapter.AddRatingCallback() {
//                        @Override
//                        public void onSuccess() {
//                            Toast.makeText(CustomerReviewActivity.this, "Bình luận thành công!", Toast.LENGTH_SHORT).show();
//                            messageInput.setText("");
//                        }
//
//                        @Override
//                        public void onError(String errorMessage) {
//                            Toast.makeText(CustomerReviewActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });

    }
    private void setButtonBackground(Button button, int colorResId) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(ContextCompat.getColor(this, colorResId)); // Thiết lập màu cho button
        drawable.setCornerRadius(16f); // Độ bo tròn
        button.setBackground(drawable); // Áp dụng drawable cho button
    }

    private void loadFragment(Fragment fragment) {
        // Replace the current fragment in the fragment_container
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

//    private void getRatingsBySupplierId(int supplierId) {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        String jwtToken = sharedPreferences.getString("JwtToken", null);
//
//        String url = ApiEndpoints.GET_RATING_BY_SUPPLIER_ID + "/" +supplierId; // Replace with your API URL
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                url,
//                null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject ratingObject = response.getJSONObject(i);
//                                int id = ratingObject.getInt("id");
//                                JSONObject userObject = ratingObject.getJSONObject("users");
//                                String fullName = userObject.getString("fullName");
//                                String imgUrl = userObject.getString("imgUrl");
//                                int ratingStar = ratingObject.getInt("rating_star");
//                                String responseMessage = ratingObject.getString("response_message");
//
//                                // Add to rating list
//                                ratingList.add(new Rating(id, fullName, ratingStar, responseMessage, imgUrl));
//                            }
//
//                            // Notify adapter that data has changed
//                            reviewAdapter.notifyDataSetChanged();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(CustomerReviewActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(CustomerReviewActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ){
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
//                return headers;
//            }
//        };
//
//        // Add the request to the RequestQueue.
//        requestQueue.add(jsonArrayRequest);
//    }

    private void getSupplierById(int supplierId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.GET_SUPPLIER_BY_ID + "/" + supplierId; // URL API với supplierId

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy img_url từ response
                            String imgUrl = response.getString("img_url");

                            // Thực hiện hành động với imgUrl (ví dụ, hiển thị hình ảnh)
                            // Ví dụ: bạn có thể load imgUrl vào ImageView sử dụng thư viện như Glide
                            Glide.with(CustomerReviewActivity.this)
                                    .load(imgUrl)
                                    .into(supplierImage);  // Đảm bảo rằng bạn đã khai báo imageView trong layout

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CustomerReviewActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error fetching data";
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            errorMessage += " - Status code: " + statusCode;
                        }
                        Toast.makeText(CustomerReviewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

        // Thêm request vào RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

//    private void addRating(int userId, int supplierId, float ratingStar, String responseMessage, final ReviewAdapter.AddRatingCallback callback) {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        String jwtToken = sharedPreferences.getString("JwtToken", null);
//
//        String url = ApiEndpoints.CREATE_RATING;
//
//        JSONObject ratingData = new JSONObject();
//        try {
//            ratingData.put("user_id", userId);
//            ratingData.put("supplier_id", supplierId);
//            ratingData.put("rating_star", ratingStar);
//            ratingData.put("response_message", responseMessage);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.POST,
//                url,
//                ratingData,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            // Parse response to get new rating details
//                            int id = response.getInt("id");
//                            JSONObject userObject = response.getJSONObject("users");
//                            String fullName = userObject.getString("fullName");
//                            String imgUrl = userObject.getString("imgUrl");
//                            int ratingStar = response.getInt("ratingStar");
//                            String responseMessage = response.getString("responseMessage");
//
//                            // Tạo đối tượng Rating mới và thêm vào adapter
//                            Rating newRating = new Rating(id, fullName, ratingStar, responseMessage, imgUrl);
//                            reviewAdapter.addRating(newRating); // Thêm rating mới vào adapter
//
//                            // Thông báo thành công
//                            if (callback != null) {
//                                callback.onSuccess();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            if (callback != null) {
//                                callback.onError("Error parsing response");
//                            }
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (callback != null) {
//                            callback.onError(error.getMessage());
//                        }
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + jwtToken);
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        requestQueue.add(jsonObjectRequest);
//    }



}
