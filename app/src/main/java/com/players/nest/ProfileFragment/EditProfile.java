package com.players.nest.ProfileFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.players.nest.Login_Register.CheckUniqueUsername;
import com.players.nest.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.players.nest.HelperClasses.BottomSheetNav;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.HelperClasses.Permissions;
import com.players.nest.ModelClasses.User;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity implements BottomSheetNav.BottomSheetListener {

    public final int REQUEST_PERMISSION_CODE = 101;
    private static final String TAG = "EDIT_PROFILE";

    //firebase
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    String lastUserName, lastProfilePic;

    //Fragment widgets
    User user;
    Uri imageUri;
    Toolbar toolbar;
    ImageView plusIcon;
    EditText mPhoneNumber;
    TextView mChangeProfilePhoto;
    LoadingDialog loadingDialog;
    ImageView mProfilePhoto;
    TextInputLayout userNameLayout, fullNameLayout, emailLayout;
    TextInputEditText mDisplayName, mUsername, mDescription, mEmail;
    ConstraintLayout constraintLayout;
    boolean isUsernameUnique;
    private CheckUniqueUsername checkUniqueUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = findViewById(R.id.toolbar6);
        mProfilePhoto = findViewById(R.id.profile_pic);
        mDisplayName = findViewById(R.id.fullName);
        mUsername = findViewById(R.id.userName);
        mDescription = findViewById(R.id.desc);
        mEmail = findViewById(R.id.email_ID);
        plusIcon = findViewById(R.id.imageView12);
        mPhoneNumber = findViewById(R.id.phoneNumber);
        userNameLayout = findViewById(R.id.textInputLayout2);
        fullNameLayout = findViewById(R.id.display_name);
        emailLayout = findViewById(R.id.email);
        mChangeProfilePhoto = findViewById(R.id.changeProfilePhoto);
        constraintLayout = findViewById(R.id.constrainLayout);

        loadingDialog = new LoadingDialog(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());


        mChangeProfilePhoto.setOnClickListener(v -> {
            BottomSheetNav bottomDialog = new BottomSheetNav();
            bottomDialog.show(getSupportFragmentManager(), null);
        });

        //Custom Methods
        getData();
    }


    /**
     * Retrieves the data contained in the widgets and submits it to the database
     * Before donig so it chekcs to make sure the username chosen is unqiue
     *
     * @param deviceToken
     */



    public void saveProfileSettings(String deviceToken) {

        loadingDialog.startDialog();
        final String displayName = String.valueOf(mDisplayName.getText());
        final String username = String.valueOf(mUsername.getText());
        final String description = String.valueOf(mDescription.getText());
        final String email = String.valueOf(mEmail.getText());
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());
        User data = user;
        checkUniqueUsername = new CheckUniqueUsername() {
            @Override
            public void isUnique(boolean unique) {
                if(unique || data.getUsername().equals(username) ){
                    User user = new User(firebaseUser.getUid(), phoneNumber, email, displayName, username,
                            description, "", "", deviceToken, data.getRatings(), data.getAccount_balance());

                    if (imageUri == null && lastProfilePic.equals("")) {
                        addToDatabase(user);
                    } else if (imageUri != null) {
                        saveToStorage(displayName, username, description, email, phoneNumber, imageUri, deviceToken, data.getRatings());
                    } else if (lastProfilePic != null) {
                        if (!lastProfilePic.equals("")) {
                            User user2 = new User(firebaseUser.getUid(), phoneNumber, email, displayName, username,
                                    description, lastProfilePic, "", deviceToken, data.getRatings(), data.getAccount_balance());
                            addToDatabase(user2);
                        }
                    }
                }
                else{
                    Snackbar.make(constraintLayout,"Username already taken",BaseTransientBottomBar.LENGTH_LONG).show();
                    mUsername.setError("Username already taken");
                }

            }
        };


    }

    private void isUserNameExists() {
        loadingDialog.startDialog();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS));
        final String userName = String.valueOf(mUsername.getText());
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isUsernameUnique = true;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (Objects.requireNonNull(user).getUsername().equals(userName)) {
                        loadingDialog.dismissDialog();
                        isUsernameUnique = false;
                        break;
                    }
                }
                checkUniqueUsername.isUnique(isUsernameUnique);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveToStorage(final String displayName, final String username, final String description,
                               final String email, final long phoneNumber, final Uri imageUri,
                               final String deviceToken, int ratings) {

        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.POST_FOLDER))
                .child(firebaseUser.getUid() + "/" + "PROFILE_PIC");
        storageReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    User user = new User(firebaseUser.getUid(), phoneNumber, email, displayName,
                            username, description, String.valueOf(uri), "",
                            deviceToken, ratings, 0);
                    addToDatabase(user);
                });
            } else {
                loadingDialog.dismissDialog();
                Toast.makeText(EditProfile.this, Objects.requireNonNull(task.getException())
                        .getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addToDatabase(User user) {

        databaseReference.setValue(user).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                loadingDialog.dismissDialog();
                Toast.makeText(EditProfile.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                loadingDialog.dismissDialog();
                Toast.makeText(EditProfile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    //case2: if the user made a change to their email

    // step1) Reauthenticate
    //          -Confirm the password and email
//            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
//            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
//            dialog.setTargetFragment(EditProfileFragment.this, 1);

    // step2) check if the email already is registered
    //          -'fetchProvidersForEmail(String email)'
    // step3) change the email
    //          -submit the new email to the database and authentication


    private void checkIfEmailExists(final String email) {

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS));

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean flag = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String searchEmail = String.valueOf(dataSnapshot.child("email").getValue());
                    if (searchEmail.equals(email)) {
                        flag = true;

                    }
                }
                if (!flag) {
//                    emailLayout.setError("Email address does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getImageFromUserAndCrop(Uri imageUri) {

        UCrop uCrop = UCrop.of(Objects.requireNonNull(imageUri), Uri.fromFile(new File(getCacheDir(),
                Objects.requireNonNull(imageUri.getLastPathSegment()))));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(150, 150);

        //Changing the UI of the Cropping Activity
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarWidgetColor(getResources().getColor(R.color.PrimaryDarkWhite));

        uCrop.withOptions(options);
        uCrop.start(this);
    }


//
//    @Override
//    public void onConfirmPassword(String password) {
//        Log.d(TAG, "onConfirmPassword: got the password: " + password);
//
//        // Get auth credentials from the user for re-authentication. The players below shows
//        // email and password credentials but there are multiple possible providers,
//        // such as GoogleAuthProvider or FacebookAuthProvider.
//        AuthCredential credential = EmailAuthProvider
//                .getCredential(mAuth.getCurrentUser().getEmail(), password);
//
//        ///////////////////// Prompt the user to re-provide their sign-in credentials
//        mAuth.getCurrentUser().reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User re-authenticated.");
//
//                            ///////////////////////check to see if the email is not already present in the database
//                            mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
//                                @Override
//                                public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
//                                    if (signInMethodQueryResult != null) {
//                                        try {
//                                            if (task.getResult().getProviders().size() == 1) {
//                                                Log.d(TAG, "onComplete: that email is already in use.");
//                                                Toast.makeText(EditProfileActivity.this, "That email is already in use", Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                Log.d(TAG, "onComplete: That email is available.");
//
//                                                //////////////////////the email is available so update it
//                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isSuccessful()) {
//                                                                    Log.d(TAG, "User email address updated.");
//                                                                    Toast.makeText(EditProfileActivity.this, "email updated", Toast.LENGTH_SHORT).show();
//                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
//                                                                }
//                                                            }
//                                                        });
//                                            }
//                                        } catch (NullPointerException e) {
//                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
//                                        }
//                                    }
//                                }
//                            });
//                        } else {
//                            Log.d(TAG, "onComplete: re-authentication failed.");
//                        }
//
//                    }
//                });
//    }

    private void setWidgets(User user) {

        lastUserName = user.getUsername();
        lastProfilePic = user.getProfilePic();

        mUsername.setText(user.getUsername());
        mDisplayName.setText(user.getFullName());
        mDescription.setText(user.getDescription());
        mEmail.setText(user.getEmail());
        mPhoneNumber.setText(String.valueOf(user.getPhone_number()));

        if (user.getProfilePic() != null) {
            if (!user.getProfilePic().equals("")) {
                Glide.with(this).load(user.getProfilePic()).into(mProfilePhoto);
                plusIcon.setVisibility(View.GONE);
            }
        }
    }


    private void getData() {


        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS)).child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userOb = snapshot.getValue(User.class);
                assert userOb != null;
                user = userOb;
                setWidgets(userOb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void checkCredentials() {

        String user_name = String.valueOf(mUsername.getText());
        String full_name = String.valueOf(mDisplayName.getText());
        String email = String.valueOf(mEmail.getText());

        fullNameLayout.setError(null);
        userNameLayout.setError(null);
        emailLayout.setError(null);

        if (user_name.isEmpty()) {
            userNameLayout.setError("User name can't be Empty.");
        } else if (full_name.isEmpty()) {
            fullNameLayout.setError("Please enter your Full name.");
        } else if (email.isEmpty()) {
            emailLayout.setError("Please enter your Email address.");
        } else {
            Pattern pattern = Pattern.compile("^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{0,29}$");
            Matcher matcher = pattern.matcher(user_name);
            if(matcher.matches()) {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isUserNameExists();
                        saveProfileSettings(task.getResult());
                    } else
                        Toast.makeText(EditProfile.this, "Something went wrong. Please try again later.",
                                Toast.LENGTH_SHORT).show();
                });
            }
            else {
                mUsername.setError("Username not valid");
                Snackbar.make(constraintLayout, "Username cannot contains special character or space except '.' and '_'", Snackbar.LENGTH_LONG).show();
            }
        }
    }


    ////////////////////////////////////////////////////////OVERRIDDEN METHODS////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_fragment_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.post) {
            checkCredentials();
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUEST_PERMISSION_CODE == requestCode) {
            if (grantResults.length > 0) {
                for (int grantedPermissions : grantResults) {
                    if (grantedPermissions == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                    }
                }
            } else
                Toast.makeText(this, "Please grant permissions to proceed.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == REQUEST_PERMISSION_CODE && resultCode == RESULT_OK) {
                getImageFromUserAndCrop(Uri.parse(data.getDataString()));
            } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
                imageUri = UCrop.getOutput(data);
                mProfilePhoto.setImageURI(UCrop.getOutput(data));
                plusIcon.setVisibility(View.GONE);
            }
        } else
            Toast.makeText(this, "Please Select a Image.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void changePic() {
        if (Permissions.isPermissionGrantedByApp(EditProfile.this)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select an image"), REQUEST_PERMISSION_CODE);
        } else
            ActivityCompat.requestPermissions(EditProfile.this, Permissions.PERMISSIONS, REQUEST_PERMISSION_CODE);
    }


    @Override
    public void removePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?");
        builder.setTitle("Remove profile picture");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProfilePhoto.setImageResource(R.drawable.ic_no_profile_pic_logo_1);
                plusIcon.setVisibility(View.VISIBLE);
                imageUri = null;
                lastProfilePic = "";
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
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
