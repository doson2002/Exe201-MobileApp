package com.example.exe201.Fragment.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.exe201.Adapter.MessagePagerAdapter;
import com.example.exe201.R;
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

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        pagerAdapter = new MessagePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Thiết lập TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Chat");
                            break;
                        case 1:
                            tab.setText("Notification");
                            break;
                    }
                }).attach();

        return view;
    }
}
