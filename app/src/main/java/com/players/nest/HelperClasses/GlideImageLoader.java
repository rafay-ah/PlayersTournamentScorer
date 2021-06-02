package com.players.nest.HelperClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class GlideImageLoader {

    public static void loadImageWithProgress(Context context, String imgURL, ImageView imageView, final ProgressBar progressBar) {

        Glide.with(context)
                .load(imgURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        assert e != null;
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);
    }


    public static void loadImageWithPlayIcon(final Context context, final String imgURL, final ImageView imageView,
                                             final ImageView playIcon) {

        Glide.with(context)
                .load(imgURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (imgURL.toLowerCase().contains(Constants.VIDEO_FILE_FIREBASE)) {
                            playIcon.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                }).into(imageView);
    }


    public static void loadImageWithPlaceHolder(Context context, String imgURL, ImageView imageView, final LinearLayout placeHolder) {

        Glide.with(context)
                .load(imgURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        placeHolder.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);
    }
}
