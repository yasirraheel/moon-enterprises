package com.geo.enterprises.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.geo.enterprises.R;
import com.geo.enterprises.dashboard.DashboardActivity;
import com.geo.enterprises.notifications.NotificationsActivity;
import com.geo.enterprises.orders.YourOrdersActivity;
import com.geo.enterprises.utils.ActivityTransitionUtils;
import com.geo.enterprises.utils.ConfirmationDialog;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ivBack, ivProfileImage;
    private com.google.android.material.textfield.TextInputEditText etName, etPhone, etCity, etPassword;
    private com.google.android.material.button.MaterialButton btnChangeImage, btnUpdateProfile, btnLogout;
    private PreferenceManager preferenceManager;
    private com.geo.enterprises.api.ApiService apiService;
    private android.net.Uri selectedImageUri;
    private com.geo.enterprises.utils.LoadingDialog loadingDialog;

    // Dealer badge views
    private View llDealerBadge;
    private TextView tvDealerBadgeText, tvProfileName, tvProfilePhone;

    // Bottom Navigation Views
    private View navHome, navNotifications, navOrders, navSettings;
    private ImageView ivNavHome, ivNavNotifications, ivNavOrders, ivNavSettings;
    private TextView tvNavHome, tvNavNotifications, tvNavOrders, tvNavSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        
        preferenceManager = new PreferenceManager(this);
        apiService = com.geo.enterprises.api.ApiClient.getInstance().getApiService();
        
        loadUserProfile();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        ivProfileImage = findViewById(R.id.iv_profile_image);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etCity = findViewById(R.id.et_city);
        etPassword = findViewById(R.id.et_password);
        btnChangeImage = findViewById(R.id.btn_change_image);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
        btnLogout = findViewById(R.id.btn_logout);

        // Dealer badge views
        llDealerBadge = findViewById(R.id.ll_dealer_badge);
        tvDealerBadgeText = findViewById(R.id.tv_dealer_badge_text);
        tvProfileName = findViewById(R.id.tv_profile_name);
        tvProfilePhone = findViewById(R.id.tv_profile_phone);

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
        
        btnChangeImage.setOnClickListener(v -> {
            openImagePicker();
        });
        
        btnUpdateProfile.setOnClickListener(v -> {
            updateUserProfile();
        });
        
        btnLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void showLogoutConfirmationDialog() {
        new ConfirmationDialog(this)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout from your account?")
            .setIcon(R.drawable.ic_logout)
            .setConfirmButtonText("Logout")
            .setCancelButtonText("Cancel")
            .setOnConfirmListener(new ConfirmationDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    performLogout();
                }
                
                @Override
                public void onCancel() {
                    // User cancelled, do nothing
                }
            })
            .show();
    }

    private void loadUserProfile() {
        if (preferenceManager != null) {
            com.geo.enterprises.models.User user = preferenceManager.getUserData();
            if (user != null) {
                android.util.Log.d("ProfileLoad", "Loading user profile");
                android.util.Log.d("ProfileLoad", "User avatar value: " + user.getAvatar());

                etName.setText(user.getFullName());
                etPhone.setText(user.getPhone());
                etCity.setText(user.getCity());

                // Update profile name with user's name
                if (tvProfileName != null) {
                    tvProfileName.setText(user.getFullName());
                }

                // Update profile phone display
                if (tvProfilePhone != null) {
                    tvProfilePhone.setText(user.getPhone());
                }

                // Update dealer badge
                updateDealerUI(user);

                // Load profile image - simple approach
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    String avatarFilename = user.getAvatar();
                    
                    android.util.Log.d("ProfileLoad", "Avatar filename: " + avatarFilename);
                    
                    // Build avatar URL using AppConfig
                    String avatarUrl = com.geo.enterprises.config.AppConfig.getAvatarUrl(avatarFilename);
                    
                    final String finalAvatarUrl = avatarUrl;
                    android.util.Log.d("ProfileLoad", "Final avatar URL: " + finalAvatarUrl);
                    
                    com.bumptech.glide.Glide.with(this)
                            .load(finalAvatarUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_account)
                            .error(R.drawable.ic_account)
                            .skipMemoryCache(true)
                            .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                                @Override
                                public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                    android.util.Log.e("ProfileLoad", "Glide failed to load avatar: " + finalAvatarUrl);
                                    if (e != null) {
                                        android.util.Log.e("ProfileLoad", "Error: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    android.util.Log.d("ProfileLoad", "Glide successfully loaded avatar: " + finalAvatarUrl);
                                    return false;
                                }
                            })
                            .into(ivProfileImage);
                } else {
                    android.util.Log.d("ProfileLoad", "No avatar found, using default");
                    ivProfileImage.setImageResource(R.drawable.ic_account);
                }
            }
        }
    }

    private void updateDealerUI(com.geo.enterprises.models.User user) {
        if (user == null) return;

        String status = user.getDealerStatus();
        if (status == null || "null".equals(status)) status = "na";
        status = status.trim().toLowerCase();

        if ("approved".equals(status)) {
            // Show dealer badge
            if (llDealerBadge != null) {
                llDealerBadge.setVisibility(View.VISIBLE);

                if (tvDealerBadgeText != null) {
                    double commission = user.getDealerCommission();
                    String badgeText = "Dealer";
                    if (commission > 0) {
                        String commissionText = commission == Math.floor(commission)
                            ? String.format("%.0f%%", commission)
                            : String.format("%.1f%%", commission);
                        badgeText += " (" + commissionText + ")";
                    }
                    tvDealerBadgeText.setText(badgeText);
                }
            }
        } else {
            // Hide dealer badge
            if (llDealerBadge != null) {
                llDealerBadge.setVisibility(View.GONE);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                com.bumptech.glide.Glide.with(this)
                        .load(selectedImageUri)
                        .circleCrop()
                        .into(ivProfileImage);
            }
        }
    }

    private void updateUserProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "Please enter your name");
            return;
        }

        if (phone.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "Please enter your phone number");
            return;
        }

        if (city.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "Please enter your city");
            return;
        }

        // Show loading
        loadingDialog = com.geo.enterprises.utils.LoadingDialog.show(this, "Updating profile...");

        // Create multipart request for image upload
        if (selectedImageUri != null) {
            uploadProfileWithImage(name, phone, city, password);
        } else {
            updateProfileWithoutImage(name, phone, city, password);
        }
    }

    private void uploadProfileWithImage(String name, String phone, String city, String password) {
        try {
            // Get the actual file path from URI
            String filePath = getRealPathFromURI(selectedImageUri);
            if (filePath == null) {
                SnackbarUtils.showError(findViewById(android.R.id.content), "Could not access selected image");
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                return;
            }

            java.io.File imageFile = new java.io.File(filePath);
            if (!imageFile.exists()) {
                SnackbarUtils.showError(findViewById(android.R.id.content), "Image file not found");
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                return;
            }

            // Create multipart request body
            okhttp3.MultipartBody.Builder builder = new okhttp3.MultipartBody.Builder()
                    .setType(okhttp3.MultipartBody.FORM);

            // Add text fields
            builder.addFormDataPart("full_name", name);
            builder.addFormDataPart("phone", phone);
            builder.addFormDataPart("city", city);
            if (!password.isEmpty()) {
                builder.addFormDataPart("password", password);
            }

            // Add image file
            okhttp3.RequestBody imageBody = okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse("image/*"), imageFile);
            builder.addFormDataPart("avatar", imageFile.getName(), imageBody);

            okhttp3.MultipartBody requestBody = builder.build();

            // Create custom OkHttp client for multipart upload
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(com.geo.enterprises.config.AppConfig.API_BASE_URL + "profile")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer " + preferenceManager.getAuthToken())
                    .addHeader("Accept", "application/json")
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                    runOnUiThread(() -> {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }

                        if (response.isSuccessful()) {
                            try {
                                String responseBody = response.body().string();
                                android.util.Log.d("ProfileUpdate", "Response: " + responseBody);
                                
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                com.geo.enterprises.models.ApiResponse apiResponse = gson.fromJson(responseBody, com.geo.enterprises.models.ApiResponse.class);

                                if (apiResponse.isSuccess()) {
                                    // Parse the response data to get updated user info
                                    try {
                                        com.google.gson.JsonObject jsonObject = gson.fromJson(responseBody, com.google.gson.JsonObject.class);
                                        if (jsonObject.has("data")) {
                                            com.google.gson.JsonObject dataObject = jsonObject.getAsJsonObject("data");
                                            String updatedAvatar = dataObject.has("avatar") && !dataObject.get("avatar").isJsonNull() 
                                                ? dataObject.get("avatar").getAsString() : null;
                                            
                                            android.util.Log.d("ProfileUpdate", "Avatar from API: " + updatedAvatar);
                                            
                                            // Extract filename using AppConfig
                                            if (updatedAvatar != null && !updatedAvatar.isEmpty()) {
                                                updatedAvatar = com.geo.enterprises.config.AppConfig.extractAvatarFilename(updatedAvatar);
                                                android.util.Log.d("ProfileUpdate", "Avatar filename extracted: " + updatedAvatar);
                                            }
                                            
                                            // Update local user data
                                            com.geo.enterprises.models.User user = preferenceManager.getUserData();
                                            if (user != null) {
                                                user.setFullName(name);
                                                user.setPhone(phone);
                                                user.setCity(city);
                                                if (updatedAvatar != null && !updatedAvatar.isEmpty()) {
                                                    user.setAvatar(updatedAvatar);
                                                    android.util.Log.d("ProfileUpdate", "Saved avatar to user: " + updatedAvatar);
                                                }
                                                preferenceManager.saveUserData(user);
                                                android.util.Log.d("ProfileUpdate", "User data saved with avatar: " + user.getAvatar());
                                            }
                                        }
                                    } catch (Exception e) {
                                        android.util.Log.e("ProfileUpdate", "Error parsing user data: " + e.getMessage());
                                        e.printStackTrace();
                                    }

                                    SnackbarUtils.showSuccess(findViewById(android.R.id.content), "Profile updated successfully");
                                    etPassword.setText(""); // Clear password field
                                    selectedImageUri = null; // Clear selected image
                                    
                                    // Reload profile image
                                    loadUserProfile();
                                } else {
                                    SnackbarUtils.showError(findViewById(android.R.id.content), apiResponse.getMessage());
                                }
                            } catch (Exception e) {
                                android.util.Log.e("ProfileUpdate", "Parse error: " + e.getMessage());
                                SnackbarUtils.showError(findViewById(android.R.id.content), "Failed to parse response: " + e.getMessage());
                            }
                        } else {
                            android.util.Log.e("ProfileUpdate", "HTTP Error: " + response.code() + " - " + response.message());
                            SnackbarUtils.showError(findViewById(android.R.id.content), "Failed to update profile: " + response.code());
                        }
                    });
                }

                @Override
                public void onFailure(okhttp3.Call call, java.io.IOException e) {
                    runOnUiThread(() -> {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                        android.util.Log.e("ProfileUpdate", "Network error: " + e.getMessage());
                        SnackbarUtils.showError(findViewById(android.R.id.content), "Network error: " + e.getMessage());
                    });
                }
            });

        } catch (Exception e) {
            android.util.Log.e("ProfileUpdate", "Upload error: " + e.getMessage());
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            SnackbarUtils.showError(findViewById(android.R.id.content), "Error preparing image upload: " + e.getMessage());
        }
    }

    private String getRealPathFromURI(android.net.Uri uri) {
        String result = null;
        android.database.Cursor cursor = null;
        try {
            String[] proj = {android.provider.MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }
        } catch (Exception e) {
            android.util.Log.e("ProfileUpdate", "Error getting file path: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private void updateProfileWithoutImage(String name, String phone, String city, String password) {
        // Create update request
        com.geo.enterprises.models.UpdateProfileRequest request = new com.geo.enterprises.models.UpdateProfileRequest();
        request.setFullName(name);
        request.setPhone(phone);
        request.setCity(city);
        if (!password.isEmpty()) {
            request.setPassword(password);
        }

        if (apiService != null) {
            String token = "Bearer " + preferenceManager.getAuthToken();
            retrofit2.Call<com.geo.enterprises.models.ApiResponse<Void>> call = apiService.updateProfile(token, request);
            call.enqueue(new retrofit2.Callback<com.geo.enterprises.models.ApiResponse<Void>>() {
                @Override
                public void onResponse(retrofit2.Call<com.geo.enterprises.models.ApiResponse<Void>> call, retrofit2.Response<com.geo.enterprises.models.ApiResponse<Void>> response) {
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }
                    
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            // Update local user data
                            com.geo.enterprises.models.User user = preferenceManager.getUserData();
                            if (user != null) {
                                user.setFullName(name);
                                user.setPhone(phone);
                                user.setCity(city);
                                preferenceManager.saveUserData(user);
                            }
                            
                            SnackbarUtils.showSuccess(findViewById(android.R.id.content), "Profile updated successfully");
                            etPassword.setText(""); // Clear password field
                        } else {
                            SnackbarUtils.showError(findViewById(android.R.id.content), response.body().getMessage());
                        }
                    } else {
                        SnackbarUtils.showError(findViewById(android.R.id.content), "Failed to update profile");
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.geo.enterprises.models.ApiResponse<Void>> call, Throwable t) {
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }
                    SnackbarUtils.showError(findViewById(android.R.id.content), "Network error: " + t.getMessage());
                }
            });
        } else {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            SnackbarUtils.showError(findViewById(android.R.id.content), "Service not available");
        }
    }

    private void performLogout() {
        // Clear user data
        preferenceManager.clearUserData();
        
        // Show logout success message
        SnackbarUtils.showLogoutSuccess(findViewById(android.R.id.content));
        
        // Navigate to login after a short delay
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(this, com.geo.enterprises.auth.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void setupBottomNavigation() {
        // Set Settings as selected by default
        setSelectedTab(navHome, ivNavHome, tvNavHome, false);
        setSelectedTab(navNotifications, ivNavNotifications, tvNavNotifications, false);
        setSelectedTab(navOrders, ivNavOrders, tvNavOrders, false);
        setSelectedTab(navSettings, ivNavSettings, tvNavSettings, true);
        
        // Setup click listeners
        navHome.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInLeftAndFinish(this, new Intent(this, DashboardActivity.class));
        });
        
        navNotifications.setOnClickListener(v -> {
            ActivityTransitionUtils.slideInRightAndFinish(this, new Intent(this, NotificationsActivity.class));
        });
        
        navOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, YourOrdersActivity.class));
        });
        
        navSettings.setOnClickListener(v -> {
            // Already on settings, just update selection
            setSelectedTab(navHome, ivNavHome, tvNavHome, false);
            setSelectedTab(navNotifications, ivNavNotifications, tvNavNotifications, false);
            setSelectedTab(navOrders, ivNavOrders, tvNavOrders, false);
            setSelectedTab(navSettings, ivNavSettings, tvNavSettings, true);
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
        ActivityTransitionUtils.slideOutLeft(this);
    }
}
