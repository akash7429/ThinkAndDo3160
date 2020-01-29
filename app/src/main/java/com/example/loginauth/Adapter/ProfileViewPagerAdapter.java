package com.example.loginauth.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.loginauth.Fragments.ProfileFragment;

public class ProfileViewPagerAdapter extends FragmentPagerAdapter {

    int size = 0;
    public ProfileViewPagerAdapter(FragmentManager fm, int size) {

        super(fm);
        this.size = size;
    }

    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new ProfileFragment();
                default:
                    return null;
        }
    }
    public int getCount(){
        return 0;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Posts";
            default:
                return null;
        }
    }
}
