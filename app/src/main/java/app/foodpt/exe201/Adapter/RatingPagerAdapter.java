package app.foodpt.exe201.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import app.foodpt.exe201.Fragment.Partner.HighRatingFragment;
import app.foodpt.exe201.Fragment.Partner.LowRatingFragment;

import java.util.List;

public class RatingPagerAdapter extends FragmentStateAdapter {

    private List<String> lowRatingComments;
    private List<String> highRatingComments;

    public RatingPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> lowRatingComments, List<String> highRatingComments) {
        super(fragmentActivity);
        this.lowRatingComments = lowRatingComments;
        this.highRatingComments = highRatingComments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LowRatingFragment(lowRatingComments);
            case 1:
                return new HighRatingFragment(highRatingComments);
            default:
                return new LowRatingFragment(lowRatingComments);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}