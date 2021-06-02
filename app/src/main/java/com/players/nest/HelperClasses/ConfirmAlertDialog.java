package com.players.nest.HelperClasses;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.players.nest.R;

public class ConfirmAlertDialog extends DialogFragment {

    int TYPE;
    TextView okBtn, msg, title;
    ConfirmDialogListener listener;

    public ConfirmAlertDialog(int TYPE, ConfirmDialogListener listener) {
        this.listener = listener;
        this.TYPE = TYPE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_match_alert_dialog, container, false);

        //For Transparent BG
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        setCancelable(false);

        okBtn = view.findViewById(R.id.textView114);
        msg = view.findViewById(R.id.textView124);
        title = view.findViewById(R.id.textView115);

        if (TYPE == 1) {
            msg.setText(getString(R.string.MATCH_CANCELLED_DIALOG));
            title.setText(R.string.MATCH_CANCELLED);

        } else if (TYPE == Constants.HOST_REJECTED) {
            msg.setText(R.string.HOST_REJECTED_DIALOG);
            title.setText(R.string.MATCH_CANCELLED);

        } else if (TYPE == Constants.MATCH_STARTED_DIALOG) {
            msg.setText(R.string.YOU_CANT_ENTER_MATCH);

        } else if (TYPE == Constants.SCORES_ARE_CORRECT) {
            msg.setText(getString(R.string.THIS_SCORE_WILL_SUBMITTED));
            setCancelable(true);

        } else if (TYPE == Constants.EVIDENCE_SUBMITTED)
            msg.setText(R.string.EVIDENCE_SUBMITTED_SUCCESSFULLY);


        else if( TYPE == 10){
            msg.setText("Are you sure you want to leave the tournament.Your money will be refunded.");
            setCancelable(true);
        }
        okBtn.setOnClickListener(view1 -> {
            listener.okBtn();
            dismiss();
        });

        return view;
    }


    public interface ConfirmDialogListener {
        void okBtn();
    }
}
