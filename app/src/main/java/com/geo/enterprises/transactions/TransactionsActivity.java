package com.geo.enterprises.transactions;

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
import com.geo.enterprises.models.Transaction;
import com.geo.enterprises.models.TransactionResponse;
import com.geo.enterprises.utils.DateTimeUtils;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {
    
    private ImageView ivBack;
    private RecyclerView rvTransactions;
    private LinearLayout emptyView;
    
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    
    private int currentPage = 1;
    private int lastPage = 1;
    private boolean isLoading = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        
        // Initialize
        preferenceManager = new PreferenceManager(this);
        apiService = ApiClient.getInstance().getApiService();
        transactionList = new ArrayList<>();
        
        // Initialize views
        initializeViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup click listeners
        setupClickListeners();
        
        // Load transactions
        loadTransactions();
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        rvTransactions = findViewById(R.id.rv_transactions);
        emptyView = findViewById(R.id.empty_view);

        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
    }
    
    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter(transactionList, new TransactionAdapter.OnTransactionClickListener() {
            @Override
            public void onTransactionClick(Transaction transaction) {
                // Show transaction details
                showTransactionDetails(transaction);
            }
        });
        
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(transactionAdapter);
        
        // Add pagination scroll listener
        rvTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && currentPage < lastPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreTransactions();
                    }
                }
            }
        });
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }
    
    private void loadTransactions() {
        if (apiService == null) {
            android.util.Log.e("TransactionsActivity", "API Service is null");
            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Service unavailable");
            showEmptyState();
            return;
        }
        
        String authToken = preferenceManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            android.util.Log.e("TransactionsActivity", "Auth token is null or empty");
            SnackbarUtils.showDanger(findViewById(android.R.id.content), "Authentication required");
            showEmptyState();
            return;
        }
        
        String token = "Bearer " + authToken;
        android.util.Log.d("TransactionsActivity", "Loading transactions...");
        android.util.Log.d("TransactionsActivity", "Token: " + token);
        
        isLoading = true;
        showLoading();
        
        // Load first page with high limit to get all transactions
        Call<TransactionResponse> call = apiService.getUserTransactions(token, 10000, 1, null, null, null, null);
        
        android.util.Log.d("TransactionsActivity", "API Call URL: " + call.request().url());
        
        call.enqueue(new Callback<TransactionResponse>() {
                @Override
                public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                    isLoading = false;
                    hideLoading();
                    
                    android.util.Log.d("TransactionsActivity", "Response code: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        TransactionResponse transactionResponse = response.body();
                        
                        android.util.Log.d("TransactionsActivity", "Response success: " + transactionResponse.isSuccess());
                        
                        if (transactionResponse.isSuccess()) {
                            List<Transaction> transactions = transactionResponse.getData();
                            
                            android.util.Log.d("TransactionsActivity", "Transactions count: " + (transactions != null ? transactions.size() : 0));
                            
                            if (transactions != null && !transactions.isEmpty()) {
                                transactionList.clear();
                                transactionList.addAll(transactions);
                                transactionAdapter.notifyDataSetChanged();
                                
                                // Update pagination info
                                if (transactionResponse.getPagination() != null) {
                                    currentPage = transactionResponse.getPagination().getCurrentPage();
                                    lastPage = transactionResponse.getPagination().getLastPage();
                                }
                                
                                hideEmptyState();
                            } else {
                                android.util.Log.w("TransactionsActivity", "Transactions list is empty");
                                showEmptyState();
                            }
                        } else {
                            String message = transactionResponse.getMessage();
                            android.util.Log.e("TransactionsActivity", "API error: " + message);
                            if (message != null && !message.isEmpty()) {
                                SnackbarUtils.showDanger(findViewById(android.R.id.content), message);
                            } else {
                                SnackbarUtils.showDanger(findViewById(android.R.id.content), "Failed to load transactions");
                            }
                            showEmptyState();
                        }
                    } else {
                        android.util.Log.e("TransactionsActivity", "Response not successful or body is null");
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            android.util.Log.e("TransactionsActivity", "Error body: " + errorBody);
                        } catch (Exception e) {
                            android.util.Log.e("TransactionsActivity", "Error reading error body: " + e.getMessage());
                        }
                        SnackbarUtils.showDanger(findViewById(android.R.id.content), 
                            "Failed to load transactions (Code: " + response.code() + ")");
                        showEmptyState();
                    }
                }
                
                @Override
                public void onFailure(Call<TransactionResponse> call, Throwable t) {
                    isLoading = false;
                    hideLoading();
                    android.util.Log.e("TransactionsActivity", "Network error: " + t.getMessage(), t);
                    SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
                    showEmptyState();
                }
            });
    }
    
    private void loadMoreTransactions() {
        if (isLoading || currentPage >= lastPage) {
            return;
        }
        
        String authToken = preferenceManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            return;
        }
        
        String token = "Bearer " + authToken;
        isLoading = true;
        int nextPage = currentPage + 1;
        
        apiService.getUserTransactions(token, 20, nextPage, null, null, null, null)
            .enqueue(new Callback<TransactionResponse>() {
                @Override
                public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                    isLoading = false;
                    
                    if (response.isSuccessful() && response.body() != null) {
                        TransactionResponse transactionResponse = response.body();
                        
                        if (transactionResponse.isSuccess()) {
                            List<Transaction> transactions = transactionResponse.getData();
                            
                            if (transactions != null && !transactions.isEmpty()) {
                                transactionList.addAll(transactions);
                                transactionAdapter.notifyDataSetChanged();
                                
                                // Update pagination info
                                if (transactionResponse.getPagination() != null) {
                                    currentPage = transactionResponse.getPagination().getCurrentPage();
                                    lastPage = transactionResponse.getPagination().getLastPage();
                                }
                            }
                        }
                    }
                }
                
                @Override
                public void onFailure(Call<TransactionResponse> call, Throwable t) {
                    isLoading = false;
                }
            });
    }
    
    private void showTransactionDetails(Transaction transaction) {
        // Inflate custom dialog layout
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        android.view.View dialogView = inflater.inflate(R.layout.dialog_transaction_details, null);
        
        // Find views
        androidx.cardview.widget.CardView cvIconContainer = dialogView.findViewById(R.id.cv_transaction_icon_container);
        android.widget.ImageView ivIcon = dialogView.findViewById(R.id.iv_transaction_dialog_icon);
        android.widget.TextView tvTitle = dialogView.findViewById(R.id.tv_transaction_dialog_title);
        android.widget.TextView tvType = dialogView.findViewById(R.id.tv_transaction_dialog_type);
        android.widget.TextView tvAmount = dialogView.findViewById(R.id.tv_transaction_dialog_amount);
        android.widget.TextView tvDate = dialogView.findViewById(R.id.tv_transaction_dialog_date);
        android.widget.TextView tvCurrentBalance = dialogView.findViewById(R.id.tv_transaction_dialog_current_balance);
        android.widget.TextView tvRemainingBalance = dialogView.findViewById(R.id.tv_transaction_dialog_remaining_balance);
        android.widget.LinearLayout llDescriptionContainer = dialogView.findViewById(R.id.ll_transaction_description_container);
        android.widget.TextView tvDescription = dialogView.findViewById(R.id.tv_transaction_dialog_description);
        android.widget.LinearLayout llReferenceContainer = dialogView.findViewById(R.id.ll_transaction_reference_container);
        android.widget.TextView tvReference = dialogView.findViewById(R.id.tv_transaction_dialog_reference);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btn_transaction_dialog_close);
        
        // Set title
        tvTitle.setText("Transaction Details");
        
        // Set transaction type
        String typeLabel = transaction.getTransactionTypeLabel();
        if (typeLabel != null && !typeLabel.trim().isEmpty()) {
            tvType.setText(typeLabel);
        } else if (transaction.getTransactionType() != null) {
            tvType.setText(capitalizeFirst(transaction.getTransactionType()));
        }
        
        // Set amount with color
        String amountText;
        int amountColor;
        int iconColor;
        
        if (transaction.isCredit()) {
            amountText = "+ " + (transaction.getFormattedAmount() != null ? 
                transaction.getFormattedAmount() : transaction.getAmount());
            amountColor = getResources().getColor(android.R.color.holo_green_dark);
            iconColor = getResources().getColor(android.R.color.holo_green_dark);
        } else {
            amountText = "- " + (transaction.getFormattedAmount() != null ? 
                transaction.getFormattedAmount() : transaction.getAmount());
            amountColor = getResources().getColor(android.R.color.holo_red_dark);
            iconColor = getResources().getColor(android.R.color.holo_red_dark);
        }
        
        tvAmount.setText(amountText);
        tvAmount.setTextColor(amountColor);
        cvIconContainer.setCardBackgroundColor(iconColor);
        
        // Set date
        if (transaction.getCreatedAt() != null) {
            tvDate.setText(formatDateTime(transaction.getCreatedAt()));
        }
        
        // Set balances
        String currentBalanceText = (transaction.getFormattedCurrentBalance() != null ? 
            transaction.getFormattedCurrentBalance() : transaction.getCurrentBalance());
        tvCurrentBalance.setText(currentBalanceText);
        
        String remainingBalanceText = (transaction.getFormattedRemainingBalance() != null ? 
            transaction.getFormattedRemainingBalance() : transaction.getRemainingBalance());
        tvRemainingBalance.setText(remainingBalanceText);
        tvRemainingBalance.setTextColor(amountColor);
        
        // Set description if available
        if (transaction.getDescription() != null && !transaction.getDescription().trim().isEmpty()) {
            llDescriptionContainer.setVisibility(android.view.View.VISIBLE);
            tvDescription.setText(transaction.getDescription());
        } else {
            llDescriptionContainer.setVisibility(android.view.View.GONE);
        }
        
        // Set reference ID if available
        if (transaction.getReferenceId() != null && !transaction.getReferenceId().trim().isEmpty()) {
            llReferenceContainer.setVisibility(android.view.View.VISIBLE);
            tvReference.setText(transaction.getReferenceId());
        } else {
            llReferenceContainer.setVisibility(android.view.View.GONE);
        }
        
        // Create and show dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();
        
        // Make dialog background transparent to show custom background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        // Set button click listener
        btnClose.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    
    private void showLoading() {
        // Show loading state if needed
    }
    
    private void hideLoading() {
        // Hide loading state if needed
    }
    
    private void showEmptyState() {
        rvTransactions.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }
    
    private void hideEmptyState() {
        rvTransactions.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }
    
    private String formatDateTime(String dateTimeStr) {
        // Use DateTimeUtils to format with Pakistan timezone (UTC+5)
        return DateTimeUtils.formatTransactionDateTime(dateTimeStr);
    }
}
