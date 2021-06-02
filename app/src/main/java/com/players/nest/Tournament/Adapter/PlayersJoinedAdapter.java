package com.players.nest.Tournament.Adapter;

import android.content.Context;
import android.util.Log;
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
import com.players.nest.ModelClasses.User;
import com.players.nest.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayersJoinedAdapter extends RecyclerView.Adapter<PlayersJoinedAdapter.ViewHolder> {
    private static final String TAG = "PlayersJoinedAdapter";
    private Context context;
    private List<String> joinUserIdList;
    private List<User> userList;
    private List<User> filterList;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

    public PlayersJoinedAdapter(Context context,List<User> userList) {
        this.context = context;

        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.players_joined_items,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.ratings.setText(user.getRatings() + "");
        holder.username.setText(user.getUsername());
        //holder.ratings.setText(user.getRatings());
        Log.d(TAG, "onBindViewHolder: " + user.getUsername());
        if (!user.getProfilePic().equals(""))
            Glide.with(context).load(user.getProfilePic()).into(holder.profilePic);;
        if(user.getStatus().equals("OFFLINE")){
            long lastActiveTime = user.getLastActiveTime();
            Date now = new Date();
            if(now.getTime() - lastActiveTime> 0){
                long diff = now.getTime() - lastActiveTime;
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                long hours = TimeUnit.MILLISECONDS.toHours(diff) % 24;
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(diff)%60;
                if(days != 0){
                    holder.status.setText("Active " + days + "days Ago");
                }
                else if(hours != 0){
                    holder.status.setText("Active " + hours + "hours Ago");
                }
                else if(minutes != 0){
                    holder.status.setText("Active " + minutes + "mins Ago");

                }
                else{
                    holder.status.setText("Active " + seconds + "seconds Ago");
                }
            }
        }
        else{
            holder.status.setText("ONLINE");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView status,username,ratings;
        private ImageView profilePic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_card);
            status = itemView.findViewById(R.id.tv_status);
            username = itemView.findViewById(R.id.tv_username);
            ratings = itemView.findViewById(R.id.tv_ratings);

        }
    }

    private List<User> filterUsers(){
        List<User> filteredList = new ArrayList<>();
        if(joinUserIdList.size()>0 && userList.size()>0){
            for(User user: userList){
                for(String userId: joinUserIdList){
                    if(user.getUser_id().equals(userId)){
                        filteredList.add(user);
                    }
                }
            }
        }
//        Log.d(TAG, "filterUsers: " + filterList.size());
        return filteredList;
    }
}
