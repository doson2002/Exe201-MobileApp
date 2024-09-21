package com.example.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.helpers.StringHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    Button sign_in_btn;
    EditText et_email, et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        et_email = findViewById(R.id.email);
        et_password = findViewById(R.id.password);

        sign_in_btn = findViewById(R.id.sign_in_btn);
        //Set Sign in Button On Click Listener;
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();
            }
        });

    }

    public void authenticateUser(){
        // Kiểm tra lỗi:
        if( !validateEmail()||!validatePassword()){
            return;
        }

        // Khởi tạo RequestQueue:
        RequestQueue queue = Volley.newRequestQueue(SignInActivity.this);
        // URL gửi đến:
        String url = ApiEndpoints.LOGIN_USER;

        //Set parameters
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email",et_email.getText().toString());
        params.put("password",et_password.getText().toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    // Lấy thông tin từ response
                    String full_name = response.optString("name","");
                    String email = response.optString("email","");
                    String phone = response.optString("phone","");
                    String token = response.optString("token","");
                    String img_url = response.optString("img_url", "");
                    int gender = response.getInt("gender");
                    int userId = response.getInt("id");
                    boolean firstLogin = response.getBoolean("first_login");


                    // Lưu token vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("JwtToken", token);
                    editor.putInt("user_id", userId);
                    editor.putString("email", email);
                    editor.putString("phone",phone);
                    editor.putString("full_name",full_name);
                    editor.putString("img_url",img_url);
                    editor.putInt("gender",gender);
                    // Lưu token hoặc trạng thái đăng nhập
                    editor.putBoolean("isLoggedIn", true);
                    // Lấy ra mảng roles
                    JSONArray rolesArray = response.getJSONArray("roles");
                    // Truy xuất phần tử đầu tiên của roles
                    String role = rolesArray.getString(0);
                    editor.putString("role",role);
                    editor.apply();
// Kiểm tra vai trò đầu tiên trong mảng roles
                    if(role != null && !role.isEmpty()) {
                        if(role.equalsIgnoreCase("ROLE_PARTNER")) {

                            if(!firstLogin){
                                Intent goToProfile = new Intent(SignInActivity.this, HomePageActivity.class);
                                // Pass Values To Profile Activity:
                                goToProfile.putExtra("full_name", full_name);
                                goToProfile.putExtra("email", email);
                                goToProfile.putExtra("phone", phone);
                                // Start Activity:
                                startActivity(goToProfile);
                            }else{
                                Intent goToUpdatePartnerStep1 = new Intent(SignInActivity.this, UpdatePartnerStep1Activity.class);
                                goToUpdatePartnerStep1.putExtra("role", role);
                                startActivity(goToUpdatePartnerStep1);
                            }



                        } else if (role.equalsIgnoreCase("ROLE_CUSTOMER")) {
                            // Set Intent for go to profile
                            Intent goToProfile = new Intent(SignInActivity.this, ProfileActivity.class);
                            // Start Activity:
                            startActivity(goToProfile);
                        }
                    }
                    finish();



                }catch (JSONException e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.e("VolleyError", "Timeout Error: " + error.toString());
                } else if (error instanceof NoConnectionError) {
                    Log.e("VolleyError", "No Connection Error: " + error.toString());
                } else if (error instanceof AuthFailureError) {
                    Log.e("VolleyError", "Authentication Failure Error: " + error.toString());
                } else if (error instanceof ServerError) {
                    Log.e("VolleyError", "Server Error: " + error.toString());
                } else if (error instanceof NetworkError) {
                    Log.e("VolleyError", "Network Error: " + error.toString());
                } else if (error instanceof ParseError) {
                    Log.e("VolleyError", "Parse Error: " + error.toString());
                } else {
                    Log.e("VolleyError", "Unknown Error: " + error.toString());
                }
                error.printStackTrace();
                System.out.println(error.getMessage());
                Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // Thời gian chờ tối đa (30 giây)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Số lần thử lại
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);

    }
    public void goToHome(View view){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void goToSignUpAct(View view){
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }




    public boolean validateEmail(){
        String email = et_email.getText().toString();
        // Check If Email Is Empty:
        if(email.isEmpty()){
            et_email.setError("Email cannot be empty!");
            return false;
        }else if(!StringHelper.regexEmailValidationPattern(email)){
            et_email.setError("Please enter a valid email");
            return false;
        }else{
            et_email.setError(null);
            return true;
        }// Check If Email Is Empty.
    }
    // End Of Validate Email Field.
    public boolean validatePassword(){
        String password_p = et_password.getText().toString();

        // Check If Password and Confirm Field Is Empty:
        if(password_p.isEmpty()){
            et_password.setError("Password cannot be empty!");
            return false;
        }else{
            et_password.setError(null);
            return true;
        }// Check Password  Is Empty.
    }
}
//ENd sign in