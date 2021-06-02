package com.players.nest.AlertFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.JoinMatch.MatchDetails;
import com.players.nest.JoinMatch.MatchDisputes;
import com.players.nest.JoinMatch.MatchStarted;
import com.players.nest.JoinMatch.SubmitActivity;
import com.players.nest.MatchDisputeChat.View.MatchDisputeChatActivity;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.R;
import com.players.nest.Tournament.TournamentStartedActivity;

import java.util.ArrayList;
import java.util.HashMap;



public class TournamentAlertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MATCH_STARTED = 0;
    private static final int MATCH_CONNECTING = 1;
    private static final int MATCH_FINISHED = 2;
    private static final int MATCH_DISPUTE = 3;
    private static final int MATCH_INVITATION = 4;
    private static final String TAG = "ALERT_FRAGMENT";

    Context context;
    User currentUser, opponentUser;
    ArrayList<TournamentDetail> mList;
    DatabaseReference userReference, matchReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public TournamentAlertAdapter(Context context, ArrayList<TournamentDetail> mList) {
        this.context = context;
        this.mList = mList;
    }


    @Override
    public int getItemViewType(int position) {
        String matchStatus = mList.get(position).getTournamentStatus();

        switch (matchStatus) {
            case Constants.MATCH_STARTED:
            case Constants.SUBMITTING_RESULTS:
                return MATCH_STARTED;
            case Constants.MATCH_FINISHED:
                return MATCH_FINISHED;
            case Constants.MATCH_DISPUTE:
                return MATCH_DISPUTE;
            case Constants.MATCH_INVITATION:
                return MATCH_INVITATION;
            default:
                return MATCH_CONNECTING;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       // if (viewType == MATCH_STARTED) {
            View view = inflater.inflate(R.layout.alert_tournament_ongoing, parent, false);
            return new mViewHolderClass(view);
         // }
//        else if (viewType == MATCH_FINISHED) {
//            View view = inflater.inflate(R.layout.alert_match_finished, parent, false);
//            return new matchFinishedViewHolderClass(view);
//        }
//        else if (viewType == MATCH_STARTED) {
//            View view = inflater.inflate(R.layout.alert_fragment_ongoing_match_recycler_view, parent, false);
//            return new matchStartedViewHolderClass(view);
//        } else if (viewType == MATCH_INVITATION) {
//            View view = inflater.inflate(R.layout.alert_match_finished, parent, false);
//            return new matchInvitationViewHolderClass(view);
//        }
//        else {
//            View view = inflater.inflate(R.layout.alert_match_dispute, parent, false);
//            return new matchDisputeViewHolderClass(view);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == MATCH_STARTED) {
            ((mViewHolderClass) holder).setData(position);
        }
//        else if (holder.getItemViewType() == MATCH_FINISHED) {
//            ((matchFinishedViewHolderClass) holder).setData(position);
//        } else if (holder.getItemViewType() == MATCH_STARTED)
//            ((matchStartedViewHolderClass) holder).setOngoingMatchData(position);
//        else if (holder.getItemViewType() == MATCH_INVITATION)
//            ((matchInvitationViewHolderClass) holder).setData(position);
//        else {
//            ((matchDisputeViewHolderClass) holder).setData(position, ((matchDisputeViewHolderClass) holder).itemView);
//        }
    }


