package com.players.nest.JoinMatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.Chats.ChatFragment;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.NotificationHelper;
import com.players.nest.MainActivity;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.Scores;
import com.players.nest.ModelClasses.User;
import com.players.nest.Notifications.NotificationReq;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.players.nest.JoinMatch.JoinMatch.JOIN_MATCH;

public class MatchStarted extends AppCompatActivity {

    public static final String TAG = "MATCH_STARTED";

    public static final long START_TIME_IN_MILLIS = 5400000;
    private static final String SHARED_PREF = "STORE_CLOCK_TIME_WHEN_ACTIVITY_CLOSES";
    private static final String TIME_LEFT_IN_MILLIS = "TIME_LEFT";
    private static final String END_TIME_IN_MILLIS = "END_TIME";
    private static final String IS_CLOCK_RUNNING = "CLOCK_RUNNING_STATUS";

    long mTimeLeftInMillis, mEndTimeInMillis;
    boolean isClockRunning = false;
    CountDownTimer countDownTimer;

    User user, opponentUser;
    MatchDetail matchDetail;
    boolean isCancel = true, isOpen, isEnd = true;

    AlertDialog.Builder dialog;
    AlertDialog dialog1;
    TextView clock;
    Toolbar toolbar;
    ProgressBar progressBar;
    ImageView userProPic, oppProPic;
    EditText userScore, opponentScore;
    NestedScrollView nestedScrollView;
    TextView submitScores, cancelMatch, cancel, currentUsername, oppName, gameName, submitInfo;
    ExpandableLayout scoresExpandableLayout, cancelLayout, chatFragmentLayout, opponentSubmittedScoreLayout;

