package com.players.nest.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.Chats.MainChatActivity;
import com.players.nest.FanFollowingActivity.FansFollowingActivity;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.ExpandableGridView;
import com.players.nest.HelperClasses.HelperMethods;
import com.players.nest.HelperClasses.NotificationHelper;
import com.players.nest.InviteUsers.InviteUsers;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.players.nest.HelperClasses.Constants.INVITATION_SENT;
import static com.players.nest.HomeFragment.PostsAdapter.FROM_HOME_FRAGMENT;
import static com.players.nest.MainActivity.MAIN_ACTIVITY_FRAGMENT;
import static com.players.nest.ProfileFragment.ProfileFragment.PARCEL_KEY;
import static com.players.nest.SearchActivity.SearchPeopleFragment.VIEW_PROFILE_PARCEL;

public class ViewProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "VIEW_PROFILE_FRAGMENT";
    public static final String VIEW_PROFILE_TYPE = "FROM_VIEW_PROFILE";

    String TYPE = "";
    MatchDetail matchDetail;

    User user;
    int postCount;
    Toolbar toolbar;
    ProgressBar progressBar;
    ConstraintLayout postsFound;
    String invitedMatchID = null;
    NestedScrollView mainLayout;
    ImageView profilePic, plusIcon;
    ExpandableGridView expandableGridView;
    TextView followTxt, fullName, description;
    TextView postCountTxt, messageTxt, inviteTxt, ratingTxt;
    LinearLayout noPostFound, followingLayout, fansLayout;
    ArrayList<UsersPosts> userPostsArrayList = new ArrayList<>();

    ImageView star1, star2, star3, star4, star5;
    TextView fansTxt, followingTxt;

    //Firebase Variables
    FirebaseUser firebaseUser;
    List<String> fansUserIds = new ArrayList<>();
    List<String> followingUserIds = new ArrayList<>();
    DatabaseReference databaseReference, matchesReference, userDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_profile_layout, container, false);

        toolbar = view.findViewById(R.id.toolbar7);
