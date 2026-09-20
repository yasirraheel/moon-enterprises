package com.geo.enterprises.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.models.Order;
import com.geo.enterprises.utils.DateTimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
    
    private Context context;
    private List<Order> ordersList;
    private boolean hideGameName; // Flag to hide game name in filtered views
    
    public OrdersAdapter(Context context, List<Order> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
        this.hideGameName = false; // Default: show game name
    }
    
    public OrdersAdapter(Context context, List<Order> ordersList, boolean hideGameName) {
        this.context = context;
        this.ordersList = ordersList;
        this.hideGameName = hideGameName;
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = ordersList.get(position);
        
        // Set order ID - REMOVED as per user request
        holder.tvOrderId.setVisibility(View.GONE);
        // holder.tvOrderId.setText(String.valueOf(order.getId()));
        
        // Show/hide game name based on flag
        if (hideGameName) {
            // Hide game name and separator, show only bond name
            holder.tvGameLabel.setVisibility(View.GONE);
            holder.tvGameName.setVisibility(View.GONE);
            holder.tvBondSeparator.setVisibility(View.GONE);
            holder.tvBondLabel.setText("Bond: "); // Keep bond label
            holder.tvBondName.setText(order.getBondName());
        } else {
            // Show both game and bond names (default behavior)
            holder.tvGameLabel.setVisibility(View.VISIBLE);
            holder.tvGameName.setVisibility(View.VISIBLE);
            holder.tvBondSeparator.setVisibility(View.VISIBLE);
            holder.tvGameName.setText(order.getGameName());
            holder.tvBondName.setText(order.getBondName());
        }
        
        // Set order details
        holder.tvRttp.setText(order.getRttp());
        holder.tvFirst.setText("₨" + order.getFirst());
        holder.tvSecond.setText("₨" + order.getSecond());
        
        // Calculate and display total amount
        double totalAmount = order.getTotalAmount();
        holder.tvTotalAmount.setText("₨" + String.format("%.0f", totalAmount));
        
        // Set status with color coding
        String status = order.getStatus();
        holder.tvStatus.setText(status.toUpperCase());
        
        // Set status background based on order status
        int statusBackground;
        switch (status.toLowerCase()) {
            case "win":
                statusBackground = R.drawable.bg_status_win; // Primary color (Blue)
                break;
            case "ok":
                statusBackground = R.drawable.bg_status_ok; // Green
                break;
            case "approved":
                statusBackground = R.drawable.bg_status_approved; // Purple (any other color)
                break;
            case "pending":
                statusBackground = R.drawable.bg_status_pending; // Warning (Orange)
                break;
            case "rejected":
                statusBackground = R.drawable.bg_status_rejected; // Danger (Red)
                break;
            default:
                statusBackground = R.drawable.bg_status_pending; // Default to warning
                break;
        }
        holder.tvStatus.setBackgroundResource(statusBackground);
        
        // Set created date with proper formatting
        if (order.getCreatedAt() != null) {
            holder.tvCreatedAt.setText(formatDateTime(order.getCreatedAt()));
        }
    }
    
    private String formatDateTime(String dateTimeString) {
        // Use special order formatting (no timezone conversion needed)
        return DateTimeUtils.formatOrderDateTime(dateTimeString, "dd MMM yyyy, hh:mm a");
    }
    
    @Override
    public int getItemCount() {
        return ordersList != null ? ordersList.size() : 0;
    }
    
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvGameLabel, tvGameName, tvBondSeparator, tvBondLabel, tvBondName;
        TextView tvRttp, tvFirst, tvSecond, tvTotalAmount, tvStatus, tvCreatedAt;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvGameLabel = itemView.findViewById(R.id.tv_game_label);
            tvGameName = itemView.findViewById(R.id.tv_game_name);
            tvBondSeparator = itemView.findViewById(R.id.tv_bond_separator);
            tvBondLabel = itemView.findViewById(R.id.tv_bond_label);
            tvBondName = itemView.findViewById(R.id.tv_bond_name);
            tvRttp = itemView.findViewById(R.id.tv_rttp);
            tvFirst = itemView.findViewById(R.id.tv_first);
            tvSecond = itemView.findViewById(R.id.tv_second);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
        }
    }
}
