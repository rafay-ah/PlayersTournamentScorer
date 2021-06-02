package com.players.nest.InviteUsers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;

import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.EntryDialog;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.HelperClasses.MatchSettingsDialog;
import com.players.nest.HelperClasses.NotificationHelper;
import com.players.nest.ModelClasses.GameFormats;
import com.players.nest.ModelClasses.Games;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.ProfileFragment.WalletActivity;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Objects;

public class InviteUsers extends AppCompatActivity implements View.OnClickListener,
        GamesListDialog.GetSelectedGame, EntryDialog.entryDialogInterface,
        MatchSettingsDialog.FragmentListener {

    Games game;
    String selectedFormat = "", selectedRules = "";
    double winningAmt, entryAmt;

    User user;
    Spinner spinner;
    Toolbar toolbar;
    EditText customRules;
    ProgressBar progressBar;
    String selectedGameId = null;
    ExpandableLayout expandableLayout;
    Button entryBtn, matchSettingBtn;
    NestedScrollView nestedScrollView;
    LinearLayout sendInvitationLayout;
    TextView gamesListTxt, selectedFormatTxt, selectedRulesTxt, entryAmtTxt;
    FirebaseUser firebaseCurrentUser;
    DatabaseReference userDetails;

    ArrayList<Games> gamesArrayList = new ArrayList<>();
    ArrayList<GameFormats> gamesRules = new ArrayList<>();
    ArrayList<GameFormats> gamesFormat = new ArrayList<>();
    ArrayList<GameFormats> sortedFormatList = new ArrayList<>();
    ArrayList<GameFormats> sortedRulesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_users);

        spinner = findViewById(R.id.spinner3);
        toolbar = findViewById(R.id.toolbar22);
        entryBtn = findViewById(R.id.button19);
        customRules = findViewById(R.id.editText3);
        entryAmtTxt = findViewById(R.id.textView73);
        matchSettingBtn = findViewById(R.id.button20);
        gamesListTxt = findViewById(R.id.textView198);
        selectedFormatTxt = findViewById(R.id.textView68);
        selectedRulesTxt = findViewById(R.id.textView84);
        progressBar = findViewById(R.id.progressBar20);
        expandableLayout = findViewById(R.id.expandableLayout12);
        sendInvitationLayout = findViewById(R.id.linearLayout38);
        nestedScrollView = findViewById(R.id.nestedScrollView11);
        firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseCurrentUser.getUid());


        //setToolbar
        toolbar.setNavigationOnClickListener(view -> finish());


        //OnClickListeners
        entryBtn.setOnClickListener(this);
        gamesListTxt.setOnClickListener(this);
        matchSettingBtn.setOnClickListener(this);
        sendInvitationLayout.setOnClickListener(this);

        getUserIntent();
    }


    private void getUserIntent() {

        Intent intent = getIntent();
        if (intent != null) {
            this.user = intent.getParcelableExtra(Constants.USER_OBJECT);
            assert user != null;
            toolbar.setTitle("Invite " + user.getUsername());
            getGamesFromDatabase();
        } else
            Toast.makeText(this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
    }


    private void getGamesFromDatabase() {
        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_GAMES))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gamesArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Games games = dataSnapshot.getValue(Games.class);
                            gamesArrayList.add(games);
                        }
                        getFormat();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(InviteUsers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    private void getFormat() {

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_GAMES_FORMAT))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gamesFormat.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                GameFormats gameFormatOb = dataSnapshot1.getValue(GameFormats.class);
                                gamesFormat.add(gameFormatOb);
                            }
                        }
                        getRules();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(InviteUsers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    private void getRules() {

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_GAMES_RULES))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gamesRules.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                GameFormats gameRulesOb = dataSnapshot1.getValue(GameFormats.class);
                                gamesRules.add(gameRulesOb);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        sendInvitationLayout.setVisibility(View.VISIBLE);
                        setWidgets();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(InviteUsers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setWidgets() {

        //Setting Spinner
        String[] spinnerList = {"PS4", "XBOX ONE", "PS5", "XBOX X/S Series", "PC"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView1 = (TextView) view;
                if (textView1 != null)
                    textView1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textColor, null));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Setting Game Txt onStartup
        gamesListTxt.setText(gamesArrayList.get(0).getName());
        game = gamesArrayList.get(0);
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.textView198) {
            GamesListDialog dialog = new GamesListDialog(this, gamesArrayList, this);
            dialog.show(getSupportFragmentManager(), "GAMES_LIST_DIALOG");

        } else if (id == R.id.button19) {
            EntryDialog entryDialog = new EntryDialog();
            entryDialog.show(getSupportFragmentManager(), "ENTRY_DIALOG");

        } else if (id == R.id.button20) {
            if (selectedGameId != null)
                setSortedFormatAndRulesList();
            else
                setGameFormatDialog2();

        } else if (id == R.id.linearLayout38) {
            if (!entryBtn.getText().toString().equals("ENTRY") && expandableLayout.isExpanded()) {
                ConfirmDialog confirmDialog = new ConfirmDialog(Constants.SEND_INVITATION,
                        this::storeMatchDetailIntoDatabase);
                confirmDialog.show(getSupportFragmentManager(), "CONFIRM_DIALOG");
            } else
                Toast.makeText(this, "Please select entry amount and match settings to proceed.",
                        Toast.LENGTH_SHORT).show();
        }
    }


    private void storeMatchDetailIntoDatabase() {
        makeGame(userDetails, (long) entryAmt);
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.changeMsg("Sending request. Please wait.");
        loadingDialog.startDialog();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.DB_MATCHES));

        String matchID = reference.push().getKey();
        assert firebaseUser != null;

        MatchDetail matchDetails;


        matchDetails = new MatchDetail(game, entryAmt, winningAmt, String.valueOf(System.currentTimeMillis()),
                matchID, firebaseUser.getUid(), user.getUser_id(), selectedFormat, selectedRules,
                customRules.getText().toString(), spinner.getSelectedItem().toString(), false,
                false, Constants.MATCH_INVITATION, null, false,
                false, "", "");

        assert matchID != null;
        reference.child(matchID).setValue(matchDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingDialog.dismissDialog();
                new NotificationHelper().sendNotification(getApplicationContext(), user.getDeviceToken(), "Match Invitation",
                        user.getUsername() + " has sent you a match request.",
                        null, null);
                Toast.makeText(this, "Invitation Sent", Toast.LENGTH_SHORT).show();
                Log.d("CEKK", "storeMatchDetailIntoDatabase: " + user.getUsername());
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.INVITED_MATCH_ID, matchID);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                loadingDialog.dismissDialog();
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    //When nothing is selected from the gameList
    private void setGameFormatDialog2() {

        String txtName = gamesListTxt.getText().toString();
        for (Games game : gamesArrayList) {
            if (txtName.equals(game.getName()))
                selectedGameId = game.getGameID();
        }

        if (selectedGameId != null)
            setSortedFormatAndRulesList();
        else
            Toast.makeText(this, "No Settings Found for the Game.Please try again later.",
                    Toast.LENGTH_SHORT).show();
    }


    private void setSortedFormatAndRulesList() {

        sortedFormatList.clear();
        sortedRulesList.clear();

        for (GameFormats gameFormats : gamesFormat) {
            if (selectedGameId != null) {
                if (selectedGameId.equals(gameFormats.getGameID())) {
                    sortedFormatList.add(gameFormats);
                }
            }
        }

        for (GameFormats gameFormats : gamesRules) {
            if (selectedGameId != null) {
                if (selectedGameId.equals(gameFormats.getGameID())) {
                    sortedRulesList.add(gameFormats);
                }
            }
        }

        MatchSettingsDialog dialog = new MatchSettingsDialog(this, sortedFormatList, sortedRulesList);
        dialog.show(getSupportFragmentManager(), "MATCH_SETTING_DIALOG");
    }


    private void showHiddenLayout() {
        entryAmtTxt.setText(String.valueOf(entryAmt));
        selectedFormatTxt.setText(selectedFormat);
        selectedRulesTxt.setText(selectedRules);
        expandableLayout.setDuration(1000);
        expandableLayout.setExpanded(true, true);
    }


    private void checkUserSelections() {

        if (!entryBtn.getText().toString().equals("ENTRY") && !selectedFormat.equals("") && !selectedRules.equals("")) {
            showHiddenLayout();
        }
    }


    @Override
    public void getGame(Games game) {
        this.game = game;
        gamesListTxt.setText(game.getName());
        expandableLayout.setExpanded(false);
        selectedGameId = game.getGameID();

        entryBtn.setText("ENTRY");
        selectedFormat = "";
        selectedRules = "";
    }

    @Override
    public void entryAmt(String txt, double amt, double winningFee) {
        userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                boolean check = (long) amt <= user.getAccount_balance();
                if (check) {
                    entryBtn.setText(txt);
                    entryBtn.setTextSize(20);
                    entryAmt = amt;
                    winningAmt = winningFee;
                } else {
                    Alert("Oops", "Your balance not enough", "cancel", "deposit");
                }
                checkUserSelections();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void dataFromMatchSettingDialog(String format, String rule) {
        selectedFormat = format;
        if (rule != null) {
            selectedRules = rule;
        } else {
            selectedRules = "No additional rules";
        }
        checkUserSelections();
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
    }

    private void makeGame(DatabaseReference user, long amount) {
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User usr = snapshot.getValue(User.class);
                usr.setAccount_balance(usr.getAccount_balance() - amount);
                user.setValue(usr);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Alert(String title, String msg, String no, String yes) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton(no, null)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent a = new Intent(InviteUsers.this, WalletActivity.class);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }).create().show();
    }
}