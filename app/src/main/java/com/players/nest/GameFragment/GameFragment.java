package com.players.nest.GameFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.ModelClasses.Games;
import com.players.nest.MyMatchesActivity.MyMatchesActivity;
import com.players.nest.R;
import com.players.nest.SearchActivity.SearchActivity;
import com.players.nest.Tournament.TournamentActivity;

import java.util.ArrayList;

public class GameFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "GAME_FRAGMENT";

    View view;
    Toolbar toolbar;
    TextView select_GameTXT;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TopGamesAdapter gamesAdapter;
    LinearLayout matchBtn, tournamentBtn;
    ArrayList<Games> gamesArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game, container, false);

        toolbar = view.findViewById(R.id.toolbar8);
        matchBtn = view.findViewById(R.id.linearLayout14);
        progressBar = view.findViewById(R.id.progressBar23);
        select_GameTXT = view.findViewById(R.id.textView2);
        tournamentBtn = view.findViewById(R.id.linearLayout15);
        recyclerView = view.findViewById(R.id.recycler_view5);


        //Setting Games Adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        gamesAdapter = new TopGamesAdapter(getContext(), gamesArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(gamesAdapter);


        toolbar.inflateMenu(R.menu.game_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search_icon)
                startActivity(new Intent(getActivity(), SearchActivity.class));
            else
                startActivity(new Intent(getActivity(), MyMatchesActivity.class));
            return true;
        });
        matchBtn.setOnClickListener(this);
        tournamentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TournamentActivity.class));
            }
        });


        //Custom Method
        getGamesData();

        return view;
    }


    private void getGamesData() {
        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_GAMES));
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Games game = dataSnapshot.getValue(Games.class);
                    assert game != null;
                    game.setGameID(dataSnapshot.getKey());
                    gamesArrayList.add(game);
                }
                gamesAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view.findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.linearLayout14) {

            Animation slideUpAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            matchBtn.setVisibility(View.INVISIBLE);
            tournamentBtn.setVisibility(View.INVISIBLE);
            select_GameTXT.setVisibility(View.VISIBLE);
            select_GameTXT.startAnimation(slideUpAnim);
        }
    }
}
