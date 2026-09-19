package com.geo.enterprises.orders;

import android.Manifest;
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
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.models.AppSettings;
import com.geo.enterprises.models.Order;
import com.geo.enterprises.models.User;
import com.geo.enterprises.utils.ConfirmationDialog;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GameOrdersActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvOrders;
    private View emptyView;
    private TextView tvEmptyMessage;
    private ExtendedFloatingActionButton fabExport;
    
    private OrdersAdapter ordersAdapter;
    private List<Order> ordersList;
    private String gameName;
    
    private PreferenceManager preferenceManager;
    private User currentUser;
    private AppSettings appSettings;
    
    // Track export format for permission callback
    private boolean pendingExportAsPdf = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_orders);
        
        // Initialize
        preferenceManager = new PreferenceManager(this);
        currentUser = preferenceManager.getUserData();
        appSettings = preferenceManager.getAppSettings();
        
        // Get intent data
        Intent intent = getIntent();
        if (intent != null) {
            gameName = intent.getStringExtra("game_name");
            ordersList = (List<Order>) intent.getSerializableExtra("orders");
            
            if (ordersList == null) {
                ordersList = new ArrayList<>();
            }
        }
        
        // Initialize views
        initializeViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup click listeners
        setupClickListeners();
        
        // Display orders
        displayOrders();
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvOrders = findViewById(R.id.rv_orders);
        emptyView = findViewById(R.id.empty_view);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        fabExport = findViewById(R.id.fab_export);
        
        // Set title
        if (gameName != null) {
            tvTitle.setText(gameName);
        }
        
        // Apply status bar padding to prevent overlap
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
        
        // Apply professional bottom padding fix for export FAB
        if (fabExport != null) {
            WindowInsetsHelper.applyProfessionalBottomNavigationFix(this, fabExport);
        }
    }
    
    private void setupRecyclerView() {
        ordersAdapter = new OrdersAdapter(this, ordersList, true); // true = hide game name
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(ordersAdapter);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        fabExport.setOnClickListener(v -> showExportDialog());
    }
    
    private void displayOrders() {
        if (ordersList != null && !ordersList.isEmpty()) {
            rvOrders.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            ordersAdapter.notifyDataSetChanged();
            
            // Update export button
            updateExportButton();
        } else {
            rvOrders.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText("No orders found for " + gameName);
            fabExport.setVisibility(View.GONE);
        }
    }
    
    private void updateExportButton() {
        double totalSum = calculateTotalSum();
        fabExport.setText(String.format("Export (₨%.0f)", totalSum));
        fabExport.setVisibility(View.VISIBLE);
    }
    
    private double calculateTotalSum() {
        double sum = 0;
        for (Order order : ordersList) {
            sum += order.getTotalAmount();
        }
        return sum;
    }
    
    private void showExportDialog() {
        new ConfirmationDialog(this)
            .setTitle("Export Orders")
            .setMessage("Choose your preferred export format:\n\n• PDF - Portable document format\n• Image - PNG image file\n\nBoth formats will be saved to your Downloads folder.")
            .setIcon(R.drawable.ic_export)
            .setConfirmButtonText("Export as PDF")
            .setCancelButtonText("Export as Image")
            .setOnConfirmListener(new ConfirmationDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    // PDF selected
                    checkPermissionAndExport(true);
                }
                
                @Override
                public void onCancel() {
                    // Image selected (using cancel button as second option)
                    checkPermissionAndExport(false);
                }
            })
            .show();
    }
    
    private void checkPermissionAndExport(boolean asPdf) {
        // For Android 13+ (API 33+), we don't need WRITE_EXTERNAL_STORAGE for public directories
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+, no permission needed for Downloads folder
            exportInvoice(asPdf);
            return;
        }
        
        // For Android 10-12 (API 29-32), use scoped storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+, no permission needed with MediaStore
            exportInvoice(asPdf);
            return;
        }
        
        // For Android 6-9 (API 23-28), need WRITE_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Store the export format for later
                pendingExportAsPdf = asPdf;
                
                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                // Permission already granted
                exportInvoice(asPdf);
            }
        } else {
            // Below Android 6, no runtime permission needed
            exportInvoice(asPdf);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with export
                exportInvoice(pendingExportAsPdf);
            } else {
                // Permission denied
                SnackbarUtils.showError(findViewById(android.R.id.content), 
                    "Permission denied. Cannot export files.");
                
                // Check if user checked "Don't ask again"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // User checked "Don't ask again", show dialog to go to settings
                        showPermissionSettingsDialog();
                    }
                }
            }
        }
    }
    
    private void showPermissionSettingsDialog() {
        new ConfirmationDialog(this)
            .setTitle("Permission Required")
            .setMessage("Storage permission is required to export orders. Please enable it in app settings.")
            .setIcon(R.drawable.ic_settings)
            .setConfirmButtonText("Open Settings")
            .setCancelButtonText("Cancel")
            .setOnConfirmListener(new ConfirmationDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    // Open app settings
                    android.content.Intent intent = new android.content.Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    android.net.Uri uri = android.net.Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                
                @Override
                public void onCancel() {
                    // User cancelled, do nothing
                }
            })
            .show();
    }
    
    private void exportInvoice(boolean asPdf) {
        try {
            // Inflate invoice layout
            View invoiceView = LayoutInflater.from(this).inflate(R.layout.layout_invoice, null);
            
            // Populate invoice data
            populateInvoiceData(invoiceView);
            
            if (asPdf) {
                // Match A4 export canvas width to avoid unnecessary scaling and maximize vertical space.
                int width = 1190;
                
                // Measure and layout the view properly
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                
                invoiceView.measure(widthMeasureSpec, heightMeasureSpec);
                invoiceView.layout(0, 0, invoiceView.getMeasuredWidth(), invoiceView.getMeasuredHeight());
                
                exportPaginatedPdf(invoiceView);
            } else {
                // Use a stable wide canvas for image export as well.
                int width = 1500;
                
                // Measure and layout the view properly
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                
                invoiceView.measure(widthMeasureSpec, heightMeasureSpec);
                invoiceView.layout(0, 0, invoiceView.getMeasuredWidth(), invoiceView.getMeasuredHeight());
                
                exportAsImage(invoiceView);
            }
            
        } catch (Exception e) {
            android.util.Log.e("GameOrdersActivity", "Export error: " + e.getMessage());
            e.printStackTrace();
            SnackbarUtils.showError(findViewById(android.R.id.content), 
                "Export failed: " + e.getMessage());
        }
    }

    private void exportPaginatedPdf(View invoiceView) throws IOException {
        PdfDocument document = new PdfDocument();
        
        // A4 Portrait: 595x842 points. Doubled for quality: 1190x1684.
        int pageWidth = 1190;
        int pageHeight = 1684;
        
        // Get view measurements (should be measured before passed here)
        int viewWidth = invoiceView.getMeasuredWidth();
        
        // Calculate scale to fit view width into page width
        // Never upscale smaller layouts; upscaling reduces usable page height and causes early page breaks.
        float scale = viewWidth > 0 ? Math.min(1.0f, (float) pageWidth / viewWidth) : 1.0f;
        
        // Calculate effective page height in view pixels (unscaled)
        float scaledPageHeight = pageHeight / scale;
        
        // Get padding from view
        int paddingStart = invoiceView.getPaddingStart();
        int paddingEnd = invoiceView.getPaddingEnd();
        int paddingTop = invoiceView.getPaddingTop();
        int paddingBottom = invoiceView.getPaddingBottom();
        
        // Content width in view pixels
        int contentWidth = viewWidth - paddingStart - paddingEnd;
        
        // Cast to LinearLayout to access children
        LinearLayout rootLayout = (LinearLayout) invoiceView;
        
        // Find the orders container to skip it and to know where to split header/footer
        int ordersContainerIndex = -1;
        View tableHeader = null;
        
        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            View child = rootLayout.getChildAt(i);
            if (child.getId() == R.id.ll_invoice_orders) {
                ordersContainerIndex = i;
                // The table header is usually the view before the orders container
                if (i > 0) {
                    tableHeader = rootLayout.getChildAt(i - 1);
                }
                break;
            }
        }
        
        if (ordersContainerIndex == -1) {
            // Fallback if structure is different
            ordersContainerIndex = rootLayout.getChildCount();
        }

        // Initialize Page 1
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        
        // Draw white background
        canvas.drawColor(android.graphics.Color.WHITE);
        
        // Apply scale for the entire page
        canvas.save();
        canvas.scale(scale, scale);
        
        int currentY = paddingTop;
        
        // 1. Draw Header (Children before ordersContainer)
        for (int i = 0; i < ordersContainerIndex; i++) {
            View child = rootLayout.getChildAt(i);
            
            // Measure child with contentWidth (unscaled)
            int childWidthSpec = View.MeasureSpec.makeMeasureSpec(contentWidth, View.MeasureSpec.EXACTLY);
            int childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            child.measure(childWidthSpec, childHeightSpec);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            currentY += params.topMargin;
            
            canvas.save();
            canvas.translate(paddingStart, currentY);
            child.draw(canvas);
            canvas.restore();
            
            currentY += child.getMeasuredHeight() + params.bottomMargin;
        }
        
        // 2. Loop Orders
        for (Order order : ordersList) {
            // Create Row
            View row = createOrderRow(order);
            
            // Measure row with contentWidth (unscaled)
            int rowWidthSpec = View.MeasureSpec.makeMeasureSpec(contentWidth, View.MeasureSpec.EXACTLY);
            int rowHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            row.measure(rowWidthSpec, rowHeightSpec);
            row.layout(0, 0, row.getMeasuredWidth(), row.getMeasuredHeight());
            int rowHeight = row.getMeasuredHeight();
            
            // Check if row fits using scaledPageHeight
            if (currentY + rowHeight > scaledPageHeight - paddingBottom) {
                // Finish current page
                canvas.restore(); // Restore scale before finishing page
                document.finishPage(page);
                
                // Start new page
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                canvas.drawColor(android.graphics.Color.WHITE);
                
                // Re-apply scale for new page
                canvas.save();
                canvas.scale(scale, scale);
                
                currentY = paddingTop;
                
                // Draw Table Header again if available
                if (tableHeader != null) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tableHeader.getLayoutParams();
                    currentY += params.topMargin;
                    
                    canvas.save();
                    canvas.translate(paddingStart, currentY);
                    tableHeader.draw(canvas);
                    canvas.restore();
                    
                    currentY += tableHeader.getMeasuredHeight() + params.bottomMargin;
                }
            }
            
            // Draw Row
            canvas.save();
            canvas.translate(paddingStart, currentY);
            row.draw(canvas);
            canvas.restore();
            currentY += rowHeight;
        }
        
        // 3. Draw Footer (Children after ordersContainer)
        // Calculate required height for footer
        int footerHeight = 0;
        for (int i = ordersContainerIndex + 1; i < rootLayout.getChildCount(); i++) {
            View child = rootLayout.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            int childWidthSpec = View.MeasureSpec.makeMeasureSpec(contentWidth, View.MeasureSpec.EXACTLY);
            int childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            child.measure(childWidthSpec, childHeightSpec);
            footerHeight += child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
        }
        
        // Check if footer fits
        if (currentY + footerHeight > scaledPageHeight - paddingBottom) {
            canvas.restore(); // Restore scale
            document.finishPage(page);
            pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            canvas.drawColor(android.graphics.Color.WHITE);
            canvas.save();
            canvas.scale(scale, scale);
            currentY = paddingTop;
        }
        
        // Draw Footer
        for (int i = ordersContainerIndex + 1; i < rootLayout.getChildCount(); i++) {
            View child = rootLayout.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            currentY += params.topMargin;
            
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            
            canvas.save();
            canvas.translate(paddingStart, currentY);
            child.draw(canvas);
            canvas.restore();
            
            currentY += child.getMeasuredHeight() + params.bottomMargin;
        }
        
        canvas.restore(); // Restore scale before finishing
        document.finishPage(page);
        
        // Save PDF
        String fileName = gameName.replaceAll("[^a-zA-Z0-9]", "_") + "_Orders_" + System.currentTimeMillis() + ".pdf";
        savePdfFile(document, fileName);
    }
    
    private void savePdfFile(PdfDocument document, String fileName) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - Use MediaStore
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
            }
        } else {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                document.writeTo(fos);
            }
            document.close();
            
            // Scan file
            android.content.Intent mediaScanIntent = new android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(android.net.Uri.fromFile(file));
            sendBroadcast(mediaScanIntent);
            
            SnackbarUtils.showSuccess(findViewById(android.R.id.content), "PDF saved to Downloads: " + fileName);
        }
    }
    
    private void populateInvoiceData(View invoiceView) {
        // App logo
        ImageView ivLogo = invoiceView.findViewById(R.id.iv_invoice_logo);
        if (appSettings != null && appSettings.getAppLogo() != null && !appSettings.getAppLogo().isEmpty()) {
            // Load logo from URL if available
            try {
                String logoUrl = appSettings.getAppLogo();
                // If the logo URL doesn't start with http, construct the full URL
                if (!logoUrl.startsWith("http")) {
                    logoUrl = com.geo.enterprises.config.AppConfig.PUBLIC_BASE_URL + "/" + logoUrl;
                }
                
                com.bumptech.glide.Glide.with(this)
                    .load(logoUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ivLogo);
            } catch (Exception e) {
                android.util.Log.e("GameOrdersActivity", "Error loading logo: " + e.getMessage());
                ivLogo.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            ivLogo.setImageResource(R.mipmap.ic_launcher);
        }
        
        // App branding
        TextView tvAppName = invoiceView.findViewById(R.id.tv_invoice_app_name);
        TextView tvTagline = invoiceView.findViewById(R.id.tv_invoice_tagline);
        
        if (appSettings != null) {
            tvAppName.setText(appSettings.getAppName());
            tvTagline.setText(appSettings.getAppTagline() != null ? appSettings.getAppTagline() : "Prize Bond Booking System");
        }
        
        // Invoice Title with Game Name
        TextView tvInvoiceTitle = invoiceView.findViewById(R.id.tv_invoice_title);
        tvInvoiceTitle.setText(gameName + " VOUCHER");
        
        // User info
        TextView tvUserName = invoiceView.findViewById(R.id.tv_invoice_user_name);
        TextView tvUserPhone = invoiceView.findViewById(R.id.tv_invoice_user_phone);
        TextView tvDate = invoiceView.findViewById(R.id.tv_invoice_date);
        
        if (currentUser != null) {
            tvUserName.setText(currentUser.getFullName());
            tvUserPhone.setText(currentUser.getPhone());
        }
        
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);
        
        // Orders table - only show orders for this game
        LinearLayout llOrders = invoiceView.findViewById(R.id.ll_invoice_orders);
        llOrders.removeAllViews();
        
        android.util.Log.d("GameOrdersActivity", "Exporting " + ordersList.size() + " orders for game: " + gameName);
        
        // Add orders for this game (no header needed, game name is in title)
        for (Order order : ordersList) {
            View orderRow = createOrderRow(order);
            llOrders.addView(orderRow);
        }
        
        // Total amount (no subtotal needed, keep it minimal)
        TextView tvTotal = invoiceView.findViewById(R.id.tv_invoice_total);
        double totalSum = calculateTotalSum();
        tvTotal.setText(String.format("₨ %.0f", totalSum));
    }
    
    private View createGameHeader(String gameName, int orderCount) {
        LinearLayout header = new LinearLayout(this);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setPadding(8, 12, 8, 8);
        header.setBackgroundColor(getResources().getColor(R.color.primary_color));
        
        TextView tvGameName = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        tvGameName.setLayoutParams(params);
        tvGameName.setText(gameName + " (" + orderCount + " orders)");
        tvGameName.setTextSize(11);
        tvGameName.setTextColor(getResources().getColor(R.color.white));
        tvGameName.setTypeface(null, android.graphics.Typeface.BOLD);
        header.addView(tvGameName);
        
        return header;
    }
    
    private View createGameSubtotal(List<Order> gameOrders) {
        LinearLayout subtotal = new LinearLayout(this);
        subtotal.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        subtotal.setOrientation(LinearLayout.HORIZONTAL);
        subtotal.setPadding(8, 8, 8, 8);
        subtotal.setBackgroundColor(getResources().getColor(R.color.background_primary));
        
        // Calculate subtotals
        double firstTotal = 0;
        double secondTotal = 0;
        double grandTotal = 0;
        
        for (Order order : gameOrders) {
            try {
                firstTotal += Double.parseDouble(order.getFirst());
                secondTotal += Double.parseDouble(order.getSecond());
            } catch (NumberFormatException e) {
                // Ignore
            }
            grandTotal += order.getTotalAmount();
        }
        
        // Spacer
        View spacer = new View(this);
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        spacer.setLayoutParams(spacerParams);
        subtotal.addView(spacer);
        
        // Subtotal label
        TextView tvLabel = new TextView(this);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(110, LinearLayout.LayoutParams.WRAP_CONTENT));
        tvLabel.setText("Subtotal:");
        tvLabel.setTextSize(9);
        tvLabel.setTextColor(getResources().getColor(R.color.text_primary));
        tvLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        tvLabel.setGravity(android.view.Gravity.END);
        subtotal.addView(tvLabel);
        
        // First subtotal
        TextView tvFirst = new TextView(this);
        tvFirst.setLayoutParams(new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT));
        tvFirst.setText(String.format("%.0f", firstTotal));
        tvFirst.setTextSize(9);
        tvFirst.setTextColor(getResources().getColor(R.color.text_primary));
        tvFirst.setTypeface(null, android.graphics.Typeface.BOLD);
        tvFirst.setGravity(android.view.Gravity.END);
        subtotal.addView(tvFirst);
        
        // Second subtotal
        TextView tvSecond = new TextView(this);
        tvSecond.setLayoutParams(new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT));
        tvSecond.setText(String.format("%.0f", secondTotal));
        tvSecond.setTextSize(9);
        tvSecond.setTextColor(getResources().getColor(R.color.text_primary));
        tvSecond.setTypeface(null, android.graphics.Typeface.BOLD);
        tvSecond.setGravity(android.view.Gravity.END);
        subtotal.addView(tvSecond);
        
        // Grand subtotal
        TextView tvTotal = new TextView(this);
        tvTotal.setLayoutParams(new LinearLayout.LayoutParams(130, LinearLayout.LayoutParams.WRAP_CONTENT));
        tvTotal.setText(String.format("%.0f", grandTotal));
        tvTotal.setTextSize(9);
        tvTotal.setTextColor(getResources().getColor(R.color.text_primary));
        tvTotal.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTotal.setGravity(android.view.Gravity.END);
        subtotal.addView(tvTotal);
        
        return subtotal;
    }
    
    private View createOrderRow(Order order) {
        InvoiceOrderRowView row = new InvoiceOrderRowView(order);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return row;
    }

    private String getExportStatusLabel(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "PENDING";
        }

        String normalized = status.trim().toLowerCase(Locale.getDefault());
        switch (normalized) {
            case "win":
                return "WIN";
            case "ok":
                return "OK";
            case "approved":
                return "APPROVED";
            case "rejected":
                return "REJECTED";
            case "pending":
            default:
                return normalized.toUpperCase(Locale.getDefault());
        }
    }

    private int getExportStatusColor(String status) {
        if (status == null) {
            return ContextCompat.getColor(this, R.color.warning);
        }

        switch (status.trim().toLowerCase(Locale.getDefault())) {
            case "win":
                return ContextCompat.getColor(this, R.color.primary_color);
            case "ok":
                return 0xFF4CAF50;
            case "approved":
                return 0xFF9C27B0;
            case "rejected":
                return 0xFFF44336;
            case "pending":
            default:
                return 0xFFFFA500;
        }
    }

    private String formatInvoiceNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "0";
        }

        try {
            return String.format(Locale.getDefault(), "%.0f", Double.parseDouble(value.trim()));
        } catch (NumberFormatException e) {
            return value.trim();
        }
    }

    private String formatInvoiceNumber(double value) {
        return String.format(Locale.getDefault(), "%.0f", value);
    }

    private String normalizeInvoiceText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
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

    private String ellipsizeInvoiceText(String text, TextPaint paint, float maxWidth) {
        if (maxWidth <= 0f) {
            return "";
        }
        return TextUtils.ellipsize(
                normalizeInvoiceText(text, "-"),
                paint,
                maxWidth,
                TextUtils.TruncateAt.END).toString();
    }

    private final class InvoiceOrderRowView extends View {
        private final float[] columnWeights = {1.7f, 1.0f, 0.9f, 0.9f, 1.0f, 1.1f};

        private final Order order;
        private final TextPaint bondPaint;
        private final TextPaint valuePaint;
        private final TextPaint statusPaint;
        private final Paint badgePaint;
        private final Paint dividerPaint;
        private final RectF badgeRect = new RectF();
        private final float horizontalInset;
        private final float cellPadding;
        private final float verticalPadding;
        private final float badgeHorizontalPadding;
        private final float badgeVerticalPadding;
        private final float badgeCornerRadius;
        private final float badgeMinWidth;
        private final int textColor;
        private final int whiteColor;
        private final float totalWeight;

        InvoiceOrderRowView(Order order) {
            super(GameOrdersActivity.this);
            this.order = order;

            textColor = ContextCompat.getColor(GameOrdersActivity.this, R.color.text_primary);
            whiteColor = ContextCompat.getColor(GameOrdersActivity.this, R.color.white);

            horizontalInset = dpToPx(2f);
            cellPadding = dpToPx(2f);
            verticalPadding = dpToPx(3f);
            badgeHorizontalPadding = dpToPx(8f);
            badgeVerticalPadding = dpToPx(3f);
            badgeCornerRadius = dpToPx(10f);
            badgeMinWidth = dpToPx(46f);

            bondPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            bondPaint.setColor(textColor);
            bondPaint.setTextSize(spToPx(10f));

            valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            valuePaint.setColor(textColor);
            valuePaint.setTextSize(spToPx(11f));
            valuePaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

            statusPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            statusPaint.setColor(whiteColor);
            statusPaint.setTextSize(spToPx(9f));
            statusPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

            badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dividerPaint.setColor(ContextCompat.getColor(GameOrdersActivity.this, R.color.divider_color));
            dividerPaint.setStrokeWidth(Math.max(1f, dpToPx(1f)));

            float weightSum = 0f;
            for (float weight : columnWeights) {
                weightSum += weight;
            }
            totalWeight = weightSum;

            setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);

            float textHeight = Math.max(getPaintHeight(bondPaint), getPaintHeight(valuePaint));
            float badgeHeight = getPaintHeight(statusPaint) + (badgeVerticalPadding * 2f);
            int desiredHeight = Math.round(Math.max(dpToPx(28f), Math.max(textHeight, badgeHeight) + (verticalPadding * 2f)));

            setMeasuredDimension(width, resolveSize(desiredHeight, heightMeasureSpec));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float rowLeft = horizontalInset;
            float rowRight = getWidth() - horizontalInset;
            float availableWidth = Math.max(0f, rowRight - rowLeft);
            float centerY = getHeight() / 2f;
            float currentLeft = rowLeft;

            String[] values = {
                    normalizeInvoiceText(order.getBondName(), "-"),
                    normalizeInvoiceText(order.getRttp(), "-"),
                    formatInvoiceNumber(order.getFirst()),
                    formatInvoiceNumber(order.getSecond()),
                    formatInvoiceNumber(order.getTotalAmount()),
                    getExportStatusLabel(order.getStatus())
            };

            for (int i = 0; i < columnWeights.length; i++) {
                float columnWidth = totalWeight == 0f ? 0f : (availableWidth * columnWeights[i] / totalWeight);
                float columnLeft = currentLeft;
                float columnRight = columnLeft + columnWidth;

                if (i == 0) {
                    drawLeftText(canvas, values[i], bondPaint, columnLeft, columnRight, centerY);
                } else if (i == 1) {
                    drawCenteredText(canvas, values[i], valuePaint, columnLeft, columnRight, centerY);
                } else if (i >= 2 && i <= 4) {
                    drawRightText(canvas, values[i], valuePaint, columnLeft, columnRight, centerY);
                } else {
                    drawStatusBadge(canvas, values[i], order.getStatus(), columnLeft, columnRight, centerY);
                }

                currentLeft = columnRight;
            }

            float dividerY = getHeight() - (dividerPaint.getStrokeWidth() / 2f);
            canvas.drawLine(rowLeft, dividerY, rowRight, dividerY, dividerPaint);
        }

        private float getPaintHeight(Paint paint) {
            return paint.descent() - paint.ascent();
        }

        private void drawLeftText(Canvas canvas, String text, TextPaint paint, float columnLeft, float columnRight, float centerY) {
            float maxWidth = Math.max(0f, (columnRight - columnLeft) - (cellPadding * 2f));
            String displayText = ellipsizeInvoiceText(text, paint, maxWidth);
            canvas.drawText(displayText, columnLeft + cellPadding, getTextBaseline(centerY, paint), paint);
        }

        private void drawCenteredText(Canvas canvas, String text, TextPaint paint, float columnLeft, float columnRight, float centerY) {
            float maxWidth = Math.max(0f, (columnRight - columnLeft) - (cellPadding * 2f));
            String displayText = ellipsizeInvoiceText(text, paint, maxWidth);
            float textWidth = paint.measureText(displayText);
            float x = columnLeft + ((columnRight - columnLeft) - textWidth) / 2f;
            canvas.drawText(displayText, x, getTextBaseline(centerY, paint), paint);
        }

        private void drawRightText(Canvas canvas, String text, TextPaint paint, float columnLeft, float columnRight, float centerY) {
            float maxWidth = Math.max(0f, (columnRight - columnLeft) - (cellPadding * 2f));
            String displayText = ellipsizeInvoiceText(text, paint, maxWidth);
            float x = columnRight - cellPadding - paint.measureText(displayText);
            canvas.drawText(displayText, Math.max(columnLeft + cellPadding, x), getTextBaseline(centerY, paint), paint);
        }

        private void drawStatusBadge(Canvas canvas, String label, String rawStatus, float columnLeft, float columnRight, float centerY) {
            float maxBadgeWidth = Math.max(0f, (columnRight - columnLeft) - (cellPadding * 2f));
            String badgeLabel = ellipsizeInvoiceText(label, statusPaint, Math.max(0f, maxBadgeWidth - (badgeHorizontalPadding * 2f)));
            float badgeTextWidth = statusPaint.measureText(badgeLabel);
            float badgeWidth = Math.min(maxBadgeWidth, Math.max(badgeMinWidth, badgeTextWidth + (badgeHorizontalPadding * 2f)));
            float badgeHeight = getPaintHeight(statusPaint) + (badgeVerticalPadding * 2f);
            float badgeLeft = columnLeft + ((columnRight - columnLeft) - badgeWidth) / 2f;
            float badgeTop = centerY - (badgeHeight / 2f);

            badgeRect.set(badgeLeft, badgeTop, badgeLeft + badgeWidth, badgeTop + badgeHeight);
            badgePaint.setColor(getExportStatusColor(rawStatus));
            canvas.drawRoundRect(badgeRect, badgeCornerRadius, badgeCornerRadius, badgePaint);

            float textX = badgeRect.left + ((badgeRect.width() - statusPaint.measureText(badgeLabel)) / 2f);
            canvas.drawText(badgeLabel, textX, getTextBaseline(centerY, statusPaint), statusPaint);
        }
    }
    
    
    private void exportAsImage(View invoiceView) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(
                    invoiceView.getMeasuredWidth(),
                    invoiceView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            
            Canvas canvas = new Canvas(bitmap);
            invoiceView.draw(canvas);
            
            // Save image
            String fileName = gameName.replaceAll("[^a-zA-Z0-9]", "_") + "_Orders_" + System.currentTimeMillis() + ".png";
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - Use MediaStore
                android.content.ContentValues values = new android.content.ContentValues();
                values.put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png");
                values.put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                
                android.net.Uri uri = getContentResolver().insert(
                    android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                
                if (uri != null) {
                    FileOutputStream fos = (FileOutputStream) getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    
                    SnackbarUtils.showSuccess(findViewById(android.R.id.content), 
                        "Image saved to Downloads: " + fileName);
                }
            } else {
                // Android 9 and below - Use traditional storage
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), fileName);
                
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                
                // Notify media scanner
                android.content.Intent mediaScanIntent = new android.content.Intent(
                    android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(android.net.Uri.fromFile(file));
                sendBroadcast(mediaScanIntent);
                
                SnackbarUtils.showSuccess(findViewById(android.R.id.content), 
                        "Image saved to Downloads: " + fileName);
            }
            
        } catch (IOException e) {
            android.util.Log.e("GameOrdersActivity", "Image export error: " + e.getMessage());
            e.printStackTrace();
            SnackbarUtils.showError(findViewById(android.R.id.content), 
                "Failed to export image: " + e.getMessage());
        }
    }
}
