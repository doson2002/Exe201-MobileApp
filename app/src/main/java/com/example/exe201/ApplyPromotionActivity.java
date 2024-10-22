package com.example.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.PromotionCustomerAdapter;
import com.example.exe201.DTO.PromotionResponse;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplyPromotionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PromotionCustomerAdapter promotionCustomerAdapter;
    private List<PromotionResponse> promotionList;
    private EditText editTextVoucherCode;
    private ImageView imageViewBack;
    private Button buttonUseVoucher;
    private int selectedPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_promotion);
        EdgeToEdge.enable(this);

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
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });

        SupplierInfo supplierInfoChose = Utils.getSupplierInfo(this);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        recyclerView = findViewById(R.id.recyclerViewVouchers);
        editTextVoucherCode = findViewById(R.id.editTextVoucherCode);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        promotionList = new ArrayList<>();
        promotionCustomerAdapter = new PromotionCustomerAdapter(promotionList, position -> {
            selectedPosition = position; // Cập nhật selectedPosition
        });
        recyclerView.setAdapter(promotionCustomerAdapter);
        // Gọi hàm để lấy danh sách khuyến mãi ban đầu
        assert supplierInfoChose != null;
        fetchPromotions(supplierInfoChose.getId(),"",jwtToken);
        // Thêm TextWatcher để theo dõi sự thay đổi trong EditText
        editTextVoucherCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchCode = charSequence.toString().trim();
                assert supplierInfoChose != null;
                fetchPromotions(supplierInfoChose.getId(),searchCode, jwtToken); // Gọi lại API với mã code mới
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        buttonUseVoucher = findViewById(R.id.buttonUseVoucher);
        buttonUseVoucher.setOnClickListener(v -> {
            // Kiểm tra xem có voucher nào đã được chọn không
            if (selectedPosition != -1) { // -1 nghĩa là chưa chọn voucher nào
                // Lấy promotionId từ voucher đã chọn
                PromotionResponse selectedPromotion = promotionList.get(selectedPosition);
                int promotionId = selectedPromotion.getId(); // Giả sử bạn có phương thức getId() để lấy ID

                // Thông báo voucher đã được áp dụng
                Toast.makeText(this, "Voucher đã được áp dụng: " + selectedPromotion.getCode(), Toast.LENGTH_SHORT).show();

                // Trở về trang order và gửi promotionId
                Intent intent = new Intent();
                intent.putExtra("promotion", selectedPromotion);
                setResult(RESULT_OK, intent);
                finish(); // Hoặc dùng finishAffinity() nếu cần thiết

            } else {
                // Thông báo chưa chọn voucher
                Toast.makeText(this, "Vui lòng chọn một voucher để áp dụng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPromotions(int supplierId, String code,String jwtToken) {
        String url = ApiEndpoints.GET_ALL_PROMOTION +"/"+ supplierId + "?code=" + code; // Thay thế bằng URL của bạn

        // Tạo yêu cầu JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Phân tích JSON
                        JSONArray promotionsArray = response.getJSONArray("content");
                        for (int i = 0; i < promotionsArray.length(); i++) {
                            JSONObject promotionObject = promotionsArray.getJSONObject(i);
                            PromotionResponse promotion = new PromotionResponse();
                            promotion.setCode(promotionObject.getString("code"));
                            promotion.setDescription(promotionObject.getString("description"));
                            promotion.setStatus(promotionObject.getBoolean("status"));
                            promotion.setDiscountPercentage(promotionObject.getDouble("discount_percentage"));
                            promotion.setFixedDiscountAmount(promotionObject.getDouble("fixed_discount_amount"));
                            promotionList.add(promotion);
                        }

                        // Cập nhật adapter
                        promotionCustomerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ApplyPromotionActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Xử lý lỗi
                    Toast.makeText(ApplyPromotionActivity.this, "Error fetching promotions", Toast.LENGTH_SHORT).show();
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        // Thêm yêu cầu vào queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}