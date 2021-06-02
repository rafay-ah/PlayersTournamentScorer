package com.players.nest.FindMatchFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.ModelClasses.Games;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FindMatchesFragment extends Fragment {

    private static final String TAG = "FIND_MATCHES_FRAGMENT";
    Games gameObject;
    String type = "";

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView noDataFound;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    FindMatchAdapter findMatchAdapter;
    DatabaseReference databaseReference;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<MatchDetail> selectedMatchesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_matches, container, false);

        toolbar = view.findViewById(R.id.toolbar11);
        noDataFound = view.findViewById(R.id.textView105);
        progressBar = view.findViewById(R.id.progressBar21);
        recyclerView = view.findViewById(R.id.recycler_view9);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout1);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES));


        //Setting Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view1 -> Objects.requireNonNull(getActivity()).onBackPressed());


        swipeRefreshLayout.setOnRefreshListener(this::getCreatedMatches);


        setAdapter();
        getCreatedMatches();

        return view;
    }


    //Knowing which Activity is Hosting this Fragment.
    public void getType(Games gameObject, String type) {
        this.gameObject = gameObject;
        this.type = type;
    }


    private void getCreatedMatches() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedMatchesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MatchDetail matchDetail = dataSnapshot.getValue(MatchDetail.class);
                    if (matchDetail != null) {
                        if (type.equals(getString(R.string.FIND_ALL_MATCHES))) {
                            if (!matchDetail.getHostUserId().equals(firebaseUser.getUid())
                                    && !matchDetail.getMatchStatus().equals(Constants.MATCH_FINISHED)
                                    && !matchDetail.getMatchStatus().equals(Constants.MATCH_DISPUTE)
                                    && !matchDetail.getMatchStatus().equals(Constants.MATCH_INVITATION)) {

                                checkIfUserIsRejectedOrNot(matchDetail, dataSnapshot);
                            }
                        } else {
                            if (!matchDetail.getHostUserId().equals(firebaseUser.getUid())
                                    && matchDetail.getGame().getGameID().equals(gameObject.getGameID())
                                    && !matchDetail.getMatchStatus().equals(Constants.MATCH_FINISHED)
                                    && !matchDetail.getMatchStatus().equals(Constants.MATCH_INVITATION)) {

                                checkIfUserIsRejectedOrNot(matchDetail, dataSnapshot);
                            }
                        }
                    }
                }
                if (selectedMatchesList.size() == 0) {
                    noDataFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    Collections.reverse(selectedMatchesList);
                    recyclerView.setVisibility(View.VISIBLE);
                    findMatchAdapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void checkIfUserIsRejectedOrNot(MatchDetail matchDetail, DataSnapshot dataSnapshot) {

        boolean found = false;

        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            if (Objects.equals(dataSnapshot1.getKey(), "rejectedUsersIds"))
                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                    if (Objects.equals(dataSnapshot2.getKey(), firebaseUser.getUid()))
                        found = true;
                }
        }

        if (!found)
            selectedMatchesList.add(matchDetail);
    }


    /* Passing a List of matches to show in the recyclerView ---> Depending on which activity this
     Fragment is attached, this Fragment will get matches List,
     Either selected Game List or All Matches Created.*/
    private void setAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        findMatchAdapter = new FindMatchAdapter(Objects.requireNonNull(getContext()), selectedMatchesList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(findMatchAdapter);
    }

}
