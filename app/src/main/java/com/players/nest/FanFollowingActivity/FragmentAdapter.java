package com.players.nest.FanFollowingActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.players.nest.FanFollowingActivity.Fragments.FansFragment;
import com.players.nest.FanFollowingActivity.Fragments.FollowingFragment;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.ModelClasses.User;

public class FragmentAdapter extends FragmentPagerAdapter {

    String[] tabTitles = {"Fans", "Following"};
    User user;
    int tabCount;

    public FragmentAdapter(@NonNull FragmentManager fm, int behavior, User user) {
        super(fm, behavior);
        tabCount = behavior;
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            FansFragment fragment = new FansFragment();
            fragment.getDataFromActivity(user);
            return fragment;
        } else {
            FollowingFragment fragment2 = new FollowingFragment();
            fragment2.getDataFromActivity(Constants.FANS_FOLLOWING_ACTIVITY, user);
            return fragment2;
        }
    }


    @Override
    public int getCount() {
        return tabCount;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
