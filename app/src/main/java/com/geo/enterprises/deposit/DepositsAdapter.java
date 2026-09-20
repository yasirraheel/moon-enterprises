package com.geo.enterprises.deposit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.models.DepositItem;
import com.geo.enterprises.utils.DateTimeUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class DepositsAdapter extends RecyclerView.Adapter<DepositsAdapter.DepositViewHolder> {
    
    private Context context;
    private List<DepositItem> depositsList;
    
    public DepositsAdapter(Context context, List<DepositItem> depositsList) {
        this.context = context;
        this.depositsList = depositsList;
    }
    
    @NonNull
    @Override
    public DepositViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_deposit, parent, false);
        return new DepositViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DepositViewHolder holder, int position) {
        DepositItem deposit = depositsList.get(position);
        
        // Set deposit ID - REMOVED as per user request
        holder.tvDepositId.setVisibility(View.GONE);
        // holder.tvDepositId.setText(String.valueOf(deposit.getId()));
        
        // Set amount
        try {
            double amountValue = Double.parseDouble(deposit.getAmount());
            holder.tvAmount.setText("₨" + String.format("%.0f", amountValue));
        } catch (NumberFormatException e) {
            holder.tvAmount.setText("₨" + deposit.getAmount());
        }
        
        // Set payment method (bank name + account number)
        if (deposit.getPaymentMethod() != null) {
            String paymentInfo = deposit.getPaymentMethod().getBankOrAccountName();
            if (deposit.getPaymentMethod().getAccountNo() != null && 
                !deposit.getPaymentMethod().getAccountNo().isEmpty()) {
                paymentInfo += " - " + deposit.getPaymentMethod().getAccountNo();
            }
            holder.tvPaymentMethod.setText(paymentInfo);
        } else {
            holder.tvPaymentMethod.setText("N/A");
        }
        
        // Set transaction ID
        if (deposit.getTransactionId() != null && !deposit.getTransactionId().isEmpty()) {
            holder.tvTransactionId.setText(deposit.getTransactionId());
        } else {
            holder.tvTransactionId.setText("N/A");
        }
        
        // Set admin notes (show only if exists)
        if (deposit.getAdminNotes() != null && !deposit.getAdminNotes().isEmpty()) {
            holder.llAdminNotes.setVisibility(View.VISIBLE);
            holder.tvAdminNotes.setText(deposit.getAdminNotes());
        } else {
            holder.llAdminNotes.setVisibility(View.GONE);
        }
        
        // Set status with color coding
        String status = deposit.getStatus();
        holder.tvStatus.setText(status.toUpperCase());
        
        // Set status background based on deposit status
        int statusBackground;
        switch (status.toLowerCase()) {
            case "approved":
                statusBackground = R.drawable.bg_status_approved;
                break;
            case "rejected":
                statusBackground = R.drawable.bg_status_rejected;
                break;
            case "pending":
            default:
                statusBackground = R.drawable.bg_status_pending;
                break;
        }
        holder.tvStatus.setBackgroundResource(statusBackground);
        
        // Set date using DateTimeUtils to preserve server timezone
        if (deposit.getDate() != null) {
            holder.tvDate.setText(DateTimeUtils.formatDepositDateTime(deposit.getDate()));
        }
        
        // Add click listener to show details
        holder.itemView.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_history_details, null);
            
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
            
            tvTitle.setText("Deposit Details");
            tvStatus.setText(status.toUpperCase());
            if ("Pending".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            } else if ("Approved".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_approved);
            } else if ("Rejected".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
            }
            
            String formattedAmount = holder.tvAmount.getText().toString();
            tvAmount.setText(formattedAmount);
            
            if (deposit.getPaymentMethod() != null) {
                tvBank.setText(deposit.getPaymentMethod().getBankOrAccountName());
                if (deposit.getPaymentMethod().getAccountTitle() != null && !deposit.getPaymentMethod().getAccountTitle().isEmpty()) {
                    tvAccountTitle.setText(deposit.getPaymentMethod().getAccountTitle());
                } else {
                    llAccountTitle.setVisibility(View.GONE);
                }
                
                if (deposit.getPaymentMethod().getAccountNo() != null && !deposit.getPaymentMethod().getAccountNo().isEmpty()) {
                    tvAccountNo.setText(deposit.getPaymentMethod().getAccountNo());
                } else {
                    llAccountNo.setVisibility(View.GONE);
                }
            } else {
                llBank.setVisibility(View.GONE);
                llAccountTitle.setVisibility(View.GONE);
                llAccountNo.setVisibility(View.GONE);
            }
            
            if (deposit.getTransactionId() != null && !deposit.getTransactionId().isEmpty()) {
                tvTransactionId.setText(deposit.getTransactionId());
            } else {
                llTransactionId.setVisibility(View.GONE);
            }
            
            if (deposit.getAdminNotes() != null && !deposit.getAdminNotes().isEmpty()) {
                tvAdminNotes.setText(deposit.getAdminNotes());
            } else {
                llAdminNotes.setVisibility(View.GONE);
            }
            
            if (deposit.getDate() != null) {
                tvDate.setText(DateTimeUtils.formatDepositDateTime(deposit.getDate()));
            } else {
                tvDate.setText("N/A");
            }
            
            android.app.Dialog dialog = new android.app.Dialog(context);
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
        return depositsList != null ? depositsList.size() : 0;
    }
    
    static class DepositViewHolder extends RecyclerView.ViewHolder {
        TextView tvDepositId, tvAmount, tvPaymentMethod, tvTransactionId;
        TextView tvStatus, tvDate, tvAdminNotes;
        LinearLayout llAdminNotes;
        
        public DepositViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvDepositId = itemView.findViewById(R.id.tv_deposit_id);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAdminNotes = itemView.findViewById(R.id.tv_admin_notes);
            llAdminNotes = itemView.findViewById(R.id.ll_admin_notes);
        }
    }
}
