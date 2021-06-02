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

public class ConfirmDialog extends DialogFragment {

    int type;
    TextView yes, cancel, msg;
    ConfirmDialogInterface confirmDialogInterface;

    public ConfirmDialog(int type, ConfirmDialogInterface confirmDialogInterface) {
        this.confirmDialogInterface = confirmDialogInterface;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_dialog, container, false);

        msg = view.findViewById(R.id.textView91);
        yes = view.findViewById(R.id.textView92);
        cancel = view.findViewById(R.id.textView93);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        if (type == Constants.CONFIRM_EXIT_MATCH) {
            msg.setText(R.string.EXIT_DIALOG_TEXT);

        } else if (type == Constants.SUBMIT_RESULT_DIALOG) {
            msg.setText(R.string.SUBMIT_RESULT_DIALOG);

        } else if (type == Constants.MY_MATCHES_MENU) {
            msg.setText(R.string.DELETE_MY_MATCH);

        } else if (type == Constants.CREATE_MATCH_BUTTON) {
            msg.setText(R.string.CONFIRM_CREATE_MATCH);

        } else if (type == Constants.ENTER_DISPUTE_DIALOG)
            msg.setText(R.string.ENTER_DISPUTE_DIALOG_MSG);

        else if (type == Constants.SEND_INVITATION)
            msg.setText(R.string.SEND_INVITATION_ALERT_MSG);

        else if (type == Constants.CANCEL_INVITE)
            msg.setText(getString(R.string.CANCEL_REQUEST_MSG));

        else if (type == Constants.JOIN_MATCH)
            msg.setText(getString(R.string.CONFIRM_JOIN_MATCH));

        cancel.setOnClickListener(view12 -> dismiss());
        yes.setOnClickListener(view1 -> {
            dismiss();
            confirmDialogInterface.onConfirmed();
        });

        return view;
    }

    public interface ConfirmDialogInterface {
        void onConfirmed();
    }
}
