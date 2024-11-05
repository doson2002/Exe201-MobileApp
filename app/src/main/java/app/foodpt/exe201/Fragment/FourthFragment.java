package app.foodpt.exe201.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import app.foodpt.exe201.ChooseRole;
import app.foodpt.exe201.MainActivity;
import app.foodpt.exe201.R;

public class FourthFragment extends Fragment {
    public FourthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        // Lắng nghe sự kiện click
        view.findViewById(R.id.fourth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến fragment tiếp theo
                Intent intent = new Intent(getActivity(), ChooseRole.class);
                startActivity(intent);
            }
        });
        return view;    }
}