/*
        postCountTxt = view.findViewById(R.id.textView30);
*/
        followTxt = view.findViewById(R.id.textView29);
        fullName = view.findViewById(R.id.textView5);
        profilePic = view.findViewById(R.id.profile_pic);
        description = view.findViewById(R.id.textView7);
        plusIcon = view.findViewById(R.id.imageView2);
        fansTxt = view.findViewById(R.id.tvFollowers);
        messageTxt = view.findViewById(R.id.textView54);
        inviteTxt = view.findViewById(R.id.textView55);
        ratingTxt = view.findViewById(R.id.textView190);
        followingTxt = view.findViewById(R.id.tvFollowing);
        fansLayout = view.findViewById(R.id.linearLayout33);
        progressBar = view.findViewById(R.id.progressBar3);
        noPostFound = view.findViewById(R.id.linearLayout10);
        postsFound = view.findViewById(R.id.constraintLayout7);
        mainLayout = view.findViewById(R.id.nestedScrollView3);
        followingLayout = view.findViewById(R.id.linearLayout32);
        star1 = view.findViewById(R.id.imageView67);
        star2 = view.findViewById(R.id.imageView68);
        star3 = view.findViewById(R.id.imageView69);
        star4 = view.findViewById(R.id.imageView70);
        star5 = view.findViewById(R.id.imageView71);
        expandableGridView = view.findViewById(R.id.expandableGridView2);


        //Firebase Variables Instantiated
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_FOLLOW));
        matchesReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    User detail = data.getValue(User.class);
                    Log.d("AAAAA", "onCreateView: " + detail.getAccount_balance());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getContext(), userPostsArrayList);
        expandableGridView.setExpanded(true);
        expandableGridView.setAdapter(gridViewAdapter);
        plusIcon.setVisibility(View.GONE);

        //Getting data from Previous Fragments (Search Fragment) OR Home Fragment OR MatchActivity OR Chat Fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = bundle.getParcelable(VIEW_PROFILE_PARCEL);
            if (user != null) {
                Log.d(TAG, "IF CONDITION: " + user);
            } else {
                User homeFragment_User = bundle.getParcelable(FROM_HOME_FRAGMENT);
                TYPE = FROM_HOME_FRAGMENT;
                user = homeFragment_User;
            }
            getPosts();

        }


        toolbar.setNavigationOnClickListener(view1 -> Objects.requireNonNull(getActivity()).onBackPressed());

        followTxt.setOnClickListener(this);
        messageTxt.setOnClickListener(this);
        fansLayout.setOnClickListener(this);
        inviteTxt.setOnClickListener(this);
        followingLayout.setOnClickListener(this);


        return view;
    }


    private void getFansFollowingTxt(User user) {

        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_FOLLOW))
                .child(user.getUser_id());

        followingRef.child(getString(R.string.USER_FOLLOWERS)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fansUserIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    fansUserIds.add(dataSnapshot.getKey());
                }
                fansTxt.setText(String.valueOf(fansUserIds.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        //For Checking if a User follows other user and mark the follow button as Following.
        // ---> (DONE BECAUSE OF CRASHES WHEN USING VALUE EVENT LISTENER)
        followingRef.child(getString(R.string.USER_FOLLOWERS)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fansUserIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    fansUserIds.add(dataSnapshot.getKey());
                }
                if (fansUserIds.contains(firebaseUser.getUid())) {
                    checkedFollowed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        //Getting the following counts
        followingRef.child(getString(R.string.USER_FOLLOWING)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingUserIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    followingUserIds.add(dataSnapshot.getKey());
                }
                followingTxt.setText(String.valueOf(followingUserIds.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void setWidgets(User user) {
        getFansFollowingTxt(user);

        followTxt.setText("Fan");
        ratingTxt.setText(String.valueOf(user.getRatings()));
/*
        postCountTxt.setText(String.valueOf(postCount));
*/
        fullName.setText(user.getFullName());
        toolbar.setTitle(user.getUsername());
        description.setText(user.getDescription());

        if (user.getProfilePic() != null) {
            if (!user.getProfilePic().equals(""))
                Glide.with(Objects.requireNonNull(getContext())).load(user.getProfilePic()).into(profilePic);
        }

        HelperMethods.setRatings(user.getRatings(), star1, star2, star3, star4, star5);
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }


    private void getPosts() {

        progressBar.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        userPostsArrayList.clear();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USER_POSTS)).child(user.getUser_id());
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UsersPosts userPosts = dataSnapshot.getValue(UsersPosts.class);
                    userPostsArrayList.add(userPosts);
                }
                postCount = userPostsArrayList.size();
                if (userPostsArrayList.size() == 0) {
                    noPostFound.setVisibility(View.VISIBLE);
                    postsFound.setVisibility(View.GONE);
                } else {
                    postsFound.setVisibility(View.VISIBLE);
                    noPostFound.setVisibility(View.GONE);
                    Collections.reverse(userPostsArrayList);
                    setupGridView(userPostsArrayList);
                }
                isInvitationSent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void setupGridView(final ArrayList<UsersPosts> userPostsArrayList) {

        expandableGridView.deferNotifyDataSetChanged();

        expandableGridView.setOnItemClickListener((adapterView, view, i, l) -> {
            ViewPostFragment viewPostFragment = new ViewPostFragment();
            Bundle bundle = new Bundle();
            bundle.putString("TYPE", VIEW_PROFILE_TYPE);
            bundle.putParcelable(PARCEL_KEY, userPostsArrayList.get(i));
            viewPostFragment.setArguments(bundle);

            FragmentTransaction transaction;

            assert getFragmentManager() != null;
            if (TYPE.equals(FROM_HOME_FRAGMENT)) {
                transaction = getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHolder, viewPostFragment);
            } else
                transaction = getFragmentManager().beginTransaction()
                        .replace(R.id.view_profile_fragment_holder, viewPostFragment);
            transaction.addToBackStack(MAIN_ACTIVITY_FRAGMENT).commit();
        });

    }


    public void isInvitationSent() {

        matchesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MatchDetail matchDetail = dataSnapshot.getValue(MatchDetail.class);
                    if (matchDetail.getMatchStatus().equals(Constants.MATCH_INVITATION)
                            && matchDetail.getJoinedUserID().equals(user.getUser_id())
                            && matchDetail.getHostUserId().equals(firebaseUser.getUid())) {
                        invitationSentTxt();
                        invitedMatchID = matchDetail.getMatch_ID();
                    }
                }
                setWidgets(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void invitationSentTxt() {
        inviteTxt.setText(INVITATION_SENT);
        inviteTxt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.accent_border, null));
        inviteTxt.setTextColor(ResourcesCompat.getColor(getResources(), R.color.PrimaryDarkWhite, null));
    }


    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.textView29) {
            if (followTxt.getText().toString().equals(getString(R.string.FOLLOWING_STRING))) {
                followTxt.setText("Follow");
                followTxt.setBackgroundResource(R.drawable.grey_border);
                followTxt.setTextColor(ResourcesCompat.getColor(getResources(), R.color.PrimaryLightBlack, null));
                removeInfoFromDatabase();

            } else {
                checkedFollowed();
            }
        } else if (view.getId() == R.id.textView54) {
            Intent intent = new Intent(getActivity(), MainChatActivity.class);
            intent.putExtra(Constants.USER_OBJECT, user);
            intent.putExtra(PARCEL_KEY, VIEW_PROFILE_TYPE);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).overridePendingTransition(0, 0);

        } else if (view.getId() == R.id.linearLayout33) {
            Intent intent = new Intent(getContext(), FansFollowingActivity.class);
            intent.putExtra(Constants.USER_OBJECT, user);
            intent.putExtra(Constants.FANS_FRAGMENT, Constants.OPEN_FANS_FRAGMENT);
            startActivity(intent);
        } else if (view.getId() == R.id.linearLayout32) {
            Intent intent = new Intent(getContext(), FansFollowingActivity.class);
            intent.putExtra(Constants.USER_OBJECT, user);
            intent.putExtra(Constants.FANS_FRAGMENT, Constants.OPEN_FOLLOWING_FRAGMENT);
            startActivity(intent);
        } else if (view.getId() == R.id.textView55) {
            if (inviteTxt.getText().toString().equals(INVITATION_SENT))
                cancelMatchRequest();
            else {
                Intent intent = new Intent(getContext(), InviteUsers.class);
                intent.putExtra(Constants.USER_OBJECT, user);
                startActivityForResult(intent, 100);
            }
        }
    }

    private void notifyOpponent() {
        new NotificationHelper().sendNotification(getContext(), user.getDeviceToken(), "Match Invitation",
                user.getUsername() + " has sent you a match request.",
                null, null);
        Log.d("CEKK", "notifyOpponent: " + user.getUsername());
    }


    private void cancelMatchRequest() {

        if (invitedMatchID != null) {
            DatabaseReference match = matchesReference.child(invitedMatchID);
            match.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    matchDetail = snapshot.getValue(MatchDetail.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ConfirmDialog alertDialog = new ConfirmDialog(Constants.CANCEL_INVITE,
                    () -> matchesReference.child(invitedMatchID).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    inviteTxt.setText("Invite");
                                    inviteTxt.setTextColor(getResources().getColor(R.color.PrimaryLightBlack,
                                            null));
                                    inviteTxt.setBackground(ResourcesCompat.getDrawable(getResources(),
                                            R.drawable.grey_border, null));

                                    FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseCurrentUser.getUid());
                                    userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);
                                            user.setAccount_balance(user.getAccount_balance() + matchDetail.getEntryFee());
                                            userDetails.setValue(user);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else
                                    Toast.makeText(getContext(), "Something went wrong. Please try again later.",
                                            Toast.LENGTH_SHORT).show();
                            }));
            alertDialog.show(getFragmentManager(), "CANCEL_REQUEST");
        } else
            Toast.makeText(getContext(), "Something went wrong. Please try again later.",
                    Toast.LENGTH_SHORT).show();
    }


    private void checkedFollowed() {
        followTxt.setText(getString(R.string.FOLLOWING_STRING));
        followTxt.setTextColor(ResourcesCompat.getColor(getResources(), R.color.PrimaryDarkWhite, null));
        followTxt.setBackgroundResource(R.drawable.view_profile_bg);
        addToDatabase();
    }


    private void removeInfoFromDatabase() {
        databaseReference.child(firebaseUser.getUid()).child(getString(R.string.USER_FOLLOWING)).child(user.getUser_id()).removeValue();
        databaseReference.child(user.getUser_id()).child(getString(R.string.USER_FOLLOWERS)).child(firebaseUser.getUid()).removeValue();
    }


    private void addToDatabase() {
        //Changing User's Following List
        databaseReference.child(firebaseUser.getUid()).child(getString(R.string.USER_FOLLOWING)).child(user.getUser_id())
                .setValue(true);
        notifyOpponent();
        //Also changing Other User's Follower List.
        databaseReference.child(user.getUser_id()).child(getString(R.string.USER_FOLLOWERS)).child(firebaseUser.getUid())
                .setValue(true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            invitationSentTxt();
            invitedMatchID = data.getStringExtra(Constants.INVITED_MATCH_ID);
        }
    }
}
