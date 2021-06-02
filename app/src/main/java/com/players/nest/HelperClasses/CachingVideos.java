package com.players.nest.HelperClasses;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.Objects;

public class CachingVideos {

    public static File mVideoFileDirectory(Context context) {
        return new File(context.getCacheDir(), "/mVideos/");
    }

    public static boolean isVideoExistsInCache(Context context, String videoUriLastSegment) {
        return getVideoFile(context, videoUriLastSegment).exists();
    }

    public static File getVideoFile(Context context, String videoUriLastPathSegment) {

        int lastIndexOf = videoUriLastPathSegment.lastIndexOf("/");
        String videoName = videoUriLastPathSegment.substring(lastIndexOf);
        return new File(mVideoFileDirectory(context), videoName);
    }

    public static void putVideoIntoCache(Context context, VideoView videoView, String videoUri) {

        File videoDirectory = mVideoFileDirectory(context);

        //Making mVideos Folder inside Cache Directory
        if (!videoDirectory.exists()) {
            if (!videoDirectory.mkdir()) {
                Toast.makeText(context, "Cannot create a directory!", Toast.LENGTH_SHORT).show();
            } else {
                videoDirectory.mkdirs();
            }
        }

        File videoFile = getVideoFile(context, Objects.requireNonNull(Uri.parse(videoUri).getLastPathSegment()));

        FirebaseStorage.getInstance().getReferenceFromUrl(videoUri)
                .getFile(videoFile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        videoView.setVideoPath(videoFile.getPath());
                        videoView.start();
                    } else
                        Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage()
                                , Toast.LENGTH_LONG).show();
                });
    }
}
