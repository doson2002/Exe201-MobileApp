package app.foodpt.exe201;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

import app.foodpt.exe201.Adapter.ViewPagerAdapter;
import app.foodpt.exe201.Fragment.FirstFragment;
import app.foodpt.exe201.Fragment.FourthFragment;
import app.foodpt.exe201.Fragment.SecondFragment;
import app.foodpt.exe201.Fragment.ThirdFragment;

public class BeginActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    private WormDotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_begin);

        viewPager = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        // Tạo danh sách các fragment layout cho ViewPager2
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new FirstFragment());
        fragments.add(new SecondFragment());
        fragments.add(new ThirdFragment());
        fragments.add(new FourthFragment());

        // Adapter cho ViewPager2
        adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Liên kết ViewPager2 với DotsIndicator
        dotsIndicator.setViewPager2(viewPager);
    }
}