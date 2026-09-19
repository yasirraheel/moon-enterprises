package com.geo.enterprises.withdrawal;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.User;
import com.geo.enterprises.models.Withdrawal;
import com.geo.enterprises.models.WithdrawalMethod;
import com.geo.enterprises.models.WithdrawalMethodsResponse;
import com.geo.enterprises.utils.LoadingDialog;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawalActivity extends AppCompatActivity {

    private ImageView ivBack;
    private Spinner spinnerWithdrawalMethod;
    private CardView cardWithdrawalMethodDetails;
    private ImageView ivMethodLogo;
    private TextView tvMethodName, tvAccountTitle, tvAccountNo, tvMinLimit, tvMaxLimit;
    private TextView tvBalance, tvWithdrawalInstructions;
    private EditText etAmount;
    private EditText etAccountNumber;
    private EditText etAccountTitle;
    private AutoCompleteTextView etBankName;
    private MaterialButton btnSubmit;

    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private User currentUser;
    private LoadingDialog loadingDialog;
    private boolean accountNumberWasPrefilled = false;
    
    private List<WithdrawalMethod> withdrawalMethods;
    private WithdrawalMethod selectedMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        initializeViews();
        applyUrduFont();
        setupApiService();
        loadWithdrawalMethods();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        spinnerWithdrawalMethod = findViewById(R.id.spinner_withdrawal_method);
        cardWithdrawalMethodDetails = findViewById(R.id.card_withdrawal_method_details);
        ivMethodLogo = findViewById(R.id.iv_withdrawal_method_logo);
        tvMethodName = findViewById(R.id.tv_withdrawal_method_name);
        tvAccountTitle = findViewById(R.id.tv_withdrawal_account_title);
        tvAccountNo = findViewById(R.id.tv_withdrawal_account_no);
        tvMinLimit = findViewById(R.id.tv_withdrawal_min_limit);
        tvMaxLimit = findViewById(R.id.tv_withdrawal_max_limit);
        tvBalance = findViewById(R.id.tv_balance);
        tvWithdrawalInstructions = findViewById(R.id.tv_withdrawal_instructions);
        etAmount = findViewById(R.id.et_amount);
        etAccountNumber = findViewById(R.id.et_account_number);
        etAccountTitle = findViewById(R.id.et_account_title);
        etBankName = findViewById(R.id.et_bank_name);
        btnSubmit = findViewById(R.id.btn_submit);
        
        setupBankDropdown();
        
        withdrawalMethods = new ArrayList<>();
        
        // Apply status bar padding to prevent overlap
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
        
        // Apply professional bottom padding fix for submit button
        View submitButton = findViewById(R.id.btn_submit);
        if (submitButton != null) {
            WindowInsetsHelper.applyProfessionalBottomNavigationFix(this, submitButton);
        }
    }

    private void setupBankDropdown() {
        String[] banks = new String[] {
            "Easypaisa", "JazzCash", "SadaPay", "NayaPay",
            "Allied Bank Limited (ABL)", "Habib Bank Limited (HBL)",
            "Bank Alfalah", "Meezan Bank", "United Bank Limited (UBL)",
            "MCB Bank", "Standard Chartered", "Faysal Bank",
            "Askari Bank", "Bank Al Habib", "JS Bank",
            "National Bank of Pakistan (NBP)"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, banks);
        etBankName.setAdapter(adapter);
    }

    private void setupApiService() {
        apiService = ApiClient.getInstance().getApiService();
        preferenceManager = new PreferenceManager(this);
    }
    
    private void applyUrduFont() {
        try {
            Typeface nastaliq = ResourcesCompat.getFont(this, R.font.noto_nastaliq_urdu);
            if (nastaliq == null) return;

            int[] urduTextIds = {
                R.id.tv_processing_time_title,
                R.id.tv_processing_time_desc
            };

            for (int id : urduTextIds) {
                TextView tv = findViewById(id);
                if (tv != null) {
                    tv.setTypeface(nastaliq);
                    tv.setIncludeFontPadding(true);
                }
            }

            // Submit button
            if (btnSubmit != null) {
                btnSubmit.setTypeface(nastaliq);
            }

        } catch (Exception e) {
            android.util.Log.e("UrduFont", "Font error: " + e.getMessage());
        }
    }
    
    private void loadWithdrawalMethods() {
        apiService.getWithdrawalMethods().enqueue(new Callback<WithdrawalMethodsResponse>() {
            @Override
            public void onResponse(Call<WithdrawalMethodsResponse> call, Response<WithdrawalMethodsResponse> response) {
                android.util.Log.d("WithdrawalActivity", "API Response received");
                if (response.isSuccessful() && response.body() != null) {
                    WithdrawalMethodsResponse methodsResponse = response.body();
                    android.util.Log.d("WithdrawalActivity", "Response body: " + methodsResponse.toString());
                    if (methodsResponse.isSuccess() && methodsResponse.getData() != null) {
                        withdrawalMethods = methodsResponse.getData();
                        android.util.Log.d("WithdrawalActivity", "Number of methods: " + withdrawalMethods.size());
                        
                        // Log each method's details
                        for (int i = 0; i < withdrawalMethods.size(); i++) {
                            WithdrawalMethod method = withdrawalMethods.get(i);
                            android.util.Log.d("WithdrawalActivity", "Method " + i + ": " + method.getBankOrAccountName());
                            android.util.Log.d("WithdrawalActivity", "  Bank Image: " + method.getBankImage());
                            android.util.Log.d("WithdrawalActivity", "  Account Title: " + method.getAccountTitle());
                            android.util.Log.d("WithdrawalActivity", "  Account No: " + method.getAccountNo());
                        }
                        
                        setupWithdrawalMethodSpinner();
                    } else {
                        android.util.Log.e("WithdrawalActivity", "Response not successful or data is null");
                        SnackbarUtils.showError(findViewById(android.R.id.content),
                                "No withdrawal methods available");
                    }
                } else {
                    android.util.Log.e("WithdrawalActivity", "Response failed: " + response.code());
                    SnackbarUtils.showError(findViewById(android.R.id.content),
                            "Failed to load withdrawal methods");
                }
            }

            @Override
            public void onFailure(Call<WithdrawalMethodsResponse> call, Throwable t) {
                android.util.Log.e("WithdrawalActivity", "API call failed", t);
                SnackbarUtils.showError(findViewById(android.R.id.content),
                        "Network error: " + t.getMessage());
            }
        });
    }
    
    private void setupWithdrawalMethodSpinner() {
        // Filter only active methods
        List<WithdrawalMethod> activeMethods = new ArrayList<>();
        for (WithdrawalMethod method : withdrawalMethods) {
            if (method.isActive()) {
                activeMethods.add(method);
            }
        }

        // Use custom adapter with images and placeholder
        WithdrawalMethodAdapter adapter = new WithdrawalMethodAdapter(this, activeMethods, true);
        spinnerWithdrawalMethod.setAdapter(adapter);
        
        spinnerWithdrawalMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                android.util.Log.d("WithdrawalActivity", "Spinner item selected at position: " + position);
                if (position > 0) {
                    // Subtract 1 because of placeholder at position 0
                    selectedMethod = activeMethods.get(position - 1);
                    android.util.Log.d("WithdrawalActivity", "Selected method: " + selectedMethod.getBankOrAccountName());
                    android.util.Log.d("WithdrawalActivity", "Method has image: " + selectedMethod.getBankImage());
                    showWithdrawalMethodDetails(selectedMethod);
                } else {
                    selectedMethod = null;
                    android.util.Log.d("WithdrawalActivity", "No method selected (position 0)");
                    cardWithdrawalMethodDetails.setVisibility(View.GONE);
                    tvWithdrawalInstructions.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMethod = null;
                cardWithdrawalMethodDetails.setVisibility(View.GONE);
                tvWithdrawalInstructions.setVisibility(View.GONE);
            }
        });
    }
    
    private void showWithdrawalMethodDetails(WithdrawalMethod method) {
        cardWithdrawalMethodDetails.setVisibility(View.VISIBLE);

        // Load method logo/image
        if (method.getBankImage() != null && !method.getBankImage().isEmpty()) {
            String imageUrl = method.getBankImage();
            
            // Log the original URL
            android.util.Log.d("WithdrawalActivity", "Original image URL: " + imageUrl);
            
            // The API now returns the correct URL with /public/ included
            // Just use it as-is
            final String finalImageUrl = imageUrl;
            
            android.util.Log.d("WithdrawalActivity", "Loading image from: " + finalImageUrl);
            
            Glide.with(this)
                    .load(finalImageUrl)
                    .placeholder(R.drawable.ic_orders)
                    .error(R.drawable.ic_orders)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            String errorMsg = "Failed to load image";
                            if (e != null && e.getRootCauses() != null && !e.getRootCauses().isEmpty()) {
                                Throwable rootCause = e.getRootCauses().get(0);
                                errorMsg = rootCause.getMessage();
                            }
                            android.util.Log.e("WithdrawalActivity", "Image load failed: " + finalImageUrl, e);
                            android.util.Log.e("WithdrawalActivity", "Error details: " + errorMsg);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            android.util.Log.d("WithdrawalActivity", "Image loaded successfully: " + finalImageUrl);
                            return false;
                        }
                    })
                    .into(ivMethodLogo);
        } else {
            android.util.Log.d("WithdrawalActivity", "No bank image URL provided");
            ivMethodLogo.setImageResource(R.drawable.ic_orders);
        }

        // Set method details
        tvMethodName.setText(method.getBankOrAccountName());
        tvAccountTitle.setText(method.getAccountTitle());
        tvAccountNo.setText(method.getAccountNo());
        
        // Display withdrawal limits
        tvMinLimit.setText("₨ " + String.format("%.0f", method.getMinimumLimitDouble()));
        tvMaxLimit.setText("₨ " + String.format("%.0f", method.getMaximumLimitDouble()));
        
        // Also show instructions
        showMethodInstructions(method);
    }
    
    private void showMethodInstructions(WithdrawalMethod method) {
        String instructions = String.format(
            "Min: ₨ %s | Max: ₨ %s\nPlease ensure your amount is within these limits.",
            String.format("%.0f", method.getMinAmountDouble()),
            String.format("%.0f", method.getMaxAmountDouble())
        );
        tvWithdrawalInstructions.setText(instructions);
        tvWithdrawalInstructions.setVisibility(View.VISIBLE);
    }

    private void loadUserData() {
        currentUser = preferenceManager.getUserData();
        
        if (currentUser != null) {
            // Display balance
            tvBalance.setText("₨ " + String.format("%.0f", currentUser.getBalance()));
            
            // Pre-fill account number if exists in user data
            if (currentUser.getAccountNo() != null && !currentUser.getAccountNo().isEmpty()) {
                etAccountNumber.setText(currentUser.getAccountNo());
                etAccountNumber.setEnabled(false); // Make it non-editable if already saved
                accountNumberWasPrefilled = true;
            } else {
                // Account number is null/empty, allow user to enter it
                etAccountNumber.setEnabled(true);
                accountNumberWasPrefilled = false;
            }
        }
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        // Validate withdrawal method selection
        if (selectedMethod == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Please select a withdrawal method");
            return;
        }
        
        String amount = etAmount.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();
        String accountTitle = etAccountTitle.getText().toString().trim();
        String bankName = etBankName.getText().toString().trim();

        // Validate inputs
        if (amount.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Amount is required");
            etAmount.requestFocus();
            return;
        }

        double withdrawalAmount;
        try {
            withdrawalAmount = Double.parseDouble(amount);
            if (withdrawalAmount <= 0) {
                SnackbarUtils.showError(findViewById(android.R.id.content),
                        "Amount must be greater than 0");
                etAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Invalid amount");
            etAmount.requestFocus();
            return;
        }
        
        // Validate against withdrawal method limits
        double minAmount = selectedMethod.getMinAmountDouble();
        double maxAmount = selectedMethod.getMaxAmountDouble();
        
        if (withdrawalAmount < minAmount) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Amount must be at least ₨ " + String.format("%.0f", minAmount));
            etAmount.requestFocus();
            return;
        }
        
        if (withdrawalAmount > maxAmount) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Amount cannot exceed ₨ " + String.format("%.0f", maxAmount));
            etAmount.requestFocus();
            return;
        }

        // Check sufficient balance
        if (currentUser != null && withdrawalAmount > currentUser.getBalance()) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                "Insufficient balance. Available: ₨ " + String.format("%.0f", currentUser.getBalance()));
            return;
        }

        if (accountNumber.isEmpty()) {
            etAccountNumber.setError("Account number is required");
            etAccountNumber.requestFocus();
            return;
        }

        if (accountTitle.isEmpty()) {
            etAccountTitle.setError("Account title is required");
            etAccountTitle.requestFocus();
            return;
        }

        if (bankName.isEmpty()) {
            etBankName.setError("Bank name is required");
            etBankName.requestFocus();
            return;
        }

        // If account number wasn't pre-filled (user entered it now), save it permanently
        if (!accountNumberWasPrefilled) {
            saveAccountNumberToUserData(accountNumber);
        }

        // Submit withdrawal request
        submitWithdrawal(amount, accountNumber, accountTitle, bankName);
    }

    private void saveAccountNumberToUserData(String accountNumber) {
        if (currentUser != null) {
            currentUser.setAccountNo(accountNumber);
            preferenceManager.saveUserData(currentUser);
            
            // Also update on server (you may need to create an API endpoint for this)
            // For now, it will be saved locally and the server will get it in the withdrawal request
        }
    }

    private void submitWithdrawal(String amount, String accountNumber, String accountTitle, String bankName) {
        if (apiService == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "API service not available");
            return;
        }

        if (currentUser == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "User not logged in");
            return;
        }

        // Show loading dialog
        loadingDialog = LoadingDialog.show(this, "Submitting withdrawal request...");

        // Prepare withdrawal data
        Map<String, String> withdrawalData = new HashMap<>();
        withdrawalData.put("amount", amount);
        withdrawalData.put("account_number", accountNumber);
        withdrawalData.put("account_title", accountTitle);
        withdrawalData.put("bank_name", bankName);

        // Get auth token
        String token = "Bearer " + preferenceManager.getAuthToken();

        // Submit withdrawal request
        Call<ApiResponse<Withdrawal>> call = apiService.createWithdrawal(token, withdrawalData);

        call.enqueue(new Callback<ApiResponse<Withdrawal>>() {
            @Override
            public void onResponse(Call<ApiResponse<Withdrawal>> call, Response<ApiResponse<Withdrawal>> response) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Withdrawal> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        SnackbarUtils.showSuccess(findViewById(android.R.id.content),
                            apiResponse.getMessage() != null ? apiResponse.getMessage() : "Withdrawal request submitted successfully");
                        
                        // Delay navigation to show success message
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            // Navigate to Withdrawals History
                            Intent intent = new Intent(WithdrawalActivity.this, WithdrawalsActivity.class);
                            startActivity(intent);
                            finish();
                        }, 2000); // 2 seconds delay
                    } else {
                        SnackbarUtils.showError(findViewById(android.R.id.content),
                            apiResponse.getMessage() != null ? apiResponse.getMessage() : "Failed to submit withdrawal request");
                    }
                } else {
                    String errorMessage = "Failed to submit withdrawal request";
                    
                    if (response.code() == 400) {
                        errorMessage = "Insufficient balance or invalid data";
                    } else if (response.code() == 401) {
                        errorMessage = "Session expired. Please login again";
                    } else if (response.code() == 422) {
                        errorMessage = "Validation failed. Please check your inputs";
                    }
                    
                    SnackbarUtils.showError(findViewById(android.R.id.content), errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Withdrawal>> call, Throwable t) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                
                SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Network error: " + t.getMessage());
            }
        });
    }
}
