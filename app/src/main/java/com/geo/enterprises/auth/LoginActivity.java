package com.geo.enterprises.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.geo.enterprises.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.AppSettings;
import com.geo.enterprises.models.LoginRequest;
import com.geo.enterprises.models.LoginResponse;
import com.geo.enterprises.utils.ActivityTransitionUtils;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.BaseActivity;
import com.geo.enterprises.dashboard.DashboardActivity;
import com.geo.enterprises.fcm.FcmTokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private CheckBox cbRememberMe;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    
    // Dynamic branding views
    private ImageView ivAppLogo;
    private TextView tvAppTitle, tvAppTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_compact);
        
        // Initialize managers and services first
        apiService = ApiClient.getInstance().getApiService();
        preferenceManager = new PreferenceManager(this);
        
        initializeViews();
        setupClickListeners();
        loadDynamicBranding();
        
        // Check if user is already logged in
        if (preferenceManager.isLoggedIn()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }
    
    private void initializeViews() {
        etUsername = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        progressBar = findViewById(R.id.progress_bar);
        
        // Dynamic branding views
        ivAppLogo = findViewById(R.id.iv_app_logo);
        tvAppTitle = findViewById(R.id.tv_app_title);
        tvAppTagline = findViewById(R.id.tv_app_tagline);
        
        // Password toggle is now handled by TextInputLayout
        // No need to find ivPasswordToggle
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        tvRegister.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInRight(this, new Intent(this, RegisterActivity.class));
        });
        
        tvForgotPassword.setOnClickListener(v -> {
            showForgotPasswordDialog();
        });
        
        // Password toggle is now handled automatically by TextInputLayout
        // No need for manual toggle functionality
    }
    
    // Password toggle is now handled automatically by TextInputLayout
    // No need for manual toggle functionality
    
    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Phone number is required");
            etUsername.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        
        showLoading(true);
        
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        Call<ApiResponse<LoginResponse>> call = apiService.login(loginRequest);
        call.enqueue(createMaintenanceAwareCallback(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                showLoading(false);
                
                // Debug logging
                android.util.Log.d("LoginActivity", "Response code: " + response.code());
                android.util.Log.d("LoginActivity", "Response body: " + response.body());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        LoginResponse loginResponse = apiResponse.getData();
                        
                        // Save user data and token
                        preferenceManager.saveUserData(loginResponse);
                        preferenceManager.saveAuthToken(loginResponse.getToken());
                        preferenceManager.setLoggedIn(true);
                        preferenceManager.setJustLoggedIn(true); // Flag to show success message

                        // Initialize FCM first
                        FcmTokenManager.initializeFcm(LoginActivity.this);

                        // Check and request notification permission before navigating
                        checkAndRequestNotificationPermission();

            // Navigate to dashboard
            ActivityTransitionUtils.fadeInAndFinish(LoginActivity.this, new Intent(LoginActivity.this, DashboardActivity.class));
                        
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Login failed";
                        android.util.Log.e("LoginActivity", "API Error: " + errorMsg);
                        SnackbarUtils.showApiError(findViewById(android.R.id.content), errorMsg);
                    }
                } else {
                    String errorMsg = "Login failed. Response code: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " Error: " + response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg += " Error parsing response";
                        }
                    }
                    android.util.Log.e("LoginActivity", errorMsg);
                    SnackbarUtils.showApiError(findViewById(android.R.id.content), errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                showLoading(false);
                android.util.Log.e("LoginActivity", "Network error: " + t.getMessage(), t);
                SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
            }
        }));
    }
    
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("Logging in...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }
    
    private void loadDynamicBranding() {
        // Try to get cached settings first
        if (preferenceManager != null) {
            AppSettings settings = preferenceManager.getAppSettings();
            if (settings != null) {
                updateBranding(settings);
            }
        }
        
        // Load fresh settings from API
        apiService.getSettings().enqueue(createMaintenanceAwareCallback(new Callback<ApiResponse<AppSettings>>() {
            @Override
            public void onResponse(Call<ApiResponse<AppSettings>> call, Response<ApiResponse<AppSettings>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AppSettings settings = response.body().getData();
                            if (settings != null) {
                                updateBranding(settings);
                                // Cache the settings
                                if (preferenceManager != null) {
                                    preferenceManager.saveAppSettings(settings);
                                }
                            }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AppSettings>> call, Throwable t) {
                // Ignore failure, use default branding
            }
        }));
    }
    
    private void updateBranding(AppSettings settings) {
        // Update app title
        if (settings.getAppName() != null && !settings.getAppName().isEmpty()) {
            tvAppTitle.setText(settings.getAppName());
        }
        
        // Update app tagline
        if (settings.getAppTagline() != null && !settings.getAppTagline().isEmpty()) {
            tvAppTagline.setText(settings.getAppTagline());
        }
        
        // Update app logo
        if (settings.getAppLogo() != null && !settings.getAppLogo().isEmpty()) {
                    Glide.with(this)
                            .load(settings.getAppLogo())
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_launcher_foreground)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .circleCrop())
                            .into(ivAppLogo);
        }
    }
    
    private void showForgotPasswordDialog() {
        // Inflate custom forgot password layout
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_forgot_password_whatsapp, null);
        
        // Get views from the layout
        com.google.android.material.textfield.TextInputEditText etPhone = dialogView.findViewById(R.id.et_forgot_password_phone);
        com.google.android.material.button.MaterialButton btnCancel = dialogView.findViewById(R.id.btn_forgot_password_cancel);
        com.google.android.material.button.MaterialButton btnSend = dialogView.findViewById(R.id.btn_forgot_password_send);
        
        // Create and show dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false); // Make dialog non-cancelable
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Set button click listeners
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSend.setOnClickListener(v -> {
            String phoneNumber = etPhone.getText().toString().trim();
            
            if (phoneNumber.isEmpty()) {
                etPhone.setError("Phone number is required");
                etPhone.requestFocus();
                return;
            }
            
            // Get admin WhatsApp number from settings
            AppSettings settings = preferenceManager.getAppSettings();
            String adminWhatsApp = null;
            
            if (settings != null && settings.getWhatsappNumber() != null) {
                adminWhatsApp = settings.getWhatsappNumber();
            }
            
            if (adminWhatsApp == null || adminWhatsApp.isEmpty()) {
                SnackbarUtils.showError(findViewById(android.R.id.content), 
                    "WhatsApp support is not configured. Please contact admin.");
                return;
            }
            
            // Create pre-filled message
            String message = "Hello Admin,\n\n" +
                    "I would like to request a password reset for my account.\n\n" +
                    "My registered phone number: " + phoneNumber + "\n\n" +
                    "Please help me reset my password.\n\n" +
                    "Thank you!";
            
            // Open WhatsApp with pre-filled message
            openWhatsApp(adminWhatsApp, message);
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void openWhatsApp(String phoneNumber, String message) {
        try {
            // Remove any non-numeric characters from phone number
            String cleanNumber = phoneNumber.replaceAll("[^0-9+]", "");
            
            // Encode the message for URL
            String encodedMessage = android.net.Uri.encode(message);
            
            // Create WhatsApp intent with API URL
            String url = "https://api.whatsapp.com/send?phone=" + cleanNumber + "&text=" + encodedMessage;
            
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(url));
            intent.setPackage("com.whatsapp"); // Specify WhatsApp package
            
            try {
                startActivity(intent);
                SnackbarUtils.showSuccess(findViewById(android.R.id.content), 
                    "Opening WhatsApp...");
            } catch (android.content.ActivityNotFoundException e) {
                // WhatsApp not installed, try without package specification (will open in browser)
                intent.setPackage(null);
                startActivity(intent);
                SnackbarUtils.showSuccess(findViewById(android.R.id.content), 
                    "Opening WhatsApp Web...");
            }
        } catch (Exception e) {
            android.util.Log.e("LoginActivity", "Error opening WhatsApp", e);
            SnackbarUtils.showError(findViewById(android.R.id.content),
                "Failed to open WhatsApp: " + e.getMessage());
        }
    }

    private void checkAndRequestNotificationPermission() {
        // Log for debugging
        android.util.Log.d("LoginActivity", "Checking notification permission...");

        // Check if we should ask for permission
        boolean shouldAsk = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+, check runtime permission
            boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;

            android.util.Log.d("LoginActivity", "Android 13+, has permission: " + hasPermission);

            if (!hasPermission) {
                // Permission not granted, check if we should ask
                shouldAsk = preferenceManager.shouldAskForNotificationPermission();
                android.util.Log.d("LoginActivity", "Should ask: " + shouldAsk);
            }
        } else {
            // For Android < 13, always ask once to educate user (notifications work automatically)
            shouldAsk = preferenceManager.shouldAskForNotificationPermission();
            android.util.Log.d("LoginActivity", "Android < 13, should ask: " + shouldAsk);
        }

        if (shouldAsk) {
            android.util.Log.d("LoginActivity", "Showing notification permission dialog");
            // Mark that we're asking now (before showing dialog to prevent multiple requests)
            preferenceManager.saveLastNotificationPermissionRequestTime();
        }
    }
}
