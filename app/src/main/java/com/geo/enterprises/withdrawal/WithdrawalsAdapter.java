package com.geo.enterprises.withdrawal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.models.Withdrawal;
import com.geo.enterprises.utils.DateTimeUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class WithdrawalsAdapter extends RecyclerView.Adapter<WithdrawalsAdapter.WithdrawalViewHolder> {

    private List<Withdrawal> withdrawals;

    public WithdrawalsAdapter(List<Withdrawal> withdrawals) {
        this.withdrawals = withdrawals;
    }

    @NonNull
    @Override
    public WithdrawalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdrawal, parent, false);
        return new WithdrawalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawalViewHolder holder, int position) {
        Withdrawal withdrawal = withdrawals.get(position);
        
        // Set withdrawal ID - REMOVED as per user request
        holder.tvWithdrawalId.setVisibility(View.GONE);
        // holder.tvWithdrawalId.setText(String.valueOf(withdrawal.getId()));
        
        // Set amount
        try {
            double amount = Double.parseDouble(withdrawal.getAmount());
            holder.tvAmount.setText("₨ " + String.format("%.0f", amount));
        } catch (NumberFormatException e) {
            holder.tvAmount.setText("₨ " + withdrawal.getAmount());
        }
        
        // Set account number
        holder.tvAccountNumber.setText(withdrawal.getAccountNumber());
        
        // Set status with appropriate background
        String status = withdrawal.getStatus();
        holder.tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
        
        if ("pending".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
        } else if ("approved".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_approved);
        } else if ("rejected".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
        }
        
        // Show transaction ID if available (for approved withdrawals)
        if (withdrawal.getTransactionId() != null && !withdrawal.getTransactionId().isEmpty()) {
            holder.llTransactionId.setVisibility(View.VISIBLE);
            holder.tvTransactionId.setText(withdrawal.getTransactionId());
        } else {
            holder.llTransactionId.setVisibility(View.GONE);
        }
        
        // Show admin notes if available
        if (withdrawal.getAdminNotes() != null && !withdrawal.getAdminNotes().isEmpty()) {
            holder.llAdminNotes.setVisibility(View.VISIBLE);
            holder.tvAdminNotes.setText(withdrawal.getAdminNotes());
        } else {
            holder.llAdminNotes.setVisibility(View.GONE);
        }
        
        // Set date using DateTimeUtils to preserve server timezone
        holder.tvDate.setText(DateTimeUtils.formatWithdrawalDateTime(withdrawal.getDate()));
        
        // Add click listener to show details
        holder.itemView.setOnClickListener(v -> {
            String displayStatus = status.substring(0, 1).toUpperCase() + status.substring(1);
            String formattedAmount = holder.tvAmount.getText().toString();
            
            View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_history_details, null);
            
            TextView tvTitle = dialogView.findViewById(R.id.tv_history_dialog_title);
            TextView tvStatus = dialogView.findViewById(R.id.tv_history_status);
            TextView tvAmount = dialogView.findViewById(R.id.tv_history_amount);
            TextView tvBank = dialogView.findViewById(R.id.tv_history_bank);
            TextView tvAccountTitle = dialogView.findViewById(R.id.tv_history_account_title);
            TextView tvAccountNo = dialogView.findViewById(R.id.tv_history_account_no);
            TextView tvTransactionId = dialogView.findViewById(R.id.tv_history_transaction_id);
            TextView tvAdminNotes = dialogView.findViewById(R.id.tv_history_admin_notes);
            TextView tvDate = dialogView.findViewById(R.id.tv_history_date);
            
            LinearLayout llBank = dialogView.findViewById(R.id.ll_history_bank);
            LinearLayout llAccountTitle = dialogView.findViewById(R.id.ll_history_account_title);
            LinearLayout llAccountNo = dialogView.findViewById(R.id.ll_history_account_no);
            LinearLayout llTransactionId = dialogView.findViewById(R.id.ll_history_transaction_id);
            LinearLayout llAdminNotes = dialogView.findViewById(R.id.ll_history_admin_notes);
            com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btn_history_dialog_close);
            
            tvTitle.setText("Withdrawal Details");
            tvStatus.setText(displayStatus.toUpperCase());
            if ("Pending".equalsIgnoreCase(displayStatus)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            } else if ("Approved".equalsIgnoreCase(displayStatus)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_approved);
            } else if ("Rejected".equalsIgnoreCase(displayStatus)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
            }
            
            tvAmount.setText(formattedAmount);
            
            if (withdrawal.getBankName() != null && !withdrawal.getBankName().isEmpty()) {
                tvBank.setText(withdrawal.getBankName());
            } else {
                llBank.setVisibility(View.GONE);
            }
            
            if (withdrawal.getAccountTitle() != null && !withdrawal.getAccountTitle().isEmpty()) {
                tvAccountTitle.setText(withdrawal.getAccountTitle());
            } else {
                llAccountTitle.setVisibility(View.GONE);
            }
            
            if (withdrawal.getAccountNumber() != null && !withdrawal.getAccountNumber().isEmpty()) {
                tvAccountNo.setText(withdrawal.getAccountNumber());
            } else {
                llAccountNo.setVisibility(View.GONE);
            }
            
            if (withdrawal.getTransactionId() != null && !withdrawal.getTransactionId().isEmpty()) {
                tvTransactionId.setText(withdrawal.getTransactionId());
            } else {
                llTransactionId.setVisibility(View.GONE);
            }
            
            if (withdrawal.getAdminNotes() != null && !withdrawal.getAdminNotes().isEmpty()) {
                tvAdminNotes.setText(withdrawal.getAdminNotes());
            } else {
                llAdminNotes.setVisibility(View.GONE);
            }
            
            tvDate.setText(DateTimeUtils.formatWithdrawalDateTime(withdrawal.getDate()));
            
            android.app.Dialog dialog = new android.app.Dialog(holder.itemView.getContext());
            dialog.setContentView(dialogView);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            
            btnClose.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return withdrawals != null ? withdrawals.size() : 0;
    }

    public void updateData(List<Withdrawal> newWithdrawals) {
        this.withdrawals = newWithdrawals;
        notifyDataSetChanged();
    }

    static class WithdrawalViewHolder extends RecyclerView.ViewHolder {
        TextView tvWithdrawalId;
        TextView tvStatus;
        TextView tvAmount;
        TextView tvAccountNumber;
        LinearLayout llTransactionId;
        TextView tvTransactionId;
        LinearLayout llAdminNotes;
        TextView tvAdminNotes;
        TextView tvDate;

        public WithdrawalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWithdrawalId = itemView.findViewById(R.id.tv_withdrawal_id);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvAccountNumber = itemView.findViewById(R.id.tv_account_number);
            llTransactionId = itemView.findViewById(R.id.ll_transaction_id);
            tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
            llAdminNotes = itemView.findViewById(R.id.ll_admin_notes);
            tvAdminNotes = itemView.findViewById(R.id.tv_admin_notes);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
