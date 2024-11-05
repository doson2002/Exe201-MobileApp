package app.foodpt.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseRole extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_role);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //End of On Create method

    public void goToSignUp(View view) {
        Intent intent = new Intent(ChooseRole.this, SignUpActivity.class);

        if (view.getId() == R.id.customer) {
            intent.putExtra("role_id", 3); // Role for customer
        } else if (view.getId() == R.id.business) {
            intent.putExtra("role_id", 2); // Role for business partner
        }

        startActivity(intent);
        finish();
    }
}