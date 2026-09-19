package com.geo.enterprises.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.utils.ShimmerView;

public class ShimmerAdapter extends RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder> {
    
    private int itemCount;
    
    public ShimmerAdapter(int itemCount) {
        this.itemCount = itemCount;
    }
    
    @NonNull
    @Override
    public ShimmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_category, parent, false);
        return new ShimmerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ShimmerViewHolder holder, int position) {
        holder.startShimmer();
    }
    
    @Override
    public int getItemCount() {
        return itemCount;
    }
    
    public static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        private ImageView categoryImage;
        private TextView categoryName;
        private TextView categoryDateTime;
        private TextView categoryTime;
        private ImageView arrowImage;
        
        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.iv_category_image);
            categoryName = itemView.findViewById(R.id.tv_category_name); 
            categoryDateTime = itemView.findViewById(R.id.tv_category_datetime);
            categoryTime = itemView.findViewById(R.id.tv_category_time);
            arrowImage = itemView.findViewById(R.id.iv_category_image); // Use same as image for arrow
        }
        
        public void startShimmer() {
            // Set placeholder text
            categoryName.setText("Loading...");
            categoryDateTime.setText("Loading date...");
            categoryTime.setText("Loading time...");
            
            // Apply shimmer effect to all views with staggered delay
            View[] views = {categoryImage, categoryName, categoryDateTime, categoryTime};
            ShimmerView.applyShimmerStaggered(views, 150); // 150ms delay between each
        }
    }
}