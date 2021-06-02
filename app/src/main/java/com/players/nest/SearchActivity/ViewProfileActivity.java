package com.players.nest.SearchActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.players.nest.R;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;
import com.players.nest.ModelClasses.User;
import com.players.nest.ProfileFragment.ViewProfileFragment;

import static com.players.nest.SearchActivity.SearchPeopleFragment.VIEW_PROFILE_PARCEL;

public class ViewProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_activty);

        Intent intent = getIntent();
        if (intent != null) {
            User userObj = intent.getParcelableExtra(Constants.USER_OBJECT);
            if (userObj != null) {
                ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(VIEW_PROFILE_PARCEL, userObj);
                viewProfileFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.view_profile_fragment_holder, viewProfileFragment).commit();
            }
        }
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