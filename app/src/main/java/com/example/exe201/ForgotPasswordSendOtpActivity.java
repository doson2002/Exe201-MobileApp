package com.example.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordSendOtpActivity extends AppCompatActivity {
    private EditText edEmail;
    private TextView tvErrorMessage;
    private Button btnSendOtp;
    private ImageView backArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_send_otp);
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        // Liên kết các view từ layout
        edEmail = findViewById(R.id.EdEmail);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        // Set action cho nút Send OTP
        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    sendOtpRequest(email);
                } else {
                    tvErrorMessage.setText("Please enter a valid email address.");
                }
            }
        });

    }
    private void sendOtpRequest(String email) {
        String url = ApiEndpoints.FORGOT_PASSWORD_SEND_OTP +"/"+ email;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Reset error message trước khi gửi yêu cầu mới
        tvErrorMessage.setText("");

        // Create a JsonObjectRequest để gọi API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                Toast.makeText(ForgotPasswordSendOtpActivity.this, "Email sent for verification!", Toast.LENGTH_SHORT).show();

                                // Điều hướng đến ForgotPasswordVerifyOtpActivity
                                Intent intent = new Intent(ForgotPasswordSendOtpActivity.this, ForgotPasswordVerifyOtpActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMessage = response.getString("errorMessage");
                                tvErrorMessage.setText(errorMessage);  // Hiển thị errorMessage từ server
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvErrorMessage.setText("An error occurred while parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "An unexpected error occurred.";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                // Parse error response
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                JSONObject jsonError = new JSONObject(responseBody);
                                if (jsonError.has("errorMessage")) {
                                    errorMessage = jsonError.getString("errorMessage");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        tvErrorMessage.setText("Failed to send OTP: " + errorMessage);
                    }
                }
        );
        // Thiết lập thời gian chờ (timeout) cho yêu cầu
        int socketTimeout = 10000; // 10 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        // Thêm yêu cầu vào hàng đợi của Volley
        requestQueue.add(jsonObjectRequest);
    }


}