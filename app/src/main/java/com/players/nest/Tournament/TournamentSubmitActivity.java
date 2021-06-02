package com.players.nest.Tournament;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.TournamentMatches;
import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.Chats.ChatFragment;
import com.players.nest.HelperClasses.ConfirmAlertDialog;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.MainActivity;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.ScoreApproval;
import com.players.nest.ModelClasses.Scores;
import com.players.nest.ModelClasses.User;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.players.nest.JoinMatch.JoinMatch.JOIN_MATCH;

public class TournamentSubmitActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SUBMIT_ACTIVITY";
    private static final String END_TIME_IN_MILLIS = "END_TIME";
    private static final String TIME_LEFT_IN_MILLIS = "TIME_LEFT";

    public final long START_TIME_MILLIS = 300000;

    boolean currentUserWon = false;
    private static final String SHARED_PREF = "STORE_CLOCK_TIME_WHEN_ACTIVITY_CLOSES";
    SharedPreferences preferences;
    Scores opponentSubmittedScore;
    MatchDetail matchDetail;
    User currentUser, opponentUser;

    Toolbar toolbar;
    ProgressBar progressBar;
    Button noBtn, yesBtn, back2Home;
    ConstraintLayout mainLayout;
    ExpandableLayout loadingLayout, scoreLayout, resultLayout;
    TextView username, oppUsername, userScore, oppScore,
            oppSubmittedScoreTxt, waitingMsg, clockTxt;
    TextView winnerTxt, winnerMsg, tryNextTime;

    CountDownTimer countDownTimer;
    long mTimeLeftInMillis;
    boolean isTimerRunning = false;

    DatabaseReference scoreReference;
    ValueEventListener valueEventListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
    private boolean isClockRunning;
    private static final String IS_CLOCK_RUNNING = "CLOCK_RUNNING_STATUS";
    private long mEndTimeInMillis;
    private boolean isCustomClockRunning;
    private  CountDownTimer myCustomTimer;
    private TournamentDetail tournamentDetail;
    private TournamentMatches tournamentMatches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_submit);

        noBtn = findViewById(R.id.button16);
        toolbar = findViewById(R.id.toolbar19);
        yesBtn = findViewById(R.id.button15);
        back2Home = findViewById(R.id.button18);
        username = findViewById(R.id.textView12);
        clockTxt = findViewById(R.id.textView169);
        winnerTxt = findViewById(R.id.textView171);
        winnerMsg = findViewById(R.id.textView172);
        tryNextTime = findViewById(R.id.textView173);
        userScore = findViewById(R.id.textView48);
        oppScore = findViewById(R.id.textView166);
        waitingMsg = findViewById(R.id.textView10);
        oppUsername = findViewById(R.id.textView28);
        progressBar = findViewById(R.id.progressBar18);
        mainLayout = findViewById(R.id.constraintLayout17);
        loadingLayout = findViewById(R.id.expandableLayout8);
        resultLayout = findViewById(R.id.expandableLayout10);
        scoreLayout = findViewById(R.id.expandableLayout9);
        oppSubmittedScoreTxt = findViewById(R.id.textView168);

        toolbar.setNavigationOnClickListener(v -> openMainActivity());


        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
        back2Home.setOnClickListener(this);


        getDataFromMatchStartedActivity();
        if(tournamentDetail != null && tournamentMatches!= null) {
            preferences = getSharedPreferences(SHARED_PREF +
                    tournamentDetail.getTournamentID() + tournamentMatches.getMatch_ID(), MODE_PRIVATE);
        }
    }


    private void openMainActivity() {
        Intent intent = new Intent(TournamentSubmitActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.finish();
        startActivity(intent);
    }


    private void getDataFromMatchStartedActivity() {

        Intent intent = getIntent();
        User currentUser = intent.getParcelableExtra(Constants.USER_OBJECT);
        User opponentUser = intent.getParcelableExtra(Constants.OPPONENT_USER_OBJECT);
        TournamentDetail tournamentDetail = intent.getParcelableExtra("TOURNAMENT_DETAILS");
        TournamentMatches tournamentMatches = intent.getParcelableExtra("CURRENT_MATCH");
        if (currentUser != null && opponentUser != null && tournamentDetail != null && tournamentMatches!= null) {
            this.currentUser = currentUser;
            this.opponentUser = opponentUser;
            this.tournamentDetail = tournamentDetail;
            this.tournamentMatches = tournamentMatches;

            attachChatFragment();
            attachApprovalListener();
            attachScoreApprovalListener();
            getScores();
            setWidgets();
        } else
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
    }

    private void attachScoreApprovalListener() {
        scoreReference = FirebaseDatabase.getInstance().getReference("Tournament")
                .child(tournamentDetail.getTournamentID()).child("matchDetails").child(tournamentMatches.getMatch_ID());
        scoreReference.addValueEventListener(valueEventListener);
    }


    private void attachChatFragment() {
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setUserObject(opponentUser, JOIN_MATCH);
        getSupportFragmentManager().beginTransaction().replace(R.id.linearLayout27, chatFragment).commit();
    }


    private void getScores() {

        FirebaseDatabase.getInstance().getReference("Tournament")
                .child(tournamentDetail.getTournamentID())
                .child("matchDetails")
                .child(tournamentMatches.getMatch_ID())
                .child(getString(R.string.DB_MATCHES_SCORES))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Scores scores = snapshot.getValue(Scores.class);
                            assert scores != null;
                            if (!scores.getUserID().equals(firebaseUser.getUid()))
                                updateOpponentScores(scores);
                            else {
                                loadingLayout.setExpanded(true, true);
                                scoreLayout.setExpanded(false, true);
                            }
                            opponentSubmittedScore = scores;
                            startCustomTimer();
                        }
                        setWidgets();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void attachApprovalListener() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals(getString(R.string.DB_MATCHES_SCORE_APPROVAL))) {
                        ScoreApproval approvalStatus = dataSnapshot.getValue(ScoreApproval.class);
                        assert approvalStatus != null;
                        if (approvalStatus.getApprovedStatus().equals(Constants.NOT_APPROVED)) {
                            matchDispute();
                            scoreReference.removeEventListener(valueEventListener);
                        }
                    } else if (dataSnapshot.getKey().equals(getString(R.string.MATCH_WINNER))) {
                        String winner = dataSnapshot.getValue(String.class);
                        assert winner != null;
                        if (!winner.equals("")) {
                            if (!winner.equals(firebaseUser.getUid())) {
                                winnerTxt.setText("LOSER");
                                Log.d(TAG, "onDataChange: Loser Layout");
                                winnerMsg.setText(R.string.LOSER_DESC);
                                updateRatings(false);
                                //balance(false);
                            } else {
                                updateRatings(true);
                                //balance(true);
                            }
                            loadingLayout.setExpanded(false, true);
                            resultLayout.setExpanded(true, true);
                            scoreReference.removeEventListener(valueEventListener);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TournamentSubmitActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        };
    }


    private void matchDispute() {
        loadingLayout.setExpanded(false, true);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getString(R.string.MATCH_STATUS), Constants.MATCH_DISPUTE);

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES))
                .child(matchDetail.getMatch_ID())
                .updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(TournamentSubmitActivity.this, "Your Opponent has filed a Match Dispute.",
                        Toast.LENGTH_SHORT).show();
              //  Intent intent = new Intent(TournamentSubmitActivity.this, MatchDisputes.class);
              //  intent.putExtra(Constants.MATCH_DETAIL_OBJECT, matchDetail);
               // intent.putExtra(Constants.OPPONENT_USER_OBJECT, opponentUser);
             //   finishAndRemoveTask();
             //   startActivity(intent);
            } else {
                Toast.makeText(TournamentSubmitActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateOpponentScores(Scores opponentScore) {
        opponentSubmittedScore = opponentScore;
        if (opponentScore.getSubmitted()) {
            userScore.setText(String.valueOf(opponentScore.getOpponentScore()));
            oppScore.setText(String.valueOf(opponentScore.getUserScore()));
            oppSubmittedScoreTxt.setText(getString(R.string.SCORE_SUBMITTED, opponentUser.getUsername()));
        } else
            oppSubmittedScoreTxt.setText("Opponent hasn't submitted the score.");
        loadingLayout.setExpanded(false, true);
        scoreLayout.setExpanded(true, true);
        mTimeLeftInMillis = START_TIME_MILLIS;
        startTimer();
    }


    private void setWidgets() {

        waitingMsg.setText(getString(R.string.WAITING_FOR_SCORES, opponentUser.getUsername()));
        username.setText(currentUser.getUsername());
        oppUsername.setText(opponentUser.getUsername());

        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button15) {

            ConfirmAlertDialog dialog = new ConfirmAlertDialog(Constants.SCORES_ARE_CORRECT, () -> {

                updateApprovalStats(Constants.APPROVED);
                chooseWinner();
                scoreLayout.setExpanded(false, true);
            });
            dialog.show(getSupportFragmentManager(), "CONFIRM_ALERT_DIALOG");
        } else if (v.getId() == R.id.button16) {
            ConfirmDialog confirmDialog = new ConfirmDialog(Constants.ENTER_DISPUTE_DIALOG, () -> {

                updateApprovalStats(Constants.NOT_APPROVED);
//                Intent intent = new Intent(TournamentSubmitActivity.this, MatchDisputes.class);
//                intent.putExtra(Constants.MATCH_DETAIL_OBJECT, matchDetail);
//                scoreReference.removeEventListener(valueEventListener);
//                matchReference.removeEventListener(approvalListener);
//                startActivity(intent);
//                finish();
            });
            confirmDialog.show(getSupportFragmentManager(), "CONFIRM_DIALOG");

        } else if (v.getId() == R.id.button18) {
            Intent intent = new Intent(TournamentSubmitActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    private void updateApprovalStats(String status) {

        ScoreApproval scoreApproval = new ScoreApproval(firebaseUser.getUid(), status);

        FirebaseDatabase.getInstance().getReference("Tournament")
                .child(tournamentDetail.getTournamentID())
                .child("matchDetails")
                .child(tournamentMatches.getMatch_ID())
                .child(getString(R.string.DB_MATCHES_SCORE_APPROVAL))
                .setValue(scoreApproval);
    }


    private void chooseWinner() {

        if (isTimerRunning || isClockRunning) {
            isTimerRunning = false;
            isClockRunning = false;
            countDownTimer.cancel();
            myCustomTimer.cancel();

        }
        if(opponentSubmittedScore!=null) {
            if(opponentSubmittedScore.getUserID().equals(firebaseUser.getUid())) {
                if(opponentSubmittedScore.getUserScore() > opponentSubmittedScore.getOpponentScore()){
                    currentUserWon = true;
                }
                else  currentUserWon = false;
            }
            else {
                if (opponentSubmittedScore.getOpponentScore() > opponentSubmittedScore.getUserScore()) {
                    currentUserWon = true;
                }
                else currentUserWon = false;

            }


            showResultsLayout();
        }

    }


    private void showResultsLayout() {

        loadingLayout.setExpanded(false, true);

        if (currentUserWon) {
            scoreLayout.setExpanded(false, true);
            tryNextTime.setVisibility(View.GONE);
            updateRatings(true);
            //  balance(true);
        } else {
            updateRatings(false);
            //    balance(false);
            winnerTxt.setText("LOSER");
            winnerMsg.setText(R.string.LOSER_DESC);

        }
        scoreLayout.setExpanded(false, true);
        resultLayout.setExpanded(true, true);
        updateDatabase();
    }


    private void updateRatings(boolean userWon) {

        int ratingInc;
        if (userWon) {
            ratingInc = currentUser.getRatings() + 2;
            Log.d(TAG, "updateRatings: " + currentUser.getUsername());
            updateRatingsDB(ratingInc);
        } else if (currentUser.getRatings() > 40) {
            ratingInc = currentUser.getRatings() - 4;
            updateRatingsDB(ratingInc);
        }
    }


    private void updateRatingsDB(int ratingInc) {

        if (ratingInc < 40)
            ratingInc = 40;
        HashMap<String, Object> ratings = new HashMap<>();
        ratings.put(getString(R.string.DB_USERS_RATINGS), ratingInc);

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(firebaseUser.getUid())
                .updateChildren(ratings);
    }


    private void updateDatabase() {
        HashMap<String, Object> updateStatus = new HashMap<>();
        updateStatus.put(getString(R.string.MATCH_STATUS), Constants.MATCH_FINISHED);
        updateStatus.put(getString(R.string.FINISHED_TIME), String.valueOf(System.currentTimeMillis()));

        if (currentUserWon)
            updateStatus.put(getString(R.string.MATCH_WINNER), currentUser.getUser_id());
        else
            updateStatus.put(getString(R.string.MATCH_WINNER), opponentUser.getUser_id());

        FirebaseDatabase.getInstance().getReference("Tournament")
                .child(tournamentDetail.getTournamentID())
                .child("matchDetails")
                .child(tournamentMatches.getMatch_ID())
                .updateChildren(updateStatus);
    }


    private void startTimer() {

        isTimerRunning = true;
        mEndTimeInMillis = System.currentTimeMillis() + mTimeLeftInMillis;
        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateClockTxt();
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                isTimerRunning = false;
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();
                Log.d(TAG, "onFinish: ");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("submitResultTimerFinished", true);
                FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES))
                        .child(matchDetail.getMatch_ID())
                        .updateChildren(hashMap);
//                updateApprovalStats(Constants.APPROVED);
//                chooseWinner();
                scoreLayout.setExpanded(false, true);
            }
        };
        countDownTimer.start();
    }


    private void updateClockTxt() {
        int min = (int) ((mTimeLeftInMillis / 1000) / 60);
        int sec = (int) ((mTimeLeftInMillis / 1000) % 60);

        clockTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));
    }
    private void startCustomTimer(){
        isClockRunning = true;
        mEndTimeInMillis = System.currentTimeMillis() + mTimeLeftInMillis;
        myCustomTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                isClockRunning = false;
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();
                Log.d(TAG, "onFinish: ");
                chooseWinner();
                updateApprovalStats(Constants.APPROVED);
                scoreLayout.setExpanded(false, true);
            }
        };
        myCustomTimer.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseHelperClass.changeStatus(Constants.ONLINE);

        if(tournamentDetail != null && tournamentMatches!= null) {
            scoreReference = FirebaseDatabase.getInstance().getReference("Tournament")
                    .child(tournamentDetail.getTournamentID()).child("matchDetails").child(tournamentMatches.getMatch_ID());
            scoreReference.addValueEventListener(valueEventListener);
        }
        mTimeLeftInMillis = START_TIME_MILLIS;
        if(preferences != null) {
            isClockRunning = preferences.getBoolean(IS_CLOCK_RUNNING, false);
            mEndTimeInMillis = preferences.getLong(END_TIME_IN_MILLIS, 0);
        }
        if (isCustomClockRunning) {
            mTimeLeftInMillis = mEndTimeInMillis - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                // startCustomTimer();
            }
            //startTimer();
            //updateClockTxt();
        }


    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: called");
        if (isClockRunning  ) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(TIME_LEFT_IN_MILLIS, mTimeLeftInMillis);
            editor.putLong(END_TIME_IN_MILLIS, mEndTimeInMillis);
            editor.putBoolean(IS_CLOCK_RUNNING, isClockRunning);
            editor.apply();
        }
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openMainActivity();
    }

    private void balance(boolean win) {
        if (win) {
            userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Objects.requireNonNull(user).setAccount_balance(winner(0, matchDetail.getEntryFee() * 2, user.getAccount_balance()));
                    userDetails.setValue(user);

                    DatabaseReference admin = FirebaseDatabase.getInstance().getReference("users");
                    admin.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User usr = data.getValue(User.class);
                                if (Objects.requireNonNull(usr).getRole() == 1) {
                                    usr.setAccount_balance(winner(1, matchDetail.getEntryFee() * 2, usr.getAccount_balance()));
                                    FirebaseDatabase.getInstance().getReference("users").child(Objects.requireNonNull(data.getKey())).setValue(usr);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private double winner(int role, double winningAmount, double balance) {
        double win = 0;
        if (role == 0) {
            win = (92 * winningAmount) / 100;
        } else {
            win = (8 * winningAmount) / 100;
        }
        double all = balance + win;
        return all;
    }
}