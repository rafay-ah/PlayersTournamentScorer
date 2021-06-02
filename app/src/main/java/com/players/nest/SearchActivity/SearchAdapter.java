package com.players.nest.SearchActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.players.nest.MainActivity;
import com.players.nest.ModelClasses.User;
import com.players.nest.R;

import java.util.ArrayList;

import static com.players.nest.SearchActivity.SearchPeopleFragment.RECENT_SEARCH_TYPE;
import static com.players.nest.SearchActivity.SearchPeopleFragment.SEARCH_TYPE;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdaptViewHolderClass> {

    String TYPE;
    Context context;
    TextView recentSearchTxt;
    ArrayList<User> userDetail;

    public SearchAdapter(Context context, String TYPE, ArrayList<User> userDetail, TextView recentSearchTxt) {
        this.context = context;
        this.TYPE = TYPE;
        this.userDetail = userDetail;
        this.recentSearchTxt = recentSearchTxt;
    }

    @NonNull
    @Override
    public SearchAdaptViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.search_recycler_view_layout, parent, false);
        return new SearchAdaptViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdaptViewHolderClass holder, int position) {

        holder.username.setText(userDetail.get(position).getUsername());
        holder.fullName.setText(userDetail.get(position).getFullName());

        if (userDetail.get(position).getProfilePic().equals("")) {
            holder.imageView.setImageResource(R.drawable.ic_no_profile_pic_logo_1);
        } else {
            Glide.with(context).load(userDetail.get(position).getProfilePic()).into(holder.imageView);
        }

        if (TYPE.equals(SEARCH_TYPE)) {
            holder.deleteBtn.setVisibility(View.GONE);
            recentSearchTxt.setVisibility(View.GONE);
        } else if (TYPE.equals(RECENT_SEARCH_TYPE)) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            recentSearchTxt.setVisibility(View.VISIBLE);
            if (userDetail.size() == 0)
                recentSearchTxt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userDetail.size();
    }

    public class SearchAdaptViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView, deleteBtn;
        TextView username, fullName;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        public SearchAdaptViewHolderClass(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView20);
            deleteBtn = itemView.findViewById(R.id.imageView21);
            username = itemView.findViewById(R.id.textView);
            fullName = itemView.findViewById(R.id.textView49);

            itemView.setOnClickListener(this);
            deleteBtn.setOnClickListener(view -> {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.USER_DATA))
                        .child(context.getString(R.string.SEARCH_HISTORY)).child(currentUser.getUid());

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user.getUser_id().equals(userDetail.get(getAdapterPosition()).getUser_id())) {
                                String key = dataSnapshot.getKey();
                                assert key != null;
                                databaseReference.child(key).removeValue().addOnSuccessListener(aVoid -> {
                                    userDetail.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                    if (userDetail.size() == 0)
                                        recentSearchTxt.setVisibility(View.GONE);
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onClick(View view) {

            final User user = SearchAdapter.this.userDetail.get(getAdapterPosition());

            assert currentUser != null;
            if (user.getUser_id().equals(currentUser.getUid())) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Constants.MAIN_ACTIVITY_PROFILE, Constants.FROM_SEARCH_FRAGMENT);
                context.startActivity(intent);
            } else {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.USER_DATA))
                        .child(context.getString(R.string.SEARCH_HISTORY)).child(currentUser.getUid());

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean flag = false;
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                User checkUser = dataSnapshot.getValue(User.class);
                                assert checkUser != null;
                                if (checkUser.getUser_id().equals(user.getUser_id())) {
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                databaseReference.push().setValue(user);
                            }
                        } else {
                            databaseReference.push().setValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra(Constants.USER_OBJECT, user);
                context.startActivity(intent);
            }
        }
    }
}
