package com.geo.enterprises.deposit;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.res.ResourcesCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.Deposit;
import com.geo.enterprises.models.PaymentMethod;
import com.geo.enterprises.models.PaymentMethodsResponse;
import com.geo.enterprises.utils.LoadingDialog;
import com.geo.enterprises.utils.PreferenceManager;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivBack, ivBankLogo, ivPaymentProofPreview, ivCopyAccount;
    private Spinner spinnerPaymentMethod;
    private CardView cardPaymentDetails;
    private LinearLayout llCopyAccount;
    private TextView tvBankName, tvAccountTitle, tvAccountNo, tvFileName, tvMinLimit, tvMaxLimit;
    private EditText etAmount, etTransactionId;
    private MaterialButton btnChooseFile, btnSubmit;

    private List<PaymentMethod> paymentMethods;
    private List<PaymentMethod> activePaymentMethods; // Store filtered active methods
    private PaymentMethod selectedPaymentMethod;
    private Uri selectedImageUri;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private LoadingDialog loadingDialog;

    private static final int REQUEST_STORAGE_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_deposit);

        initializeViews();
        applyUrduFont();
        initializeServices();
        loadPaymentMethods();
        setupListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        spinnerPaymentMethod = findViewById(R.id.spinner_payment_method);
        cardPaymentDetails = findViewById(R.id.card_payment_details);
        ivBankLogo = findViewById(R.id.iv_bank_logo);
        tvBankName = findViewById(R.id.tv_bank_name);
        tvAccountTitle = findViewById(R.id.tv_account_title);
    tvAccountNo = findViewById(R.id.tv_account_no);
    llCopyAccount = findViewById(R.id.ll_copy_account);
    ivCopyAccount = findViewById(R.id.iv_copy_account);
        tvMinLimit = findViewById(R.id.tv_min_limit);
        tvMaxLimit = findViewById(R.id.tv_max_limit);
        etAmount = findViewById(R.id.et_amount);
        etTransactionId = findViewById(R.id.et_transaction_id);
        btnChooseFile = findViewById(R.id.btn_choose_file);
        tvFileName = findViewById(R.id.tv_file_name);
        ivPaymentProofPreview = findViewById(R.id.iv_payment_proof_preview);
        btnSubmit = findViewById(R.id.btn_submit);

        paymentMethods = new ArrayList<>();
        
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

    private void initializeServices() {
        apiService = ApiClient.getInstance().getApiService();
        preferenceManager = new PreferenceManager(this);
    }

    private void applyUrduFont() {
        try {
            Typeface nastaliq = ResourcesCompat.getFont(this, R.font.noto_nastaliq_urdu);
            if (nastaliq == null) return;

            int[] urduTextIds = {
                R.id.tv_how_to_deposit_title,
                R.id.tv_how_to_deposit_steps,
                R.id.tv_amount_urdu,
                R.id.tv_transaction_id_urdu,
                R.id.tv_payment_proof_urdu,
                R.id.tv_important_notice_title,
                R.id.tv_important_notice_desc
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

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    // Subtract 1 because of placeholder at position 0
                    selectedPaymentMethod = activePaymentMethods.get(position - 1);
                    showPaymentMethodDetails(selectedPaymentMethod);
                } else {
                    selectedPaymentMethod = null;
                    cardPaymentDetails.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPaymentMethod = null;
                cardPaymentDetails.setVisibility(View.GONE);
            }
        });

    btnChooseFile.setOnClickListener(v -> checkPermissionAndOpenChooser());

    btnSubmit.setOnClickListener(v -> submitDeposit());

    // Copy account number functionality
    llCopyAccount.setOnClickListener(v -> copyAccountNumber());
    }

    private void loadPaymentMethods() {
        apiService.getPaymentMethods().enqueue(new Callback<PaymentMethodsResponse>() {
            @Override
            public void onResponse(Call<PaymentMethodsResponse> call, Response<PaymentMethodsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentMethodsResponse pmResponse = response.body();
                    if (pmResponse.isSuccess() && pmResponse.getData() != null) {
                        paymentMethods = pmResponse.getData();
                        setupPaymentMethodSpinner();
                    } else {
                        SnackbarUtils.showError(findViewById(android.R.id.content),
                                "No payment methods available");
                    }
                } else {
                    SnackbarUtils.showError(findViewById(android.R.id.content),
                            "Failed to load payment methods");
                }
            }

            @Override
            public void onFailure(Call<PaymentMethodsResponse> call, Throwable t) {
                SnackbarUtils.showError(findViewById(android.R.id.content),
                        "Network error: " + t.getMessage());
            }
        });
    }

    private void setupPaymentMethodSpinner() {
        // Filter only active methods
        activePaymentMethods = new ArrayList<>();
        for (PaymentMethod method : paymentMethods) {
            if (method.isActive()) {
                activePaymentMethods.add(method);
            }
        }

        // Use custom adapter with images
        PaymentMethodAdapter adapter = new PaymentMethodAdapter(this, activePaymentMethods, true);
        spinnerPaymentMethod.setAdapter(adapter);
    }

    

    private void showPaymentMethodDetails(PaymentMethod method) {
        cardPaymentDetails.setVisibility(View.VISIBLE);

        // Load bank logo
        if (method.getBankImage() != null && !method.getBankImage().isEmpty()) {
            String imageUrl = method.getBankImage();
            
            // Log the original URL
            android.util.Log.d("DepositActivity", "Original image URL: " + imageUrl);
            android.util.Log.d("DepositActivity", "Full payment method: " + method.toString());
            
            // Fix the URL - the API returns URL without 'public' but server needs it
            // API returns: [SERVER_DOMAIN]/img/...
            // But should be: [SERVER_DOMAIN]/public/img/...
            if (imageUrl.contains("/img/") && !imageUrl.contains("/public/img/")) {
                imageUrl = imageUrl.replace("/img/", "/public/img/");
                android.util.Log.d("DepositActivity", "Fixed image URL: " + imageUrl);
            }
            
            // Create final copy for inner class
            final String finalImageUrl = imageUrl;
            
            android.util.Log.d("DepositActivity", "Loading image from: " + finalImageUrl);
            
            // Load image with Glide
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
                            android.util.Log.e("DepositActivity", "Image load failed: " + finalImageUrl, e);
                            android.util.Log.e("DepositActivity", "Error details: " + errorMsg);
                            
                            SnackbarUtils.showError(findViewById(android.R.id.content), 
                                "Failed to load bank image");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            android.util.Log.d("DepositActivity", "Image loaded successfully: " + finalImageUrl);
                            return false;
                        }
                    })
                    .into(ivBankLogo);
        } else {
            // Set placeholder if no image
            android.util.Log.w("DepositActivity", "No bank image provided");
            ivBankLogo.setImageResource(R.drawable.ic_orders);
        }

        tvBankName.setText(method.getBankOrAccountName());
        tvAccountTitle.setText(method.getAccountTitle());
        tvAccountNo.setText(method.getAccountNo());
        
        // Display deposit limits
        tvMinLimit.setText("₨ " + String.format("%.0f", method.getMinimumLimit()));
        tvMaxLimit.setText("₨ " + String.format("%.0f", method.getMaximumLimit()));
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Show file name
            String fileName = getFileName(selectedImageUri);
            tvFileName.setText(fileName);

            // Show preview
            ivPaymentProofPreview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(ivPaymentProofPreview);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void submitDeposit() {
        // Validate inputs
        if (selectedPaymentMethod == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Please select a payment method");
            return;
        }

        String amount = etAmount.getText().toString().trim();
        if (amount.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Please enter amount");
            etAmount.requestFocus();
            return;
        }

        // Validate amount against limits
        try {
            double amountValue = Double.parseDouble(amount);
            double minLimit = selectedPaymentMethod.getMinimumLimit();
            double maxLimit = selectedPaymentMethod.getMaximumLimit();
            
            if (amountValue < minLimit) {
                SnackbarUtils.showError(findViewById(android.R.id.content),
                        "Amount must be at least ₨ " + String.format("%.0f", minLimit));
                etAmount.requestFocus();
                return;
            }
            
            if (amountValue > maxLimit) {
                SnackbarUtils.showError(findViewById(android.R.id.content),
                        "Amount cannot exceed ₨ " + String.format("%.0f", maxLimit));
                etAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Please enter a valid amount");
            etAmount.requestFocus();
            return;
        }

        String transactionId = etTransactionId.getText().toString().trim();
        if (transactionId.isEmpty()) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Please enter transaction ID");
            etTransactionId.requestFocus();
            return;
        }

        if (selectedImageUri == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Please upload payment proof");
            return;
        }

        // Show loading with custom dialog
        loadingDialog = LoadingDialog.show(this, "Submitting deposit request...");

        // Prepare multipart request — handle content:// URIs without relying on real file paths (avoids EACCES on scoped storage)
        try {
            RequestBody requestFile;
            String fileName = getFileName(selectedImageUri);

            // Read stream from ContentResolver for content:// URIs
            if ("content".equals(selectedImageUri.getScheme())) {
                java.io.InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(selectedImageUri);
                    java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                    byte[] data = new byte[4096];
                    int nRead;
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    byte[] fileBytes = buffer.toByteArray();

                    String mime = getContentResolver().getType(selectedImageUri);
                    if (mime == null) mime = "image/*";
                    requestFile = RequestBody.create(MediaType.parse(mime), fileBytes);
                } finally {
                    if (inputStream != null) try { inputStream.close(); } catch (Exception ignored) {}
                }
            } else {
                // Fallback to file path for file:// URIs
                String filePath = getRealPathFromURI(selectedImageUri);
                java.io.File file = new java.io.File(filePath);
                requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
                if (fileName == null) fileName = file.getName();
            }

            MultipartBody.Part body = MultipartBody.Part.createFormData("payment_proof", fileName, requestFile);

            RequestBody paymentMethodId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedPaymentMethod.getId()));
            RequestBody amountBody = RequestBody.create(MediaType.parse("text/plain"), amount);
            RequestBody transactionIdBody = RequestBody.create(MediaType.parse("text/plain"), transactionId);

            String token = "Bearer " + preferenceManager.getAuthToken();

            apiService.createDeposit(token, paymentMethodId, amountBody, transactionIdBody, body)
                    .enqueue(new Callback<ApiResponse<Deposit>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Deposit>> call, Response<ApiResponse<Deposit>> response) {
                            if (loadingDialog != null) {
                                loadingDialog.dismiss();
                            }

                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<Deposit> apiResponse = response.body();
                                if (apiResponse.isSuccess()) {
                                    SnackbarUtils.showSuccess(findViewById(android.R.id.content),
                                            apiResponse.getMessage() != null ? apiResponse.getMessage() : "Deposit request submitted successfully");
                                    
                                    // Delay before navigating to show success message
                                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                                        // Navigate to Deposit History
                                        Intent intent = new Intent(DepositActivity.this, DepositsActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }, 2000); // 2 seconds delay
                                } else {
                                    SnackbarUtils.showError(findViewById(android.R.id.content),
                                            apiResponse.getMessage() != null ? apiResponse.getMessage() : "Failed to submit deposit request");
                                }
                            } else {
                                SnackbarUtils.showError(findViewById(android.R.id.content),
                                        "Failed to submit deposit request. Please try again.");
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Deposit>> call, Throwable t) {
                            if (loadingDialog != null) {
                                loadingDialog.dismiss();
                            }
                            SnackbarUtils.showError(findViewById(android.R.id.content),
                                    "Network error: " + t.getMessage());
                        }
                    });

        } catch (Exception e) {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            SnackbarUtils.showError(findViewById(android.R.id.content),
                    "Error: " + e.getMessage());
        }
    }

    /**
     * Permission helpers for runtime storage/media access
     */
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }

    private void checkPermissionAndOpenChooser() {
        if (hasStoragePermission()) {
            openFileChooser();
        } else {
            requestStoragePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                SnackbarUtils.showError(findViewById(android.R.id.content), "Permission denied. Cannot choose payment proof image.");
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return contentUri.getPath();
    }
    
    /**
     * Copy account number to clipboard
     */
    private void copyAccountNumber() {
        if (tvAccountNo != null && selectedPaymentMethod != null) {
            String accountNumber = selectedPaymentMethod.getAccountNo();
            if (accountNumber != null && !accountNumber.trim().isEmpty()) {
                // Copy to clipboard
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Account Number", accountNumber);
                clipboard.setPrimaryClip(clip);
                
                // Show success message
                SnackbarUtils.showSuccess(findViewById(android.R.id.content), 
                    "Account number copied: " + accountNumber);
                
                // Optional: Add haptic feedback on the clickable container
                if (llCopyAccount != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        llCopyAccount.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM);
                    } else {
                        llCopyAccount.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                    }
                }
            } else {
                SnackbarUtils.showError(findViewById(android.R.id.content), 
                    "No account number available to copy");
            }
        }
    }
}
