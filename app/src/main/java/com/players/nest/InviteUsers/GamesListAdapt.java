package com.players.nest.InviteUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.players.nest.R;
import com.players.nest.ModelClasses.Games;

import java.util.ArrayList;

public class GamesListAdapt extends RecyclerView.Adapter<GamesListAdapt.mViewHolderClass> {

    Context context;
    ArrayList<Games> mList;
    GameSelected gameSelected;

    public GamesListAdapt(Context context, ArrayList<Games> mList, GameSelected gameSelected) {
        this.context = context;
        this.mList = mList;
        this.gameSelected = gameSelected;
    }

    @NonNull
    @Override
    public GamesListAdapt.mViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.games_list_dialog_recycler_view, parent, false);
        return new mViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GamesListAdapt.mViewHolderClass holder, int position) {

        holder.gameName.setText(mList.get(position).getName());
        Glide.with(context).load(mList.get(position).getGameImg()).into(holder.imgRes);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class mViewHolderClass extends RecyclerView.ViewHolder {

        TextView gameName;
        ImageView imgRes;

        public mViewHolderClass(@NonNull View itemView) {
            super(itemView);

            gameName = itemView.findViewById(R.id.textView200);
            imgRes = itemView.findViewById(R.id.imageView66);

            itemView.setOnClickListener(v -> {
                gameSelected.onGameSelected(mList.get(getAdapterPosition()));
            });
        }
    }


    public interface GameSelected {
        void onGameSelected(Games game);
    }
}
