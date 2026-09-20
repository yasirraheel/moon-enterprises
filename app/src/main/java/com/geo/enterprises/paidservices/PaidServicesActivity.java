package com.geo.enterprises.paidservices;

import android.os.Bundle;
import android.util.Log;
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
import com.geo.enterprises.models.PaidService;
import com.geo.enterprises.models.PaidServicesResponse;
import com.geo.enterprises.models.PurchaseResponse;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaidServicesActivity extends AppCompatActivity implements PaidServicesAdapter.OnServiceClickListener {

    private static final String TAG = "PaidServicesActivity";

    private RecyclerView rvPaidServices;
    private LinearLayout llLoading, llEmpty, llError;
    private TextView tvErrorMessage;
    private MaterialButton btnRetry;
    private ImageView ivBack;

    private PaidServicesAdapter adapter;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_services);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadPaidServices();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        rvPaidServices = findViewById(R.id.rv_paid_services);
        llLoading = findViewById(R.id.ll_loading);
        llEmpty = findViewById(R.id.ll_empty);
        llError = findViewById(R.id.ll_error);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnRetry = findViewById(R.id.btn_retry);

        // Apply status bar padding
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }

        apiService = ApiClient.getInstance().getApiService();
        preferenceManager = new PreferenceManager(this);
    }

    private void setupRecyclerView() {
        adapter = new PaidServicesAdapter(this);
        rvPaidServices.setLayoutManager(new LinearLayoutManager(this));
        rvPaidServices.setAdapter(adapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());
        btnRetry.setOnClickListener(v -> loadPaidServices());
    }

    private void loadPaidServices() {
        showLoading();
        
        // Get auth token
        String authToken = preferenceManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "No authentication token found! User needs to login.");
            showError("Please login to view paid services");
            return;
        }
        
        String token = "Bearer " + authToken;
        
        // Debug: Log the token (first 20 chars only for security)
        Log.d(TAG, "Auth Token (first 20 chars): " + token.substring(0, Math.min(20, token.length())) + "...");
        Log.d(TAG, "Full token length: " + token.length());

        apiService.getPaidServices(token).enqueue(new Callback<PaidServicesResponse>() {
            @Override
            public void onResponse(Call<PaidServicesResponse> call, Response<PaidServicesResponse> response) {
                // Debug: Log request URL
                Log.d(TAG, "Request URL: " + call.request().url());
                Log.d(TAG, "Response Code: " + response.code());
                Log.d(TAG, "Has Auth Header: " + (call.request().header("Authorization") != null));
                
                if (response.isSuccessful() && response.body() != null) {
                    PaidServicesResponse servicesResponse = response.body();
                    
                    if (servicesResponse.isSuccess() && servicesResponse.getData() != null) {
                        List<PaidService> services = servicesResponse.getData();
                        
                        if (services.isEmpty()) {
                            showEmpty();
                        } else {
                            // Debug logging for each service
                            for (PaidService service : services) {
                                Log.d(TAG, "=== Service Data ===");
                                Log.d(TAG, "Title: " + service.getTitle());
                                Log.d(TAG, "Has Purchased: " + service.hasPurchased());
                                Log.d(TAG, "Show Buy Button: " + service.showBuyButton());
                                Log.d(TAG, "Golden Text: " + service.getGoldenText());
                            }
                            
                            adapter.setServices(services);
                            showContent();
                            Log.d(TAG, "Loaded " + services.size() + " paid services");
                        }
                    } else {
                        showError("No services available");
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful: " + response.code());
                    showError("Failed to load services");
                }
            }

            @Override
            public void onFailure(Call<PaidServicesResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showLoading() {
        rvPaidServices.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        llLoading.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        rvPaidServices.setVisibility(View.VISIBLE);
    }

    private void showEmpty() {
        llLoading.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        rvPaidServices.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        llLoading.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        rvPaidServices.setVisibility(View.GONE);
        llError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
    }

    @Override
    public void onServiceClick(PaidService service) {
        // If already purchased, show details dialog with lucky numbers
        if (service.hasPurchased()) {
            showServiceDetailsDialog(service);
        } else if (service.showBuyButton()) {
            // If not purchased and can buy, go directly to confirmation
            confirmPurchase(service);
        } else {
            // Otherwise show details
            showServiceDetailsDialog(service);
        }
    }

    private void showServiceDetailsDialog(PaidService service) {
        // Inflate custom service details layout
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_service_details, null);
        
        // Get views from the layout
        android.widget.TextView tvTitle = dialogView.findViewById(R.id.tv_service_details_title);
        android.widget.TextView tvPrice = dialogView.findViewById(R.id.tv_service_details_price);
        android.widget.TextView tvDescription = dialogView.findViewById(R.id.tv_service_details_description);
        android.widget.LinearLayout llGoldenTextContainer = dialogView.findViewById(R.id.ll_golden_text_container);
        android.widget.TextView tvGoldenText = dialogView.findViewById(R.id.tv_service_details_golden_text);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btn_service_details_close);
        com.google.android.material.button.MaterialButton btnGetService = dialogView.findViewById(R.id.btn_service_details_get);
        
        // Set title with checkmark if purchased
        String title = service.hasPurchased() ? "✅ " + service.getTitle() : service.getTitle();
        tvTitle.setText(title);
        
        // Set price
        tvPrice.setText("₨ " + service.getPrice());
        
        // Set description
        tvDescription.setText(service.getDescription());
        
        // Show golden text if purchased
        if (service.hasPurchased() && service.getGoldenText() != null && !service.getGoldenText().isEmpty()) {
            llGoldenTextContainer.setVisibility(View.VISIBLE);
            tvGoldenText.setText(service.getGoldenText());
        } else {
            llGoldenTextContainer.setVisibility(View.GONE);
        }
        
        // Show/hide get service button based on purchase status
        if (service.hasPurchased() || !service.showBuyButton()) {
            btnGetService.setVisibility(View.GONE);
        } else {
            btnGetService.setVisibility(View.VISIBLE);
            btnGetService.setText("Get Service - ₨ " + service.getPrice());
        }
        
        // Create and show dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Set button click listeners
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnGetService.setOnClickListener(v -> {
            dialog.dismiss();
            confirmPurchase(service);
        });
        
        dialog.show();
    }

    private void confirmPurchase(PaidService service) {
        // Inflate custom purchase confirmation layout
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_purchase_confirmation, null);
        
        // Get views from the layout
        android.widget.TextView tvServiceName = dialogView.findViewById(R.id.tv_purchase_service_name);
        android.widget.TextView tvPrice = dialogView.findViewById(R.id.tv_purchase_price);
        android.widget.TextView tvCurrentBalance = dialogView.findViewById(R.id.tv_purchase_current_balance);
        android.widget.TextView tvAfterBalance = dialogView.findViewById(R.id.tv_purchase_after_balance);
        com.google.android.material.button.MaterialButton btnCancel = dialogView.findViewById(R.id.btn_purchase_cancel);
        com.google.android.material.button.MaterialButton btnConfirm = dialogView.findViewById(R.id.btn_purchase_confirm);
        
        // Get user balance
        double userBalance = 0;
        com.geo.enterprises.models.User currentUser = preferenceManager.getUserData();
        if (currentUser != null) {
            userBalance = currentUser.getBalance();
        }
        
        // Parse service price - handle potential formatting issues
        double servicePrice = 0;
        try {
            String priceStr = service.getPrice();
            // Remove any commas or currency symbols that might be in the price
            priceStr = priceStr.replaceAll("[^0-9.]", "");
            servicePrice = Double.parseDouble(priceStr);
            Log.d(TAG, "Original price: " + service.getPrice() + ", Cleaned: " + priceStr + ", Parsed: " + servicePrice);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing service price: " + service.getPrice(), e);
            // Try to use the raw price if parsing fails
            servicePrice = 0;
        }
        
        // Set values
        tvServiceName.setText(service.getTitle());
        tvPrice.setText("₨ " + service.getPrice()); // Use raw price string to avoid format issues
        tvCurrentBalance.setText("₨ " + String.format("%.0f", userBalance));
        
        double afterBalance = userBalance - servicePrice;
        tvAfterBalance.setText("₨ " + String.format("%.0f", afterBalance));
        
        Log.d(TAG, "=== Purchase Confirmation ===");
        Log.d(TAG, "Service: " + service.getTitle());
        Log.d(TAG, "Price String: " + service.getPrice());
        Log.d(TAG, "Price Parsed: " + servicePrice);
        Log.d(TAG, "Current Balance: " + userBalance);
        Log.d(TAG, "After Balance: " + afterBalance);
        Log.d(TAG, "===========================");
        
        // Create and show dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Set button click listeners
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            processPurchase(service);
        });
        
        dialog.show();
    }

    private void processPurchase(PaidService service) {
        // Show loading using SnackbarUtils
        SnackbarUtils.showLoading(this, "Processing your purchase...");

        // Get auth token
        String token = "Bearer " + preferenceManager.getAuthToken();

        // Prepare purchase data
        Map<String, Object> purchaseData = new HashMap<>();
        purchaseData.put("service_id", service.getId());

        apiService.purchasePaidService(token, purchaseData).enqueue(new Callback<PurchaseResponse>() {
            @Override
            public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {
                SnackbarUtils.dismissCurrentLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    PurchaseResponse purchaseResponse = response.body();
                    
                    if (purchaseResponse.isSuccess()) {
                        Log.d(TAG, "Purchase successful: " + service.getTitle());
                        Log.d(TAG, "Reloading services to get updated purchase status...");
                        
                        // Show success message using SnackbarUtils
                        String successMsg = purchaseResponse.getMessage();
                        if (purchaseResponse.getData() != null) {
                            successMsg += "\n\nRemaining Balance: ₨ " + 
                                    String.format("%.2f", purchaseResponse.getData().getRemainingBalance());
                        }
                        SnackbarUtils.showSuccess(findViewById(android.R.id.content), successMsg);
                        
                        // Reload services to refresh purchase status
                        loadPaidServices();
                    } else {
                        // Show error using SnackbarUtils
                        SnackbarUtils.showDanger(findViewById(android.R.id.content), 
                                purchaseResponse.getMessage());
                        Log.e(TAG, "Purchase failed: " + purchaseResponse.getMessage());
                    }
                } else {
                    // Handle HTTP error
                    String errorMessage = "Failed to complete purchase";
                    
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            
                            // Try to parse error message from JSON
                            com.google.gson.JsonObject jsonObject = new com.google.gson.JsonParser()
                                    .parse(errorBody)
                                    .getAsJsonObject();
                            
                            if (jsonObject.has("message")) {
                                errorMessage = jsonObject.get("message").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    
                    SnackbarUtils.showDanger(findViewById(android.R.id.content), errorMessage);
                    Log.e(TAG, "Purchase response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PurchaseResponse> call, Throwable t) {
                SnackbarUtils.dismissCurrentLoading();
                SnackbarUtils.showDanger(findViewById(android.R.id.content), 
                        "Network error: " + t.getMessage());
                Log.e(TAG, "Purchase API call failed", t);
            }
        });
    }
}
