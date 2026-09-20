package com.geo.enterprises.transactions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.models.Transaction;
import com.geo.enterprises.utils.DateTimeUtils;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    
    private List<Transaction> transactions;
    private OnTransactionClickListener listener;
    
    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }
    
    public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        if (transactions != null && position < transactions.size()) {
            Transaction transaction = transactions.get(position);
            holder.bind(transaction);
        }
    }
    
    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }
    
    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }
    
    public void addAll(List<Transaction> newTransactions) {
        if (transactions != null) {
            transactions.addAll(newTransactions);
            notifyDataSetChanged();
        }
    }
    
    public void clear() {
        if (transactions != null) {
            transactions.clear();
            notifyDataSetChanged();
        }
    }
    
    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivTransactionIcon;
        private TextView tvTransactionType;
        private TextView tvTransactionDescription;
        private TextView tvTransactionDate;
        private TextView tvTransactionAmount;
        private TextView tvTransactionBalance;
        
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTransactionIcon = itemView.findViewById(R.id.iv_transaction_icon);
            tvTransactionType = itemView.findViewById(R.id.tv_transaction_type);
            tvTransactionDescription = itemView.findViewById(R.id.tv_transaction_description);
            tvTransactionDate = itemView.findViewById(R.id.tv_transaction_date);
            tvTransactionAmount = itemView.findViewById(R.id.tv_transaction_amount);
            tvTransactionBalance = itemView.findViewById(R.id.tv_transaction_balance);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onTransactionClick(transactions.get(position));
                    }
                }
            });
        }
        
        public void bind(Transaction transaction) {
            // Set transaction type label
            String typeLabel = transaction.getTransactionTypeLabel();
            if (typeLabel != null && !typeLabel.trim().isEmpty()) {
                tvTransactionType.setText(typeLabel);
            } else if (transaction.getTransactionType() != null) {
                tvTransactionType.setText(capitalizeFirst(transaction.getTransactionType()));
            } else {
                tvTransactionType.setText("Transaction");
            }
            
            // Set description
            String description = transaction.getDescription();
            if (description != null && !description.trim().isEmpty()) {
                tvTransactionDescription.setText(description);
                tvTransactionDescription.setVisibility(View.VISIBLE);
            } else {
                tvTransactionDescription.setVisibility(View.GONE);
            }
            
            // Set date using DateTimeUtils to preserve server timezone
            if (transaction.getCreatedAt() != null) {
                tvTransactionDate.setText(DateTimeUtils.formatTransactionDateTime(transaction.getCreatedAt()));
            }
            
            // Set amount with color based on credit/debit
            String amountText;
            int amountColor;
            
            if (transaction.isCredit()) {
                amountText = "+ " + (transaction.getFormattedAmount() != null ? 
                    transaction.getFormattedAmount() : transaction.getAmount());
                amountColor = itemView.getContext().getResources().getColor(android.R.color.holo_green_dark);
            } else {
                amountText = "- " + (transaction.getFormattedAmount() != null ? 
                    transaction.getFormattedAmount() : transaction.getAmount());
                amountColor = itemView.getContext().getResources().getColor(android.R.color.holo_red_dark);
            }
            
            tvTransactionAmount.setText(amountText);
            tvTransactionAmount.setTextColor(amountColor);
            
            // Set remaining balance
            String balanceText = "Balance: " + (transaction.getFormattedRemainingBalance() != null ? 
                transaction.getFormattedRemainingBalance() : transaction.getRemainingBalance());
            tvTransactionBalance.setText(balanceText);
            
            // Set icon color based on transaction type
            if (transaction.isCredit()) {
                ivTransactionIcon.setColorFilter(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                ivTransactionIcon.setColorFilter(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }
        }
        
        private String capitalizeFirst(String text) {
            if (text == null || text.isEmpty()) {
                return text;
            }
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }
        

    }
}
