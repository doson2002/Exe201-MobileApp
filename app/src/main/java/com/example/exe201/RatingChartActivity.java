package com.example.exe201;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.RatingPagerAdapter;
import com.example.exe201.DTO.SupplierRatingResponse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RatingChartActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private BarChart barChart;
    private TextView textTotalRating, textViewReviews;
    private ImageView back_arrow;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private RatingPagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating_chart);

        textTotalRating = findViewById(R.id.textTotalRating);
        textViewReviews = findViewById(R.id.textViewReviews);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Khởi tạo biểu đồ
        barChart = findViewById(R.id.barChart);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        int supplierId = sharedPreferences.getInt("supplier_id",0);
        // Khởi tạo Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Gọi API để lấy dữ liệu
        fetchRatingDetails(supplierId,jwtToken); // Thay thế 123L bằng supplierId thực tế
        fetchRatings(supplierId, jwtToken);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView(); // Lấy view tùy chỉnh
                if (tabView != null) {
                    TextView tabText = tabView.findViewById(R.id.tabText);
                    tabText.setTextColor(getResources().getColor(R.color.orange)); // Màu chữ khi được chọn
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView(); // Lấy view tùy chỉnh
                if (tabView != null) {
                    TextView tabText = tabView.findViewById(R.id.tabText);
                    tabText.setTextColor(getResources().getColor(R.color.black)); // Màu chữ khi không được chọn
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Không cần xử lý gì ở đây
            }
        });

    }
    private void fetchRatingDetails(int supplierId, String jwtToken) {
        String url = ApiEndpoints.GET_RATING_DETAIL + "/" + supplierId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ánh xạ dữ liệu từ JSON sang model SupplierRatingResponse
                            SupplierRatingResponse ratingResponse = new SupplierRatingResponse();
                            ratingResponse.setTotalReviews(response.getInt("totalReviews"));
                            ratingResponse.setAverageRating(response.getDouble("averageRating"));
                            ratingResponse.setOneStarCount(response.getInt("oneStarCount"));
                            ratingResponse.setTwoStarCount(response.getInt("twoStarCount"));
                            ratingResponse.setThreeStarCount(response.getInt("threeStarCount"));
                            ratingResponse.setFourStarCount(response.getInt("fourStarCount"));
                            ratingResponse.setFiveStarCount(response.getInt("fiveStarCount"));

                            // Cập nhật biểu đồ
                            updateChart(ratingResponse);
                            textViewReviews.setText("("+ String.valueOf(ratingResponse.getTotalReviews()) + " lượt đánh giá)");
                            textTotalRating.setText(String.format("%.1f", ratingResponse.getAverageRating()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RatingChartActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Toast.makeText(RatingChartActivity.this, "API Request Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };

        // Thêm yêu cầu vào hàng đợi của Volley
        requestQueue.add(jsonObjectRequest);
    }

    private void updateChart(SupplierRatingResponse ratingResponse) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        // Thêm các cột vào biểu đồ với giá trị tương ứng cho từng đánh giá
        entries.add(new BarEntry(1, ratingResponse.getOneStarCount()));
        entries.add(new BarEntry(2, ratingResponse.getTwoStarCount()));
        entries.add(new BarEntry(3, ratingResponse.getThreeStarCount()));
        entries.add(new BarEntry(4, ratingResponse.getFourStarCount()));
        entries.add(new BarEntry(5, ratingResponse.getFiveStarCount()));

        BarDataSet dataSet = new BarDataSet(entries, "Rating Distribution");

        // Thiết lập màu cho từng cột theo kiểu 5 màu
        dataSet.setColors(new int[]{Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.MAGENTA});

        // Hiển thị giá trị trên các cột
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f); // Đặt độ rộng của cột

        // Tùy chỉnh trục X để hiển thị nhãn từ 1 đến 5 sao
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setValueFormatter(new StarValueFormatter()); // Sử dụng ValueFormatter tùy chỉnh
// Ẩn lưới dọc trên trục X
        xAxis.setDrawGridLines(false); // Ẩn lưới dọc

        // Tùy chỉnh trục Y để hiển thị nhãn số lượng đánh giá và ẩn lưới ngang
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false); // Tắt lưới ngang
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMinimum(0f); // Đặt giá trị tối thiểu cho trục Y

        // Tùy chỉnh trục Y bên phải
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // Ẩn trục Y bên phải

        // Tắt mô tả và chú thích của biểu đồ
        barChart.getDescription().setEnabled(false);

        // Đặt màu nền của biểu đồ là màu trắng
        barChart.setBackgroundColor(Color.WHITE);

        // Đặt dữ liệu và cập nhật biểu đồ
        barChart.setData(barData);
        barChart.setFitBars(true); // Đảm bảo cột được căn đều
        barChart.invalidate(); // Vẽ lại biểu đồ với dữ liệu mới
    }

    // ValueFormatter tùy chỉnh để hiển thị số lượng ngôi sao
    private class StarValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int intValue = (int) value;
            StringBuilder stars = new StringBuilder();

            // Xây dựng chuỗi sao tương ứng với giá trị
            for (int i = 0; i < intValue; i++) {
                stars.append("★"); // Thêm ngôi sao vào chuỗi
            }

            return stars.toString(); // Trả về chuỗi sao
        }
    }

    private void fetchRatings(int supplierId, String jwtToken) {
        String url = ApiEndpoints.GET_ALL_RATING+ "/"+ supplierId; // Đặt URL API của bạn ở đây

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Tạo danh sách cho lowRatingComments (1, 2, 3 sao) và highRatingComments (4, 5 sao)
                        List<String> lowRatingComments = new ArrayList<>();
                        List<String> highRatingComments = new ArrayList<>();

                        // Duyệt qua các key trong response
                        for (Iterator<String> it = response.keys(); it.hasNext(); ) {
                            String starRating = it.next();
                            JSONArray commentsArray = response.getJSONArray(starRating);

                            // Chuyển đổi rating từ String sang int để phân loại
                            int rating = Integer.parseInt(starRating);

                            // Phân loại các đánh giá
                            if (rating >= 1 && rating <= 3) {
                                for (int i = 0; i < commentsArray.length(); i++) {
                                    String comment = commentsArray.getString(i);
                                    if (!comment.isEmpty()) { // Bỏ qua các comment trống
                                        lowRatingComments.add(comment);
                                    }
                                }
                            } else if (rating == 4 || rating == 5) {
                                for (int i = 0; i < commentsArray.length(); i++) {
                                    String comment = commentsArray.getString(i);
                                    if (!comment.isEmpty()) { // Bỏ qua các comment trống
                                        highRatingComments.add(comment);
                                    }
                                }
                            }
                        }

                        // Thiết lập ViewPager2 và TabLayout sau khi phân loại xong
                        pagerAdapter = new RatingPagerAdapter(this, lowRatingComments, highRatingComments);
                        viewPager2.setAdapter(pagerAdapter);

                        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
                            View tabView = LayoutInflater.from(tabLayout.getContext()).inflate(R.layout.tab_item, null);
                            TextView tabText = tabView.findViewById(R.id.tabText);
                            switch (position) {
                                case 0:
                                    tabText.setText("1-3 ★");
                                    break;
                                case 1:
                                    tabText.setText("4-5 ★");
                                    break;
                            }
                            tab.setCustomView(tabView);
                        }).attach();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Xử lý lỗi
                    Toast.makeText(this, "Failed to load data!", Toast.LENGTH_SHORT).show();
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

}

