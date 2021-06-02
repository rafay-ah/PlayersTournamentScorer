package com.players.nest.Chats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.players.nest.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.ModelClasses.User;

import static com.players.nest.ProfileFragment.ProfileFragment.PARCEL_KEY;
import static com.players.nest.ProfileFragment.ViewProfileFragment.VIEW_PROFILE_TYPE;

public class MainChatActivity extends AppCompatActivity {

    Intent intent;
    MessagesFragment messagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        Log.d("MESSAGE_ACTIVITY", "onCreate: WHERE ARE YOU??");

        intent = getIntent();
        String TYPE = intent.getStringExtra(PARCEL_KEY);

        if (TYPE != null) {
            if (TYPE.equals(VIEW_PROFILE_TYPE)) {
                User user = intent.getParcelableExtra(Constants.USER_OBJECT);
                if (user != null)
                    InflateChatFragment(user);
                else
                    inflateChatFromNotification();
            }
        } else {
            InflateMessageFragment();
        }
    }


    //Because we are getting String as a User object from notification.
    private void inflateChatFromNotification() {

        JsonParser parser = new JsonParser();
        JsonElement mJson = parser.parse((String) intent.getExtras().get(Constants.USER_OBJECT));
        Gson gson = new Gson();
        User user = gson.fromJson(mJson, User.class);

        InflateChatFragment(user);
    }


    private void InflateMessageFragment() {
        messagesFragment = new MessagesFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_chat_fragment_holder, messagesFragment);
        fragmentTransaction.commit();
    }


    private void InflateChatFragment(User user) {

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setUserObject(user, null);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_chat_fragment_holder, chatFragment, Constants.CHAT_FRAGMENT);
        fragmentTransaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseHelperClass.changeStatus(Constants.ONLINE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (messagesFragment != null && messagesFragment.databaseReference != null
                && messagesFragment.valueEventListener != null) {
            messagesFragment.databaseReference.removeEventListener(messagesFragment.valueEventListener);
        }
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
    }
}