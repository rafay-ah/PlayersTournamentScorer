package com.players.nest.JoinMatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.players.nest.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.players.nest.HelperClasses.CameraOrGalleryDialog;
import com.players.nest.HelperClasses.ConfirmAlertDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.HelperClasses.NotificationHelper;
import com.players.nest.HelperClasses.Permissions;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.ModelClasses.MatchDispute;
import com.players.nest.ModelClasses.User;
import com.players.nest.Notifications.NotificationReq;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MatchDisputes extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 10;
    private static final int GALLERY_REQUEST = 20;
    private static final int VIDEO_GALLERY_REQUEST = 30;
    private static final String TAG = "MATCH_DISPUTES";

    Uri selectedPhoto, selectedVideo;
    LoadingDialog loadingDialog;

    Toolbar toolbar;
    Button submitBtn;
    ImageView clearIcon1, clearIcon2;
    MatchDetail matchDetail;
    TextInputEditText message;
    TextView uploadPhoto, picName, videoName, uploadVideo;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_dispute);

        toolbar = findViewById(R.id.toolbar20);
        picName = findViewById(R.id.textView177);
        submitBtn = findViewById(R.id.button17);
        clearIcon1 = findViewById(R.id.imageView56);
        clearIcon2 = findViewById(R.id.imageView57);
        uploadPhoto = findViewById(R.id.textView57);
        videoName = findViewById(R.id.textView182);
        uploadVideo = findViewById(R.id.textView183);
        message = findViewById(R.id.message);
        loadingDialog = new LoadingDialog(this);


        Intent intent = getIntent();
        MatchDetail matchDetail = intent.getParcelableExtra(Constants.MATCH_DETAIL_OBJECT);
        if (matchDetail != null) {
            this.matchDetail = matchDetail;
        } else
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();


        //Toolbar
        toolbar.setNavigationOnClickListener(v -> finish());


        submitBtn.setOnClickListener(this);
        uploadPhoto.setOnClickListener(this);
        uploadVideo.setOnClickListener(this);
        clearIcon1.setOnClickListener(this);
        clearIcon2.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView57) {

            CameraOrGalleryDialog cameraOrGalleryDialog = new CameraOrGalleryDialog(new CameraOrGalleryDialog.CameraGalleryDialogListener() {
                @Override
                public void onCamera() {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }

                @Override
                public void onGallery() {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            });

            if (Permissions.isPermissionGrantedByApp(this))
                cameraOrGalleryDialog.show(getSupportFragmentManager(), "CAMERA_GALLERY_DIALOG");
            else
                Toast.makeText(this, "Please Provide the requested permissions.", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.button17) {
            if ((Uri.EMPTY.equals(selectedPhoto) && selectedPhoto == null) || (Uri.EMPTY.equals(selectedVideo) && selectedVideo == null)) {
                Toast.makeText(this, "Please upload correct image of the scores", Toast.LENGTH_SHORT).show();
                Log.d("UPLOADINININ", "onClick: if");
            } else if (selectedPhoto != null && selectedVideo == null) {
                uploadImageOnly();
                Log.d("UPLOADINININ", "onClick: else if uploadImageOnly();");
            } else if (selectedVideo != null && selectedPhoto == null) {
                uploadVideoOnly();
                Log.d("UPLOADINININ", "onClick: else if uploadVideoOnly();");
            } else {
                submitCurrentUserEvidence();
                Log.d("UPLOADINININ", "onClick: else both();");
            }
        } else if (v.getId() == R.id.imageView56) {
            picName.setText("");
            clearIcon1.setVisibility(View.INVISIBLE);
            selectedPhoto = null;
        } else if (v.getId() == R.id.imageView57) {
            videoName.setText("");
            clearIcon2.setVisibility(View.INVISIBLE);
            selectedVideo = null;
        } else if (v.getId() == R.id.textView183) {
            openVideoGallery();
        }
    }


    private void openVideoGallery() {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_GALLERY_REQUEST);
    }


    private void submitCurrentUserEvidence() {

        loadingDialog.changeMsg("Please wait while we are uploading your evidence.");
        loadingDialog.startDialog();
        final StorageReference firebaseStorageRef = FirebaseStorage.getInstance().getReference(getString(R.string.MATCH_DISPUTES_FOLDER))
                .child(firebaseUser.getUid())
                .child(selectedPhoto.getLastPathSegment() + ".jpg");

        UploadTask uploadTask = firebaseStorageRef.putFile(selectedPhoto);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseStorageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful())
                        uploadVideo(task1.getResult());
                    else {
                        Toast.makeText(MatchDisputes.this, "Something went wrong. Please try again later.",
                                Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                });
            } else {
                Toast.makeText(MatchDisputes.this, "Something went wrong. Please try again later.",
                        Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        });
    }

    private void uploadImageOnly() {

        loadingDialog.changeMsg("Please wait while we are uploading your evidence.");
        loadingDialog.startDialog();
        final StorageReference firebaseStorageRef = FirebaseStorage.getInstance().getReference(getString(R.string.MATCH_DISPUTES_FOLDER))
                .child(firebaseUser.getUid())
                .child(selectedPhoto.getLastPathSegment() + ".jpg");

        UploadTask uploadTask = firebaseStorageRef.putFile(selectedPhoto);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseStorageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful())
                        UploadIntoDatabase(task1.getResult(), null);
                    else {
                        Toast.makeText(MatchDisputes.this, "Something went wrong. Please try again later.",
                                Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                });
            } else {
                Toast.makeText(MatchDisputes.this, "Something went wrong. Please try again later.",
                        Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        });
    }

    private void uploadVideoOnly() {
        loadingDialog.changeMsg("Please wait while we are uploading your evidence.");
        loadingDialog.startDialog();
        if (selectedVideo != null) {
            StorageReference StorageRef = FirebaseStorage.getInstance().getReference(getString(R.string.MATCH_DISPUTES_FOLDER))
                    .child(firebaseUser.getUid())
                    .child(selectedVideo.getLastPathSegment() + ".mp4");
            StorageRef.putFile(selectedVideo).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StorageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful())
                            UploadIntoDatabase(null, task1.getResult());
                    });
                } else {
                    Toast.makeText(MatchDisputes.this, "Something went wrong. Please try again later.",
                            Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                }
            });
        }
    }

    private void uploadVideo(Uri imageUri) {

        if (selectedVideo != null) {
            StorageReference StorageRef = FirebaseStorage.getInstance().getReference(getString(R.string.MATCH_DISPUTES_FOLDER))
                    .child(firebaseUser.getUid())
                    .child(selectedVideo.getLastPathSegment() + ".mp4");
            StorageRef.putFile(selectedVideo).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StorageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful())
                            UploadIntoDatabase(imageUri, task1.getResult());
                    });
                } else {
                    Toast.makeText(MatchDisputes.this, "Something went wrong. Please try again later.",
                            Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                }
            });
        } else
            UploadIntoDatabase(imageUri, null);
    }


    private void UploadIntoDatabase(Uri imageUri, Uri videoUri) {

        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("fullName").getValue().toString();
                String profileImage = snapshot.child("profilePic").getValue().toString();
                Map<String, String> userinfo = new HashMap<>();
                userinfo.put("fullName", name);
                userinfo.put("profilePic", profileImage);
                MatchDispute matchDispute;
                matchDispute = new MatchDispute(firebaseUser.getUid(), String.valueOf(imageUri),
                        String.valueOf(videoUri), String.valueOf(message.getText()), name, profileImage);

                FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCH_DISPUTES))
                        .child(matchDetail.getMatch_ID())
                        .child(firebaseUser.getUid())
                        .setValue(matchDispute)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showSuccessDialog();
                                /*Added one more field in adminApproval for checking whether it is approved by admin or not*/
                                //adminApproval();
                            } else {
                                Toast.makeText(MatchDisputes.this, "Something went wrong. Please try again later.",
                                        Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void adminApproval() {
        /*This method is for checking admin's approval*/
        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCH_DISPUTES))
                .child(matchDetail.getMatch_ID()).child("admin_approval")
                .setValue("0")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(MatchDisputes.this, "Something went wrong. Please try again later.",
                                Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                });
    }


    private void showSuccessDialog() {
        notifyOtherUser();
        ConfirmAlertDialog dialog = new ConfirmAlertDialog(Constants.EVIDENCE_SUBMITTED, this::finish);
        try {
            dialog.show(MatchDisputes.this.getSupportFragmentManager(), "CONFIRM_ALERT_DIALOG");
        } catch (IllegalStateException e) {
            Log.d(TAG, "showSuccessDialog: " + e);
        }
    }


    public void notifyOtherUser() {

        if (firebaseUser.getUid().equals(matchDetail.getHostUserId()))
            databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                    .child(matchDetail.getJoinedUserID());
        else
            databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                    .child(matchDetail.getHostUserId());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    new NotificationHelper().sendNotification(getApplicationContext(),
                            user.getDeviceToken(), "Evidence Submitted",
                            "Your Opponent has submitted the evidence of match dispute in "
                                    + matchDetail.getGame().getName(),
                            "Alert_Fragment", new NotificationReq.Data(Constants.OPEN_ALERT_FRAGMENT));
                    loadingDialog.dismissDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(MatchDisputes.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == CAMERA_REQUEST) {
                if (data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    String image = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                    picName.setText(image);
                    selectedPhoto = Uri.parse(image);
                }
            } else if (requestCode == GALLERY_REQUEST) {
                Log.d(TAG, "gallery: " + data.getData());
                picName.setText(Objects.requireNonNull(data.getData()).getLastPathSegment());
                selectedPhoto = data.getData();
            } else if (requestCode == VIDEO_GALLERY_REQUEST) {
                selectedVideo = data.getData();
                videoName.setText(String.valueOf(selectedVideo));
                Log.d(TAG, "video : " + selectedVideo);
            }
        }
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