//    public void getOpponentUserData(final int type, final int position, final TextView message, final ImageView profilePic) {
//
//        if (firebaseUser.getUid().equals(mList.get(position).getHostUserId()))
//            userReference = firebaseDatabase.getReference(context.getString(R.string.DB_USERS))
//                    .child(mList.get(position).getJoinedUserID());
//        else
//            userReference = firebaseDatabase.getReference(context.getString(R.string.DB_USERS))
//                    .child(mList.get(position).getHostUserId());
//
//        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User oppUser = snapshot.getValue(User.class);
//                assert oppUser != null;
//                opponentUser = oppUser;
//                if (type == MATCH_FINISHED) {
//                    if (mList.get(position). ().equals(firebaseUser.getUid()))
//                        message.setText(context.getString(R.string.ALERT_MATCH_LOSER, oppUser.getUsername(),
//                                mList.get(position).getGame().getName()));
//                    else
//                        message.setText(context.getString(R.string.ALERT_MATCH_WINNER, oppUser.getUsername(),
//                                mList.get(position).getGame().getName()));
//
//                } else if (type == MATCH_DISPUTE) {
//                    message.setText(context.getString(R.string.SUBMIT_EVIDENCE, oppUser.getUsername()));
//                } else if (type == MATCH_INVITATION)
//                    message.setText(context.getString(R.string.MATCH_INVITATION, oppUser.getUsername()));
//                else
//                    message.setText(oppUser.getUsername());
//
//                if (oppUser.getProfilePic() != null) {
//                    if (!oppUser.getProfilePic().equals(""))
//                        Glide.with(context).load(oppUser.getProfilePic()).into(profilePic);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }


    public void getCurrentUserData() {
        FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_USERS))
                .child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUser = snapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class mViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView gamePic;
        TextView tournamentName, message, gameName, disabledTxt, time;
        //Button accept, decline;
        Button join;

        public mViewHolderClass(@NonNull View itemView) {
            super(itemView);

           // gameName = itemView.findViewById(R.id.textView56);
            gamePic = itemView.findViewById(R.id.gamePic);
            tournamentName = itemView.findViewById(R.id.tournamentName);
            join = itemView.findViewById(R.id.btn_join);
            join.setOnClickListener(this);
//            disabledTxt = itemView.findViewById(R.id.textView120);
//            message = itemView.findViewById(R.id.textView113);
//            time = itemView.findViewById(R.id.textView131);
//            accept = itemView.findViewById(R.id.button12);
 //           decline = itemView.findViewById(R.id.button13);

//            matchReference = FirebaseDatabase.getInstance().getReference("Tournaments");

//            accept.setOnClickListener(this);
//            decline.setOnClickListener(this);
        }


        public void setData(int position) {

            if (mList.get(position).getTournamentStatus().equals(Constants.MATCH_STARTED)) {
                tournamentName.setText(mList.get(position).getTournamentName());
                if(!mList.get(position).getGame().getGameImg().isEmpty()){
                    Glide.with(context).load(mList.get(position).getGame().getGameImg()).into(gamePic);
                }

//            time.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(mList.get(position).getgetTimeJoined())
//                    , System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
//                accept.setVisibility(View.VISIBLE);
//                decline.setVisibility(View.VISIBLE);
                //getOpponentUserData(MATCH_CONNECTING, position, message, profilePic);

            }
//            else if (mList.get(position).getTournamentStatus().equals(Constants.MATCH_WAITING)) {
//                disabledTxt.setText(context.getString(R.string.HOST_FAILED_TO_JOIN));
//                disabledTxt.setTextSize(14);
//                accept.setVisibility(View.GONE);
//                decline.setVisibility(View.GONE);
//            }
        }


        @Override
        public void onClick(View view) {

            int id = view.getId();
            if(id == R.id.btn_join){

            }

//            if (id == R.id.button12) {
//                HashMap<String, Object> accepted = new HashMap<>();
//                accepted.put("hostAccepted", true);
//                matchReference.child(mList.get(getAdapterPosition()).getTournamentID())
//                        .updateChildren(accepted);
//                Intent intent = new Intent(context, MatchStarted.class);
//                intent.putExtra(Constants.MATCH_DETAIL_OBJECT, mList.get(getAdapterPosition()));
//                context.startActivity(intent);
//            } else if (id == R.id.button13) {
//                HashMap<String, Object> declined = new HashMap<>();
//                declined.put("hostRejected", true);
//                matchReference.child(mList.get(getAdapterPosition()).getTournamentID())
//                        .updateChildren(declined);
//            }
        }
    }


