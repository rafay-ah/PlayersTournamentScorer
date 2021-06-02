package com.players.nest.HelperClasses;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.players.nest.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import static com.players.nest.ProfileFragment.ViewProfileFragment.VIEW_PROFILE_TYPE;

public class ViewPostBottomSheet extends BottomSheetDialogFragment {

    String TYPE;
    boolean isCommentOff;
    LinearLayout editLayout, deleteLayout, turnOffCommentsLayout;
    TextView edit, delete, shareTo, saveToCollections, turnOffCommentTxt;

    ViewProfileBottomSheetListener listener;

    public ViewPostBottomSheet(String TYPE, boolean isCommentingOff) {
        this.TYPE = TYPE;
        this.isCommentOff = isCommentingOff;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_profile_bottom_sheet, container, false);

        edit = view.findViewById(R.id.textView194);
        delete = view.findViewById(R.id.textView25);
        shareTo = view.findViewById(R.id.textView20);
        editLayout = view.findViewById(R.id.linearLayout11);
        deleteLayout = view.findViewById(R.id.linearLayout5);
        turnOffCommentTxt = view.findViewById(R.id.textView27);
        saveToCollections = view.findViewById(R.id.textView26);
        turnOffCommentsLayout = view.findViewById(R.id.linearLayout7);

        if (TYPE.equals(VIEW_PROFILE_TYPE)) {
            editLayout.setVisibility(View.GONE);
            deleteLayout.setVisibility(View.GONE);
            turnOffCommentsLayout.setVisibility(View.GONE);
        }

        if (isCommentOff)
            turnOffCommentTxt.setText("Turn on commenting");
        else
            turnOffCommentTxt.setText("Turn off commenting");

        delete.setOnClickListener(view1 -> {
            listener.onDelete();
            dismiss();
        });

        editLayout.setOnClickListener(view1 -> {
            dismiss();
            listener.onEditPost();
        });

        turnOffCommentsLayout.setOnClickListener(view1 -> {
            dismiss();
            listener.onTurnOffCommenting();
        });

        return view;
    }


    public interface ViewProfileBottomSheetListener {
        void onDelete();

        void onEditPost();

        void onTurnOffCommenting();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ViewProfileBottomSheetListener) getTargetFragment();
        Log.d("mTAG", "onAttach: " + listener);
    }

    ///For Transparent BG of the BottomSheet
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) Objects.requireNonNull(getView()).getParent()).setBackgroundColor(Color.TRANSPARENT);
    }
}
