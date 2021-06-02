package com.players.nest.MatchDisputeChat.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.players.nest.R;
public class WinnerDialog {

    TextView message;
    Button button;
    Activity activity;
    AlertDialog dialog;

    public WinnerDialog(Activity activity) {
        this.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = this.activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.winner_dialog, null);

        message = view.findViewById(R.id.winner_name);
        button = view.findViewById(R.id.winner_button);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public void changeMsg(String message) {
        this.message.setText(message);
    }

    public void startDialog() {
        dialog.show();
        try {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {

        }
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}
