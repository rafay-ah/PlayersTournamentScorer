package com.players.nest.FanFollowingActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.NotificationHelper;
import com.players.nest.HelperClasses.ShareBottomSheet;
import com.players.nest.MainActivity;
import com.players.nest.ModelClasses.Chats;
import com.players.nest.ModelClasses.User;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.SearchActivity.ViewProfileActivity;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int Type;
    Context context;
    UsersPosts usersPost;
    ArrayList<User> usersList;

    DatabaseReference databaseReference, chatRef;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public RecyclerViewAdapter(int Type, Context context, ArrayList<User> usersList, UsersPosts usersPost) {
        this.Type = Type;
        this.context = context;
        this.usersList = usersList;
        this.usersPost = usersPost;
        databaseReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_USERS));
        chatRef = FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_CHATS));
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (Type == Constants.SHARE_BOTTOM_SHEET) {
            View view = inflater.inflate(R.layout.following_recycler_view_item, parent, false);
            return new shareViewHolderClass(view);
        } else {
            View view = inflater.inflate(R.layout.fans_recycler_view_item, parent, false);
            return new mViewHolderClass(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (Type == Constants.SHARE_BOTTOM_SHEET) {
            ((shareViewHolderClass) (holder)).setData(position);
        } else
            ((mViewHolderClass) (holder)).setData(position);
    }


    public void setDataViewHolders(TextView username, TextView fullName, ImageView imgRes, int position) {

        username.setText(usersList.get(position).getUsername());
        fullName.setText(usersList.get(position).getFullName());

        if (usersList.get(position).getProfilePic() != null) {
            if (!usersList.get(position).getProfilePic().equals(""))
                Glide.with(context).load(usersList.get(position).getProfilePic()).into(imgRes);
        }
    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class mViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView username, fullName;
        ImageView imgRes;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        public mViewHolderClass(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.textView186);
            fullName = itemView.findViewById(R.id.textView187);
            imgRes = itemView.findViewById(R.id.imageView60);

            itemView.setOnClickListener(this);
        }

        void setData(int position) {
            setDataViewHolders(username, fullName, imgRes, position);
        }

        @Override
        public void onClick(View v) {

            User userObj = usersList.get(getAdapterPosition());

            if (userObj.getUser_id().equals(firebaseUser.getUid())) {
                //Open Current User Profile
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Constants.MAIN_ACTIVITY_PROFILE, Constants.FROM_SEARCH_FRAGMENT);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra(Constants.USER_OBJECT, userObj);
                context.startActivity(intent);
            }
        }
    }


    private class shareViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView username, fullName, sendBtn;
        ImageView imgRes;

        public shareViewHolderClass(View view) {
            super(view);

            username = view.findViewById(R.id.textView186);
            fullName = view.findViewById(R.id.textView187);
            imgRes = view.findViewById(R.id.imageView60);
            sendBtn = view.findViewById(R.id.textView191);

            sendBtn.setOnClickListener(this);
        }


        public void setData(int position) {
            setDataViewHolders(username, fullName, imgRes, position);
        }

        @Override
        public void onClick(View v) {

            if (usersPost != null) {
                if (sendBtn.getText().toString().equals("Send")) {
                    sendBtn.setText("Sent");
                    sendBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.accent_border, null));
                    sendBtn.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.PrimaryDarkWhite, null));
                    ShareBottomSheet share_bottom_sheet = (ShareBottomSheet) ((FragmentActivity) (context)).getSupportFragmentManager()
                            .findFragmentByTag("SHARE_BOTTOM_SHEET");
                    assert share_bottom_sheet != null;
                    String message = share_bottom_sheet.message.getText().toString();
                    addChatToDatabase(message, getAdapterPosition());
                }
            }
        }


        private void addChatToDatabase(String message, int adapterPosition) {

            String messageId = chatRef.push().getKey();
            Chats chat = new Chats(firebaseUser.getUid(), usersList.get(adapterPosition).getUser_id(), message,
                    messageId, Constants.POST_MESSAGE_TYPE, usersPost, "", false, System.currentTimeMillis());

            assert messageId != null;
            chatRef.child(messageId).setValue(chat).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //Notify Other User
                    sendNotificationsToOtherUser(adapterPosition);
                } else {
                    Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        private void sendNotificationsToOtherUser(int adapterPosition) {
            Toast.makeText(context, "Message Sent", Toast.LENGTH_SHORT).show();

            databaseReference.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User currentUser = snapshot.getValue(User.class);
                            if (currentUser != null) {
                                new NotificationHelper().sendNotificationsForChatFragment(context,
                                        currentUser, usersList.get(adapterPosition), "Sent you a post.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
    }
}
