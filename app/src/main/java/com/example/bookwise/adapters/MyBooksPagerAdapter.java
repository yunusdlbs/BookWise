package com.example.bookwise.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bookwise.fragments.FavoriKitaplarFragment;
import com.example.bookwise.fragments.OduncKitaplarFragment;

public class MyBooksPagerAdapter extends FragmentStateAdapter {
    public MyBooksPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new FavoriKitaplarFragment() : new OduncKitaplarFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
