package com.players.nest.Tournament;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.players.nest.MainActivity;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.TournamentMatches;
import com.players.nest.ModelClasses.User;
import com.players.nest.R;
import com.players.nest.Tournament.Adapter.PlayersJoinedAdapter;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class JoinTournamentActivity extends AppCompatActivity {
    private static final String TAG = "JoinTournamentActivity";

    private TextView tournamentName,format,console,ratings,createdBy,totalPrice,entryPrice,createdWhen,daysLeft;
    private TextView days,hoursLeft,hours,minutesLeft,minutes,secondsLeft,seconds,colon1,colon2,colon3,ends;
    private TextView rules;
    private CardView click;
    private CountDownTimer timer;
    private AppCompatButton button;
    private TournamentDetail tournamentDetail;
    private RecyclerView recyclerView;
    private List<String> joinUserIdList;
    private List<User> userList;
    private List<User> filterList;
    PlayersJoinedAdapter playersJoinedAdapter;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
    private boolean isTimerOver = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_tournament);
        tournamentName = findViewById(R.id.tv_tournament_name);
        format = findViewById(R.id.tv_format);
        console = findViewById(R.id.tv_console);
        ratings = findViewById(R.id.tv_ratings);
        createdBy = findViewById(R.id.tv_created_by);
        totalPrice = findViewById(R.id.tv_total_price);
        colon1 = findViewById(R.id.tv_colon1);
        colon2 = findViewById(R.id.tv_colon2);
        colon3 = findViewById(R.id.tv_colon3);
        daysLeft = findViewById(R.id.tv_days_left);
        days = findViewById(R.id.tv_days);
        hoursLeft = findViewById(R.id.tv_hours_left);
        hours= findViewById(R.id.tv_hours);
        minutesLeft = findViewById(R.id.tv_min_left);
        minutes = findViewById(R.id.tv_min);
        secondsLeft = findViewById(R.id.tv_sec_left);
        seconds = findViewById(R.id.tv_sec);
        ends = findViewById(R.id.tv_ends);
        button = findViewById(R.id.btn_leave);
        rules = findViewById(R.id.tv_rules);
        userList = new ArrayList<>();
        joinUserIdList = new ArrayList<>();
        filterList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_players_joined);
        button.setOnClickListener((view) -> {
            ConfirmAlertDialog confirmDialog = new ConfirmAlertDialog(10,() ->{
                if(tournamentDetail != null){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tournament")
                            .child(tournamentDetail.getTournamentID());

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                TournamentDetail detail = snapshot.getValue(TournamentDetail.class);
                                if(detail!= null){
                                    if(detail.getJoinedUserID()!= null){
                                        List<String> joinedUserIDList= detail.getJoinedUserID();
                                        for(int i=0;i<joinedUserIDList.size();i++){
                                            if(joinedUserIDList.get(i).equals(firebaseUser.getUid())){
                                                joinedUserIDList.remove(i);
                                                break;
                                            }
                                        }
                                        detail.setJoinedUserID(joinedUserIDList);
                                        reference.setValue(detail).addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                updateBalance();
                                            }
                                            else
                                                Toast.makeText(JoinTournamentActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                                        });

                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(JoinTournamentActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        confirmDialog.show(getSupportFragmentManager(),"Leave Tournament");

        });
        getDataFromIntent();
        setData();
        initializeTimer();
        setListenerForPlayersJoined();
        setAdapter();


    }

    private void updateBalance() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(firebaseUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    double amt = Objects.requireNonNull(user).getAccount_balance() + tournamentDetail.getEntryFee();
                    user.setAccount_balance(amt);
                    userRef.setValue(user).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(JoinTournamentActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(JoinTournamentActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTournamentMatches();
    }

    private void setAdapter() {
        playersJoinedAdapter = new PlayersJoinedAdapter(this,filterList);
        recyclerView.setAdapter(playersJoinedAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setListenerForPlayersJoined() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tournament")
                .child(tournamentDetail.getTournamentID());

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TournamentDetail detail = snapshot.getValue(TournamentDetail.class);
                    if(detail!= null){
                        if(detail.getJoinedUserID()!= null){
                            joinUserIdList = detail.getJoinedUserID();
                            getUserData();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JoinTournamentActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
            }
        });
    }


    private boolean checkIfTimerIsOver() {
        //TODO Implement check timer
        return isTimerOver;
    }

    private void getUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshotItems : snapshot.getChildren()){
                    User user = dataSnapshotItems.getValue(User.class);
                    userList.add(user);
                }
                filterUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setTournamentMatches(){
        if(checkIfTimerIsOver()){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tournament")
                    .child(tournamentDetail.getTournamentID());
            tournamentDetail.setTournamentStatus(Constants.MATCH_STARTED);
            List<String> userIDList = tournamentDetail.getJoinedUserID();
            List<TournamentMatches> tournamentMatchesList = new ArrayList<>();
            if(tournamentDetail.getMatchDetails() == null || tournamentDetail.getMatchDetails().size() == 0){
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: ");
                        if(snapshot.exists()){
                            TournamentDetail tournamentDetail1 = snapshot.getValue(TournamentDetail.class);
                            if(Objects.requireNonNull(tournamentDetail1).getMatchDetails() == null || tournamentDetail1.getMatchDetails().size() == 0){
                                for(int i=0,k=0;i<tournamentDetail1.getMaxPlayers();i=i+2,k++){
                                    String userOne = "Not Joined";
                                    if(i<userIDList.size()){
                                        userOne = userIDList.get(i);
                                    }
                                    String userTwo = "Not Joined";
                                    if(i+1<userIDList.size()){
                                        userTwo = userIDList.get(i+1);
                                    }
                                    TournamentMatches tournamentMatches = new TournamentMatches("","",
                                            false,false,false,false,
                                            false, false,null,
                                            k+"",userOne,userTwo,Constants.MATCH_WAITING,0,null);
                                    tournamentMatchesList.add(tournamentMatches);
                                }
                                int num = tournamentDetail1.getMaxPlayers()/4;
                                for(int i=num,k=1;i>0;i/=2,k++){
                                    for(int j=0;j<i;j++){
                                        int index = tournamentMatchesList.size();
                                        TournamentMatches tournamentMatches = new TournamentMatches("","",
                                                false,false,false,false,
                                                false, false,null,
                                                index+"","","",Constants.MATCH_WAITING_FOR_PLAYERS,k,null);
                                        tournamentMatchesList.add(tournamentMatches);
                                    }
                                }
                                tournamentDetail.setMatchDetails(tournamentMatchesList);
                                reference.setValue(tournamentDetail).addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(JoinTournamentActivity.this,TournamentStartedActivity.class);
                                        intent.putExtra("TOURNAMENT_DETAILS",tournamentDetail);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Log.e(TAG, "onDataChange: " + task.getException() );
                                    }
                                });
                            }else{
                                Intent intent = new Intent(JoinTournamentActivity.this,TournamentStartedActivity.class);
                                intent.putExtra("TOURNAMENT_DETAILS",tournamentDetail);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else{
                Intent intent = new Intent(JoinTournamentActivity.this,TournamentStartedActivity.class);
                intent.putExtra("TOURNAMENT_DETAILS",tournamentDetail);
                startActivity(intent);
                finish();
            }


        }
    }

    private void setData() {
        if(tournamentDetail != null){
            tournamentName.setText(tournamentDetail.getTournamentName());
            format.setText(tournamentDetail.getTournamentFormat());
            ratings.setText(tournamentDetail.getMinRating());
            totalPrice.setText("$ " +tournamentDetail.getWinningAmt());
            console.setText(tournamentDetail.getConsole());
//            createdWhen.setText(tournamentDetail.getTimeCreated());
            userRef.child(tournamentDetail.getHostUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    createdBy.setText(Objects.requireNonNull(user).getUsername());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            rules.setText(tournamentDetail.getTournamentRule());
        }
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        tournamentDetail = (TournamentDetail) intent.getParcelableExtra("TOURNAMENT_DETAILS");

    }

    private void filterUsers(){
        filterList.clear();
        for(User user: userList){
            for(String userId: joinUserIdList){
                if(user.getUser_id().equals(userId)){
                    filterList.add(user);
                }
            }
        }
        playersJoinedAdapter.notifyDataSetChanged();
        Log.d(TAG, "filterUsers: " + filterList.size());
    }
    private void initializeTimer(){
        DateTime dt = new DateTime(tournamentDetail.getTimeToStart());
        Date startDate = changeDateToLocal(dt.toDate());
        Date now = new Date();
        long started = (startDate != null ? startDate.getTime() : 0) - now.getTime();
        timer = new CountDownTimer(started,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Date now = new Date();
                long started = (startDate != null ? startDate.getTime() : 0) - now.getTime();
                long day = TimeUnit.MILLISECONDS.toDays(started);
                long hour = TimeUnit.MILLISECONDS.toHours(started) % 24;
                long minute = TimeUnit.MILLISECONDS.toMinutes(started) % 60;
                long second = TimeUnit.MILLISECONDS.toSeconds(started)%60;
                daysLeft.setText(formatNumber(day));
                hoursLeft.setText(formatNumber(hour));
                minutesLeft.setText(formatNumber(minute));
                secondsLeft.setText(formatNumber(second));
                ends.setText("TOURNAMENT STARTS IN");
                colon1.setText(":");
                colon2.setText(":");
                colon3.setText(":");
                days.setText("Days");
                hours.setText("Hours");
                minutes.setText("Minutes");
                seconds.setText("Seconds");
            }

            @Override
            public void onFinish() {
                timer.cancel();
                isTimerOver = true;
                setTournamentMatches();
            }
        };
        timer.start();
    }
    private String formatNumber(long num){
        DecimalFormat format = new DecimalFormat("00");
        return format.format(num);
    }
    private Date changeDateToLocal(Date date){
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
//        String dateString = format.format(date);
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//        try{
//            Date value = format.parse(dateString);
//            SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
//            newFormat.setTimeZone(TimeZone.getDefault());
//            String newDateString = newFormat.format(date);
//            return newFormat.parse(newDateString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return new Date(date.getTime() + Calendar.getInstance().getTimeZone().getOffset(new Date().getTime()));
    }
}