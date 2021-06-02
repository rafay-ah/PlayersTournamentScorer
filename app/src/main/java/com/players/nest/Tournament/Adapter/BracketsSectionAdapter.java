package com.players.nest.Tournament.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.players.nest.Tournament.Fragment.BracketsColumnFragment;
import com.players.nest.Tournament.Model.ColumnData;

import java.util.ArrayList;
import java.util.List;

public class BracketsSectionAdapter extends FragmentStatePagerAdapter{

    private List<ColumnData> sectionList;
    public BracketsSectionAdapter(@NonNull FragmentManager fm, List<ColumnData> list) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    sectionList = list;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("colomn_data", this.sectionList.get(position));
        BracketsColumnFragment fragment = new BracketsColumnFragment();
        bundle.putInt("section_number", position);
        if (position > 0)
            bundle.putInt("previous_section_size", sectionList.get(position - 1).getMatches().size());
        else if (position == 0)
            bundle.putInt("previous_section_size", sectionList.get(position).getMatches().size());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return this.sectionList.size();
    }
}
