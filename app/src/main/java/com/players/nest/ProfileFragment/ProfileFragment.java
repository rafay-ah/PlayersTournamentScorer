package com.players.nest.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.FanFollowingActivity.FansFollowingActivity;
import com.players.nest.FindMatchFragment.FindMatchesFragment;
import com.players.nest.HelperClasses.CheckInternet;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.ExpandableGridView;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.HelperMethods;
import com.players.nest.Login_Register.LoginRegisterActivity;
import com.players.nest.ModelClasses.User;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.MyMatchesActivity.MyMatchesActivity;
import com.players.nest.MyTournamentActivity.MyTournamentActivity;
import com.players.nest.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.players.nest.MainActivity.MAIN_ACTIVITY_FRAGMENT;

public class ProfileFragment extends Fragment implements DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "PROFILE_FRAGMENT";
    public static final String PARCEL_KEY = "PARCEL_KEY";
    private static final String PROFILE_TYPE = "FROM_PROFILE_FRAGMENT";

    int postsCount;
    Toolbar toolbar;
    Button retryBtn;
    ProgressBar progressBar;
    GridViewAdapter adapter;
    NestedScrollView nestedScrollView;
    ConstraintLayout postsLayout;
    ImageView profilePic, plusIcon;
    ExpandableGridView gridView;
    DrawerLayout drawerLayout;
    ConstraintLayout mainLayout;
    NavigationView navigationView;
    List<String> fansUserIds = new ArrayList<>();
    List<String> followingUserIds = new ArrayList<>();
    LinearLayout noInternetLayout, walletLayout;
    LinearLayout noPostLayout, fansLayout, followingLayout;
    TextView fullName, description, accountBalance, editProfile;
    ArrayList<UsersPosts> postsArrayList = new ArrayList<>();

    TextView postsCountTxt, followersTxt, followingTxt, ratingTxt;
    ImageView star1, star2, star3, star4, star5;

    //Firebase variables
    FirebaseUser firebaseCurrentUser;
    ValueEventListener valueEventListener;
    DatabaseReference databaseRef, usersPostsRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        toolbar = view.findViewById(R.id.toolbar);
        retryBtn = view.findViewById(R.id.retryBtn);
        fullName = view.findViewById(R.id.textView5);
        description = view.findViewById(R.id.textView7);
        accountBalance = view.findViewById(R.id.balance);
        profilePic = view.findViewById(R.id.profile_pic);
        plusIcon = view.findViewById(R.id.imageView2);
        mainLayout = view.findViewById(R.id.mainLayout);
        ratingTxt = view.findViewById(R.id.textView190);
        drawerLayout = view.findViewById(R.id.drawerLayout);
