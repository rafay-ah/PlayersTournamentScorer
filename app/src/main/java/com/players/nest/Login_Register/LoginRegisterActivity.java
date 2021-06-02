package com.players.nest.Login_Register;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.players.nest.R;
public class LoginRegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));

        getSupportFragmentManager().beginTransaction().add(R.id.loginFragmentHolder, new LoginFragment()).commit();
    }
}