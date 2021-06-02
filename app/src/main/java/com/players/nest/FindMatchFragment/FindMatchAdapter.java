package com.players.nest.FindMatchFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.JoinMatch.MatchDetails;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.R;

import java.util.ArrayList;

public class FindMatchAdapter extends RecyclerView.Adapter<FindMatchAdapter.myViewHolderClass> {

    Context context;
    ArrayList<MatchDetail> matchDetailsList;
    DatabaseReference userRef;

    public FindMatchAdapter(Context context, ArrayList<MatchDetail> matchDetails) {
        this.context = context;
        this.matchDetailsList = matchDetails;

        userRef = FirebaseDatabase.getInstance().
                getReference(context.getString(R.string.DB_USERS));
    }

    @NonNull
    @Override
    public myViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.find_match_recycler_view_layout, parent, false);
        return new myViewHolderClass(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull myViewHolderClass holder, int position) {

        String amount = String.valueOf(matchDetailsList.get(position).getEntryFee());

        holder.format.setText(matchDetailsList.get(position).getFormat());
        holder.rules.setText(matchDetailsList.get(position).getRules());
        holder.entryFee.setText("$ " + amount + " JOIN");
        holder.winningAmt.setText("$" + matchDetailsList.get(position).getWinningAmt());
        holder.console.setText(matchDetailsList.get(position).getConsole());
        holder.timeCreated.setText(DateUtils.getRelativeTimeSpanString
                (Long.parseLong(matchDetailsList.get(position).getTimeCreated())
                        , System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));

        holder.gameName.setText(matchDetailsList.get(position).getGame().getName());
        Glide.with(context).load(matchDetailsList.get(position).getGame().getGameImg()).into(holder.gamePic);

        getUserInfo(matchDetailsList.get(position).getHostUserId(), holder.userName, holder.proPic, position);
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


    @Override
    public int getItemCount() {
        return matchDetailsList.size();
    }


    public class myViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView timeCreated;
        ImageView proPic, gamePic;
        TextView format, rules, entryFee, console, userName, gameName, winningAmt;

        public myViewHolderClass(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textView104);
            gamePic = itemView.findViewById(R.id.imageView31);
            proPic = itemView.findViewById(R.id.imageView32);
            format = itemView.findViewById(R.id.textView98);
            rules = itemView.findViewById(R.id.textView102);
            winningAmt = itemView.findViewById(R.id.textView53);
            timeCreated = itemView.findViewById(R.id.textView100);
            entryFee = itemView.findViewById(R.id.textView101);
            console = itemView.findViewById(R.id.textView103);
            gameName = itemView.findViewById(R.id.textView96);

            itemView.setOnClickListener(this);
//            proPic.setOnClickListener(this);
//            userName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == itemView) {
                Intent intent = new Intent(context, MatchDetails.class);
                intent.putExtra(Constants.MATCH_DETAIL_OBJECT, matchDetailsList.get(getAdapterPosition()));
                context.startActivity(intent);
            }
        }
    }
}
