package com.geo.enterprises.orders;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.DraftOrder;
import com.geo.enterprises.models.Order;
import com.geo.enterprises.models.User;
import com.geo.enterprises.utils.ConfirmationDialog;
import com.geo.enterprises.utils.LoadingDialog;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity implements DraftOrderAdapter.OnItemActionListener {
    
    private static final String TAG = "OrdersActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final float PDF_BOND_WEIGHT = 1.6f;
    private static final float PDF_RTTP_WEIGHT = 1.0f;
    private static final float PDF_FIRST_WEIGHT = 0.9f;
    private static final float PDF_SECOND_WEIGHT = 0.9f;
    private static final float PDF_TOTAL_WEIGHT = 1.0f;
    private static final float PDF_STATUS_WEIGHT = 1.1f;
    
    private TextView tvGameName, tvBondName;
    private EditText etRttp, etFirst, etSecond;
    private EditText etExportName;
    private Button btnAddToDraft, btnBookAll;
    private ImageView ivBack;
    private TextView tvClearAll, tvEmptyState, tvRttpError;
    private TextView tvTotalOrders, tvGrandTotal;

    // RecyclerView for draft orders
    private RecyclerView rvDraftOrders;
    private DraftOrderAdapter draftAdapter;
    private List<DraftOrder> draftOrders;
    private List<DraftOrder> previewOrders;

    // Dealer badge views
    private View llDealerBadge;
    private TextView tvDealerCommission;
    
    // Real-time commission views
    private View llRealtimeCommission;
    private TextView tvRealtimePayable, tvRealtimeNote;
    
    private String gameId;
    private String gameName;
    private String bondId;
    private String bondName;
    
    private PreferenceManager preferenceManager;
    private User currentUser;
    private ApiService apiService;
    private LoadingDialog loadingDialog;
    
    private Handler previewHandler;
    private Runnable previewRunnable;
    private boolean exportReady = false;
    private String draftKey;
    private boolean pendingExport = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        
        // Initialize preference manager and API
        preferenceManager = new PreferenceManager(this);
        currentUser = preferenceManager.getUserData();
        apiService = ApiClient.getInstance().getApiService();
        
        // Initialize lists
        draftOrders = new ArrayList<>();
        previewOrders = new ArrayList<>();
        previewHandler = new Handler();
        
        // Get intent data
        getIntentData();
        
        // Initialize views
        initializeViews();

        // Load any saved drafts for this game/bond
        loadDraftOrdersFromStorage();
        
        // Setup click listeners
        setupClickListeners();
        
        // Display game and bond info
        displayOrderInfo();
        
        // Setup real-time preview
        setupRealTimePreview();
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            gameId = intent.getStringExtra("game_id");
            gameName = intent.getStringExtra("game_name");
            bondId = intent.getStringExtra("bond_id");
            bondName = intent.getStringExtra("bond_name");
            
            android.util.Log.d("OrdersActivity", "=== RECEIVED INTENT DATA ===");
            android.util.Log.d("OrdersActivity", "Game ID: " + gameId);
            android.util.Log.d("OrdersActivity", "Game Name: " + gameName);
            android.util.Log.d("OrdersActivity", "Bond ID: " + bondId);
            android.util.Log.d("OrdersActivity", "Bond Name: " + bondName);
            android.util.Log.d("OrdersActivity", "============================");
        }
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        tvGameName = findViewById(R.id.tv_game_name);
        tvBondName = findViewById(R.id.tv_bond_name);
        etRttp = findViewById(R.id.et_rttp);
        etFirst = findViewById(R.id.et_first);
        etSecond = findViewById(R.id.et_second);
        btnAddToDraft = findViewById(R.id.btn_add_to_draft);
        btnBookAll = findViewById(R.id.btn_book_all);
        etExportName = findViewById(R.id.et_export_name);
        tvClearAll = findViewById(R.id.tv_clear_all);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvGrandTotal = findViewById(R.id.tv_grand_total);
        tvRttpError = findViewById(R.id.tv_rttp_error);
        rvDraftOrders = findViewById(R.id.rv_draft_orders);

        // Prepare draft storage key per game/bond
        draftKey = "draft_orders_" + (gameId != null ? gameId : "na") + "_" + (bondId != null ? bondId : "na");

        // Dealer badge views
        llDealerBadge = findViewById(R.id.ll_dealer_badge);
        tvDealerCommission = findViewById(R.id.tv_dealer_commission);
        
        // Real-time commission views
        llRealtimeCommission = findViewById(R.id.ll_realtime_commission_info);
        tvRealtimePayable = findViewById(R.id.tv_realtime_payable);
        tvRealtimeNote = findViewById(R.id.tv_realtime_commission_note);

        // Update dealer badge
        updateDealerUI();

        // Setup RecyclerView
        draftAdapter = new DraftOrderAdapter(this);
        rvDraftOrders.setLayoutManager(new LinearLayoutManager(this));
        rvDraftOrders.setAdapter(draftAdapter);
        // Ensure RV expands inside NestedScrollView
        rvDraftOrders.setNestedScrollingEnabled(false);
        rvDraftOrders.setHasFixedSize(false);

        // Setup RTTP input validation
        setupRttpValidation();
        
        // Apply status bar padding to prevent overlap
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
    }
    
    private void setupRttpValidation() {
        // RTTP Validation: Max 4 characters, digits (0-9) and plus (+) only
        android.text.InputFilter[] filters = new android.text.InputFilter[1];
        filters[0] = new android.text.InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                     android.text.Spanned dest, int dstart, int dend) {
                // Allow digits, plus sign, and dot separator
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (!Character.isDigit(c) && c != '+' && c != '.' && c != '\n') {
                        return "";
                    }
                }
                return null; // Accept the input
            }
        };
        etRttp.setFilters(filters);
        
        // Phone keyboard shows digits, +, and * which is good for RTTP
        // etRttp.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        btnAddToDraft.setOnClickListener(v -> addToDraft());
        
        btnBookAll.setOnClickListener(v -> {
            if (!exportReady) {
                bookAllOrders();
            } else {
                exportOrdersPdf();
            }
        });
        
        tvClearAll.setOnClickListener(v -> clearAllDrafts());
    }
    
    private void displayOrderInfo() {
        if (gameName != null) {
            tvGameName.setText(gameName);
        }
        if (bondName != null) {
            tvBondName.setText(bondName);
        }
    }

    private void updateDealerUI() {
        if (currentUser == null) return;

        String status = currentUser.getDealerStatus();
        if (status == null || "null".equals(status)) status = "na";
        status = status.trim().toLowerCase();

        if ("approved".equals(status)) {
            // Show dealer badge
            if (llDealerBadge != null) {
                llDealerBadge.setVisibility(View.VISIBLE);

                if (tvDealerCommission != null) {
                    double commission = currentUser.getDealerCommission();
                    if (commission > 0) {
                        String commissionText = commission == Math.floor(commission)
                            ? String.format("%.0f%%", commission)
                            : String.format("%.1f%%", commission);
                        tvDealerCommission.setText(commissionText);
                        tvDealerCommission.setVisibility(View.VISIBLE);
                    } else {
                        tvDealerCommission.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            // Hide dealer badge
            if (llDealerBadge != null) {
                llDealerBadge.setVisibility(View.GONE);
            }
        }
    }
    
    private void setupRealTimePreview() {
        TextWatcher previewWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous preview update
                if (previewRunnable != null) {
                    previewHandler.removeCallbacks(previewRunnable);
                }
                
                // Schedule new preview update (debounced for performance)
                previewRunnable = () -> updatePreview();
                previewHandler.postDelayed(previewRunnable, 300); // 300ms delay
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        };
        
        etRttp.addTextChangedListener(previewWatcher);
        etFirst.addTextChangedListener(previewWatcher);
        etSecond.addTextChangedListener(previewWatcher);
    }
    
    private void updatePreview() {
        String rttpInput = etRttp.getText().toString().trim();
        String firstStr = etFirst.getText().toString().trim();
        String secondStr = etSecond.getText().toString().trim();
        
        // Validate RTTP in real-time
        String validationError = validateRtppInput(rttpInput);
        if (!rttpInput.isEmpty() && validationError != null) {
            tvRttpError.setText(validationError);
            tvRttpError.setVisibility(View.VISIBLE);
            previewOrders.clear();
        } else {
            tvRttpError.setVisibility(View.GONE);
        }
        
        // Clear previous preview orders
        previewOrders.clear();
        
        if (!rttpInput.isEmpty() && validationError == null) {
            // Parse RTTP input (single, dot-separated, or newline-separated)
            String[] rttps = rttpInput.split("[\\.\\n]+");
            
            double first = firstStr.isEmpty() ? 0 : parseDouble(firstStr);
            double second = secondStr.isEmpty() ? 0 : parseDouble(secondStr);
            
            // Create preview orders for each RTTP
            for (String rttp : rttps) {
                rttp = rttp.trim();
                if (!rttp.isEmpty() && rttp.length() <= 4 && isValidRttp(rttp)) {
                    DraftOrder preview = new DraftOrder(rttp, first, second);
                    preview.setState(DraftOrder.State.PREVIEW);
                    previewOrders.add(preview);
                }
            }
        }
        
        // Combine drafted orders + preview orders and display
        List<DraftOrder> allOrders = new ArrayList<>();
        allOrders.addAll(draftOrders);
        allOrders.addAll(previewOrders);
        
        draftAdapter.setOrders(allOrders);
        updateTotalsRealtime();
        updateEmptyState();
    }
    
    /**
     * Validate RTTP input format
     * Returns error message if invalid, null if valid
     */
    private String validateRtppInput(String input) {
        if (input.isEmpty()) {
            return null;
        }
        
        String[] rttps = input.split("[\\.\\n]+");
        for (String rttp : rttps) {
            rttp = rttp.trim();
            if (!rttp.isEmpty()) {
                // Check max length
                if (rttp.length() > 4) {
                    return "Each RTTP max 4 chars, found: " + rttp;
                }
                
                // Check if valid characters (digits and +)
                if (!isValidRttp(rttp)) {
                    return "Invalid chars in RTTP: " + rttp + " (use digits 0-9 and +)";
                }
            }
        }
        return null;
    }
    
    /**
     * Check if RTTP contains only digits and plus sign
     */
    private boolean isValidRttp(String rttp) {
        for (char c : rttp.toCharArray()) {
            if (!Character.isDigit(c) && c != '+') {
                return false;
            }
        }
        return true;
    }
    
    private void addToDraft() {
        String rttpInput = etRttp.getText().toString().trim();
        String firstStr = etFirst.getText().toString().trim();
        String secondStr = etSecond.getText().toString().trim();
        
        // Validate RTTP
        if (rttpInput.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "RTTP is required");
            etRttp.requestFocus();
            return;
        }
        
        // Validate RTTP format
        String validationError = validateRtppInput(rttpInput);
        if (validationError != null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), validationError);
            etRttp.requestFocus();
            return;
        }
        
        // At least one amount required
        if (firstStr.isEmpty() && secondStr.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content), 
                "Either First or Second amount is required");
            etFirst.requestFocus();
            return;
        }
        
        double first = firstStr.isEmpty() ? 0 : parseDouble(firstStr);
        double second = secondStr.isEmpty() ? 0 : parseDouble(secondStr);
        
        // Validate amounts
        if (!validateAmount("First", first, firstStr.isEmpty()) || 
            !validateAmount("Second", second, secondStr.isEmpty())) {
            return;
        }
        
        // Parse RTTPs (single, dot-separated, or newline-separated)
        String[] rttps = rttpInput.split("[\\.\\n]+");
        int addedCount = 0;
        
        for (String rttp : rttps) {
            rttp = rttp.trim();
            if (!rttp.isEmpty() && isValidRttp(rttp) && rttp.length() <= 4) {
                // Create drafted order
                DraftOrder order = new DraftOrder(rttp, first, second);
                order.setState(DraftOrder.State.DRAFTED);
                draftOrders.add(order);
                addedCount++;
            }
        }
        
        if (addedCount > 0) {
            // Clear preview orders
            previewOrders.clear();
            
            // Update adapter
            List<DraftOrder> allOrders = new ArrayList<>(draftOrders);
            draftAdapter.setOrders(allOrders);
            
            // Clear inputs
            clearInputs();
            
            // Update totals
            updateTotals();
            updateEmptyState();

            // Persist drafts for this game/bond
            saveDraftOrdersToStorage();
            
            SnackbarUtils.showSuccess(findViewById(android.R.id.content), 
                addedCount + " order(s) added to draft");
        }
    }
    
    private boolean validateAmount(String label, double amount, boolean isEmpty) {
        if (isEmpty) return true; // Optional
        
        // Minimum amount must be at least 10
        if (amount < 10) {
            SnackbarUtils.showError(findViewById(android.R.id.content), 
                label + " amount must be at least 10");
            return false;
        }
        
        // Divisible by 5
        if (amount % 5 != 0) {
            SnackbarUtils.showError(findViewById(android.R.id.content), 
                label + " amount must be divisible by 5");
            return false;
        }
        
        return true;
    }
    
    private void clearInputs() {
        etRttp.setText("");
        etFirst.setText("");
        etSecond.setText("");
        etRttp.requestFocus();
    }
    
    private void updateTotals() {
        // Calculate totals from drafted orders only
        int orderCount = draftOrders.size();
        double grandTotal = 0;
        
        for (DraftOrder order : draftOrders) {
            grandTotal += order.getTotal();
        }
        
        tvTotalOrders.setText(String.valueOf(orderCount));
        tvGrandTotal.setText("₨ " + String.format("%.0f", grandTotal));
        
        // Update real-time commission display
        if (currentUser != null && currentUser.isDealer() && grandTotal > 0) {
            double commissionPercent = currentUser.getDealerCommission();
            if (commissionPercent > 0) {
                double commissionAmount = grandTotal * (commissionPercent / 100.0);
                double payableAmount = grandTotal - commissionAmount;
                
                if (llRealtimeCommission != null) {
                    llRealtimeCommission.setVisibility(View.VISIBLE);
                    if (tvRealtimePayable != null) {
                        tvRealtimePayable.setText("₨ " + String.format("%.0f", payableAmount));
                    }
                    if (tvRealtimeNote != null) {
                        tvRealtimeNote.setText(String.format("₨ %.0f is your commission (%.1f%%)", commissionAmount, commissionPercent));
                    }
                }
            } else {
                if (llRealtimeCommission != null) llRealtimeCommission.setVisibility(View.GONE);
            }
        } else {
            if (llRealtimeCommission != null) llRealtimeCommission.setVisibility(View.GONE);
        }
        
        // Enable/disable book button
        btnBookAll.setEnabled(orderCount > 0);
        
        // Show/hide clear all button
        tvClearAll.setVisibility(orderCount > 0 ? View.VISIBLE : View.GONE);
    }
    
    private void updateTotalsRealtime() {
        // Calculate totals including preview orders for real-time display
        int orderCount = draftOrders.size();
        int previewCount = previewOrders.size();
        double grandTotal = 0;
        
        for (DraftOrder order : draftOrders) {
            grandTotal += order.getTotal();
        }
        
        for (DraftOrder order : previewOrders) {
            grandTotal += order.getTotal();
        }
        
        // Show combined total count only (no +x)
        int combinedCount = orderCount + previewCount;
        tvTotalOrders.setText(String.valueOf(combinedCount));
        
        tvGrandTotal.setText("₨ " + String.format("%.0f", grandTotal));
        
        // Update real-time commission display
        if (currentUser != null && currentUser.isDealer() && grandTotal > 0) {
            double commissionPercent = currentUser.getDealerCommission();
            if (commissionPercent > 0) {
                double commissionAmount = grandTotal * (commissionPercent / 100.0);
                double payableAmount = grandTotal - commissionAmount;
                
                if (llRealtimeCommission != null) {
                    llRealtimeCommission.setVisibility(View.VISIBLE);
                    if (tvRealtimePayable != null) {
                        tvRealtimePayable.setText("₨ " + String.format("%.0f", payableAmount));
                    }
                    if (tvRealtimeNote != null) {
                        tvRealtimeNote.setText(String.format("₨ %.0f is your commission (%.1f%%)", commissionAmount, commissionPercent));
                    }
                }
            } else {
                if (llRealtimeCommission != null) llRealtimeCommission.setVisibility(View.GONE);
            }
        } else {
            if (llRealtimeCommission != null) llRealtimeCommission.setVisibility(View.GONE);
        }
        
        // Enable/disable book button
        btnBookAll.setEnabled(orderCount > 0);
        
        // Show/hide clear all button
        tvClearAll.setVisibility(orderCount > 0 ? View.VISIBLE : View.GONE);
    }
    
    private void updateEmptyState() {
        boolean isEmpty = draftOrders.isEmpty() && previewOrders.isEmpty();
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void saveDraftOrdersToStorage() {
        if (preferenceManager == null || draftKey == null) return;
        // Save all orders with their current states (PREVIEW, DRAFTED, SUCCESS, FAILED, etc.)
        preferenceManager.saveDraftOrders(draftKey, draftOrders);
    }

    private void loadDraftOrdersFromStorage() {
        if (preferenceManager == null || draftKey == null) return;
        List<DraftOrder> stored = preferenceManager.getDraftOrders(draftKey);
        if (!stored.isEmpty()) {
            draftOrders.clear();
            draftOrders.addAll(stored);
            draftAdapter.setOrders(new ArrayList<>(draftOrders));
            updateTotals();
            updateEmptyState();
            
            // Check if any orders are in SUCCESS or FAILED state (means they were placed)
            boolean hasPlacedOrders = false;
            for (DraftOrder order : draftOrders) {
                if (order.getState() == DraftOrder.State.SUCCESS || 
                    order.getState() == DraftOrder.State.FAILED) {
                    hasPlacedOrders = true;
                    break;
                }
            }
            
            // Restore export mode if orders were placed
            if (hasPlacedOrders) {
                exportReady = true;
                btnBookAll.setText("Export PDF");
                if (etExportName != null) {
                    etExportName.setVisibility(View.VISIBLE);
                }
            } else {
                exportReady = false;
                btnBookAll.setText("Book All Orders");
                if (etExportName != null) {
                    etExportName.setVisibility(View.GONE);
                }
            }
        }
    }
    
    private void clearAllDrafts() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_clear_all, null);
        TextView tvMessage = dialogView.findViewById(R.id.tv_clear_message);
        Button btnCancel = dialogView.findViewById(R.id.btn_clear_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_clear_confirm);

        tvMessage.setText("This will remove all " + draftOrders.size() + " draft orders. Proceed?");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            draftOrders.clear();
            previewOrders.clear();
            draftAdapter.clear();
            updateTotals();
            updateEmptyState();
            if (draftKey != null) {
                preferenceManager.clearDraftOrders(draftKey);
            }
            exportReady = false;
            btnBookAll.setText("Book All Orders");
            if (etExportName != null) {
                etExportName.setVisibility(View.GONE);
                etExportName.setText("");
            }
            SnackbarUtils.showSuccess(findViewById(android.R.id.content), "All drafts cleared");
        });

        dialog.show();
    }
    
    @Override
    public void onDeleteClick(int position) {
        // Only delete drafted items (not previews)
        if (position < draftOrders.size()) {
            DraftOrder removed = draftOrders.remove(position);
            
            // Update adapter
            List<DraftOrder> allOrders = new ArrayList<>();
            allOrders.addAll(draftOrders);
            allOrders.addAll(previewOrders);
            draftAdapter.setOrders(allOrders);
            
            updateTotals();
            updateEmptyState();
            saveDraftOrdersToStorage();
            
            SnackbarUtils.showSuccess(findViewById(android.R.id.content), 
                "Order removed: " + removed.getRttp());
        }
    }
    
    private void bookAllOrders() {
        if (draftOrders.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "No orders to place");
            return;
        }
        
        // Calculate grand total
        double grandTotal = 0;
        for (DraftOrder order : draftOrders) {
            grandTotal += order.getTotal();
        }
        
        // Check balance
        if (currentUser == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), 
                "User information not available");
            return;
        }
        
        double userBalance = currentUser.getBalance();
        if (userBalance < grandTotal) {
            SnackbarUtils.showError(findViewById(android.R.id.content), 
                "Insufficient balance. Required: ₨" + String.format("%.0f", grandTotal) + 
                ", Your balance: ₨" + String.format("%.0f", userBalance));
            return;
        }
        
        // Show styled confirmation dialog
        final double finalGrandTotal = grandTotal;
        showBulkConfirmation(finalGrandTotal, userBalance);
    }
    
    private void submitBulkOrders() {
        // Filter orders to submit (exclude already successful ones)
        List<DraftOrder> ordersToSubmit = new ArrayList<>();
        List<com.geo.enterprises.models.BulkOrderRequest.BulkOrderItem> requestItems = new ArrayList<>();
        
        for (DraftOrder order : draftOrders) {
            if (order.getState() != DraftOrder.State.SUCCESS) {
                ordersToSubmit.add(order);
                order.setState(DraftOrder.State.PLACING);
                
                requestItems.add(new com.geo.enterprises.models.BulkOrderRequest.BulkOrderItem(
                    gameName,
                    bondName,
                    order.getRttp(),
                    (int) order.getFirst(),
                    (int) order.getSecond(),
                    currentUser.getPhone()
                ));
            }
        }
        
        if (ordersToSubmit.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "No pending orders to submit");
            return;
        }

        draftAdapter.setOrders(new ArrayList<>(draftOrders));
        
        if (bulkProgressDialog == null || !bulkProgressDialog.isShowing()) {
            showBulkProgressDialog();
        }
        
        // Update dialog to indeterminate
        tvProgressText.setText("Submitting " + ordersToSubmit.size() + " orders...");
        pbHorizontal.setIndeterminate(true);
        tvCounter.setText("Please wait...");
        
        String token = "Bearer " + preferenceManager.getAuthToken();
        com.geo.enterprises.models.BulkOrderRequest request = new com.geo.enterprises.models.BulkOrderRequest(requestItems);
        
        apiService.createBulkOrders(token, request).enqueue(new retrofit2.Callback<ApiResponse<List<com.geo.enterprises.models.BulkOrderResult>>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<List<com.geo.enterprises.models.BulkOrderResult>>> call, 
                                 retrofit2.Response<ApiResponse<List<com.geo.enterprises.models.BulkOrderResult>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<com.geo.enterprises.models.BulkOrderResult>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        processBulkResponse(ordersToSubmit, apiResponse.getData());
                    } else {
                        handleBulkFailure(ordersToSubmit, apiResponse.getMessage());
                    }
                } else {
                    handleBulkFailure(ordersToSubmit, "Request failed (HTTP " + response.code() + ")");
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<List<com.geo.enterprises.models.BulkOrderResult>>> call, Throwable t) {
                handleBulkFailure(ordersToSubmit, "Network error: " + t.getMessage());
            }
        });
    }

    private void processBulkResponse(List<DraftOrder> submittedOrders, List<com.geo.enterprises.models.BulkOrderResult> results) {
        int successCount = 0;
        int failedCount = 0;
        
        // Calculate existing success count from orders NOT in this batch
        for (DraftOrder order : draftOrders) {
            if (order.getState() == DraftOrder.State.SUCCESS && !submittedOrders.contains(order)) {
                successCount++;
            }
        }

        for (com.geo.enterprises.models.BulkOrderResult result : results) {
            int index = result.getIndex();
            if (index >= 0 && index < submittedOrders.size()) {
                DraftOrder order = submittedOrders.get(index);
                if (result.isSuccess()) {
                    order.setState(DraftOrder.State.SUCCESS);
                    if (result.getData() != null) {
                        order.setOrderId(result.getData().getId());
                    }
                    // Update balance
                    if (currentUser != null) {
                        double newBalance = currentUser.getBalance() - order.getTotal();
                        currentUser.setBalance(newBalance);
                    }
                    successCount++;
                } else {
                    order.setState(DraftOrder.State.FAILED);
                    order.setErrorMessage(result.getMessage() != null ? result.getMessage() : "Failed");
                    failedCount++;
                }
            }
        }
        
        // Save user data
        if (currentUser != null) {
            preferenceManager.saveUserData(currentUser);
        }
        
        draftAdapter.setOrders(new ArrayList<>(draftOrders));
        onBulkSubmissionComplete(successCount, failedCount);
    }

    private void handleBulkFailure(List<DraftOrder> submittedOrders, String error) {
        for (DraftOrder order : submittedOrders) {
            order.setState(DraftOrder.State.FAILED);
            order.setErrorMessage(error);
        }
        draftAdapter.setOrders(new ArrayList<>(draftOrders));
        
        // Calculate totals
        int successCount = 0;
        int failedCount = 0;
        for (DraftOrder order : draftOrders) {
            if (order.getState() == DraftOrder.State.SUCCESS) successCount++;
            else if (order.getState() == DraftOrder.State.FAILED) failedCount++;
        }
        
        onBulkSubmissionComplete(successCount, failedCount);
    }

    private void showBulkConfirmation(double totalAmount, double userBalance) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_booking_confirmation, null);

        TextView tvGame = dialogView.findViewById(R.id.tv_booking_game);
        TextView tvBond = dialogView.findViewById(R.id.tv_booking_bond);
        TextView tvRttp = dialogView.findViewById(R.id.tv_booking_rttp);
        TextView tvFirst = dialogView.findViewById(R.id.tv_booking_first);
        TextView tvSecond = dialogView.findViewById(R.id.tv_booking_second);
        TextView tvTotal = dialogView.findViewById(R.id.tv_booking_total);
        TextView tvCurrentBalance = dialogView.findViewById(R.id.tv_booking_current_balance);
        TextView tvAfterBalance = dialogView.findViewById(R.id.tv_booking_after_balance);
        Button btnCancel = dialogView.findViewById(R.id.btn_booking_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_booking_confirm);
        
        // Dealer commission views
        LinearLayout llCommissionInfo = dialogView.findViewById(R.id.ll_booking_commission_info);
        TextView tvPayable = dialogView.findViewById(R.id.tv_booking_payable);
        TextView tvCommissionNote = dialogView.findViewById(R.id.tv_booking_commission_note);

        // Populate values
        tvGame.setText(gameName);
        tvBond.setText(bondName);
        tvRttp.setText("Multiple (" + draftOrders.size() + ")");

        double sumFirst = 0;
        double sumSecond = 0;
        for (DraftOrder order : draftOrders) {
            sumFirst += order.getFirst();
            sumSecond += order.getSecond();
        }

        tvFirst.setText("₨ " + String.format("%.0f", sumFirst));
        tvSecond.setText("₨ " + String.format("%.0f", sumSecond));
        tvTotal.setText("₨ " + String.format("%.0f", totalAmount));

        tvCurrentBalance.setText("₨ " + String.format("%.0f", userBalance));
        
        // Calculate commission if dealer
        double payableAmount = totalAmount;
        if (currentUser != null && currentUser.isDealer()) {
            double commissionPercent = currentUser.getDealerCommission();
            if (commissionPercent > 0) {
                double commissionAmount = totalAmount * (commissionPercent / 100.0);
                payableAmount = totalAmount - commissionAmount;
                
                llCommissionInfo.setVisibility(View.VISIBLE);
                tvPayable.setText("₨ " + String.format("%.0f", payableAmount));
                tvCommissionNote.setText(String.format("₨ %.0f is your commission (%.1f%%)", commissionAmount, commissionPercent));
            } else {
                llCommissionInfo.setVisibility(View.GONE);
            }
        } else {
            llCommissionInfo.setVisibility(View.GONE);
        }
        
        // Update After Balance with PAYABLE amount
        tvAfterBalance.setText("₨ " + String.format("%.0f", (userBalance - payableAmount)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            submitBulkOrders();
        });

        dialog.show();
    }
    
    private AlertDialog bulkProgressDialog;
    private TextView tvProgressText, tvCurrentRttp, tvCounter;
    private ProgressBar pbHorizontal;
    private TextView tvSuccessCount, tvFailedCount;
    
    private void showBulkProgressDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bulk_order_progress, null);
        
        tvProgressText = dialogView.findViewById(R.id.tv_progress_text);
        tvCurrentRttp = dialogView.findViewById(R.id.tv_current_rttp);
        tvCounter = dialogView.findViewById(R.id.tv_counter);
        pbHorizontal = dialogView.findViewById(R.id.pb_horizontal);
        tvSuccessCount = dialogView.findViewById(R.id.tv_success_count);
        tvFailedCount = dialogView.findViewById(R.id.tv_failed_count);
        
        pbHorizontal.setMax(draftOrders.size());
        pbHorizontal.setProgress(0);
        tvSuccessCount.setText("0");
        tvFailedCount.setText("0");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        
        bulkProgressDialog = builder.create();
        bulkProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bulkProgressDialog.show();
    }
    
    // submitNextOrder and submitSingleOrder removed - replaced by submitBulkOrders

    
    private void onBulkSubmissionComplete(int successCount, int failedCount) {
        // Dismiss progress dialog
        if (bulkProgressDialog != null) {
            bulkProgressDialog.dismiss();
        }
        
        // Show summary with retry option for failed orders
        if (failedCount == 0) {
            String message = "✓ All " + successCount + " orders placed successfully!";
            SnackbarUtils.showSuccess(findViewById(android.R.id.content), message);
        } else {
            showRetryDialog(successCount, failedCount);
        }
        
        // Prepare export mode
        exportReady = true;
        btnBookAll.setText("Export PDF");
        if (etExportName != null) {
            etExportName.setVisibility(View.VISIBLE);
        }

        // Keep orders in list to show success/failed states
        // User can review and export, or clear manually
        updateTotals();
        
        // Save state so it persists when user navigates away and back
        saveDraftOrdersToStorage();
    }

    private void showRetryDialog(int successCount, int failedCount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_retry_failed, null);
        
        // Set summary
        TextView tvSummary = dialogView.findViewById(R.id.tv_retry_summary);
        tvSummary.setText(successCount + " successful • " + failedCount + " failed");
        
        // Set message
        TextView tvMessage = dialogView.findViewById(R.id.tv_retry_message);
        tvMessage.setText("Would you like to retry the failed orders?");
        
        AlertDialog dialog = builder.setView(dialogView).create();
        
        // Prevent cancellation by clicking outside
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        
        // Set dialog appearance
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        // Cancel button - skip to export
        dialogView.findViewById(R.id.btn_retry_cancel).setOnClickListener(v -> {
            dialog.dismiss();
            // Keep export mode active, user can export now
        });
        
        // Retry button
        dialogView.findViewById(R.id.btn_retry_confirm).setOnClickListener(v -> {
            dialog.dismiss();
            retryFailedOrders();
        });
        
        dialog.show();
    }

    private void retryFailedOrders() {
        // Collect failed orders
        List<DraftOrder> failedOrders = new ArrayList<>();
        for (DraftOrder order : draftOrders) {
            if (order.getState() == DraftOrder.State.FAILED) {
                order.setState(DraftOrder.State.DRAFTED);
                order.setErrorMessage(null);
                failedOrders.add(order);
            }
        }

        if (failedOrders.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "No failed orders to retry");
            return;
        }

        // Move failed orders back to top of drafted list for retry
        draftOrders.removeAll(failedOrders);
        draftOrders.addAll(0, failedOrders);
        draftAdapter.setOrders(new ArrayList<>(draftOrders));

        // Reset export mode
        exportReady = false;
        btnBookAll.setText("Book All Orders");
        if (etExportName != null) {
            etExportName.setVisibility(View.GONE);
        }

        // Show progress and retry
        // showBulkProgressDialog(); - submitBulkOrders will show it
        submitBulkOrders();
    }

    private void exportOrdersPdf() {
        // For Android 13+ (API 33+), we don't need WRITE_EXTERNAL_STORAGE for public directories
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            performExport();
            return;
        }
        
        // For Android 10-12 (API 29-32), use scoped storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            performExport();
            return;
        }
        
        // For Android 6-9 (API 23-28), need WRITE_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                pendingExport = true;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                performExport();
            }
        } else {
            // Below Android 6, no runtime permission needed
            performExport();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingExport) {
                    performExport();
                    pendingExport = false;
                }
            } else {
                SnackbarUtils.showError(findViewById(android.R.id.content),
                        "Permission denied. Cannot export files.");
                
                // Check if user checked "Don't ask again"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showPermissionSettingsDialog();
                    }
                }
                pendingExport = false;
            }
        }
    }

    private void showPermissionSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Storage permission is required to export PDF files. You have denied this permission. Please enable it in Settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    android.net.Uri uri = android.net.Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performExport() {
        String customerName = etExportName != null ? etExportName.getText().toString().trim() : "";
        if (customerName.isEmpty()) {
            customerName = "Orders";
        }
        
        try {
            generatePdfWithPagination(customerName);
        } catch (Exception e) {
            Log.e(TAG, "PDF export failed", e);
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Failed to export PDF: " + e.getMessage());
        }
    }

    private void generatePdfWithPagination(String fileName) throws IOException {
        android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
        
        // A4 Landscape size in high resolution points (doubled for better quality and fitting more rows)
        // Standard A4 Landscape is 842 x 595 points. We double it to ~1684 x 1190 to allow Views (pixels) to fit comfortably.
        int pageWidth = 1684;
        int pageHeight = 1190;
        int sideMargin = 24; // Keep content comfortably inside the page while maximizing usable width
        int contentWidth = pageWidth - (2 * sideMargin); // Width for header/footer content
        int tableHorizontalInset = 90; // Extra inset so the right-most columns never sit on the page edge
        int tableLeft = sideMargin + tableHorizontalInset;
        int tableContentWidth = contentWidth - (2 * tableHorizontalInset);
        
        android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(
                pageWidth, pageHeight, 1).create();

        // 1. Inflate Layouts
        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_pdf_page_header, null);
        View tableHeaderView = LayoutInflater.from(this).inflate(R.layout.layout_pdf_table_header, null);
        View footerView = LayoutInflater.from(this).inflate(R.layout.layout_pdf_footer, null);

        // 2. Populate Header and Footer
        populatePdfHeader(headerView, fileName);
        
        // Calculate totals
        double grandTotal = 0;
        int successCount = 0;
        int failedCount = 0;
        for (DraftOrder order : draftOrders) {
            if (order.getState() == DraftOrder.State.SUCCESS) {
                successCount++;
                grandTotal += order.getTotal();
            } else if (order.getState() == DraftOrder.State.FAILED) {
                failedCount++;
            }
        }
        
        TextView tvTotal = footerView.findViewById(R.id.tv_draft_total);
        String totalText = String.format("₨ %.0f", grandTotal);
        if (failedCount > 0) {
            totalText += " (" + successCount + "/" + (successCount + failedCount) + " successful)";
        }
        tvTotal.setText(totalText);

        // 3. Measure Views
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(contentWidth, View.MeasureSpec.EXACTLY);
        int tableWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(tableContentWidth, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        headerView.measure(widthMeasureSpec, heightMeasureSpec);
        headerView.layout(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight());
        int headerHeight = headerView.getMeasuredHeight();

        tableHeaderView.measure(tableWidthMeasureSpec, heightMeasureSpec);
        tableHeaderView.layout(0, 0, tableHeaderView.getMeasuredWidth(), tableHeaderView.getMeasuredHeight());
        int tableHeaderHeight = tableHeaderView.getMeasuredHeight();

        footerView.measure(widthMeasureSpec, heightMeasureSpec);
        footerView.layout(0, 0, footerView.getMeasuredWidth(), footerView.getMeasuredHeight());
        int footerHeight = footerView.getMeasuredHeight();

        // 4. Start Page Loop
        android.graphics.pdf.PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        int currentY = 0;
        int bottomMargin = 20; // Increased margin

        // Draw Header on first page
        canvas.save();
        canvas.translate(sideMargin, currentY);
        headerView.draw(canvas);
        canvas.restore();
        currentY += headerHeight;

        // Draw Table Header
        canvas.save();
        canvas.translate(tableLeft, currentY);
        tableHeaderView.draw(canvas);
        canvas.restore();
        currentY += tableHeaderHeight;

        // Loop through orders
        for (DraftOrder order : draftOrders) {
            // Create row view
            View rowView = createPdfOrderRow(order, order.getState() == DraftOrder.State.FAILED);
            rowView.measure(tableWidthMeasureSpec, heightMeasureSpec);
            rowView.layout(0, 0, rowView.getMeasuredWidth(), rowView.getMeasuredHeight());
            int rowHeight = rowView.getMeasuredHeight();

            // Check if row fits
            if (currentY + rowHeight > pageHeight - bottomMargin - footerHeight) {
                // Finish current page
                document.finishPage(page);
                
                // Start new page
                pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                currentY = 20; // Reduced top margin for subsequent pages

                // Draw Table Header again
                canvas.save();
                canvas.translate(tableLeft, currentY);
                tableHeaderView.draw(canvas);
                canvas.restore();
                currentY += tableHeaderHeight;
            }

            // Draw Row
            canvas.save();
            canvas.translate(tableLeft, currentY);
            rowView.draw(canvas);
            canvas.restore();
            currentY += rowHeight;
        }

        // Draw Footer
        if (currentY + footerHeight > pageHeight - bottomMargin) {
            document.finishPage(page);
            pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            currentY = 20;
        }

        canvas.save();
        canvas.translate(sideMargin, currentY);
        footerView.draw(canvas);
        canvas.restore();
        
        document.finishPage(page);

        // 5. Save PDF
        savePdf(document, fileName);
    }

    private void populatePdfHeader(View headerView, String customerName) {
        // App logo
        ImageView ivLogo = headerView.findViewById(R.id.iv_draft_logo);
        TextView tvAppName = headerView.findViewById(R.id.tv_draft_app_name);
        TextView tvTagline = headerView.findViewById(R.id.tv_draft_tagline);

        com.geo.enterprises.models.AppSettings appSettings = preferenceManager.getAppSettings();
        if (appSettings != null) {
            tvAppName.setText(appSettings.getAppName());
            tvTagline.setText(appSettings.getAppTagline() != null ? appSettings.getAppTagline() : "Prize Bond Booking System");
            
            if (appSettings.getAppLogo() != null && !appSettings.getAppLogo().isEmpty()) {
                try {
                    String logoUrl = appSettings.getAppLogo();
                    if (!logoUrl.startsWith("http")) {
                        logoUrl = com.geo.enterprises.config.AppConfig.PUBLIC_BASE_URL + "/" + logoUrl;
                    }
                    com.bumptech.glide.Glide.with(this)
                            .load(logoUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(ivLogo);
                } catch (Exception e) {
                    ivLogo.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                ivLogo.setImageResource(R.mipmap.ic_launcher);
            }
        }

        // Update Summary Card
        TextView tvGameCategory = headerView.findViewById(R.id.tv_game_category);
        TextView tvTotalOrders = headerView.findViewById(R.id.tv_total_orders_header);
        TextView tvDate = headerView.findViewById(R.id.tv_generated_date);
        TextView tvCustomerName = headerView.findViewById(R.id.tv_customer_name);

        tvGameCategory.setText("Game Category: " + (gameName != null ? gameName : "All Categories"));
        tvTotalOrders.setText("Total Orders: " + (draftOrders != null ? draftOrders.size() : 0));
        tvCustomerName.setText("Customer Name: " + (customerName != null && !customerName.isEmpty() ? customerName : "-"));
        
        String currentDate = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault()).format(new Date());
        tvDate.setText("Generated on: " + currentDate);
    }

    private View createPdfOrderRow(DraftOrder order, boolean isFailed) {
        PdfOrderRowView row = new PdfOrderRowView(order, isFailed);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return row;
    }

    private String getPdfStatusLabel(DraftOrder.State state) {
        if (state == null) {
            return "UNKNOWN";
        }

        switch (state) {
            case PREVIEW:
                return "PREVIEW";
            case DRAFTED:
                return "DRAFT";
            case PLACING:
                return "PLACING";
            case SUCCESS:
                return "SUCCESS";
            case FAILED:
                return "FAILED";
            default:
                return state.name();
        }
    }

    private int getPdfStatusColor(DraftOrder.State state) {
        if (state == null) {
            return 0xFFFFA500;
        }

        switch (state) {
            case PREVIEW:
            case DRAFTED:
                return 0xFFFFA500;
            case PLACING:
                return ContextCompat.getColor(this, R.color.primary_color);
            case SUCCESS:
                return 0xFF4CAF50;
            case FAILED:
                return 0xFFF44336;
            default:
                return 0xFFFFA500;
        }
    }

    private int getPdfStatusBackground(DraftOrder.State state) {
        if (state == null) {
            return R.drawable.bg_status_pending;
        }

        switch (state) {
            case PREVIEW:
            case DRAFTED:
                return R.drawable.bg_status_pending;
            case PLACING:
                return R.drawable.bg_status_win;
            case SUCCESS:
                return R.drawable.bg_status_ok;
            case FAILED:
                return R.drawable.bg_status_rejected;
            default:
                return R.drawable.bg_status_pending;
        }
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics());
    }

    private float spToPx(float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics());
    }

    private float getTextBaseline(float centerY, Paint paint) {
        return centerY - ((paint.descent() + paint.ascent()) / 2f);
    }

    private String normalizePdfText(String text, String fallback) {
        if (text == null || text.trim().isEmpty()) {
            return fallback;
        }
        return text.trim();
    }

    private String ellipsizePdfText(String text, TextPaint paint, float maxWidth) {
        if (maxWidth <= 0f) {
            return "";
        }

        return TextUtils.ellipsize(
                normalizePdfText(text, "-"),
                paint,
                maxWidth,
                TextUtils.TruncateAt.END
        ).toString();
    }

    private final class PdfOrderRowView extends View {
        private final float[] weights = {
                PDF_BOND_WEIGHT,
                PDF_RTTP_WEIGHT,
                PDF_FIRST_WEIGHT,
                PDF_SECOND_WEIGHT,
                PDF_TOTAL_WEIGHT,
                PDF_STATUS_WEIGHT
        };

        private final DraftOrder order;
        private final boolean isFailed;
        private final TextPaint bondPaint;
        private final TextPaint valuePaint;
        private final TextPaint statusPaint;
        private final Paint dividerPaint;
        private final Paint failedBgPaint;
        private final Paint badgePaint;
        private final RectF badgeRect = new RectF();
        private final float totalWeight;
        private final float horizontalInset;
        private final float cellPadding;
        private final float verticalPadding;
        private final float badgeHorizontalPadding;
        private final float badgeVerticalPadding;
        private final float badgeCornerRadius;
        private final float badgeMinWidth;

        PdfOrderRowView(DraftOrder order, boolean isFailed) {
            super(OrdersActivity.this);
            this.order = order;
            this.isFailed = isFailed;

            horizontalInset = dpToPx(2f);
            cellPadding = dpToPx(3f);
            verticalPadding = dpToPx(4f);
            badgeHorizontalPadding = dpToPx(8f);
            badgeVerticalPadding = dpToPx(3f);
            badgeCornerRadius = dpToPx(10f);
            badgeMinWidth = dpToPx(64f);

            int textColor = ContextCompat.getColor(OrdersActivity.this, R.color.text_primary);

            bondPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            bondPaint.setColor(textColor);
            bondPaint.setTextSize(spToPx(10f));

            valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            valuePaint.setColor(textColor);
            valuePaint.setTextSize(spToPx(10f));
            valuePaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

            statusPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            statusPaint.setColor(ContextCompat.getColor(OrdersActivity.this, R.color.white));
            statusPaint.setTextSize(spToPx(8f));
            statusPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

            dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dividerPaint.setColor(ContextCompat.getColor(OrdersActivity.this, R.color.divider_color));
            dividerPaint.setStrokeWidth(Math.max(1f, dpToPx(1f)));

            failedBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            failedBgPaint.setColor(ContextCompat.getColor(OrdersActivity.this, R.color.background_primary));

            badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            float sum = 0f;
            for (float weight : weights) {
                sum += weight;
            }
            totalWeight = sum;

            setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);

            float textHeight = Math.max(getPaintHeight(bondPaint), getPaintHeight(valuePaint));
            float badgeHeight = getPaintHeight(statusPaint) + (badgeVerticalPadding * 2f);
            int desiredHeight = Math.round(Math.max(dpToPx(32f), Math.max(textHeight, badgeHeight) + (verticalPadding * 2f)));

            setMeasuredDimension(width, resolveSize(desiredHeight, heightMeasureSpec));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float rowLeft = horizontalInset;
            float rowRight = getWidth() - horizontalInset;
            float rowWidth = Math.max(0f, rowRight - rowLeft);
            float centerY = getHeight() / 2f;
            float currentLeft = rowLeft;

            if (isFailed) {
                canvas.drawRect(rowLeft, 0, rowRight, getHeight(), failedBgPaint);
            }

            String[] values = {
                    normalizePdfText(bondName, "-"),
                    normalizePdfText(order.getRttp(), "-"),
                    order.getFirstFormatted(),
                    order.getSecondFormatted(),
                    order.getTotalFormatted(),
                    getPdfStatusLabel(order.getState())
            };

            for (int i = 0; i < weights.length; i++) {
                float columnWidth = totalWeight == 0f ? 0f : (rowWidth * weights[i] / totalWeight);
                float columnLeft = currentLeft;
                float columnRight = columnLeft + columnWidth;

                if (i == 0) {
                    drawLeftText(canvas, values[i], bondPaint, columnLeft, columnRight, centerY);
                } else if (i == 1) {
                    drawCenteredText(canvas, values[i], valuePaint, columnLeft, columnRight, centerY);
                } else if (i >= 2 && i <= 4) {
                    drawRightText(canvas, values[i], valuePaint, columnLeft, columnRight, centerY);
                } else {
                    drawStatusBadge(canvas, values[i], order.getState(), columnLeft, columnRight, centerY);
                }

                currentLeft = columnRight;
            }

            // Vertical column separators
            currentLeft = rowLeft;
            for (int i = 0; i < weights.length - 1; i++) {
                float width = totalWeight == 0f ? 0f : (rowWidth * weights[i] / totalWeight);
                currentLeft += width;
                canvas.drawLine(currentLeft, 0, currentLeft, getHeight(), dividerPaint);
            }

            // Top and bottom borders
            float halfStroke = dividerPaint.getStrokeWidth() / 2f;
            canvas.drawLine(rowLeft, halfStroke, rowRight, halfStroke, dividerPaint);
            canvas.drawLine(rowLeft, getHeight() - halfStroke, rowRight, getHeight() - halfStroke, dividerPaint);
        }

        private float getPaintHeight(Paint paint) {
            return paint.descent() - paint.ascent();
        }

        private void drawLeftText(Canvas canvas, String text, TextPaint paint, float columnLeft, float columnRight, float centerY) {
            float maxWidth = Math.max(0f, (columnRight - columnLeft) - (cellPadding * 2f));
            String value = ellipsizePdfText(text, paint, maxWidth);
            canvas.drawText(value, columnLeft + cellPadding, getTextBaseline(centerY, paint), paint);
        }

        private void drawCenteredText(Canvas canvas, String text, TextPaint paint, float columnLeft, float columnRight, float centerY) {
            float maxWidth = Math.max(0f, (columnRight - columnLeft) - (cellPadding * 2f));
            String value = ellipsizePdfText(text, paint, maxWidth);
            float textWidth = paint.measureText(value);
            float x = columnLeft + ((columnRight - columnLeft) - textWidth) / 2f;
            canvas.drawText(value, x, getTextBaseline(centerY, paint), paint);
        }

        private void drawRightText(Canvas canvas, String text, TextPaint paint, float columnLeft, float columnRight, float centerY) {
            float maxWidth = Math.max(0f, (columnRight - columnLeft) - (cellPadding * 2f));
            String value = ellipsizePdfText(text, paint, maxWidth);
            float x = columnRight - cellPadding - paint.measureText(value);
            canvas.drawText(value, Math.max(columnLeft + cellPadding, x), getTextBaseline(centerY, paint), paint);
        }

        private void drawStatusBadge(Canvas canvas, String label, DraftOrder.State state, float columnLeft, float columnRight, float centerY) {
            float maxBadgeWidth = Math.max(0f, (columnRight - columnLeft) - (cellPadding * 2f));
            String badgeLabel = ellipsizePdfText(label, statusPaint, Math.max(0f, maxBadgeWidth - (badgeHorizontalPadding * 2f)));
            float badgeTextWidth = statusPaint.measureText(badgeLabel);
            float badgeWidth = Math.min(maxBadgeWidth, Math.max(badgeMinWidth, badgeTextWidth + (badgeHorizontalPadding * 2f)));
            float badgeHeight = getPaintHeight(statusPaint) + (badgeVerticalPadding * 2f);

            float badgeLeft = columnLeft + ((columnRight - columnLeft) - badgeWidth) / 2f;
            float badgeTop = centerY - (badgeHeight / 2f);
            badgeRect.set(badgeLeft, badgeTop, badgeLeft + badgeWidth, badgeTop + badgeHeight);

            badgePaint.setColor(getPdfStatusColor(state));
            canvas.drawRoundRect(badgeRect, badgeCornerRadius, badgeCornerRadius, badgePaint);

            float textX = badgeRect.left + ((badgeRect.width() - statusPaint.measureText(badgeLabel)) / 2f);
            canvas.drawText(badgeLabel, textX, getTextBaseline(centerY, statusPaint), statusPaint);
        }
    }

    private void savePdf(android.graphics.pdf.PdfDocument document, String name) throws IOException {
        String fileName = name.replaceAll("[^a-zA-Z0-9]", "_") + "_Orders_" + System.currentTimeMillis() + ".pdf";
        File file;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            android.net.Uri uri = getContentResolver().insert(
                    android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (FileOutputStream fos = (FileOutputStream) getContentResolver().openOutputStream(uri)) {
                    document.writeTo(fos);
                }
                document.close();
                SnackbarUtils.showSuccess(findViewById(android.R.id.content), "PDF saved to Downloads: " + fileName);
                clearDraftsAfterExport();
            }
        } else {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                document.writeTo(fos);
            }
            document.close();

            android.content.Intent mediaScanIntent = new android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(android.net.Uri.fromFile(file));
            sendBroadcast(mediaScanIntent);

            SnackbarUtils.showSuccess(findViewById(android.R.id.content), "PDF saved to Downloads: " + fileName);
            clearDraftsAfterExport();
        }
    }
    
    private void clearDraftsAfterExport() {
        // Clear persisted drafts and orders
        if (draftKey != null) {
            preferenceManager.clearDraftOrders(draftKey);
        }
        
        draftOrders.clear();
        draftAdapter.clear();
        
        // Reset export mode
        exportReady = false;
        btnBookAll.setText("Book All Orders");
        if (etExportName != null) {
            etExportName.setVisibility(View.GONE);
            etExportName.setText("");
        }
        
        // Clear input fields
        etRttp.setText("");
        etFirst.setText("");
        etSecond.setText("");
        
        updateTotals();
        tvEmptyState.setVisibility(View.VISIBLE);
    }
    
    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
