package com.geo.enterprises.orders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.AppSettings;
import com.geo.enterprises.models.GameCategory;
import com.geo.enterprises.models.GameOrderSummary;
import com.geo.enterprises.models.Order;
import com.geo.enterprises.models.OrdersResponse;
import com.geo.enterprises.models.User;
import com.geo.enterprises.utils.ConfirmationDialog;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;
import com.geo.enterprises.dashboard.ShimmerAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourOrdersActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private ImageView ivBack;
    private RecyclerView rvOrders;
    private RecyclerView rvShimmerLoading;
    private View emptyView;
    private TextView tvEmptyMessage;
    
    private GameOrderSummaryAdapter summaryAdapter;
    private OrdersAdapter ordersAdapter;
    private List<Order> ordersList;
    private List<GameOrderSummary> gameSummaries;
    private List<GameCategory> gameCategories;
    
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private User currentUser;
    private AppSettings appSettings;
    private SwipeRefreshLayout swipeRefresh;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_orders);
        
        // Initialize
        preferenceManager = new PreferenceManager(this);
        apiService = ApiClient.getInstance().getApiService();
        ordersList = new ArrayList<>();
        gameSummaries = new ArrayList<>();
        gameCategories = new ArrayList<>();
        currentUser = preferenceManager.getUserData();
        appSettings = preferenceManager.getAppSettings();
        
        // Initialize views
        initializeViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup click listeners
        setupClickListeners();
        
        // Load orders
        loadUserOrders();
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        rvOrders = findViewById(R.id.rv_orders);
        rvShimmerLoading = findViewById(R.id.rv_shimmer_loading);
        emptyView = findViewById(R.id.empty_view);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        
        // Apply status bar padding
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
        
        // Setup swipe refresh
        setupSwipeRefresh();
    }
    
    private void setupSwipeRefresh() {
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(
                R.color.primary_color,
                R.color.primary_dark,
                R.color.primary_light
            );
            swipeRefresh.setOnRefreshListener(() -> {
                loadUserOrders();
            });
        }
    }
    
    private void setupRecyclerView() {
        // Setup game summary adapter
        summaryAdapter = new GameOrderSummaryAdapter(summary -> {
            // Navigate to GameOrdersActivity with filtered orders
            navigateToGameOrders(summary);
        });
        
        ordersAdapter = new OrdersAdapter(this, ordersList);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(summaryAdapter); // Start with summary adapter
        
        // Setup shimmer recyclerview
        if (rvShimmerLoading != null) {
            rvShimmerLoading.setLayoutManager(new LinearLayoutManager(this));
        }
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }
    
    private void loadUserOrders() {
        if (apiService == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "API service not available");
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        // First fetch game categories to get image URLs
        fetchGameCategories();
    }
    
    private void fetchGameCategories() {
        android.util.Log.d("YourOrdersActivity", "Fetching game categories...");
        
        apiService.getGameCategories().enqueue(new Callback<ApiResponse<List<GameCategory>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GameCategory>>> call, Response<ApiResponse<List<GameCategory>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<GameCategory> categories = response.body().getData();
                    if (categories != null) {
                        gameCategories.clear();
                        gameCategories.addAll(categories);
                        android.util.Log.d("YourOrdersActivity", "Game categories loaded: " + categories.size());
                    }
                } else {
                    android.util.Log.e("YourOrdersActivity", "Failed to load game categories");
                }
                
                // Now fetch orders
                fetchUserOrders();
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<GameCategory>>> call, Throwable t) {
                android.util.Log.e("YourOrdersActivity", "Error loading game categories: " + t.getMessage());
                // Continue to fetch orders even if categories fail
                fetchUserOrders();
            }
        });
    }
    
    private void fetchUserOrders() {
        // Show loading
        showLoading(true);
        
        // Get auth token
        String token = "Bearer " + preferenceManager.getAuthToken();
        
        android.util.Log.d("YourOrdersActivity", "Loading user orders...");
        
        // Make API call - load all orders (increased limit to 10000)
        apiService.getUserOrders(token, 10000, 1, null).enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                showLoading(false);
                
                android.util.Log.d("YourOrdersActivity", "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    OrdersResponse ordersResponse = response.body();
                    
                    if (ordersResponse.isSuccess()) {
                        List<Order> orders = ordersResponse.getData();
                        
                        android.util.Log.d("YourOrdersActivity", "Orders loaded: " + (orders != null ? orders.size() : 0));
                        
                        if (orders != null && !orders.isEmpty()) {
                            ordersList.clear();
                            ordersList.addAll(orders);
                            
                            // Group orders by game and create summaries
                            createGameSummaries();
                            
                            // Show categories list
                            rvOrders.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        } else {
                            // Show empty view
                            showEmptyView("No orders yet. Start booking now!");
                        }
                    } else {
                        android.util.Log.e("YourOrdersActivity", "API error: " + ordersResponse.getMessage());
                        SnackbarUtils.showError(findViewById(android.R.id.content), 
                            ordersResponse.getMessage());
                        showEmptyView("Failed to load orders");
                    }
                } else {
                    android.util.Log.e("YourOrdersActivity", "Response not successful: " + response.code());
                    String errorMessage = "Failed to load orders";
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please login again.";
                    }
                    SnackbarUtils.showError(findViewById(android.R.id.content), errorMessage);
                    showEmptyView(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                showLoading(false);
                android.util.Log.e("YourOrdersActivity", "Network error: " + t.getMessage());
                SnackbarUtils.showError(findViewById(android.R.id.content), 
                    "Network error: " + t.getMessage());
                showEmptyView("Network error. Please try again.");
            }
        });
    }
    
    private void showLoading(boolean show) {
        if (show) {
            // Show shimmer loading
            if (rvShimmerLoading != null) {
                ShimmerAdapter shimmerAdapter = new ShimmerAdapter(5);
                rvShimmerLoading.setAdapter(shimmerAdapter);
                rvShimmerLoading.setVisibility(View.VISIBLE);
            }
            if (rvOrders != null) {
                rvOrders.setVisibility(View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        } else {
            // Stop refresh animation
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            // Hide shimmer loading
            if (rvShimmerLoading != null) {
                rvShimmerLoading.setVisibility(View.GONE);
            }
        }
    }
    
    private void showEmptyView(String message) {
        rvOrders.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText(message);
    }
    
    // Export functionality moved to GameOrdersActivity
    
    private void createGameSummaries() {
        // Group orders by game name
        Map<String, List<Order>> groupedOrders = new LinkedHashMap<>();
        
        for (Order order : ordersList) {
            String gameName = order.getGameName();
            if (!groupedOrders.containsKey(gameName)) {
                groupedOrders.put(gameName, new ArrayList<>());
            }
            groupedOrders.get(gameName).add(order);
        }
        
        // Create summaries with actual game images
        gameSummaries.clear();
        for (Map.Entry<String, List<Order>> entry : groupedOrders.entrySet()) {
            String gameName = entry.getKey();
            int orderCount = entry.getValue().size();
            
            // Find matching game category to get image
            String gameImage = null;
            for (GameCategory category : gameCategories) {
                if (category.getName() != null && category.getName().equalsIgnoreCase(gameName)) {
                    gameImage = category.getImage();
                    android.util.Log.d("YourOrdersActivity", "Found image for " + gameName + ": " + gameImage);
                    break;
                }
            }
            
            // Use placeholder if no image found
            if (gameImage == null || gameImage.isEmpty()) {
                gameImage = "ic_games";
                android.util.Log.d("YourOrdersActivity", "No image found for " + gameName + ", using placeholder");
            }
            
            gameSummaries.add(new GameOrderSummary(gameName, orderCount, gameImage));
        }
        
        // Update adapter
        summaryAdapter.setSummaryList(gameSummaries);
        
        android.util.Log.d("YourOrdersActivity", "Created " + gameSummaries.size() + " game summaries");
    }
    
    private void navigateToGameOrders(GameOrderSummary summary) {
        // Filter orders for this game
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : ordersList) {
            if (order.getGameName().equals(summary.getGameName())) {
                filteredOrders.add(order);
            }
        }
        
        // Navigate to GameOrdersActivity
        Intent intent = new Intent(this, GameOrdersActivity.class);
        intent.putExtra("game_name", summary.getGameName());
        intent.putExtra("orders", (Serializable) filteredOrders);
        startActivity(intent);
    }
    
    private double calculateTotalSum() {
        double sum = 0;
        for (Order order : ordersList) {
            sum += order.getTotalAmount();
        }
        return sum;
    }
}
