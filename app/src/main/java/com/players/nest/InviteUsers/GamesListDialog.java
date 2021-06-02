package com.players.nest.InviteUsers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.R;
import com.players.nest.ModelClasses.Games;

import java.util.ArrayList;

public class GamesListDialog extends DialogFragment {

    Context context;
    RecyclerView recyclerView;
    ArrayList<Games> gamesList;
    GetSelectedGame getSelectedGame;

    public GamesListDialog(Context context, ArrayList<Games> gamesList, GetSelectedGame getSelectedGame) {
        this.context = context;
        this.gamesList = gamesList;
        this.getSelectedGame = getSelectedGame;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.games_list_dialog, container, false);

        recyclerView = view.findViewById(R.id.recycler_view18);

        //setAdapter
        GamesListAdapt adapt = new GamesListAdapt(context, gamesList, game -> {
            getSelectedGame.getGame(game);
            dismiss();
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapt);

        return view;
    }

    public interface GetSelectedGame {
        void getGame(Games game);
    }
}
