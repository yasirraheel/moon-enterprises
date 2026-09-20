package com.geo.enterprises;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.auth.LoginActivity;
import com.geo.enterprises.dashboard.DashboardActivity;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.AppSettings;
import com.geo.enterprises.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private static final int PERMISSION_REQUEST_CODE = 101;
    
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    
    // UI Views
    private ImageView ivAppLogo;
    private TextView tvAppTitle, tvAppTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Log notification click debug info
        android.util.Log.d("NotificationClick", "=== MainActivity onCreate ===");
        android.util.Log.d("NotificationClick", "Intent: " + (getIntent() != null ? getIntent().toString() : "null"));
        if (getIntent() != null && getIntent().getExtras() != null) {
            android.util.Log.d("NotificationClick", "Intent extras: " + getIntent().getExtras().toString());
            if (getIntent().hasExtra("FROM_NOTIFICATION")) {
                android.util.Log.d("NotificationClick", "FROM_NOTIFICATION detected: " + getIntent().getBooleanExtra("FROM_NOTIFICATION", false));
            }
        }

        initializeViews();
        preferenceManager = new PreferenceManager(this);
        apiService = ApiClient.getInstance().getApiService();

        checkPermissionsAndStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        android.util.Log.d("NotificationClick", "=== MainActivity onNewIntent ===");
        android.util.Log.d("NotificationClick", "Intent: " + (intent != null ? intent.toString() : "null"));
        if (intent != null && intent.getExtras() != null) {
            android.util.Log.d("NotificationClick", "Intent extras: " + intent.getExtras().toString());
            if (intent.hasExtra("FROM_NOTIFICATION")) {
                android.util.Log.d("NotificationClick", "FROM_NOTIFICATION detected: " + intent.getBooleanExtra("FROM_NOTIFICATION", false));
            }
        }
        checkPermissionsAndStart();
    }
    
    private void checkPermissionsAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("MainActivity", "Requesting POST_NOTIFICATIONS permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                return;
            }
        }
        // Permission granted or not needed
        loadAppSettings();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("MainActivity", "Notification permission GRANTED");
            } else {
                android.util.Log.d("MainActivity", "Notification permission DENIED");
            }
            // Proceed with app flow regardless of decision
            loadAppSettings();
        }
    }
    
    private void initializeViews() {
        ivAppLogo = findViewById(R.id.iv_app_logo);
        tvAppTitle = findViewById(R.id.tv_app_title);
        tvAppTagline = findViewById(R.id.tv_app_tagline);

        // Set a stylish placeholder immediately — never use the app icon
        ivAppLogo.setImageDrawable(
                androidx.core.content.ContextCompat.getDrawable(this, R.drawable.splash_logo)
        );
    }
    
    private void loadAppSettings() {
        apiService.getSettings().enqueue(new Callback<ApiResponse<AppSettings>>() {
            @Override
            public void onResponse(Call<ApiResponse<AppSettings>> call, Response<ApiResponse<AppSettings>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AppSettings settings = response.body().getData();
                    if (settings != null) {
                        updateSplashContent(settings);
                        // Save settings for use in other activities
                        preferenceManager.saveAppSettings(settings);
                    }
                }
                // Navigate after loading (or timeout)
                navigateToNextActivity();
            }

            @Override
            public void onFailure(Call<ApiResponse<AppSettings>> call, Throwable t) {
                // Navigate even if settings fail to load
                navigateToNextActivity();
            }
        });
    }
    
    private void updateSplashContent(AppSettings settings) {
        // Update app title
        if (settings.getAppName() != null && !settings.getAppName().isEmpty()) {
            tvAppTitle.setText(settings.getAppName());
        }
        
        // Update app tagline
        if (settings.getAppTagline() != null && !settings.getAppTagline().isEmpty()) {
            tvAppTagline.setText(settings.getAppTagline());
        }
        
        // Always load logo from server; use styled placeholder (not app icon) as fallback
        String logoUrl = settings.getAppLogo();
        if (logoUrl != null && !logoUrl.isEmpty()) {
            // Build full URL if needed
            if (!logoUrl.startsWith("http")) {
                logoUrl = com.geo.enterprises.config.AppConfig.PUBLIC_BASE_URL + "/" + logoUrl;
            }
            Glide.with(this)
                    .load(logoUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.splash_logo)
                            .error(R.drawable.splash_logo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter())
                    .into(ivAppLogo);
        }
        // If no logo URL provided, the splash_placeholder set in initializeViews() remains
    }
    
    private void navigateToNextActivity() {
        // Check if this was launched from a notification
        boolean fromNotification = getIntent() != null && getIntent().hasExtra("FROM_NOTIFICATION");

        // Skip splash delay if coming from notification for instant response
        long delay = fromNotification ? 0 : SPLASH_DELAY;

        if (fromNotification) {
            android.util.Log.d("NotificationClick", "Skipping splash delay - navigating immediately");
        }

        new Handler().postDelayed(() -> {
            if (preferenceManager.isLoggedIn()) {
                // User is already logged in, go to dashboard
                Intent dashboardIntent = new Intent(this, DashboardActivity.class);

                // Pass notification extras if this was launched from a notification
                if (fromNotification) {
                    android.util.Log.d("NotificationClick", "Passing notification extras to DashboardActivity");
                    dashboardIntent.putExtras(getIntent().getExtras());
                }

                android.util.Log.d("NotificationClick", "Starting DashboardActivity from MainActivity");
                startActivity(dashboardIntent);
            } else {
                // User is not logged in, go to login
                android.util.Log.d("NotificationClick", "User not logged in, redirecting to LoginActivity");
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, delay);
    }
}
