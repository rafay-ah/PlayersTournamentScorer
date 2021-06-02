package com.players.nest.JoinMatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.players.nest.HelperClasses.ConfirmAlertDialog;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.HelperClasses.NotificationHelper;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.ProfileFragment.WalletActivity;
import com.players.nest.SearchActivity.ViewProfileActivity;

import java.util.Objects;

public class MatchDetails extends AppCompatActivity {

    User hostUser;
    MatchDetail matchDetail;

    Toolbar toolbar;
    ProgressBar progressBar;
    ImageView gameImg, hostProPic;
    NestedScrollView nestedScrollView;
    TextView gameName, hostUsername, hostFullName, format, matchReqTxt,
            rules, console, entryFee, winningAmt, time, matchStatus,
            customRulesTxt, customRules;
    LinearLayout joinMatch, inviteAcceptRejectLay, acceptLayout, rejectLayout;

    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        time = findViewById(R.id.textView151);
        toolbar = findViewById(R.id.toolbar17);
        gameImg = findViewById(R.id.imageView44);
        hostProPic = findViewById(R.id.imageView45);
        hostFullName = findViewById(R.id.textView145);
        hostUsername = findViewById(R.id.textView143);
        gameName = findViewById(R.id.textView142);
        format = findViewById(R.id.textView155);
        rules = findViewById(R.id.textView154);
        console = findViewById(R.id.textView153);
        entryFee = findViewById(R.id.textView152);
        winningAmt = findViewById(R.id.textView150);
        matchReqTxt = findViewById(R.id.textView202);
        matchStatus = findViewById(R.id.textView157);
        joinMatch = findViewById(R.id.linearLayout22);
        progressBar = findViewById(R.id.progressBar8);
        customRules = findViewById(R.id.textView204);
        acceptLayout = findViewById(R.id.linearLayout40);
        rejectLayout = findViewById(R.id.linearLayout41);
        customRulesTxt = findViewById(R.id.textView203);
        inviteAcceptRejectLay = findViewById(R.id.linearLayout39);
        nestedScrollView = findViewById(R.id.nestedScrollView5);


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());


        getDataFromFindMatchFragment();


        joinMatch.setOnClickListener(view -> {
            if (matchStatus.getText().toString().equals(Constants.MATCH_WAITING)) {
                FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseCurrentUser.getUid());
                userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        boolean check = (long) matchDetail.getEntryFee() <= user.getAccount_balance();
                        if (check) {
                            user.setAccount_balance(user.getAccount_balance() - (long) matchDetail.getEntryFee());
                            Intent intent = new Intent(MatchDetails.this, JoinMatch.class);
                            intent.putExtra(Constants.MATCH_DETAIL_OBJECT, matchDetail);
                            startActivity(intent);
                            finish();
                        } else {
                            Alert("Oops", "Your Money Not Enough", "Ok", "Deposit");
                        }
                        userDetails.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else
                showAlertDialog();
        });
        acceptLayout.setOnClickListener(v -> {
            ConfirmDialog dialog = new ConfirmDialog(Constants.JOIN_MATCH, new ConfirmDialog.ConfirmDialogInterface() {
                @Override
                public void onConfirmed() {
                    FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseCurrentUser.getUid());
                    userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            boolean check = (long) matchDetail.getEntryFee() <= user.getAccount_balance();
                            if (check) {
                                user.setAccount_balance(user.getAccount_balance() - (long) matchDetail.getEntryFee());
                                Intent intent = new Intent(MatchDetails.this, JoinMatch.class);
                                intent.putExtra(Constants.MATCH_DETAIL_OBJECT, matchDetail);
                                startActivity(intent);
                            } else {
                                Alert("Oops", "Your Money Not Enough", "Ok", "Deposit");
                            }
                            userDetails.setValue(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            dialog.show(getSupportFragmentManager(), "CONFIRM_DIALOG");
        });
        rejectLayout.setOnClickListener(v -> {
            databaseReference.removeEventListener(valueEventListener);
            hostUser.setAccount_balance(hostUser.getAccount_balance() + matchDetail.getEntryFee());
            FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS)).child(matchDetail.getHostUserId()).setValue(hostUser);
            notifyHost();
        });
        hostProPic.setOnClickListener(v -> openViewProfileFragment());
        hostUsername.setOnClickListener(v -> openViewProfileFragment());


        //Status Listener
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchDetail matchDetail = snapshot.getValue(MatchDetail.class);
                assert matchDetail != null;
                matchStatus.setText(matchDetail.getMatchStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES)).child(matchDetail.getMatch_ID());
        databaseReference.addValueEventListener(valueEventListener);

    }


    private void openViewProfileFragment() {
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra(Constants.USER_OBJECT, hostUser);
        startActivity(intent);
    }


    private void notifyHost() {

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startDialog();

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            new NotificationHelper().sendNotification(getApplicationContext(), hostUser.getDeviceToken(),
                                    "Match Request Rejected",
                                    user.getUsername() + " has rejected your match request.",
                                    null, null);
                            databaseReference.removeValue();
                            loadingDialog.dismissDialog();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(MatchDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showAlertDialog() {
        ConfirmAlertDialog confirmAlertDialog = new ConfirmAlertDialog(Constants.MATCH_STARTED_DIALOG,
                () -> {
                });
        confirmAlertDialog.show(getSupportFragmentManager(), "DIALOG");
    }


    private void getDataFromFindMatchFragment() {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        MatchDetail matchDetail = intent.getParcelableExtra(Constants.MATCH_DETAIL_OBJECT);
        String type = intent.getStringExtra(Constants.MATCH_REQUEST);
        if (matchDetail != null) {
            this.matchDetail = matchDetail;
            setInvitationWidget(type);
            setWidget();
        }
    }


    private void setInvitationWidget(String type) {

        if (type != null) {
            matchReqTxt.setVisibility(View.VISIBLE);
            joinMatch.setVisibility(View.INVISIBLE);
            customRulesTxt.setVisibility(View.VISIBLE);
            customRules.setVisibility(View.VISIBLE);
            customRules.setText(matchDetail.getCustomRules());
            if (matchDetail.getCustomRules().equals(""))
                customRules.setText("No custom rules defined.");
            inviteAcceptRejectLay.setVisibility(View.VISIBLE);
        } else {
            matchReqTxt.setVisibility(View.GONE);
            joinMatch.setVisibility(View.VISIBLE);
            customRules.setVisibility(View.GONE);
            customRulesTxt.setVisibility(View.GONE);
            inviteAcceptRejectLay.setVisibility(View.GONE);
        }
    }


    private void setWidget() {
        gameName.setText(matchDetail.getGame().getName());
        Glide.with(this).load(matchDetail.getGame().getGameImg()).into(gameImg);
        format.setText(matchDetail.getFormat());
        rules.setText(matchDetail.getRules());
        console.setText(matchDetail.getConsole());
        entryFee.setText("$ " + matchDetail.getEntryFee());
        winningAmt.setText(getString(R.string.WINNING_AMOUNT, matchDetail.getWinningAmt()));
        time.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(matchDetail.getTimeCreated())
                , System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
        matchStatus.setText(matchDetail.getMatchStatus());

        getHostUserInfo();
    }


    private void getHostUserInfo() {

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(matchDetail.getHostUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    hostUser = user;
                    assert user != null;
                    hostUsername.setText(user.getUsername());
                    hostFullName.setText(user.getFullName());
                    matchReqTxt.setText(getString(R.string.MATCH_REQUEST_MSG, user.getUsername()));


                    if (!user.getProfilePic().equals(""))
                        Glide.with(MatchDetails.this).load(user.getProfilePic()).into(hostProPic);
                }
                progressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MatchDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseHelperClass.changeStatus(Constants.ONLINE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
        databaseReference.removeEventListener(valueEventListener);
    }

    public void Alert(String title, String msg, String no, String yes) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton(no, null)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent a = new Intent(MatchDetails.this, WalletActivity.class);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }).create().show();
    }
}