package com.geo.enterprises.paidservices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geo.enterprises.R;
import com.geo.enterprises.models.PaidService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class PaidServicesAdapter extends RecyclerView.Adapter<PaidServicesAdapter.ServiceViewHolder> {

    private List<PaidService> services;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(PaidService service);
    }

    public PaidServicesAdapter(OnServiceClickListener listener) {
        this.services = new ArrayList<>();
        this.listener = listener;
    }

    public void setServices(List<PaidService> services) {
        this.services = services != null ? services : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paid_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        PaidService service = services.get(position);
        holder.bind(service);
        
        // Add entrance animation
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.item_slide_in));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardService;
        private ImageView ivServiceImage;
        private TextView tvServiceTitle;
        private TextView tvServicePrice;
        private TextView tvServiceDescription;
        private MaterialButton btnGetService;
        private View llPurchasedBadge;
        private FrameLayout cardGoldenText;
        private TextView tvGoldenText;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardService = itemView.findViewById(R.id.card_service);
            ivServiceImage = itemView.findViewById(R.id.iv_service_image);
            tvServiceTitle = itemView.findViewById(R.id.tv_service_title);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
            tvServiceDescription = itemView.findViewById(R.id.tv_service_description);
            btnGetService = itemView.findViewById(R.id.btn_get_service);
            llPurchasedBadge = itemView.findViewById(R.id.ll_purchased_badge);
            cardGoldenText = itemView.findViewById(R.id.card_golden_text);
            tvGoldenText = itemView.findViewById(R.id.tv_golden_text);
        }

        public void bind(PaidService service) {
            tvServiceTitle.setText(service.getTitle());
            tvServicePrice.setText("₨ " + service.getPrice());
            tvServiceDescription.setText(service.getDescription());
            
            // Debug logging
            android.util.Log.d("PaidServiceAdapter", "=== Binding Service ===");
            android.util.Log.d("PaidServiceAdapter", "Service: " + service.getTitle());
            android.util.Log.d("PaidServiceAdapter", "Has Purchased: " + service.hasPurchased());
            android.util.Log.d("PaidServiceAdapter", "Show Buy Button: " + service.showBuyButton());
            android.util.Log.d("PaidServiceAdapter", "Golden Text: " + service.getGoldenText());
            
            // Handle purchased badge visibility
            if (service.hasPurchased()) {
                llPurchasedBadge.setVisibility(View.VISIBLE);
                android.util.Log.d("PaidServiceAdapter", "Showing purchased badge");
            } else {
                llPurchasedBadge.setVisibility(View.GONE);
                android.util.Log.d("PaidServiceAdapter", "Hiding purchased badge");
            }
            
            // Handle golden text (lucky numbers) visibility
            if (service.hasPurchased() && service.getGoldenText() != null && !service.getGoldenText().isEmpty()) {
                cardGoldenText.setVisibility(View.VISIBLE);
                tvGoldenText.setText(service.getGoldenText());
                android.util.Log.d("PaidServiceAdapter", "Showing golden text: " + service.getGoldenText());
            } else {
                cardGoldenText.setVisibility(View.GONE);
                android.util.Log.d("PaidServiceAdapter", "Hiding golden text");
            }
            
            // Handle buy button visibility
            if (service.showBuyButton() && !service.hasPurchased()) {
                btnGetService.setVisibility(View.VISIBLE);
                btnGetService.setText("Get Service - ₨ " + service.getPrice());
                android.util.Log.d("PaidServiceAdapter", "Showing buy button");
            } else {
                btnGetService.setVisibility(View.GONE);
                android.util.Log.d("PaidServiceAdapter", "Hiding buy button");
            }

            // Load image with Glide using AppConfig to fix URL
            if (service.getImage() != null && !service.getImage().isEmpty()) {
                String imageUrl = com.geo.enterprises.config.AppConfig.getServiceImageUrl(service.getImage());
                
                android.util.Log.d("PaidService", "Original URL: " + service.getImage());
                android.util.Log.d("PaidService", "Fixed URL: " + imageUrl);
                
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_premium_star)
                        .error(R.drawable.ic_premium_star)
                        .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e, Object model, 
                                    com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, 
                                    boolean isFirstResource) {
                                android.util.Log.e("PaidService", "Failed to load image: " + imageUrl);
                                if (e != null) {
                                    android.util.Log.e("PaidService", "Error: " + e.getMessage());
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, 
                                    com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, 
                                    com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                android.util.Log.d("PaidService", "Successfully loaded image: " + imageUrl);
                                return false;
                            }
                        })
                        .into(ivServiceImage);
            } else {
                ivServiceImage.setImageResource(R.drawable.ic_premium_star);
            }

            // Set click listener only on the card (not button to avoid duplicate clicks)
            View.OnClickListener clickListener = v -> {
                if (listener != null) {
                    listener.onServiceClick(service);
                }
            };
            
            cardService.setOnClickListener(clickListener);
            btnGetService.setOnClickListener(clickListener);
        }
    }
}
