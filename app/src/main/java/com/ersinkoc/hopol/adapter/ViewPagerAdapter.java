package com.ersinkoc.hopol.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ersinkoc.hopol.fragment.Add;
import com.ersinkoc.hopol.fragment.Home;
import com.ersinkoc.hopol.fragment.Profile;
import com.ersinkoc.hopol.fragment.discoverFragment;
import com.ersinkoc.hopol.fragment.search;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int noOfTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int noOfTabs) {
        super(fm);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch(position){

            case 0:
               return new Home();

            case 1:
                return new search();

            case 2:
                return new Add();

            case 3:
                return new discoverFragment();

            case 4:
                return new Profile();



            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return  noOfTabs;
    }
}
