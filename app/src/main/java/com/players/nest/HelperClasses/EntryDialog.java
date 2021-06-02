package com.players.nest.HelperClasses;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.players.nest.R;

public class EntryDialog extends DialogFragment implements View.OnClickListener {

    LinearLayout one, five, ten, twenty, fifty, hundred, twoHundred, twoHundredFifty;
    entryDialogInterface mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entry_dialog, container, false);

        one = view.findViewById(R.id.one);
        five = view.findViewById(R.id.five);
        ten = view.findViewById(R.id.ten);
        twenty = view.findViewById(R.id.twenty);
        fifty = view.findViewById(R.id.fifty);
        hundred = view.findViewById(R.id.hundred);
        twoHundred = view.findViewById(R.id.twoHundred);
        twoHundredFifty = view.findViewById(R.id.twoHundredFifty);


        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        one.setOnClickListener(this);
        five.setOnClickListener(this);
        ten.setOnClickListener(this);
        twenty.setOnClickListener(this);
        fifty.setOnClickListener(this);
        hundred.setOnClickListener(this);
        twoHundred.setOnClickListener(this);
        twoHundredFifty.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.one) {
            mListener.entryAmt("$1.00", 1, 1.84);
        } else if (id == R.id.five) {
            mListener.entryAmt("$5.00", 5, 9.2);
        } else if (id == R.id.ten) {
            mListener.entryAmt("$10", 10, 18.4);
        } else if (id == R.id.twenty) {
            mListener.entryAmt("$20", 20, 36.8);
        } else if (id == R.id.fifty) {
            mListener.entryAmt("$50", 50, 92);
        } else if (id == R.id.hundred) {
            mListener.entryAmt("$100", 100, 184);
        } else if (id == R.id.twoHundred) {
            mListener.entryAmt("$200", 200, 368);
        } else if (id == R.id.twoHundredFifty) {
            mListener.entryAmt("$250", 250, 460);
        }
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (entryDialogInterface) getTargetFragment();
        if (mListener == null)
            mListener = (entryDialogInterface) context;
    }

    public interface entryDialogInterface {
        void entryAmt(String txt, double amt, double winningAmt);
    }

}
