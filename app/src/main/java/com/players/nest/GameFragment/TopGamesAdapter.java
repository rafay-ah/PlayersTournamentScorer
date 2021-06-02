package com.players.nest.GameFragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.players.nest.ModelClasses.Games;
import com.players.nest.R;

import java.util.ArrayList;

public class TopGamesAdapter extends RecyclerView.Adapter<TopGamesAdapter.ViewHolderClass> {

    public static final String SELECTED_GAME_OBJECT = "SELECTED_GAME";
    Context context;
    ArrayList<Games> gamesArrayList;

    public TopGamesAdapter(Context context, ArrayList<Games> gamesArrayList) {
        this.context = context;
        this.gamesArrayList = gamesArrayList;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.top_games_recycler_view, parent, false);
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.gameName.setText(gamesArrayList.get(position).getName());
        Glide.with(context).load(gamesArrayList.get(position).getGameImg()).into(holder.imgRes);
    }

    @Override
    public int getItemCount() {
        return gamesArrayList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView gameName;
        ImageView imgRes;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            gameName = itemView.findViewById(R.id.textView63);
            imgRes = itemView.findViewById(R.id.imageView25);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Games game = gamesArrayList.get(getAdapterPosition());

            Intent intent = new Intent(context, GameActivity.class);
            intent.putExtra(SELECTED_GAME_OBJECT, game);
            context.startActivity(intent);
        }
    }
}
