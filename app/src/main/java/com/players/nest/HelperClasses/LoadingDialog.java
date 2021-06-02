package com.players.nest.HelperClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.players.nest.R;

public class LoadingDialog {

    TextView message;
    Activity activity;
    AlertDialog dialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = this.activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_loading_dialog, null);

        message = view.findViewById(R.id.textView15);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
    }

    public void changeMsg(String message) {
        this.message.setText(message);
    }

    public void startDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}
