package com.players.nest.HomeFragment;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.players.nest.HelperClasses.HelperMethods;
import com.players.nest.HelperClasses.UnSendMsgDialog;
import com.players.nest.ModelClasses.Comment;
import com.players.nest.ModelClasses.User;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.R;
import com.players.nest.SearchActivity.ViewProfileActivity;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.mViewHolderClass> {

    Context context;
    ArrayList<Comment> commentsList;

    User userObject;
    UsersPosts usersPost;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    public CommentAdapter(Context context, ArrayList<Comment> commentsList, UsersPosts usersPost) {
        this.context = context;
        this.commentsList = commentsList;
        this.usersPost = usersPost;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_USERS));
    }

    @NonNull
    @Override
    public mViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.comment_recycler_view, parent, false);
        return new mViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolderClass holder, int position) {

        holder.time.setText(DateUtils.getRelativeTimeSpanString(
                commentsList.get(position).getDate_created(),
                System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));

        getUserInfo(commentsList.get(position).getUser_id(), commentsList.get(position).getComment(),
                holder.profilePic, holder.usernameAndComment);
    }


    private void getUserInfo(String userId, final String comment, final ImageView profilePic, final TextView usernameAndComment) {

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userObject = user;
                if (user != null) {
                    if (!user.getProfilePic().equals(""))
                        Glide.with(context).load(user.getProfilePic()).into(profilePic);
                    usernameAndComment.setText(HelperMethods.usernameAndCaption(user.getUsername(), comment));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }


    public class mViewHolderClass extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        ImageView profilePic;
        TextView usernameAndComment, time;

        public mViewHolderClass(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.imageView49);
            time = itemView.findViewById(R.id.textView164);
            usernameAndComment = itemView.findViewById(R.id.textView163);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {

            UnSendMsgDialog dialog = new UnSendMsgDialog(Constants.DELETE_COMMENT, new UnSendMsgDialog.unSendMsgListener() {
                @Override
                public void unSendMsg() {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_COMMENTS))
                            .child(usersPost.getPostId())
                            .child(commentsList.get(getAdapterPosition()).getCommentID())
                            .removeValue();
                }

                @Override
                public void copyMessage() {
                }
            });

            if (commentsList.get(getAdapterPosition()).getUser_id().equals(firebaseUser.getUid()))
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "DELETE_DIALOG");

            return true;
        }

        @Override
        public void onClick(View v) {
            if (!commentsList.get(getAdapterPosition()).getUser_id().equals(firebaseUser.getUid())) {
                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra(Constants.USER_OBJECT, userObject);
                context.startActivity(intent);
            }
        }
    }
}
