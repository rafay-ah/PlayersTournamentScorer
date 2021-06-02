package com.players.nest.GameFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.FindMatchFragment.AlreadyCreatedMatchesActivity;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.EntryDialog;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.HelperClasses.MatchSettingsDialog;
import com.players.nest.ModelClasses.GameFormats;
import com.players.nest.ModelClasses.Games;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.MyMatchesActivity.MyMatchesActivity;
import com.players.nest.ProfileFragment.WalletActivity;
import com.players.nest.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Objects;

import static com.players.nest.GameFragment.TopGamesAdapter.SELECTED_GAME_OBJECT;

public class
MatchMakingFragment extends Fragment implements EntryDialog.entryDialogInterface,
        MatchSettingsDialog.FragmentListener {

    private static final String ENTRY_DIALOG = "Entry_Dialog";
    private static final String CONFIRM_DIALOG = "Confirm_Dialog";
    private static final String MATCH_SETTING_DIALOG = "MATCH_SETTING_DIALOG";
    private static final String TAG = "MATCH_MAKING_FRAG";

    Games game;
    ImageView imgRes;
    Spinner consoleSpinner;
    LoadingDialog loadingDialog;
    ConstraintLayout constraintLayout;
    ExpandableLayout expandableLayoutMatchDetails;
    NestedScrollView nestedScrollView;
    TextView gameName, alreadyCreatedMatch;
    Button entryBtn, matchBtn, matchSettingBtn;
    FirebaseUser firebaseCurrentUser;
    DatabaseReference userDetails;

    String formatDescStr = "";
    String ruleDescStr = "";
    double entryFee, winningAmt;
    TextView formatDesc, entryFeeTxt, additionalRulesTxt;

    ArrayList<GameFormats> gameFormatsArrayList = new ArrayList<>();
    ArrayList<GameFormats> gameRulesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_making, container, false);

        imgRes = view.findViewById(R.id.iv_game_pic);
        gameName = view.findViewById(R.id.textView65);
        entryBtn = view.findViewById(R.id.bt_entry);
        matchBtn = view.findViewById(R.id.btn_create);
        formatDesc = view.findViewById(R.id.textView68);
        entryFeeTxt = view.findViewById(R.id.textView73);
        matchSettingBtn = view.findViewById(R.id.bt_match_setting);
        consoleSpinner = view.findViewById(R.id.sp_device);
        additionalRulesTxt = view.findViewById(R.id.textView84);
        alreadyCreatedMatch = view.findViewById(R.id.tv_already_join);
        nestedScrollView = view.findViewById(R.id.nestedScrollView4);
        constraintLayout = view.findViewById(R.id.constraintLayout10);
        expandableLayoutMatchDetails = view.findViewById(R.id.expandableLayout12);
        firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseCurrentUser.getUid());

        loadingDialog = new LoadingDialog(getActivity());

        getDataFromActivity();
        onClickListeners();

        return view;
    }


    private void onClickListeners() {

        alreadyCreatedMatch.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AlreadyCreatedMatchesActivity.class);
            intent.putExtra(SELECTED_GAME_OBJECT, game);
            startActivity(intent);
        });


        entryBtn.setOnClickListener(view -> {
            EntryDialog entryDialog = new EntryDialog();
            assert getFragmentManager() != null;
            entryDialog.setTargetFragment(MatchMakingFragment.this, 100);
            entryDialog.show(getFragmentManager(), ENTRY_DIALOG);
        });
        matchBtn.setOnClickListener(view -> {
            if (entryBtn.getText().equals("ENTRY")) {
                Toast.makeText(getContext(), "Please enter entry amount.", Toast.LENGTH_SHORT).show();
            } else if (!checkValuesAndShowHiddenLayout()) {
                Toast.makeText(getContext(), "Please choose match format.", Toast.LENGTH_SHORT).show();
            } else {
                ConfirmDialog confirmDialog = new ConfirmDialog(Constants.CREATE_MATCH_BUTTON,
                        this::storeMatchDetailIntoDatabase);
                assert getFragmentManager() != null;
                confirmDialog.show(getFragmentManager(), CONFIRM_DIALOG);
            }
        });
        matchSettingBtn.setOnClickListener(view -> {
            MatchSettingsDialog settingsDialog = new MatchSettingsDialog(getContext(), gameFormatsArrayList, gameRulesList);
            settingsDialog.setTargetFragment(MatchMakingFragment.this, 10);
            assert getFragmentManager() != null;
            settingsDialog.show(getFragmentManager(), MATCH_SETTING_DIALOG);
        });
    }


    private void storeMatchDetailIntoDatabase() {
        entry(userDetails, (long) entryFee);
        matchBtn.setEnabled(false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.DB_MATCHES));

        String matchID = reference.push().getKey();
        assert firebaseUser != null;
        MatchDetail matchDetails = new MatchDetail(game, entryFee, winningAmt, String.valueOf(System.currentTimeMillis()), matchID,
                firebaseUser.getUid(), "", formatDescStr, ruleDescStr, null, consoleSpinner.getSelectedItem().toString(),
                false, false, Constants.MATCH_WAITING, null,
                false, false, "", "");

        assert matchID != null;
        reference.child(matchID).setValue(matchDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingDialog.dismissDialog();
                startActivity(new Intent(getContext(), MyMatchesActivity.class));
                Objects.requireNonNull(getActivity()).finish();
                matchBtn.setEnabled(true);
            } else {
                matchBtn.setEnabled(true);
                loadingDialog.dismissDialog();
                Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getDataFromActivity() {
        FragmentActivity activity = getActivity();
        assert activity != null;
        ((GameActivity) activity).passData(gameObj -> {
            game = gameObj;
            setWidgets();
        });
    }


    private void setWidgets() {

        Glide.with(Objects.requireNonNull(getContext())).load(game.getGameImg()).into(imgRes);
        gameName.setText(game.getName());

        String[] spinnerList = {"PS4", "XBOX ONE", "PS5", "XBOX Series X/S", "PC", "XBOXPS Controller"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerList);
        consoleSpinner.setAdapter(spinnerAdapter);

        consoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView1 = (TextView) view;
                if (expandableLayoutMatchDetails.isExpanded())
                    nestedScrollView.fullScroll(View.FOCUS_DOWN);
                if (textView1 != null)
                    textView1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textColor, null));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        getFormat();
        getRulesOfGames();
    }


    private void getFormat() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_GAMES_FORMAT))
                .child(game.getGameID());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameFormatsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GameFormats gameFormats = dataSnapshot.getValue(GameFormats.class);
                    gameFormatsArrayList.add(gameFormats);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getRulesOfGames() {

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_GAMES_RULES))
                .child(game.getGameID());
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameRulesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GameFormats gameFormats = dataSnapshot.getValue(GameFormats.class);
                    gameRulesList.add(gameFormats);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    entryFee = amt;
                    winningAmt = winningFee;
                    checkValuesAndShowHiddenLayout();
                } else {
                    Alert("Oops", "Your balance not enough", "cancel", "deposit");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void dataFromMatchSettingDialog(String format, String rule) {
        if (rule == null) {
            formatDescStr = format;
            ruleDescStr = "No Additional rules applied.";
        } else {
            ruleDescStr = rule;
            formatDescStr = format;
        }
        checkValuesAndShowHiddenLayout();
    }


    private boolean checkValuesAndShowHiddenLayout() {
        if (entryBtn.getText().equals("ENTRY")) {
            expandableLayoutMatchDetails.setExpanded(false, true);
        } else if (!formatDescStr.equals("") && !ruleDescStr.equals("")) {
            formatDesc.setText(formatDescStr);
            additionalRulesTxt.setText(ruleDescStr);
            entryFeeTxt.setText(entryBtn.getText().toString());
            expandableLayoutMatchDetails.setExpanded(true, true);
            runHandlerThread();
            return true;
        }
        return false;
    }


    private void runHandlerThread() {

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            nestedScrollView.setSmoothScrollingEnabled(true);
            nestedScrollView.fullScroll(View.FOCUS_DOWN);
        }, 800);
    }

    private void entry(DatabaseReference user, long amount) {
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
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton(no, null)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent a = new Intent(getContext(), WalletActivity.class);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }).create().show();
    }
}
