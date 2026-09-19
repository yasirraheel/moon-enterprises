package com.geo.enterprises.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geo.enterprises.R;
import com.geo.enterprises.models.GameCategory;
import com.geo.enterprises.utils.ShimmerView;

import java.util.List;

public class GameCategoryAdapter extends RecyclerView.Adapter<GameCategoryAdapter.GameCategoryViewHolder> {
    
    private List<GameCategory> gameCategories;
    private OnGameCategoryClickListener listener;
    
    public interface OnGameCategoryClickListener {
        void onGameCategoryClick(GameCategory gameCategory);
    }
    
    public GameCategoryAdapter(List<GameCategory> gameCategories, OnGameCategoryClickListener listener) {
        this.gameCategories = gameCategories;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public GameCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_category, parent, false);
        return new GameCategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull GameCategoryViewHolder holder, int position) {
        android.util.Log.d("GameCategoryAdapter", "=== onBindViewHolder called ===");
        android.util.Log.d("GameCategoryAdapter", "Position: " + position);
        android.util.Log.d("GameCategoryAdapter", "GameCategories null: " + (gameCategories == null));
        android.util.Log.d("GameCategoryAdapter", "GameCategories size: " + (gameCategories != null ? gameCategories.size() : 0));
        
        if (gameCategories != null && position < gameCategories.size()) {
            GameCategory category = gameCategories.get(position);
            android.util.Log.d("GameCategoryAdapter", "Binding position " + position + ": " + category.getName());
            holder.bind(category);
            holder.setupClickListener(listener, category);
        } else {
            android.util.Log.w("GameCategoryAdapter", "Invalid position or null gameCategories");
        }
    }
    
    @Override
    public int getItemCount() {
        int count = gameCategories != null ? gameCategories.size() : 0;
        android.util.Log.d("GameCategoryAdapter", "getItemCount: " + count);
        return count;
    }
    
    public void updateGameCategories(List<GameCategory> newCategories) {
        this.gameCategories = newCategories;
        notifyDataSetChanged();
    }
    
    public void addAll(List<GameCategory> newCategories) {
        if (gameCategories != null) {
            gameCategories.addAll(newCategories);
            notifyDataSetChanged();
        }
    }
    
    public void clear() {
        if (gameCategories != null) {
            gameCategories.clear();
            notifyDataSetChanged();
        }
    }
    
    
    static class GameCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;
        TextView tvCategoryDateTime;
        TextView tvCategoryTime;
        
        public GameCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryDateTime = itemView.findViewById(R.id.tv_category_datetime);
            tvCategoryTime = itemView.findViewById(R.id.tv_category_time);
            
            android.util.Log.d("GameCategoryAdapter", "ViewHolder created");
        }
        
        public void setupClickListener(OnGameCategoryClickListener listener, GameCategory category) {
            android.util.Log.d("GameCategoryAdapter", "Setting up click listener for: " + category.getName());
            
            itemView.setOnClickListener(v -> {
                android.util.Log.d("GameCategoryAdapter", "Card clicked! Category: " + category.getName());
                if (listener != null) {
                    android.util.Log.d("GameCategoryAdapter", "Calling listener for: " + category.getName());
                    listener.onGameCategoryClick(category);
                } else {
                    android.util.Log.e("GameCategoryAdapter", "Listener is null!");
                }
            });
        }
        
        public void bind(GameCategory category) {
            tvCategoryName.setText(category.getName());
            
            // Set date and time
            tvCategoryDateTime.setText("Date: " + category.getDateOnly());
            tvCategoryTime.setText("Time: " + category.getTimeOnly());
            
            // Load category image
            if (category.getImage() != null && !category.getImage().isEmpty()) {
                String imageUrl = category.getImage();
                
                // If it's already a complete URL, use it as is
                if (!imageUrl.startsWith("http")) {
                    imageUrl = com.geo.enterprises.config.AppConfig.PUBLIC_BASE_URL + "/img-category/" + imageUrl;
                }
                
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_games)
                        .error(R.drawable.ic_games)
                        .into(ivCategoryImage);
            } else {
                ivCategoryImage.setImageResource(R.drawable.ic_games);
            }
        }
    }
}
