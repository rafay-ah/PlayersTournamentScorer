package com.players.nest.ProfileFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.players.nest.HelperClasses.GlideImageLoader;
import com.players.nest.ModelClasses.UsersPosts;
import com.players.nest.R;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    Context context;
    ArrayList<UsersPosts> usersPosts;


    public GridViewAdapter(Context context, ArrayList<UsersPosts> usersPosts) {
        this.context = context;
        this.usersPosts = usersPosts;
    }

    @Override
    public int getCount() {
        return usersPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View view1 = inflater.inflate(R.layout.grid_view_layout, viewGroup, false);
        ImageView imageView = view1.findViewById(R.id.gridImageView);
        ImageView playIcon = view1.findViewById(R.id.imageView52);

        GlideImageLoader.loadImageWithPlayIcon(context, usersPosts.get(i).getImageUri(), imageView, playIcon);


        return view1;
    }
}
