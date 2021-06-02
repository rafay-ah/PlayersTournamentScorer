package com.players.nest.Tournament.ViewHolder;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.google.android.exoplayer2.C;
import com.players.nest.R;
import com.players.nest.Tournament.Animation.SlideAnimation;

public class BracketCellViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "BracketCellViewHolder";
    private TextView teamOneName;
    private TextView teamTwoName;
    private TextView teamOneScore;
    private TextView teamTwoScore;
    private TextView teamOneRating;
    private TextView teamTwoRating;
    private Animation animation;
    private LinearLayout rootLayout;
    private View viewHorizontal;
    private View viewVertical;

    public TextView getTeamOneName() {
        return teamOneName;
    }

    public TextView getTeamOneRating() {
        return teamOneRating;
    }

    public TextView getTeamTwoRating() {
        return teamTwoRating;
    }

    public TextView getTeamTwoName() {
        return teamTwoName;
    }

    public TextView getTeamOneScore() {
        return teamOneScore;
    }

    public TextView getTeamTwoScore() {
        return teamTwoScore;
    }

    public LinearLayout getRootLayout() {
        return rootLayout;
    }
    public View getViewHorizontal() {
        return viewHorizontal;
    }


    public View getViewVertical() {
        return viewVertical;
    }




    public void setAnimation(int height){
        Log.d(TAG, "setAnimation: " + height);
        animation = new SlideAnimation(rootLayout, rootLayout.getHeight(),
                height);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(200);
        rootLayout.setAnimation(animation);
        rootLayout.startAnimation(animation);
    }


    public BracketCellViewHolder(@NonNull View itemView) {
        super(itemView);
        rootLayout = itemView.findViewById(R.id.layout_root);
        teamOneName = (TextView) itemView.findViewById(R.id.tv_username1);
        teamTwoName = (TextView) itemView.findViewById(R.id.tv_username2);
        teamOneScore = (TextView) itemView.findViewById(R.id.tv_score1);
        teamTwoScore = (TextView) itemView.findViewById(R.id.tv_score2);
        teamOneRating = (TextView) itemView.findViewById(R.id.tv_rating1);
        teamTwoRating= (TextView) itemView.findViewById(R.id.tv_rating2);
        viewHorizontal = itemView.findViewById(R.id.line_h);
        viewVertical = itemView.findViewById(R.id.line_v);
    }
}
