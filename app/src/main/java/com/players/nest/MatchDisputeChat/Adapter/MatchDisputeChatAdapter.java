package com.players.nest.MatchDisputeChat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.players.nest.HelperClasses.HelperMethods;
import com.players.nest.MatchDisputeChat.Model.MatchDisputeModel;
import com.players.nest.MatchDisputeChat.View.PhotoOrVideoActivity;

import java.util.ArrayList;

public class MatchDisputeChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SENDER_MESSAGE = 0;
    private static final int RECEIVER_MESSAGE = 1;
    private static final int ADMIN_MESSAGE = 2;

    private final Context context;
    private final ArrayList<MatchDisputeModel> matchDisputeModelsChats;
    private final String matchID;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference chatReference;

    public MatchDisputeChatAdapter(Context context, ArrayList<MatchDisputeModel> matchDisputeModelsChats, String matchID) {
        this.context = context;
        this.matchDisputeModelsChats = matchDisputeModelsChats;
        this.matchID = matchID;

        chatReference = FirebaseDatabase.getInstance().getReference("Match_Disputes").child(matchID).child("Chat");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewType == SENDER_MESSAGE) {
            View view = inflater.inflate(R.layout.match_dispute_sender_chat_layout, parent, false);
            return new SenderViewHolder(view);
        } else if (viewType == RECEIVER_MESSAGE) {
            View view = inflater.inflate(R.layout.match_dispute_reciever_chat_layout, parent, false);
            return new ReceiverViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.match_dispute_admin_chat_layout, parent, false);
            return new AdminViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == SENDER_MESSAGE) {
            MatchDisputeModel selectedItem = matchDisputeModelsChats.get(position);
            if (selectedItem.getMessage().equals("null")) {
                Glide.with(context).load(selectedItem.getImageUrl()).into(((SenderViewHolder) holder).imageView);
                ((SenderViewHolder) holder).linearLayout.setVisibility(View.GONE);
                ((SenderViewHolder) holder).cardView.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).setClickImage(context, position);
            } else if (selectedItem.getImageUrl().equals("null")) {
                ((SenderViewHolder) holder).textViewText.setText(selectedItem.getMessage());
                ((SenderViewHolder) holder).linearLayout.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).cardView.setVisibility(View.GONE);
            }
            //((SenderViewHolder) holder).setData(position);
        } else if (holder.getItemViewType() == RECEIVER_MESSAGE) {
            MatchDisputeModel selectedItem = matchDisputeModelsChats.get(position);
            if (selectedItem.getMessage().equals("null")) {
                Glide.with(context).load(selectedItem.getImageUrl()).into(((ReceiverViewHolder) holder).imageView);
                ((ReceiverViewHolder) holder).linearLayout.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).cardView.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).setClickImage(context, position);
            } else if (selectedItem.getImageUrl().equals("null")) {
                ((ReceiverViewHolder) holder).textViewText.setText(selectedItem.getMessage());
                ((ReceiverViewHolder) holder).linearLayout.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).cardView.setVisibility(View.GONE);
            }
            //((ReceiverViewHolder) holder).setData(position);
        } else {
            MatchDisputeModel selectedItem = matchDisputeModelsChats.get(position);
            if (selectedItem.getMessage().equals("null")) {
                Glide.with(context).load(selectedItem.getImageUrl()).into(((AdminViewHolder) holder).imageView);
                ((AdminViewHolder) holder).linearLayout.setVisibility(View.GONE);
                ((AdminViewHolder) holder).cardView.setVisibility(View.VISIBLE);
                ((AdminViewHolder) holder).setClickImage(context, position);
            } else if (selectedItem.getImageUrl().equals("null")) {
                ((AdminViewHolder) holder).textViewText.setText(selectedItem.getMessage());
                ((AdminViewHolder) holder).linearLayout.setVisibility(View.VISIBLE);
                ((AdminViewHolder) holder).cardView.setVisibility(View.GONE);
            }
            //((AdminViewHolder) holder).setData(position);
        }
    }

    @Override
    public int getItemCount() {
        return matchDisputeModelsChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (matchDisputeModelsChats.get(position).getType().equals("user")) {
            Log.d("ADAPTERSSSSS", "getItemViewType: " + matchDisputeModelsChats.get(position).getSenderId().equals(firebaseUser.getUid()));
            if (matchDisputeModelsChats.get(position).getSenderId().equals(firebaseUser.getUid())) {
                return SENDER_MESSAGE;
            } else {
                Log.d("ADAPTERSSSSS", "getItemViewType: I am here");
                return RECEIVER_MESSAGE;
            }

        } else {
            return ADMIN_MESSAGE;
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewText, textViewTime, textViewSeen;
        ImageView imageView;
        CardView cardView;
        LinearLayout linearLayout;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewText = itemView.findViewById(R.id.sender_text);
            textViewTime = itemView.findViewById(R.id.sender_time);
            textViewSeen = itemView.findViewById(R.id.sender_seen);

            imageView = itemView.findViewById(R.id.sender_image);
            cardView = itemView.findViewById(R.id.sender_image_card);
            linearLayout = itemView.findViewById(R.id.sender_message_layout);
        }

        public void setData(int position) {
            textViewText.setText(matchDisputeModelsChats.get(position).getMessage());
            //setDateStamp(position, textViewTime);
        }

        private void setClickImage(Context context, int position) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoOrVideoActivity.class);
                    intent.putExtra("source", "image");
                    intent.putExtra("url", matchDisputeModelsChats.get(position).getImageUrl());
                    context.startActivity(intent);
                }
            });
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView textViewText, textViewTime;
        ImageView imageView;
        CardView cardView;
        LinearLayout linearLayout;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewText = itemView.findViewById(R.id.receiver_text);
            textViewTime = itemView.findViewById(R.id.receiver_time);

            imageView = itemView.findViewById(R.id.receiver_image);
            cardView = itemView.findViewById(R.id.receiver_image_card);
            linearLayout = itemView.findViewById(R.id.receiver_message_layout);
        }

        public void setData(int position) {
            textViewText.setText(matchDisputeModelsChats.get(position).getMessage());
            // setDateStamp(position, textViewTime);
        }

        private void setClickImage(Context context, int position) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoOrVideoActivity.class);
                    intent.putExtra("source", "image");
                    intent.putExtra("url", matchDisputeModelsChats.get(position).getImageUrl());
                    context.startActivity(intent);
                }
            });
        }

    }

    public class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView textViewText, textViewTime;
        ImageView imageView;
        CardView cardView;
        LinearLayout linearLayout;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewText = itemView.findViewById(R.id.admin_text);
            textViewTime = itemView.findViewById(R.id.admin_time);

            imageView = itemView.findViewById(R.id.admin_image);
            cardView = itemView.findViewById(R.id.admin_image_card);
            linearLayout = itemView.findViewById(R.id.admin_message_layout);
        }

        public void setData(int position) {
            textViewText.setText(matchDisputeModelsChats.get(position).getMessage());
            //setDateStamp(position, textViewTime);
        }

        private void setClickImage(Context context, int position) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoOrVideoActivity.class);
                    intent.putExtra("source", "image");
                    intent.putExtra("url", matchDisputeModelsChats.get(position).getImageUrl());
                    context.startActivity(intent);
                }
            });
        }
    }

    public void setDateStamp(int position, TextView dateTxt) {
        if (position > 0) {

            String previousMsgTime = HelperMethods.getDateFormatFromMillis(matchDisputeModelsChats.get(position - 1).getTimeCreated());
            String currentMsgTime = HelperMethods.getDateFormatFromMillis(matchDisputeModelsChats.get(position).getTimeCreated());

            if (currentMsgTime.equals(previousMsgTime)) {
                dateTxt.setVisibility(View.GONE);
            } else {
                dateTxt.setVisibility(View.VISIBLE);
                dateTxt.setText(HelperMethods.getDateFormatFromMillis(matchDisputeModelsChats.get(position).getTimeCreated()));
            }
        } else {
            dateTxt.setText(HelperMethods.getDateFormatFromMillis(matchDisputeModelsChats.get(position).getTimeCreated()));
            dateTxt.setVisibility(View.VISIBLE);
        }
    }

    private void setClickImage(String url, Context context, ImageView imageView) {
        Intent intent = new Intent(context, PhotoOrVideoActivity.class);
        intent.putExtra("source", "image");
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private void setClickImage(String url, Context context) {
        Intent intent = new Intent(context, PhotoOrVideoActivity.class);
        intent.putExtra("source", "image");
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
}
