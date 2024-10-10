package com.example.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ForgotPasswordVerifyOtpActivity extends AppCompatActivity {
    private TextView tvCountdownTimer;
    private TextView tvResendOtp, tvErrorMessage;
    private Button verifyOtpButton;
    private EditText otpInput;
    private String email; // Email cần gửi lại OTP
    private CountDownTimer countDownTimer;
    private static final long OTP_VALIDITY_DURATION = 60000;  // 1 minute in milliseconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_verify_otp);
        // Liên kết các view
        tvCountdownTimer = findViewById(R.id.tvCountdownTimer);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        verifyOtpButton = findViewById(R.id.verifyOtp);
        otpInput = findViewById(R.id.EdOtp);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);




        email = getIntent().getStringExtra("email");
        // Khởi tạo bộ đếm thời gian OTP
        startCountdownTimer();

        // Set action cho TextView "Gửi lại OTP"
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic gửi lại OTP
                resendOtp(email);
            }
        });
        // Set action cho nút "Xác thực OTP"
        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpInput.getText().toString().trim();
                String email = getIntent().getStringExtra("email"); // Lấy email từ Intent nếu truyền qua Activity
                if (otp.isEmpty()) {
                    Toast.makeText(ForgotPasswordVerifyOtpActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    // Gọi hàm verifyOtp
                    verifyOtp(email, otp);
                }
            }
        });
    }
    // Bắt đầu đếm ngược thời gian
    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(OTP_VALIDITY_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdownTimer.setText("Time remaining: " + millisUntilFinished / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                tvCountdownTimer.setVisibility(View.GONE);
                tvResendOtp.setVisibility(View.VISIBLE); // Hiển thị "Resend OTP" khi hết thời gian
            }
        }.start();
    }
    // Gửi lại OTP qua API khi người dùng nhấn "Resend OTP"
    private void resendOtp(String email) {
        String url = ApiEndpoints.FORGOT_PASSWORD_SEND_OTP+ "/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        tvErrorMessage.setText("");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String status = response.getString("status");
                            if ("success".equals(status)) {
                                Toast.makeText(ForgotPasswordVerifyOtpActivity.this, "OTP resent successfully!", Toast.LENGTH_SHORT).show();
                                tvResendOtp.setVisibility(View.GONE); // Ẩn nút "Resend OTP"
                                startCountdownTimer(); // Bắt đầu lại đếm ngược
                            } else {
                                String errorMessage = response.getString("errorMessage");
                                tvErrorMessage.setText(errorMessage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvErrorMessage.setText("An error occurred while processing response.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvErrorMessage.setText("Failed to resend OTP. Please try again.");
                    }
                }
        );
        // Thiết lập thời gian chờ (timeout) cho yêu cầu
        int socketTimeout = 10000; // 10 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        requestQueue.add(jsonObjectRequest);
    }
    private void verifyOtp(String email, String otp) {
        String url = ApiEndpoints.FORGOT_PASSWORD_VERIFY_OTP + "/" + email + "?otp=" + otp;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Tạo JsonObjectRequest để gửi yêu cầu POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,  // Không cần body, dữ liệu đã nằm trong query params
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Xử lý phản hồi từ API
                            String status = response.getString("status");
                            if ("success".equals(status)) {
                                Toast.makeText(ForgotPasswordVerifyOtpActivity.this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                                // Chuyển hướng sang màn hình tiếp theo
                                 Intent intent = new Intent(ForgotPasswordVerifyOtpActivity.this, ForgotPasswordChangePassActivity.class);
                                 intent.putExtra("email", email);
                                 startActivity(intent);
                                finish(); // Kết thúc Activity sau khi thành công
                            } else {
                                // Hiển thị lỗi từ API
                                String errorMessage = response.getString("errorMessage");
                                tvErrorMessage.setText(errorMessage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvErrorMessage.setText("Opssssss!!! Có lỗi đã xảy ra.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi khi gọi API
                        tvErrorMessage.setText("Failed to verify OTP. Please try again.");
                    }
                }
        );

        // Thêm yêu cầu vào RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}