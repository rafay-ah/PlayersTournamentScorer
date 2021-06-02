package com.players.nest.ProfileFragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.players.nest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalService;
import com.players.nest.DataModel;
import com.players.nest.HelperClasses.LoadingDialog;
import com.players.nest.HelperClasses.WithdrawAlertDialog;
import com.players.nest.ModelClasses.PreviousTransactionsRecord;
import com.players.nest.ModelClasses.User;
import com.players.nest.NotificationAPI;
import com.players.nest.NotificationClient;
import com.players.nest.NotificationModel;
import com.players.nest.RootModel;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Callback;


public class WithdrawDepositActivity extends AppCompatActivity {
    private static final String TAG = "WithdrawDepositActivity";
    private static final int PAYPAL_REQUEST_CODE = 101;
    private static final String PAYPAL_EMAIL_ADDRESS = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$";


    Toolbar toolbar;
    TextView txt;
    LinearLayout withdrawLayout;
    EditText amt, paypalAddress, nameGovt;
    Button button;
    FirebaseUser firebaseUser;
    JSONObject transactionID;
    DatabaseReference databaseReference;
    DatabaseReference updateReference;
    DatabaseReference transactionRef;
    WithdrawLimit withdrawLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_deposit);

        txt = findViewById(R.id.textView7);
        toolbar = findViewById(R.id.toolbar3);
        withdrawLayout = findViewById(R.id.linearLayout3);
        amt = findViewById(R.id.editTextTextPersonName4);
        paypalAddress = findViewById(R.id.editTextTextPersonName7);
        nameGovt = findViewById(R.id.editTextTextPersonName2);

        button = findViewById(R.id.button5);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("account_balance");


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String TYPE = "TYPE_DEPOSIT_WITHDRAW";
        String type = intent.getStringExtra(TYPE);
