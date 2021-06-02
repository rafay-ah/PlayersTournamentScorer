package com.players.nest.MatchDisputeChat.View;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.players.nest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.HelperClasses.Permissions;
import com.players.nest.MatchDisputeChat.Adapter.MatchDisputeChatAdapter;
import com.players.nest.MatchDisputeChat.Helper.WinnerDialog;
import com.players.nest.MatchDisputeChat.Model.MatchDisputeModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.players.nest.GlobalVariable.HelperClass.RECEIVER_ID;
import static com.players.nest.GlobalVariable.HelperClass.RECEIVER_NAME;
import static com.players.nest.GlobalVariable.HelperClass.SENDER_NAME;
import static com.players.nest.GlobalVariable.HelperClass.SENDER_ONE_ID;

public class MatchDisputeChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String matchID;
    private ProgressBar progressBarMain;
    private NestedScrollView nestedScrollView;

    /*User 1*/
    private LinearLayout linearLayoutUserOne;
    private ImageView imageViewUserOne;
    private CardView cardViewUserOne;
    private TextView textViewUserOne;
    private TextView textViewUserNameOne;
    private CircleImageView profileUserOne;
    private String idUserOne;
    private String messageUserOne;
    private String scoreImageUserOne;
    private String scoreVideoUserOne;

    /*User 2*/
    private ImageView imageViewUserTwo;
    private CardView cardViewUserTwo;
    private TextView textViewUserTwo;
    private LinearLayout linearLayoutUserTwo;
    private TextView textViewUserNameTwo;
    private CircleImageView profileUserTwo;
    private String idUserTwo;
    private String messageUserTwo;
    private String scoreImageUserTwo;
    private String scoreVideoUserTwo;

    //Chat test
    private EditText chatEditText;
    private CardView cardViewSend;
    private TextView textViewChat;
    private ImageView attachImage;
    private LoadingDialog loadingDialog;
    private WinnerDialog winnerDialog;

    private RecyclerView recyclerView;
    private MatchDisputeChatAdapter matchDisputeChatAdapter;
    private final ArrayList<MatchDisputeModel> matchDisputeModelsChats = new ArrayList<>();
    private FirebaseUser firebaseUser;

    private LinearLayout linearLayoutAdminJoined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_dispute_chat);
        findViewByID();
        setToolbar();
        idUserOne = getIntent().getStringExtra("user_id");
        matchID = getIntent().getStringExtra("match_id");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        loadingDialog = new LoadingDialog(this);
        winnerDialog = new WinnerDialog(this);
        userOneAndTwoData();
        showingChatHasStartedText();
        setChat();
        clickListeners();
        checkWinner();
        joiningAdmin();
    }

    private void joiningAdmin() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Match_Disputes").child(matchID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("admin_joined")) {
                    if (snapshot.child("admin_joined").getValue().toString().equals("yes")) {
                        linearLayoutAdminJoined.setVisibility(View.VISIBLE);
                    } else {
                        linearLayoutAdminJoined.setVisibility(View.GONE);
                    }
                } else {
                    linearLayoutAdminJoined.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkWinner() {
        FirebaseDatabase.getInstance().getReference("Matches").child(matchID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String matchWinner = snapshot.child("matchWinner").getValue().toString();
                if (matchWinner.equals("")) {

                } else {
                    FirebaseDatabase.getInstance().getReference("users").child(matchWinner).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userName = snapshot.child("fullName").getValue().toString();
                            if (!userName.equals("null") || !userName.equals("") || userName != null)
                                winnerDialog.changeMsg("The winner is\n" + userName);
                            winnerDialog.startDialog();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickListeners() {
        cardViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chatEditText.getText().toString().equals("")) {
                    sendingTextMessage();
                }

            }
        });
        attachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChooserDialog();
            }
        });
    }

    /*Attaching Image*/
    private void openChooserDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a File");

        CharSequence[] options = new CharSequence[]{
                "Camera", "Gallery"};

        builder.setItems(options, (dialog, which) -> {

            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openGallery() {
        if (Permissions.isPermissionGrantedByApp(this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
        } else
            requestPermissions(Permissions.PERMISSIONS, Permissions.PERMISSION_REQUEST_CODE);
    }

    public void openCamera() {
        if (Permissions.isPermissionGrantedByApp(this)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE);
        } else
            requestPermissions(Permissions.PERMISSIONS, Permissions.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == Permissions.PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean flag = true;
                for (int permission : grantResults) {
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Please Grant Permissions to proceed.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Please Grant Permissions", Toast.LENGTH_LONG).show();
                this.onBackPressed();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*readMsgRef.addValueEventListener(chatEventListener);
        databaseReference.addValueEventListener(settingIsSeenTrue);
        statusRef.addValueEventListener(statusRealTimeListener);*/

        if (data != null) {
            if (requestCode == Constants.CAMERA_REQUEST_CODE) {
                if (data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    String image = MediaStore.Images.Media.insertImage(this.getContentResolver(),
                            bitmap, "Title", null);

                    addImageToStorage(Uri.parse(image));
                }
            } else if (requestCode == Constants.GALLERY_REQUEST_CODE) {
                addImageToStorage(Uri.parse(data.getDataString()));
            }
        }
    }

    private void addImageToStorage(Uri imageUri) {

        loadingDialog.startDialog();
        loadingDialog.changeMsg("Sending image...");
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(getString(R.string.CHAT_IMAGES_FOLDER))
                .child(firebaseUser.getUid())
                .child(Objects.requireNonNull(imageUri.getLastPathSegment()));
        storageReference.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storageReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                addImageMsgToDatabase(Objects.requireNonNull(task1.getResult()));
                            } else {
                                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                        });
                    } else {
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                });
    }

    private void addImageMsgToDatabase(Uri result) {

        String messageId = FirebaseDatabase.getInstance().getReference("Match_Disputes").child(matchID).child("Chat").push().getKey();
        final MatchDisputeModel matchDisputeModel = new MatchDisputeModel("null", idUserOne, RECEIVER_ID, "true", "user", result.toString(), System.currentTimeMillis());
        assert messageId != null;
        FirebaseDatabase.getInstance().getReference("Match_Disputes").child(matchID).child("Chat").child(messageId).setValue(matchDisputeModel).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                loadingDialog.dismissDialog();
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                loadingDialog.dismissDialog();
            }
        });
    }
    /*Attaching Image*/

    private void setChat() {
        nestedScrollView.setVisibility(View.GONE);
        progressBarMain.setVisibility(View.VISIBLE);
        matchDisputeChatAdapter = new MatchDisputeChatAdapter(this, matchDisputeModelsChats, matchID);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(matchDisputeChatAdapter);
        FirebaseDatabase.getInstance().getReference("Match_Disputes").child(matchID).child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchDisputeModelsChats.clear();
                nestedScrollView.setVisibility(View.VISIBLE);
                progressBarMain.setVisibility(View.GONE);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MatchDisputeModel matchDisputeModel = dataSnapshot.getValue(MatchDisputeModel.class);
                    assert matchDisputeModel != null;
                    if (matchDisputeModel.getSenderId().equals(firebaseUser.getUid()) && matchDisputeModel.getReceiverId().equals(RECEIVER_ID)
                            || matchDisputeModel.getSenderId().equals(RECEIVER_ID) && matchDisputeModel.getReceiverId().equals(firebaseUser.getUid()))
                        matchDisputeModelsChats.add(matchDisputeModel);
                }
                matchDisputeChatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(matchDisputeModelsChats.size() - 1);
                nestedScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showingChatHasStartedText() {
        FirebaseDatabase.getInstance().getReference("Match_Disputes").child(matchID).child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                if (count == 0) {
                    textViewChat.setVisibility(View.GONE);
                } else {
                    textViewChat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendingTextMessage() {
        String key = FirebaseDatabase.getInstance().getReference("Match_Disputes").child(matchID).child("Chat").push().getKey();
        /*Check whether it is imageurl or text message. if it is message then blank the image url/make it null. */
        final MatchDisputeModel matchDisputeModel = new MatchDisputeModel(chatEditText.getText().toString(), idUserOne, RECEIVER_ID, "true", "user", "null", System.currentTimeMillis());
        assert key != null;
        FirebaseDatabase.getInstance().getReference("Match_Disputes").child(matchID).child("Chat").child(key).setValue(matchDisputeModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                chatEditText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MatchDisputeChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userOneAndTwoData() {
        FirebaseDatabase.getInstance().getReference("Match_Disputes")
                .child(matchID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    arrayList.add(dataSnapshot.getKey());
                    if (dataSnapshot.exists()) {
                        if (Objects.equals(dataSnapshot.getKey(), idUserOne)) {
                            if (dataSnapshot.exists()) {
                                SENDER_ONE_ID = idUserOne;
                                SENDER_NAME = dataSnapshot.child("userName").getValue().toString();
                                String image = dataSnapshot.child("scoreImage").getValue().toString();
                                String video = dataSnapshot.child("scoreVideo").getValue().toString();
                                String message = dataSnapshot.child("message").getValue().toString();
                                settingUsers(imageViewUserOne, cardViewUserOne, textViewUserOne, image, video, message);
                                setUserName(idUserOne, textViewUserNameOne, "You", profileUserOne);
                            }
                        } else if (!dataSnapshot.getKey().equals(idUserOne) && !dataSnapshot.getKey().equals("Chat") && !dataSnapshot.getKey().equals("admin_approval") && !dataSnapshot.getKey().equals("admin_joined")) {
                            if (dataSnapshot.exists()) {
                                idUserTwo = dataSnapshot.getKey();
                                RECEIVER_ID = dataSnapshot.getKey();
                                RECEIVER_NAME = dataSnapshot.child("userName").getValue().toString();
                                String image = dataSnapshot.child("scoreImage").getValue().toString();
                                String video = dataSnapshot.child("scoreVideo").getValue().toString();
                                String message = dataSnapshot.child("message").getValue().toString();
                                settingUsers(imageViewUserTwo, cardViewUserTwo, textViewUserTwo, image, video, message);
                                setUserName(idUserTwo, textViewUserNameTwo, "", profileUserTwo);
                            }
                        }
                    }
                }
                arrayList.remove("admin_approval");
                arrayList.remove("Chat");
                if (arrayList.size() == 0) {
                    linearLayoutUserOne.setVisibility(View.GONE);
                    linearLayoutUserTwo.setVisibility(View.GONE);
                } else if (arrayList.contains(idUserOne) && arrayList.size() == 1) {
                    linearLayoutUserOne.setVisibility(View.VISIBLE);
                    linearLayoutUserTwo.setVisibility(View.GONE);
                } else if (!arrayList.contains(idUserOne) && arrayList.size() == 1) {
                    linearLayoutUserOne.setVisibility(View.GONE);
                    linearLayoutUserTwo.setVisibility(View.VISIBLE);
                } else if (arrayList.size() == 2) {
                    linearLayoutUserOne.setVisibility(View.VISIBLE);
                    linearLayoutUserTwo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUserName(String id, TextView textView, String name, CircleImageView circleImageView) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullname = Objects.requireNonNull(snapshot.child("fullName").getValue()).toString();
                String profileUrl = snapshot.child("profilePic").getValue().toString();
                Log.d("PROFILEURL", "onDataChange: " + profileUrl);

                Glide.with(getApplicationContext()).load(profileUrl).placeholder(ContextCompat.getDrawable(MatchDisputeChatActivity.this, R.drawable.ic_no_profile_pic_logo_1)).into(circleImageView);

                if (name.equals("")) {
                    textView.setText(fullname);
                } else {
                    textView.setText("You");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void settingUsers(ImageView imageView, CardView cardView, TextView textView, String scoreImage, String scoreVideo, String message) {
        if (!scoreImage.equals("null") && scoreVideo.equals("null")) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(scoreImage).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickImageAndVideo("image", scoreImage);
                }
            });
            cardView.setVisibility(View.GONE);
        } else if (!scoreVideo.equals("null") && scoreImage.equals("null")) {
            cardView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickImageAndVideo("video", scoreVideo);
                }
            });
        } else {
            cardView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickImageAndVideo("video", scoreVideo);
                }
            });
            Glide.with(getApplicationContext()).load(scoreImage).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickImageAndVideo("image", scoreImage);
                }
            });
        }
        if (message.equals("")) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText("Message: " + message);
        }
    }

    private void setClickImageAndVideo(String source, String url) {
        Intent intent = new Intent(this, PhotoOrVideoActivity.class);
        intent.putExtra("source", source);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    /*Find view by id's */
    private void findViewByID() {
        toolbar = findViewById(R.id.match_dispute_chat_toolbar);
        nestedScrollView = findViewById(R.id.scrollViewNested);
        progressBarMain = findViewById(R.id.progress_match_dispute);

        textViewUserNameOne = findViewById(R.id.match_dispute_chat_user_one_name);
        textViewUserNameTwo = findViewById(R.id.match_dispute_chat_user_two_name);

        imageViewUserOne = findViewById(R.id.match_dispute_chat_user_one_image);
        imageViewUserTwo = findViewById(R.id.match_dispute_chat_user_two_image);

        cardViewUserOne = findViewById(R.id.match_dispute_chat_user_one_video);
        cardViewUserTwo = findViewById(R.id.match_dispute_chat_user_two_video);

        linearLayoutUserOne = findViewById(R.id.linear_layout_user_one);
        linearLayoutUserTwo = findViewById(R.id.linear_layout_user_two);

        textViewUserOne = findViewById(R.id.match_dispute_chat_user_one_text);
        textViewUserTwo = findViewById(R.id.match_dispute_chat_user_two_text);

        profileUserOne = findViewById(R.id.match_dispute_profile_one);
        profileUserTwo = findViewById(R.id.match_dispute_profile_two);

        chatEditText = findViewById(R.id.chat_edittext);
        cardViewSend = findViewById(R.id.card_view_send);
        textViewChat = findViewById(R.id.chat_start);
        recyclerView = findViewById(R.id.chat_recyclerview);
        attachImage = findViewById(R.id.attach_image);

        linearLayoutAdminJoined = findViewById(R.id.admin_join);
    }

    /*Setting Toolbar cross button*/
    private void setToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}