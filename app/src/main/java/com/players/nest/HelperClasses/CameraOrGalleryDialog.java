package com.players.nest.HelperClasses;

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

public class CameraOrGalleryDialog extends DialogFragment {

    LinearLayout camera, gallery;
    CameraGalleryDialogListener dialogListener;

    public CameraOrGalleryDialog(CameraGalleryDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_gallery_dialog, container, false);

        //For Transparent BG
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        camera = view.findViewById(R.id.linearLayout30);
        gallery = view.findViewById(R.id.linearLayout31);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dialogListener.onCamera();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dialogListener.onGallery();
            }
        });

        return view;
    }


    public interface CameraGalleryDialogListener {

        void onCamera();

        void onGallery();
    }
}