//        sendNotificationToUser("Admin");
//        sendNotificationToUser("topics/Admin");
//        sendNotificationToUser("/topics/Admin");
        assert type != null;
        if (type.equals("Deposit")) {
            toolbar.setTitle("Deposit Money");
            txt.setText(R.string.DEPOSIT_STRING);
            button.setText(R.string.DEPOSIT);
            withdrawLayout.setVisibility(View.GONE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!amt.getText().toString().isEmpty()) {
                        Log.d(TAG, "onClick: " + amt.getText());

                        Intent webIntent = new Intent(WithdrawDepositActivity.this, DepositWithdrawWebViewActivity.class);

                        webIntent.putExtra("userID", firebaseUser.getUid());
                        webIntent.putExtra("amount", amt.getText().toString());
                        startActivity(webIntent);

                    } else
                        Toast.makeText(WithdrawDepositActivity.this, "Please Enter amount more than $1", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            withdrawLayout.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //send notification to admin that user want to withdraw some money.
                    if (amt.getText().toString().isEmpty()) {
                        Toast.makeText(WithdrawDepositActivity.this, "Please Enter amount more than $1.",
                                Toast.LENGTH_SHORT).show();
                    } else if (paypalAddress.getText().toString().isEmpty() ||
                            paypalAddress.getText().toString().matches(PAYPAL_EMAIL_ADDRESS)) {
                        Toast.makeText(WithdrawDepositActivity.this, "Please enter a Valid email Address.",
                                Toast.LENGTH_SHORT).show();
                    } else if (nameGovt.getText().toString().isEmpty())
                        Toast.makeText(WithdrawDepositActivity.this, "Please enter your Name.",
                                Toast.LENGTH_SHORT).show();
                    else {
                        button.setEnabled(false);
                        Log.d(TAG, "onClick: ");
                        getAdminUser();


                    }
                }
            });
        }


    }

    private void getAdminUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("users");
        List<User> adminList = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()) {
                    User user = users.getValue(User.class);
                    if (user != null && user.getRole() == 1) {
                        adminList.add(user);
                    }
                }
                checkWithDrawLimitAndDoTransaction(amt.getText().toString(), adminList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });
    }

    private void checkWithDrawLimitAndDoTransaction(String amountStr, List<User> adminList) {

        withdrawLimit = new WithdrawLimit() {
            @Override
            public void sendWithdrawLimit(Double amount, Double accountBal) {
                Log.e(TAG, "sendWithdrawLimit: " + amount);
                double withdrawnAmt = Double.parseDouble(amt.getText().toString());
                if (amount + withdrawnAmt <= 100.0) {
                    withdrawMoney(accountBal, adminList);
                    sendNotificationToUser("Admin");
                    sendNotificationToUser("topics/Admin");
                    sendNotificationToUser("/topics/Admin");
                } else {
                    amt.setError("Withdrawal Limit Surpassed");
                    Toast.makeText(WithdrawDepositActivity.this, "Withdrawal Limit Surpassed", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "sendWithdrawLimit: " + "Withdrawal Limit Surpassed");
                }
            }
        };

        checkWithDrawLimit(firebaseUser.getUid(), amountStr);

    }

    private void withdrawMoney(Double accountBal, List<User> adminList) {

//        String randomID = AlphaNumericRandomGenerator.generateID();

        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startDialog();
        double withdrawnAmt = Double.parseDouble(amt.getText().toString());
        final double withdrawnAmtFinal = takeProcessingFees(withdrawnAmt);
        final String name = nameGovt.getText().toString();
        final String paypalEmail = paypalAddress.getText().toString();
        transactionRef = FirebaseDatabase.getInstance().getReference("Previous_Transactions").child(firebaseUser.getUid());
        final String transID = transactionRef.push().getKey();
        if (adminList.size() > 0) {
            //If there are more than one Admin Account then user One of them;
            User user = adminList.get(0);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUser_id());
            if (withdrawnAmt - withdrawnAmtFinal > 0.0) {
                user.setAccount_balance(user.getAccount_balance() + (withdrawnAmt - withdrawnAmtFinal));
                userRef.setValue(user).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Error while updating AdminBal ");
                        }

                    }
                });
            }

        }
        PreviousTransactionsRecord ob1 = new PreviousTransactionsRecord(firebaseUser.getUid(), transID, transactionDate(),
                PreviousTransactionsRecord.WITHDRAW, PreviousTransactionsRecord.PENDING, paypalEmail, name, withdrawnAmt, withdrawnAmtFinal, accountBal);

        assert transID != null;
        transactionRef.child(transID).setValue(ob1).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    WithdrawAlertDialog alertDialog = new WithdrawAlertDialog(WithdrawDepositActivity.this, transID, withdrawnAmt);
                    alertDialog.setCanceledOnTouchOutside(false);
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                    button.setEnabled(true);
                } else {
                    Toast.makeText(WithdrawDepositActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismissDialog();
            }
        });

        //Also update database
        updateDatabase(withdrawnAmt, false);
    }

    private double takeProcessingFees(double withdrawnAmt) {
        int feePercentage = 8;
        return (withdrawnAmt - ((withdrawnAmt * feePercentage) / 100));
    }


    private void updateDatabase(final double amount, final boolean flag) {

        updateReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double previous_balance = Double.parseDouble(String.valueOf(dataSnapshot.getValue()));
                HashMap<String, Object> hashMap = new HashMap<>();

                if (flag)
                    hashMap.put("account_balance", previous_balance + amount);
                else
                    hashMap.put("account_balance", previous_balance - amount);
                updateReference.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String transactionDate() {
        Date todayDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
        return dateFormat.format(todayDate);
    }


    private void sendNotificationToUser(String token) {
        RootModel rootModel = new RootModel(token, new NotificationModel("Request Payment", "Request Payment by " + paypalAddress.getText().toString()), new DataModel(nameGovt.getText().toString(), "30"));

        NotificationAPI apiService = NotificationClient.getClient().create(NotificationAPI.class);
        retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(rootModel);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "Successfully notification send by using retrofit.");
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private void checkWithDrawLimit(String uid, String amount) {
        double transAmt = Double.parseDouble(amount);
        Log.d(TAG, "checkWithDrawLimit: ");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = (DatabaseReference) database.getReference("Previous_Transactions");
        this.databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double accountBal = Double.parseDouble(snapshot.getValue() + "");
                if (accountBal >= transAmt) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        double transactionOnThatDay = 0;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                DataSnapshot item = snapshot.child(uid);
                                for (DataSnapshot trans : item.getChildren()) {
                                    PreviousTransactionsRecord previousTransactionsRecord = trans.getValue(PreviousTransactionsRecord.class);
                                    if (previousTransactionsRecord.getDate() != null) {
                                        boolean checkSameDay = isSameDay(previousTransactionsRecord.getDate());
                                        if (checkSameDay) {
                                            transactionOnThatDay += previousTransactionsRecord.getAmount();

                                        }
                                    }
                                }
                                withdrawLimit.sendWithdrawLimit(transactionOnThatDay, (double) accountBal);

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "onCancelled: ");
                        }

                    });

                } else {
                    amt.setError("Not Enough Balance");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean isSameDay(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
        boolean sameDay;
        try {

            Date date = dateFormat.parse(dateString);
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            Date now = new Date();
            if (date != null) {
                cal1.setTime(date);
            }
            cal2.setTime(now);
            sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        } catch (ParseException e) {
            Log.e(TAG, "isSameDay: ");
            e.printStackTrace();
            sameDay = false;
        }
        return sameDay;

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}

interface WithdrawLimit {
    void sendWithdrawLimit(Double amount, Double accountBal);
}

