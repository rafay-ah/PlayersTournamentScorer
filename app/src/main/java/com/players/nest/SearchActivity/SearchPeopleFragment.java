package com.players.nest.SearchActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.ModelClasses.User;
import com.players.nest.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SearchPeopleFragment extends Fragment {

    private static final String TAG = "SEARCH_FRAGMENT";
    public static final String VIEW_PROFILE_PARCEL = "VIEW_PROFILE_USER_OBJECT";
    public static final String SEARCH_TYPE = "SEARCHING";
    public static final String RECENT_SEARCH_TYPE = "RECENT_SEARCH";

    EditText searchBar;
    TextView recentSearch;
    ProgressBar progressBar;
    RecyclerView recyclerView, searchRecyclerView;
    SearchAdapter searchAdapter, recentAdapter;
    ArrayList<User> userArrayList;
    ArrayList<User> profileFoundArrayList;
    ArrayList<User> searchHistoryList = new ArrayList<>();

    DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recentSearch = view.findViewById(R.id.textView4);
        recyclerView = view.findViewById(R.id.recycler_view3);
        progressBar = view.findViewById(R.id.progressBar10);
        searchRecyclerView = view.findViewById(R.id.recycler_view4);
        searchBar = view.findViewById(R.id.editTextTextPersonName);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        userArrayList = new ArrayList<>();
        profileFoundArrayList = new ArrayList<>();
        searchAdapter = new SearchAdapter(getContext(), SEARCH_TYPE, profileFoundArrayList, recentSearch);
        recyclerView.setAdapter(searchAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recentAdapter = new SearchAdapter(getContext(), RECENT_SEARCH_TYPE, searchHistoryList, recentSearch);
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setAdapter(recentAdapter);


        getSearchHistoryOfUser();
        getUsersList();


        ((SearchActivity) Objects.requireNonNull(getActivity())).passVal(new SearchActivity.searchFragmentInterface() {
            @Override
            public void emptyString() {
                profileFoundArrayList.clear();
                searchAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.GONE);
                recentSearch.setVisibility(View.VISIBLE);
                searchRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearch(String msg) {
                recyclerView.setVisibility(View.VISIBLE);
                searchRecyclerView.setVisibility(View.GONE);
                recentSearch.setVisibility(View.GONE);
                searchUser(msg);
            }
        });


        return view;
    }


    private void searchUser(String search) {
        for (User userObject : userArrayList) {
            if (userObject.getFullName().toLowerCase().matches(".*\\b" + search.toLowerCase() + "\\b.*")
                    || userObject.getUsername().toLowerCase().matches(".*\\b" + search.toLowerCase() + "\\b.*")) {
                if (!profileFoundArrayList.contains(userObject)) {
                    profileFoundArrayList.add(userObject);
                }
            }
        }
        searchAdapter.notifyDataSetChanged();
    }


    public void getSearchHistoryOfUser() {

        progressBar.setVisibility(View.VISIBLE);
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference searchRef = FirebaseDatabase.getInstance().getReference(getString(R.string.USER_DATA))
                .child(getString(R.string.SEARCH_HISTORY)).child(uid);

        searchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchHistoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!searchHistoryList.contains(user)) {
                        searchHistoryList.add(user);
                    }
                }
                Collections.reverse(searchHistoryList);
                if (searchHistoryList.size() == 0) {
                    searchRecyclerView.setVisibility(View.GONE);
                    recentSearch.setVisibility(View.GONE);
                } else {
                    searchRecyclerView.setVisibility(View.VISIBLE);
                    recentSearch.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
                recentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getUsersList() {
        userRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS));
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userArrayList.add(user);
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
