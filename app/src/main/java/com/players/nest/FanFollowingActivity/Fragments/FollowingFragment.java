package com.players.nest.FanFollowingActivity.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.FanFollowingActivity.RecyclerViewAdapter;
import com.players.nest.ModelClasses.User;
import com.players.nest.ModelClasses.UsersPosts;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {

    User user;
    int TYPE;
    UsersPosts userPost;
    ImageView clearIcon;
    EditText searchBar;
    TextView noFansFound;
    ProgressBar progressBar;
    RecyclerViewAdapter adapter, searchAdapter;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView, filteredRecyclerView;

    DatabaseReference databaseReference;
    List<String> userIDs = new ArrayList<>();
    ArrayList<User> usersList = new ArrayList<>();
    ArrayList<User> filteredUsersList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fans, container, false);

        clearIcon = view.findViewById(R.id.imageView59);
        searchBar = view.findViewById(R.id.textView185);
        noFansFound = view.findViewById(R.id.textView188);
        progressBar = view.findViewById(R.id.progressBar19);
        recyclerView = view.findViewById(R.id.recycler_view16);
        filteredRecyclerView = view.findViewById(R.id.recycler_view17);
        nestedScrollView = view.findViewById(R.id.nestedScrollView9);
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_FOLLOW));

        searchBar.setHint("Search Followings...");

        setAdapter();
        getUserIds();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    clearIcon.setVisibility(View.GONE);
                    filteredRecyclerView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    filteredRecyclerView.setVisibility(View.VISIBLE);

                    clearIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    searchUser(s.toString());
                }
            }
        });

        clearIcon.setOnClickListener(v -> {
            searchBar.setText("");
        });

        return view;
    }


    public void searchUser(String username) {
        filteredUsersList.clear();
        if (!username.isEmpty()) {
            for (User user : usersList) {
                if (user.getFullName().toLowerCase().matches(".*\\b" + username.toLowerCase() + "\\b.*")
                        || user.getUsername().toLowerCase().matches(".*\\b" + username.toLowerCase() + "\\b.*")) {
                    if (!filteredUsersList.contains(user)) {
                        filteredUsersList.add(user);
                    }
                }
            }
        }
        searchAdapter.notifyDataSetChanged();
    }


    private void getUserIds() {

        progressBar.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);

        databaseReference.child(user.getUser_id())
                .child(getString(R.string.USER_FOLLOWING))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String userId = dataSnapshot.getKey();
                                userIDs.add(userId);
                            }
                            getUserInfo();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void getUserInfo() {

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (userIDs.contains(dataSnapshot.getKey())) {
                                User user = dataSnapshot.getValue(User.class);
                                if (!usersList.contains(user))
                                    usersList.add(user);
                            }
                        }
                        if (usersList.size() == 0) {
                            nestedScrollView.setVisibility(View.GONE);
                            noFansFound.setVisibility(View.VISIBLE);
                        } else {
                            adapter.notifyDataSetChanged();
                            nestedScrollView.setVisibility(View.VISIBLE);
                            noFansFound.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void setAdapter() {

        adapter = new RecyclerViewAdapter(TYPE, getContext(), usersList, userPost);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //Search RecyclerView
        searchAdapter = new RecyclerViewAdapter(TYPE, getContext(), filteredUsersList, userPost);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        filteredRecyclerView.setLayoutManager(layoutManager2);
        filteredRecyclerView.setAdapter(searchAdapter);
    }


    public void getDataFromActivity(int TYPE, User user) {
        this.user = user;
        this.TYPE = TYPE;
    }

    public void getDataFromActivity(int TYPE, User user, UsersPosts usersPost) {
        this.user = user;
        this.TYPE = TYPE;
        this.userPost = usersPost;
    }
}
