package com.geo.enterprises.withdrawal;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.Withdrawal;
import com.geo.enterprises.models.WithdrawalsResponse;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawalsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private MaterialButton btnFilterAll;
    private MaterialButton btnFilterPending;
    private MaterialButton btnFilterApproved;
    private MaterialButton btnFilterRejected;
    private LinearLayout shimmerViewContainer;
    private RecyclerView rvWithdrawals;
    private LinearLayout llEmptyState;

    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private WithdrawalsAdapter adapter;
    private List<Withdrawal> withdrawalsList;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawals);

        initializeViews();
        setupApiService();
        setupRecyclerView();
        setupFilterButtons();
        setupClickListeners();
        loadWithdrawals("all");
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        btnFilterAll = findViewById(R.id.btn_filter_all);
        btnFilterPending = findViewById(R.id.btn_filter_pending);
        btnFilterApproved = findViewById(R.id.btn_filter_approved);
        btnFilterRejected = findViewById(R.id.btn_filter_rejected);
        shimmerViewContainer = findViewById(R.id.shimmer_view_container);
        rvWithdrawals = findViewById(R.id.rv_withdrawals);
        llEmptyState = findViewById(R.id.ll_empty_state);
        
        // Apply status bar padding to prevent overlap
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
    }

    private void setupApiService() {
        apiService = ApiClient.getInstance().getApiService();
        preferenceManager = new PreferenceManager(this);
    }

    private void setupRecyclerView() {
        withdrawalsList = new ArrayList<>();
        adapter = new WithdrawalsAdapter(withdrawalsList);
        rvWithdrawals.setLayoutManager(new LinearLayoutManager(this));
        rvWithdrawals.setAdapter(adapter);
    }

    private void setupFilterButtons() {
        updateFilterButtonsState("all");
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnFilterAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateFilterButtonsState("all");
            loadWithdrawals(null);
        });

        btnFilterPending.setOnClickListener(v -> {
            currentFilter = "pending";
            updateFilterButtonsState("pending");
            loadWithdrawals("pending");
        });

        btnFilterApproved.setOnClickListener(v -> {
            currentFilter = "approved";
            updateFilterButtonsState("approved");
            loadWithdrawals("approved");
        });

        btnFilterRejected.setOnClickListener(v -> {
            currentFilter = "rejected";
            updateFilterButtonsState("rejected");
            loadWithdrawals("rejected");
        });
    }

    private void updateFilterButtonsState(String activeFilter) {
        // Reset all buttons
        resetFilterButton(btnFilterAll);
        resetFilterButton(btnFilterPending);
        resetFilterButton(btnFilterApproved);
        resetFilterButton(btnFilterRejected);

        // Set active button
        MaterialButton activeButton = null;
        switch (activeFilter) {
            case "all":
                activeButton = btnFilterAll;
                break;
            case "pending":
                activeButton = btnFilterPending;
                break;
            case "approved":
                activeButton = btnFilterApproved;
                break;
            case "rejected":
                activeButton = btnFilterRejected;
                break;
        }

        if (activeButton != null) {
            activeButton.setBackgroundColor(getResources().getColor(R.color.primary_color));
            activeButton.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void resetFilterButton(MaterialButton button) {
        button.setBackgroundColor(getResources().getColor(R.color.white));
        button.setTextColor(getResources().getColor(R.color.primary_color));
    }

    private void loadWithdrawals(String status) {
        if (apiService == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "API service not available");
            return;
        }

        // Show shimmer loading
        showLoading();

        String token = "Bearer " + preferenceManager.getAuthToken();
        
        Call<WithdrawalsResponse> call = apiService.getUserWithdrawals(token, status, 50);

        call.enqueue(new Callback<WithdrawalsResponse>() {
            @Override
            public void onResponse(Call<WithdrawalsResponse> call, Response<WithdrawalsResponse> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    WithdrawalsResponse withdrawalsResponse = response.body();
                    
                    if (withdrawalsResponse.isSuccess()) {
                        List<Withdrawal> withdrawals = withdrawalsResponse.getData();
                        
                        if (withdrawals != null && !withdrawals.isEmpty()) {
                            withdrawalsList.clear();
                            withdrawalsList.addAll(withdrawals);
                            adapter.updateData(withdrawalsList);
                            showContent();
                        } else {
                            showEmptyState();
                        }
                    } else {
                        showEmptyState();
                        SnackbarUtils.showError(findViewById(android.R.id.content),
                            withdrawalsResponse.getMessage() != null ? withdrawalsResponse.getMessage() : "Failed to load withdrawals");
                    }
                } else {
                    showEmptyState();
                    SnackbarUtils.showError(findViewById(android.R.id.content),
                        "Failed to load withdrawals. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WithdrawalsResponse> call, Throwable t) {
                hideLoading();
                showEmptyState();
                SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Network error: " + t.getMessage());
            }
        });
    }

    private void showLoading() {
        shimmerViewContainer.setVisibility(View.VISIBLE);
        rvWithdrawals.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.GONE);
    }

    private void hideLoading() {
        shimmerViewContainer.setVisibility(View.GONE);
    }

    private void showContent() {
        rvWithdrawals.setVisibility(View.VISIBLE);
        llEmptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        rvWithdrawals.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.VISIBLE);
    }
}
