package app.foodpt.exe201;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.helpers.StringHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class SignUpActivity extends AppCompatActivity {

    EditText full_name, email, phone, password, confirm_password;
    Button sign_up_btn;
    Spinner gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        View rootView = findViewById(R.id.main);
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


        // Hook Edit Text Fields:
        full_name = findViewById(R.id.full_name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirm_password= findViewById(R.id.confirm_password);
        gender = findViewById(R.id.gender_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, R.layout.gender_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        // Hook Sign Up Button:
        sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_up_btn.setText("ĐANG TẠO");
                sign_up_btn.setTextColor(Color.BLACK);
                // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                sign_up_btn.setEnabled(false);
                processFormFields();
            }
        });

    }
    // End Of On Create Method.
    public void goToHome(View view){
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void goToSignInAct(View view){
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    // Phương thức để xử lý các trường form
    public void processFormFields() {
        // Kiểm tra lỗi:
         if(!validateFullName() || !validateEmail()||!validatePasswordAndConfirm()||!validatePhone()){
             sign_up_btn.setText("ĐĂNG KÝ");
             sign_up_btn.setTextColor(Color.WHITE);
             // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
             sign_up_btn.setEnabled(true);
             return;
         }
        // End Of Check For Errors.

        // Khởi tạo RequestQueue:
        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
        // URL gửi đến:
        String url = ApiEndpoints.CREATE_USER;

        // Tạo JSONObject chứa dữ liệu bạn muốn gửi
        JSONObject jsonBody = new JSONObject();
        try {
            // Lấy giá trị role_id từ Intent
            Intent intent = getIntent();
            int roleId = intent.getIntExtra("role_id", 0); // 0 là giá trị mặc định nếu không có role_id
            // Giả sử gender là Spinner đã được khởi tạo và gán giá trị
            String selectedGender = gender.getSelectedItem().toString();
            int genderValue;

            switch (selectedGender) {
                case "Male":
                    genderValue = 0;
                    break;
                case "Female":
                    genderValue = 1;
                    break;
                case "Other":
                    genderValue = 2;
                    break;
                default:
                    genderValue = 0; // Hoặc xử lý trường hợp không xác định
                    break;
            }

            jsonBody.put("full_name", full_name.getText().toString());
            jsonBody.put("email", email.getText().toString());
            jsonBody.put("phone_number", phone.getText().toString());
            jsonBody.put("password", password.getText().toString());
            jsonBody.put("gender", genderValue); // Hoặc lấy giá trị từ UI nếu cần
            jsonBody.put("role_id", roleId); // Hoặc lấy giá trị từ UI nếu cần
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SignUpActivity.this, "Error creating JSON object", Toast.LENGTH_LONG).show();
            return;
        }

        // Tạo JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Log phản hồi từ server
                            Log.d("API_RESPONSE", "Response: " + response.toString());

                            // Phân tích phản hồi JSON:
                            String message = response.getString("message");
                            if (message.equalsIgnoreCase("Đăng ký tài khoản thành công")) {
                                // Reset các trường dữ liệu:
                                full_name.setText(null);
                                phone.setText(null);
                                email.setText(null);
                                password.setText(null);
                                confirm_password.setText(null);

                                // Hiển thị thông báo thành công:
                                Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                            } else {
                                // Hiển thị thông báo lỗi:
                                Toast.makeText(SignUpActivity.this, "Unexpected response: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SignUpActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                        sign_up_btn.setText("ĐĂNG KÝ");
                        sign_up_btn.setTextColor(Color.WHITE);
                        // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                        sign_up_btn.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace(); // In ra stack trace để kiểm tra lỗi

                        String errorMessage = "Unknown error occurred"; // Giá trị mặc định nếu không có thông báo lỗi

                        // Kiểm tra nếu phản hồi lỗi có chứa dữ liệu từ server
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                // Lấy chuỗi JSON từ dữ liệu lỗi
                                String responseBody = new String(error.networkResponse.data, "utf-8");

                                // Log phản hồi lỗi từ server
                                Log.d("API_ERROR_RESPONSE", "Error Response: " + responseBody);
                                if (responseBody != null && !responseBody.isEmpty()) {
                                    try {
                                        JSONObject data = new JSONObject(responseBody);
                                        errorMessage = data.optString("message", "Unknown error");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        errorMessage = "Email đã tồn tại, vui lòng chọn email khác";
                                    }
                                } else {
                                    // Nếu phản hồi rỗng hoặc null
                                    errorMessage = "Server returned an empty response";
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                errorMessage = "Encoding error";
                            }
                        } else {
                            // Nếu không có dữ liệu từ server, hiển thị mã lỗi (nếu có)
                            errorMessage = error.getMessage();
                        }

                        // Hiển thị thông báo lỗi chi tiết với Toast
                        Toast.makeText(SignUpActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        sign_up_btn.setText("ĐĂNG KÝ");
                        sign_up_btn.setTextColor(Color.WHITE);
                        // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                        sign_up_btn.setEnabled(true);
                    }
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Thêm yêu cầu vào hàng đợi
        queue.add(jsonObjectRequest);
    }
    // End Of Process Form Fields Method.

    public boolean validateFullName(){
        String fullName = full_name.getText().toString();
        if(fullName.isEmpty()){
            full_name.setError("Full name cannot be empty!");
            return false;
        }else{
            full_name.setError(null);
            return true;
        }
    }
    //End Of Validate FullName

    public boolean validateEmail(){
        String email_e = email.getText().toString();
        // Check If Email Is Empty:
        if(email_e.isEmpty()){
            email.setError("Email cannot be empty!");
            return false;
        }else if(!StringHelper.regexEmailValidationPattern(email_e)){
            email.setError("Please enter a valid email");
            return false;
        }else{
            email.setError(null);
            return true;
        }// Check If Email Is Empty.
    }
    // End Of Validate Email Field.
    public boolean validatePhone(){
        String phoneNum = phone.getText().toString();
        // Check If Email Is Empty:
        if(phoneNum.isEmpty()){
            phone.setError("Số điện thoại không được để trống!");
            return false;
        }else if(!StringHelper.regexPhoneValidationPattern(phoneNum)){
            phone.setError("Vui lòng nhập số điện thoại hợp lệ");
            return false;
        }else{
            phone.setError(null);
            return true;
        }// Check If Email Is Empty.
    }
    public boolean validatePasswordAndConfirm(){
        String password_p = password.getText().toString();
        String confirm_p = confirm_password.getText().toString();

        // Check If Password and Confirm Field Is Empty:
        if(password_p.isEmpty()){
            password.setError("Password cannot be empty!");
            return false;
        }else if (!password_p.equals(confirm_p)){
            password.setError("Passwords do not match!");
            return false;
        }else if(confirm_p.isEmpty()){
            confirm_password.setError("Confirm field cannot be empty!");
            return false;
        }else{
            password.setError(null);
            confirm_password.setError(null);
            return true;
        }// Check Password and Confirm Field Is Empty.
    }
    // End Of Validate Password and Confirm Field.

}