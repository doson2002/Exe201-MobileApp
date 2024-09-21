package com.example.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdatePartnerStep1Activity extends AppCompatActivity {

    private EditText restaurantName, description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_partner_step1);
        restaurantName = findViewById(R.id.restaurant_name);
        description = findViewById(R.id.description);
        Button nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdatePartnerStep1Activity.this, UpdatePartnerStep2Activity.class);

                // Pass the data to the second Activity
                intent.putExtra("restaurantName", restaurantName.getText().toString());
                intent.putExtra("description", description.getText().toString());
                startActivity(intent);
            }
        });
    }
}