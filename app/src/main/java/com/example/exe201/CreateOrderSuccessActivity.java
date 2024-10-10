package com.example.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exe201.DTO.FoodOrder;
import com.example.exe201.Fragment.Customer.HomeFragment;

public class CreateOrderSuccessActivity extends AppCompatActivity {

    private ImageView backArrow;
    private Button see_order_button, back_home_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_order_success);
        backArrow = findViewById(R.id.back_button);
        see_order_button = findViewById(R.id.see_order_button);
        back_home_button = findViewById(R.id.back_home_button);

        Intent intent = getIntent();
        FoodOrder foodOrder = intent.getParcelableExtra("foodOrder");

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        see_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateOrderSuccessActivity.this, FoodOrderDetailActivity.class);
                intent.putExtra("foodOrder",  foodOrder); // Truyền cartMap
                startActivity(intent);
            }
        });
        back_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateOrderSuccessActivity.this, HomeFragment.class);
                startActivity(intent);
            }
        });


    }
}