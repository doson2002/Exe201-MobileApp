package app.foodpt.exe201.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import app.foodpt.exe201.Fragment.Customer.ChatFragment;
import app.foodpt.exe201.Fragment.Customer.NotificationFragment;

public class MessagePagerAdapter extends FragmentStateAdapter {

    public MessagePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatFragment(); // Fragment chat
            case 1:
                return new NotificationFragment(); // Fragment notification
            default:
                return new ChatFragment(); // Mặc định là ChatFragment
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Tổng số Fragment con
    }
}