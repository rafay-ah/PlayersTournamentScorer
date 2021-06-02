package com.players.nest.Login_Register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.players.nest.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.MainActivity;
import com.players.nest.Recaptcha.RecaptchaResponseViewModel;
import com.players.nest.Recaptcha.RecaptchaVerifyResponse;

import java.util.HashMap;
import java.util.Objects;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    String SITE_KEY = "6LfZMaQaAAAAANk26XoIB4T-2qil4hTw3JUDrml7";
    String SECRET_KEY = "6LfZMaQaAAAAAFej67HlldFyUE-ABV97fnxs4QJt";
    String BASE_URL = "https://www.google.com/";
    public static final String LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

    Button loginBtn;
    LoadingDialog dialog;
    TextView registerTxt, forgetPass;
    EditText emailId, password;
    ConstraintLayout constraintLayout;
    AppCompatCheckBox reCaptcha;

    //Firebase Variables
    FirebaseAuth mAuth;
    private boolean isVerified=true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        registerTxt = view.findViewById(R.id.textView14);
        emailId = view.findViewById(R.id.editTextTextPersonName5);
        password = view.findViewById(R.id.editTextTextPersonName6);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        loginBtn = view.findViewById(R.id.button);
        forgetPass = view.findViewById(R.id.textView13);
        reCaptcha = view.findViewById(R.id.cb_recaptcha);
        reCaptcha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !(isVerified)) {
                    reCaptcha.setChecked(false);
                    reCaptcha.setClickable(false);
                    SafetyNet.getClient(Objects.requireNonNull(getActivity())).verifyWithRecaptcha(SITE_KEY)
                            .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                                @Override
                                public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                    if (!response.getTokenResult().isEmpty()) {
                                        verifyTokenReq(response.getTokenResult());
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            reCaptcha.setClickable(true);
                            if (e instanceof ApiException) {
                                ApiException apiException = (ApiException) e;
                                Toast.makeText(getContext(), "Api Exception", Toast.LENGTH_LONG).show();
                                Log.e(TAG, "onFailure: " + apiException);
                            } else {
                                Log.e(TAG, "onFailure: " + e);
                            }
                        }
                    });
                }
            }
        });

        //onClickListeners
        loginBtn.setOnClickListener(view1 -> {
            if (isVerified) {
                checkCredentials();
            } else {
                reCaptcha.setError("Invalid ReCaptcha");
                Toast.makeText(getContext(), "Invalid ReCaptcha", Toast.LENGTH_LONG).show();
            }
        });

        forgetPass.setOnClickListener(view12 -> {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.loginFragmentHolder, new ForgetPasswordFragment())
                    .addToBackStack(LOGIN_FRAGMENT_TAG).commit();
        });

        registerTxt.setOnClickListener(view13 -> {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.loginFragmentHolder, new RegisterFragment())
                    .addToBackStack(LOGIN_FRAGMENT_TAG).commit();
        });

        return view;
    }

    private void verifyTokenReq(String tokenResult) {
        RecaptchaResponseViewModel mViewModel = ViewModelProviders.of(LoginFragment.this).get(RecaptchaResponseViewModel.class);
        mViewModel.getmRecaptchaObservable("https://www.google.com", tokenResult, SECRET_KEY).observe(LoginFragment.this, new Observer<RecaptchaVerifyResponse>() {
            @Override
            public void onChanged(@Nullable RecaptchaVerifyResponse recaptchaVerifyResponse) {
                if (recaptchaVerifyResponse != null && recaptchaVerifyResponse.isSuccess()) {
                    isVerified = true;
                    reCaptcha.setChecked(true);
                } else {
                    reCaptcha.setClickable(true);
                    reCaptcha.setError("Try Again");
                    Toast.makeText(getContext(), "Try again", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onChanged: " + Objects.requireNonNull(recaptchaVerifyResponse).getErrorCodes());
                }
            }
        });

    }

    private void checkCredentials() {

        String email = emailId.getText().toString();
        String pass = password.getText().toString();

        if (!email.matches(EMAIL_REGEX)) {
            Snackbar.make(constraintLayout, "Please enter correct Email Address.", Snackbar.LENGTH_LONG).show();
            emailId.setBackgroundTintList(AppCompatResources.getColorStateList(Objects.requireNonNull(getContext()), R.color.WarningColor));
            password.setBackgroundTintList(AppCompatResources.getColorStateList(getContext(), R.color.DisabledText));
        } else if (pass.isEmpty()) {
            Snackbar.make(constraintLayout, "Please enter your password.", Snackbar.LENGTH_LONG).show();
            password.setBackgroundTintList(AppCompatResources.getColorStateList(Objects.requireNonNull(getContext()), R.color.WarningColor));
            emailId.setBackgroundTintList(AppCompatResources.getColorStateList(getContext(), R.color.DisabledText));
        } else {
            signInUser(email, pass);
        }
    }

    private void addDeviceToken() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> deviceToken = new HashMap<>();
                deviceToken.put("deviceToken", task.getResult());
                FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(deviceToken);
                startActivity(new Intent(getActivity(), MainActivity.class));
                Objects.requireNonNull(getActivity()).finish();
            } else
                Toast.makeText(getActivity(), task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
        });
    }

    private void signInUser(String email, String pass) {

        dialog = new LoadingDialog(getActivity());
        dialog.startDialog();

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addDeviceToken();
            } else {
                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                loginBtn.setEnabled(true);
                dialog.dismissDialog();
            }
        });
    }
}

