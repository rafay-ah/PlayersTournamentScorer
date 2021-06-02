package com.players.nest.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.ModelClasses.PreviousTransactionsRecord;
import com.players.nest.ModelClasses.User;

import java.util.ArrayList;
import java.util.Objects;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TYPE = "TYPE_DEPOSIT_WITHDRAW";

    Toolbar toolbar;
    TextView walletBalance, previousTrans;
    LinearLayout previousTransactionLayout;
    Button depositBtn, withdrawBtn;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    DatabaseReference transactionRef;
    ValueEventListener valueEventListener;

    ArrayList<PreviousTransactionsRecord> transactionsRecordsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        toolbar = findViewById(R.id.toolbar2);
        depositBtn = findViewById(R.id.button3);
        withdrawBtn = findViewById(R.id.button4);
        walletBalance = findViewById(R.id.textView6);
        previousTrans = findViewById(R.id.textView45);
        recyclerView = findViewById(R.id.recycler_view2);
        previousTransactionLayout = findViewById(R.id.previousTransactions);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());


        //OnClickListeners
        depositBtn.setOnClickListener(this);
        withdrawBtn.setOnClickListener(this);


        //Custom Methods
        setAccountBalance();
        setRecyclerView();
    }

    private void setRecyclerView() {

        transactionRef = FirebaseDatabase.getInstance().getReference("Previous_Transactions").child(firebaseUser.getUid());
        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PreviousTransactionsRecord transactionsRecord = snapshot.getValue(PreviousTransactionsRecord.class);
                    transactionsRecordsList.add(transactionsRecord);
                }
                setTransactions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WalletActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTransactions() {

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        if (!transactionsRecordsList.isEmpty()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            WalletTransactionAdapter adapter = new WalletTransactionAdapter(transactionsRecordsList);
            recyclerView.setAdapter(adapter);
            previousTrans.setVisibility(View.VISIBLE);
            previousTransactionLayout.setVisibility(View.VISIBLE);
        } else {
            previousTransactionLayout.setVisibility(View.GONE);
            previousTrans.setVisibility(View.GONE);
        }
    }

    private void setAccountBalance() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                walletBalance.setText("" + snapshot.getValue(User.class).getAccount_balance());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WalletActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.child(firebaseUser.getUid()).addValueEventListener(valueEventListener);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button3) {
            Intent intent = new Intent(this, WithdrawDepositActivity.class);
            intent.putExtra(TYPE, "Deposit");
            startActivity(intent);
        } else {
            double balance = Double.parseDouble(walletBalance.getText().toString());
            if (balance > 0) {
                Intent intent = new Intent(this, WithdrawDepositActivity.class);
                intent.putExtra(TYPE, "Withdraw");
                startActivity(intent);
            } else
                Toast.makeText(this, "Your balance is too Low!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }
}