package com.players.nest.Chats;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.players.nest.HelperClasses.NotificationHelper;
import com.players.nest.HelperClasses.Permissions;
import com.players.nest.ModelClasses.Chats;
import com.players.nest.ModelClasses.User;
import com.players.nest.R;
import com.players.nest.SearchActivity.ViewProfileActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CHAT_FRAGMENT";

    CardView sendMsg;
    EditText msgEditText;
    ProgressBar progressBar;
    Activity attachedActivity;
    RecyclerView recyclerView;
    ImageView profilePic, backArrow, attachFile;
    TextView userFullName, lastSeen;

    String type;
    User user, currentUser;
    ChatsAdapter chatsAdapter;
    FirebaseUser firebaseUser;
    LoadingDialog loadingDialog;
    DatabaseReference statusRef;
    DatabaseReference databaseReference, readMsgRef;
    ArrayList<Chats> chatsArrayList = new ArrayList<>();
    ValueEventListener statusRealTimeListener;
    ValueEventListener settingIsSeenTrue;
    ValueEventListener chatEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        profilePic = view.findViewById(R.id.imageView38);
        userFullName = view.findViewById(R.id.textView116);
        lastSeen = view.findViewById(R.id.textView117);
        sendMsg = view.findViewById(R.id.cardView17);
        recyclerView = view.findViewById(R.id.recycler_view11);
        progressBar = view.findViewById(R.id.progressBar5);
        backArrow = view.findViewById(R.id.imageView37);
        attachFile = view.findViewById(R.id.imageView64);
        msgEditText = view.findViewById(R.id.editTextTextPersonName8);


        attachedActivity = getActivity();
        loadingDialog = new LoadingDialog(attachedActivity);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_CHATS));


        //Adding TextWatcher to make the attach Icon Invisible if user enters some text.
        msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                    attachFile.setVisibility(View.VISIBLE);
                else
                    attachFile.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //OnClickListeners
        backArrow.setOnClickListener(this);
        sendMsg.setOnClickListener(this);
        attachFile.setOnClickListener(this);
        userFullName.setOnClickListener(this);
        profilePic.setOnClickListener(this);


        if (type != null) {
            backArrow.setVisibility(View.GONE);
        }

        setAdapter();
        chatListener();
        MsgSeenByUser();
        getCurrentUserData();
        setWidgets();

        return view;
    }


    private void getCurrentUserData() {
        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentUser = snapshot.getValue(User.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    void MsgSeenByUser() {

        settingIsSeenTrue = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    assert chat != null;
                    if (user != null && chat.getReceiverId().equals(firebaseUser.getUid()) && chat.getSenderId().equals(user.getUser_id())) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        databaseReference.addValueEventListener(settingIsSeenTrue);
    }


    private void setAdapter() {

        chatsAdapter = new ChatsAdapter(Objects.requireNonNull(getContext()), chatsArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatsAdapter);
    }


    private void chatListener() {

        progressBar.setVisibility(View.VISIBLE);

        readMsgRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_CHATS));

        chatEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    assert chat != null;
                    if (user != null && chat.getSenderId().equals(firebaseUser.getUid()) && chat.getReceiverId().equals(user.getUser_id())
                            || user != null && chat.getSenderId().equals(user.getUser_id()) && chat.getReceiverId().equals(firebaseUser.getUid()))
                        chatsArrayList.add(chat);
                }
                Log.d(TAG, "onDataChange: chat Listener called");
                chatsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.scrollToPosition(chatsArrayList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(attachedActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        };

        readMsgRef.addValueEventListener(chatEventListener);
    }


    @SuppressLint("SetTextI18n")
    private void setWidgets() {
        if (user != null) {

            if (!user.getProfilePic().equals(""))
                Glide.with(Objects.requireNonNull(getActivity())).load(user.getProfilePic()).into(profilePic);


            statusRealTimeListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user1 = snapshot.getValue(User.class);
                    assert user1 != null;
                    if (user1.getStatus().equals(Constants.ONLINE)) {
                        lastSeen.setText("Active now");
                    } else {
                        lastSeen.setText("Active " + DateUtils.getRelativeTimeSpanString(user1.getLastActiveTime(),
                                System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };

            statusRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                    .child(user.getUser_id());

            statusRef.addValueEventListener(statusRealTimeListener);

            userFullName.setText(user.getFullName());
        }
    }


    public void setUserObject(User user, String type) {
        this.user = user;
        this.type = type;
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.imageView37) {
            assert attachedActivity != null;
            attachedActivity.onBackPressed();
        } else if (id == R.id.cardView17) {
            String msg = msgEditText.getText().toString();
            if (!msg.equals("")) {
                msgEditText.setText("");
                addMsgToDatabase(msg);
            }
        } else if (id == R.id.imageView64)
            openChooserDialog();
        else if (id == R.id.imageView38 || id == R.id.textView116) {

            if (user != null) {
                Intent intent = new Intent(getContext(), ViewProfileActivity.class);
                intent.putExtra(Constants.USER_OBJECT, user);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(attachedActivity, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }
    }


    private void openChooserDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
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
        if (Permissions.isPermissionGrantedByApp(getContext())) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
        } else
            requestPermissions(Permissions.PERMISSIONS, Permissions.PERMISSION_REQUEST_CODE);
    }


    public void openCamera() {
        if (Permissions.isPermissionGrantedByApp(getContext())) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE);
        } else
            requestPermissions(Permissions.PERMISSIONS, Permissions.PERMISSION_REQUEST_CODE);
    }


    private void addMsgToDatabase(final String msg) {

        String messageId = databaseReference.push().getKey();
        if(user != null) {
            final Chats chat = new Chats(firebaseUser.getUid(), user.getUser_id(), msg, messageId,
                    Constants.TEXT_MESSAGE_TYPE, null, "", false, System.currentTimeMillis());

            assert messageId != null;
            databaseReference.child(messageId).setValue(chat).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(attachedActivity, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    new NotificationHelper().sendNotificationsForChatFragment(getContext(),
                            currentUser, user, msg);
//                sendNotificationsToOtherUser(msg);
                }
            });
        }
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
                    Toast.makeText(getContext(), "Please Grant Permissions to proceed.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Please Grant Permissions", Toast.LENGTH_LONG).show();
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        readMsgRef.addValueEventListener(chatEventListener);
        databaseReference.addValueEventListener(settingIsSeenTrue);
        statusRef.addValueEventListener(statusRealTimeListener);

        if (data != null) {
            if (requestCode == Constants.CAMERA_REQUEST_CODE) {
                if (data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    String image = MediaStore.Images.Media.insertImage(Objects.requireNonNull(getContext()).getContentResolver(),
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
                                Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                        });
                    } else {
                        Toast.makeText(attachedActivity, Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                });
    }


    private void addImageMsgToDatabase(Uri result) {
        if(user != null) {
            String messageId = databaseReference.push().getKey();
            Chats chat = new Chats(firebaseUser.getUid(), user.getUser_id(), "", messageId, Constants.IMAGE_MESSAGE_TYPE
                    , null, result.toString(), false, System.currentTimeMillis());

            assert messageId != null;
            databaseReference.child(messageId).setValue(chat).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(attachedActivity, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog.dismissDialog();
                    //Notify Other User..
                    new NotificationHelper().sendNotificationsForChatFragment(getContext(),
                            currentUser, user, "Sent you a post.");
                }
            });
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: called");
        //Because when the app was opened using notification and then close, the listeners where still attached to it.
        try {
            databaseReference.removeEventListener(settingIsSeenTrue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        try {
        statusRef.removeEventListener(statusRealTimeListener);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        try {
        readMsgRef.removeEventListener(chatEventListener);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
