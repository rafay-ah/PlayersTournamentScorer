  package com.players.nest.Tournament.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.R;
import com.players.nest.Tournament.Fragment.BracketsColumnFragment;
import com.players.nest.Tournament.Model.MatchData;
import com.players.nest.Tournament.Util.BracketsUtility;
import com.players.nest.Tournament.ViewHolder.BracketCellViewHolder;

import java.util.ArrayList;

public class BracketsCellAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BracketsColumnFragment fragment;
    private Context context;
    private ArrayList<MatchData> list;
    private boolean handler;

    public BracketsCellAdapter(BracketsColumnFragment bracketsColumnFragment, Context context, ArrayList<MatchData> list) {
        this.fragment = bracketsColumnFragment;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tournament_tree_items, parent, false);
        return new BracketCellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BracketCellViewHolder viewHolder = null;
        if (holder instanceof BracketCellViewHolder){
            viewHolder = (BracketCellViewHolder) holder;
            setFields(viewHolder, position);
        }
    }

    private void setFields(BracketCellViewHolder viewHolder, int position) {
        handler = new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                viewHolder.setAnimation(list.get(position).getHeight());
            }
        }, 100);

        viewHolder.getTeamOneName().setText(list.get(position).getCompetitorOne().getName());
        viewHolder.getTeamTwoName().setText(list.get(position).getCompetitorTwo().getName());
        viewHolder.getTeamOneScore().setText(list.get(position).getCompetitorOne().getScore());
        viewHolder.getTeamTwoScore().setText(list.get(position).getCompetitorTwo().getScore());
        viewHolder.getTeamOneRating().setText(list.get(position).getCompetitorOne().getRating());
        viewHolder.getTeamTwoRating().setText(list.get(position).getCompetitorTwo().getRating());
        int height = list.get(position).getHeight();
        if(position%2==0){
            viewHolder.getViewVertical().getLayoutParams().height = height;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.getViewVertical().getLayoutParams();
            layoutParams.setMargins(-100,height/2 -1,0,0);
            viewHolder.getViewVertical().setLayoutParams(layoutParams);
        }
        else{
            viewHolder.getViewVertical().getLayoutParams().height = height;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.getViewVertical().getLayoutParams();
            layoutParams.setMargins(-100,-(height/2),0,0);
            viewHolder.getViewVertical().setLayoutParams(layoutParams);
        }

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
    public void setList(ArrayList<MatchData> columnList) {
        this.list = columnList;
        notifyDataSetChanged();
    }
}
