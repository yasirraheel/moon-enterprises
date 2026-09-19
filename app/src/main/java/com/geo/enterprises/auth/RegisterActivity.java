package com.geo.enterprises.auth;

import android.content.Intent;
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

import com.geo.enterprises.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.AppSettings;
import com.geo.enterprises.models.LoginResponse;
import com.geo.enterprises.models.RegisterRequest;
import com.geo.enterprises.utils.ActivityTransitionUtils;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.dashboard.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText etFullName, etPhone, etCity, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private CheckBox cbAgreeTerms;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    
    // Dynamic branding views
    private ImageView ivAppLogo;
    private TextView tvAppTitle, tvAppTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_compact);
        
        // Initialize managers and services first
        apiService = ApiClient.getInstance().getApiService();
        preferenceManager = new PreferenceManager(this);
        
        initializeViews();
        setupClickListeners();
        loadDynamicBranding();
    }
    
    private void initializeViews() {
        etFullName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        etCity = findViewById(R.id.et_city);
        etPassword = findViewById(R.id.et_password);
        // etConfirmPassword = findViewById(R.id.et_confirm_password); // Removed as per request
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        cbAgreeTerms = findViewById(R.id.cb_agree_terms);
        progressBar = findViewById(R.id.progress_bar);
        
        // Dynamic branding views
        ivAppLogo = findViewById(R.id.iv_app_logo);
        tvAppTitle = findViewById(R.id.tv_app_title);
        tvAppTagline = findViewById(R.id.tv_app_tagline);
        
        // Password toggles are now handled by TextInputLayout
        // No need to find ivPasswordToggle and ivConfirmPasswordToggle
    }
    
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> performRegistration());
        tvLogin.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInLeftAndFinish(this, new Intent(this, LoginActivity.class));
        });
        
        // Password toggles are now handled automatically by TextInputLayout
        // No need for manual toggle functionality
    }
    
    // Password toggles are now handled automatically by TextInputLayout
    // No need for manual toggle functionality
    
    private void performRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = password; // Auto-confirm password since field is hidden
        
        // Validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }
        
        // Validate and format phone number for Pakistan
        // Remove any spaces or dashes
        phone = phone.replaceAll("[\\s-]", "");
        
        // Validate format: must be 03XXXXXXXXX (11 digits starting with 03)
        if (!phone.matches("^03[0-9]{9}$")) {
            etPhone.setError("Phone number must be a valid Pakistan mobile number (e.g., 03001234567)");
            etPhone.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(city)) {
            etCity.setError("City is required");
            etCity.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        
        if (password.length() < 8) {
            etPassword.setError("Password must be at least 8 characters");
            etPassword.requestFocus();
            return;
        }
        
        /* Confirm password validation removed since field is hidden
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }
        */
        
        if (!cbAgreeTerms.isChecked()) {
            SnackbarUtils.showWarning(findViewById(android.R.id.content), "Please agree to the terms and conditions");
            return;
        }
        
        showLoading(true);
        
        RegisterRequest registerRequest = new RegisterRequest(fullName, phone, city, password, confirmPassword);
        
        // Log the request data for debugging
        android.util.Log.d("RegisterActivity", "Registering with: ");
        android.util.Log.d("RegisterActivity", "Full Name: " + fullName);
        android.util.Log.d("RegisterActivity", "Phone: " + phone);
        android.util.Log.d("RegisterActivity", "City: " + city);
        android.util.Log.d("RegisterActivity", "Password length: " + password.length());
        
        Call<ApiResponse<LoginResponse>> call = apiService.register(registerRequest);
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        LoginResponse loginResponse = apiResponse.getData();
                        
                        // Save user data and token
                        preferenceManager.saveUserData(loginResponse);
                        preferenceManager.saveAuthToken(loginResponse.getToken());
                        preferenceManager.setLoggedIn(true);
                        preferenceManager.setJustRegistered(true);
                        
                        // Navigate to dashboard
                        ActivityTransitionUtils.fadeInAndFinish(RegisterActivity.this, new Intent(RegisterActivity.this, DashboardActivity.class));
                        
                    } else {
                        // Handle error response with errors object
                        if (apiResponse.getErrors() != null) {
                            handleErrorsObject(apiResponse.getErrors());
                        } else if (apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()) {
                            SnackbarUtils.showError(findViewById(android.R.id.content), apiResponse.getMessage());
                        } else {
                            SnackbarUtils.showError(findViewById(android.R.id.content), "Registration failed. Please try again.");
                        }
                    }
                } else {
                    // Handle error response
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            com.google.gson.JsonObject jsonObject = new com.google.gson.Gson().fromJson(errorBody, com.google.gson.JsonObject.class);
                            
                            // Check for errors object first
                            if (jsonObject.has("errors")) {
                                handleErrorsFromJson(jsonObject.getAsJsonObject("errors"));
                            } else if (jsonObject.has("message")) {
                                String message = jsonObject.get("message").getAsString();
                                SnackbarUtils.showError(findViewById(android.R.id.content), message);
                            } else {
                                SnackbarUtils.showError(findViewById(android.R.id.content), "Registration failed. Please check your information.");
                            }
                        } else if (response.code() == 403) {
                            SnackbarUtils.showError(findViewById(android.R.id.content), "Registration is currently disabled. Please try again later.");
                        } else {
                            SnackbarUtils.showError(findViewById(android.R.id.content), "Registration failed. Error code: " + response.code());
                        }
                    } catch (Exception e) {
                        android.util.Log.e("RegisterActivity", "Error parsing response: " + e.getMessage());
                        SnackbarUtils.showError(findViewById(android.R.id.content), "Registration failed. Please try again.");
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                showLoading(false);
                android.util.Log.e("RegisterActivity", "Network failure: " + t.getMessage());
                SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
            }
        });
    }
    
    private void handleErrorsObject(Object errorsObj) {
        try {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            String json = gson.toJson(errorsObj);
            com.google.gson.JsonObject errors = gson.fromJson(json, com.google.gson.JsonObject.class);
            handleErrorsFromJson(errors);
        } catch (Exception e) {
            android.util.Log.e("RegisterActivity", "Error parsing errors object: " + e.getMessage());
            SnackbarUtils.showError(findViewById(android.R.id.content), "Registration failed. Please try again.");
        }
    }
    
    private void handleErrorsFromJson(com.google.gson.JsonObject errors) {
        try {
            // Handle specific field errors
            if (errors.has("full_name")) {
                String error = errors.getAsJsonArray("full_name").get(0).getAsString();
                etFullName.setError(error);
                etFullName.requestFocus();
                SnackbarUtils.showError(findViewById(android.R.id.content), error);
                android.util.Log.e("RegisterActivity", "Full name error: " + error);
            } else if (errors.has("phone")) {
                String error = errors.getAsJsonArray("phone").get(0).getAsString();
                etPhone.setError(error);
                etPhone.requestFocus();
                SnackbarUtils.showError(findViewById(android.R.id.content), error);
                android.util.Log.e("RegisterActivity", "Phone error: " + error);
            } else if (errors.has("city")) {
                String error = errors.getAsJsonArray("city").get(0).getAsString();
                etCity.setError(error);
                etCity.requestFocus();
                SnackbarUtils.showError(findViewById(android.R.id.content), error);
                android.util.Log.e("RegisterActivity", "City error: " + error);
            } else if (errors.has("password")) {
                String error = errors.getAsJsonArray("password").get(0).getAsString();
                etPassword.setError(error);
                etPassword.requestFocus();
                SnackbarUtils.showError(findViewById(android.R.id.content), error);
                android.util.Log.e("RegisterActivity", "Password error: " + error);
            } else {
                // Show first error found
                String firstKey = errors.keySet().iterator().next();
                String error = errors.getAsJsonArray(firstKey).get(0).getAsString();
                SnackbarUtils.showError(findViewById(android.R.id.content), error);
                android.util.Log.e("RegisterActivity", "Error (" + firstKey + "): " + error);
            }
        } catch (Exception e) {
            android.util.Log.e("RegisterActivity", "Error handling errors JSON: " + e.getMessage());
            SnackbarUtils.showError(findViewById(android.R.id.content), "Please check your information and try again.");
        }
    }
    
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
            btnRegister.setText("Creating Account...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            btnRegister.setText("Register");
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
        apiService.getSettings().enqueue(new Callback<ApiResponse<AppSettings>>() {
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
        });
    }
    
    private void updateBranding(AppSettings settings) {
        // Update app title
        if (settings.getAppName() != null && !settings.getAppName().isEmpty()) {
            tvAppTitle.setText("Join " + settings.getAppName());
        }
        
        // Update app tagline
        if (settings.getAppTagline() != null && !settings.getAppTagline().isEmpty()) {
            tvAppTagline.setText("Create your account today");
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
}
