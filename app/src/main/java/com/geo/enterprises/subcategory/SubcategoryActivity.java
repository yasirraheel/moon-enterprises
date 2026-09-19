package com.geo.enterprises.subcategory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.Subcategory;
import com.geo.enterprises.models.SubcategoryResponse;
import com.geo.enterprises.utils.LoadingDialog;
import com.geo.enterprises.utils.WindowInsetsHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubcategoryActivity extends AppCompatActivity {
    
    // UI Components
    private ImageView ivBack;
    private TextView tvCategoryTitle;
    private TextView tvStartDateTime;
    private TextView tvCloseDateTime;
    private RecyclerView rvSubcategories;
    private View cardEmptySubcategories;
    
    // Data
    private int gameId;
    private String gameName;
    private String gameImage;
    private String gameDate;
    private String gameTime;
    private String gameDateTime;
    
    // Adapter and API
    private SubcategoryAdapter subcategoryAdapter;
    private List<Subcategory> subcategoryList;
    private ApiService apiService;
    private LoadingDialog loadingDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);
        
        // Get intent data
        getIntentData();
        
        // Initialize components
        initializeViews();
        initializeApi();
        setupRecyclerView();
        setupClickListeners();
        
        // Load subcategories
        loadSubcategories();
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        gameId = intent.getIntExtra("game_id", 0);
        gameName = intent.getStringExtra("game_name");
        gameImage = intent.getStringExtra("game_image");
        gameDate = intent.getStringExtra("game_date");
        gameTime = intent.getStringExtra("game_time");
        gameDateTime = intent.getStringExtra("game_datetime");
        
        android.util.Log.d("BondsActivity", "=== RECEIVED INTENT DATA ===");
        android.util.Log.d("BondsActivity", "Game ID: " + gameId);
        android.util.Log.d("BondsActivity", "Game Name: " + gameName);
        android.util.Log.d("BondsActivity", "Game Image: " + gameImage);
        android.util.Log.d("BondsActivity", "Game Date: " + gameDate);
        android.util.Log.d("BondsActivity", "Game Time: " + gameTime);
        android.util.Log.d("BondsActivity", "Game DateTime: " + gameDateTime);
        android.util.Log.d("BondsActivity", "============================");
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        tvCategoryTitle = findViewById(R.id.tv_category_title);
        tvStartDateTime = findViewById(R.id.tv_start_datetime);
        tvCloseDateTime = findViewById(R.id.tv_close_datetime);
        rvSubcategories = findViewById(R.id.rv_subcategories);
        cardEmptySubcategories = findViewById(R.id.card_empty_subcategories);

        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
        
        // Set game title
        if (gameName != null) {
            tvCategoryTitle.setText(gameName);
        }
        
        // Set main game's date and time (this is what shows at the top)
        if (gameDate != null && !gameDate.equals("Not Available")) {
            tvStartDateTime.setText("Date: " + gameDate);
        } else {
            tvStartDateTime.setText("Date: Not Available");
        }
        
        if (gameTime != null && !gameTime.equals("Not Available")) {
            tvCloseDateTime.setText("Time: " + gameTime);
        } else {
            tvCloseDateTime.setText("Time: Not Available");
        }
    }
    
    private void initializeApi() {
        apiService = ApiClient.getInstance().getApiService();
    }
    
    private void setupRecyclerView() {
        subcategoryList = new ArrayList<>();
        subcategoryAdapter = new SubcategoryAdapter(subcategoryList, new SubcategoryAdapter.OnSubcategoryClickListener() {
            @Override
            public void onSubcategoryClick(Subcategory subcategory) {
                // Validate before navigating
                if (validateSubcategoryAvailability(subcategory)) {
                    navigateToOrders(subcategory);
                }
            }
        });
        
        // Pass the game image to the adapter
        if (gameImage != null) {
            subcategoryAdapter.setGameImage(gameImage);
        }
        
        rvSubcategories.setLayoutManager(new LinearLayoutManager(this));
        rvSubcategories.setAdapter(subcategoryAdapter);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }
    
    private void loadSubcategories() {
        if (apiService == null) {
            android.util.Log.e("SubcategoryActivity", "API Service is null");
            showEmptyState();
            return;
        }
        
        if (gameId == 0) {
            android.util.Log.e("BondsActivity", "Invalid game ID");
            showEmptyState();
            return;
        }
        
        android.util.Log.d("BondsActivity", "Loading bonds for game ID: " + gameId);
        
        // Show loading dialog
        loadingDialog = LoadingDialog.show(this);
        
        apiService.getSubcategories(gameId).enqueue(new Callback<SubcategoryResponse>() {
            @Override
            public void onResponse(Call<SubcategoryResponse> call, Response<SubcategoryResponse> response) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                
                android.util.Log.d("SubcategoryActivity", "Response received: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    SubcategoryResponse subcategoryResponse = response.body();
                    android.util.Log.d("SubcategoryActivity", "Success: " + subcategoryResponse.isSuccess());
                    
                    if (subcategoryResponse.isSuccess()) {
                        List<Subcategory> subcategories = subcategoryResponse.getData();
                        android.util.Log.d("SubcategoryActivity", "Subcategories count: " + (subcategories != null ? subcategories.size() : 0));
                        
                        if (subcategories != null && !subcategories.isEmpty()) {
                            // Update adapter with subcategories
                            subcategoryAdapter.updateSubcategories(subcategories);
                            hideEmptyState();
                            
                            // Log all subcategories
                            for (int i = 0; i < subcategories.size(); i++) {
                                Subcategory sub = subcategories.get(i);
                                android.util.Log.d("SubcategoryActivity", "Subcategory " + i + ": " + sub.getName());
                            }
                        } else {
                            android.util.Log.d("SubcategoryActivity", "No subcategories found");
                            showEmptyState();
                        }
                    } else {
                        android.util.Log.e("SubcategoryActivity", "API success=false");
                        showEmptyState();
                    }
                } else {
                    android.util.Log.e("SubcategoryActivity", "API response failed: " + response.code());
                    showEmptyState();
                }
            }
            
            @Override
            public void onFailure(Call<SubcategoryResponse> call, Throwable t) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                
                android.util.Log.e("SubcategoryActivity", "Network error: " + t.getMessage());
                showEmptyState();
            }
        });
    }
    
    private void showEmptyState() {
        rvSubcategories.setVisibility(View.GONE);
        cardEmptySubcategories.setVisibility(View.VISIBLE);
    }
    
    private void hideEmptyState() {
        rvSubcategories.setVisibility(View.VISIBLE);
        cardEmptySubcategories.setVisibility(View.GONE);
    }
    
    private boolean validateSubcategoryAvailability(Subcategory subcategory) {
        // Check if status is OFF
        if (!subcategory.isActive()) {
            com.geo.enterprises.utils.SnackbarUtils.showWarning(
                findViewById(android.R.id.content),
                "This game is currently closed. Please try again later."
            );
            return false;
        }
        
        // Check if start and close dates/times are available
        if (subcategory.getStartDate() == null || subcategory.getStartDate().trim().isEmpty() ||
            subcategory.getStartTime() == null || subcategory.getStartTime().trim().isEmpty() ||
            subcategory.getCloseDate() == null || subcategory.getCloseDate().trim().isEmpty() ||
            subcategory.getCloseTime() == null || subcategory.getCloseTime().trim().isEmpty()) {
            // If timing info is not available, allow access (backward compatibility)
            return true;
        }
        
        try {
            // Parse start date and time
            String startDateTimeStr = subcategory.getStartDate() + " " + subcategory.getStartTime();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date startDateTime = sdf.parse(startDateTimeStr);
            
            // Parse close date and time
            String closeDateTimeStr = subcategory.getCloseDate() + " " + subcategory.getCloseTime();
            java.util.Date closeDateTime = sdf.parse(closeDateTimeStr);
            
            // Get current date and time
            java.util.Date currentDateTime = new java.util.Date();
            
            // Check if current time is before start time
            if (currentDateTime.before(startDateTime)) {
                com.geo.enterprises.utils.SnackbarUtils.showInfo(
                    findViewById(android.R.id.content),
                    "This game has not started yet. It will open on " + subcategory.getStartDate() + " at " + subcategory.getStartTime()
                );
                return false;
            }
            
            // Check if current time is after close time
            if (currentDateTime.after(closeDateTime)) {
                com.geo.enterprises.utils.SnackbarUtils.showDanger(
                    findViewById(android.R.id.content),
                    "This game has closed. The closing time was " + subcategory.getCloseDate() + " at " + subcategory.getCloseTime()
                );
                return false;
            }
            
        } catch (java.text.ParseException e) {
            android.util.Log.e("SubcategoryActivity", "Error parsing date/time: " + e.getMessage());
            // If parsing fails, allow access (backward compatibility)
            return true;
        }
        
        // All validations passed
        return true;
    }
    
    private void navigateToOrders(Subcategory bond) {
        android.util.Log.d("SubcategoryActivity", "=== NAVIGATING TO ORDERS ===");
        android.util.Log.d("SubcategoryActivity", "Game ID: " + gameId);
        android.util.Log.d("SubcategoryActivity", "Game Name: " + gameName);
        android.util.Log.d("SubcategoryActivity", "Bond ID: " + bond.getId());
        android.util.Log.d("SubcategoryActivity", "Bond Name: " + bond.getName());
        
        Intent intent = new Intent(this, com.geo.enterprises.orders.OrdersActivity.class);
        intent.putExtra("game_id", gameId);
        intent.putExtra("game_name", gameName);
        intent.putExtra("bond_id", String.valueOf(bond.getId()));
        intent.putExtra("bond_name", bond.getName());
        startActivity(intent);
    }
}
