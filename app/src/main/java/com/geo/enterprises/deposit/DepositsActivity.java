package com.geo.enterprises.deposit;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.dashboard.ShimmerAdapter;
import com.geo.enterprises.models.DepositItem;
import com.geo.enterprises.models.DepositsResponse;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositsActivity extends AppCompatActivity {
    
    private ImageView ivBack;
    private TextView chipAll, chipPending, chipApproved, chipRejected;
    private RecyclerView rvDeposits;
    private RecyclerView rvShimmerLoading;
    private View emptyView;
    private TextView tvEmptyMessage;
    
    private DepositsAdapter depositsAdapter;
    private List<DepositItem> depositsList;
    
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    
    private String currentStatusFilter = null; // null = all
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposits);
        
        // Initialize
        preferenceManager = new PreferenceManager(this);
        apiService = ApiClient.getInstance().getApiService();
        depositsList = new ArrayList<>();
        
        // Initialize views
        initializeViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup click listeners
        setupClickListeners();
        
        // Load deposits
        loadDeposits(null);
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        chipAll = findViewById(R.id.chip_all);
        chipPending = findViewById(R.id.chip_pending);
        chipApproved = findViewById(R.id.chip_approved);
        chipRejected = findViewById(R.id.chip_rejected);
        rvDeposits = findViewById(R.id.rv_deposits);
        rvShimmerLoading = findViewById(R.id.rv_shimmer_loading);
        emptyView = findViewById(R.id.empty_view);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        
        // Apply status bar padding to prevent overlap
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
    }
    
    private void setupRecyclerView() {
        depositsAdapter = new DepositsAdapter(this, depositsList);
        rvDeposits.setLayoutManager(new LinearLayoutManager(this));
        rvDeposits.setAdapter(depositsAdapter);
        
        // Setup shimmer recyclerview
        if (rvShimmerLoading != null) {
            rvShimmerLoading.setLayoutManager(new LinearLayoutManager(this));
        }
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        // Status filter chips
        chipAll.setOnClickListener(v -> {
            updateFilterChips(null);
            loadDeposits(null);
        });
        
        chipPending.setOnClickListener(v -> {
            updateFilterChips("pending");
            loadDeposits("pending");
        });
        
        chipApproved.setOnClickListener(v -> {
            updateFilterChips("approved");
            loadDeposits("approved");
        });
        
        chipRejected.setOnClickListener(v -> {
            updateFilterChips("rejected");
            loadDeposits("rejected");
        });
    }
    
    private void updateFilterChips(String status) {
        currentStatusFilter = status;
        
        // Reset all chips to inactive state
        chipAll.setBackgroundResource(R.drawable.bg_input);
        chipAll.setTextColor(getResources().getColor(R.color.text_primary));
        chipPending.setBackgroundResource(R.drawable.bg_input);
        chipPending.setTextColor(getResources().getColor(R.color.text_primary));
        chipApproved.setBackgroundResource(R.drawable.bg_input);
        chipApproved.setTextColor(getResources().getColor(R.color.text_primary));
        chipRejected.setBackgroundResource(R.drawable.bg_input);
        chipRejected.setTextColor(getResources().getColor(R.color.text_primary));
        
        // Highlight active chip
        if (status == null) {
            chipAll.setBackgroundResource(R.drawable.bg_button);
            chipAll.setTextColor(getResources().getColor(R.color.white));
        } else if (status.equals("pending")) {
            chipPending.setBackgroundResource(R.drawable.bg_button);
            chipPending.setTextColor(getResources().getColor(R.color.white));
        } else if (status.equals("approved")) {
            chipApproved.setBackgroundResource(R.drawable.bg_button);
            chipApproved.setTextColor(getResources().getColor(R.color.white));
        } else if (status.equals("rejected")) {
            chipRejected.setBackgroundResource(R.drawable.bg_button);
            chipRejected.setTextColor(getResources().getColor(R.color.white));
        }
    }
    
    private void loadDeposits(String status) {
        if (apiService == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "API service not available");
            return;
        }
        
        // Show loading
        showLoading(true);
        
        // Get auth token
        String token = "Bearer " + preferenceManager.getAuthToken();
        
        android.util.Log.d("DepositsActivity", "Loading deposits with status: " + status);
        
        // Make API call
        apiService.getUserDeposits(token, status, 50, 1).enqueue(new Callback<DepositsResponse>() {
            @Override
            public void onResponse(Call<DepositsResponse> call, Response<DepositsResponse> response) {
                showLoading(false);
                
                android.util.Log.d("DepositsActivity", "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    DepositsResponse depositsResponse = response.body();
                    
                    if (depositsResponse.isSuccess()) {
                        List<DepositItem> deposits = depositsResponse.getData();
                        
                        android.util.Log.d("DepositsActivity", "Deposits loaded: " + (deposits != null ? deposits.size() : 0));
                        
                        if (deposits != null && !deposits.isEmpty()) {
                            depositsList.clear();
                            depositsList.addAll(deposits);
                            depositsAdapter.notifyDataSetChanged();
                            
                            // Show deposits list
                            rvDeposits.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        } else {
                            // Show empty view
                            String emptyMessage = "No deposits found";
                            if (status != null) {
                                emptyMessage = "No " + status + " deposits";
                            }
                            showEmptyView(emptyMessage);
                        }
                    } else {
                        android.util.Log.e("DepositsActivity", "API error: " + depositsResponse.getMessage());
                        SnackbarUtils.showError(findViewById(android.R.id.content), 
                            depositsResponse.getMessage());
                        showEmptyView("Failed to load deposits");
                    }
                } else {
                    android.util.Log.e("DepositsActivity", "Response not successful: " + response.code());
                    String errorMessage = "Failed to load deposits";
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please login again.";
                    }
                    SnackbarUtils.showError(findViewById(android.R.id.content), errorMessage);
                    showEmptyView(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<DepositsResponse> call, Throwable t) {
                showLoading(false);
                android.util.Log.e("DepositsActivity", "Network error: " + t.getMessage());
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
            if (rvDeposits != null) {
                rvDeposits.setVisibility(View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        } else {
            // Hide shimmer loading
            if (rvShimmerLoading != null) {
                rvShimmerLoading.setVisibility(View.GONE);
            }
        }
    }
    
    private void showEmptyView(String message) {
        rvDeposits.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText(message);
    }
}
