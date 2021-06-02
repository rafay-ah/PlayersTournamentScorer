package com.players.nest.HomeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.HelperMethods;
import com.players.nest.ModelClasses.Comment;
import com.players.nest.ModelClasses.User;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.R;

import java.util.ArrayList;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "COMMENTS";

    User user;
    UsersPosts usersPost;

    Toolbar toolbar;
    EditText addComment;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    NestedScrollView nestedScrollView;
    ConstraintLayout addCommentLayout;
    ImageView userProPic, currentUserPic;
    TextView userNameAndCaption, time, postBtn;
    LinearLayout captionLayout, commentDisabledLayout;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ValueEventListener commentEventListener;
    ArrayList<Comment> commentsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        time = findViewById(R.id.textView160);
        toolbar = findViewById(R.id.toolbar18);
        postBtn = findViewById(R.id.textView162);
        userProPic = findViewById(R.id.imageView47);
        progressBar = findViewById(R.id.progressBar12);
        currentUserPic = findViewById(R.id.imageView48);
        captionLayout = findViewById(R.id.linearLayout24);
        recyclerView = findViewById(R.id.recycler_view13);
        userNameAndCaption = findViewById(R.id.textView161);
        nestedScrollView = findViewById(R.id.nestedScrollView7);
        addComment = findViewById(R.id.editTextTextPersonName9);
        addCommentLayout = findViewById(R.id.constraintLayout15);
        commentDisabledLayout = findViewById(R.id.commentsDisabledLayout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_COMMENTS));

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                postBtn.setEnabled(!charSequence.toString().isEmpty());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        addComment.addTextChangedListener(textWatcher);


        postBtn.setOnClickListener(this);

        getDataFromFragments();
        showKeyboard();
        setAdapter();
    }


    private void showKeyboard() {
        addComment.requestFocus();
    }


    private void setAdapter() {
        commentAdapter = new CommentAdapter(CommentsActivity.this, commentsList, usersPost);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CommentsActivity.this,
                LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentAdapter);
    }


    private void getDataFromFragments() {

        Intent intent = getIntent();
        UsersPosts userPost = intent.getParcelableExtra(Constants.USER_POST_OBJECT);
        User user = intent.getParcelableExtra(Constants.USER_OBJECT);
        if (userPost != null && user != null) {
            this.usersPost = userPost;
            this.user = user;
        }

        setWidgets();
        getComments();
    }


    private void getComments() {

        commentEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentsList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        databaseReference.child(usersPost.getPostId()).addValueEventListener(commentEventListener);
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.textView162) {
            if (!addComment.getText().toString().equals("")) {
                addCommentToDatabase();
                addComment.setText("");
            }
        }
    }


    private void addCommentToDatabase() {

        String commentID = databaseReference.child(usersPost.getPostId()).push().getKey();

        Comment comment = new Comment(commentID, addComment.getText().toString()
                , firebaseUser.getUid(), System.currentTimeMillis());

        assert commentID != null;
        databaseReference.child(usersPost.getPostId())
                .child(commentID).setValue(comment);
    }


    private void setWidgets() {

        if (!user.getProfilePic().equals(""))
            Glide.with(this).load(user.getProfilePic()).into(userProPic);

        if (usersPost.getCaption().equals("")) {
            captionLayout.setVisibility(View.GONE);
        } else
            userNameAndCaption.setText(HelperMethods.usernameAndCaption(user.getUsername(), usersPost.getCaption()));

        Log.d(TAG, "setWidgets: " + usersPost.getDateCreated());
        time.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(usersPost.getDateCreated())
                , System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));


        //Check if Comments are Turned of or not.
        if (usersPost.isTurnOffComments()) {
            commentDisabledLayout.setVisibility(View.VISIBLE);
            addCommentLayout.setVisibility(View.INVISIBLE);
        } else {
            commentDisabledLayout.setVisibility(View.GONE);
            addCommentLayout.setVisibility(View.VISIBLE);
        }


        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS)).child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        if (!user.getProfilePic().equals(""))
                            Glide.with(CommentsActivity.this).load(user.getProfilePic()).into(currentUserPic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseHelperClass.changeStatus(Constants.ONLINE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
    }
}
