package com.geo.enterprises.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.chip.Chip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.dashboard.DashboardActivity;
import com.geo.enterprises.dashboard.ShimmerAdapter;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.orders.YourOrdersActivity;
import com.geo.enterprises.settings.SettingsActivity;
import com.geo.enterprises.utils.ActivityTransitionUtils;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;
import com.geo.enterprises.utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle, tvMarkAllRead;
    private RecyclerView rvNotifications;
    private RecyclerView rvShimmerLoading;
    private NotificationsAdapter adapter;
    private List<NotificationItem> notificationList;
    private List<NotificationItem> allNotificationsList; // Store all notifications
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    
    // Filter chips
    private Chip chipAll, chipPublic, chipSystem;
    private String currentFilter = "all"; // "all", "public", "system"

    // Bottom Navigation Views
    private View navHome, navNotifications, navOrders, navSettings;
    private ImageView ivNavHome, ivNavNotifications, ivNavOrders, ivNavSettings;
    private TextView tvNavHome, tvNavNotifications, tvNavOrders, tvNavSettings;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        setupBottomNavigation();
        
        // Initialize API service and preference manager
        apiService = ApiClient.getInstance().getApiService();
        preferenceManager = new PreferenceManager(this);

        // Initialize filter chips
        initializeFilterChips();

        // Handle FCM notification tap
        handleNotificationIntent(getIntent());

        loadNotifications();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvMarkAllRead = findViewById(R.id.tv_mark_all_read);
        rvNotifications = findViewById(R.id.rv_notifications);
        rvShimmerLoading = findViewById(R.id.rv_shimmer_loading);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        
        // Setup swipe refresh
        setupSwipeRefresh();
        
        // Initialize filter chips
        chipAll = findViewById(R.id.chip_all);
        chipPublic = findViewById(R.id.chip_public);
        chipSystem = findViewById(R.id.chip_system);

        // Bottom Navigation Views
        navHome = findViewById(R.id.nav_home);
        navNotifications = findViewById(R.id.nav_notifications);
        navOrders = findViewById(R.id.nav_orders);
        navSettings = findViewById(R.id.nav_settings);
        ivNavHome = findViewById(R.id.iv_nav_home);
        ivNavNotifications = findViewById(R.id.iv_nav_notifications);
        ivNavOrders = findViewById(R.id.iv_nav_orders);
        ivNavSettings = findViewById(R.id.iv_nav_settings);
        tvNavHome = findViewById(R.id.tv_nav_home);
        tvNavNotifications = findViewById(R.id.tv_nav_notifications);
        tvNavOrders = findViewById(R.id.tv_nav_orders);
        tvNavSettings = findViewById(R.id.tv_nav_settings);
        
        // Apply status bar padding to prevent overlap
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
        
        // Apply professional bottom navigation fix to prevent hiding under system nav bar
        View bottomNav = findViewById(R.id.nav_home).getParent() instanceof View ? 
            (View) findViewById(R.id.nav_home).getParent() : null;
        if (bottomNav != null) {
            WindowInsetsHelper.applyProfessionalBottomNavigationFix(this, bottomNav);
        }
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        if (tvMarkAllRead != null) {
            tvMarkAllRead.setOnClickListener(v -> markAllNotificationsAsRead());
        }
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        allNotificationsList = new ArrayList<>(); // Initialize the list to store all notifications
        adapter = new NotificationsAdapter(notificationList, new NotificationsAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(NotificationItem notification) {
                showNotificationDetailDialog(notification);
            }

            @Override
            public void onMarkAsRead(NotificationItem notification) {
                markNotificationAsRead(notification);
            }
        });
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
        
        // Setup shimmer recyclerview
        if (rvShimmerLoading != null) {
            rvShimmerLoading.setLayoutManager(new LinearLayoutManager(this));
        }
    }
    
    private void setupSwipeRefresh() {
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(
                R.color.primary_color,
                R.color.primary_dark,
                R.color.primary_light
            );
            swipeRefresh.setOnRefreshListener(() -> {
                loadNotifications();
            });
        }
    }

    private void loadNotifications() {
        if (apiService == null) {
            android.util.Log.e("NotificationsActivity", "API Service is null");
            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Service unavailable");
            showErrorState();
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        String authToken = preferenceManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            android.util.Log.e("NotificationsActivity", "Auth token is null or empty");
            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Authentication required");
            showErrorState();
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        String token = "Bearer " + authToken;
        android.util.Log.d("NotificationsActivity", "Loading notifications...");
        android.util.Log.d("NotificationsActivity", "Token: " + token);
        
        // Show shimmer loading
        showShimmerLoading(true);
        
        Call<ApiResponse<List<NotificationItem>>> call = apiService.getNotifications(token);
        
        android.util.Log.d("NotificationsActivity", "API Call URL: " + call.request().url());
        
        call.enqueue(createMaintenanceAwareCallback(new Callback<ApiResponse<List<NotificationItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NotificationItem>>> call, Response<ApiResponse<List<NotificationItem>>> response) {
                showShimmerLoading(false);
                
                android.util.Log.d("NotificationsActivity", "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<NotificationItem>> apiResponse = response.body();
                    
                    android.util.Log.d("NotificationsActivity", "Response success: " + apiResponse.isSuccess());
                    
                    if (apiResponse.isSuccess()) {
                        List<NotificationItem> apiNotifications = apiResponse.getData();
                        
                        android.util.Log.d("NotificationsActivity", "Notifications count: " + (apiNotifications != null ? apiNotifications.size() : 0));
                        
                        if (apiNotifications != null && !apiNotifications.isEmpty()) {
                            allNotificationsList.clear();
                            // Set the local unread status based on server-side read status
                            for (NotificationItem notification : apiNotifications) {
                                notification.setUnread(!notification.isRead());
                            }
                            allNotificationsList.addAll(apiNotifications);
                            
                            // Apply current filter
                            applyFilter();
                            rvNotifications.setVisibility(View.VISIBLE);
                        } else {
                            android.util.Log.w("NotificationsActivity", "Notifications list is empty");
                            showEmptyState();
                        }
                    } else {
                        String message = apiResponse.getMessage();
                        android.util.Log.e("NotificationsActivity", "API error: " + message);
                        if (message != null && !message.isEmpty()) {
                            SnackbarUtils.showDanger(findViewById(android.R.id.content), message);
                        } else {
                            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Failed to load notifications");
                        }
                        showErrorState();
                    }
                } else {
                    android.util.Log.e("NotificationsActivity", "Response not successful or body is null");
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        android.util.Log.e("NotificationsActivity", "Error body: " + errorBody);
                    } catch (Exception e) {
                        android.util.Log.e("NotificationsActivity", "Error reading error body: " + e.getMessage());
                    }
                    SnackbarUtils.showDanger(findViewById(android.R.id.content), 
                        "Failed to load notifications (Code: " + response.code() + ")");
                    showErrorState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NotificationItem>>> call, Throwable t) {
                showShimmerLoading(false);
                android.util.Log.e("NotificationsActivity", "Network error: " + t.getMessage(), t);
                SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
                showErrorState();
            }
        }));
    }
    
    private void showShimmerLoading(boolean show) {
        // Stop refresh animation when loading finishes
        if (!show && swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }
        if (show) {
            // Show shimmer loading
            if (rvShimmerLoading != null) {
                ShimmerAdapter shimmerAdapter = new ShimmerAdapter(5);
                rvShimmerLoading.setAdapter(shimmerAdapter);
                rvShimmerLoading.setVisibility(View.VISIBLE);
            }
            if (rvNotifications != null) {
                rvNotifications.setVisibility(View.GONE);
            }
        } else {
            // Hide shimmer loading
            if (rvShimmerLoading != null) {
                rvShimmerLoading.setVisibility(View.GONE);
            }
        }
    }
    
    private void showEmptyState() {
        rvNotifications.setVisibility(View.VISIBLE);
        SnackbarUtils.showInfo(findViewById(android.R.id.content), "No notifications available");
    }
    
    private void showErrorState() {
        rvNotifications.setVisibility(View.VISIBLE);
        SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
    }

    private void showNotificationDetailDialog(NotificationItem notification) {
        NotificationDetailDialog.show(this, notification, new NotificationDetailDialog.OnNotificationActionListener() {
            @Override
            public void onMarkAsRead(NotificationItem notification) {
                markNotificationAsRead(notification);
            }

            @Override
            public void onClose() {
                // Dialog closed, do nothing
            }
        });
    }

    private void markNotificationAsRead(NotificationItem notification) {
        if (apiService == null) {
            android.util.Log.e("NotificationsActivity", "API Service is null");
            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Service unavailable");
            return;
        }
        
        String authToken = preferenceManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            android.util.Log.e("NotificationsActivity", "Auth token is null or empty");
            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Authentication required");
            return;
        }
        
        String token = "Bearer " + authToken;
        android.util.Log.d("NotificationsActivity", "Marking notification as read: " + notification.getId());
        
        // Mark as read locally first for immediate UI feedback
        notification.setUnread(false);
        notification.setRead(true);
        adapter.notifyDataSetChanged();
        
        // Send API request to mark as read on server
        apiService.markNotificationAsRead(token, notification.getId()).enqueue(createMaintenanceAwareCallback(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                android.util.Log.d("NotificationsActivity", "Mark as read response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    android.util.Log.d("NotificationsActivity", "Notification marked as read successfully");
                    SnackbarUtils.showSuccess(findViewById(android.R.id.content), "Notification marked as read");
                } else {
                    android.util.Log.e("NotificationsActivity", "Failed to mark notification as read");
                    // Revert local state if server request failed
                    notification.setUnread(true);
                    notification.setRead(false);
                    adapter.notifyDataSetChanged();
                    SnackbarUtils.showDanger(findViewById(android.R.id.content), "Failed to mark notification as read");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                android.util.Log.e("NotificationsActivity", "Network error marking notification as read: " + t.getMessage(), t);
                // Revert local state if network request failed
                notification.setUnread(true);
                notification.setRead(false);
                adapter.notifyDataSetChanged();
                SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
            }
        }));
    }

    private void markAllNotificationsAsRead() {
        if (apiService == null) {
            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Service unavailable");
            return;
        }

        String authToken = preferenceManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Authentication required");
            return;
        }

        // Check if there are any unread notifications
        boolean hasUnread = false;
        for (NotificationItem notification : allNotificationsList) {
            if (notification.isUnread()) {
                hasUnread = true;
                break;
            }
        }

        if (!hasUnread) {
            SnackbarUtils.showInfo(findViewById(android.R.id.content), "All notifications are already read");
            return;
        }

        String token = "Bearer " + authToken;

        // Mark all as read locally first for immediate UI feedback
        for (NotificationItem notification : allNotificationsList) {
            notification.setUnread(false);
            notification.setRead(true);
        }
        adapter.notifyDataSetChanged();

        // Send API request
        apiService.markAllNotificationsAsRead(token).enqueue(createMaintenanceAwareCallback(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    SnackbarUtils.showSuccess(findViewById(android.R.id.content), "All notifications marked as read");
                } else {
                    // Revert on failure
                    for (NotificationItem notification : allNotificationsList) {
                        notification.setUnread(true);
                        notification.setRead(false);
                    }
                    adapter.notifyDataSetChanged();
                    SnackbarUtils.showDanger(findViewById(android.R.id.content), "Failed to mark all as read");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Revert on failure
                for (NotificationItem notification : allNotificationsList) {
                    notification.setUnread(true);
                    notification.setRead(false);
                }
                adapter.notifyDataSetChanged();
                SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
            }
        }));
    }

    private void setupBottomNavigation() {
        // Set Notifications as selected by default
        setSelectedTab(navHome, ivNavHome, tvNavHome, false);
        setSelectedTab(navNotifications, ivNavNotifications, tvNavNotifications, true);
        setSelectedTab(navOrders, ivNavOrders, tvNavOrders, false);
        setSelectedTab(navSettings, ivNavSettings, tvNavSettings, false);
        
        // Setup click listeners
        navHome.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInLeftAndFinish(this, new Intent(this, DashboardActivity.class));
        });
        
        navNotifications.setOnClickListener(v -> {
            // Already on notifications, just update selection
            setSelectedTab(navHome, ivNavHome, tvNavHome, false);
            setSelectedTab(navNotifications, ivNavNotifications, tvNavNotifications, true);
            setSelectedTab(navOrders, ivNavOrders, tvNavOrders, false);
            setSelectedTab(navSettings, ivNavSettings, tvNavSettings, false);
        });
        
        navOrders.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInRightAndFinish(this, new Intent(this, YourOrdersActivity.class));
        });
        
        navSettings.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInRightAndFinish(this, new Intent(this, SettingsActivity.class));
        });
    }
    
    private void setSelectedTab(View navItem, ImageView icon, TextView text, boolean selected) {
        if (selected) {
            navItem.setSelected(true);
            icon.setColorFilter(getResources().getColor(R.color.primary_color));
            text.setTextColor(getResources().getColor(R.color.primary_color));
        } else {
            navItem.setSelected(false);
            icon.setColorFilter(getResources().getColor(R.color.text_secondary));
            text.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityTransitionUtils.slideOutRight(this);
    }
    
    private void initializeFilterChips() {
        // Set up chip group behavior (only one can be selected at a time)
        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chipPublic.setChecked(false);
                chipSystem.setChecked(false);
                currentFilter = "all";
                applyFilter();
            }
        });
        
        chipPublic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chipAll.setChecked(false);
                chipSystem.setChecked(false);
                currentFilter = "public";
                applyFilter();
            }
        });
        
        chipSystem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chipAll.setChecked(false);
                chipPublic.setChecked(false);
                currentFilter = "system";
                applyFilter();
            }
        });
    }
    
    private void applyFilter() {
        notificationList.clear();
        
        switch (currentFilter) {
            case "public":
                for (NotificationItem notification : allNotificationsList) {
                    if (notification.isPublicNotification()) {
                        notificationList.add(notification);
                    }
                }
                break;
            case "system":
                for (NotificationItem notification : allNotificationsList) {
                    if (notification.isUserSpecificNotification()) {
                        notificationList.add(notification);
                    }
                }
                break;
            case "all":
            default:
                notificationList.addAll(allNotificationsList);
                break;
        }
        
        adapter.notifyDataSetChanged();

        // Show empty state if no notifications match the filter
        if (notificationList.isEmpty()) {
            showEmptyState();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null) {
            String notificationId = intent.getStringExtra("notification_id");
            String actionType = intent.getStringExtra("action_type");
            String type = intent.getStringExtra("type");

            if (notificationId != null) {
                android.util.Log.d("NotificationsActivity", "Opened from FCM notification - ID: " + notificationId + ", Type: " + actionType);
                // The notification will be visible in the list when loadNotifications() is called
                // Optional: Could auto-scroll to this notification or open detail dialog once loaded
            }
        }
    }
}