    FirebaseUser firebaseUser;
    SharedPreferences preferences;
    DatabaseReference userReference, matchReference;
    FirebaseDatabase firebaseDatabase;
    ValueEventListener valueEventListener;
    ValueEventListener databaseReferenceListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_started);

        toolbar = findViewById(R.id.toolbar16);
        cancel = findViewById(R.id.textView132);
        clock = findViewById(R.id.textView129);
        userScore = findViewById(R.id.editText2);
        oppName = findViewById(R.id.textView108);
        gameName = findViewById(R.id.textView130);
        oppProPic = findViewById(R.id.imageView33);
        submitInfo = findViewById(R.id.textView165);
        userProPic = findViewById(R.id.imageView34);
        opponentScore = findViewById(R.id.editText);
        progressBar = findViewById(R.id.progressBar9);
        submitScores = findViewById(R.id.textView128);
        cancelMatch = findViewById(R.id.textView131);
        currentUsername = findViewById(R.id.textView107);
        cancelLayout = findViewById(R.id.expandableLayout6);
        nestedScrollView = findViewById(R.id.nestedScrollView6);
        chatFragmentLayout = findViewById(R.id.expandableLayout4);
        scoresExpandableLayout = findViewById(R.id.expandableLayout5);
        opponentSubmittedScoreLayout = findViewById(R.id.expandableLayout7);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        preferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);


        //Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());


        //OnClickListeners
        submitScores.setOnClickListener(view -> {
            if (scoresExpandableLayout.isExpanded()) {
                checkEditTexts();
            } else {
                scoresExpandableLayout.setExpanded(true, true);
                cancelLayout.setExpanded(true, true);
                chatFragmentLayout.setExpanded(false, true);
            }
        });
        cancel.setOnClickListener(view -> {
            scoresExpandableLayout.setExpanded(false, true);
            cancelLayout.setExpanded(false, true);
            chatFragmentLayout.setExpanded(true, true);
        });

        cancelMatch.setOnClickListener(view -> {
            RequestCencel();
        });

        getDataFromFragment();
        attachScoresListener();
        addListener();
    }


    private void getDataFromFragment() {
        Intent intent = getIntent();
        MatchDetail matchDetail = intent.getParcelableExtra(Constants.MATCH_DETAIL_OBJECT);
        if (matchDetail != null) {
            this.matchDetail = matchDetail;
            getOpponentData();
        }
    }


    private void getOpponentData() {

        if (firebaseUser.getUid().equals(matchDetail.getHostUserId()))
            userReference = firebaseDatabase.getReference(getString(R.string.DB_USERS))
                    .child(matchDetail.getJoinedUserID());
        else
            userReference = firebaseDatabase.getReference(getString(R.string.DB_USERS))
                    .child(matchDetail.getHostUserId());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User oppUser = snapshot.getValue(User.class);
                opponentUser = oppUser;
                assert oppUser != null;
                if (!oppUser.getProfilePic().equals(""))
                    Glide.with(MatchStarted.this).load(oppUser.getProfilePic()).into(oppProPic);
                oppName.setText(oppUser.getUsername());
                submitInfo.setText(getString(R.string.OPPONENT_SUBMITTED_SCORES, oppUser.getUsername()));

                attachChatFragment(oppUser);
                getCurrentUserData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void getCurrentUserData() {

        gameName.setText(matchDetail.getGame().getName());

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                user = currentUser;
                assert currentUser != null;
                currentUsername.setText(currentUser.getUsername());
                if (!currentUser.getProfilePic().equals(""))
                    Glide.with(MatchStarted.this).load(currentUser.getProfilePic()).into(userProPic);

                progressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void startTimer() {

        isClockRunning = true;
        mEndTimeInMillis = System.currentTimeMillis() + mTimeLeftInMillis;

        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMillis = l;
                updateClockTxt();
            }

            @Override
            public void onFinish() {
                //Do Something When clock Finishes
                isClockRunning = false;
                countDownTimer.cancel();
                updateDatabase();
            }
        };
        countDownTimer.start();
    }


    private void updateDatabase() {

        Scores scores = new Scores(firebaseUser.getUid(), 0, 0, false);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("timerFinished", true);

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES))
                .child(matchDetail.getMatch_ID())
                .child(getString(R.string.DB_MATCHES_SCORES))
                .setValue(scores);
        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES))
                .child(matchDetail.getMatch_ID())
                .updateChildren(hashMap);
    }


    private void updateClockTxt() {
        int min = (int) ((mTimeLeftInMillis / 1000) / 60);
        int sec = (int) ((mTimeLeftInMillis / 1000) % 60);

        clock.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));
    }


    private void checkEditTexts() {

        if (userScore.getText().toString().isEmpty() || opponentScore.getText().toString().isEmpty())
            Toast.makeText(this, "Please enter you and your opponent's scores.", Toast.LENGTH_SHORT).show();
        else
            confirmDialog();
    }


    //Updating and starting Submit Activity
    private void confirmDialog() {

        final String score1 = userScore.getText().toString();
        final String score2 = opponentScore.getText().toString();

        final ConfirmDialog dialog = new ConfirmDialog(Constants.SUBMIT_RESULT_DIALOG, () -> {

            addScoresToDatabase(score1, score2);
            openSubmitActivity();
        });
        dialog.show(getSupportFragmentManager(), "CONFIRM_SUBMIT_DIALOG");
    }


    private void openSubmitActivity() {

        if (isClockRunning)
            countDownTimer.cancel();
        isClockRunning = false;
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();

        HashMap<String, Object> matchStatusMap = new HashMap<>();
        matchStatusMap.put(getString(R.string.MATCH_STATUS), Constants.SUBMITTING_RESULTS);
        matchReference.updateChildren(matchStatusMap);

        Intent intent = new Intent(MatchStarted.this, SubmitActivity.class);
        intent.putExtra(Constants.USER_OBJECT, user);
        intent.putExtra(Constants.MATCH_DETAIL_OBJECT, matchDetail);
        intent.putExtra(Constants.OPPONENT_USER_OBJECT, opponentUser);
        finishAndRemoveTask();
        startActivity(intent);
    }

    private void RequestCencel() {
        isCancel = true;
        HashMap<String, Object> matchStatusMap = new HashMap<>();
        matchStatusMap.put(getString(R.string.MATCH_STATUS), Constants.REQUEST_CANCEL);
        matchReference.updateChildren(matchStatusMap);
    }

    private void RejectRequest() {
        isCancel = false;
        HashMap<String, Object> matchStatusMap = new HashMap<>();
        matchStatusMap.put(getString(R.string.MATCH_STATUS), Constants.MATCH_STARTED);
        matchReference.updateChildren(matchStatusMap);
    }

    public void Alert(String title, String msg, String approve, String cancel, MatchDetail matchDetail) {
        isOpen = false;
        if (!isCancel) {
            dialog.setTitle(title)
                    .setMessage(msg)
                    .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            RejectRequest();
                            isOpen = true;
                        }
                    })
                    .setPositiveButton(approve, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Intent a = new Intent(MatchStarted.this, MainActivity.class);
//                            a.addCategory(Intent.CATEGORY_HOME);
//                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(a);
                            HashMap<String, Object> matchStatusMap = new HashMap<>();
                            matchStatusMap.put(getString(R.string.MATCH_STATUS), Constants.APPROVE_CANCEL);
                            matchReference.updateChildren(matchStatusMap);
                        }
                    });
        } else {
            dialog.setTitle(title)
                    .setMessage(msg)
                    .setNegativeButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton(cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            RejectRequest();
                            isOpen = true;
                        }
                    });
        }
        dialog1 = dialog.create();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
    }


    private void addScoresToDatabase(String score1, String score2) {

        Scores scores = new Scores(firebaseUser.getUid(), Integer.parseInt(score1), Integer.parseInt(score2), true);

        firebaseDatabase.getReference(getString(R.string.DB_MATCHES)).child(matchDetail.getMatch_ID())
                .child(getString(R.string.DB_MATCHES_SCORES))
                .setValue(scores);
    }
    private void addListener(){
        databaseReferenceListener = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES)).child(matchDetail.getMatch_ID())
                .child(getString(R.string.DB_MATCHES_SCORES))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            openSubmitActivity();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void attachScoresListener() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                MatchDetail matchDetail2 = snapshot.getValue(MatchDetail.class);
                if (matchDetail2 == null) {
                    finishAndRemoveTask();
                }
                assert matchDetail2 != null;
                if (!matchDetail2.isTimerFinished()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        assert dataSnapshot.getKey() != null;
                        if (dataSnapshot.getKey().equals(getString(R.string.DB_MATCHES_SCORES))) {
                            opponentSubmittedScoreLayout.setExpanded(true, true);
                            openSubmitActivity();
                            notifyOpponent();
                        } else
                            opponentSubmittedScoreLayout.setExpanded(false);

                        if (matchDetail2.getMatchStatus().equals(Constants.REQUEST_CANCEL)) {
                            if (isOpen) {
                                if (!isCancel) {
                                    Alert("Cancel Match", "want to cancel?", "ok", "no", matchDetail2);
                                } else {
                                    Alert("Cancel Match", "Your Cancel Match Sended, waiting approved", "", "cancel", matchDetail2);
                                }
                            }
                        } else {
                            if (isCancel) isCancel = false;
                            dialog1.dismiss();
                            isOpen = true;
                        }

                        if (matchDetail2.getMatchStatus().equals(Constants.APPROVE_CANCEL)) {
                            finishAndRemoveTask();
                            countDownTimer.cancel();
                            matchReference.removeEventListener(valueEventListener);
                            if (isEnd) {
                                isEnd = false;
                                Toast.makeText(MatchStarted.this, "Your Match Has Been Canceled", Toast.LENGTH_SHORT).show();
                                Intent a = new Intent(MatchStarted.this, MainActivity.class);

                                if (isClockRunning)
                                    countDownTimer.cancel();
                                isClockRunning = false;
                                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear().apply();

                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                a.putExtra("Status", "Cancel");
                                startActivity(a);
                                if (isOpen) {
                                    matchReference.removeValue();
                                }
                                user.setAccount_balance(user.getAccount_balance() + matchDetail2.getEntryFee());
                                FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                                        .child(firebaseUser.getUid()).setValue(user);
                                finish();
                            }
                        }
                    }
                } else if (matchDetail2.isTimerFinished()) {
                    matchReference.removeEventListener(valueEventListener);
                    countDownTimer.cancel();
                    Toast.makeText(MatchStarted.this, "Timer Finished", Toast.LENGTH_SHORT).show();
                    openSubmitActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

    }


    private void notifyOpponent() {

        String body = getString(R.string.OPPONENT_SUBMITTED_SCORE_NOTIFICATION_BODY, opponentUser.getUsername());
        new NotificationHelper().sendNotification(getApplicationContext(), opponentUser.getDeviceToken(), matchDetail.getGame().getName(),
                body, Constants.OPEN_ALERT_FRAGMENT, new NotificationReq.Data(Constants.OPEN_ALERT_FRAGMENT));
    }


    private void attachChatFragment(User opponentUser) {

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setUserObject(opponentUser, JOIN_MATCH);
        getSupportFragmentManager().beginTransaction().add(R.id.linearLayout20, chatFragment).commit();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseHelperClass.changeStatus(Constants.ONLINE);
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        isClockRunning = preferences.getBoolean(IS_CLOCK_RUNNING, false);
        mEndTimeInMillis = preferences.getLong(END_TIME_IN_MILLIS, 0);

        Log.d(TAG, "onStart: " + mTimeLeftInMillis);

        if (isClockRunning) {
            mTimeLeftInMillis = mEndTimeInMillis - System.currentTimeMillis();
            if (mTimeLeftInMillis < 1000) {
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
            }
        }

        dialog = new AlertDialog.Builder(this);
        dialog1 = dialog.create();

        matchReference = firebaseDatabase.getReference(getString(R.string.DB_MATCHES)).child(matchDetail.getMatch_ID());
        matchReference.addValueEventListener(valueEventListener);
        updateClockTxt();
        startTimer();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isClockRunning) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(TIME_LEFT_IN_MILLIS, mTimeLeftInMillis);
            editor.putLong(END_TIME_IN_MILLIS, mEndTimeInMillis);
            editor.putBoolean(IS_CLOCK_RUNNING, isClockRunning);
            editor.apply();
        }
//        countDownTimer.cancel();
        Log.d("mTAG", "onPause Called ");
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
        matchReference.removeEventListener(valueEventListener);
    }
}