//    private class matchStartedViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        TextView opponentName, gameName, winningAmt;
//        ImageView imgRes;
//
//        public matchStartedViewHolderClass(View view) {
//            super(view);
//
//            opponentName = view.findViewById(R.id.textView135);
//            gameName = view.findViewById(R.id.textView136);
//            winningAmt = view.findViewById(R.id.textView138);
//            imgRes = view.findViewById(R.id.imageView43);
//
//            view.setOnClickListener(this);
//        }
//
//        public void setOngoingMatchData(int position) {
//
//            gameName.setText(mList.get(position).getGame().getName());
//            winningAmt.setText("Entry Fee: " + mList.get(position).getEntryFee());
//
//            getOpponentUserData(MATCH_STARTED, position, opponentName, imgRes);
//            getCurrentUserData();
//        }
//
//        @Override
//        public void onClick(View view) {
//            Log.d("ASDASDEWQEQWE", "onClick: " + mList.get(getAdapterPosition()).getMatchStatus());
//            if (mList.get(getAdapterPosition()).getMatchStatus().equals(Constants.SUBMITTING_RESULTS)) {
//                Intent intent = new Intent(context, SubmitActivity.class);
//                intent.putExtra(Constants.MATCH_DETAIL_OBJECT, mList.get(getAdapterPosition()));
//                intent.putExtra(Constants.OPPONENT_USER_OBJECT, opponentUser);
//                intent.putExtra(Constants.USER_OBJECT, currentUser);
//                context.startActivity(intent);
//            } else if (mList.get(getAdapterPosition()).getMatchStatus().equals(Constants.MATCH_DISPUTE)) {
//                context.startActivity(new Intent(context, MatchDisputes.class));
//            } else {
//                Intent intent = new Intent(context, MatchStarted.class);
//                intent.putExtra(Constants.MATCH_DETAIL_OBJECT, mList.get(getAdapterPosition()));
//                context.startActivity(intent);
//            }
//        }
//    }

//
//    private class tournamentFinishedViewHolderClass extends RecyclerView.ViewHolder {
//
//        TextView msg, time;
//        ImageView opponentImage;
//
//        public tournamentFinishedViewHolderClass(@NonNull View itemView) {
//            super(itemView);
//
//            msg = itemView.findViewById(R.id.textView174);
//            time = itemView.findViewById(R.id.textView175);
//            opponentImage = itemView.findViewById(R.id.imageView53);
//        }
//
//        public void setData(int position) {
//
//            getOpponentUserData(MATCH_FINISHED, position, msg, opponentImage);
//        }
//    }


