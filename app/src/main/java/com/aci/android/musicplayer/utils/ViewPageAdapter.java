package com.aci.android.musicplayer.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class ViewPageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    FragmentManager mManager;
    FragmentTransaction ft;

    public ViewPageAdapter(FragmentManager manager) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mManager = manager;
        ft = mManager.beginTransaction();


    }
    @NonNull
    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    public void replaceFragment(Fragment fragment, int index){
        mFragmentList.remove(index);
        mFragmentList.add(index, fragment);
        notifyDataSetChanged();
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
