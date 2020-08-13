package com.mask.bond;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mask.bond.StudentChatFragment;
import com.mask.bond.StudentTeachersFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
       switch(position){
           case 0: return new StudentChatFragment();
           case 1: return new StudentTeachersFragment();
       }
       return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Chat";
            case 1: return "Teachers";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}