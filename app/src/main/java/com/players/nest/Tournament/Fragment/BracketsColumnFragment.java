package com.players.nest.Tournament.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.ModelClasses.TournamentMatches;
import com.players.nest.Tournament.Adapter.BracketsCellAdapter;
import com.players.nest.Tournament.Model.ColumnData;
import com.players.nest.Tournament.Model.MatchData;
import com.players.nest.R;
import com.players.nest.Tournament.Util.BracketsUtility;

import java.util.ArrayList;

public class BracketsColumnFragment extends Fragment {
    private ColumnData columnData;
    private int sectionNumber = 0;
    private int previousBracketSize;
    private ArrayList<MatchData> list;
    private RecyclerView bracketsRV;
    private BracketsCellAdapter  adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brackets_colomn,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        getExtras();
        initAdapter();
        
    }

    private void initAdapter() {
        adapter = new BracketsCellAdapter(this, getContext(), list);
        if (bracketsRV != null) {
            bracketsRV.setHasFixedSize(true);
            bracketsRV.setNestedScrollingEnabled(false);
            bracketsRV.setAdapter(adapter);
            bracketsRV.smoothScrollToPosition(0);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            bracketsRV.setLayoutManager(layoutManager);
            bracketsRV.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private void getExtras() {
        if (getArguments() != null) {
            list = new ArrayList<>();
            columnData = (ColumnData) getArguments().getSerializable("colomn_data");
            sectionNumber = getArguments().getInt("section_number");
            previousBracketSize = getArguments().getInt("previous_section_size");
            list.addAll(columnData.getMatches());
            setInitialHeightForList();
        }
    }


    private void initViews() {
        bracketsRV = (RecyclerView) getView().findViewById(R.id.rv_score_board);
    }


    private void setInitialHeightForList() {
        int i=1;
        for (MatchData data : list){
            if (sectionNumber == 0){
                data.setHeight(BracketsUtility.dpToPx(118*(sectionNumber+1)));
            }else if (sectionNumber == 1 && previousBracketSize != list.size()){
                data.setHeight(BracketsUtility.dpToPx((int) (118*Math.round(Math.pow(2,sectionNumber)))));
            }else if (sectionNumber == 1 && previousBracketSize == list.size()) {
                data.setHeight(BracketsUtility.dpToPx((int) (118*Math.round(Math.pow(2,sectionNumber)))));
            } else if (previousBracketSize > list.size()) {
                data.setHeight(BracketsUtility.dpToPx((int) (118*Math.round(Math.pow(2,sectionNumber)))));
            }else if (previousBracketSize == list.size()) {
                data.setHeight(BracketsUtility.dpToPx((int) (118*Math.round(Math.pow(2,sectionNumber)))));
            }
        }
    }
    public ArrayList<MatchData> getColumnList() {
        return list;
    }
    public int getSectionNumber() {
        return sectionNumber;
    }
    public void expandHeight(int height) {

        for (MatchData data : list) {
            data.setHeight(height);
        }
        adapter.setList(list);
    }

    public void shrinkView(int height) {
        for (MatchData data : list) {
            data.setHeight(height);
        }
        adapter.setList(list);
    }
    public int getCurrentBracketSize() {
        return list.size();
    }
    public int getPreviousBracketSize() {
        return previousBracketSize;
    }

}
