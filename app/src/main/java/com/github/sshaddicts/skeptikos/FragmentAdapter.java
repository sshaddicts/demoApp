package com.github.sshaddicts.skeptikos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.github.sshaddicts.skeptikos.fragments.LogInFragment;
import com.github.sshaddicts.skeptikos.fragments.SelectPictureFragment;

public class FragmentAdapter extends FragmentStatePagerAdapter{

    private int pagesNumber;

    public FragmentAdapter(FragmentManager fm, int totalPageNumber) {
        super(fm);
        this.pagesNumber = totalPageNumber;
    }

    @Override
    public Fragment getItem(int position) {
            switch (position){
                case 1: return new LogInFragment();
                case 2: return new SelectPictureFragment();
                default: return new LogInFragment();
            }
    }

    @Override
    public int getCount() {
        return pagesNumber;
    }
}
