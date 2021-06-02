package com.players.nest.HelperClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.players.nest.R;

public class WithdrawAlertDialog extends Dialog {

    String transactionID;
    double amount, transferFee = 0.08;
    Button okBtn;
    Context context;
    TextView transID, totalAmount, processingFee, msg;

    public WithdrawAlertDialog(@NonNull Context context, String transactionID, double amount) {
        super(context);
        this.context = context;
        this.transactionID = transactionID;
        this.amount = amount;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_alert_dialog);

        okBtn = findViewById(R.id.button6);
        transID = findViewById(R.id.textView38);
        totalAmount = findViewById(R.id.textView33);
        processingFee = findViewById(R.id.textView34);
        msg = findViewById(R.id.textView36);

        double processFee = transferFee * amount;
        String message = "$" + amount + " " + context.getString(R.string.WITHDRAW_DESCRIPTION);

        transID.setText(transactionID);
        totalAmount.setText("$ " + amount);
        processingFee.setText("$ " + processFee);
        msg.setText(message);
        Log.d("mTAG", "onCreate: " + message);


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                ((Activity) context).finish();
            }
        });
    }
}
