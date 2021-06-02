package com.players.nest.Tournament.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.exoplayer2.C;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.ModelClasses.Scores;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.TournamentMatches;
import com.players.nest.Tournament.Adapter.BracketsSectionAdapter;
import com.players.nest.Tournament.CustomViews.WrapContentViewPager;
import com.players.nest.Tournament.Model.ColumnData;
import com.players.nest.R;
import com.players.nest.Tournament.Model.CompetitorData;
import com.players.nest.Tournament.Model.MatchData;
import com.players.nest.Tournament.Util.BracketsUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BracketsFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private WrapContentViewPager viewPager;
    private List<ColumnData> sectionList;
    private int mNextSelectedScreen;
    private int mCurrentPagerState;
    private BracketsSectionAdapter sectionAdapter;
    private TournamentDetail tournamentDetail;
    public BracketsFragment(TournamentDetail tournamentDetail){
        this.tournamentDetail = tournamentDetail;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brackets,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        sectionList = new ArrayList<>();
        setData(tournamentDetail);
        intialiseViewPagerAdapter();
        setValueListener();

    }

    private void setValueListener() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tournament")
                .child(tournamentDetail.getTournamentID());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TournamentDetail tournamentDetail = snapshot.getValue(TournamentDetail.class);
                    sectionList.clear();
                    setData(tournamentDetail);
                    sectionAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews() {
        viewPager =(WrapContentViewPager) Objects.requireNonNull(getView()).findViewById(R.id.container);
    }

    private void setData(TournamentDetail tournamentDetail) {



        List<TournamentMatches> tournamentMatchesList = tournamentDetail.getMatchDetails();
        List<MatchData> columnMatchList1 = new ArrayList<>();
        List<MatchData> columnMatchList2 = new ArrayList<>();
        List<MatchData> columnMatchList3 = new ArrayList<>();
        List<MatchData> columnMatchList4 = new ArrayList<>();
        List<MatchData> columnMatchList5 = new ArrayList<>();
        List<MatchData> columnMatchList6 = new ArrayList<>();
        List<MatchData> columnMatchList7 = new ArrayList<>();
        List<MatchData> columnMatchList8 = new ArrayList<>();
        for(int i=0;i<tournamentMatchesList.size();i++){
            TournamentMatches tournamentMatches = tournamentMatchesList.get(i);
            Scores scores = tournamentMatches.getScores();
            String userOne = tournamentMatches.getUserOneId();
            String userTwo = tournamentMatches.getUserTwoId();
            String userOneScore="-";
            String userTwoScore="-";
            if(!TextUtils.isEmpty(userOne) && scores != null && scores.getUserID().equals(userOne)){
                userOneScore = scores.getUserScore()+"";
                userTwoScore = scores.getOpponentScore()+"";
            }
            else if(!TextUtils.isEmpty(userTwo) && scores != null && scores.getUserID().equals(userTwo)){
                userOneScore = scores.getOpponentScore()+"";
                userTwoScore = scores.getUserScore()+"";
            }
            CompetitorData competitorOne = new CompetitorData(userOne, userOneScore,"45");
            CompetitorData competitorTwo = new CompetitorData(userTwo, userTwoScore,"45");
            switch (tournamentMatches.getSectionNumber()) {
                case 0:
                    MatchData matchData1 = new MatchData(competitorOne, competitorTwo);
                    columnMatchList1.add(matchData1);
                    break;
                case 1:
                    MatchData matchData2 = new MatchData(competitorOne, competitorTwo);
                    columnMatchList2.add(matchData2);
                    break;
                case 2:
                    MatchData matchData3 = new MatchData(competitorOne, competitorTwo);
                    columnMatchList3.add(matchData3);
                    break;
                case 3:
                    MatchData matchData4 = new MatchData(competitorOne, competitorTwo);
                    columnMatchList4.add(matchData4);
                    break;
                case 4:
                    MatchData matchData5 = new MatchData(competitorOne, competitorTwo);
                    columnMatchList5.add(matchData5);
                    break;
                case 5:
                    MatchData matchData6 = new MatchData(competitorOne, competitorTwo);
                    columnMatchList6.add(matchData6);
                    break;
                case 6:
                    MatchData matchData7 = new MatchData(competitorOne, competitorTwo);
                    columnMatchList7.add(matchData7);
                    break;
                case 7:
                    MatchData matchData8 = new MatchData(competitorOne, competitorTwo);
                    columnMatchList8.add(matchData8);
                    break;
                default:
                    break;
            }

        }
        if(columnMatchList1.size()!=0){
            ColumnData columnData = new ColumnData(columnMatchList1);
            sectionList.add(columnData);
        }
        if(columnMatchList2.size()!=0){
            ColumnData columnData = new ColumnData(columnMatchList2);
            sectionList.add(columnData);
        }
        if(columnMatchList3.size()!=0){
            ColumnData columnData = new ColumnData(columnMatchList3);
            sectionList.add(columnData);
        }
        if(columnMatchList4.size()!=0){
            ColumnData columnData = new ColumnData(columnMatchList4);
            sectionList.add(columnData);
        }
        if(columnMatchList5.size()!=0){
            ColumnData columnData = new ColumnData(columnMatchList5);
            sectionList.add(columnData);
        }
        if(columnMatchList6.size()!=0){
            ColumnData columnData = new ColumnData(columnMatchList6);
            sectionList.add(columnData);
        }
        if(columnMatchList7.size()!=0){
            ColumnData columnData = new ColumnData(columnMatchList7);
            sectionList.add(columnData);
        }
        if(columnMatchList8.size()!=0){
            ColumnData columnData = new ColumnData(columnMatchList8);
            sectionList.add(columnData);
        }


//        CompetitorData competitorOne = new CompetitorData("Manchester United Fc", "2","45");
//        CompetitorData competitorTwo = new CompetitorData("Arsenal", "1","45");
//        CompetitorData competitorThree = new CompetitorData("Chelsea", "2","45")11;
//        CompetitorData competitorFour = new CompetitorData("Tottenham", "1","45");
//        CompetitorData competitorFive = new CompetitorData("Manchester FC", "2","45");
//        CompetitorData competitorSix = new CompetitorData("Liverpool", "4","45");
//        CompetitorData competitorSeven = new CompetitorData("West ham ", "2","45");
//        CompetitorData competitorEight = new CompetitorData("Bayern munich", "1","45");
//        MatchData matchData1 = new MatchData(competitorOne,competitorTwo);
//        MatchData matchData2 = new MatchData(competitorThree, competitorFour);
//        MatchData matchData3 = new MatchData(competitorFive,competitorSix);
//        MatchData matchData4 = new MatchData(competitorSeven, competitorEight);
//        Colomn1matchesList.add(matchData1);
//        Colomn1matchesList.add(matchData2);
//        Colomn1matchesList.add(matchData3);
//        Colomn1matchesList.add(matchData4);
//        Colomn1matchesList.add(matchData1);
//        Colomn1matchesList.add(matchData2);
//        Colomn1matchesList.add(matchData3);
//        Colomn1matchesList.add(matchData4);
//        Colomn1matchesList.add(matchData1);
//        Colomn1matchesList.add(matchData2);
//        Colomn1matchesList.add(matchData3);
//        Colomn1matchesList.add(matchData4);
//        Colomn1matchesList.add(matchData1);
//        Colomn1matchesList.add(matchData2);
//        Colomn1matchesList.add(matchData3);
//        Colomn1matchesList.add(matchData4);
//        ColumnData colomnData1 = new ColumnData(Colomn1matchesList);
//        sectionList.add(colomnData1);
//
////        for(int i=0;i<tournamentMatches.size();i++){
////            TournamentMatches tournamentMatch = tournamentMatches.get(i);
////            CompetitorData competitorOne = new CompetitorData(tournamentMatch.getUserOneId(),
////                    tournamentMatch.getScores().getUserScore()+"","45");
////            CompetitorData competitorTwo = new CompetitorData(tournamentMatch.getUserTwoId(),
////                    tournamentMatch.getScores().getOpponentScore()+"","45");
////            MatchData matchData = new MatchData(competitorOne,competitorTwo);
////            Colomn1matchesList.add(matchData);
////        }
//
//        CompetitorData competitorNine = new CompetitorData("Manchester United Fc", "2","45");
//        CompetitorData competitorTen = new CompetitorData("Chelsea", "4","45");
//        CompetitorData competitorEleven = new CompetitorData("Liverpool", "2","45");
//        CompetitorData competitorTwelve = new CompetitorData("westham", "1","45");
//        MatchData matchData5 = new MatchData(competitorNine,competitorTen);
//        MatchData matchData6 = new MatchData(competitorEleven, competitorTwelve);
//        colomn2MatchesList.add(matchData5);
//        colomn2MatchesList.add(matchData6);
//        colomn2MatchesList.add(matchData5);
//        colomn2MatchesList.add(matchData6);
//        colomn2MatchesList.add(matchData5);
//        colomn2MatchesList.add(matchData6);
//        colomn2MatchesList.add(matchData5);
//        colomn2MatchesList.add(matchData6);
//        ColumnData colomnData2 = new ColumnData(colomn2MatchesList);
//        sectionList.add(colomnData2);
//        CompetitorData competitorThirteen = new CompetitorData("Chelsea", "2","45");
//        CompetitorData competitorForteen = new CompetitorData("Liverpool", "1","45");
//        MatchData matchData7 = new MatchData(competitorThirteen, competitorForteen);
//        MatchData matchData8 = new MatchData(competitorThirteen, competitorForteen);
//        colomn3MatchesList.add(matchData7);
//        colomn3MatchesList.add(matchData8);
//        colomn3MatchesList.add(matchData7);
//        colomn3MatchesList.add(matchData8);
//        ColumnData colomnData3 = new ColumnData(colomn3MatchesList);
//        sectionList.add(colomnData3);
//        MatchData matchData9 = new MatchData(competitorThirteen, competitorForteen);
//        colomn4MatchesList.add(matchData9);
//        colomn4MatchesList.add(matchData9);
//        ColumnData columnData4 = new ColumnData(colomn4MatchesList);
//        colomn5MatchesList.add(matchData9);
//        ColumnData columnData5= new ColumnData(colomn4MatchesList);
//        sectionList.add(columnData5);

    }
    private void intialiseViewPagerAdapter() {

        sectionAdapter = new BracketsSectionAdapter(getChildFragmentManager(),this.sectionList);
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(sectionAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setPageMargin(-200);
        viewPager.setHorizontalFadingEdgeEnabled(true);
        viewPager.setFadingEdgeLength(50);

        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mCurrentPagerState != ViewPager.SCROLL_STATE_SETTLING) {
            // We are moving to next screen on right side
            if (positionOffset > 0.5) {
                // Closer to next screen than to current
                if (position + 1 != mNextSelectedScreen) {
                    mNextSelectedScreen = position + 1;
                    //update view here
                    if (getBracketsFragment(position).getColumnList().get(0).getHeight()
                            != BracketsUtility.dpToPx(262)) {
                        //     getBracketsFragment(position).shrinkView(BracketsUtility.dpToPx(262));
                    }
                    if (getBracketsFragment(position + 1).getColumnList().get(0).getHeight()
                            != BracketsUtility.dpToPx(262)) {
                        //    getBracketsFragment(position + 1).shrinkView(BracketsUtility.dpToPx(262));
                        }
                    }
            } else {
                // Closer to current screen than to next
                if (position != mNextSelectedScreen) {
                    mNextSelectedScreen = position;
                    //updateViewhere

                    if (getBracketsFragment(position + 1).getCurrentBracketSize() ==
                            getBracketsFragment(position + 1).getPreviousBracketSize()) {
                        //getBracketsFragment(position + 1).shrinkView(BracketsUtility.dpToPx(262));
                        //getBracketsFragment(position).shrinkView(BracketsUtility.dpToPx(262));
                    } else {
                        int currentFragmentSize = getBracketsFragment(position + 1).getCurrentBracketSize();
                        int previousFragmentSize = getBracketsFragment(position + 1).getPreviousBracketSize();
                        if (currentFragmentSize != previousFragmentSize) {
                          //  getBracketsFragment(position + 1).expandHeight(BracketsUtility.dpToPx(524));
                            //getBracketsFragment(position).shrinkView(BracketsUtility.dpToPx(262));
                        }
                    }
                }
            }
        } else {
            // We are moving to next screen left side
            if (positionOffset > 0.5) {
                // Closer to current screen than to next
                if (position + 1 != mNextSelectedScreen) {
                    mNextSelectedScreen = position + 1;
                    //update view for screen

                }
            } else {
                // Closer to next screen than to current
                if (position != mNextSelectedScreen) {
                    mNextSelectedScreen = position;
                    //updateviewfor screem
                }
            }
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public BracketsColumnFragment getBracketsFragment(int position) {
        BracketsColumnFragment bracktsFrgmnt = null;
        if (getChildFragmentManager() != null) {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof BracketsColumnFragment) {
                        bracktsFrgmnt = (BracketsColumnFragment) fragment;
                        if (bracktsFrgmnt.getSectionNumber() == position)
                            break;
                    }
                }
            }
        }
        return bracktsFrgmnt;
    }
}
