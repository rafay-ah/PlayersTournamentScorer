package com.players.nest.HelperClasses;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.players.nest.R;

import java.util.Objects;

public class BottomSheetNav extends BottomSheetDialogFragment {

    BottomSheetListener mListener;
    LinearLayout change, remove;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        change = view.findViewById(R.id.linearLayout3);
        remove = view.findViewById(R.id.linearLayout4);

        change.setOnClickListener(view12 -> {
            mListener.changePic();
            dismiss();
        });

        remove.setOnClickListener(view1 -> {
            mListener.removePic();
            dismiss();
        });

        return view;
    }

    ///For Transparent BG of the BottomSheet
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) Objects.requireNonNull(getView()).getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    public interface BottomSheetListener {
        void changePic();

        void removePic();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (BottomSheetListener) context;
    }
}
