package com.players.nest.AlertFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.ModelClasses.MatchDetail;

import java.util.ArrayList;
import java.util.Collections;

public class AlertFragment extends Fragment {

    private static final String TAG = "ALERT_FRAGMENT";

    ProgressBar progressBar;
    TextView noDataFound, recentTxt;
    SwipeRefreshLayout refreshLayout;
    AlertAdapter adapter, recentAdapter, invitesAdapter;
    TournamentAlertAdapter tournamentAlertAdapter, tournamentRecentAdapter, tournamentInviteAdapter;
    ArrayList<MatchDetail> arrayList = new ArrayList<>();
    ArrayList<MatchDetail> invitesList = new ArrayList<>();
    ArrayList<MatchDetail> recentList = new ArrayList<>();
    RecyclerView recyclerView, recentRecyclerView, invitesRecyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ValueEventListener valueEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert, container, false);

        recyclerView = view.findViewById(R.id.recycler_view12);
        recentRecyclerView = view.findViewById(R.id.recycler_view15);
        progressBar = view.findViewById(R.id.progressBar7);
        noDataFound = view.findViewById(R.id.textView127);
        recentTxt = view.findViewById(R.id.textView176);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        invitesRecyclerView = view.findViewById(R.id.recycler_view19);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //Methods
        setAdapter();
        ListenForRequests();


        refreshLayout.setOnRefreshListener(() -> {
            adapter.notifyDataSetChanged();
            reference.removeEventListener(valueEventListener);
            ListenForRequests();
            recentAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        });

        return view;
    }


    private void setAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new AlertAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //Recent's RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recentAdapter = new AlertAdapter(getContext(), recentList);
        recentRecyclerView.setLayoutManager(linearLayoutManager);
        recentRecyclerView.setAdapter(recentAdapter);

        //Invites RecyclerView
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        invitesAdapter = new AlertAdapter(getContext(), invitesList);
        invitesRecyclerView.setLayoutManager(layoutManager1);
        invitesRecyclerView.setAdapter(invitesAdapter);
    }


    private void ListenForRequests() {
        progressBar.setVisibility(View.VISIBLE);
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES));

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                recentList.clear();
                invitesList.clear();
                int size1 = 0;
                int size2 = 0;
                int size3 = 0;
                int size4 = 0;
                int size5 = 0;
                int size6 = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MatchDetail matchDetail = dataSnapshot.getValue(MatchDetail.class);
                    assert matchDetail != null;
                    if (matchDetail.getHostUserId().equals(firebaseUser.getUid()) || matchDetail.getJoinedUserID().equals(firebaseUser.getUid())) {
                        if (matchDetail.getMatchStatus().equals(Constants.MATCH_CONNECTING)) {
                            if (!matchDetail.getJoinedUserID().equals("")) {
                                arrayList.add(matchDetail);
                                size1++;
                            }

                        }
                        else if(matchDetail.getMatchStatus().equals(Constants.MATCH_STARTED)){
                            if (!matchDetail.getJoinedUserID().equals("")) {
                                arrayList.add(matchDetail);
                                size2++;
                            }
                        }
                        else if(matchDetail.getMatchStatus().equals(Constants.SUBMITTING_RESULTS)){
                            if (!matchDetail.getJoinedUserID().equals("")) {
                                arrayList.add(matchDetail);
                                size3++;
                            }
                        }
                        else if(matchDetail.getMatchStatus().equals(Constants.MATCH_DISPUTE)){
                            if (!matchDetail.getJoinedUserID().equals("")) {
                                arrayList.add(matchDetail);
                                size4++;
                            }
                        }
                        else if (matchDetail.getMatchStatus().equals(Constants.MATCH_FINISHED)) {
                            recentList.add(matchDetail);
                            size5++;
                        }
                        else if (matchDetail.getMatchStatus().equals(Constants.MATCH_INVITATION)
                                && matchDetail.getJoinedUserID().equals(firebaseUser.getUid())) {
                            invitesList.add(matchDetail);
                            size6++;
                        }
                    }
                }
                if (recentList.size() == 0) {
                    recentTxt.setVisibility(View.GONE);
                    recentRecyclerView.setVisibility(View.GONE);
                } else
                    recentTxt.setVisibility(View.VISIBLE);

                if (arrayList.isEmpty() && recentList.isEmpty())
                    noDataFound.setVisibility(View.VISIBLE);
                else
                    noDataFound.setVisibility(View.GONE);

                Collections.reverse(recentList);
                adapter.notifyDataSetChanged();
                recentAdapter.notifyDataSetChanged();
                invitesAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        reference.addValueEventListener(valueEventListener);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reference.removeEventListener(valueEventListener);
    }
}
