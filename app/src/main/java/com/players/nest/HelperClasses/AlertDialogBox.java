package com.players.nest.HelperClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.players.nest.R;

public class AlertDialogBox extends Dialog {

    Button okBtn;
    Context context;
    String transactionIDString, amount;
    TextView amt, transactionID;

    public AlertDialogBox(@NonNull Context context, String transactionIDString, String amount) {
        super(context);
        this.context = context;
        this.amount = amount;
        this.transactionIDString = transactionIDString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_box);

        transactionID = findViewById(R.id.textView44);
        amt = findViewById(R.id.textView43);
        okBtn = findViewById(R.id.button6);

        transactionID.setText(transactionIDString);
        amt.setText("$" + amount);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                ((Activity) context).finish();
            }
        });
    }
}