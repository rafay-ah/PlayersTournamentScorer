package com.players.nest.ProfileFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.R;
import com.players.nest.ModelClasses.PreviousTransactionsRecord;

import java.util.ArrayList;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.myViewHolderClass> {

    ArrayList<PreviousTransactionsRecord> mList;

    WalletTransactionAdapter(ArrayList<PreviousTransactionsRecord> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public myViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.wallet_recycler_view, parent, false);
        return new myViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolderClass holder, int position) {
        holder.transactionID.setText(mList.get(position).getTransactionID());
        holder.amount.setText("$" + mList.get(position).getAmount());
        holder.date.setText(mList.get(position).getDate());
        holder.transactionState.setText(mList.get(position).getTransactionState());

        if (mList.get(position).getTransactionState().equals(PreviousTransactionsRecord.APPROVED)) {
            holder.transactionState.setBackgroundResource(R.drawable.transaction_state_bg);
        } else {
            holder.transactionState.setBackgroundResource(R.drawable.transaction_pending_bg);
        }
        holder.type.setText(mList.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class myViewHolderClass extends RecyclerView.ViewHolder {

        TextView transactionID, date, amount, type, transactionState;

        public myViewHolderClass(@NonNull View itemView) {
            super(itemView);

            transactionID = itemView.findViewById(R.id.textView14);
            amount = itemView.findViewById(R.id.textView15);
            date = itemView.findViewById(R.id.textView17);
            type = itemView.findViewById(R.id.textView16);
            transactionState = itemView.findViewById(R.id.textView18);
        }
    }

}
