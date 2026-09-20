package com.geo.enterprises.api;

import java.util.List;
import java.util.Map;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.ApkVersionResponse;
import com.geo.enterprises.models.AppSettings;
import com.geo.enterprises.models.Deposit;
import com.geo.enterprises.models.GameCategory;
import com.geo.enterprises.models.LoginRequest;
import com.geo.enterprises.models.LoginResponse;
import com.geo.enterprises.models.Order;
import com.geo.enterprises.models.PaymentMethodsResponse;
import com.geo.enterprises.models.RegisterRequest;
import com.geo.enterprises.models.SubcategoryResponse;
import com.geo.enterprises.models.UpdateProfileRequest;
import com.geo.enterprises.models.User;
import com.geo.enterprises.models.Withdrawal;
import com.geo.enterprises.notifications.NotificationItem;
import com.geo.enterprises.notifications.NotificationCount;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    
    @GET("settings")
    Call<ApiResponse<AppSettings>> getSettings();
    
    @GET("notifications")
    Call<ApiResponse<List<NotificationItem>>> getNotifications(@Header("Authorization") String token);
    
    @GET("notifications/count")
    Call<ApiResponse<NotificationCount>> getNotificationCount(@Header("Authorization") String token);
    
    @POST("notifications/{id}/mark-read")
    Call<ApiResponse<Void>> markNotificationAsRead(@Header("Authorization") String token, @retrofit2.http.Path("id") int notificationId);

    @POST("notifications/mark-all-read")
    Call<ApiResponse<Void>> markAllNotificationsAsRead(@Header("Authorization") String token);

    // FCM Token Management
    @POST("fcm/register")
    Call<ApiResponse<Void>> registerFcmToken(
        @Header("Authorization") String token,
        @Body Map<String, String> fcmData
    );

    @POST("fcm/unregister")
    Call<ApiResponse<Void>> unregisterFcmToken(
        @Header("Authorization") String token,
        @Body Map<String, String> fcmData
    );

    @POST("login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);
    
    @POST("register")
    Call<ApiResponse<LoginResponse>> register(@Body RegisterRequest registerRequest);
    
    @POST("logout")
    Call<ApiResponse<Void>> logout(@Header("Authorization") String token);
    
    @GET("profile")
    Call<ApiResponse<User>> getProfile(@Header("Authorization") String token);
    
    @GET("user/balance")
    Call<ApiResponse<Double>> getUserBalance(@Header("Authorization") String token);
    
    @POST("profile")
    Call<ApiResponse<Void>> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);

    @GET("game-categories")
    Call<ApiResponse<List<GameCategory>>> getGameCategories();
    
    @GET("subcategories")
    Call<SubcategoryResponse> getSubcategories(@Query("category_id") int categoryId);
    
    @POST("orders/create")
    Call<ApiResponse<Order>> createOrder(@Header("Authorization") String token, @Body Map<String, String> orderData);

    @POST("orders/create-bulk")
    Call<ApiResponse<List<com.geo.enterprises.models.BulkOrderResult>>> createBulkOrders(@Header("Authorization") String token, @Body com.geo.enterprises.models.BulkOrderRequest request);
    
    @GET("orders")
    Call<com.geo.enterprises.models.OrdersResponse> getUserOrders(
        @Header("Authorization") String token,
        @Query("per_page") int perPage,
        @Query("page") int page,
        @Query("status") String status
    );
    
    @GET("transactions")
    Call<com.geo.enterprises.models.TransactionResponse> getUserTransactions(
        @Header("Authorization") String token,
        @Query("per_page") Integer perPage,
        @Query("page") Integer page,
        @Query("type") String type,
        @Query("transaction_type") String transactionType,
        @Query("date_from") String dateFrom,
        @Query("date_to") String dateTo
    );
    
    @GET("payment-methods")
    Call<PaymentMethodsResponse> getPaymentMethods();
    
    @Multipart
    @POST("deposits/create")
    Call<ApiResponse<Deposit>> createDeposit(
        @Header("Authorization") String token,
        @Part("payment_method_id") RequestBody paymentMethodId,
        @Part("amount") RequestBody amount,
        @Part("transaction_id") RequestBody transactionId,
        @Part MultipartBody.Part paymentProof
    );
    
    
    @GET("deposits")
    Call<com.geo.enterprises.models.DepositsResponse> getUserDeposits(
        @Header("Authorization") String token,
        @Query("status") String status,
        @Query("per_page") Integer perPage,
        @Query("page") Integer page
    );

    @POST("dealership/apply")
    Call<Void> applyForDealership(@Header("Authorization") String token);

    @GET("dealership/status")
    Call<com.geo.enterprises.models.DealershipStatusResponse> getDealershipStatus(@Header("Authorization") String token);

    @POST("user/reset-commission")
    Call<com.geo.enterprises.models.ResetCommissionResponse> resetCommission(@Header("Authorization") String token);

    // Withdrawal endpoints
    @GET("withdrawal-methods")
    Call<com.geo.enterprises.models.WithdrawalMethodsResponse> getWithdrawalMethods();
    
    @POST("withdrawals/create")
    Call<ApiResponse<Withdrawal>> createWithdrawal(
        @Header("Authorization") String token,
        @Body Map<String, String> withdrawalData
    );

    @GET("withdrawals")
    Call<com.geo.enterprises.models.WithdrawalsResponse> getUserWithdrawals(
        @Header("Authorization") String token,
        @Query("status") String status,
        @Query("per_page") Integer perPage
    );

    // Paid Services endpoint
    @GET("paid-services")
    Call<com.geo.enterprises.models.PaidServicesResponse> getPaidServices(@Header("Authorization") String token);
    
    @POST("paid-service/purchase")
    Call<com.geo.enterprises.models.PurchaseResponse> purchasePaidService(
        @Header("Authorization") String token,
        @Body Map<String, Object> purchaseData
    );
    
    // APK Version Check
    @GET("apk-version")
    Call<ApkVersionResponse> getApkVersion();
    
    // Help Videos
    @GET("help-videos")
    Call<com.geo.enterprises.models.HelpVideosResponse> getHelpVideos();
    
    @POST("help-videos/{id}/increment-view")
    Call<ApiResponse<Void>> incrementVideoView(@retrofit2.http.Path("id") int videoId);

    // Trial Status Check
    @GET("is_trial")
    Call<com.geo.enterprises.models.TrialStatusResponse> getIsTrial();
}

