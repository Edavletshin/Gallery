package com.example.testsport.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

import com.example.testsport.fragments.FavouritesList;
import com.example.testsport.fragments.PhotosList;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public TabsAdapter(FragmentManager fm) {
        super(fm);

        mFragmentList.add(new PhotosList());
        mFragmentList.add(new FavouritesList());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0: return "Images";
            case 1: return "Favorite";
        }
        return "";
    }

    public FavouritesList getFavouritesList()
    {
        return ((FavouritesList)mFragmentList.get(1));
    }

    public PhotosList getPhotosList()
    {
        return ((PhotosList)mFragmentList.get(0));
    }

}