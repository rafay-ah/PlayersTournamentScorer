package com.players.nest.FindMatchFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.ModelClasses.Games;
import com.players.nest.R;

import static com.players.nest.GameFragment.TopGamesAdapter.SELECTED_GAME_OBJECT;
import static com.players.nest.HelperClasses.Constants.OFFLINE;
import static com.players.nest.HelperClasses.Constants.ONLINE;

public class AlreadyCreatedMatchesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_created_matches);


        Intent intent = getIntent();
        Games gameObject = intent.getParcelableExtra(SELECTED_GAME_OBJECT);
        if (gameObject != null) {

            FindMatchesFragment findMatchesFragment = new FindMatchesFragment();
            findMatchesFragment.getType(gameObject, getString(R.string.SELECTED_GAME_MATCHES));
            getSupportFragmentManager().beginTransaction().replace(R.id.linearLayout17, findMatchesFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseHelperClass.changeStatus(ONLINE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelperClass.changeStatus(OFFLINE);
    }
}