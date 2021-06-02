package com.players.nest.MatchDisputeChat.View;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.players.nest.R;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoOrVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_or_video);

        PhotoView photoView = findViewById(R.id.photoViewClick);
        VideoView videoView = findViewById(R.id.videoViewClick);

        String source = getIntent().getStringExtra("source");
        String url = getIntent().getStringExtra("url");
        assert source != null;
        if (source.equals("image")) {
            Glide.with(this).load(url).into(photoView);
            videoView.setVisibility(View.GONE);
        } else {
            videoView.setVideoURI(Uri.parse(url));
            videoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    videoView.start();
                }
            });
            photoView.setVisibility(View.GONE);
        }

    }
}