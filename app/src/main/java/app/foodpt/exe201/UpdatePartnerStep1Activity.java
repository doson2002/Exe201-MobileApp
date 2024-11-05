package app.foodpt.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdatePartnerStep1Activity extends AppCompatActivity {

    private EditText restaurantName, description;
    private String restaurantNameString = "";
    private String descriptionString = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_partner_step1);
        View rootView = findViewById(R.id.root_view);
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
        restaurantName = findViewById(R.id.restaurant_name);
        description = findViewById(R.id.description);
        Button nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lấy dữ liệu từ các EditText
                restaurantNameString = restaurantName.getText().toString().trim();
                descriptionString = description.getText().toString().trim();
                // Kiểm tra nếu các trường rỗng
                if (restaurantNameString.isEmpty()) {
                    restaurantName.setError("Tên nhà hàng không được để trống");
                    return;
                }

                if (descriptionString.isEmpty()) {
                    description.setError("Mô tả không được để trống");
                    return;
                }
                Intent intent = new Intent(UpdatePartnerStep1Activity.this, UpdatePartnerStep2Activity.class);
                // Pass the data to the second Activity
                intent.putExtra("restaurantName", restaurantNameString);
                intent.putExtra("description", descriptionString);
                startActivity(intent);
            }
        });
    }
}