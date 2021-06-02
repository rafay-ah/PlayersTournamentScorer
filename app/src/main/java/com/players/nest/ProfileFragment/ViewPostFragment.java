package com.players.nest.ProfileFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.players.nest.HelperClasses.CachingVideos;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.DeletePostAlert;
import com.players.nest.HelperClasses.GlideImageLoader;
import com.players.nest.HelperClasses.HelperMethods;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.HelperClasses.ShareBottomSheet;
import com.players.nest.HelperClasses.ViewPostBottomSheet;
import com.players.nest.HomeFragment.CommentsActivity;
import com.players.nest.ModelClasses.Comment;
import com.players.nest.ModelClasses.User;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.SearchActivity.ViewProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.players.nest.HomeFragment.PostsAdapter.IMAGE_NOT_LIKED;
import static com.players.nest.ProfileFragment.ProfileFragment.PARCEL_KEY;

public class ViewPostFragment extends Fragment implements ViewPostBottomSheet.ViewProfileBottomSheetListener,
        DeletePostAlert.DeleteAlertListener, View.OnClickListener {

    private static final String TAG = "VIEW_POST";

    User user;
    UsersPosts usersPost;

    String TYPE;
    Toolbar toolbar;
    FrameLayout placeHolder;
    NestedScrollView nestedScrollView;
    ConstraintLayout mainVideoLayout;
    ProgressBar progressBar, progressBar2;
    LinearLayout commentLayout, imgPlaceHolder;
    ImageView postImageView, profilePic, menuIcon, likeIcon, commentIcon,
            shareIcon;
    TextView userName, usernameAndCaption, likesCount, commentCount, date;

    VideoView videoView;
    LinearLayout playIcon;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String openViewProfileFragment = null;
    ArrayList<Comment> comments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

        //Reference Created
        toolbar = view.findViewById(R.id.toolbar4);
        date = view.findViewById(R.id.textView59);
        videoView = view.findViewById(R.id.videoView2);
        menuIcon = view.findViewById(R.id.imageView5);
        userName = view.findViewById(R.id.textView16);
        likeIcon = view.findViewById(R.id.imageView13);
        likesCount = view.findViewById(R.id.textView8);
        shareIcon = view.findViewById(R.id.imageView15);
        commentCount = view.findViewById(R.id.textView11);
        profilePic = view.findViewById(R.id.profile_pic);
        postImageView = view.findViewById(R.id.imageView8);
        progressBar = view.findViewById(R.id.progressBar16);
        playIcon = view.findViewById(R.id.play_icon_layout);
        commentIcon = view.findViewById(R.id.imageView14);
        placeHolder = view.findViewById(R.id.placeholder);
        mainVideoLayout = view.findViewById(R.id.frameLayout);
        commentLayout = view.findViewById(R.id.linearLayout26);
        progressBar2 = view.findViewById(R.id.progressBar17);
        imgPlaceHolder = view.findViewById(R.id.linearLayout44);
        usernameAndCaption = view.findViewById(R.id.textView47);
        nestedScrollView = view.findViewById(R.id.nestedScrollView8);


        //Firebase Variables Instantiated
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        Bundle bundle = getArguments();
        if (bundle != null) {
            TYPE = bundle.getString("TYPE");
            openViewProfileFragment = bundle.getString(Constants.FROM_CHAT_FRAGMENT);
            UsersPosts usersPost = bundle.getParcelable(PARCEL_KEY);
            assert usersPost != null;
            this.usersPost = usersPost;
            setWidgets(usersPost);
        } else {
            Toast.makeText(getContext(), "Error. Please Try again later.", Toast.LENGTH_SHORT).show();
        }


        //onClickListeners
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view12 -> Objects.requireNonNull(getActivity()).onBackPressed());
        menuIcon.setOnClickListener(view1 -> {
            if (usersPost != null) {
                ViewPostBottomSheet bottomSheet = new ViewPostBottomSheet(TYPE, usersPost.isTurnOffComments());

                assert getFragmentManager() != null;
                bottomSheet.setTargetFragment(ViewPostFragment.this, 100);
                bottomSheet.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "ViewProfileBottomSheet");
            }
        });
        userName.setOnClickListener(this);
        commentIcon.setOnClickListener(this);
        commentLayout.setOnClickListener(this);
        likeIcon.setOnClickListener(this);
        shareIcon.setOnClickListener(this);
        mainVideoLayout.setOnClickListener(this);

        return view;
    }


    @SuppressLint("SetTextI18n")
    private void setWidgets(UsersPosts usersPost) {

        progressBar.setVisibility(View.VISIBLE);
        if (usersPost.getPostType().equals(Constants.VIDEO_POST_TYPE))
            playVideo();
        else {
            videoView.setVisibility(View.GONE);
            mainVideoLayout.setVisibility(View.GONE);
            postImageView.setVisibility(View.VISIBLE);
            imgPlaceHolder.setVisibility(View.VISIBLE);
            GlideImageLoader.loadImageWithPlaceHolder(getContext(), usersPost.getImageUri()
                    , postImageView, imgPlaceHolder);
        }

        //If Posts Turn off Comments is enabled.
        if (usersPost.isTurnOffComments()) {
            commentLayout.setVisibility(View.GONE);
            commentIcon.setVisibility(View.GONE);
        } else {
            commentLayout.setVisibility(View.VISIBLE);
            commentIcon.setVisibility(View.VISIBLE);
        }

        //Setting Date
        CharSequence dateAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(usersPost.getDateCreated())
                , System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        date.setText(dateAgo);

        //Setting Likes
        HashMap<String, Object> likesHashMap = usersPost.getLikes();
        if (likesHashMap == null) {
            likesCount.setText("0 Likes");
            likeIcon.setTag(IMAGE_NOT_LIKED);
        }

        //Attaching Like Listener
        HelperMethods helperMethods = new HelperMethods(Objects.requireNonNull(getContext()), usersPost.getPostId(), likeIcon);
        helperMethods.likeListener(likesCount);

        getPostData(usersPost);
        getComments();
    }


    private void getComments() {
        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_COMMENTS))
                .child(usersPost.getPostId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            comments.add(comment);
                        }
                        if (comments.size() == 0)
                            commentLayout.setVisibility(View.GONE);
                        else {
                            commentLayout.setVisibility(View.VISIBLE);
                            commentCount.setText(getString(R.string.VIEW_ALL_COMMENTS, comments.size()));
                        }

                        progressBar.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void playVideo() {

        mainVideoLayout.setVisibility(View.VISIBLE);
        postImageView.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.VISIBLE);

        String videoUriLastPathSegment = Uri.parse(usersPost.getImageUri()).getLastPathSegment();

        if (CachingVideos.isVideoExistsInCache(getContext(), videoUriLastPathSegment)) {
            assert videoUriLastPathSegment != null;
            videoView.setVideoPath(CachingVideos.getVideoFile(getContext(), videoUriLastPathSegment).getPath());
            videoView.start();
        } else
            CachingVideos.putVideoIntoCache(getContext(), videoView, usersPost.getImageUri());


        videoView.setOnPreparedListener(mp -> {
            progressBar2.setVisibility(View.GONE);
            placeHolder.setVisibility(View.GONE);
        });
        videoView.setOnInfoListener((mp, what, extra) -> {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                    progressBar2.setVisibility(View.GONE);
                    return true;
                }
                case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                    progressBar2.setVisibility(View.VISIBLE);
                    return true;
                }
            }
            return false;
        });
        videoView.setOnCompletionListener(mp -> videoView.start());
    }


    private void getPostData(final UsersPosts usersPost) {

        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(usersPost.getUserId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userAccountData = snapshot.getValue(User.class);
                assert userAccountData != null;
                user = userAccountData;
                userName.setText(userAccountData.getUsername());
                if (!userAccountData.getProfilePic().equals(""))
                    Glide.with(Objects.requireNonNull(getActivity())).load(userAccountData.getProfilePic()).into(profilePic);

                usernameAndCaption.setText(HelperMethods.usernameAndCaption(userAccountData.getUsername(), usersPost.getCaption()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    /**
     * ViewPostBottomSheet Listener's Override Methods
     */
    @Override
    public void onDelete() {
        DeletePostAlert deletePostAlert = new DeletePostAlert();
        assert getFragmentManager() != null;
        deletePostAlert.setTargetFragment(ViewPostFragment.this, 101);
        deletePostAlert.show(getFragmentManager(), "DeletePostAlert");
    }


    @Override
    public void onEditPost() {
        EditText editText = new EditText(getContext());
        editText.setText(usersPost.getCaption());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Caption");
        builder.setView(editText);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String caption = editText.getText().toString();
            changeCaptionOfPost(caption);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    @Override
    public void onTurnOffCommenting() {

        //Checking if Comments are already turned off or not.
        turnOffComments(!usersPost.isTurnOffComments());
    }


    @SuppressLint("ShowToast")
    private void turnOffComments(boolean value) {

        HashMap<String, Object> turnOffComments = new HashMap<>();
        turnOffComments.put("turnOffComments", value);

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_POST))
                .child(usersPost.getPostId())
                .updateChildren(turnOffComments);

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USER_POSTS))
                .child(firebaseUser.getUid())
                .child(usersPost.getPostId())
                .updateChildren(turnOffComments)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast toast;
                        if (value) {
                            toast = Toast.makeText(getContext(), "Comments are Turned off. " +
                                    "From now no one can comment on your post.", Toast.LENGTH_SHORT);
                        } else {
                            toast = Toast.makeText(getContext(), "Comments turned back on.", Toast.LENGTH_SHORT);
                            commentIcon.setVisibility(View.VISIBLE);
                        }
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else
                        Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                });
    }


    private void changeCaptionOfPost(String caption) {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startDialog();
        loadingDialog.changeMsg("Updating...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("caption", caption);
        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_POST))
                .child(usersPost.getPostId())
                .updateChildren(hashMap);
        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USER_POSTS))
                .child(firebaseUser.getUid())
                .child(usersPost.getPostId())
                .updateChildren(hashMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        usernameAndCaption.setText(HelperMethods.usernameAndCaption(user.getUsername(), caption));
                    } else
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void deletePost() {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USER_POSTS))
                .child(usersPost.getUserId()).child(usersPost.getPostId());
        //Also Deleting from Post Folder inside DB.
        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_POST)).child(usersPost.getPostId()).removeValue();

        myRef.removeValue().addOnCompleteListener(task -> {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(usersPost.getImageUri());
            storageReference.delete().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Toast.makeText(getContext(), "Deleted.", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getActivity()).onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Error while deleting post.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.imageView13) {
            HelperMethods helperMethods = new HelperMethods(Objects.requireNonNull(getContext()), usersPost.getPostId(), likeIcon);
            if (likeIcon.getTag().equals(IMAGE_NOT_LIKED))
                helperMethods.likePostAndSaveIntoDatabase();
            else
                helperMethods.unLikePost();
        } else if (view.getId() == R.id.imageView14 || view.getId() == R.id.linearLayout26) {
            Intent intent = new Intent(getContext(), CommentsActivity.class);
            intent.putExtra(Constants.USER_OBJECT, user);
            intent.putExtra(Constants.USER_POST_OBJECT, usersPost);
            startActivity(intent);
        } else if (view.getId() == R.id.textView16 && openViewProfileFragment != null) {
            Intent intent = new Intent(getContext(), ViewProfileActivity.class);
            intent.putExtra(Constants.USER_OBJECT, user);
            startActivity(intent);
            ((FragmentActivity) (Objects.requireNonNull(getContext()))).overridePendingTransition(0, 0);
        } else if (view.getId() == R.id.imageView15) {
            ShareBottomSheet bottomSheet = new ShareBottomSheet(getContext(), usersPost);
            bottomSheet.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "SHARE_BOTTOM_SHEET");
        } else if (view.getId() == R.id.frameLayout) {
            if (!videoView.isPlaying()) {
                videoView.start();
                playIcon.setVisibility(View.GONE);
            } else {
                videoView.pause();
                playIcon.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (videoView.getVisibility() == View.VISIBLE) {
            videoView.stopPlayback();
        }
    }
}