/*
        postsCountTxt = view.findViewById(R.id.textView30);
*/
        followersTxt = view.findViewById(R.id.tvFollowers);
        followingTxt = view.findViewById(R.id.tvFollowing);
        noInternetLayout = view.findViewById(R.id.noInternet);
        gridView = view.findViewById(R.id.expandableGridView);
        noPostLayout = view.findViewById(R.id.noPostLayout);
        postsLayout = view.findViewById(R.id.postsLayout);
        editProfile = view.findViewById(R.id.textView29);
        walletLayout = view.findViewById(R.id.walletLayout);
        navigationView = view.findViewById(R.id.navigationView);
        fansLayout = view.findViewById(R.id.linearLayout33);
        followingLayout = view.findViewById(R.id.linearLayout32);
        nestedScrollView = view.findViewById(R.id.nestedScrollView2);
        progressBar = view.findViewById(R.id.profileProgressBar);

        star1 = view.findViewById(R.id.imageView67);
        star2 = view.findViewById(R.id.imageView68);
        star3 = view.findViewById(R.id.imageView69);
        star4 = view.findViewById(R.id.imageView70);
        star5 = view.findViewById(R.id.imageView71);


        //Firebase Var Initialized
        firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(firebaseCurrentUser.getUid());
        usersPostsRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USER_POSTS));


        /////////////
        nestedScrollView.setVisibility(View.GONE);
        gridView.setExpanded(true);


        //Inflating toolbar menu
        toolbar.inflateMenu(R.menu.profile_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            drawerLayout.openDrawer(GravityCompat.END);
            return true;
        });

        ///////////Listeners
        editProfile.setOnClickListener(view1 -> {
            assert getFragmentManager() != null;
            startActivity(new Intent(getActivity(), EditProfile.class));
            Objects.requireNonNull(getActivity()).overridePendingTransition(0, 0);
        });

        retryBtn.setOnClickListener(view12 -> checkInternet());
        walletLayout.setOnClickListener(view13 -> {
            startActivity(new Intent(getActivity(), WalletActivity.class));
            Objects.requireNonNull(getActivity()).overridePendingTransition(0, 0);
        });

        fansLayout.setOnClickListener(this);
        drawerLayout.addDrawerListener(this);
        followingLayout.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);


        return view;
    }


    private void getFollowers_FollowingList() {

        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_FOLLOW))
                .child(firebaseCurrentUser.getUid());

        followingRef.child(getString(R.string.USER_FOLLOWERS)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fansUserIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userIds = dataSnapshot.getKey();
                    fansUserIds.add(userIds);
                }
                followersTxt.setText(String.valueOf(fansUserIds.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        followingRef.child(getString(R.string.USER_FOLLOWING)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingUserIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userIds = dataSnapshot.getKey();
                    followingUserIds.add(userIds);
                }
                followingTxt.setText(String.valueOf(followingUserIds.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void checkInternet() {
        if (CheckInternet.isInternetAvailable(Objects.requireNonNull(getContext())))
            getUsersData();
        else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            noInternetLayout.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }


    private void getUsersData() {

        progressBar.setVisibility(View.VISIBLE);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                setProfileFragment(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        };

        databaseRef.addValueEventListener(valueEventListener);

        usersPostsRef.child(firebaseCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UsersPosts usersPost = dataSnapshot.getValue(UsersPosts.class);
                    postsArrayList.add(usersPost);
                }
                postsCount = postsArrayList.size();
                /*postsCountTxt.setText(String.valueOf(postsCount));*/
                if (postsArrayList.size() == 0) {
                    noPostLayout.setVisibility(View.VISIBLE);
                    postsLayout.setVisibility(View.GONE);
                } else {
                    postsLayout.setVisibility(View.VISIBLE);
                    noPostLayout.setVisibility(View.GONE);
                    setupGridView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        getFollowers_FollowingList();
    }


    private void setupGridView() {

        Collections.reverse(postsArrayList);
        adapter = new GridViewAdapter(getContext(), postsArrayList);
        gridView.setAdapter(adapter);

        //GridView OnClick Listener
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {

            ViewPostFragment viewPostFragment = new ViewPostFragment();
            Bundle bundle = new Bundle();
            bundle.putString("TYPE", PROFILE_TYPE);
            bundle.putParcelable(PARCEL_KEY, postsArrayList.get(i));

            viewPostFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentHolder, viewPostFragment, Constants.VIEW_POST_FRAGMENT)
                    .addToBackStack(MAIN_ACTIVITY_FRAGMENT)
                    .commit();
        });
    }


    private void setProfileFragment(User user) {

        toolbar.setTitle(user.getUsername());
        fullName.setText(user.getFullName());
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        accountBalance.setText(decimalFormat.format((double) user.getAccount_balance()));
        ratingTxt.setText(String.valueOf(user.getRatings()));
        HelperMethods.setRatings(user.getRatings(), star1, star2, star3, star4, star5);
//        //Description
//        if (user.getDescription().equals("")) {
//            description.setVisibility(View.GONE);
//        } else
        description.setText(user.getDescription());

        //Profile Picture
        if (user.getProfilePic().equals("")) {
            profilePic.setImageResource(R.drawable.ic_no_profile_pic_logo_1);
            plusIcon.setVisibility(View.VISIBLE);
        } else {
            if (getActivity() != null && profilePic != null) {
                Glide.with(Objects.requireNonNull(getActivity()).getApplicationContext()).load(user.getProfilePic()).into(profilePic);
                plusIcon.setVisibility(View.GONE);
            }
        }
        progressBar.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
        setNavigationDrawer(user);
    }


    //Inflating Navigation HeaderView and setting data
    private void setNavigationDrawer(User user) {

        View headerView = navigationView.getHeaderView(0);
        TextView headerUsername = headerView.findViewById(R.id.textView95);
        ImageView close = headerView.findViewById(R.id.imageView29);
        headerUsername.setText(user.getUsername());
        close.setOnClickListener(view -> drawerLayout.closeDrawer(GravityCompat.END));
    }


    public void showLogOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setMessage("Are you sure?");
        builder.setTitle("Confirm Sign Out");
        builder.setPositiveButton("Log Out", (dialog, which) -> {
            setDeviceTokenTo0();
            FirebaseHelperClass.changeStatus(Constants.OFFLINE);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void setDeviceTokenTo0() {

        HashMap<String, Object> token = new HashMap<>();
        token.put("deviceToken", "");

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(firebaseCurrentUser.getUid())
                .updateChildren(token);
    }


    @Override
    public void onPause() {
        super.onPause();
        databaseRef.removeEventListener(valueEventListener);
    }

    /*      Navigation Drawer Listener       */
    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        mainLayout.setTranslationX(-(slideOffset * drawerView.getWidth()));
        drawerLayout.bringChildToFront(drawerView);
        drawerLayout.requestLayout();
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.logOut) {
            showLogOutDialog();
        } else if (itemId == R.id.myMatches) {
            startActivity(new Intent(getActivity(), MyMatchesActivity.class));
        } else if( itemId == R.id.myTournament){
            startActivity(new Intent(getActivity(), MyTournamentActivity.class));
        } else if (itemId == R.id.findMatches) {
            FindMatchesFragment findMatchesFragment = new FindMatchesFragment();
            findMatchesFragment.getType(null, getString(R.string.FIND_ALL_MATCHES));
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().addToBackStack(MAIN_ACTIVITY_FRAGMENT)
                    .replace(R.id.fragmentHolder, findMatchesFragment).commit();
            drawerLayout.closeDrawer(GravityCompat.END);
        } else if (itemId == R.id.settings) {
            drawerLayout.closeDrawer(GravityCompat.END);
            startActivity(new Intent(getActivity(), Settings.class));
        }
        else if (itemId ==R.id.FAQS){
            startActivity(new Intent(getActivity(), FAQ.class));
        }
        else if (itemId == R.id.TOS){
            startActivity(new Intent(getActivity(), TermsOfService.class));

        }
        else if(itemId == R.id.privacyPolicy){
            startActivity(new Intent(getActivity(), PrivacyPolicy.class));

        }

        return false;
    }

    @Override
    public void onClick(View v) {

        //Fans Fragment
        if (v.getId() == R.id.linearLayout33) {

            Intent intent = new Intent(getContext(), FansFollowingActivity.class);
            intent.putExtra(Constants.FANS_FRAGMENT, Constants.OPEN_FANS_FRAGMENT);
            startActivity(intent);
        } else if (v.getId() == R.id.linearLayout32) {
            Intent intent = new Intent(getContext(), FansFollowingActivity.class);
            intent.putExtra(Constants.FANS_FRAGMENT, Constants.OPEN_FOLLOWING_FRAGMENT);
            startActivity(intent);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //Custom Methods
        checkInternet();
    }
}
