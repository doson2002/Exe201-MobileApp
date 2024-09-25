package com.example.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button sign_in, sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

// Kiểm tra xem biến isLoggedIn có tồn tại không
        if (sharedPreferences.contains("isLoggedIn")) {
            // Biến isLoggedIn tồn tại, kiểm tra giá trị
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String role = sharedPreferences.getString("role", "");

            // Nếu người dùng đã đăng nhập
            if (isLoggedIn) {
                // Kiểm tra role của người dùng
                if (role != null && role.equalsIgnoreCase("ROLE_CUSTOMER")) {
                    // Người dùng là Customer -> chuyển tới HomePageActivity
                    Intent intent = new Intent(MainActivity.this, BottomNavHomePageActivity.class);
                    startActivity(intent);
                    finish();  // Kết thúc MainActivity
                } else if (role != null && role.equalsIgnoreCase("ROLE_PARTNER")) {
                    // Người dùng là Partner -> chuyển tới PartnerHomePageActivity
                    Intent intent = new Intent(MainActivity.this, PartnerHomePageActivity.class);
                    startActivity(intent);
                    finish();  // Kết thúc MainActivity
                } else {
                    // Nếu role không hợp lệ hoặc không tìm thấy, giữ lại ở MainActivity
                    // Không làm gì cả, giữ nguyên ở MainActivity
                }
            } else {
                // Người dùng chưa đăng nhập -> chuyển tới LoginActivity
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                finish();  // Kết thúc MainActivity
            }
        } else {
            // Biến isLoggedIn không tồn tại -> không làm gì cả, tiếp tục ở MainActivity
            // Không cần làm gì ở đây, giữ nguyên ở MainActivity
        }

    }
    //End of On Create method

    public void goToSignUp(View view) {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToChooseRole(View view){
        Intent intent = new Intent(MainActivity.this, ChooseRole.class);
        startActivity(intent);
        finish();
    }

     //ENd of Go To Sign Up activity.
     public void goToSignIn(View view) {
         Intent intent = new Intent(MainActivity.this, SignInActivity.class);
         startActivity(intent);
         finish();
     }
    //ENd of Go To Sign In activity.
    public void goToTest(View view){
        Intent intent = new Intent(MainActivity.this, PartnerHomePageActivity.class);
        startActivity(intent);
        finish();
    }
}