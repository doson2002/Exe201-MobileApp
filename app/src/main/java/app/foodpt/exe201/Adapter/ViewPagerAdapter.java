package app.foodpt.exe201.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import app.foodpt.exe201.Fragment.FirstFragment;
import app.foodpt.exe201.Fragment.FourthFragment;
import app.foodpt.exe201.Fragment.SecondFragment;
import app.foodpt.exe201.Fragment.ThirdFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<Fragment> fragmentList;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,List<Fragment> fragmentList) {

        super(fragmentActivity);
        this.fragmentList = fragmentList;

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }



    @Override
    public int getItemCount() {
        return  fragmentList.size();
    }
}