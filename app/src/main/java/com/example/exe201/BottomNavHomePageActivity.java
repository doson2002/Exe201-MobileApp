package com.example.exe201;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.exe201.Fragment.Customer.ActivityFragment;
import com.example.exe201.Fragment.Customer.CustomerMessageFragment;
import com.example.exe201.Fragment.Customer.HomeFragment;
import com.example.exe201.Fragment.Customer.NotificationFragment;
import com.example.exe201.Fragment.Customer.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottom_nav_home_page);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

        // Lưu tiêu đề gốc
        String[] originalTitles = new String[bottomNavigationView.getMenu().size()];
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            originalTitles[i] = menuItem.getTitle().toString();
        }
        // Set Fragment mặc định khi khởi tạo Activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                    new HomeFragment()).commit();
            // Chọn item Home mặc định
            bottomNavigationView.setSelectedItemId(R.id.action_home);
            // Ẩn tiêu đề của action_home
            MenuItem homeItem = bottomNavigationView.getMenu().findItem(R.id.action_home);
            if (homeItem != null) {
                homeItem.setTitle(""); // Ẩn tiêu đề
                // Căn giữa icon bằng cách thêm padding nếu cần
                View homeItemView = bottomNavigationView.findViewById(homeItem.getItemId());
                if (homeItemView != null) {
                    homeItemView.setPadding(0, 20, 0, 20); // Điều chỉnh padding theo ý muốn
                }
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Duyệt qua tất cả các item của BottomNavigationView
                for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                    MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
                    View menuItemView = bottomNavigationView.findViewById(menuItem.getItemId());

                    if (menuItem.getItemId() == item.getItemId()) {
                        // Khi item được chọn, ẩn tiêu đề
                        menuItem.setTitle("");
                        // Căn giữa icon bằng cách thêm padding
                        if (menuItemView != null) {
                            menuItemView.setPadding(0, 20, 0, 20); // Điều chỉnh padding theo ý muốn
                        }
                    } else {
                        // Khi item không được chọn, khôi phục tiêu đề gốc
                        menuItem.setTitle(originalTitles[i]);
                        menuItemView.setPadding(0, 0, 0, 0); // Điều chỉnh padding theo ý muốn

                    }
                }
                // Reset vị trí cho tất cả các mục về vị trí ban đầu (TranslationY = 0)
                for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                    // Tìm view của mục trong menu bằng ID
                    View view = bottomNavigationView.findViewById(bottomNavigationView.getMenu().getItem(i).getItemId());
                    // Đưa mục trở về vị trí cũ (y = 0)
                    if (view != null) {
                        view.animate().translationY(0).setDuration(150).start();  // Reset vị trí về gốc
                        view.setElevation(0); // Đặt độ cao về 0
                    }
                }

                // Thêm hiệu ứng nhảy cho mục được chọn
                View selectedItem = bottomNavigationView.findViewById(item.getItemId());
                selectedItem.animate().translationY(-40).setDuration(300).setInterpolator(new OvershootInterpolator()).start();
                selectedItem.setElevation(5); // Đặt độ cao cho mục được chọn
                switch (item.getItemId()) {
                    case R.id.action_home:
                        replaceFragment(new HomeFragment());
                        break;
                    case R.id.action_activity:
                        replaceFragment(new ActivityFragment());
                        break;
                    case R.id.action_notifications:
                        replaceFragment(new CustomerMessageFragment());
                        break;
                    case R.id.action_profile:
                        replaceFragment(new ProfileFragment());
                        break;
                }
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}