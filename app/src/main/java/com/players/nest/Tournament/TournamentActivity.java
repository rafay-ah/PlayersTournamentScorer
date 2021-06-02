package com.players.nest.Tournament;

import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowInsets;
import android.view.WindowMetrics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.players.nest.Application.TournamentApplication;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.R;
import com.players.nest.Tournament.Fragment.BracketsFragment;

public class TournamentActivity extends AppCompatActivity {
    private BracketsFragment bracketFragment;
    private TournamentDetail tournamentDetail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);
        getDataFromActivity();
        initialiseBracketsFragment();


    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(this,TournamentStartedActivity.class);
//        intent.putExtra("TOURNAMENT_DETAILS",tournamentDetail);
//        startActivity(intent);
//        finish();
//    }

    private void getDataFromActivity() {
        Intent intent = getIntent();
        tournamentDetail = intent.getParcelableExtra("TOURNAMENT_DETAILS");
    }

    private void initialiseBracketsFragment(){
        bracketFragment = new BracketsFragment(tournamentDetail);
        FragmentManager manager =   getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container,bracketFragment,"brackets_home_fragment");
        transaction.commit();
        manager.executePendingTransactions();
    }
    private void setScreenSize(){
        int height;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            WindowMetrics windowMetrics =  getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.statusBars());
            height = windowMetrics.getBounds().height() - insets.top - insets.bottom;

        }
        else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
        }
        TournamentApplication.getInstance().setScreeHeight(height);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setScreenSize();
    }
}
