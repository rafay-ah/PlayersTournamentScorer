package com.players.nest.Tournament;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.ModelClasses.Games;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.TournamentMatches;
import com.players.nest.R;
import com.players.nest.Tournament.Adapter.JoinAlreadyCreatedAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.players.nest.GameFragment.TopGamesAdapter.SELECTED_GAME_OBJECT;

public class JoinAlreadyCreatedActivity extends AppCompatActivity {
    private RecyclerView tournamentsRV;
    private ArrayList<TournamentDetail> tournamentDetails;
    private  JoinAlreadyCreatedAdapter alreadyCreatedAdapter;
    private Games games;
    public static final String SELECTED_GAME_OBJECT = "SELECTED_GAME";
    private FirebaseUser firebaseUser;
    private TextView noDataFound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_already_created_tournament);
        tournamentsRV = findViewById(R.id.rv_touraments);
        tournamentDetails = new ArrayList<>();
        alreadyCreatedAdapter = new JoinAlreadyCreatedAdapter(this,getSupportFragmentManager(),
                tournamentDetails);
        tournamentsRV.setAdapter(alreadyCreatedAdapter);
        noDataFound = findViewById(R.id.tv_not_found);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        tournamentsRV.setLayoutManager(new LinearLayoutManager(this));
        getGameObject();
        getTournamentDetails();


    }
    private void getGameObject(){
        Intent intent = getIntent();
        games = intent.getParcelableExtra(SELECTED_GAME_OBJECT);

    }

    private void getTournamentDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tournament");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tournamentDetails.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        //for(DataSnapshot snapshotItems : dataSnapshot.getChildren()){
                            TournamentDetail tournamentDetail = dataSnapshot.getValue(TournamentDetail.class);
                            //TODO !Objects.requireNonNull(tournamentDetail).getHostUserId().equals(firebaseUser.getUid())
                        //                                    &&
                            if( Objects.requireNonNull(tournamentDetail).getGame().getGameID().equals(games.getGameID())
                                    && !tournamentDetail.getTournamentStatus().equals(Constants.MATCH_FINISHED)
                                    && !tournamentDetail.getTournamentStatus().equals(Constants.TOURNAMENT_CANCELED)
                                    && !tournamentDetail.getTournamentStatus().equals(Constants.MATCH_STARTED)) {
                                tournamentDetails.add(tournamentDetail);
                            }
                        //}
                    }
                    if(tournamentDetails.size() == 0){
                        noDataFound.setVisibility(View.VISIBLE);
                        tournamentsRV.setVisibility(View.INVISIBLE);
                    }
                    else{
                        Collections.reverse(tournamentDetails);
                        tournamentsRV.setVisibility(View.VISIBLE);
                        alreadyCreatedAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });
    }
}