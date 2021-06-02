package com.players.nest.Login_Register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.exoplayer2.C;
import com.players.nest.ProfileFragment.PrivacyPolicy;
import com.players.nest.ProfileFragment.TermsOfService;
import com.players.nest.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.MainActivity;
import com.players.nest.ModelClasses.User;
import com.players.nest.Recaptcha.RecaptchaResponseViewModel;
import com.players.nest.Recaptcha.RecaptchaVerifyResponse;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.players.nest.Login_Register.LoginFragment.EMAIL_REGEX;

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    String SITE_KEY = "6LfZMaQaAAAAANk26XoIB4T-2qil4hTw3JUDrml7";
    String SECRET_KEY = "6LfZMaQaAAAAAFej67HlldFyUE-ABV97fnxs4QJt";

    Button registerBtn;
    LoadingDialog dialog;
    ConstraintLayout constraintLayout;
    EditText email, fullName, userName;
    TextInputEditText password;
    AppCompatCheckBox reCaptcha;
    //Firebase Variables
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private boolean isVerified;
    private boolean isUsernameUnique;
    CheckUniqueUsername checkUniqueUsername;
    TextView tvTos;
    TextView tvPrivacy;
    CheckBox cbTos;
    CheckBox cbPrivacy;
    boolean boolTos;
    boolean boolPrivacy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //Reference Created
        registerBtn = view.findViewById(R.id.button2);
        email = view.findViewById(R.id.editTextTextPersonName5);
        fullName = view.findViewById(R.id.editTextTextPersonName6);
        userName = view.findViewById(R.id.editTextTextPersonName3);
        password = view.findViewById(R.id.textInputEditText);
        constraintLayout = view.findViewById(R.id.constraintLayout2);
        reCaptcha = view.findViewById(R.id.cb_recaptcha);
        tvPrivacy = view.findViewById(R.id.tv_privacy);
        cbPrivacy = view.findViewById(R.id.cb_privacy);
        tvTos = view.findViewById(R.id.tv_tos);
        cbTos = view.findViewById(R.id.cb_tos);

        reCaptcha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !(isVerified)) {
                    reCaptcha.setChecked(false);
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
        registerBtn.setOnClickListener(view1 -> {
            if(boolTos) {
                if(boolPrivacy) {
                    if (isVerified) {
                        checkCredentials();
                    } else {
                        reCaptcha.setError("Invalid ReCaptcha");
                        Toast.makeText(getContext(), "Invalid ReCaptcha", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    cbPrivacy.setError("Accept Privacy Policy");
                    Toast.makeText(getContext(), "Accept Privacy Policy", Toast.LENGTH_LONG).show();
                }
            }
            else{
                cbTos.setError("Accept Terms and Service");
                Toast.makeText(getContext(), "Accept Terms and Service", Toast.LENGTH_LONG).show();
            }
        });

        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PrivacyPolicy.class);
                startActivity(intent);
            }
        });
        tvTos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TermsOfService.class);
                startActivity(intent);
            }
        });

        cbPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolPrivacy=isChecked;
            }
        });
        cbTos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolTos=isChecked;
            }
        });

        return view;
    }

    private void verifyTokenReq(String tokenResult) {
        RecaptchaResponseViewModel mViewModel = ViewModelProviders.of(RegisterFragment.this).get(RecaptchaResponseViewModel.class);
        mViewModel.getmRecaptchaObservable("https://www.google.com", tokenResult, SECRET_KEY).observe(RegisterFragment.this, new Observer<RecaptchaVerifyResponse>() {
            @Override
            public void onChanged(@Nullable RecaptchaVerifyResponse recaptchaVerifyResponse) {
                if (recaptchaVerifyResponse != null && recaptchaVerifyResponse.isSuccess()) {
                    isVerified = true;
                    reCaptcha.setChecked(true);
                } else {
                    reCaptcha.setError("Try Again");
                    //Toast.makeText(getContext(),"Try again",Toast.LENGTH_LONG).show();
                    Snackbar.make(constraintLayout, "Try again", Snackbar.LENGTH_LONG).show();
                    Log.e(TAG, "onChanged: " + Objects.requireNonNull(recaptchaVerifyResponse).getErrorCodes());
                }
            }
        });

    }

    private void isUserNameExists() {
        dialog = new LoadingDialog(getActivity());
        dialog.startDialog();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS));

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isUsernameUnique = true;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String searchUsername = String.valueOf(dataSnapshot.child("username").getValue());
                    if (searchUsername.equals(userName.getText().toString())) {
                        dialog.dismissDialog();
                        isUsernameUnique = false;
                        break;
                    }
                }
                checkUniqueUsername.isUnique(isUsernameUnique);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkCredentials() {

        String emailId = email.getText().toString();
        String pass = String.valueOf(password.getText());
        String user_name = String.valueOf(userName.getText());
        String full_name = String.valueOf(fullName.getText());

        if (!emailId.matches(EMAIL_REGEX)) {
            Snackbar.make(constraintLayout, "Please enter correct Email Address.", Snackbar.LENGTH_LONG).show();
        } else if (pass.isEmpty()) {
            Snackbar.make(constraintLayout, "Please enter your password.", Snackbar.LENGTH_LONG).show();
        } else if (full_name.isEmpty())
            Snackbar.make(constraintLayout, "Please enter your name.", Snackbar.LENGTH_LONG).show();
        else if (user_name.isEmpty())
            Snackbar.make(constraintLayout, "Please enter a username.", Snackbar.LENGTH_LONG).show();
        else {

            Pattern pattern = Pattern.compile("^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{0,29}$");
            Matcher matcher = pattern.matcher(userName.getText());
            checkUniqueUsername = new CheckUniqueUsername() {
                @Override
                public void isUnique(boolean unique) {
                    if (unique) {
                        RegisterUser(emailId, pass, user_name, full_name);
                    } else {
                        registerBtn.setEnabled(true);
                        userName.setError("Username already taken");
                        //Toast.makeText(getContext(),"Username already taken",Toast.LENGTH_LONG).show();
                        Snackbar.make(constraintLayout, "Username already taken", Snackbar.LENGTH_LONG).show();
                    }
                }
            };
            if (matcher.matches()) {
                registerBtn.setEnabled(false);
                isUserNameExists();
            } else {
                Snackbar.make(constraintLayout, "Username cannot contains special character or space except '.' and '_'", BaseTransientBottomBar.LENGTH_LONG).show();
                userName.setError("Username not allowed");

            }
        }
    }


    public void RegisterUser(final String emailId, String pass, final String user_name, final String full_name) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailId, pass)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        getDeviceToken(emailId, user_name, full_name);
                    } else {
                        registerBtn.setEnabled(true);
                        Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismissDialog();
                    }
                });
    }


    private void getDeviceToken(final String emailId, final String user_name, final String full_name) {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    addNewUserToDatabase(emailId, user_name, full_name, task.getResult());
                } else {
                    registerBtn.setEnabled(true);
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismissDialog();
                }
            }
        });
    }


    private void addNewUserToDatabase(String emailId, String user_name, String full_name, String deviceToken) {

        final String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS)).child(userID);

        User user = new User(userID, 0, emailId, full_name, user_name, "", "",
                "online", deviceToken, 40, 0);
        databaseReference.setValue(user)
                .addOnCompleteListener((Activity) Objects.requireNonNull(getContext()), task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getContext(), MainActivity.class));
                        Objects.requireNonNull(getActivity()).finish();
                    } else {
                        registerBtn.setEnabled(true);
                        Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismissDialog();
                });
    }

}
