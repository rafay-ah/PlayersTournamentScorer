package com.players.nest.Login_Register;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.players.nest.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.Recaptcha.RecaptchaResponseViewModel;
import com.players.nest.Recaptcha.RecaptchaVerifyResponse;

import java.util.Objects;

import static com.players.nest.Login_Register.LoginFragment.EMAIL_REGEX;

public class ForgetPasswordFragment extends Fragment {
    private static final String TAG = "ForgetPasswordFragment";
    String SITE_KEY = "6LfZMaQaAAAAANk26XoIB4T-2qil4hTw3JUDrml7";
    String SECRET_KEY = "6LfZMaQaAAAAAFej67HlldFyUE-ABV97fnxs4QJt";
    Button resetPass;
    boolean flag = false;
    TextView emailLinkSend;
    TextInputEditText emailId;
    TextInputLayout emailLayout;
    AppCompatCheckBox reCaptcha;

    private boolean isVerified;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_pass, container, false);

        resetPass = view.findViewById(R.id.button4);
        emailLinkSend = view.findViewById(R.id.textView22);
        emailId = view.findViewById(R.id.textInputEditText2);
        emailLayout = view.findViewById(R.id.textInputLayout);
        reCaptcha = view.findViewById(R.id.cb_recaptcha);
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

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailLayout.setError(null);
                String email = String.valueOf(emailId.getText());
                if (email.matches(EMAIL_REGEX) && !flag) {
                    checkIfEmailExists(email);
                    InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getActivity())
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(emailId.getWindowToken(), 0);
                } else
                    emailLayout.setError("Please enter a valid email address.");
            }
        });

        return view;
    }

    private void checkIfEmailExists(final String email) {
        final LoadingDialog dialog = new LoadingDialog(getActivity());
        dialog.startDialog();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_USERS));

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String searchEmail = String.valueOf(dataSnapshot.child("email").getValue());
                    if (searchEmail.equals(email)) {
                        dialog.dismissDialog();
                        flag = true;
                        resetPassword(email);
                    }
                }
                if (!flag) {
                    dialog.dismissDialog();
                    emailLayout.setError("Email address does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyTokenReq(String tokenResult) {
        RecaptchaResponseViewModel mViewModel = ViewModelProviders.of(ForgetPasswordFragment.this).get(RecaptchaResponseViewModel.class);
        mViewModel.getmRecaptchaObservable("https://www.google.com", tokenResult, SECRET_KEY).observe(ForgetPasswordFragment.this, new Observer<RecaptchaVerifyResponse>() {
            @Override
            public void onChanged(@Nullable RecaptchaVerifyResponse recaptchaVerifyResponse) {
                if (recaptchaVerifyResponse != null && recaptchaVerifyResponse.isSuccess()) {
                    isVerified = true;
                    reCaptcha.setChecked(true);
                } else {
                    reCaptcha.setError("Try Again");
                    Toast.makeText(getContext(), "Try again", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onChanged: " + Objects.requireNonNull(recaptchaVerifyResponse).getErrorCodes());
                }
            }
        });

    }

    private void resetPassword(String email) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    emailLinkSend.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
