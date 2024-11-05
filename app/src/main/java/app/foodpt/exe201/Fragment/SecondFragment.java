package app.foodpt.exe201.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import app.foodpt.exe201.R;

public class SecondFragment extends Fragment {
    private ViewPager2 viewPager;
    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        // Lấy đối tượng ViewPager2 từ Activity
        viewPager = getActivity().findViewById(R.id.viewPager);
        // Lắng nghe sự kiện click
        view.findViewById(R.id.second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến fragment tiếp theo
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
        return view;
    }
}