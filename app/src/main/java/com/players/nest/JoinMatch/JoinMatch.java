package com.players.nest.JoinMatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

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
import com.players.nest.HelperClasses.ConfirmAlertDialog;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.ModelClasses.Games;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.Notifications.NotificationReq;
import com.players.nest.Notifications.NotificationRequest;
import com.players.nest.Notifications.NotificationResponse;
import com.players.nest.Notifications.RetrofitHelper;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinMatch extends AppCompatActivity {

    public static final String JOIN_MATCH = "JOIN_MATCH";
    private static final String TAG = JOIN_MATCH;
    private static final long START_TIME_IN_MILLIS = 600000;


    User user;
    Games gamesObj;
    MatchDetail matchDetail;

    Toolbar toolbar;
    LoadingDialog loadingDialog;
    ConstraintLayout mainLayout;
    LinearLayout linearLayout;
    CountDownTimer countDownTimer;
    ImageView currentUserImg, opponentImg;
    TextView gameName, currentUserTxt, opponentUsername,
            readyToPlay, clock;
    ExpandableLayout infoTxt, checkMatchDetails, waitingForHost;

    FirebaseUser firebaseUser;
    List<String> rejectedUserIdList = new ArrayList<>();
    ValueEventListener valueEventListener;
    DatabaseReference matchesReference, userReference, userDetails;


    boolean hostAccepted = false;
    boolean isTimerRunning = false;
    long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_match);

        clock = findViewById(R.id.textView111);
        toolbar = findViewById(R.id.toolbar12);
        gameName = findViewById(R.id.textView109);
        readyToPlay = findViewById(R.id.textView112);
        opponentImg = findViewById(R.id.imageView33);
        currentUserTxt = findViewById(R.id.textView107);
        infoTxt = findViewById(R.id.expandableLayout);
        opponentUsername = findViewById(R.id.textView108);
        currentUserImg = findViewById(R.id.imageView34);
        linearLayout = findViewById(R.id.linearLayout19);
        mainLayout = findViewById(R.id.constraintLayout13);
        waitingForHost = findViewById(R.id.expandableLayout3);
        checkMatchDetails = findViewById(R.id.expandableLayout2);

        loadingDialog = new LoadingDialog(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        showLayoutAndHideProgressbar();
        getDataFromFragment();


        //Setting Toolbar
        toolbar.setNavigationOnClickListener(view -> AreYouSureExitDialog());

        readyToPlay.setOnClickListener(view -> {
                if (readyToPlay.getText().toString().equals(getString(R.string.READY_TO_PLAY))) {
                readyToPlay.setText(getString(R.string.NOT_READY));
                readyToPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.accent_border, null));
                HideLayoutAndShowProgressbar();
                addJoinedUserIdToDatabase();
            } else {
                readyToPlay.setText(getString(R.string.READY_TO_PLAY));
                readyToPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.green_border, null));
                showLayoutAndHideProgressbar();
                //Removing data from Database.
                removeJoinedUserFromDatabase();
            }
        });
    }


    private void getDataFromFragment() {

        mainLayout.setVisibility(View.GONE);
        loadingDialog.startDialog();

        Intent intent = getIntent();
        MatchDetail matchDetail = intent.getParcelableExtra(Constants.MATCH_DETAIL_OBJECT);
        if (matchDetail != null) {
            gamesObj = matchDetail.getGame();
            this.matchDetail = matchDetail;

            //Instantiating User Database Reference
            userReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS));
            matchesReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES))
                    .child(matchDetail.getMatch_ID());

            getCurrentUserData();
        }

    }


    private void changeMatchStatusToConnecting() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getString(R.string.MATCH_STATUS), Constants.MATCH_CONNECTING);
        matchesReference.updateChildren(hashMap);
    }


    public void checkIfHostAcceptsOrDeclines() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    MatchDetail matchDetail = snapshot.getValue(MatchDetail.class);
                    assert matchDetail != null;
                    if (matchDetail.isHostAccepted()) {
                        hostAccepted = true;
                        if (isTimerRunning) {
                            countDownTimer.cancel();
                            isTimerRunning = false;
                        }
                        HashMap<String, Object> statusOngoing = new HashMap<>();
                        statusOngoing.put(getString(R.string.MATCH_STATUS), Constants.MATCH_STARTED);
                        matchesReference.updateChildren(statusOngoing);
                        Intent intent = new Intent(JoinMatch.this, MatchStarted.class);
                        intent.putExtra(Constants.MATCH_DETAIL_OBJECT, matchDetail);
                        startActivity(intent);
                        finish();
                    }
                    if (matchDetail.isHostRejected()) {
                        updateMatchCancelledDetails();
                        ConfirmAlertDialog hostRejectedDialog = new ConfirmAlertDialog(Constants.HOST_REJECTED,
                                () -> {
                                    balance(true);
                                    isTimerRunning = false;
                                    finish();
                                });
                        hostRejectedDialog.show(getSupportFragmentManager(), "HOST_REJECTED");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        matchesReference.addValueEventListener(valueEventListener);
    }


    private void AreYouSureExitDialog() {


        ConfirmDialog confirmDialog = new ConfirmDialog(Constants.CONFIRM_EXIT_MATCH, () -> {
            if (isTimerRunning) {
                isTimerRunning = false;
                countDownTimer.cancel();
            }
            TimerFinished();
        });
        confirmDialog.show(getSupportFragmentManager(), "EXIT_DIALOG");
    }


    private void showLayoutAndHideProgressbar() {

        infoTxt.setDuration(500);
        infoTxt.setExpanded(true, true);
        checkMatchDetails.setDuration(500);
        checkMatchDetails.setExpanded(true, true);
        waitingForHost.setExpanded(false, true);
    }


    private void HideLayoutAndShowProgressbar() {
        infoTxt.setDuration(500);
        infoTxt.setExpanded(false, true);
        checkMatchDetails.setDuration(500);
        checkMatchDetails.setExpanded(false, true);
        waitingForHost.setExpanded(true, true);
    }


    private void addJoinedUserIdToDatabase() {

        HashMap<String, Object> userReadyToPlay = new HashMap<>();
        userReadyToPlay.put(getString(R.string.JOINED_USER_ID), firebaseUser.getUid());
        matchesReference.updateChildren(userReadyToPlay).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notifyHost();
            } else
                Toast.makeText(JoinMatch.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
        });
    }


    private void notifyHost() {

        userReference.child(matchDetail.getHostUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User hostUser = snapshot.getValue(User.class);

                    String title = getString(R.string.MATCH_REQUEST_TITLE, gamesObj.getName());
                    String body = getString(R.string.MATCH_REQUEST_BODY, user.getUsername());

                    assert hostUser != null;
                    NotificationReq notificationReq = new NotificationReq(hostUser.getDeviceToken(),
                            new NotificationReq.Notification(title, body, "", Constants.NOTIFICATION_SOUND),
                            null);

                    RetrofitHelper.getRetrofit(Constants.BASE_URL)
                            .create(NotificationRequest.class)
                            .sent(notificationReq)
                            .enqueue(new Callback<NotificationResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                                    if (response.code() == 200)
                                        Toast.makeText(JoinMatch.this, "Match Request sent Successfully.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                                    Toast.makeText(JoinMatch.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void removeJoinedUserFromDatabase() {
        HashMap<String, Object> uId = new HashMap<>();
        uId.put(getString(R.string.JOINED_USER_ID), "");
        matchesReference.updateChildren(uId);
    }


    private void attachChatFragment(User opponentUser) {

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setUserObject(opponentUser, JOIN_MATCH);
        getSupportFragmentManager().beginTransaction().add(R.id.linearLayout19, chatFragment).commit();

        checkIfUserIsRejectedOrNot();
    }


    private void getCurrentUserData() {

        userReference.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User currentUser = snapshot.getValue(User.class);
                        assert currentUser != null;
                        user = currentUser;
                        if (user.getProfilePic() == null)
                            FirebaseHelperClass.profilePicIsNull();

                        setWidgets();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(JoinMatch.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                });
    }


    private void setWidgets() {

        //Setting Current User
        gameName.setText(gamesObj.getName());
        currentUserTxt.setText(user.getUsername());
        if (!user.getProfilePic().equals(""))
            Glide.with(this).load(user.getProfilePic()).into(currentUserImg);

        //getting Opponent User Info
        getOpponentUserData();
    }


    private void getOpponentUserData() {

        userReference.child(matchDetail.getHostUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User opponentUser = snapshot.getValue(User.class);
                assert opponentUser != null;
                opponentUsername.setText(opponentUser.getUsername());
                if (opponentUser.getProfilePic() == null)
                    FirebaseHelperClass.profilePicIsNull();

                if (opponentUser.getProfilePic() != null) {
                    if (!opponentUser.getProfilePic().equals(""))
                        Glide.with(JoinMatch.this).load(opponentUser.getProfilePic()).into(opponentImg);
                }

                attachChatFragment(opponentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void showAlertDialog() {

        ConfirmAlertDialog dialog = new ConfirmAlertDialog(0, new ConfirmAlertDialog.ConfirmDialogListener() {
            @Override
            public void okBtn() {
                startTimer();
            }
        });
        dialog.show(getSupportFragmentManager(), "CONFIRM_DIALOG");
    }


    private void startTimer() {

        isTimerRunning = true;

        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMillis = l;
                updateCountdownTextView();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                countDownTimer.cancel();
                if (!hostAccepted)
                    TimerFinished();
            }
        };
        countDownTimer.start();
    }


    private void TimerFinished() {

        updateMatchCancelledDetails();
        removeJoinedUserFromDatabase();
        matchCancelledDialog();
    }


    private void matchCancelledDialog() {

        ConfirmAlertDialog dialog = new ConfirmAlertDialog(1, new ConfirmAlertDialog.ConfirmDialogListener() {
            @Override
            public void okBtn() {
                DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

                userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        user.setAccount_balance(user.getAccount_balance() + (long) matchDetail.getEntryFee());
                        userDetails.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                isTimerRunning = false;
                finish();
            }
        });

        dialog.show(getSupportFragmentManager(), "CONFIRM_DIALOG");
    }


    //  Converting Millis into min and secs to store in TextView.
    private void updateCountdownTextView() {

        int min = (int) ((mTimeLeftInMillis / 1000) / 60);
        int sec = (int) ((mTimeLeftInMillis / 1000) % 60);

        clock.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));
    }


    public void checkIfUserIsRejectedOrNot() {

        matchesReference.child(getString(R.string.REJECTED_USERS)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rejectedUserIdList.clear();
                boolean flag = false;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userID = dataSnapshot.getKey();
                        if (userID != null) {
                            if (userID.equals(firebaseUser.getUid())) {
                                flag = true;
                            }
                        }
                    }
                    if (flag) {
                        matchCancelledDialog();
                    } else {
                        showAlertDialog();
                        changeMatchStatusToConnecting();
                    }
                } else {
                    showAlertDialog();
                    changeMatchStatusToConnecting();
                }
                mainLayout.setVisibility(View.VISIBLE);
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JoinMatch.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        });

        checkIfHostAcceptsOrDeclines();
    }


    public void updateMatchCancelledDetails() {

        HashMap<String, Object> hashMap = new HashMap<>();
        HashMap<String, Object> updateRejectedUsersList = new HashMap<>();
        hashMap.put(getString(R.string.MATCH_STATUS), Constants.MATCH_WAITING);
        updateRejectedUsersList.put(firebaseUser.getUid(), true);

        matchesReference.updateChildren(hashMap);
        matchesReference.child(getString(R.string.REJECTED_USERS)).updateChildren(updateRejectedUsersList);

        changeHostRejectedBackToFalse();
    }


    private void changeHostRejectedBackToFalse() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("hostRejected", false);
        matchesReference.updateChildren(hashMap);
    }


    @Override
    public void onBackPressed() {

        AreYouSureExitDialog();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isTimerRunning) {
            isTimerRunning = false;
            countDownTimer.cancel();
        }

        if (isTimerRunning && !hostAccepted) {
            updateMatchCancelledDetails();
            removeJoinedUserFromDatabase();
        }
        matchesReference.removeEventListener(valueEventListener);
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseHelperClass.changeStatus(Constants.ONLINE);
    }

    private void balance(boolean win) {
        if (win) {
            userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    user.setAccount_balance(user.getAccount_balance() + matchDetail.getEntryFee());
                    userDetails.setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}