package com.players.nest.Tournament.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.JoinMatch.JoinMatch;
import com.players.nest.MainActivity;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.TournamentMatches;
import com.players.nest.ModelClasses.User;
import com.players.nest.Notifications.NotificationReq;
import com.players.nest.R;
import com.players.nest.Tournament.JoinTournamentActivity;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class JoinAlreadyCreatedAdapter  extends RecyclerView.Adapter<JoinAlreadyCreatedAdapter.ViewHolder> {
    private Context context;
    private ArrayList<TournamentDetail> tournamentDetails;
    private FragmentManager fragmentManager;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public  JoinAlreadyCreatedAdapter(Context context, FragmentManager fragmentManager, ArrayList<TournamentDetail> tournamentDetails) {
        this.context = context;
        this.tournamentDetails = tournamentDetails;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tournament_details_items,parent,false);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TournamentDetail tournamentDetail = tournamentDetails.get(position);
        holder.tournamentName.setText(tournamentDetail.getTournamentName());
        holder.format.setText(tournamentDetail.getTournamentFormat());
        holder.ratings.setText(tournamentDetail.getMinRating());
        holder.entryPrice.setText("$ "+ formatDecimal(tournamentDetail.getEntryFee()) + " JOIN");
        holder.totalPrice.setText("$ " +formatDecimal(tournamentDetail.getWinningAmt()));
        holder.console.setText(tournamentDetail.getConsole());
        Date created = changeDateToLocal(new Date(Long.parseLong(tournamentDetail.getTimeCreated())));

        Date now = new Date();
        long diff = now.getTime() - created.getTime();
        if(diff>0){
            String when = "";
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff) % 24;
            long min = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
            long sec = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
            if(days >0){
                when = days + " days ago";
            }
            else if( hours > 0){
                when += hours + " hours ago";
            }
            else if(min > 0){
                when += min + " mins ago";
            }
            else{
                when += sec + " sec ago";
            }
            holder.createdWhen.setText(when);
        }

        holder.createdBy.setText(tournamentDetail.getCreatedBy());
        Glide.with(context).load(tournamentDetail.getGame().getGameImg()).into(holder.gameIcon);
        getUserInfo(tournamentDetail.getHostUserId(),holder.createdBy,holder.profilePic,position);
        holder.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog dialog = new ConfirmDialog(Constants.JOIN_MATCH, new ConfirmDialog.ConfirmDialogInterface() {
                    @Override
                    public void onConfirmed() {
                        updateBalance(tournamentDetail);
                    }
                });
                dialog.show(fragmentManager,"JOIN_MATCH");

            }
        });
            DateTime dt = new DateTime(tournamentDetail.getTimeToStart());
            Date startDate = changeDateToLocal(dt.toDate());

            long started = (startDate != null ? startDate.getTime() : 0) - now.getTime();
            if (holder.timer != null) {
                holder.timer.cancel();
            }
            holder.timer = new CountDownTimer(started,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Date now = new Date();
                        long started = (startDate != null ? startDate.getTime() : 0) - now.getTime();
                        long days = TimeUnit.MILLISECONDS.toDays(started);
                        long hours = TimeUnit.MILLISECONDS.toHours(started) % 24;
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(started) % 60;
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(started)%60;
                        holder.daysLeft.setText(formatNumber(days));
                        holder.hoursLeft.setText(formatNumber(hours));
                        holder.minutesLeft.setText(formatNumber(minutes));
                        holder.secondsLeft.setText(formatNumber(seconds));
                        holder.ends.setText("STARTS IN");
                        holder.colon1.setText(":");
                        holder.colon2.setText(":");
                        holder.colon3.setText(":");
                        holder.days.setText("Days");
                        holder.hours.setText("Hours");
                        holder.seconds.setText("Seconds");
                        holder.minutes.setText("Minutes");
                    }

                    @Override
                    public void onFinish() {
                        holder.timer.cancel();

                        holder.ends.setText("Already Started");

                    }
            };
            holder.timer.start();

    }
    private void addJoinedUserIdToDatabase(TournamentDetail tournamentDetail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<String> joinUserId;
        if(tournamentDetail.getJoinedUserID() == null){
            joinUserId = new ArrayList<>();
        }
        else{
            joinUserId= tournamentDetail.getJoinedUserID();
        }
        int flag=0;
        for(String userId: joinUserId){
            if(userId.equals(Objects.requireNonNull(user).getUid())){
                flag=1;
                break;
            }
        }
        if(flag==0) {
            joinUserId.add(Objects.requireNonNull(user).getUid());
            tournamentDetail.setJoinedUserID(joinUserId);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tournament")
                    .child(tournamentDetail.getTournamentID());
            reference.setValue(tournamentDetail).addOnSuccessListener(aVoid -> {
                Intent intent = new Intent(context, JoinTournamentActivity.class);
                intent.putExtra("TOURNAMENT_DETAILS", tournamentDetail);
                context.startActivity(intent);

            }).addOnFailureListener(aVoid -> {
                Toast.makeText(context, "Something gone wrong", Toast.LENGTH_LONG).show();
            });
        } else{
            Intent intent = new Intent(context, JoinTournamentActivity.class);
            intent.putExtra("TOURNAMENT_DETAILS", tournamentDetail);
            context.startActivity(intent);
        }
    }



    @Override
    public int getItemCount() {
        return tournamentDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tournamentName,format,console,ratings,createdBy,totalPrice,entryPrice,createdWhen,daysLeft;
        TextView days,hoursLeft,hours,minutesLeft,minutes,secondsLeft,seconds,colon1,colon2,colon3,ends;
        TextView click;
        CountDownTimer timer;
        ImageView gameIcon,profilePic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tournamentName = itemView.findViewById(R.id.tv_tournament_name);
            click = itemView.findViewById(R.id.tv_join);
            format = itemView.findViewById(R.id.tv_format);
            console = itemView.findViewById(R.id.tv_console);
            ratings = itemView.findViewById(R.id.tv_ratings);
            createdBy = itemView.findViewById(R.id.tv_created_by);
            totalPrice = itemView.findViewById(R.id.tv_total_price);
            entryPrice = itemView.findViewById(R.id.tv_join);
            createdWhen = itemView.findViewById(R.id.created_when);
            colon1 = itemView.findViewById(R.id.tv_colon1);
            colon2 = itemView.findViewById(R.id.tv_colon2);
            colon3 = itemView.findViewById(R.id.tv_colon3);
            daysLeft = itemView.findViewById(R.id.tv_days_left);
            days = itemView.findViewById(R.id.tv_days);
            hoursLeft = itemView.findViewById(R.id.tv_hours_left);
            hours= itemView.findViewById(R.id.tv_hours);
            minutesLeft = itemView.findViewById(R.id.tv_min_left);
            minutes = itemView.findViewById(R.id.tv_min);
            secondsLeft = itemView.findViewById(R.id.tv_sec_left);
            seconds = itemView.findViewById(R.id.tv_sec);
            ends = itemView.findViewById(R.id.tv_ends);
            profilePic = itemView.findViewById(R.id.profile_card);
            gameIcon = itemView.findViewById(R.id.game_icon);
        }
    }
    private void getUserInfo(String userID, final TextView userName, final ImageView proPic, final int position) {

        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (!user.getProfilePic().equals(""))
                    Glide.with(context).load(user.getProfilePic()).into(proPic);
                userName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void updateBalance(TournamentDetail tournamentDetail) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(firebaseUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    double amt = Objects.requireNonNull(user).getAccount_balance() - tournamentDetail.getEntryFee();
                    if (amt > 0) {
                        user.setAccount_balance(amt);
                        userRef.setValue(user).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                addJoinedUserIdToDatabase(tournamentDetail);

                            } else {
                                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        Toast.makeText(context,"Balance is Insufficient",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });
    }
    private String formatDecimal(double amt){
        DecimalFormat df = new DecimalFormat("####0.00");
        return df.format(amt);
    }
    private String formatNumber(long num){
        DecimalFormat df = new DecimalFormat("00");
        return df.format(num);
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
