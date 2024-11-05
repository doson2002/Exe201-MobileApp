package app.foodpt.exe201;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.PromotionAdapter;
import app.foodpt.exe201.DTO.PromotionResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionActivity extends AppCompatActivity {
    private FloatingActionButton fabAddPromotion;
    private RequestQueue requestQueue; // Volley RequestQueue để xử lý API
    private RecyclerView recyclerView;
    private PromotionAdapter adapter;
    private List<PromotionResponse> promotionList;
    private ImageView back_arrow;
    private  String jwtToken;
    private  int supplierId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
         jwtToken = sharedPreferences.getString("JwtToken", null);
         supplierId = sharedPreferences.getInt("supplier_id", 0);

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerViewPromotion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        promotionList = new ArrayList<>();
        adapter = new PromotionAdapter(this, promotionList);
        recyclerView.setAdapter(adapter);

        loadPromotions(supplierId, jwtToken);
        // Khởi tạo RequestQueue cho Volley
        requestQueue = Volley.newRequestQueue(this);

        fabAddPromotion = findViewById(R.id.fabAddPromotion);
        fabAddPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPromotionDialog();
            }
        });
    }
    private void loadPromotions(int supplierId, String jwtToken) {
        String url = ApiEndpoints.GET_ALL_PROMOTION + "/" + supplierId; // Đặt URL API đúng

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray contentArray = response.getJSONArray("content");
                        for (int i = 0; i < contentArray.length(); i++) {
                            JSONObject promotionObj = contentArray.getJSONObject(i);

                            PromotionResponse promotion = new PromotionResponse();
                            promotion.setId(promotionObj.getInt("id"));
                            promotion.setCode(promotionObj.getString("code"));
                            promotion.setDescription(promotionObj.getString("description"));
                            promotion.setStatus(promotionObj.getBoolean("status"));
                            promotion.setDiscountPercentage(promotionObj.getDouble("discount_percentage"));
                            promotion.setFixedDiscountAmount(promotionObj.getDouble("fixed_discount_amount"));
                            promotion.setSupplierId(promotionObj.getLong("supplier_id"));

                            promotionList.add(promotion);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(PromotionActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



        Volley.newRequestQueue(this).add(request);
    }

    private void openAddPromotionDialog() {
        // Tạo dialog
        final Dialog dialog = new Dialog(PromotionActivity.this);
        dialog.setContentView(R.layout.dialog_add_promotion);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // Lấy các thành phần từ dialog layout
        TextView textViewDiscountType = dialog.findViewById(R.id.textViewDiscountType);
        EditText editTextDiscountPercentage = dialog.findViewById(R.id.editTextDiscountPercentage);
        EditText editTextFixedDiscountAmount = dialog.findViewById(R.id.editTextFixedDiscountAmount);
        EditText editTextDescription = dialog.findViewById(R.id.editTextDescription);
        CheckBox checkBoxPromotionStatus = dialog.findViewById(R.id.checkBoxPromotionStatus);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);

        // Tạo PopupWindow với danh sách lựa chọn
        String[] discountTypes = getResources().getStringArray(R.array.discount_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.popup_item, discountTypes);

        // Tạo View cho PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_layout, null);
        ListView listViewPopup = popupView.findViewById(R.id.listViewPopup);
        listViewPopup.setAdapter(adapter);

        // Tạo PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        // Hiển thị PopupWindow khi người dùng click vào TextView
        textViewDiscountType.setOnClickListener(v -> popupWindow.showAsDropDown(textViewDiscountType));

        // Xử lý sự kiện khi người dùng chọn một item từ PopupWindow
        listViewPopup.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = discountTypes[position];
            textViewDiscountType.setText(selectedType); // Cập nhật TextView khi chọn

            // Hiển thị trường nhập liệu dựa trên lựa chọn
            if (selectedType.equals("Discount Percentage")) {
                editTextDiscountPercentage.setVisibility(View.VISIBLE);
                editTextFixedDiscountAmount.setVisibility(View.GONE);
            } else {
                editTextDiscountPercentage.setVisibility(View.GONE);
                editTextFixedDiscountAmount.setVisibility(View.VISIBLE);
            }

            // Đóng PopupWindow sau khi chọn
            popupWindow.dismiss();
        });

        // Xử lý khi người dùng bấm nút Cancel
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        // Xử lý khi người dùng bấm nút Confirm
        buttonConfirm.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            boolean isActive = checkBoxPromotionStatus.isChecked(); // Lấy trạng thái khuyến mãi

            // Kiểm tra trường mô tả
            if (description.isEmpty()) {
                editTextDescription.setError("Mô tả không được để trống");
                return;
            }

            // Kiểm tra loại khuyến mãi được chọn
            String selectedType = textViewDiscountType.getText().toString();
            if (selectedType.isEmpty()) {
                textViewDiscountType.setError("Loại khuyến mãi không được để trống");
                return;
            }

            double discountPercentage = 0;
            double fixedDiscountAmount = 0;

            // Kiểm tra giá trị nhập dựa trên loại khuyến mãi
            if (selectedType.equals("Discount Percentage")) {
                String discountPercentageStr = editTextDiscountPercentage.getText().toString().trim();
                if (discountPercentageStr.isEmpty()) {
                    editTextDiscountPercentage.setError("Phần trăm giảm giá không được để trống");
                    return;
                }
                try {
                    discountPercentage = Double.parseDouble(discountPercentageStr) / 100;
                } catch (NumberFormatException e) {
                    editTextDiscountPercentage.setError("Phần trăm giảm giá phải là một số hợp lệ");
                    return;
                }
            } else {
                String fixedDiscountAmountStr = editTextFixedDiscountAmount.getText().toString().trim();
                if (fixedDiscountAmountStr.isEmpty()) {
                    editTextFixedDiscountAmount.setError("Giảm giá cố định không được để trống");
                    return;
                }
                try {
                    fixedDiscountAmount = Double.parseDouble(fixedDiscountAmountStr);
                } catch (NumberFormatException e) {
                    editTextFixedDiscountAmount.setError("Giảm giá cố định phải là một số hợp lệ");
                    return;
                }
            }

            // Gọi hàm để thực hiện API tạo promotion
            createPromotion(description, discountPercentage, fixedDiscountAmount, isActive);

            dialog.dismiss(); // Đóng dialog sau khi gọi API
        });
        // Hiển thị dialog
        dialog.show();
    }

    private void createPromotion(String description, double discountPercentage, double fixedDiscountAmount, boolean isActive) {
        // Hiển thị ProgressDialog khi thực hiện API
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating promotion...");
        progressDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        int supplierId = sharedPreferences.getInt("supplier_id", 0);

        String url = ApiEndpoints.CREATE_PROMOTION;  // URL API

        // Tạo JSON để gửi lên server
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("supplier_id", supplierId);  // Thay bằng supplierId thực tế
            jsonObject.put("discount_percentage", discountPercentage);
            jsonObject.put("fixed_discount_amount", fixedDiscountAmount);
            jsonObject.put("promotion_type", 2);
            jsonObject.put("description", description);

            jsonObject.put("status", isActive);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tạo yêu cầu POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("status");
                            if ("success".equals(status)) {
                                Toast.makeText(PromotionActivity.this, "Promotion created successfully!", Toast.LENGTH_SHORT).show();
                                loadPromotions(supplierId, jwtToken);
                            } else {
                                String errorMessage = response.getString("errorMessage");
                                Toast.makeText(PromotionActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(PromotionActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        // Thêm yêu cầu vào RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}