//    private class matchDisputeViewHolderClass extends RecyclerView.ViewHolder {
//
//        TextView textView;
//        ImageView imgRes;
//
//        public matchDisputeViewHolderClass(View view) {
//            super(view);
//
//            textView = view.findViewById(R.id.textView178);
//            imgRes = view.findViewById(R.id.imageView23);
//
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, MatchDisputes.class);
//                    intent.putExtra(Constants.MATCH_DETAIL_OBJECT, mList.get(getAdapterPosition()));
//                    context.startActivity(intent);
//                }
//            });
//        }
//
//        public void setData(int position, View itemView) {
//            if (mList.get(position).getMatchWinner().equals("")) {
//                //getOpponentUserData(MATCH_DISPUTE, position, textView, imgRes);
//                checkMatchDisputesStatus(position, itemView);
//            } else {
//                getOpponentUserData(MATCH_FINISHED, position, textView, imgRes);
//            }
//        }
//
//        private void checkMatchDisputesStatus(int position, View itemView) {
//
//            if (mList.get(position).getMatchWinner().equals("")) {
//                FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_MATCH_DISPUTES))
//                        .child(mList.get(position).getMatch_ID())
//                        .child(firebaseUser.getUid())
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.exists()) {
//                                    String scoreImage = snapshot.child("scoreImage").getValue().toString();
//                                    String scoreVideo = snapshot.child("scoreVideo").getValue().toString();
//                                    Log.d("ADMIN_APPROVAL", "onDataChange: Score Image: " + scoreImage + "\nScore Video: " + scoreVideo);
//                                    /*This part is divided into 2 parts
//                                     * 1. First of all checking if files are being uploaded to the server or not. If it is not uploaded(basically checking scoreImage and scoreVideo) then
//                                     * click will take user to MatchDispute activity.
//                                     * 2. If files are uploaded then checking if it is approved by the admin or not. If it is approved by the admin then it will take user to MatchDisputeChatActivity
//                                     * otherwise it will take to MatchDispute.*/
//                                    FirebaseDatabase.getInstance().getReference("Match_Disputes")
//                                            .child(mList.get(position).getMatch_ID())
//                                            .addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.exists()) {
//                                                        String adminApproval = "0";
//                                                        String adminJoined = "no";
//                                                        if (snapshot.hasChild("admin_approval")) {
//                                                            adminApproval = snapshot.child("admin_approval").getValue().toString();
//                                                            Log.d("TESTINGADMIN", "onDataChange: admin approve" + adminApproval);
//                                                        }
//                                                        if (snapshot.hasChild("admin_joined")) {
//                                                            adminJoined = snapshot.child("admin_joined").getValue().toString();
//                                                            Log.d("TESTINGADMIN", "onDataChange: admin join" + adminJoined);
//                                                        }
//                                                        Log.d("ADMIN_APPROVAL", "onDataChange: " + adminApproval);
//                                                        if (!scoreImage.equals("null") || !scoreVideo.equals("null")) {
//                                                            if (adminApproval.equals("0")) {
//                                                                textView.setText(context.getString(R.string.EVIDENCE_SUBMITTED));
//                                                                itemView.setOnClickListener(new View.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(View v) {
//                                                                        Intent intent = new Intent(context, MatchDisputes.class);
//                                                                        intent.putExtra(Constants.MATCH_DETAIL_OBJECT, mList.get(getAdapterPosition()));
//                                                                        context.startActivity(intent);
//                                                                    }
//                                                                });
//                                                            } else {
//                                                                if (adminJoined.equals("yes")) {
//                                                                    textView.setText("Admin has joined the chat. Please enter the chat to continue");
//                                                                } else {
//                                                                    textView.setText(context.getString(R.string.ADMIN_APPROVED));
//                                                                }
//                                                                textView.setOnClickListener(new View.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(View v) {
//                                                                        Intent intent = new Intent(context, MatchDisputeChatActivity.class);
//                                                                        intent.putExtra(Constants.MATCH_DETAIL_OBJECT, mList.get(getAdapterPosition()));
//                                                                        intent.putExtra("match_id", mList.get(position).getMatch_ID());
//                                                                        intent.putExtra("user_id", firebaseUser.getUid());
//                                                                        context.startActivity(intent);
//                                                                    }
//                                                                });
//                                                            }
//                                                        } else {
//                                                            textView.setText(context.getString(R.string.NO_FILE_UPLOADED));
//                                                            textView.setOnClickListener(new View.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(View v) {
//                                                                    Intent intent = new Intent(context, MatchDisputes.class);
//                                                                    intent.putExtra(Constants.MATCH_DETAIL_OBJECT, mList.get(getAdapterPosition()));
//                                                                    context.startActivity(intent);
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                            }
//                        });
//            } else {
//
//            }
//
//        }
//    }

//
//    private class matchInvitationViewHolderClass extends RecyclerView.ViewHolder {
//
//        TextView msg, time;
//        ImageView opponentImage;
//
//        public matchInvitationViewHolderClass(View view) {
//            super(view);
//            msg = itemView.findViewById(R.id.textView174);
//            time = itemView.findViewById(R.id.textView175);
//            opponentImage = itemView.findViewById(R.id.imageView53);
//
//            itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(context, TournamentStartedActivity.class);
//                intent.putExtra("TOURNAMENT_DETAILS", mList.get(getAdapterPosition()));
//                intent.putExtra(Constants.MATCH_REQUEST, Constants.MATCH_REQUEST);
//                context.startActivity(intent);
//            });
//        }
//
//
//        public void setData(int position) {
//            time.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(mList.get(position).getTimeCreated())
//                    , System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
//            msg.setTypeface(Typeface.DEFAULT_BOLD);
//            getOpponentUserData(MATCH_INVITATION, position, msg, opponentImage);
//        }
//    }
}
