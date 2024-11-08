package app.foodpt.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.helpers.GoogleSheetsService;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PaymentDetailActivity extends AppCompatActivity {

    private TextView transferAmount, transferContent, textViewOrderStatus, countdownTimer ;
    private double totalPrice = 0;
    private String contentBank = "";
    private long orderTime;
    private Button btnConfirmTransfer;
    private Handler handler = new Handler();
    private int countdownTime = 10 * 60; // 10 phút, 600 giây
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_detail);
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
        transferAmount = findViewById(R.id.transferAmount);
        transferContent = findViewById(R.id.transferContent);
        countdownTimer = findViewById(R.id.countdownTimer);


        int orderId = getIntent().getIntExtra("orderId", 0);
        totalPrice = getIntent().getDoubleExtra("totalPrice",0);
        contentBank = getIntent().getStringExtra("contentBank");
        orderTime = getIntent().getLongExtra("orderTime", 0);

        // Hiển thị giá trị lên TextView
        transferAmount.setText(String.format("%.0f VND", totalPrice));
        transferContent.setText(contentBank);

        try {
            getQRCodeUrlFromSheet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        textViewOrderStatus = findViewById(R.id.textViewOrderStatus);

        btnConfirmTransfer = findViewById(R.id.btnConfirmTransfer);
        btnConfirmTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnConfirmTransfer.setText("ĐANG KIỂM TRA");
                btnConfirmTransfer.setEnabled(false);
                long startTime = System.currentTimeMillis();  // Lưu thời gian bắt đầu
                compareValuesWithSheet(orderTime, orderId, startTime);
            }
        });

        // Bắt đầu đếm ngược và hiển thị thời gian
        startCountdown(orderId);
    }

    private void startCountdown(int orderId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = countdownTime / 60;
                int seconds = countdownTime % 60;
                countdownTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

                if (countdownTime > 0) {
                    countdownTime--;  // Giảm thời gian mỗi giây
                    handler.postDelayed(this, 1000);  // Lặp lại sau mỗi giây
                } else {
                    // Hết giờ sau 15 phút
                    deleteOrder(orderId);  // Gọi API xóa order
                    finish();  // Quay lại trang trước đó
                }
            }
        });
    }

    // Hàm xóa đơn hàng sử dụng Volley
    private void deleteOrder(int orderId) {
        String url = ApiEndpoints.DELETE_FOOD_ORDER +"/" + orderId;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("DeleteOrder", "Order deleted successfully: " + response);
                },
                error -> {
                    Log.e("DeleteOrder", "Failed to delete order: " + error.getMessage());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("JwtToken", null);
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(deleteRequest);
    }

    public void getQRCodeUrlFromSheet() throws IOException {
        String range = "qr!B8:B8";  // Lấy giá trị từ ô B8
        Sheets sheetsService = new GoogleSheetsService(PaymentDetailActivity.this).getSheetsService();
        String spreadsheetId = "1jwyONxe8utnzD9rgKu7urVQxkS0q78mr7QCbYJ9p9IM";  // Thay bằng ID của Google Sheet

        new Thread(() -> {
            try {
                ValueRange response = sheetsService.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();
                List<List<Object>> values = response.getValues();
                if (values != null && !values.isEmpty()) {
                    String qrCodeUrl = values.get(0).get(0).toString();  // URL của QR code nằm ở ô B8
                    runOnUiThread(() -> loadQRCode(qrCodeUrl));  // Gọi hàm tải ảnh QR code
                } else {
                    Log.e("Error", "No data found in the cell");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void loadQRCode(String qrCodeUrl) {
        ImageView qrCodeImage = findViewById(R.id.qrCodeImage);
        // Sử dụng Glide hoặc Picasso để tải ảnh từ URL
        Glide.with(this)
                .load(qrCodeUrl) // URL chứa hình ảnh QR
                .placeholder(R.drawable.vietcombankqr) // Hình ảnh mặc định khi đang tải
                .error(R.drawable.vietcombankqr)
                .into(qrCodeImage);
    }

    private void compareValuesWithSheet(long orderTimeMillis, int orderId, long startTime) {
        new Thread(() -> {
            String spreadsheetId = "10UmBMouznhL_dwAehlB9U3RbhKAi8ZzeiHSjrMCQeNE";
            String range = "macro!B:D"; // Thay đổi nếu cần

            boolean found = false;

            while (!found) {
                // Kiểm tra thời gian đã trôi qua
                if (System.currentTimeMillis() - startTime >= TimeUnit.MINUTES.toMillis(1)) {
                    // Nếu quá 1 phút, cập nhật trạng thái đơn hàng và thoát
                    runOnUiThread(() -> {
                        textViewOrderStatus.setText("Đơn hàng đang chờ xử lý.");
                    });
                    updatePaymentStatus(orderId, 2); // Trạng thái đang chờ
                    updateOrderStatus(orderId, "thất bại");
                    found = true;
                    Intent intent = new Intent(PaymentDetailActivity.this, CreateOrderSuccessActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                    break;
                }

                try {
                    Sheets sheetsService = new GoogleSheetsService(PaymentDetailActivity.this).getSheetsService();
                    ValueRange response = sheetsService.spreadsheets().values()
                            .get(spreadsheetId, range)
                            .execute();
                    List<List<Object>> values = response.getValues();

                    if (values != null && !values.isEmpty()) {
                        long orderTimePlusTwoHours = orderTimeMillis + TimeUnit.HOURS.toMillis(2);
                        List<List<Object>> filteredValues = new ArrayList<>();

                        for (List<Object> row : values) {
                            if (row.size() >= 3) {
                                String timeString = row.get(2).toString();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                Date time = null;

                                try {
                                    time = sdf.parse(timeString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    continue;
                                }

                                long timeMillis = time != null ? time.getTime() : 0;

                                if (timeMillis >= orderTimeMillis && timeMillis <= orderTimePlusTwoHours) {
                                    filteredValues.add(row);
                                }
                            }
                        }

                        for (List<Object> row : filteredValues) {
                            String amount = row.get(0).toString();
                            String content = row.get(1).toString();

                            if (content.equals(contentBank)) {
                                if (amount.equals(String.format("%.0f", totalPrice))) {
                                    runOnUiThread(() -> {
                                        textViewOrderStatus.setText("Thanh toán thành công");
                                    });
                                    found = true;
                                    updatePaymentStatus(orderId, 1);
                                    updateOrderStatus(orderId, "hoàn thành");
                                    Intent intent = new Intent(PaymentDetailActivity.this, CreateOrderSuccessActivity.class);
                                    intent.putExtra("orderId", orderId);
                                    startActivity(intent);
                                } else {
                                    runOnUiThread(() -> {
                                        textViewOrderStatus.setText("Thanh toán thất bại, số tiền không hợp lệ");
                                    });
                                    found = true;
                                    updatePaymentStatus(orderId, 2);
                                    updateOrderStatus(orderId, "thất bại");
                                    Intent intent = new Intent(PaymentDetailActivity.this, FailedPaymentActivity.class);
                                    intent.putExtra("orderId", orderId);
                                    startActivity(intent);
                                }
                                break;
                            }
                        }

                        if (!found) {
                            runOnUiThread(() -> {
                                textViewOrderStatus.setText("Đang kiểm tra...");
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            textViewOrderStatus.setText("Không có dữ liệu trong bảng tính.");
                        });
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

                try {
                    Thread.sleep(5000); // Chờ 5 giây trước khi quét lại
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }
    public void updateOrderStatus(long orderId, String orderStatus) {
        // Địa chỉ API
        String url = ApiEndpoints.UPDATE_ORDER_STATUS + "/" + orderId;

        // Tạo một JSON object chứa paymentStatus
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("orderStatus", orderStatus);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Không gửi request nếu có lỗi khi tạo JSON object
        }

        // Lấy JWT Token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);

        // Tạo một request mới
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý response khi thành công
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token); // Thêm JWT Token vào header
                }
                return headers;
            }
        };

        // Thêm request vào hàng đợi (queue)
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    public void updatePaymentStatus(long orderId, int paymentStatus) {
        // Địa chỉ API
        String url = ApiEndpoints.UPDATE_PAYMENT_STATUS + "/" + orderId;

        // Tạo một JSON object chứa paymentStatus
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("paymentStatus", paymentStatus);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Không gửi request nếu có lỗi khi tạo JSON object
        }

        // Lấy JWT Token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);

        // Tạo một request mới
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý response khi thành công
                        textViewOrderStatus.setText("Payment status updated successfully!");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        textViewOrderStatus.setText("Error updating payment status: " + error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token); // Thêm JWT Token vào header
                }
                return headers;
            }
        };

        // Thêm request vào hàng đợi (queue)
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }




}