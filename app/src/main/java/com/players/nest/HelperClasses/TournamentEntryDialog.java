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

public class TournamentEntryDialog extends DialogFragment  implements View.OnClickListener{
    LinearLayout one, five, ten, twenty, fifty, hundred, twoHundred, twoHundredFifty;
    TournamentEntryDialog.entryDialogInterface mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tournament_entry_fees, container, false);

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
            mListener.entryAmt("$1.00", 1);
        } else if (id == R.id.five) {
            mListener.entryAmt("$5.00", 5);
        } else if (id == R.id.ten) {
            mListener.entryAmt("$10", 10);
        } else if (id == R.id.twenty) {
            mListener.entryAmt("$20", 20);
        } else if (id == R.id.fifty) {
            mListener.entryAmt("$50", 50);
        } else if (id == R.id.hundred) {
            mListener.entryAmt("$100", 100);
        } else if (id == R.id.twoHundred) {
            mListener.entryAmt("$200", 200);
        } else if (id == R.id.twoHundredFifty) {
            mListener.entryAmt("$250", 250);
        }
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (TournamentEntryDialog.entryDialogInterface) getTargetFragment();
        if (mListener == null)
            mListener = (TournamentEntryDialog.entryDialogInterface) context;
    }

    public interface entryDialogInterface {
        void entryAmt(String txt, double amt);
    }

}


