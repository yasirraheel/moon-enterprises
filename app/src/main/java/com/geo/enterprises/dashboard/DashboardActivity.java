package com.geo.enterprises.dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.geo.enterprises.utils.LoadingDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.auth.LoginActivity;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.AppSettings;
import com.geo.enterprises.models.GameCategory;
import com.geo.enterprises.models.User;
import com.geo.enterprises.utils.ActivityTransitionUtils;
import com.geo.enterprises.utils.AppUpdateManager;
import com.geo.enterprises.utils.ConfirmationDialog;
import com.geo.enterprises.utils.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;
import com.geo.enterprises.utils.BaseActivity;
import com.geo.enterprises.utils.MaintenanceModeHandler;
import com.geo.enterprises.settings.SettingsActivity;
import com.geo.enterprises.notifications.NotificationsActivity;
import com.geo.enterprises.orders.YourOrdersActivity;
import com.geo.enterprises.notifications.NotificationCount;
import com.geo.enterprises.subcategory.SubcategoryActivity;
import com.geo.enterprises.fcm.FcmTokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private TextView tvWelcome, tvBalance, tvPhone;
    private ImageView ivLogout, ivUserAvatar, ivMenu;
    private MaterialButton btnDeposit;
    private MaterialButton btnWithdraw;
    private MaterialButton btnPremiumService;
    private MaterialButton btnVideoGuide;
    private MaterialButton btnBecomeDealer;
    private MaterialButton btnJoinWhatsapp;
    private TextView tvAdminBadge;
    private ImageView ivResetCommission;
    private PreferenceManager preferenceManager;
    private User currentUser;
    private AppSettings appSettings;
    private ApiService apiService;
    private AppUpdateManager appUpdateManager;
    private LoadingDialog loadingDialog;
    
    // Navigation Drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View drawerHeaderView;
    private ImageView ivDrawerAvatar;
    private TextView tvDrawerUserName, tvDrawerUserPhone, tvDrawerBalance;
    
    // Top bar branding views
    private TextView tvAppTitle;
    
    // Game categories
    private RecyclerView rvGameCategories;
    private GameCategoryAdapter gameCategoryAdapter;
    private List<GameCategory> gameCategoryList;
    private View cardEmptyGames;
    private RecyclerView rvShimmerLoading;
    
    // Bottom Navigation Views
    private View navHome, navNotifications, navOrders, navSettings;
    private ImageView ivNavHome, ivNavNotifications, ivNavOrders, ivNavSettings;
    private TextView tvNavHome, tvNavNotifications, tvNavOrders, tvNavSettings;
    private TextView tvNotificationBadge;
    
    // Swipe Refresh
    private SwipeRefreshLayout swipeRefresh;

    // ===== Social Proof Cards =====
    private TextView tvActiveUsers;
    private TextView tvOnlineTitle;
    private TextView tvPayoutAlert;
    private ImageView ivTransactionType;
    private FrameLayout layoutTransactionIconBg;
    private View layoutPayoutTextContainer;
    private android.os.Handler socialProofHandler;
    private Runnable activeUsersRunnable;
    private Runnable payoutAlertRunnable;
    private int activeUserCount = 448;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_drawer);

        // Log notification click debug info
        android.util.Log.d("NotificationClick", "=== DashboardActivity onCreate ===");
        android.util.Log.d("NotificationClick", "Intent: " + (getIntent() != null ? getIntent().toString() : "null"));
        android.util.Log.d("NotificationClick", "Intent action: " + (getIntent() != null ? getIntent().getAction() : "null"));
        android.util.Log.d("NotificationClick", "Intent component: " + (getIntent() != null && getIntent().getComponent() != null ? getIntent().getComponent().getClassName() : "null"));

        if (getIntent() != null) {
            android.util.Log.d("NotificationClick", "Intent has extras: " + (getIntent().getExtras() != null));
            if (getIntent().getExtras() != null) {
                android.util.Log.d("NotificationClick", "Intent extras keys: " + getIntent().getExtras().keySet().toString());
                android.util.Log.d("NotificationClick", "Intent extras: " + getIntent().getExtras().toString());

                if (getIntent().hasExtra("FROM_NOTIFICATION")) {
                    android.util.Log.d("NotificationClick", "FROM_NOTIFICATION: " + getIntent().getBooleanExtra("FROM_NOTIFICATION", false));
                }
                if (getIntent().hasExtra("notification_id")) {
                    android.util.Log.d("NotificationClick", "notification_id: " + getIntent().getStringExtra("notification_id"));
                }
            } else {
                android.util.Log.w("NotificationClick", "Intent extras is NULL - notification data was lost!");
            }
        }

        // Initialize managers and services first
        preferenceManager = new PreferenceManager(this);
        apiService = ApiClient.getInstance().getApiService();
        appUpdateManager = new AppUpdateManager(this);
        currentUser = preferenceManager.getUserData();
        appSettings = preferenceManager.getAppSettings();

        initializeViews();
        applyUrduFont();
        setupNavigationDrawer();
        setupToolbar();
        
                if (currentUser != null) {
                    displayUserInfo();
                    
                    // Update top bar branding if settings are available
                    if (appSettings != null) {
                        updateTopBarBranding(appSettings);
                    }
                    
                    // Fetch app settings if not available
                    if (appSettings == null) {
                        fetchAppSettings();
                    }
                    
                    // Check if user just logged in or registered
                    checkAndShowWelcomeMessage();
                    
                    // Check for force updates on app load
                    checkForForceUpdates();
                } else {
                    // User data not found, redirect to login
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Update the activity's intent

        // Log notification click when activity is already running
        android.util.Log.d("NotificationClick", "=== DashboardActivity onNewIntent ===");
        android.util.Log.d("NotificationClick", "Intent action: " + (intent != null ? intent.getAction() : "null"));
        if (intent != null && intent.getExtras() != null) {
            android.util.Log.d("NotificationClick", "Intent extras: " + intent.getExtras().toString());
            if (intent.hasExtra("FROM_NOTIFICATION")) {
                android.util.Log.d("NotificationClick", "FROM_NOTIFICATION: " + intent.getBooleanExtra("FROM_NOTIFICATION", false));
                android.util.Log.d("NotificationClick", "App was brought to foreground from notification");
            }
            if (intent.hasExtra("notification_id")) {
                android.util.Log.d("NotificationClick", "notification_id: " + intent.getStringExtra("notification_id"));
            }
        } else {
            android.util.Log.d("NotificationClick", "No intent extras found in onNewIntent");
        }
    }

    private void initializeViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvBalance = findViewById(R.id.tv_balance);
        tvPhone = findViewById(R.id.tv_phone);
        ivLogout = findViewById(R.id.iv_logout);
        ivUserAvatar = findViewById(R.id.iv_user_avatar);
        ivMenu = findViewById(R.id.iv_menu);
        btnDeposit = findViewById(R.id.btn_deposit);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnPremiumService = findViewById(R.id.btn_premium_service);
        btnVideoGuide = findViewById(R.id.btn_video_guide);
        btnBecomeDealer = findViewById(R.id.btn_become_dealer);
        btnJoinWhatsapp = findViewById(R.id.btn_join_whatsapp);
        tvAdminBadge = findViewById(R.id.tv_admin_badge);
        ivResetCommission = findViewById(R.id.iv_reset_commission);
        
        // Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        
        // Top bar branding views
        tvAppTitle = findViewById(R.id.tv_app_title);
        
        // Game categories RecyclerView
        rvGameCategories = findViewById(R.id.rv_game_categories);
        rvShimmerLoading = findViewById(R.id.rv_shimmer_loading);
        android.util.Log.d("GameCategories", "RecyclerView found: " + (rvGameCategories != null));
        android.util.Log.d("GameCategories", "Shimmer RecyclerView found: " + (rvShimmerLoading != null));
        
        if (rvGameCategories != null) {
            rvGameCategories.setLayoutManager(new LinearLayoutManager(this));
            rvGameCategories.setNestedScrollingEnabled(false);
            rvGameCategories.setHasFixedSize(false);
            rvGameCategories.setFocusable(false);
            rvGameCategories.setOverScrollMode(View.OVER_SCROLL_NEVER);
            android.util.Log.d("GameCategories", "LayoutManager set on RecyclerView");
        } else {
            android.util.Log.e("GameCategories", "RecyclerView is NULL!");
        }
        
        if (rvShimmerLoading != null) {
            rvShimmerLoading.setLayoutManager(new LinearLayoutManager(this));
            rvShimmerLoading.setNestedScrollingEnabled(false);
            rvShimmerLoading.setHasFixedSize(false);
            rvShimmerLoading.setFocusable(false);
            rvShimmerLoading.setOverScrollMode(View.OVER_SCROLL_NEVER);
            android.util.Log.d("GameCategories", "Shimmer LayoutManager set");
        }
        
        cardEmptyGames = findViewById(R.id.card_empty_games);
        
        // Setup game categories RecyclerView
        setupGameCategoriesRecyclerView();
        
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
        tvNotificationBadge = findViewById(R.id.tv_notification_badge);
        
        // Swipe Refresh Layout
        swipeRefresh = findViewById(R.id.swipe_refresh);
        setupSwipeRefresh();
        
        // Setup click listeners
        ivLogout.setOnClickListener(v -> showLogoutConfirmation());
        ivMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        
        setupBottomNavigation();
        
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

        // Social Proof Cards (above WhatsApp button)
        tvActiveUsers = findViewById(R.id.tv_active_users);
        tvOnlineTitle = findViewById(R.id.tv_online_title);
        tvPayoutAlert = findViewById(R.id.tv_payout_alert);
        ivTransactionType = findViewById(R.id.iv_transaction_type);
        layoutTransactionIconBg = findViewById(R.id.layout_transaction_icon_bg);
        layoutPayoutTextContainer = findViewById(R.id.layout_payout_text_container);
        startSocialProofUpdates();
    }
    
    private void setupNavigationDrawer() {
        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(this);
        
        // Set scrim color for drawer overlay
        drawerLayout.setScrimColor(0x80000000); // Semi-transparent black
        
        // Add drawer listener for smooth animations
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Smooth slide animation with content translation
                LinearLayout mainContent = findViewById(R.id.main_content);
                if (mainContent != null) {
                    float moveFactor = drawerView.getWidth() * slideOffset * 0.4f;
                    mainContent.setTranslationX(moveFactor);
                    mainContent.setScaleX(1 - (slideOffset * 0.05f));
                    mainContent.setScaleY(1 - (slideOffset * 0.05f));
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Drawer fully opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Drawer fully closed - reset transformations
                LinearLayout mainContent = findViewById(R.id.main_content);
                if (mainContent != null) {
                    mainContent.setTranslationX(0);
                    mainContent.setScaleX(1f);
                    mainContent.setScaleY(1f);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Drawer state changed
            }
        });
        
        // Get drawer header view
        drawerHeaderView = navigationView.getHeaderView(0);
        if (drawerHeaderView != null) {
            int statusBarHeight = WindowInsetsHelper.getStatusBarHeight(this);
            int baseTopPadding = (int) (18 * getResources().getDisplayMetrics().density);
            if (statusBarHeight > 0) {
                drawerHeaderView.setPadding(
                    drawerHeaderView.getPaddingLeft(),
                    baseTopPadding + statusBarHeight,
                    drawerHeaderView.getPaddingRight(),
                    drawerHeaderView.getPaddingBottom()
                );
            }
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, drawerHeaderView);

            ivDrawerAvatar = drawerHeaderView.findViewById(R.id.iv_drawer_avatar);
            tvDrawerUserName = drawerHeaderView.findViewById(R.id.tv_drawer_user_name);
            tvDrawerUserPhone = drawerHeaderView.findViewById(R.id.tv_drawer_user_phone);
            tvDrawerBalance = drawerHeaderView.findViewById(R.id.tv_drawer_balance);
        }
        
        // Update drawer header with user info
        updateDrawerHeader();
        
        // Set Home as selected by default
        navigationView.setCheckedItem(R.id.nav_home);
    }
    
    private void updateDrawerHeader() {
        if (currentUser != null) {
            tvDrawerUserName.setText(currentUser.getFullName());
            tvDrawerUserPhone.setText(currentUser.getPhone());
            
            String balanceText = formatCurrency(currentUser.getBalance());
            tvDrawerBalance.setText(balanceText);
            
            // Load avatar
            if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                String imageUrl = com.geo.enterprises.config.AppConfig.getAvatarUrl(currentUser.getAvatar());
                
                Glide.with(this)
                    .load(imageUrl)
                    .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person))
                    .into(ivDrawerAvatar);
            } else {
                ivDrawerAvatar.setImageResource(R.drawable.ic_person);
            }
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_home) {
            // Already on home, just close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(this, YourOrdersActivity.class));
        } else if (id == R.id.nav_transactions) {
            startActivity(new Intent(this, com.geo.enterprises.transactions.TransactionsActivity.class));
        } else if (id == R.id.nav_deposits) {
            startActivity(new Intent(this, com.geo.enterprises.deposit.DepositsActivity.class));
        } else if (id == R.id.nav_withdrawals) {
            startActivity(new Intent(this, com.geo.enterprises.withdrawal.WithdrawalsActivity.class));
        } else if (id == R.id.nav_notifications) {
            startActivity(new Intent(this, NotificationsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {
            // Show share app dialog
            showShareAppDialog();
        } else if (id == R.id.nav_help) {
            // TODO: Navigate to Help activity or show help dialog
            Toast.makeText(this, "Help & Support - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            // TODO: Navigate to About activity or show about dialog
            showAboutDialog();
        } else if (id == R.id.nav_logout) {
            drawerLayout.closeDrawer(GravityCompat.START);
            showLogoutConfirmation();
            return true;
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    private void showAboutDialog() {
        // Get real app version from package manager
        String versionName = "1.0"; // Default fallback
        int versionCode = 1; // Default fallback
        
        try {
            android.content.pm.PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            android.util.Log.e("DashboardActivity", "Error getting app version: " + e.getMessage());
        }
        
        String aboutMessage = "MOON ENTERPRISES\n\n" +
                "Version " + versionName + " (Build " + versionCode + ")\n\n" +
                "A comprehensive platform for prize bond booking and management.\n\n" +
                "© 2025 MOON ENTERPRISES. All rights reserved.";
        
        new ConfirmationDialog(this)
            .setTitle("About App")
            .setMessage(aboutMessage)
            .setIcon(R.drawable.ic_about)
            .setConfirmButtonText("OK")
            .setCancelButtonText(null)
            .setOnConfirmListener(new ConfirmationDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    // Do nothing, just close
                }
                
                @Override
                public void onCancel() {
                    // Not used
                }
            })
            .show();
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    private void setupToolbar() {
        // Custom toolbar is now handled in the layout
        // No need for traditional toolbar setup
        
        // Setup Deposit button
        if (btnDeposit != null) {
            btnDeposit.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.geo.enterprises.deposit.DepositActivity.class);
                startActivity(intent);
            });
        }

        // Setup Withdraw button
        if (btnWithdraw != null) {
            btnWithdraw.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.geo.enterprises.withdrawal.WithdrawalActivity.class);
                startActivity(intent);
            });
        }
        
        // Setup Premium Service button - Simple and minimal
        if (btnPremiumService != null) {
            btnPremiumService.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.geo.enterprises.paidservices.PaidServicesActivity.class);
                startActivity(intent);
            });
        }
        
        // Setup Video Guide button
        if (btnVideoGuide != null) {
            btnVideoGuide.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.geo.enterprises.help.HelpVideosActivity.class);
                startActivity(intent);
            });
        }
        
        // Setup Become Dealer button
        if (btnBecomeDealer != null) {
            btnBecomeDealer.setOnClickListener(v -> showDealershipConfirmationDialog());
        }
    }

    private void applyUrduFont() {
        try {
            Typeface nastaliq = ResourcesCompat.getFont(this, R.font.noto_nastaliq_urdu);
            if (nastaliq != null) {
                if (btnPremiumService != null) {
                    btnPremiumService.setTypeface(nastaliq);
                }
                if (btnVideoGuide != null) {
                    btnVideoGuide.setTypeface(nastaliq);
                }
                if (tvOnlineTitle != null) {
                    tvOnlineTitle.setTypeface(nastaliq);
                }
                if (tvPayoutAlert != null) {
                    tvPayoutAlert.setTypeface(nastaliq);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("UrduFont", "Failed to load Noto Nastaliq font: " + e.getMessage());
        }

        // Setup Join WhatsApp button
        if (btnJoinWhatsapp != null) {
            btnJoinWhatsapp.setOnClickListener(v -> showWhatsAppDialog());
        }
    }
    
    private void displayUserInfo() {
        tvWelcome.setText("Welcome, " + currentUser.getFullName() + "!");
        
        // Show Admin Badge if user is admin
        if (tvAdminBadge != null) {
            tvAdminBadge.setVisibility(currentUser.isAdmin() ? View.VISIBLE : View.GONE);
        }
        
        // Initialize UI based on cached dealer status first
        updateDealerUI(currentUser);
        
        // Check dealership status to update UI (badge, button state)
        // checkDealershipStatus(false); // Deprecated: Status now comes from User profile

        // Use dynamic currency formatting
        String balanceText = formatCurrency(currentUser.getBalance());
        tvBalance.setText(balanceText);
        
        tvPhone.setText(currentUser.getPhone());
        
        // Load user avatar with fallback
        loadUserAvatar();
        
        // Update drawer header
        updateDrawerHeader();
        
        // Fetch fresh user data from server
        fetchUserProfile();
    }
    
    private String formatCurrency(double amount) {
        // Format as Rs: 1000 (compact, no decimals)
        return "₨ " + String.format("%.0f", amount);
    }
    
    private void loadUserAvatar() {
        if (currentUser != null && currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
            String avatarUrl = com.geo.enterprises.config.AppConfig.getAvatarUrl(currentUser.getAvatar());
            
            // Load user avatar from URL - disable cache to always get fresh image
            Glide.with(this)
                .load(avatarUrl)
                .apply(new RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true))
                .into(ivUserAvatar);
        } else {
            // Use fallback avatar
            ivUserAvatar.setImageResource(R.drawable.ic_person);
        }
    }
    
    private void fetchUserProfile() {
        String token = preferenceManager.getAuthToken();
        if (token != null && !token.isEmpty()) {
            apiService.getProfile("Bearer " + token).enqueue(createMaintenanceAwareCallback(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        User updatedUser = response.body().getData();
                        if (updatedUser != null) {
                            android.util.Log.d("Dashboard", "API User Full: " + updatedUser.toString());
                            
                            // Manually check if dealer_status is still null and try to fix it
                            if (updatedUser.getDealerStatus() == null || "null".equals(updatedUser.getDealerStatus())) {
                                updatedUser.setDealerStatus("na");
                            }
                            
                            // Update local user data
                            currentUser = updatedUser;
                            preferenceManager.saveUserData(updatedUser);
                            
                            // Update UI with fresh data
                            tvWelcome.setText("Welcome, " + updatedUser.getFullName() + "!");
                            tvBalance.setText(formatCurrency(updatedUser.getBalance()));
                            tvPhone.setText(updatedUser.getPhone());
                            
                            // Update dealer UI with fresh data
                            updateDealerUI(updatedUser);
                            
                            // Update drawer header
                            updateDrawerHeader();
                            
                            // Load updated avatar
                            loadUserAvatar();
                        }
                    } else {
                        android.util.Log.e("Dashboard", "Profile API failed: " + (response.body() != null ? response.body().getMessage() : "Unknown error"));
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    // Keep showing cached data if API call fails
                    android.util.Log.e("Dashboard", "Failed to fetch user profile: " + t.getMessage());
                }
            }));
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
                // Refresh all data
                refreshAllData();
            });
        }
    }
    
    private void refreshAllData() {
        // Refresh balance
        refreshBalance();
        
        // Refresh game categories
        fetchGameCategories();
        
        // Refresh notifications count
        fetchNotificationCount();
        
        // Stop refresh animation after a short delay
        new android.os.Handler().postDelayed(() -> {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
        }, 1500);
    }
    
    private void refreshBalance() {
        String token = preferenceManager.getAuthToken();
        if (token != null && !token.isEmpty()) {
            apiService.getProfile("Bearer " + token).enqueue(createMaintenanceAwareCallback(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        User updatedUser = response.body().getData();
                        if (updatedUser != null) {
                            // Update local user data
                            currentUser = updatedUser;
                            preferenceManager.saveUserData(updatedUser);
                            
                            // Update UI with fresh data
                            tvBalance.setText(formatCurrency(updatedUser.getBalance()));
                            
                            // Update dealer UI
                            updateDealerUI(updatedUser);
                            
                            // Update drawer balance
                            if (tvDrawerBalance != null) {
                                tvDrawerBalance.setText(formatCurrency(updatedUser.getBalance()));
                            }
                            
                            // Show success snackbar
                            SnackbarUtils.showSuccess(findViewById(android.R.id.content), 
                                    "Data refreshed successfully!");
                        }
                    } else {
                        // Show error snackbar
                        SnackbarUtils.showError(findViewById(android.R.id.content), 
                                "Failed to refresh balance");
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    // Show error snackbar
                    SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
                    
                    android.util.Log.e("Dashboard", "Failed to refresh balance: " + t.getMessage());
                }
            }));
        } else {
            SnackbarUtils.showError(findViewById(android.R.id.content), 
                    "Session expired. Please login again.");
        }
    }
    
    private void fetchAppSettings() {
        apiService.getSettings().enqueue(new Callback<ApiResponse<AppSettings>>() {
            @Override
            public void onResponse(Call<ApiResponse<AppSettings>> call, Response<ApiResponse<AppSettings>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    appSettings = response.body().getData();
                    if (appSettings != null) {
                        // Save app settings
                        preferenceManager.saveAppSettings(appSettings);
                        
                        // Update currency display
                        tvBalance.setText(formatCurrency(currentUser.getBalance()));
                        
                        // Update drawer balance
                        if (tvDrawerBalance != null) {
                            tvDrawerBalance.setText(formatCurrency(currentUser.getBalance()));
                        }
                        
                        // Update top bar branding
                        updateTopBarBranding(appSettings);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AppSettings>> call, Throwable t) {
                android.util.Log.e("Dashboard", "Failed to fetch app settings: " + t.getMessage());
            }
        });
    }
    
    private void checkAndShowWelcomeMessage() {
        if (preferenceManager.isJustLoggedIn()) {
            // Show login success message
            SnackbarUtils.showLoginSuccess(findViewById(android.R.id.content));
            preferenceManager.clearJustLoggedIn();
        } else if (preferenceManager.isJustRegistered()) {
            // Show registration success message
            SnackbarUtils.showRegistrationSuccess(findViewById(android.R.id.content));
            preferenceManager.clearJustRegistered();
        }
    }
    
    private void setupBottomNavigation() {
        // Set Home as selected by default
        setSelectedTab(navHome, ivNavHome, tvNavHome, true);
        setSelectedTab(navNotifications, ivNavNotifications, tvNavNotifications, false);
        setSelectedTab(navOrders, ivNavOrders, tvNavOrders, false);
        setSelectedTab(navSettings, ivNavSettings, tvNavSettings, false);
        
        // Fetch notification count
        fetchNotificationCount();
        
        // Fetch game categories
        fetchGameCategories();
        
        // Setup click listeners
        navHome.setOnClickListener(v -> {
            // Already on home, just update selection
            setSelectedTab(navHome, ivNavHome, tvNavHome, true);
            setSelectedTab(navNotifications, ivNavNotifications, tvNavNotifications, false);
            setSelectedTab(navOrders, ivNavOrders, tvNavOrders, false);
            setSelectedTab(navSettings, ivNavSettings, tvNavSettings, false);
        });
        
        navNotifications.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInRight(this, new Intent(this, NotificationsActivity.class));
        });
        
        navOrders.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInRight(this, new Intent(this, YourOrdersActivity.class));
        });
        
        navSettings.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInRight(this, new Intent(this, SettingsActivity.class));
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
    
    private void fetchNotificationCount() {
        if (apiService == null) {
            return; // Exit early if apiService is not initialized
        }
        
        String authToken = preferenceManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            tvNotificationBadge.setVisibility(View.GONE);
            return;
        }
        
        String token = "Bearer " + authToken;
        
        apiService.getNotificationCount(token).enqueue(new Callback<ApiResponse<NotificationCount>>() {
            @Override
            public void onResponse(Call<ApiResponse<NotificationCount>> call, Response<ApiResponse<NotificationCount>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    NotificationCount countData = response.body().getData();
                    if (countData != null && countData.getCount() > 0) {
                        tvNotificationBadge.setText(String.valueOf(countData.getCount()));
                        tvNotificationBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvNotificationBadge.setVisibility(View.GONE);
                    }
                } else {
                    tvNotificationBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NotificationCount>> call, Throwable t) {
                tvNotificationBadge.setVisibility(View.GONE);
            }
        });
    }

    private void fetchGameCategories() {
        if (apiService == null) {
            android.util.Log.e("GameCategories", "API Service is null");
            showEmptyState();
            return;
        }
        
        // Show shimmer loading
        showLoadingState();
        
        android.util.Log.d("GameCategories", "Fetching game categories...");
        
        apiService.getGameCategories().enqueue(new Callback<ApiResponse<List<GameCategory>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GameCategory>>> call, Response<ApiResponse<List<GameCategory>>> response) {
                android.util.Log.d("GameCategories", "Response received: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("GameCategories", "Response body: " + response.body().toString());
                    android.util.Log.d("GameCategories", "Success: " + response.body().isSuccess());
                    
                    if (response.body().isSuccess()) {
                        List<GameCategory> categories = response.body().getData();
                        android.util.Log.d("GameCategories", "Categories count: " + (categories != null ? categories.size() : 0));
                        
                        if (categories != null) {
                            for (int i = 0; i < categories.size(); i++) {
                                GameCategory cat = categories.get(i);
                                android.util.Log.d("GameCategories", "=== CATEGORY " + i + " DETAILED INFO ===");
                                android.util.Log.d("GameCategories", "ID: " + cat.getId());
                                android.util.Log.d("GameCategories", "Name: " + cat.getName());
                                android.util.Log.d("GameCategories", "Image: " + cat.getImage());
                                android.util.Log.d("GameCategories", "Status: " + cat.getStatus());
                                android.util.Log.d("GameCategories", "Created At: " + cat.getCreatedAt());
                                android.util.Log.d("GameCategories", "--- Date/Time Fields ---");
                                android.util.Log.d("GameCategories", "date: " + cat.getDate());
                                android.util.Log.d("GameCategories", "time: " + cat.getTime());
                                android.util.Log.d("GameCategories", "startDate: " + cat.getStartDate());
                                android.util.Log.d("GameCategories", "startTime: " + cat.getStartTime());
                                android.util.Log.d("GameCategories", "endDate: " + cat.getEndDate());
                                android.util.Log.d("GameCategories", "endTime: " + cat.getEndTime());
                                android.util.Log.d("GameCategories", "datetime: " + cat.getDatetime());
                                android.util.Log.d("GameCategories", "--- Computed Values ---");
                                android.util.Log.d("GameCategories", "getDateOnly(): " + cat.getDateOnly());
                                android.util.Log.d("GameCategories", "getTimeOnly(): " + cat.getTimeOnly());
                                android.util.Log.d("GameCategories", "getFormattedDateTime(): " + cat.getFormattedDateTime());
                                android.util.Log.d("GameCategories", "===============================");
                            }
                        }
                        
                        // Hide loading state
                        hideLoadingState();
                        
                        if (categories != null && !categories.isEmpty()) {
                            setupGameCategoriesAdapter(categories);
                            hideEmptyState();
                        } else {
                            android.util.Log.d("GameCategories", "No categories found, showing empty state");
                            showEmptyState();
                        }
                    } else {
                        android.util.Log.e("GameCategories", "API success=false: " + response.body().getMessage());
                        hideLoadingState();
                        showEmptyState();
                    }
                } else {
                    android.util.Log.e("GameCategories", "API response failed: " + response.code() + " - " + (response.body() != null ? response.body().getMessage() : "Unknown error"));
                    hideLoadingState();
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<GameCategory>>> call, Throwable t) {
                android.util.Log.e("GameCategories", "Network error: " + t.getMessage());
                hideLoadingState();
                showEmptyState();
            }
        });
    }

    private void setupGameCategoriesRecyclerView() {
        android.util.Log.d("Games", "=== SETTING UP RECYCLER VIEW ===");
        
        gameCategoryList = new ArrayList<>();
        gameCategoryAdapter = new GameCategoryAdapter(gameCategoryList, new GameCategoryAdapter.OnGameCategoryClickListener() {
            @Override
            public void onGameCategoryClick(GameCategory gameCategory) {
                android.util.Log.d("DashboardActivity", "=== GAME CLICKED ===");
                android.util.Log.d("DashboardActivity", "Game: " + gameCategory.getName());
                android.util.Log.d("DashboardActivity", "ID: " + gameCategory.getId());
                // Navigate to bonds screen
                navigateToBonds(gameCategory);
            }
        });
        
        android.util.Log.d("GameCategories", "Adapter created");
        android.util.Log.d("GameCategories", "RecyclerView: " + (rvGameCategories != null ? "Found" : "NULL"));
        
        rvGameCategories.setAdapter(gameCategoryAdapter);
        
        android.util.Log.d("GameCategories", "Adapter set on RecyclerView");
        android.util.Log.d("GameCategories", "RecyclerView adapter: " + (rvGameCategories.getAdapter() != null ? "Set" : "NULL"));
    }

    private void setupGameCategoriesAdapter(List<GameCategory> categories) {
        android.util.Log.d("GameCategories", "Setting up adapter with " + categories.size() + " categories");
        
        if (gameCategoryAdapter != null) {
            gameCategoryAdapter.updateGameCategories(categories);
            refreshGamesListLayout();
        }
    }

    private void showEmptyState() {
        if (rvGameCategories != null) {
            rvGameCategories.setVisibility(View.GONE);
        }
        if (cardEmptyGames != null) {
            cardEmptyGames.setVisibility(View.VISIBLE);
        }
    }

    private void hideEmptyState() {
        if (rvGameCategories != null) {
            rvGameCategories.setVisibility(View.VISIBLE);
        }
        if (cardEmptyGames != null) {
            cardEmptyGames.setVisibility(View.GONE);
        }
        refreshGamesListLayout();
    }
    
    private void showLoadingState() {
        android.util.Log.d("GameCategories", "Showing loading state");
        if (rvShimmerLoading != null) {
            // Set up shimmer adapter with 5 placeholder items
            ShimmerAdapter shimmerAdapter = new ShimmerAdapter(5);
            rvShimmerLoading.setAdapter(shimmerAdapter);
            rvShimmerLoading.setVisibility(View.VISIBLE);
        }
        if (rvGameCategories != null) {
            rvGameCategories.setVisibility(View.GONE);
        }
        if (cardEmptyGames != null) {
            cardEmptyGames.setVisibility(View.GONE);
        }
    }
    
    private void hideLoadingState() {
        android.util.Log.d("GameCategories", "Hiding loading state");
        if (rvShimmerLoading != null) {
            rvShimmerLoading.setVisibility(View.GONE);
        }
        refreshGamesListLayout();
    }

    private void refreshGamesListLayout() {
        if (rvGameCategories == null) {
            return;
        }

        rvGameCategories.post(() -> {
            rvGameCategories.requestLayout();

            if (rvGameCategories.getParent() instanceof View) {
                ((View) rvGameCategories.getParent()).requestLayout();
            }
        });
    }

    // Trial check
    private android.app.Dialog maintenanceDialog;

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notification count when returning to dashboard
        fetchNotificationCount();
        // Refresh game categories
        fetchGameCategories();
        // Refresh user profile (balance, dealer status, etc.)
        fetchUserProfile();

        // Check and request notification permission if needed
        checkAndRequestNotificationPermission();

        // Check trial status
        checkTrialStatus();
    }

    private void checkTrialStatus() {
        if (apiService == null) return;
        
        apiService.getIsTrial().enqueue(new Callback<com.geo.enterprises.models.TrialStatusResponse>() {
            @Override
            public void onResponse(Call<com.geo.enterprises.models.TrialStatusResponse> call, Response<com.geo.enterprises.models.TrialStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isTrial()) {
                        showMaintenanceDialog();
                    } else {
                        if (maintenanceDialog != null && maintenanceDialog.isShowing()) {
                            maintenanceDialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<com.geo.enterprises.models.TrialStatusResponse> call, Throwable t) {
                // Ignore failure
            }
        });
    }

    private void showMaintenanceDialog() {
        if (maintenanceDialog != null && maintenanceDialog.isShowing()) {
            return;
        }

        if (isFinishing() || isDestroyed()) {
            return;
        }

        maintenanceDialog = new android.app.Dialog(this);
        maintenanceDialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        maintenanceDialog.setContentView(R.layout.dialog_maintenance_mode);
        maintenanceDialog.setCancelable(false); // Blocking
        
        // Full screen width
        if (maintenanceDialog.getWindow() != null) {
            maintenanceDialog.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            );
            maintenanceDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        maintenanceDialog.show();
    }
    
    private void showLogoutConfirmation() {
        ConfirmationDialog.showLogoutConfirmation(this, new ConfirmationDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                performLogout();
            }
            
            @Override
            public void onCancel() {
                // User cancelled, do nothing
            }
        });
    }
    
    private void performLogout() {
        // Clear user data
        preferenceManager.clearUserData();
        
        // Show logout success message
        SnackbarUtils.showLogoutSuccess(findViewById(android.R.id.content));
        
        // Navigate to login after a short delay
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500); // 1.5 second delay to show the message
    }
    
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        // Clear FCM token from server
        FcmTokenManager.clearTokenOnLogout(this);

        // Clear user data and preferences
        preferenceManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    
    private void updateTopBarBranding(AppSettings settings) {
        // Update app title
        if (settings.getAppName() != null && !settings.getAppName().isEmpty()) {
            tvAppTitle.setText(settings.getAppName());
        }
    }
    
    private void navigateToBonds(GameCategory gameCategory) {
        android.util.Log.d("DashboardActivity", "=== NAVIGATING TO BONDS ===");
        android.util.Log.d("DashboardActivity", "Game: " + gameCategory.getName());
        android.util.Log.d("DashboardActivity", "ID: " + gameCategory.getId());
        android.util.Log.d("DashboardActivity", "Raw Date: " + gameCategory.getDate());
        android.util.Log.d("DashboardActivity", "Raw Time: " + gameCategory.getTime());
        android.util.Log.d("DashboardActivity", "Date Only: " + gameCategory.getDateOnly());
        android.util.Log.d("DashboardActivity", "Time Only: " + gameCategory.getTimeOnly());
        android.util.Log.d("DashboardActivity", "Formatted DateTime: " + gameCategory.getFormattedDateTime());
        
        Intent intent = new Intent(this, SubcategoryActivity.class);
        intent.putExtra("game_id", gameCategory.getId());
        intent.putExtra("game_name", gameCategory.getName());
        intent.putExtra("game_image", gameCategory.getImage());
        
        // Pass the main game's date and time
        intent.putExtra("game_date", gameCategory.getDateOnly());
        intent.putExtra("game_time", gameCategory.getTimeOnly());
        intent.putExtra("game_datetime", gameCategory.getFormattedDateTime());
        
        android.util.Log.d("DashboardActivity", "Intent extras added - navigating...");
        ActivityTransitionUtils.slideInRight(this, intent);
    }
    
    /**
     * Check for force updates on app load
     */
    private void checkForForceUpdates() {
        if (appUpdateManager != null) {
            // Pass false to not show share dialog, only check for force updates
            appUpdateManager.checkForUpdates(false);
        }
    }
    
    /**
     * Show share app dialog when user clicks share menu item
     */
    private void showShareAppDialog() {
        if (appUpdateManager != null) {
            // Pass true to show share dialog with update info if available
            appUpdateManager.checkForUpdates(true);
        }
    }

    private void showLoading(boolean show) {
        if (show) {
            if (loadingDialog == null || !loadingDialog.isShowing()) {
                loadingDialog = LoadingDialog.show(this, "Please wait...");
            }
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }
    }

    private void checkDealershipStatus(boolean showLoading) {
        String token = preferenceManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            return;
        }

        if (showLoading) showLoading(true);
        
        apiService.getDealershipStatus("Bearer " + token).enqueue(new Callback<com.geo.enterprises.models.DealershipStatusResponse>() {
            @Override
            public void onResponse(Call<com.geo.enterprises.models.DealershipStatusResponse> call, Response<com.geo.enterprises.models.DealershipStatusResponse> response) {
                if (showLoading) showLoading(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    com.geo.enterprises.models.DealershipStatusResponse.DealershipData data = response.body().getData();
                    
                    boolean isDealer = data.isDealer();
                    boolean hasRequest = data.hasRequest();
                    String status = "none";
                    if (data.getRequest() != null && data.getRequest().getStatus() != null) {
                        status = data.getRequest().getStatus().toLowerCase();
                    }

                    // If status is approved, force isDealer to true for UI logic
                    if ("approved".equals(status)) {
                        isDealer = true;
                    }

                    // Update local user object and persist
                    if (currentUser != null) {
                        if (currentUser.isDealer() != isDealer) {
                            currentUser.setDealer(isDealer);
                            // Also update status fields if available from response
                            if ("approved".equals(status)) {
                                currentUser.setDealerStatus("approved");
                            } else if (hasRequest) {
                                currentUser.setDealerStatus(status);
                            }
                            preferenceManager.saveUserData(currentUser);
                        }
                    }

                    if (isDealer) {
                        // Hide button if already a dealer
                        if (btnBecomeDealer != null) {
                            btnBecomeDealer.setVisibility(View.GONE);
                        }
                        // Update UI to show dealer badge/flag
                        updateDealerUI(currentUser);
                    } else if (hasRequest) {
                        if ("rejected".equals(status)) {
                            // Rejected state
                            if (btnBecomeDealer != null) {
                                btnBecomeDealer.setVisibility(View.VISIBLE);
                                btnBecomeDealer.setText("❌ Request Rejected");
                                btnBecomeDealer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.RED));
                                btnBecomeDealer.setEnabled(true);
                                btnBecomeDealer.setAlpha(1.0f);
                                btnBecomeDealer.setOnClickListener(v -> showRejectionDialog());
                            }
                        } else {
                            // Pending state
                            if (btnBecomeDealer != null) {
                                btnBecomeDealer.setVisibility(View.VISIBLE);
                                btnBecomeDealer.setText("⏳ Application Pending");
                                btnBecomeDealer.setEnabled(false);
                                btnBecomeDealer.setAlpha(0.7f);
                            }
                        }
                    } else if (data.canApply()) {
                        // Can apply state
                        if (btnBecomeDealer != null) {
                            btnBecomeDealer.setVisibility(View.VISIBLE);
                            btnBecomeDealer.setText("🤝 Set Dealership ID");
                            btnBecomeDealer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2196F3")));
                            btnBecomeDealer.setEnabled(true);
                            btnBecomeDealer.setAlpha(1.0f);
                            btnBecomeDealer.setOnClickListener(v -> showDealershipConfirmationDialog());
                        }
                    } else {
                        // Cannot apply state (maybe blocked or error)
                        if (btnBecomeDealer != null) {
                            btnBecomeDealer.setVisibility(View.GONE);
                        }
                    }
                } else {
                    android.util.Log.e("DashboardActivity", "checkDealershipStatus failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<com.geo.enterprises.models.DealershipStatusResponse> call, Throwable t) {
                if (showLoading) showLoading(false);
                android.util.Log.e("DashboardActivity", "checkDealershipStatus error: " + t.getMessage());
            }
        });
    }

    private void showRejectionDialog() {
        new ConfirmationDialog(DashboardActivity.this)
            .setTitle("Request Rejected")
            .setMessage("Your dealership application was rejected. Please contact support for more information.")
            .setConfirmButtonText("Contact Support")
            .setCancelButtonText("Close")
            .setOnConfirmListener(new ConfirmationDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    openSupport();
                }
                @Override
                public void onCancel() {}
            })
            .show();
    }

    private void updateDealerUI(User user) {
        if (user == null) return;

        // Find views
        View dealerBadge = findViewById(R.id.ll_dealer_badge);
        TextView tvBadgeText = findViewById(R.id.tv_dealer_badge_text);
        
        // Commission Earned views
        View llCommissionEarned = findViewById(R.id.ll_commission_earned);
        TextView tvCommissionEarned = findViewById(R.id.tv_commission_earned);

        String status = user.getDealerStatus();

        // Use Log to debug without disturbing user too much, but Toast if critical
        android.util.Log.d("DealerStatus", "Status from API: '" + status + "'");

        if (status == null || "null".equals(status)) status = "na";
        status = status.trim().toLowerCase();

        if ("approved".equals(status)) {
            // Approved: Show Badge (Blue Tick) + Commission Earned, Hide Button
            if (dealerBadge != null) {
                dealerBadge.setVisibility(View.VISIBLE);

                // Update badge text to include percentage: "Dealer (5%)"
                if (tvBadgeText != null) {
                    double commission = user.getDealerCommission();
                    String badgeText = "Dealer";
                    if (commission > 0) {
                        String commissionText = commission == Math.floor(commission)
                            ? String.format("%.0f%%", commission)
                            : String.format("%.1f%%", commission);
                        badgeText += " (" + commissionText + ")";
                    }
                    tvBadgeText.setText(badgeText);
                }
            }
            
            // Show earned commission
            if (llCommissionEarned != null && tvCommissionEarned != null) {
                double earned = user.getCommissionEarned();
                
                llCommissionEarned.setVisibility(View.VISIBLE);
                tvCommissionEarned.setText(formatCurrency(earned));
                
                if (ivResetCommission != null) {
                    if (earned > 0) {
                        ivResetCommission.setVisibility(View.VISIBLE);
                        ivResetCommission.setOnClickListener(v -> showResetCommissionConfirmation());
                    } else {
                        ivResetCommission.setVisibility(View.GONE);
                    }
                }
            }

            if (btnBecomeDealer != null) {
                btnBecomeDealer.setVisibility(View.GONE);
            }
        } else {
            // Not Approved: Hide Badge, Hide Earned, Show/Configure Button
            if (dealerBadge != null) {
                dealerBadge.setVisibility(View.GONE);
            }
            if (llCommissionEarned != null) {
                llCommissionEarned.setVisibility(View.GONE);
            }
            
            if (btnBecomeDealer != null) {
                btnBecomeDealer.setVisibility(View.VISIBLE);
                btnBecomeDealer.setAlpha(1.0f);
                
                switch (status) {
                    case "pending":
                        btnBecomeDealer.setText("⏳ Application Pending");
                        btnBecomeDealer.setEnabled(false);
                        btnBecomeDealer.setAlpha(0.7f);
                        btnBecomeDealer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800"))); // Orange
                        break;
                        
                    case "rejected":
                        btnBecomeDealer.setText("❌ Request Rejected");
                        btnBecomeDealer.setEnabled(true);
                        btnBecomeDealer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.RED));
                        btnBecomeDealer.setOnClickListener(v -> showRejectionDialog());
                        break;
                        
                    default: // "na" or others
                        btnBecomeDealer.setText("🤝 Dealership ID");
                        btnBecomeDealer.setEnabled(true);
                        btnBecomeDealer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2196F3"))); // Blue
                        btnBecomeDealer.setOnClickListener(v -> showDealershipConfirmationDialog());
                        break;
                }
            }
        }
    }
    
    private void showResetCommissionConfirmation() {
        new ConfirmationDialog(this)
            .setTitle("Reset Commission")
            .setMessage("Are you sure you want to reset your earned commission? This action cannot be undone.")
            .setIcon(R.drawable.ic_refresh)
            .setConfirmButtonText("Reset")
            .setCancelButtonText("Cancel")
            .setOnConfirmListener(new ConfirmationDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    performResetCommission();
                }

                @Override
                public void onCancel() {
                    // Do nothing
                }
            })
            .show();
    }

    private void performResetCommission() {
        String token = preferenceManager.getAuthToken();
        if (token == null || token.isEmpty()) return;

        showLoading(true);

        apiService.resetCommission("Bearer " + token).enqueue(new Callback<com.geo.enterprises.models.ResetCommissionResponse>() {
            @Override
            public void onResponse(Call<com.geo.enterprises.models.ResetCommissionResponse> call, Response<com.geo.enterprises.models.ResetCommissionResponse> response) {
                showLoading(false);
                android.util.Log.d("ResetCommission", "Response Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    android.util.Log.d("ResetCommission", "Success: " + response.body().getMessage());
                    android.util.Log.d("ResetCommission", "Previous: " + response.body().getPreviousCommission() + ", Current: " + response.body().getCommissionEarned());
                    
                    SnackbarUtils.showSuccess(findViewById(android.R.id.content), response.body().getMessage());
                    
                    // Manually update local user data instead of full fetch to be faster
                    if (currentUser != null) {
                        currentUser.setCommissionEarned(response.body().getCommissionEarned());
                        preferenceManager.saveUserData(currentUser);
                        updateDealerUI(currentUser);
                    } else {
                         // Fallback to fetch if current user is somehow null
                         fetchUserProfile();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        android.util.Log.e("ResetCommission", "Failed. Code: " + response.code() + ", Error Body: " + errorBody);
                    } catch (Exception e) {
                        android.util.Log.e("ResetCommission", "Failed to parse error body", e);
                    }
                    SnackbarUtils.showError(findViewById(android.R.id.content), "Failed to reset commission");
                }
            }

            @Override
            public void onFailure(Call<com.geo.enterprises.models.ResetCommissionResponse> call, Throwable t) {
                showLoading(false);
                android.util.Log.e("ResetCommission", "Network Error", t);
                SnackbarUtils.showError(findViewById(android.R.id.content), "Network error: " + t.getMessage());
            }
        });
    }

    private void openSupport() {
        if (appSettings != null && appSettings.getWhatsappNumber() != null) {
             try {
                String url = "https://api.whatsapp.com/send?phone=" + appSettings.getWhatsappNumber();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Support contact not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWhatsAppDialog() {
        // Refresh settings from cache if needed
        if (appSettings == null) {
            appSettings = preferenceManager.getAppSettings();
        }

        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_whatsapp_options);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }

        android.view.View btnChat  = dialog.findViewById(R.id.btn_whatsapp_chat);
        android.view.View btnGroup = dialog.findViewById(R.id.btn_whatsapp_group);
        com.google.android.material.button.MaterialButton btnCancel = dialog.findViewById(R.id.btn_wa_cancel);

        // WhatsApp Chat — opens direct chat with the number
        btnChat.setOnClickListener(v -> {
            dialog.dismiss();
            String number = (appSettings != null && appSettings.getWhatsappNumber() != null)
                    ? appSettings.getWhatsappNumber().trim() : null;
            if (number == null || number.isEmpty()) {
                Toast.makeText(this, "WhatsApp number not configured", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                String url = "https://api.whatsapp.com/send?phone=" + number;
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
                intent.setPackage("com.whatsapp");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Fallback to browser
                    startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)));
                }
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open WhatsApp", Toast.LENGTH_SHORT).show();
            }
        });

        // Join Group — opens the group invite link
        btnGroup.setOnClickListener(v -> {
            dialog.dismiss();
            String link = (appSettings != null && appSettings.getWhatsappGroupLink() != null)
                    ? appSettings.getWhatsappGroupLink().trim() : null;
            if (link == null || link.isEmpty()) {
                Toast.makeText(this, "WhatsApp group link not configured", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(link));
                intent.setPackage("com.whatsapp");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(link)));
                }
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open WhatsApp group", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDealershipConfirmationDialog() {
        if (currentUser == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "User data not loaded. Please try again.");
            fetchUserProfile();
            return;
        }

        // Check if user has a dealership ID assigned
        if (currentUser.getDealershipId() == null || currentUser.getDealershipId().trim().isEmpty()) {
            // Case 1: No ID assigned in profile - Request not allowed
            new ConfirmationDialog(this)
                .setTitle("Not Eligible")
                .setMessage("You do not have a Dealership ID assigned. Please contact the admin to obtain your ID manually.")
                .setConfirmButtonText("Contact Support")
                .setCancelButtonText("Close")
                .setOnConfirmListener(new ConfirmationDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        openSupport();
                    }

                    @Override
                    public void onCancel() {
                        // Do nothing
                    }
                })
                .show();
            return;
        }

        // Case 2: ID exists - Ask user to input it for verification
        showDealershipInputIdDialog();
    }

    private void showDealershipInputIdDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_input_dealership);
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false); // Prevent closing while processing
        
        // Width adjustment
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        com.google.android.material.textfield.TextInputEditText etDealershipId = dialog.findViewById(R.id.et_dealership_id);
        android.widget.Button btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);
        android.widget.Button btnConfirm = dialog.findViewById(R.id.btn_dialog_confirm);
        android.widget.ProgressBar pbLoading = dialog.findViewById(R.id.pb_loading);
        android.widget.TextView tvErrorMessage = dialog.findViewById(R.id.tv_error_message);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String inputId = etDealershipId.getText().toString().trim();
            
            // Reset error state
            tvErrorMessage.setVisibility(View.GONE);
            
            if (inputId.isEmpty()) {
                tvErrorMessage.setText("Dealership ID is required");
                tvErrorMessage.setVisibility(View.VISIBLE);
                return;
            }
            
            if (inputId.length() < 4) {
                 tvErrorMessage.setText("Dealership ID must be 4 digits");
                 tvErrorMessage.setVisibility(View.VISIBLE);
                 return;
            }

            // Disable UI during simulation
            etDealershipId.setEnabled(false);
            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
            pbLoading.setVisibility(View.VISIBLE);
            
            // Generate random delay between 3000ms (3s) and 6000ms (6s)
            long delay = 3000 + (long)(Math.random() * 3000);

            new android.os.Handler().postDelayed(() -> {
                // Ensure dialog is still showing
                if (!dialog.isShowing()) return;
                
                // Verify against local user profile data (which came from server)
                String serverId = currentUser.getDealershipId();
                
                if (inputId.equals(serverId)) {
                    // Match confirmed - proceed to apply via API directly
                    // Keep loading state visible, don't re-enable UI yet
                    
                    String token = preferenceManager.getAuthToken();
                    if (token == null || token.isEmpty()) {
                         dialog.dismiss();
                         return;
                    }
            
                    apiService.applyForDealership("Bearer " + token).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!dialog.isShowing()) return;
                            
                            // Done loading
                            pbLoading.setVisibility(View.GONE);
                            dialog.dismiss(); // Close input dialog on API response (success or handled failure)

                            if (response.code() == 201) {
                                new ConfirmationDialog(DashboardActivity.this)
                                    .setTitle("Success")
                                    .setMessage("Application submitted successfully! Please wait for admin approval.")
                                    .setConfirmButtonText("OK")
                                    .setCancelButtonText(null)
                                    .show();
                            } else if (response.code() == 400) {
                                 new ConfirmationDialog(DashboardActivity.this)
                                    .setTitle("Application Failed")
                                    .setMessage("You are already a dealer or have a pending request.")
                                    .setConfirmButtonText("OK")
                                    .setCancelButtonText(null)
                                    .show();
                            } else {
                                 SnackbarUtils.showError(findViewById(android.R.id.content), "Failed to submit application. Please try again.");
                            }
                        }
            
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            if (!dialog.isShowing()) return;
                            
                            // Re-enable UI on network failure to allow retry
                            pbLoading.setVisibility(View.GONE);
                            etDealershipId.setEnabled(true);
                            btnConfirm.setEnabled(true);
                            btnCancel.setEnabled(true);
                            
                            tvErrorMessage.setText("Network error. Please try again.");
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }
                    });
                    
                } else {
                    // Re-enable UI and show error
                    pbLoading.setVisibility(View.GONE);
                    etDealershipId.setEnabled(true);
                    btnConfirm.setEnabled(true);
                    btnCancel.setEnabled(true);
                    
                    tvErrorMessage.setText("Invalid Dealership ID. Please check and try again.");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                }
            }, delay);
        });

        dialog.show();
    }

    private void applyForDealership() {
        String token = preferenceManager.getAuthToken();
        if (token == null || token.isEmpty()) return;

        showLoading(true);
        apiService.applyForDealership("Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);
                if (response.code() == 201) {
                    new ConfirmationDialog(DashboardActivity.this)
                        .setTitle("Success")
                        .setMessage("Application submitted successfully! Please wait for admin approval.")
                        .setConfirmButtonText("OK")
                        .setCancelButtonText(null)
                        .show();
                } else if (response.code() == 400) {
                     new ConfirmationDialog(DashboardActivity.this)
                        .setTitle("Application Failed")
                        .setMessage("You are already a dealer or have a pending request.")
                        .setConfirmButtonText("OK")
                        .setCancelButtonText(null)
                        .show();
                } else {
                     SnackbarUtils.showError(findViewById(android.R.id.content), "Failed to submit application. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoading(false);
                SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
            }
        });
    }

    private void checkAndRequestNotificationPermission() {
        // Log for debugging
        android.util.Log.d("DashboardActivity", "Checking notification permission...");

        // Check if we should ask for permission
        boolean shouldAsk = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+, check runtime permission
            boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;

            android.util.Log.d("DashboardActivity", "Android 13+, has permission: " + hasPermission);

            if (!hasPermission) {
                // Permission not granted, check if we should ask
                shouldAsk = preferenceManager.shouldAskForNotificationPermission();
                android.util.Log.d("DashboardActivity", "Should ask: " + shouldAsk);
            }
        } else {
            // For Android < 13, check if we should educate user (optional, since notifications work automatically)
            // Only show once to avoid annoying users on older Android versions
            long lastRequestTime = getSharedPreferences("geo_enterprises_prefs", MODE_PRIVATE)
                    .getLong("last_notification_permission_request", 0);
            if (lastRequestTime == 0) {
                shouldAsk = true;
                android.util.Log.d("DashboardActivity", "Android < 13, first time - will educate user");
            }
        }

        if (shouldAsk) {
            android.util.Log.d("DashboardActivity", "Showing notification permission dialog");
            showNotificationPermissionDialog();
        }
    }

    private void showNotificationPermissionDialog() {
        // Inflate custom dialog layout
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_notification_permission, null);

        // Get views from the layout
        com.google.android.material.button.MaterialButton btnNotNow = dialogView.findViewById(R.id.btn_not_now);
        com.google.android.material.button.MaterialButton btnEnable = dialogView.findViewById(R.id.btn_enable);

        // Create and show dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false); // Make dialog non-cancelable
        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set button click listeners
        btnNotNow.setOnClickListener(v -> {
            // Save the time we asked, so we don't ask again until tomorrow
            preferenceManager.saveLastNotificationPermissionRequestTime();
            android.util.Log.d("DashboardActivity", "User clicked 'Not Now', saved timestamp");
            dialog.dismiss();
        });

        btnEnable.setOnClickListener(v -> {
            dialog.dismiss();
            // Save the time we asked
            preferenceManager.saveLastNotificationPermissionRequestTime();

            // Request the actual Android notification permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.util.Log.d("DashboardActivity", "Requesting Android notification permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                // For older Android, just show a toast that notifications are enabled
                Toast.makeText(this, "Notifications enabled! You'll receive updates about your account.", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    // ===== SOCIAL PROOF: Live Active Users + Withdrawals/Deposits Alert =====
    private void startSocialProofUpdates() {
        socialProofHandler = new android.os.Handler();

        // Realistic Urdu names for live transactions
        final String[] urduNames = {
            "احمد علی", "محمد حسن", "فاطمہ بی بی", "علی رضا", "زینب نور",
            "عمر فاروق", "عائشہ صدیق", "بلال احمد", "ماریہ خان", "طارق محمود",
            "سانا ملک", "حمزہ شیخ", "نادیہ اقبال", "عاصم رضا", "رخسانہ بیگم",
            "داؤد انصاری", "ثناء چودھری", "کاشف امین", "وقاص جاوید", "شہزاد اکرم",
            "ریحان الحق", "فرحان ملک", "انیلہ بانو", "عثمان غنی", "تنویر حیدر"
        };
        final int[] amounts = {2000, 3500, 5000, 7500, 10000, 12000, 15000, 20000, 25000};
        final String[] paymentMethods = {"JazzCash", "EasyPaisa", "Bank Transfer"};
        final String[] timesAgoUrdu = {
            "ابھی ابھی",
            "1 منٹ پہلے",
            "2 منٹ پہلے",
            "3 منٹ پہلے",
            "4 منٹ پہلے",
            "5 منٹ پہلے",
            "چند لمحے پہلے"
        };
        final int[] txnIndex = {0};
        final java.util.Random random = new java.util.Random();

        // ---- Card 1: Active Users counter (starts at 448, gently fluctuates 418 - 488) ----
        activeUsersRunnable = new Runnable() {
            @Override
            public void run() {
                // Natural fluctuation (-2 to +2)
                int delta = random.nextInt(5) - 2;
                activeUserCount += delta;
                if (activeUserCount < 418) activeUserCount = 422 + random.nextInt(5);
                if (activeUserCount > 488) activeUserCount = 484 - random.nextInt(5);

                if (tvActiveUsers != null) {
                    tvActiveUsers.setText(String.valueOf(activeUserCount));
                }
                // Re-schedule after 5 to 8 seconds
                long delay = 5000 + random.nextInt(3000);
                if (socialProofHandler != null) {
                    socialProofHandler.postDelayed(this, delay);
                }
            }
        };
        socialProofHandler.postDelayed(activeUsersRunnable, 1500);

        // ---- Card 2: Live Activity (Alternates between Withdrawals and Deposits with smooth, graceful bottom-to-top animation) ----
        payoutAlertRunnable = new Runnable() {
            @Override
            public void run() {
                String name = urduNames[txnIndex[0] % urduNames.length];
                int amount = amounts[random.nextInt(amounts.length)];
                String timeAgo = timesAgoUrdu[random.nextInt(timesAgoUrdu.length)];

                java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US);
                String formattedAmount = nf.format(amount);

                // Alternating: Even = Withdrawal (Green), Odd = Deposit (Blue)
                boolean isWithdrawal = (txnIndex[0] % 2 == 0);
                String alertText = isWithdrawal ?
                        (name + " نے " + timeAgo + " " + formattedAmount + " روپے نکلوائے") :
                        (name + " نے " + timeAgo + " " + formattedAmount + " روپے جمع کروائے");

                if (layoutPayoutTextContainer != null) {
                    // 1. Current text gracefully slides UP and fades out (380ms)
                    float slideOffset = 30f;
                    layoutPayoutTextContainer.animate()
                        .translationY(-slideOffset)
                        .alpha(0f)
                        .setDuration(380)
                        .setInterpolator(new android.view.animation.AccelerateInterpolator(1.2f))
                        .withEndAction(() -> {
                            // 2. Set new content while hidden
                            if (tvPayoutAlert != null) tvPayoutAlert.setText(alertText);

                            if (isWithdrawal) {
                                if (layoutTransactionIconBg != null) {
                                    layoutTransactionIconBg.setBackgroundResource(R.drawable.bg_social_icon_green);
                                }
                                if (ivTransactionType != null) {
                                    ivTransactionType.setImageResource(R.drawable.ic_social_withdraw);
                                }
                            } else {
                                if (layoutTransactionIconBg != null) {
                                    layoutTransactionIconBg.setBackgroundResource(R.drawable.bg_social_icon_blue);
                                }
                                if (ivTransactionType != null) {
                                    ivTransactionType.setImageResource(R.drawable.ic_social_deposit);
                                }
                            }

                            // 3. Position below (ready to slide in from bottom)
                            layoutPayoutTextContainer.setTranslationY(slideOffset);
                            layoutPayoutTextContainer.setAlpha(0f);

                            // 4. Smoothly and gently glide IN from bottom to 0 with decelerate curve (550ms)
                            layoutPayoutTextContainer.animate()
                                .translationY(0f)
                                .alpha(1f)
                                .setDuration(550)
                                .setInterpolator(new android.view.animation.DecelerateInterpolator(2.0f))
                                .start();

                            // Soft icon settle
                            if (layoutTransactionIconBg != null) {
                                layoutTransactionIconBg.setScaleX(0.78f);
                                layoutTransactionIconBg.setScaleY(0.78f);
                                layoutTransactionIconBg.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(500)
                                    .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                                    .start();
                            }
                        })
                        .start();
                } else {
                    if (tvPayoutAlert != null) tvPayoutAlert.setText(alertText);
                }

                txnIndex[0]++;
                // Less frequent cycle: 8 to 12 seconds between transitions
                long delay = 8000 + random.nextInt(4000);
                if (socialProofHandler != null) {
                    socialProofHandler.postDelayed(this, delay);
                }
            }
        };
        socialProofHandler.postDelayed(payoutAlertRunnable, 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socialProofHandler != null) {
            if (activeUsersRunnable != null) {
                socialProofHandler.removeCallbacks(activeUsersRunnable);
            }
            if (payoutAlertRunnable != null) {
                socialProofHandler.removeCallbacks(payoutAlertRunnable);
            }
        }
    }
}

