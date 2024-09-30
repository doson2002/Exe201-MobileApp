package com.example.exe201;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.exe201.Fragment.Customer.ActivityFragment;
import com.example.exe201.Fragment.Customer.HomeFragment;
import com.example.exe201.Fragment.Customer.NotificationFragment;
import com.example.exe201.Fragment.Customer.ProfileFragment;
import com.example.exe201.Fragment.Partner.MessageFragment;
import com.example.exe201.Fragment.Partner.PartnerHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavPartnerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottom_nav_partner_home);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Fragment mặc định khi khởi tạo Activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_partner,
                    new PartnerHomeFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        replaceFragment(new PartnerHomeFragment());
                        break;
                    case R.id.nav_message:
                        replaceFragment(new MessageFragment());
                        break;

                }
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_partner, fragment);
        fragmentTransaction.commit();
    }
}