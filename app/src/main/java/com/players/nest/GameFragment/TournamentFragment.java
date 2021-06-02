package com.players.nest.GameFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.players.nest.HelperClasses.TournamentEntryDialog;
import com.players.nest.ModelClasses.GameFormats;
import com.players.nest.ModelClasses.Games;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.MyMatchesActivity.MyMatchesActivity;
import com.players.nest.MyTournamentActivity.MyTournamentActivity;
import com.players.nest.ProfileFragment.WalletActivity;
import com.players.nest.R;
import com.players.nest.Tournament.Adapter.JoinAlreadyCreatedAdapter;
import com.players.nest.Tournament.JoinAlreadyCreatedActivity;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static com.players.nest.GameFragment.TopGamesAdapter.SELECTED_GAME_OBJECT;

public class  TournamentFragment extends Fragment  implements TournamentEntryDialog.entryDialogInterface,
        MatchSettingsDialog.FragmentListener , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {



    private static final String ENTRY_DIALOG = "Entry_Dialog";
    private static final String CONFIRM_DIALOG = "Confirm_Dialog";
    private static final String MATCH_SETTING_DIALOG = "MATCH_SETTING_DIALOG";
    private static final String TAG = "TOURNAMENT_FRAGMENT";
    Games game;
    ImageView imgRes;
    Spinner consoleSpinner,ratingSpinner,maxPlayersSpinner;
    LoadingDialog loadingDialog;
    ConstraintLayout constraintLayout;
    ExpandableLayout expandableLayoutMatchDetails;
    NestedScrollView nestedScrollView;
    TextView gameName, alreadyCreatedMatch;
    Button entryBtn, matchBtn, matchSettingBtn,datePicker;
    FirebaseUser firebaseCurrentUser;
    DatabaseReference userDetails;
    EditText tournamentName;
    String year,month,day,hour,min;
    String formatDescStr = "";
    String ruleDescStr = "";
    double entryFee, winningAmt;
    TextView formatDesc, entryFeeTxt, additionalRulesTxt,dateTime;
    int maxPlayer=0,maxRating=0;
    ArrayList<GameFormats> gameFormatsArrayList = new ArrayList<>();
    ArrayList<GameFormats> gameRulesList = new ArrayList<>();
    private String date;
    private User users;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournament, container, false);

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
        tournamentName = view.findViewById(R.id.et_tournament_name);
        loadingDialog = new LoadingDialog(getActivity());
        maxPlayersSpinner = view.findViewById(R.id.sp_player_max);
        ratingSpinner = view.findViewById(R.id.sp_rating);
        datePicker = view.findViewById(R.id.bt_date_picker);
        dateTime = view.findViewById(R.id.tv_date_time);
        getDataFromActivity();
        onClickListeners();

        return view;
    }


    private void onClickListeners() {

        alreadyCreatedMatch.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), JoinAlreadyCreatedActivity.class);
            intent.putExtra(SELECTED_GAME_OBJECT, game);
            startActivity(intent);
        });
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), TournamentFragment.this,year, month,day);
                datePickerDialog.show();
            }
        });

        entryBtn.setOnClickListener(view -> {
            TournamentEntryDialog entryDialog = new TournamentEntryDialog();
            assert getFragmentManager() != null;
            entryDialog.setTargetFragment(TournamentFragment.this, 100);
            entryDialog.show(getFragmentManager(), ENTRY_DIALOG);
        });
        matchBtn.setOnClickListener(view -> {
            if (entryBtn.getText().equals("ENTRY")) {
                Toast.makeText(getContext(), "Please enter entry amount.", Toast.LENGTH_SHORT).show();
            } else if (!checkValuesAndShowHiddenLayout()) {
                Toast.makeText(getContext(), "Please choose Tournament format.", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(tournamentName.getText())){
                Toast.makeText(getContext(), "Please Enter tournament name.", Toast.LENGTH_SHORT).show();
            }
            else {
                ConfirmDialog confirmDialog = new ConfirmDialog(Constants.CREATE_MATCH_BUTTON,
                        this::storeMatchDetailIntoDatabase);
                assert getFragmentManager() != null;
                confirmDialog.show(getFragmentManager(), CONFIRM_DIALOG);
            }
        });
        matchSettingBtn.setOnClickListener(view -> {
            MatchSettingsDialog settingsDialog = new MatchSettingsDialog(getContext(), gameFormatsArrayList, gameRulesList);
            settingsDialog.setTargetFragment(TournamentFragment.this, 10);
            assert getFragmentManager() != null;
            settingsDialog.show(getFragmentManager(), MATCH_SETTING_DIALOG);
        });
    }


    private void storeMatchDetailIntoDatabase() {
        entry(userDetails, (long) entryFee);
        matchBtn.setEnabled(false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Tournament");
        double winningAmount = calculatedWinningAmount(entryFee,maxPlayer);

        String tournamentID = reference.push().getKey();
        assert firebaseUser != null;
        List<String> joinUserId = new ArrayList<>();
        joinUserId.add(firebaseUser.getUid());
        String userName = "";
        if(users != null){
            userName = users.getUsername();
        }
        TournamentDetail tournamentDetail = new TournamentDetail(tournamentName.getText().toString(),formatDescStr,ruleDescStr
                ,consoleSpinner.getSelectedItem().toString(),String.valueOf(maxRating),0,
                maxPlayer,date,entryFee,winningAmount,null, firebaseUser.getUid(),
                tournamentID,"",String.valueOf(System.currentTimeMillis()),game,Constants.MATCH_WAITING,joinUserId);
//        MatchDetail matchDetails = new MatchDetail(game, entryFee, winningAmt, String.valueOf(System.currentTimeMillis()), matchID,
//                firebaseUser.getUid(), "", formatDescStr, ruleDescStr, null, consoleSpinner.getSelectedItem().toString(),
//                false, false, Constants.MATCH_WAITING, null,
//                false, false, "", "");

        assert tournamentID != null;
        reference.child(tournamentID).setValue(tournamentDetail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingDialog.dismissDialog();
                startActivity(new Intent(getContext(), MyTournamentActivity.class));
                Objects.requireNonNull(getActivity()).finish();
                matchBtn.setEnabled(true);
            } else {
                matchBtn.setEnabled(true);
                loadingDialog.dismissDialog();
                Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private double calculatedWinningAmount(double entryFee, int maxPlayer) {
        return entryFee*maxPlayer*0.92;
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
        String[] maxPlayerList = {"4","8","16","32","64","128","256"};
        ArrayAdapter<String> maxPlayerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,maxPlayerList);
        maxPlayersSpinner.setAdapter(maxPlayerAdapter);
        maxPlayersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maxPlayer = Integer.parseInt(maxPlayerList[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String[] maxPlayerRating = {"40","50","60","70","80","90","100"};
        ArrayAdapter<String> maxRatingAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,maxPlayerRating);
        ratingSpinner.setAdapter(maxRatingAdapter);
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maxRating = Integer.parseInt(maxPlayerRating[position]);
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
    public void entryAmt(String txt, double amt) {
        userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                users  = user;
                boolean check = (long) amt <= user.getAccount_balance();
                if (check) {
                    entryBtn.setText(txt);
                    entryBtn.setTextSize(20);
                    entryFee = amt;
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DecimalFormat format = new DecimalFormat("0000");
        this.year = format.format(year);
        DecimalFormat formatter = new DecimalFormat("00");
        this.day = formatter.format(dayOfMonth);
        this.month = formatter.format(month+1);
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), TournamentFragment.this, hour, minute, DateFormat.is24HourFormat(getContext()));
        timePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//    this.hour = hourOfDay;
//    this.min = minute;
    DecimalFormat formatter = new DecimalFormat("00");
    this.hour = formatter.format(hourOfDay);
    this.min = formatter.format(minute);
    dateTime.setText(day+"/"+month+"/"+year+" " +hourOfDay+":"+minute );
        try {
            SimpleDateFormat format = new  SimpleDateFormat("dd/MM/yyyy hh:mm");
            Date date = format.parse(day+"/"+month+"/"+year+" " +hourOfDay+":"+minute );
            if(date != null) {
                long newDate = changeDateToGMT(date.getTime());
                DateTime dt = new DateTime(newDate);
                this.date = dt.toString();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateTime.setVisibility(View.VISIBLE);
    }
    private long changeDateToGMT(long currentTime){
        Date date = new Date(currentTime);
//        try{
//            SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
//            newFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//            String newDateString = newFormat.format(date);
//            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
//            Date newDate = format.parse(newDateString);
//            return Objects.requireNonNull(newDate).getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime());

    }
}
