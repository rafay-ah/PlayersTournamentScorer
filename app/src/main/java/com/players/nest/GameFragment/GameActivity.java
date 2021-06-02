package com.players.nest.GameFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.ModelClasses.Games;
import com.players.nest.R;

import java.util.Objects;

import static com.players.nest.GameFragment.TopGamesAdapter.SELECTED_GAME_OBJECT;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GAME_FRAG";

    FragmentCommunicator fragmentCommunicatorListener;
    FirebaseHelperClass firebaseHelperClass;

    Games game;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        toolbar = findViewById(R.id.toolbar9);
        viewPager = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout2);

        //Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());


        //Getting Game Object
        Intent intent = getIntent();
        if (intent != null) {
            game = intent.getParcelableExtra(SELECTED_GAME_OBJECT);
            assert game != null;
            getSupportActionBar().setTitle(game.getName());
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setUpViewPager();
    }


    private void setUpViewPager() {
        ViewPagerAdapter2 pagerAdapter2 = new ViewPagerAdapter2(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter2);
        tabLayout.setupWithViewPager(viewPager);
    }


    public void passData(FragmentCommunicator fragmentCommunicatorListener) {
        this.fragmentCommunicatorListener = fragmentCommunicatorListener;
        fragmentCommunicatorListener.getGameData(game);
    }


    //Interface for communication with the fragment
    public interface FragmentCommunicator {
        void getGameData(Games gameObj);
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