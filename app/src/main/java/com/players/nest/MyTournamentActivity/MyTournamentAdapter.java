package com.players.nest.MyTournamentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.HelperClasses.ConfirmDialog;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.ModelClasses.TournamentDetail;
import com.players.nest.ModelClasses.User;
import com.players.nest.MyMatchesActivity.MyMatchesAdapter;
import com.players.nest.R;

import java.util.ArrayList;

public class MyTournamentAdapter extends RecyclerView.Adapter<MyTournamentAdapter.myViewHolderClass>{
    Context context;
    ArrayList<TournamentDetail> tournamentDetails;

    public MyTournamentAdapter(Context context, ArrayList<TournamentDetail> tournamentDetails) {
        this.context = context;
        this.tournamentDetails = tournamentDetails;
    }

    @NonNull
    @Override
    public MyTournamentAdapter.myViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.match_details_recycler_view_layout, parent, false);
        return new myViewHolderClass(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyTournamentAdapter.myViewHolderClass holder, int position) {

        holder.format.setText(tournamentDetails.get(position).getTournamentFormat());
        holder.rules.setText(tournamentDetails.get(position).getTournamentRule());
        holder.entryFee.setText("$ " + tournamentDetails.get(position).getEntryFee());
        holder.winningAmt.setText("$" + tournamentDetails.get(position).getWinningAmt());
        holder.console.setText(tournamentDetails.get(position).getConsole());
        holder.timeCreated.setText(DateUtils.getRelativeTimeSpanString
                (Long.parseLong(tournamentDetails.get(position).getTimeCreated()), System.currentTimeMillis()
                        , DateUtils.MINUTE_IN_MILLIS));

        holder.gameName.setText(tournamentDetails.get(position).getGame().getName());
        Glide.with(context).load(tournamentDetails.get(position).getGame().getGameImg()).into(holder.gamePic);
    }


    @Override
    public int getItemCount() {
        return tournamentDetails.size();
    }


    public class myViewHolderClass extends RecyclerView.ViewHolder {

        ImageView gamePic, menu;
        TextView format, rules, entryFee, console, gameName, timeCreated, winningAmt;

        FirebaseUser firebaseUser;
        DatabaseReference databaseReference2;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        public myViewHolderClass(@NonNull View itemView) {
            super(itemView);

            gamePic = itemView.findViewById(R.id.imageView26);
            format = itemView.findViewById(R.id.textView78);
            rules = itemView.findViewById(R.id.textView86);
            entryFee = itemView.findViewById(R.id.textView87);
            console = itemView.findViewById(R.id.textView89);
            gameName = itemView.findViewById(R.id.textView76);
            winningAmt = itemView.findViewById(R.id.textView51);
            timeCreated = itemView.findViewById(R.id.textView88);
            menu = itemView.findViewById(R.id.imageView27);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference2 = firebaseDatabase.getReference(context.getString(R.string.DB_MATCHES));

            menu.setOnClickListener(this::showPopMenu);
        }

        private void showPopMenu(View view) {

            PopupMenu menu = new PopupMenu(view.getContext(), view);
            menu.inflate(R.menu.delete_menu);
            menu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.delete) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(Constants.MY_MATCHES_MENU,
                            new ConfirmDialog.ConfirmDialogInterface() {
                                @Override
                                public void onConfirmed() {
                                    databaseReference2.child(tournamentDetails.get(getAdapterPosition()).getTournamentID())
                                            .removeValue();
                                    Long entryFee = (long) tournamentDetails.get(getAdapterPosition()).getEntryFee();
                                    FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("users").child(firebaseCurrentUser.getUid());
                                    userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);
                                            user.setAccount_balance(user.getAccount_balance() + entryFee);
                                            userDetails.setValue(user);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });

                    confirmDialog.show(((AppCompatActivity) (context)).getSupportFragmentManager(), "CONFIRM_DELETE");
                }
                return true;
            });
            menu.show();
        }
    }
}
