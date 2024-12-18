package app.foodpt.exe201.Fragment.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import app.foodpt.exe201.Adapter.MessagePagerAdapter;
import app.foodpt.exe201.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CustomerMessageFragment extends Fragment {
    private ViewPager2 viewPager;
    private MessagePagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_message, container, false);
        View rootView = view.findViewById(R.id.root_view);
        // Thiết lập WindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                // Áp dụng padding để tránh bị thanh hệ thống che
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), 0);
                return insets.consumeSystemWindowInsets();
            }
        });
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        pagerAdapter = new MessagePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Thiết lập TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Trò chuyện");
                            break;
                        case 1:
                            tab.setText("Thông báo");
                            break;
                    }
                }).attach();

        return view;
    }
}
