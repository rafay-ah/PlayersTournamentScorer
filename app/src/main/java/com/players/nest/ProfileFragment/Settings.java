package com.players.nest.ProfileFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.players.nest.R;
import com.players.nest.HelperClasses.Constants;

public class Settings extends AppCompatActivity {

    Toolbar toolbar;
    SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        switchCompat = findViewById(R.id.switch2);
        toolbar = findViewById(R.id.toolbar23);


        toolbar.setNavigationOnClickListener(v -> finish());


        //Checking if Dark Mode is already enabled or not.
        SharedPreferences preferences = getSharedPreferences(Constants.DARK_MODE, MODE_PRIVATE);
        boolean darkModeEnabled = preferences.getBoolean(Constants.DARK_MODE_ENABLED, false);

        if (darkModeEnabled)
            switchCompat.setChecked(true);


        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveToPreferences(true);
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveToPreferences(false);
                Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveToPreferences(boolean value) {
        SharedPreferences preferences = getSharedPreferences(Constants.DARK_MODE, MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(Constants.DARK_MODE_ENABLED, value);
        edit.apply();
    }
}