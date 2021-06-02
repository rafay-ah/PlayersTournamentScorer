package com.players.nest.FanFollowingActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.players.nest.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.ModelClasses.User;

import java.util.Objects;

public class FansFollowingActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    String fragmentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fansfollowinng);

        toolbar = findViewById(R.id.toolbar21);
        tabLayout = findViewById(R.id.tabLayout3);
        viewPager = findViewById(R.id.viewPager3);

        toolbar.setNavigationOnClickListener(v -> finish());


        Intent intent = getIntent();
        User user = intent.getParcelableExtra(Constants.USER_OBJECT);
        fragmentType = intent.getStringExtra(Constants.FANS_FRAGMENT);

        if (user != null)
            setViewPager(user);
        else
            getCurrentUserData();

    }

    public void getCurrentUserData() {

        FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null)
                            setViewPager(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void setViewPager(User user) {

        toolbar.setTitle(user.getUsername());

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), user);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (fragmentType != null) {
            if (!fragmentType.equals(Constants.OPEN_FANS_FRAGMENT))
                viewPager.setCurrentItem(1);
        }
    }
}