package com.players.nest.HelperClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.ModelClasses.GameFormats;
import com.players.nest.R;

import java.util.ArrayList;

import static com.players.nest.HelperClasses.MatchSettingsDialog.FORMAT_TYPE;

public class MatchSettingDialogAdapter extends RecyclerView.Adapter<MatchSettingDialogAdapter.ViewHolderClass> {

    Context context;
    String TYPE;
    RadioButton lastCheckBTn = null;
    ArrayList<GameFormats> gameFormats;
    RecyclerViewItemListener recyclerViewItemListener;

    public MatchSettingDialogAdapter(Context context, String TYPE, ArrayList<GameFormats> gameFormats, Fragment targetFragment) {
        this.context = context;
        this.TYPE = TYPE;
        this.gameFormats = gameFormats;
        this.recyclerViewItemListener = (RecyclerViewItemListener) targetFragment;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.match_setting_recycler_view_, parent, false);
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderClass holder, int position) {
        holder.heading.setText(gameFormats.get(position).getFormatHeading());
        holder.desc.setText(gameFormats.get(position).getDescription());


        //RadioButton
        holder.radioButton.setOnClickListener(view -> {
            RadioButton checked_rb = (RadioButton) view;
            if (lastCheckBTn != null) {
                lastCheckBTn.setChecked(false);
            }
            lastCheckBTn = checked_rb;

            if (TYPE.equals(FORMAT_TYPE))
                recyclerViewItemListener.formatSelected(holder.desc.getText().toString());
            else
                recyclerViewItemListener.rulesSelected(holder.desc.getText().toString());
        });
    }

    @Override
    public int getItemCount() {
        return gameFormats.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView heading, desc;
        RadioButton radioButton;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.textView74);
            desc = itemView.findViewById(R.id.textView75);
            radioButton = itemView.findViewById(R.id.radBtn);
        }
    }

    public interface RecyclerViewItemListener {
        void formatSelected(String formatDesc);

        void rulesSelected(String ruleDesc);
    }

}

