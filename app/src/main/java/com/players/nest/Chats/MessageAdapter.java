package com.players.nest.Chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.ModelClasses.Chats;
import com.players.nest.ModelClasses.Chats_MessageAdapt;
import com.players.nest.ModelClasses.User;
import com.players.nest.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.myViewHolderClass> {

    private static final String TAG = "MESSAGE_ADAPTER";

    Context context;
    FirebaseUser firebaseUser;
    DatabaseReference userRef;
    ArrayList<Chats_MessageAdapt> chatList;
    ArrayList<Chats_MessageAdapt> mAllChatList;

    public MessageAdapter(Context context, ArrayList<Chats_MessageAdapt> chatList) {
        this.context = context;
        this.chatList = chatList;
        mAllChatList = chatList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_USERS));
    }


    @NonNull
    @Override
    public myViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.all_messages_recycler_view, parent, false);
        return new myViewHolderClass(view);
    }


    @Override
    public void onBindViewHolder(@NonNull myViewHolderClass holder, int position) {

        Chats chat = chatList.get(position).getChats();

        switch (chat.getType()) {
            case Constants.TEXT_MESSAGE_TYPE:
                holder.lastMsg.setText(chat.getMessage());
                break;
            case Constants.POST_MESSAGE_TYPE:
                if (!chat.getSenderId().equals(firebaseUser.getUid()))
                    holder.lastMsg.setText("Sent you a Post");
                else
                    holder.lastMsg.setText("You sent a Post");
                break;
            case Constants.IMAGE_MESSAGE_TYPE:
                if (!chat.getSenderId().equals(firebaseUser.getUid()))
                    holder.lastMsg.setText("Sent you an Image");
                else
                    holder.lastMsg.setText("You sent an Image");
                break;
        }

        if (!chat.getSenderId().equals(firebaseUser.getUid()) && !chat.isSeen()) {
            holder.lastMsg.setTextColor(context.getColor(R.color.colorAccent));
            holder.unReadMsg.setVisibility(View.VISIBLE);
        }


        if (chat.getSenderId().equals(firebaseUser.getUid()))
            getUsers(chat.getReceiverId(), holder.name, holder.profilePic, position);
        else
            getUsers(chat.getSenderId(), holder.name, holder.profilePic, position);
    }


    public void getUsers(String userID, TextView name, ImageView profilePic, int position) {

        userRef.child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            chatList.get(position).setUser(user);
                            name.setText(user.getFullName());

                            if (!user.getProfilePic().equals(""))
                                Glide.with(context).load(user.getProfilePic()).into(profilePic);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public void updateList(String search) {

        ArrayList<Chats_MessageAdapt> mList = new ArrayList<>();
        if (search.equals("")) {
            mList.addAll(mAllChatList);
        } else {
            String str = search.toLowerCase().trim();
            for (Chats_MessageAdapt ob1 : mAllChatList) {
                User user = ob1.getUser();
                if (user != null) {
                    if (user.getFullName().toLowerCase().matches(".*\\b" + str.toLowerCase() + "\\b.*")
                            || user.getUsername().toLowerCase().matches(".*\\b" + str.toLowerCase() + "\\b.*")) {
                        if (!mList.contains(ob1)) {
                            mList.add(ob1);
                        }
                    }
                }
            }
        }
        chatList = mList;
        notifyDataSetChanged();

    }


    public class myViewHolderClass extends RecyclerView.ViewHolder {

        TextView name, lastMsg;
        ImageView profilePic, unReadMsg;

        public myViewHolderClass(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView119);
            lastMsg = itemView.findViewById(R.id.textView158);
            profilePic = itemView.findViewById(R.id.imageView39);
            unReadMsg = itemView.findViewById(R.id.imageView74);

            itemView.setOnClickListener(view -> {

                User user = chatList.get(getAdapterPosition()).getUser();
                if (user != null) {
                    ChatFragment chatFragment = new ChatFragment();
                    chatFragment.setUserObject(user, null);
                    ((AppCompatActivity) (context)).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_chat_fragment_holder, chatFragment)
                            .addToBackStack(Constants.MAIN_CHAT_ACTIVITY_FRAGMENTS)
                            .commit();
                }
            });
        }
    }
}
