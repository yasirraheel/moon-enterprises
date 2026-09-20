package com.geo.enterprises.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geo.enterprises.R;
import com.geo.enterprises.models.GameOrderSummary;

import java.util.ArrayList;
import java.util.List;

public class GameOrderSummaryAdapter extends RecyclerView.Adapter<GameOrderSummaryAdapter.ViewHolder> {

    private List<GameOrderSummary> summaryList;
    private OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(GameOrderSummary summary);
    }

    public GameOrderSummaryAdapter(OnGameClickListener listener) {
        this.summaryList = new ArrayList<>();
        this.listener = listener;
    }

    public void setSummaryList(List<GameOrderSummary> summaryList) {
        this.summaryList = summaryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameOrderSummary summary = summaryList.get(position);
        
        holder.tvCategoryName.setText(summary.getGameName());
        holder.tvOrderCount.setText(String.valueOf(summary.getOrderCount()));
        
        // Hide date/time fields, show order count badge
        holder.tvCategoryDatetime.setVisibility(View.GONE);
        holder.tvCategoryTime.setVisibility(View.GONE);
        holder.llOrderCountBadge.setVisibility(View.VISIBLE);
        holder.ivCategoryImage.setVisibility(View.VISIBLE);
        
        // Load game image
        if (summary.getGameImage() != null && !summary.getGameImage().isEmpty()) {
            String imageUrl = summary.getGameImage();
            if (!imageUrl.startsWith("http")) {
                imageUrl = com.geo.enterprises.config.AppConfig.PUBLIC_BASE_URL + "/img-category/" + imageUrl;
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_games)
                    .error(R.drawable.ic_games)
                    .into(holder.ivCategoryImage);
        } else {
            // Use default image
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_games)
                    .circleCrop()
                    .into(holder.ivCategoryImage);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGameClick(summary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        TextView tvCategoryDatetime;
        TextView tvCategoryTime;
        TextView tvOrderCount;
        LinearLayout llOrderCountBadge;
        ImageView ivCategoryImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryDatetime = itemView.findViewById(R.id.tv_category_datetime);
            tvCategoryTime = itemView.findViewById(R.id.tv_category_time);
            tvOrderCount = itemView.findViewById(R.id.tv_order_count);
            llOrderCountBadge = itemView.findViewById(R.id.ll_order_count_badge);
            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
        }
    }
}
