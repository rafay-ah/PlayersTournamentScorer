package com.players.nest.PostFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.players.nest.R;

import java.util.List;

public class RecyclerViewAdapt extends RecyclerView.Adapter<RecyclerViewAdapt.mViewHolder> {

    private static final String TAG = "GLIDE_ERROR";

    Context context;
    List<String> imageURL;
    clickListener mListener;

    public RecyclerViewAdapt(Context context, List<String> imageURL, clickListener mListener) {
        this.context = context;
        this.imageURL = imageURL;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.post_recycler_view, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewHolder holder, int position) {
        Glide.with(context).load(imageURL.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageURL.size();
    }


    public interface clickListener {
        void itemClicked(int position);
    }


    public class mViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.squareImageView);

            itemView.setOnClickListener(v -> mListener.itemClicked(getAdapterPosition()));
        }
    }
}
