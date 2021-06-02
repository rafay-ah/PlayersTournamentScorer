package com.players.nest.Tournament;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.TournamentMatches;
import com.players.nest.R;

import java.util.List;
import java.util.Objects;

public class TournamentNextMatch extends AppCompatActivity {
    private TournamentDetail tournamentDetail;
    private TournamentMatches tournamentMatches;
    AppCompatButton tournamentTree;
    private int type;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private int currentMatchPosition=-1;
    private TournamentMatches currentMatch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_next_match);
        tournamentTree = findViewById(R.id.btn_tournament_tree);
        getDataFromIntent();
        if(type != 1 ){
            if (tournamentDetail != null && tournamentDetail.getMatchDetails() != null) {
                int i = 0;
                for (TournamentMatches tournamentMatches : tournamentDetail.getMatchDetails()) {
                    if ((tournamentMatches.getUserOneId().equals(firebaseUser.getUid()) ||
                            tournamentMatches.getUserTwoId().equals(firebaseUser.getUid())) &&
                            !tournamentMatches.getMatchStatus().equals(Constants.MATCH_FINISHED)) {
                        currentMatchPosition = i;
                        currentMatch = tournamentMatches;

                    }
                    i++;
                }
                if(currentMatch != null ||currentMatchPosition==-1){
                    Intent intent = new Intent(TournamentNextMatch.this, TournamentStartedActivity.class);
                    intent.putExtra("TOURNAMENT_DETAILS", tournamentDetail);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else addValueListener();
            }

        }
        else addValueListener();
        tournamentTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TournamentNextMatch.this,TournamentActivity.class);
                intent.putExtra("TOURNAMENT_DETAILS",tournamentDetail);
                startActivity(intent);
            }
        });
    }

    private void addValueListener() {
        if (tournamentDetail != null && tournamentDetail.getTournamentWinner().isEmpty()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tournament");

            if (tournamentDetail != null && tournamentMatches != null) {
                int matchNum = Integer.parseInt(tournamentMatches.getMatch_ID());
                int oppMatchNum;
                if (matchNum % 2 == 0) {
                    oppMatchNum = matchNum + 1;
                } else {
                    oppMatchNum = matchNum - 1;
                }
                // if(tournamentMatches.getMatchStatus().equals(Constants.M))
                reference = reference.child(tournamentDetail.getTournamentID()).child(oppMatchNum + "");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int newMatchNum = matchNum / 2;
                            int maxMatch = tournamentDetail.getMaxPlayers() / 2;
                            TournamentMatches tournamentMatches1 = snapshot.getValue(TournamentMatches.class);
                            if (Objects.requireNonNull(tournamentMatches).getMatchStatus().equals(Constants.MATCH_FINISHED)
                                    && tournamentMatches.getSectionNumber() == (tournamentMatches1 != null ? tournamentMatches1.getSectionNumber() : -1)) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                        .getReference("Tournament").child(tournamentDetail.getTournamentID())
                                        .child(maxMatch + newMatchNum + "");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            TournamentMatches nextMatch = snapshot.getValue(TournamentMatches.class);
                                            Objects.requireNonNull(nextMatch).setUserOneId(tournamentMatches.getMatchWinner());
                                            nextMatch.setUserTwoId(Objects.requireNonNull(tournamentMatches1).getMatchWinner());
                                            databaseReference.setValue(nextMatch).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(TournamentNextMatch.this, TournamentStartedActivity.class);
                                                    intent.putExtra("TOURNAMENT_DETAILS", tournamentDetail);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(TournamentNextMatch.this, "Something Went Wrong", Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TournamentNextMatch.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    }
                });
            }

        }

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        tournamentDetail = intent.getParcelableExtra("TOURNAMENT_DETAILS");
        tournamentMatches = intent.getParcelableExtra("CURRENT_MATCH");
        type = intent.getIntExtra("TYPE",-1);

    }
}
