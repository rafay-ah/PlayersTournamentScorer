package com.players.nest.PostFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.MainActivity;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.R;

import java.util.Objects;

import static com.players.nest.HelperClasses.Constants.SELECTED_FILE_URL;


public class PostActivity extends AppCompatActivity {

    private static final String TAG = "POST_ACTIVITY";

    boolean error;
    Toolbar toolbar;
    ImageView imageView;
    SwitchCompat switchCompat;
    Uri imageUri, videoUri;
    LoadingDialog loadingDialog;
    TextInputEditText caption;
    String fileType, selectedFileUrl, postType;

    //Firebase Variables
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        toolbar = findViewById(R.id.toolbar3);
        caption = findViewById(R.id.caption);
        imageView = findViewById(R.id.imageView6);
        switchCompat = findViewById(R.id.switch1);


        //Setting Toolbar..
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(
                view -> finish());


        //Getting Data from Previous Fragment
        Intent intent = getIntent();
        selectedFileUrl = intent.getStringExtra(SELECTED_FILE_URL);
        postType = intent.getStringExtra(Constants.POST_TYPE);

        checkAPIVersion();
    }


    public void checkAPIVersion() {

        if (selectedFileUrl != null && postType != null) {
            error = false;
            if (postType.equals(Constants.VIDEO_POST_TYPE)) {
                Glide.with(this).load(selectedFileUrl).into(imageView);
                fileType = Constants.VIDEO_FILE;
                videoUri = Uri.parse(selectedFileUrl);
            } else {
                imageUri = Uri.parse(selectedFileUrl);
                Glide.with(this).load(imageUri).into(imageView);
                fileType = Constants.IMAGE_FILE;
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Something went wrong. Please try again later.",
                    Snackbar.LENGTH_LONG).show();
            error = true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_fragment_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (error)
            checkAPIVersion();
        else
            uploadFileIntoFirebaseStorage();
        return true;
    }


    private void uploadFileIntoFirebaseStorage() {

        loadingDialog = new LoadingDialog(this);
        loadingDialog.changeMsg("Uploading Please wait...");
        loadingDialog.startDialog();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        UploadTask uploadTask;

        if (fileType.equals(Constants.VIDEO_FILE)) {
            storageReference = FirebaseStorage.getInstance().getReference().child(getString(R.string.POST_FOLDER))
                    .child(firebaseUser.getUid()).child("Videos")
                    .child(videoUri.getLastPathSegment());
            uploadTask = storageReference.putFile(videoUri);
        } else {
            storageReference = FirebaseStorage.getInstance().getReference().child(getString(R.string.POST_FOLDER)
                    + firebaseUser.getUid() + "/" + imageUri.getLastPathSegment());
            uploadTask = storageReference.putFile(imageUri);
        }
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!fileType.equals(Constants.VIDEO_FILE))
                    Toast.makeText(PostActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(PostActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();

                storageReference.getDownloadUrl().addOnSuccessListener(PostActivity.this, this::UploadDataIntoDatabase);
            } else {
                Toast.makeText(PostActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        });
    }


    private void UploadDataIntoDatabase(Uri uri) {

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_POST));

        String captionString = String.valueOf(caption.getText());
        String dateCreated = String.valueOf(System.currentTimeMillis());
        String postsId = mRef.push().getKey();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        UsersPosts usersPosts;
        if (fileType.equals(Constants.VIDEO_FILE)) {
            usersPosts = new UsersPosts(captionString, dateCreated, uri.toString(), postsId, uid, "",
                    Constants.VIDEO_POST_TYPE, switchCompat.isChecked(), null);
        } else
            usersPosts = new UsersPosts(captionString, dateCreated, uri.toString(), postsId, uid, "",
                    Constants.IMAGE_POST_TYPE, switchCompat.isChecked(), null);


        assert postsId != null;
        mRef.child(postsId).setValue(usersPosts).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                loadingDialog.dismissDialog();
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(PostActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        });

        //Adding Data into User_Posts node
        DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USER_POSTS)).child(uid).child(postsId);
        mRef2.setValue(usersPosts);
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