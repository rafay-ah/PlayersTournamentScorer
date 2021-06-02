package com.players.nest.AlertFragment;

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
import com.players.nest.ModelClasses.UsersPosts;

import java.util.ArrayList;
import java.util.List;

public class AlertNotify extends RecyclerView.Adapter<AlertNotify.Notif> {

    private final List<UsersPosts> dataList;
    Context mContext;
    View viewku;

    public AlertNotify(Context mContext, ArrayList<UsersPosts> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Notif onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        viewku = layoutInflater.inflate(R.layout.alert_match_finished, parent, false);
        return new Notif(viewku);
    }

    @Override
    public void onBindViewHolder(@NonNull Notif holder, int position) {
        holder.name.setText(dataList.get(position).getUser().getUsername());
        holder.msg.setText(dataList.get(position).getUser().getUsername());
        if (dataList.get(position).getUser().getProfilePic() != null) {
            if (dataList.get(position).getUser().equals(""))
                Glide.with(mContext).load(dataList.get(position).getUser().getProfilePic()).into(holder.opponentImage);
        }
        //        if (dataList.get(position).getStatus().equals("Ongoing") || dataList.get(position).getStatus().equals("Finished Airing")) {
//            holder.cvAnimeBaru.setVisibility(View.VISIBLE);
//            holder.tvJudul.setText(dataList.get(position).getJudul());
//            holder.tvEpisode.setText(dataList.get(position).getJmlepisode() + " episode");
//            holder.tvType.setText(dataList.get(position).getTipe());
//            Glide.with(holder.itemView.getContext()).load(dataList.get(position).getGambar()).into(holder.ivFoto);
//            holder.cvAnimeBaru.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent in = new Intent(holder.itemView.getContext(), EpisodeAnime.class);
//                    in.putExtra("position", position);
//                    holder.itemView.getContext().startActivity(in);
//                }
//            });
//        }else {
//            holder.cvAnimeBaru.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class Notif extends RecyclerView.ViewHolder {
        TextView name, msg;
        ImageView opponentImage;

        public Notif(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView174);
            msg = itemView.findViewById(R.id.textView175);
            opponentImage = itemView.findViewById(R.id.imageView53);
        }
    }

}

