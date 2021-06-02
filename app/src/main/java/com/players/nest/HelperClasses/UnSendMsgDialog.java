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
import static android.view.View.GONE;

public class UnSendMsgDialog extends DialogFragment {

    int Type;
    View divider;
    TextView unSend, copy;
    unSendMsgListener listener;

    public UnSendMsgDialog(int Type, unSendMsgListener listener) {
        this.listener = listener;
        this.Type = Type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unsend_msg_dialog, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        divider = view.findViewById(R.id.divider10);
        unSend = view.findViewById(R.id.textView121);
        copy = view.findViewById(R.id.textView122);


        if (Type == Constants.DELETE_COMMENT) {
            divider.setVisibility(View.INVISIBLE);
            copy.setVisibility(GONE);
            unSend.setText("Delete Comment");
        } else if (Type == Constants.COPY_MESSAGE) {
            divider.setVisibility(View.GONE);
            unSend.setVisibility(GONE);
        } else if (Type == Constants.UNSENT_IMAGE) {
            divider.setVisibility(View.INVISIBLE);
            copy.setVisibility(GONE);
        }

        unSend.setOnClickListener(view1 -> {
            listener.unSendMsg();
            dismiss();
        });
        copy.setOnClickListener(v -> {
            listener.copyMessage();
            dismiss();
        });


        return view;
    }

    public interface unSendMsgListener {
        void unSendMsg();

        void copyMessage();
    }
}
