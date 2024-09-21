package com.example.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);

        // Tìm kiếm LinearLayout bằng ID
        LinearLayout layoutCashPayment = findViewById(R.id.layoutCashPayment);

        // Đặt OnClickListener cho LinearLayout
        layoutCashPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi người dùng nhấn vào layout này, chuyển đến Activity mới
                Intent intent = new Intent(OrderActivity.this, PaymentMethodActivity.class);
                startActivity(intent);
            }
        });
    }
}