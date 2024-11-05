package app.foodpt.exe201;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordChangePassActivity extends AppCompatActivity {
    private EditText edPassword, edConfirm;
    private Button btnChangePassword;
    private TextView tvErrorMessage;
    private String email;  // This will be passed from VerifyOtpActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_change_pass);

        edPassword = findViewById(R.id.edPassword);
        edConfirm = findViewById(R.id.edConfirm);
        btnChangePassword = findViewById(R.id.verifyOtp);
        tvErrorMessage = findViewById(R.id.tvErrorMessage); // Add this to the layout if you want to show error messages

        // Get the email passed from VerifyOtpActivity
        email = getIntent().getStringExtra("email");

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnChangePassword.setText("ĐANG CẬP NHẬT");
                btnChangePassword.setTextColor(Color.BLACK);
                // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                btnChangePassword.setEnabled(false);
                changePassword(email);

            }
        });
    }
    private void changePassword(String email) {
        String password = edPassword.getText().toString();
        String confirmPassword = edConfirm.getText().toString();

        // Kiểm tra nếu mật khẩu trống hoặc không trùng khớp
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            tvErrorMessage.setText("Password không được trống");
            return;
        }
        if (!password.equals(confirmPassword)) {
            tvErrorMessage.setText("Passwords không khớp");
            return;
        }

        // Tạo đối tượng JSON cho yêu cầu đổi mật khẩu
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("password", password);
            jsonRequest.put("retypePassword", confirmPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // URL của API đổi mật khẩu
        String url = ApiEndpoints.FORGOT_PASSWORD_CHANGE_PASSWORD +"/"+ email;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Tạo yêu cầu POST sử dụng Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                // Nếu đổi mật khẩu thành công, chuyển hướng đến màn hình đăng nhập hoặc màn hình chính
                                Toast.makeText(ForgotPasswordChangePassActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPasswordChangePassActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMessage = response.getString("errorMessage");
                                tvErrorMessage.setText(errorMessage);
                                btnChangePassword.setText("CẬP NHẬT");
                                btnChangePassword.setTextColor(Color.WHITE);
                                btnChangePassword.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvErrorMessage.setText("An unexpected error occurred.");
                        btnChangePassword.setText("CẬP NHẬT");
                        btnChangePassword.setTextColor(Color.WHITE);
                        btnChangePassword.setEnabled(true);
                        Log.e("VolleyError", error.toString());
                    }
                });
        // Thiết lập thời gian chờ (timeout) cho yêu cầu
        int socketTimeout = 10000; // 10 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        // Thực hiện request
        requestQueue.add(jsonObjectRequest);
    }
}
