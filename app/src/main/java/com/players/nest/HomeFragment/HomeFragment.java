package com.players.nest.HomeFragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.Chats.MainChatActivity;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.R;
import com.players.nest.SearchActivity.SearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    public static final String TAG = "HOME_FRAGMENT";

    View view;
    Toolbar toolbar;
    TextView marqueeTxt;
    FrameLayout shadowView;
    ProgressBar progressBar;
    PostsAdapter postsAdapter;
    LinearLayout noPostLayout;
    FirebaseUser firebaseUser;
    ConstraintLayout mainLayout;
    FirebaseDatabase firebaseDatabase;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.OnScrollListener listener;
    CardView allUserFabTxt, followingFabTxt, newPostFound;
    RecyclerView recyclerView, allUsersPostsRecyclerView;
    FloatingActionButton mainFab, followingFab, allUsersFab;
    DatabaseReference databaseReference;

    boolean isAllUsersListVisible = false;
    public static boolean fabExtended = false;
    float visiblePercent = 40, postArraySize = 0;
    ValueEventListener valueEventListener;
    DatabaseReference followingRef, allUsersRef, realTimePostListener;
    List<String> userFollowingList = new ArrayList<>();
    ArrayList<UsersPosts> postsArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);

            followingFab = view.findViewById(R.id.fab2);
            allUsersFab = view.findViewById(R.id.fab1);
            mainFab = view.findViewById(R.id.mainFab);
            toolbar = view.findViewById(R.id.toolbar5);
            marqueeTxt = view.findViewById(R.id.textView208);
            allUserFabTxt = view.findViewById(R.id.cardView33);
            followingFabTxt = view.findViewById(R.id.cardView34);
            newPostFound = view.findViewById(R.id.newPostFound);
            shadowView = view.findViewById(R.id.frameLayout2);
            progressBar = view.findViewById(R.id.progressBar4);
            recyclerView = view.findViewById(R.id.recycler_view);
            noPostLayout = view.findViewById(R.id.linearLayout13);
            mainLayout = view.findViewById(R.id.constraintLayout22);
            swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout3);
            allUsersPostsRecyclerView = view.findViewById(R.id.recycler_view2);


            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            //Setting Up RecyclerView
            setRecyclerView();
            listener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    playVideo();

                    if (dy > 0) {
                        marqueeTxt.setVisibility(View.GONE);
                        mainFab.hide();
                    } else if (dy < -5) {
                        marqueeTxt.setVisibility(View.VISIBLE);
                        mainFab.show();
                    }
                }
            };
            recyclerView.addOnScrollListener(listener);


            // Database Variables
            allUsersRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_POST));
            realTimePostListener = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_POST));
            followingRef = firebaseDatabase.getReference(getString(R.string.DB_FOLLOW)).child(firebaseUser.getUid())
                    .child(getString(R.string.USER_FOLLOWING));

            checkFollowings();
            setOnClickListeners();
            attachRealtimeListenerForNewPosts();

            marqueeTxt.setSelected(true);
        }
        return view;
    }


    private void attachRealtimeListenerForNewPosts() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UsersPosts usersPost = dataSnapshot.getValue(UsersPosts.class);
                    if (usersPost != null) {
                        if (!usersPost.getUserId().equals(firebaseUser.getUid())
                                && !postsArrayList.contains(usersPost))
                            postsArrayList.add(usersPost);
                    }
                }
                if (postArraySize != 0 && postArraySize < postsArrayList.size()) {
                    if (isAllUsersListVisible)
                        newPostFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        realTimePostListener.addValueEventListener(valueEventListener);
    }


    private void setOnClickListeners() {

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search_icon) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
                Objects.requireNonNull(getActivity()).overridePendingTransition(0, 0);
            } else {
                Intent intent = new Intent(getActivity(), MainChatActivity.class);
                startActivity(intent);
            }
            return true;
        });
        mainFab.setOnClickListener(v -> {
            if (fabExtended)
                closeFabMenu();
            else
                openFabMenu();
        });
        shadowView.setOnClickListener(v -> {
            if (fabExtended)
                closeFabMenu();
        });
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        allUsersFab.setOnClickListener(v -> {
            if (!isAllUsersListVisible) {
                isAllUsersListVisible = true;
                readAllPosts();
                mainFab.hide();
            }

            closeFabMenu();
        });
        followingFab.setOnClickListener(v -> {
            if (isAllUsersListVisible) {
                isAllUsersListVisible = false;
                checkFollowings();
                mainFab.hide();
            }
            closeFabMenu();
        });

        newPostFound.setOnClickListener(v -> {
            newPostFound.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
            refreshData();
        });
    }


    private void refreshData() {
        postArraySize = postsArrayList.size();
        if (isAllUsersListVisible) {
            readAllPosts();
        } else {
            checkFollowings();
        }
        mainFab.hide();
        marqueeTxt.setVisibility(View.GONE);
    }


    private void openFabMenu() {

        fabExtended = true;
        allUsersFab.show();
        followingFab.show();
        shadowView.setVisibility(View.VISIBLE);
        allUserFabTxt.setVisibility(View.VISIBLE);
        followingFabTxt.setVisibility(View.VISIBLE);
        mainFab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext())
                , R.drawable.ic_baseline_clear_24));

        stopAllOtherVideos(-1);
    }


    private void setRecyclerView() {

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        postsAdapter = new PostsAdapter(getContext(), postsArrayList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(postsAdapter);
    }


    private void playVideo() {

        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();

        if (firstVisiblePosition >= 0) {
            Rect rect_parent = new Rect();
            recyclerView.getGlobalVisibleRect(rect_parent);
            float rect_parent_area;
            float x_overlap;
            float y_overlap;
            float overlapArea;
            boolean foundFirstVideo = false;

            for (int i = firstVisiblePosition; i <= lastVisiblePosition; ++i) {
                final RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);

                try {
                    PostsAdapter.myViewHolderClass myViewHolderClass = (PostsAdapter.myViewHolderClass) holder;
                    if (myViewHolderClass != null && myViewHolderClass.mainVideoLayout.getVisibility() == View.VISIBLE) {
                        int[] location = new int[2];
                        myViewHolderClass.videoView.getLocationOnScreen(location);
                        Rect rect_child = new Rect(location[0], location[1], location[0]
                                + myViewHolderClass.videoView.getWidth(), location[1]
                                + myViewHolderClass.videoView.getHeight());
                        rect_parent_area = (float) ((rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top));
                        x_overlap = (float) Math.max(0, Math.min(rect_child.right, rect_parent.right) - Math.max(rect_child.left, rect_parent.left));
                        y_overlap = (float) Math.max(0, Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(rect_child.top, rect_parent.top));
                        overlapArea = x_overlap * y_overlap;
                        float percent = overlapArea / rect_parent_area * 100.0F;

                        if (!foundFirstVideo && percent >= visiblePercent) {
                            foundFirstVideo = true;
                            if (myViewHolderClass.playIcon.getVisibility() == View.GONE) {
                                myViewHolderClass.videoView.start();
                                stopAllOtherVideos(myViewHolderClass.getAdapterPosition());
                            }
                        } else if (percent == 0) {
                            if (myViewHolderClass.videoView.isPlaying())
                                myViewHolderClass.videoView.pause();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Pass -1 to Stop All videos
    private void stopAllOtherVideos(int adapterPosition) {
        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();

        if (firstVisiblePosition >= 0) {

            for (int i = firstVisiblePosition; i <= lastVisiblePosition; ++i) {
                if (i != adapterPosition) {
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder != null) {
                        PostsAdapter.myViewHolderClass holderClass = (PostsAdapter.myViewHolderClass) holder;
                        if (holderClass.videoView.isPlaying()) {
                            holderClass.videoView.pause();
                        }
                    }
                }
            }
        }
    }


    public void checkFollowings() {

        mainFab.hide();
        marqueeTxt.setText("Followings");

        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFollowingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userFollowingList.add(dataSnapshot.getKey());
                }

                //this will get the post of the users that the main user is following
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    /**
     * Gets Followings Posts only.
     **/
    private void readPosts() {

        DatabaseReference mRef = firebaseDatabase.getReference(getString(R.string.DB_POST));
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UsersPosts usersPosts = dataSnapshot.getValue(UsersPosts.class);
                    assert usersPosts != null;
                    for (String id : userFollowingList) {
                        if (usersPosts.getUserId().equals(id)) {
                            postsArrayList.add(usersPosts);
                        }
                    }
                }
                setWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void readAllPosts() {

        mainFab.hide();
        marqueeTxt.setText("Posts For You");

        allUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UsersPosts usersPost = dataSnapshot.getValue(UsersPosts.class);
                    if (usersPost != null) {
                        if (!usersPost.getUserId().equals(firebaseUser.getUid())
                                && !postsArrayList.contains(usersPost))
                            postsArrayList.add(usersPost);
                    }
                }
                setWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view.findViewById(android.R.id.content), "Something went wrong. Please try again later.",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private void setWidgets() {

        if (postsArrayList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            noPostLayout.setVisibility(View.VISIBLE);
        } else if (postsArrayList.size() == postArraySize) {
            //to nothing
            Toast toast = Toast.makeText(getContext(), "No New Post Found.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            noPostLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            postsAdapter.notifyDataSetChanged();
            playVideo();
            linearLayoutManager.scrollToPosition(postsArrayList.size() - 1);
        }
        mainFab.show();
        newPostFound.setVisibility(View.GONE);
        postArraySize = postsArrayList.size();
        marqueeTxt.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        Log.d(TAG, "setWidgets: " + postArraySize);
    }


    public void closeFabMenu() {
        fabExtended = false;
        allUsersFab.hide();
        followingFab.hide();
        shadowView.setVisibility(View.GONE);
        allUserFabTxt.setVisibility(View.GONE);
        followingFabTxt.setVisibility(View.GONE);
        mainFab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext())
                , R.drawable.ic_baseline_add_24));
        playVideo();
    }


    @Override
    public void onStart() {
        super.onStart();
        playVideo();
    }


    @Override
    public void onDestroyView() {

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
            if (realTimePostListener != null)
                realTimePostListener.removeEventListener(valueEventListener);
        }
        super.onDestroyView();
    }
}
