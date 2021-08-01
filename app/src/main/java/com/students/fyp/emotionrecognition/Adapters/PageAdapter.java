package com.students.fyp.emotionrecognition.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.students.fyp.emotionrecognition.Fragments.AllTracksFragment;
import com.students.fyp.emotionrecognition.Fragments.MoodFragment;
import com.students.fyp.emotionrecognition.Fragments.PlayListFragment;

public class PageAdapter  extends FragmentStatePagerAdapter {

    int mNoOfTabs;
    public PageAdapter(@NonNull FragmentManager fm, int Numberoftabs) {
        super(fm);
        this.mNoOfTabs=Numberoftabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                AllTracksFragment allTracksFragment =new AllTracksFragment();
                return allTracksFragment;
            case 1:
                MoodFragment moodFragment=new MoodFragment();
                return moodFragment;
            case 2:
                PlayListFragment playListFragment=new PlayListFragment();
                return playListFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}

