package com.players.nest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.AlertFragment.AlertFragment;
import com.players.nest.GameFragment.GameFragment;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.HomeFragment.HomeFragment;
import com.players.nest.ModelClasses.MatchDetail;
import com.players.nest.PostFragment.PostFragment;
import com.players.nest.ProfileFragment.ProfileFragment;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MAIN_ACTIVITY";
    public static final String HOME_TAG = "HOME_FRAGMENT_TAG";
    public static final String GAME_TAG = "GAME_FRAGMENT_TAG";
    public static final String POST_TAG = "POST_FRAGMENT_TAG";
    public static final String ALERT_TAG = "ALERT_FRAGMENT_TAG";
    public static final String PROFILE_TAG = "PROFILE_FRAGMENT_TAG";
    public static final String MAIN_ACTIVITY_FRAGMENT = "HOME_ACTIVITY_FRAGMENTS";

    Menu menu;
    FirebaseUser currentUser;
    BottomNavigationView bottomNavigationView;
    Bundle getStatus;
    SharedPreferences preferences;
    View notification_badge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getStatus = getIntent().getExtras();
        //Firebase Variables Instantiated
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Getting device Token
        FirebaseHelperClass.getDeviceToken();


        getType();
        ListenForRequests();

        menu = bottomNavigationView.getMenu();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {

            if (menu != null) {
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment instanceof GameFragment)
                    menu.findItem(R.id.game_icon).setChecked(true);
                else if (currentFragment instanceof AlertFragment)
                    menu.findItem(R.id.alert_icon).setChecked(true);
                else if (currentFragment instanceof PostFragment)
                    menu.findItem(R.id.post_icon).setChecked(true);
                else if (currentFragment instanceof ProfileFragment)
                    menu.findItem(R.id.profile_icon).setChecked(true);
                else if (currentFragment instanceof HomeFragment)
                    menu.findItem(R.id.home).setChecked(true);
            }
        });
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(R.id.alert_icon);
        notification_badge = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.alert_notification_badge,bottomNavigationView, false);
        notification_badge.setVisibility(View.GONE);
        itemView.addView(notification_badge);
        preferences = getSharedPreferences("AlertNotification",MODE_PRIVATE);
    }


    /**
     * Checking Which Activity Called the Main Activity, based on it suitable fragment will open.
     **/
    public void getType() {

        Intent intent = getIntent();
        String type = intent.getStringExtra(Constants.MAIN_ACTIVITY_PROFILE);
        String attachAlertFragment = intent.getStringExtra(Constants.OPEN_ALERT_FRAGMENT);

        if (type != null) {
            if (type.equals(Constants.FROM_SEARCH_FRAGMENT)) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentHolder, new ProfileFragment()).commit();
                bottomNavigationView.setSelectedItemId(R.id.profile_icon);
            }
        } else if (attachAlertFragment != null && attachAlertFragment.equals(Constants.OPEN_ALERT_FRAGMENT)) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentHolder, new AlertFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.alert_icon);
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentHolder, new GameFragment(), GAME_TAG).commit();
            bottomNavigationView.setSelectedItemId(R.id.game_icon);
        }

    }


    public Fragment getCurrentFragment() {
        List<Fragment> fragmentsList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentsList) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        int itemId = item.getItemId();

        if (itemId == R.id.home) {
            fragmentTransaction.replace(R.id.fragmentHolder, new HomeFragment(), HOME_TAG);
        } else if (itemId == R.id.game_icon) {
            fragmentTransaction.replace(R.id.fragmentHolder, new GameFragment(), GAME_TAG);
        } else if (itemId == R.id.post_icon) {
            fragmentTransaction.replace(R.id.fragmentHolder, new PostFragment(), POST_TAG);
        } else if (itemId == R.id.alert_icon) {
            notification_badge.setVisibility(View.GONE);
    //            if(notification_badge!= null){
    //                BottomNavigationItemView itemView = bottomNavigationView.findViewById(R.id.alert_icon);
    //                itemView.removeViewAt(2);
    //            }
            fragmentTransaction.replace(R.id.fragmentHolder, new AlertFragment(), ALERT_TAG);
        } else if (itemId == R.id.profile_icon) {
            fragmentTransaction.replace(R.id.fragmentHolder, new ProfileFragment(), PROFILE_TAG);
        }
        fragmentTransaction.addToBackStack(MAIN_ACTIVITY_FRAGMENT);
        fragmentTransaction.commit();
        return true;
    }


    @Override
    public void onBackPressed() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
        if (getStatus != null) {
            Log.d(TAG, "onBackPressed: " + getStatus.getString("Status") + " : " + homeFragment.isVisible());
        }
        if (getStatus != null) {
            if (getStatus.getString("Status").equals("Cancel") && homeFragment != null && homeFragment.isVisible()) {
                Alert("Match Canceled", "Your Match Has Canceled", "Close");
            }
        } else if (homeFragment != null && homeFragment.isVisible() && HomeFragment.fabExtended)
            homeFragment.closeFabMenu();
        else
            super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseHelperClass.changeStatus(Constants.ONLINE);
        FirebaseHelperClass.getDeviceToken();
    }

    private void ListenForRequests() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_MATCHES));
       FirebaseUser firebaseUser = currentUser;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size1 =0 ;
                int size2 =0 ;
                int size3 =0 ;
                int size4 =0 ;
                int size5 =0 ;
                int size6 =0 ;
                long maxTimeStamp1 = 0;
                long maxTimeStamp2=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MatchDetail matchDetail = dataSnapshot.getValue(MatchDetail.class);
                    assert matchDetail != null;
                    if (matchDetail.getHostUserId().equals(firebaseUser.getUid()) || matchDetail.getJoinedUserID().equals(firebaseUser.getUid())) {


                        String timeStamp = matchDetail.getTimeCreated();
                        String timeStamp1 = matchDetail.getMatchFinishedTime();
                        if(!timeStamp.isEmpty() ){
                            try {
                                long ts = Long.parseLong(timeStamp);
                                maxTimeStamp1 = Math.max(maxTimeStamp1, ts);
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }
                        }
                        if(!timeStamp1.isEmpty()){
                            try {
                                long ts = Long.parseLong(timeStamp1);
                                maxTimeStamp2 = Math.max(maxTimeStamp2,ts);
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }

                        }


                        if (matchDetail.getMatchStatus().equals(Constants.MATCH_CONNECTING)) {
                            if (!matchDetail.getJoinedUserID().equals("")) {
                                size1++;
                            }

                        }
                        else if(matchDetail.getMatchStatus().equals(Constants.MATCH_STARTED)){
                            if (!matchDetail.getJoinedUserID().equals("")) {
                                size2++;
                            }
                        }
                        else if(matchDetail.getMatchStatus().equals(Constants.SUBMITTING_RESULTS)){
                            if (!matchDetail.getJoinedUserID().equals("")) {
                                size3++;
                            }
                        }
                        else if(matchDetail.getMatchStatus().equals(Constants.MATCH_DISPUTE)){
                            if (!matchDetail.getJoinedUserID().equals("")) {
                                size4++;
                            }
                        }
                        else if (matchDetail.getMatchStatus().equals(Constants.MATCH_FINISHED)) {
                            size5++;
                        }
                        else if (matchDetail.getMatchStatus().equals(Constants.MATCH_INVITATION)
                                && matchDetail.getJoinedUserID().equals(firebaseUser.getUid())) {
                            size6++;
                        }
                    }
                }
                long prefTs;
                long prefTs1;

                prefTs = preferences.getLong("maxTs1",0);
                prefTs1 = preferences.getLong("maxTs2",0);
                int pref1 = preferences.getInt("size1",0);
                int pref2 = preferences.getInt("size2",0);
                int pref3 = preferences.getInt("size3",0);
                int pref4 = preferences.getInt("size4",0);
                int pref5 = preferences.getInt("size5",0);
                int pref6 = preferences.getInt("size6",0);
                Log.d(TAG, "outside: ");
                if( prefTs < maxTimeStamp1 ||  prefTs1 < maxTimeStamp2){
                    Log.d(TAG, "inside: ");
                    notification_badge.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong("maxTs1",maxTimeStamp1);
                    editor.putLong("maxTs2",maxTimeStamp2);
                    editor.apply();

                }
                else if(pref1 != size1 ||pref2 != size2 || pref3 != size3 || pref4 != size4
                        || pref5 != size5 || pref6 != size6){
                    notification_badge.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("size1",size1);
                    editor.putInt("size2",size2);
                    editor.putInt("size3",size3);
                    editor.putInt("size4",size4);
                    editor.putInt("size5",size5);
                    editor.putInt("size6",size6);
                    editor.apply();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        reference.addValueEventListener(valueEventListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
    }

    public void Alert(String title, String msg, String yes) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }
}