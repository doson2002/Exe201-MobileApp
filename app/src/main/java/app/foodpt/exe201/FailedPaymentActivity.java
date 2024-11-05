package app.foodpt.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import app.foodpt.exe201.DTO.FoodOrder;

public class FailedPaymentActivity extends AppCompatActivity {
    private ImageView backArrow;
    private Button see_order_button, back_home_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_failed_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backArrow = findViewById(R.id.back_button);
        see_order_button = findViewById(R.id.see_order_button);
        back_home_button = findViewById(R.id.back_home_button);

        Intent intent = getIntent();
        int orderId = intent.getIntExtra("orderId",0);

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
                Intent intent = new Intent(FailedPaymentActivity.this, FoodOrderDetailActivity.class);
                intent.putExtra("orderId",  orderId); // Truyền cartMap
                startActivity(intent);
            }
        });
        back_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FailedPaymentActivity.this, BottomNavHomePageActivity.class);
                startActivity(intent);
            }
        });


    }
}