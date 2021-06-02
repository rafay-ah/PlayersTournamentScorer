package com.players.nest.MyTournamentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.MyMatchesActivity.MyMatchesAdapter;
import com.players.nest.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MyTournamentActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView noDataFound;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    MyTournamentAdapter myTournamentAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<TournamentDetail> tournamentDetails = new ArrayList<>();

    DatabaseReference mRef;
    FirebaseUser firebaseUser;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        toolbar = findViewById(R.id.toolbar10);
        noDataFound = findViewById(R.id.textView94);
        recyclerView = findViewById(R.id.recycler_view8);
        progressBar = findViewById(R.id.progressBar22);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout2);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar.setTitle("My Tournament");
        //Setting Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mRef.removeEventListener(valueEventListener);
            getMatchDetails();
            mRef.addValueEventListener(valueEventListener);
        });


        setAdapter();
        getMatchDetails();
    }


    private void setAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        myTournamentAdapter = new MyTournamentAdapter(this, tournamentDetails);
        recyclerView.setAdapter(myTournamentAdapter);
    }


    private void getMatchDetails() {

        mRef = FirebaseDatabase.getInstance().getReference("Tournament");
        recyclerView.setVisibility(View.GONE);


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tournamentDetails.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        TournamentDetail tournamentDetail = dataSnapshot.getValue(TournamentDetail.class);
                        assert tournamentDetail != null;
                        if (tournamentDetail.getHostUserId().equals(firebaseUser.getUid())
                                && !tournamentDetail.getTournamentStatus().equals(Constants.MATCH_FINISHED))
                            tournamentDetails.add(tournamentDetail);
                    }
                }
                if (tournamentDetails.size() == 0) {
                    noDataFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    Collections.reverse(tournamentDetails);
                    myTournamentAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                }

                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyTournamentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseHelperClass.changeStatus(Constants.ONLINE);
        mRef.addValueEventListener(valueEventListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
        mRef.removeEventListener(valueEventListener);
    }
}

