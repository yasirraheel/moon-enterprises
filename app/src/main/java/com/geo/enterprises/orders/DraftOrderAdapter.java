package com.geo.enterprises.orders;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.models.DraftOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying draft orders in bulk order system
 */
public class DraftOrderAdapter extends RecyclerView.Adapter<DraftOrderAdapter.ViewHolder> {

    private List<DraftOrder> draftOrders;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onDeleteClick(int position);
    }

    public DraftOrderAdapter(OnItemActionListener listener) {
        this.draftOrders = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_draft_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DraftOrder order = draftOrders.get(position);
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return draftOrders.size();
    }

    public void setOrders(List<DraftOrder> orders) {
        this.draftOrders = orders;
        notifyDataSetChanged();
    }

    public void addOrder(DraftOrder order) {
        this.draftOrders.add(order);
        notifyItemInserted(draftOrders.size() - 1);
    }

    public void removeOrder(int position) {
        if (position >= 0 && position < draftOrders.size()) {
            draftOrders.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, draftOrders.size());
        }
    }

    public void updateOrder(int position, DraftOrder order) {
        if (position >= 0 && position < draftOrders.size()) {
            draftOrders.set(position, order);
            notifyItemChanged(position);
        }
    }

    public void clear() {
        int size = draftOrders.size();
        draftOrders.clear();
        notifyItemRangeRemoved(0, size);
    }

    public List<DraftOrder> getAllOrders() {
        return new ArrayList<>(draftOrders);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llContainer;
        TextView tvRttp;
        TextView tvFirst;
        TextView tvSecond;
        TextView tvTotal;
        ImageView ivDelete;
        ProgressBar pbPlacing;
        ImageView ivSuccess;
        ImageView ivFailed;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            llContainer = itemView.findViewById(R.id.ll_item_container);
            tvRttp = itemView.findViewById(R.id.tv_rttp);
            tvFirst = itemView.findViewById(R.id.tv_first);
            tvSecond = itemView.findViewById(R.id.tv_second);
            tvTotal = itemView.findViewById(R.id.tv_total);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            pbPlacing = itemView.findViewById(R.id.pb_placing);
            ivSuccess = itemView.findViewById(R.id.iv_success);
            ivFailed = itemView.findViewById(R.id.iv_failed);
        }

        void bind(DraftOrder order, int position) {
            // Set text values
            tvRttp.setText(order.getRttp());
            tvFirst.setText(order.getFirstFormatted());
            tvSecond.setText(order.getSecondFormatted());
            tvTotal.setText(order.getTotalFormatted());

            // Reset all action views
            ivDelete.setVisibility(View.GONE);
            pbPlacing.setVisibility(View.GONE);
            ivSuccess.setVisibility(View.GONE);
            ivFailed.setVisibility(View.GONE);

            // Set background and action based on state
            switch (order.getState()) {
                case PREVIEW:
                    // Yellow background for preview
                    llContainer.setBackgroundColor(Color.parseColor("#FFF9C4"));
                    // Make preview text fully visible
                    tvRttp.setAlpha(1.0f);
                    tvFirst.setAlpha(1.0f);
                    tvSecond.setAlpha(1.0f);
                    tvTotal.setAlpha(1.0f);
                    break;

                case DRAFTED:
                    // White background with delete button
                    llContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    tvRttp.setAlpha(1.0f);
                    tvFirst.setAlpha(1.0f);
                    tvSecond.setAlpha(1.0f);
                    tvTotal.setAlpha(1.0f);
                    ivDelete.setVisibility(View.VISIBLE);
                    ivDelete.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onDeleteClick(position);
                        }
                    });
                    break;

                case PLACING:
                    // Blue background with spinner
                    llContainer.setBackgroundColor(Color.parseColor("#E3F2FD"));
                    tvRttp.setAlpha(1.0f);
                    tvFirst.setAlpha(1.0f);
                    tvSecond.setAlpha(1.0f);
                    tvTotal.setAlpha(1.0f);
                    pbPlacing.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    // Light green background with checkmark
                    llContainer.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    tvRttp.setAlpha(1.0f);
                    tvFirst.setAlpha(1.0f);
                    tvSecond.setAlpha(1.0f);
                    tvTotal.setAlpha(1.0f);
                    ivSuccess.setVisibility(View.VISIBLE);
                    break;

                case FAILED:
                    // Light red background with error icon
                    llContainer.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    tvRttp.setAlpha(1.0f);
                    tvFirst.setAlpha(1.0f);
                    tvSecond.setAlpha(1.0f);
                    tvTotal.setAlpha(1.0f);
                    ivFailed.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
