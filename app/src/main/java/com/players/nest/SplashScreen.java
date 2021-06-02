package com.players.nest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.Login_Register.LoginRegisterActivity;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SPLASH_SCREEN";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        checkDarkModeEnabled();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(SplashScreen.this, LoginRegisterActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }
            finish();
        }, 2500);
    }

    private void checkDarkModeEnabled() {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.DARK_MODE, MODE_PRIVATE);
        boolean darkModeEnabled = sharedPreferences.getBoolean(Constants.DARK_MODE_ENABLED, false);

        if (darkModeEnabled) {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
