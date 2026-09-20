package com.geo.enterprises.subcategory;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geo.enterprises.R;
import com.geo.enterprises.models.Subcategory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder> {
    
    private List<Subcategory> subcategories;
    private OnSubcategoryClickListener listener;
    private String gameImage;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;
    private boolean isTimerRunning = false;
    
    public interface OnSubcategoryClickListener {
        void onSubcategoryClick(Subcategory subcategory);
    }
    
    public SubcategoryAdapter(List<Subcategory> subcategories, OnSubcategoryClickListener listener) {
        this.subcategories = subcategories;
        this.listener = listener;
        
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (getItemCount() > 0) {
                    notifyItemRangeChanged(0, getItemCount(), "TIMER_UPDATE");
                }
                timerHandler.postDelayed(this, 1000);
            }
        };
    }
    
    public void setGameImage(String gameImage) {
        this.gameImage = gameImage;
    }
    
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        startTimer();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        stopTimer();
    }
    
    private void startTimer() {
        if (!isTimerRunning && timerRunnable != null) {
            timerHandler.post(timerRunnable);
            isTimerRunning = true;
        }
    }
    
    private void stopTimer() {
        if (isTimerRunning && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
            isTimerRunning = false;
        }
    }

    @NonNull
    @Override
    public SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcategory, parent, false);
        return new SubcategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SubcategoryViewHolder holder, int position) {
        if (subcategories != null && position < subcategories.size()) {
            Subcategory subcategory = subcategories.get(position);
            holder.bind(subcategory);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull SubcategoryViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            // Check for timer update payload
            for (Object payload : payloads) {
                if ("TIMER_UPDATE".equals(payload)) {
                    holder.updateTimer();
                    return;
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }
    
    @Override
    public int getItemCount() {
        return subcategories != null ? subcategories.size() : 0;
    }
    
    public void updateSubcategories(List<Subcategory> newSubcategories) {
        this.subcategories = newSubcategories;
        notifyDataSetChanged();
    }
    
    public void addAll(List<Subcategory> newSubcategories) {
        if (subcategories != null) {
            subcategories.addAll(newSubcategories);
            notifyDataSetChanged();
        }
    }
    
    public void clear() {
        if (subcategories != null) {
            subcategories.clear();
            notifyDataSetChanged();
        }
    }
    
    class SubcategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubcategoryName;
        // private TextView tvSubcategorySchedule; // Removed as per request
        private TextView tvSubcategoryStartDatetime;
        private TextView tvSubcategoryCloseDatetime;
        private TextView tvSubcategoryStatus;
        private LinearLayout layoutTimerContainer;
        private TextView tvCountdownTimer;
        private ImageView ivTimerIcon;
        private ImageView ivSubcategoryIcon;
        private Subcategory currentSubcategory;
        
        public SubcategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubcategoryName = itemView.findViewById(R.id.tv_subcategory_name);
            // tvSubcategorySchedule = itemView.findViewById(R.id.tv_subcategory_schedule); // Removed
            tvSubcategoryStartDatetime = itemView.findViewById(R.id.tv_subcategory_start_datetime);
            tvSubcategoryCloseDatetime = itemView.findViewById(R.id.tv_subcategory_close_datetime);
            tvSubcategoryStatus = itemView.findViewById(R.id.tv_subcategory_status);
            layoutTimerContainer = itemView.findViewById(R.id.layout_timer_container);
            tvCountdownTimer = itemView.findViewById(R.id.tv_countdown_timer);
            ivTimerIcon = itemView.findViewById(R.id.iv_timer_icon);
            ivSubcategoryIcon = itemView.findViewById(R.id.iv_subcategory_icon);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onSubcategoryClick(subcategories.get(position));
                    }
                }
            });
        }
        
        public void bind(Subcategory subcategory) {
            this.currentSubcategory = subcategory;
            tvSubcategoryName.setText(subcategory.getName());
            
            // Load image using Glide
            if (gameImage != null && !gameImage.isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(gameImage)
                    .circleCrop()
                    .placeholder(R.drawable.ic_games)
                    .error(R.drawable.ic_games)
                    .into(ivSubcategoryIcon);
            } else {
                ivSubcategoryIcon.setImageResource(R.drawable.ic_games);
                // Apply padding for the default icon to look good in the circle
                int padding = (int) (8 * itemView.getContext().getResources().getDisplayMetrics().density);
                ivSubcategoryIcon.setPadding(padding, padding, padding, padding);
                ivSubcategoryIcon.setColorFilter(androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.primary_color));
            }
            
            // Show subcategory description or mode
            // String infoText = "";
            // if (subcategory.getDescription() != null && !subcategory.getDescription().trim().isEmpty()) {
            //     infoText = subcategory.getDescription();
            // } else {
            //     infoText = "Click to view details";
            // }
            // tvSubcategorySchedule.setText(infoText);
            
            // Show start date and time
            String startDateTime = "Start: ";
            if (subcategory.getStartDate() != null && !subcategory.getStartDate().trim().isEmpty()) {
                startDateTime += subcategory.getStartDate();
                if (subcategory.getStartTime() != null && !subcategory.getStartTime().trim().isEmpty()) {
                    startDateTime += " " + subcategory.getStartTime();
                }
            } else {
                startDateTime += "N/A";
            }
            tvSubcategoryStartDatetime.setText(startDateTime);
            
            // Show close date and time
            String closeDateTime = "Close: ";
            if (subcategory.getCloseDate() != null && !subcategory.getCloseDate().trim().isEmpty()) {
                closeDateTime += subcategory.getCloseDate();
                if (subcategory.getCloseTime() != null && !subcategory.getCloseTime().trim().isEmpty()) {
                    closeDateTime += " " + subcategory.getCloseTime();
                }
            } else {
                closeDateTime += "N/A";
            }
            tvSubcategoryCloseDatetime.setText(closeDateTime);
            
            // Show status (can be updated by timer logic)
            String statusText = "Status: " + (subcategory.getMode() != null ? subcategory.getMode() : "Unknown");
            tvSubcategoryStatus.setText(statusText);
            
            // Initial timer update
            updateTimer();
        }
        
        public void updateTimer() {
            if (currentSubcategory == null) return;
            
            // Check manual mode override
            if (currentSubcategory.getMode() != null && "off".equalsIgnoreCase(currentSubcategory.getMode())) {
                layoutTimerContainer.setVisibility(View.GONE);
                tvSubcategoryStatus.setVisibility(View.VISIBLE);
                tvSubcategoryStatus.setText("Status: Inactive");
                tvSubcategoryStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.error_color));
                return;
            }
            
            try {
                // Parse dates
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                
                String startStr = currentSubcategory.getStartDate() + " " + currentSubcategory.getStartTime();
                String closeStr = currentSubcategory.getCloseDate() + " " + currentSubcategory.getCloseTime();
                
                Date startDate = dateFormat.parse(startStr);
                Date closeDate = dateFormat.parse(closeStr);
                Date currentDate = new Date();
                
                if (currentDate.before(startDate)) {
                    // Not started yet
                    layoutTimerContainer.setVisibility(View.GONE);
                    tvSubcategoryStatus.setVisibility(View.VISIBLE);
                    tvSubcategoryStatus.setText("Status: Scheduled");
                    tvSubcategoryStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                } else if (currentDate.after(closeDate)) {
                    // Ended
                    layoutTimerContainer.setVisibility(View.VISIBLE);
                    layoutTimerContainer.setBackgroundResource(R.drawable.bg_timer_ended);
                    tvCountdownTimer.setText("CLOSED");
                    tvCountdownTimer.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.error_color));
                    ivTimerIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.error_color));
                    
                    tvSubcategoryStatus.setVisibility(View.GONE); // Hide status when closed as per request
                } else {
                    // Active - Show Countdown
                    layoutTimerContainer.setVisibility(View.VISIBLE);
                    layoutTimerContainer.setBackgroundResource(R.drawable.bg_timer_active);
                    
                    long diff = closeDate.getTime() - currentDate.getTime();
                    
                    long hours = TimeUnit.MILLISECONDS.toHours(diff);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
                    
                    String timeStr;
                    if (hours > 24) {
                        long days = hours / 24;
                        long remainingHours = hours % 24;
                        timeStr = String.format(Locale.getDefault(), "%02dd : %02dh : %02dm : %02ds", days, remainingHours, minutes, seconds);
                    } else {
                        timeStr = String.format(Locale.getDefault(), "%02dh : %02dm : %02ds", hours, minutes, seconds);
                    }
                    
                    tvCountdownTimer.setText(timeStr);
                    tvCountdownTimer.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary_color));
                    ivTimerIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.primary_color));
                    
                    tvSubcategoryStatus.setVisibility(View.VISIBLE);
                    tvSubcategoryStatus.setText("Status: ACTIVE");
                    tvSubcategoryStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success_color));
                    
                    // Optional: Animate blinking colon or text color if urgency
                    if (hours < 1) {
                        tvCountdownTimer.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.warning_dark));
                        ivTimerIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.warning_dark));
                    }
                }
                
            } catch (Exception e) {
                // Fallback on error
                layoutTimerContainer.setVisibility(View.GONE);
                tvSubcategoryStatus.setText("Status: " + (currentSubcategory.getMode() != null ? currentSubcategory.getMode() : "Unknown"));
                tvSubcategoryStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
            }
        }
    }
}
