package com.players.nest.Chats;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
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
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.HelperMethods;
import com.players.nest.HelperClasses.UnSendMsgDialog;
import com.players.nest.ModelClasses.Chats;
import com.players.nest.ModelClasses.User;
import com.players.nest.ProfileFragment.ViewPostFragment;
import com.players.nest.R;

import java.util.ArrayList;
import java.util.Objects;

import static com.players.nest.MainActivity.MAIN_ACTIVITY_FRAGMENT;
import static com.players.nest.ProfileFragment.ProfileFragment.PARCEL_KEY;
import static com.players.nest.ProfileFragment.ViewProfileFragment.VIEW_PROFILE_TYPE;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SENDER_MESSAGED = 0;
    private static final int RECEIVER_MESSAGED = 1;
    private static final String TAG = "CHAT_FRAGMENT";

    Context context;
    ArrayList<Chats> chatList;
    DatabaseReference chatReference, currentUserRef;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public ChatsAdapter(Context context, ArrayList<Chats> chatList) {
        this.context = context;
        this.chatList = chatList;

        chatReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_CHATS));
        currentUserRef = FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_USERS));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (viewType == SENDER_MESSAGED) {
            View view = inflater.inflate(R.layout.sender_chat_layout, parent, false);
            return new senderViewHolderClass(view);
        } else {
            View view = inflater.inflate(R.layout.reciever_chat_layout, parent, false);
            return new receiverViewHolderClass(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == SENDER_MESSAGED) {
            ((senderViewHolderClass) holder).setData(position);
        } else
            ((receiverViewHolderClass) holder).setData(position);

    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }


    @Override
    public int getItemViewType(int position) {

        if (chatList.get(position).getSenderId().equals(firebaseUser.getUid()))
            return SENDER_MESSAGED;
        else
            return RECEIVER_MESSAGED;
    }


    /**
     * View Holder Classes
     */
    public class senderViewHolderClass extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ImageView proPic, postImg, normalImage;
        ConstraintLayout postLayout, mainImageOrPostLayout;
        TextView message, username, isSeenTxt, usernameAndCaption,
                dateTxt, timeTxt;

        public senderViewHolderClass(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.textView118);
            isSeenTxt = itemView.findViewById(R.id.textView123);
            dateTxt = itemView.findViewById(R.id.textView205);
            proPic = itemView.findViewById(R.id.imageView62);
            postImg = itemView.findViewById(R.id.imageView63);
            username = itemView.findViewById(R.id.textView192);
            timeTxt = itemView.findViewById(R.id.textView206);
            normalImage = itemView.findViewById(R.id.imageView65);
            usernameAndCaption = itemView.findViewById(R.id.textView193);
            postLayout = itemView.findViewById(R.id.constraintLayout19);
            mainImageOrPostLayout = itemView.findViewById(R.id.constraintLayout20);

            message.setOnLongClickListener(this);
            postLayout.setOnLongClickListener(this);
            normalImage.setOnLongClickListener(this);
            postLayout.setOnClickListener(v -> openViewPostFragment(getAdapterPosition()));
        }


        private void setData(int position) {

            //If Message Type is Text.
            switch (chatList.get(position).getType()) {
                case Constants.TEXT_MESSAGE_TYPE:
                    mainImageOrPostLayout.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(chatList.get(position).getMessage());

                    break;
                //If Message Type is User Post.
                case Constants.POST_MESSAGE_TYPE:
                    mainImageOrPostLayout.setVisibility(View.VISIBLE);
                    setPostLayout(proPic, postImg, normalImage, username, usernameAndCaption,
                            message, postLayout, position);
                    break;

                //If Message Type is Image.
                case Constants.IMAGE_MESSAGE_TYPE:
                    mainImageOrPostLayout.setVisibility(View.VISIBLE);
                    setImage(postLayout, normalImage, message, position);
                    break;
            }

            if (position == chatList.size() - 1) {
                if (chatList.get(position).isSeen()) {
                    isSeenTxt.setText("Seen");
                    isSeenTxt.setVisibility(View.VISIBLE);
                } else {
                    isSeenTxt.setText("Delivered");
                }
            } else {
                isSeenTxt.setVisibility(View.GONE);
            }

            setDateStamp(position, dateTxt);
            timeTxt.setText(HelperMethods.getTimeFromMillis(chatList.get(position).getTimeCreated()));
        }


        @Override
        public boolean onLongClick(View view) {

            if (view.getId() == R.id.imageView65) {
                //Show only Delete Dialog
                new UnSendMsgDialog(Constants.UNSENT_IMAGE,
                        new UnSendMsgDialog.unSendMsgListener() {
                            @Override
                            public void unSendMsg() {
                                FirebaseStorage.getInstance().getReferenceFromUrl(chatList.get(getAdapterPosition())
                                        .getImageUri()).delete()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful())
                                                chatReference.child(chatList.get(getAdapterPosition())
                                                        .getMessageId()).removeValue();
                                            else
                                                Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                        });
                            }

                            @Override
                            public void copyMessage() {
                            }
                        }).show(((FragmentActivity) (context)).getSupportFragmentManager(), "DELETE_IMAGE_DIALOG");
            } else {

                UnSendMsgDialog unSendMsgDialog = new UnSendMsgDialog(Constants.UNSENT_CHAT, new UnSendMsgDialog.unSendMsgListener() {
                    @Override
                    public void unSendMsg() {
                        chatReference.child(chatList.get(getAdapterPosition()).getMessageId()).removeValue();
                    }

                    @Override
                    public void copyMessage() {
                        copyTxt(getAdapterPosition());
                    }
                });

                unSendMsgDialog.show(((FragmentActivity) (context)).getSupportFragmentManager(), "DELETE_DIALOG");
            }
            return true;
        }
    }


    public class receiverViewHolderClass extends RecyclerView.ViewHolder implements View.OnLongClickListener,
            View.OnClickListener {

        ImageView proPic, postImg, normalImage;
        ConstraintLayout postLayout, mainImageOrPostLayout;
        TextView message, username, usernameAndCaption, dateTxt,
                timeTxt;

        public receiverViewHolderClass(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.textView118);
            proPic = itemView.findViewById(R.id.imageView62);
            postImg = itemView.findViewById(R.id.imageView63);
            dateTxt = itemView.findViewById(R.id.textView205);
            timeTxt = itemView.findViewById(R.id.textView206);
            username = itemView.findViewById(R.id.textView192);
            normalImage = itemView.findViewById(R.id.imageView65);
            postLayout = itemView.findViewById(R.id.constraintLayout19);
            usernameAndCaption = itemView.findViewById(R.id.textView193);
            mainImageOrPostLayout = itemView.findViewById(R.id.constraintLayout20);

            message.setOnLongClickListener(this);
            postLayout.setOnLongClickListener(this);
            postLayout.setOnClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {

            UnSendMsgDialog popUpDialog = new UnSendMsgDialog(Constants.COPY_MESSAGE, new UnSendMsgDialog.unSendMsgListener() {
                @Override
                public void unSendMsg() {
                }

                @Override
                public void copyMessage() {
                    copyTxt(getAdapterPosition());
                }
            });

            popUpDialog.show(((FragmentActivity) (context)).getSupportFragmentManager(), "DELETE_DIALOG");
            return true;
        }


        public void setData(int position) {

            //If Message Type is Text.
            if (chatList.get(position).getType().equals(Constants.TEXT_MESSAGE_TYPE)) {
                mainImageOrPostLayout.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(chatList.get(position).getMessage());

            }
            //If Message Type is User Post.
            else if (chatList.get(position).getType().equals(Constants.POST_MESSAGE_TYPE)) {
                mainImageOrPostLayout.setVisibility(View.VISIBLE);
                setPostLayout(proPic, postImg, normalImage, username, usernameAndCaption,
                        message, postLayout, position);
            }

            //If Message Type is Image.
            else if (chatList.get(position).getType().equals(Constants.IMAGE_MESSAGE_TYPE)) {
                mainImageOrPostLayout.setVisibility(View.VISIBLE);
                setImage(postLayout, normalImage, message, position);
            }

            setDateStamp(position, dateTxt);
            timeTxt.setText(HelperMethods.getTimeFromMillis(chatList.get(position).getTimeCreated()));
        }

        @Override
        public void onClick(View v) {
            openViewPostFragment(getAdapterPosition());
        }
    }


    //RecyclerView Methods
    public void setDateStamp(int position, TextView dateTxt) {
        if (position > 0) {

            String previousMsgTime = HelperMethods.getDateFormatFromMillis(chatList.get(position - 1).getTimeCreated());
            String currentMsgTime = HelperMethods.getDateFormatFromMillis(chatList.get(position).getTimeCreated());

            if (currentMsgTime.equals(previousMsgTime)) {
                dateTxt.setVisibility(View.GONE);
            } else {
                dateTxt.setVisibility(View.VISIBLE);
                dateTxt.setText(HelperMethods.getDateFormatFromMillis(chatList.get(position).getTimeCreated()));
            }
        } else {
            dateTxt.setText(HelperMethods.getDateFormatFromMillis(chatList.get(position).getTimeCreated()));
            dateTxt.setVisibility(View.VISIBLE);
        }
    }


    private void copyTxt(int adapterPosition) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied Message", chatList.get(adapterPosition).getMessage());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, "Message Copied", Toast.LENGTH_SHORT).show();
    }


    private void openViewPostFragment(int adapterPosition) {
        ViewPostFragment viewPostFragment = new ViewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TYPE", VIEW_PROFILE_TYPE);
        bundle.putString(Constants.FROM_CHAT_FRAGMENT, Constants.FROM_CHAT_FRAGMENT);
        bundle.putParcelable(PARCEL_KEY, chatList.get(adapterPosition).getUsersPost());
        viewPostFragment.setArguments(bundle);

        ((FragmentActivity) (context)).getSupportFragmentManager().beginTransaction().replace(R.id.main_chat_fragment_holder
                , viewPostFragment).addToBackStack(MAIN_ACTIVITY_FRAGMENT).commit();
    }


    private void setPostLayout(ImageView profilePic, ImageView post, ImageView normalImage, TextView username,
                               TextView usernameAndCaption, TextView message,
                               ConstraintLayout postLayout, int position) {

        postLayout.setVisibility(View.VISIBLE);
        normalImage.setVisibility(View.GONE);

        Glide.with(context).load(chatList.get(position).getUsersPost().getImageUri()).into(post);
        if (chatList.get(position).getMessage() != null) {
            if (!chatList.get(position).getMessage().equals(""))
                message.setText(chatList.get(position).getMessage());
            else
                message.setVisibility(View.GONE);
        }
        currentUserRef.child(chatList.get(position).getUsersPost().getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                username.setText(user.getUsername());
                                usernameAndCaption.setText(HelperMethods.usernameAndCaption(user.getUsername(),
                                        chatList.get(position).getUsersPost().getCaption()));

                                if (user.getProfilePic() != null) {
                                    if (!user.getProfilePic().equals(""))
                                        Glide.with(context).load(user.getProfilePic()).into(profilePic);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void setImage(ConstraintLayout postLayout, ImageView normalImage, TextView message, int position) {

        postLayout.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        normalImage.setVisibility(View.VISIBLE);

        if (chatList.get(position).getImageUri() != null) {
            if (!chatList.get(position).getImageUri().equals(""))
                Glide.with(context).load(chatList.get(position).getImageUri()).into(normalImage);
        }
    }
}
