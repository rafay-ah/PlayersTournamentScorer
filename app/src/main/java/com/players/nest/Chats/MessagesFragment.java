package com.players.nest.Chats;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.ModelClasses.Chats;
import com.players.nest.ModelClasses.Chats_MessageAdapt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MessagesFragment extends Fragment {

    private static final String TAG = "MESSAGE_FRAGMENT";

    Toolbar toolbar;
    EditText searchBar;
    TextView messageTxt;
    ImageView clearIcon;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    FirebaseUser firebaseUser;
    MessageAdapter messageAdapter;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    ArrayList<Chats> chatList = new ArrayList<>();
    ArrayList<Chats_MessageAdapt> filterChatList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        toolbar = view.findViewById(R.id.toolbar13);
        searchBar = view.findViewById(R.id.editText2);
        clearIcon = view.findViewById(R.id.imageView75);
        messageTxt = view.findViewById(R.id.textView159);
        progressBar = view.findViewById(R.id.progressBar11);
        recyclerView = view.findViewById(R.id.recycler_view10);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        toolbar.setNavigationIcon(R.drawable.ic_baseline_black_arrow_back);
        toolbar.setNavigationOnClickListener(view1 -> Objects.requireNonNull(getActivity()).onBackPressed());

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    messageAdapter.updateList("");
                    clearIcon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    messageAdapter.updateList(editable.toString());
                    clearIcon.setVisibility(View.VISIBLE);
                }
            }
        });
        clearIcon.setOnClickListener(v -> searchBar.setText(""));

        setAdapter();
        getData();
        return view;
    }


    private void setAdapter() {
        messageAdapter = new MessageAdapter(Objects.requireNonNull(getContext()), filterChatList);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);
    }


    private void getData() {

        progressBar.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_CHATS));
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                filterChatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if (chats != null) {
                        if (chats.getReceiverId().equals(firebaseUser.getUid())
                                || chats.getSenderId().equals(firebaseUser.getUid())) {
                            chatList.add(chats);
                        }
                    }
                }
                if (chatList.size() == 0) {
                    messageTxt.setText("No Message Found.");
                } else {
                    messageTxt.setText("Messages");
                    recyclerView.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
                sortChatList();
                Collections.sort(filterChatList, (o1, o2) -> Long.compare(o2.getChats().getTimeCreated()
                        , o1.getChats().getTimeCreated()));
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }


    private void sortChatList() {

        List<String> ids = new ArrayList<>();

        for (int i = 0; i < chatList.size(); i++) {
            Chats chat = chatList.get(i);
            if (chat.getReceiverId().equals(firebaseUser.getUid())) {
                if (!ids.contains(chat.getSenderId())) {
                    String senderId = chat.getSenderId();
                    ids.add(senderId);
                    findLongestTime(senderId);
                }
            } else if (!ids.contains(chat.getReceiverId())) {
                String receiverId = chat.getReceiverId();
                ids.add(receiverId);
                findLongestTime(receiverId);
            }
        }
    }


    private void findLongestTime(String senderId) {
        long max = 0;
        int position = 0;

        for (int i = 0; i < chatList.size(); i++) {
            Chats chat = chatList.get(i);
            if (chat.getSenderId().equals(senderId) ||
                    chat.getReceiverId().equals(senderId)) {
                if (chat.getTimeCreated() > max) {
                    position = i;
                    max = chat.getTimeCreated();
                }
            }
        }
        Chats_MessageAdapt ob1 = new Chats_MessageAdapt(chatList.get(position), null);
        filterChatList.add(ob1);
    }
}
