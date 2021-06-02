package com.players.nest.PostFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.R;
import com.players.nest.VideoTrimmer.K4LVideoTrimmer;
import com.players.nest.VideoTrimmer.interfaces.OnTrimVideoListener;

import java.io.File;


public class VideoTrimmer extends AppCompatActivity implements OnTrimVideoListener {

    String path;
    K4LVideoTrimmer videoTrimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trimmer);

        videoTrimmer = findViewById(R.id.timeLine);


        //Get Video File path
        Intent intent = getIntent();
        path = intent.getStringExtra(Constants.VIDEO_FILE_PATH);


        if (videoTrimmer != null && path != null) {
            setVideoTrimmer();
        } else
            Snackbar.make(findViewById(android.R.id.content), "Something went wrong. Please upload other video.", Snackbar.LENGTH_LONG)
                    .setAction("GO BACK", v -> finish())
                    .setActionTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null))
                    .show();
    }

    private void setVideoTrimmer() {

        String destPath = getExternalCacheDir().getPath() + "/Trimmed Videos/";
        videoTrimmer.setMaxDuration(39);
        videoTrimmer.setDestinationPath(destPath);
        videoTrimmer.setVideoURI(Uri.parse(path));
        videoTrimmer.setVideoInformationVisibility(true);
        videoTrimmer.setOnTrimVideoListener(this);
    }

    @Override
    public void onTrimStarted() {
    }

    @Override
    public void getResult(Uri uri) {
        Intent intent = new Intent(VideoTrimmer.this, PostActivity.class);
        File file = new File(String.valueOf(uri));
        intent.putExtra(Constants.SELECTED_FILE_URL, String.valueOf(Uri.fromFile(file)));
        intent.putExtra(Constants.POST_TYPE, Constants.VIDEO_POST_TYPE);
        startActivity(intent);
        finish();
    }

    @Override
    public void cancelAction() {
        Toast toast = Toast.makeText(this, "Trim cancelled", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        finish();
    }

    @Override
    public void onError(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}