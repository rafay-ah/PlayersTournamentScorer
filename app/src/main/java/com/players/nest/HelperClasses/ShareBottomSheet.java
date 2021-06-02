package com.players.nest.HelperClasses;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.FanFollowingActivity.Fragments.FollowingFragment;
import com.players.nest.ModelClasses.User;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.R;

import java.util.Objects;

public class ShareBottomSheet extends BottomSheetDialogFragment {

    Context context;
    public EditText message;
    ImageView imgRes;
    UsersPosts usersPost;
    FirebaseUser firebaseUser;

    public ShareBottomSheet(Context context, UsersPosts usersPost) {
        this.context = context;
        this.usersPost = usersPost;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_bottom_sheet_layout, container, false);

        imgRes = view.findViewById(R.id.imageView61);
        message = view.findViewById(R.id.textView189);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        getUserData();

        return view;
    }


    private void getUserData() {

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userOb = snapshot.getValue(User.class);
                        attachFollowingFragment(userOb);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void attachFollowingFragment(User userOb) {
        FollowingFragment followingFragment = new FollowingFragment();
        followingFragment.getDataFromActivity(Constants.SHARE_BOTTOM_SHEET, userOb, usersPost);
        FragmentManager childFragmentManager = getChildFragmentManager();
        childFragmentManager.beginTransaction().add(R.id.linearLayout36, followingFragment).commit();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(view.getContext()).load(usersPost.getImageUri()).into(imgRes);
    }

    ///For Transparent BG of the BottomSheet
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) Objects.requireNonNull(getView()).getParent()).setBackgroundColor(Color.TRANSPARENT);
        Glide.with(context).load(usersPost.getImageUri()).into(imgRes);

    }
}
