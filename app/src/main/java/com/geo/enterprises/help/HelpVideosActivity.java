package com.geo.enterprises.help;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.dashboard.ShimmerAdapter;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.HelpVideo;
import com.geo.enterprises.utils.SnackbarUtils;
import com.geo.enterprises.utils.WindowInsetsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpVideosActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etSearch;
    private RecyclerView rvVideos;
    private RecyclerView rvShimmerLoading;
    private View emptyView;
    private SwipeRefreshLayout swipeRefresh;
    
    private HelpVideosAdapter adapter;
    private List<HelpVideo> videosList;
    private List<HelpVideo> filteredVideosList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_videos);
        
        // Initialize
        apiService = ApiClient.getInstance().getApiService();
        videosList = new ArrayList<>();
        filteredVideosList = new ArrayList<>();
        
        // Initialize views
        initializeViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup click listeners
        setupClickListeners();
        
        // Setup search listener
        setupSearchListener();
        
        // Load videos
        loadVideos();
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        etSearch = findViewById(R.id.et_search);
        rvVideos = findViewById(R.id.rv_videos);
        rvShimmerLoading = findViewById(R.id.rv_shimmer_loading);
        emptyView = findViewById(R.id.empty_view);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        
        // Apply status bar padding
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
        }
        
        // Setup swipe refresh
        setupSwipeRefresh();
    }
    
    private void setupSwipeRefresh() {
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(
                R.color.primary_color,
                R.color.primary_dark,
                R.color.primary_light
            );
            swipeRefresh.setOnRefreshListener(() -> {
                loadVideos();
            });
        }
    }
    
    private void setupRecyclerView() {
        adapter = new HelpVideosAdapter(filteredVideosList, video -> {
            // Open video player
            openVideoPlayer(video);
        });
        rvVideos.setLayoutManager(new LinearLayoutManager(this));
        rvVideos.setAdapter(adapter);
        
        // Setup shimmer recyclerview
        if (rvShimmerLoading != null) {
            rvShimmerLoading.setLayoutManager(new LinearLayoutManager(this));
        }
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupSearchListener() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not used
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterVideos(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Not used
                }
            });
        }
    }

    private void filterVideos(String query) {
        filteredVideosList.clear();
        
        String searchQuery = query.toLowerCase(Locale.getDefault()).trim();
        
        if (searchQuery.isEmpty()) {
            // Show all videos if search is empty
            filteredVideosList.addAll(videosList);
        } else {
            // Filter videos by title (case-insensitive)
            for (HelpVideo video : videosList) {
                if (video.getTitle() != null && 
                    video.getTitle().toLowerCase(Locale.getDefault()).contains(searchQuery)) {
                    filteredVideosList.add(video);
                }
            }
        }
        
        // Update adapter and show empty state if no results
        adapter.notifyDataSetChanged();
        
        if (filteredVideosList.isEmpty() && !searchQuery.isEmpty()) {
            rvVideos.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            updateEmptyViewText("No videos found");
        } else if (filteredVideosList.isEmpty()) {
            rvVideos.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            updateEmptyViewText("No videos available");
        } else {
            rvVideos.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void updateEmptyViewText(String text) {
        if (emptyView != null) {
            // Find the TextView in empty view (usually the last one with text)
            for (int i = 0; i < ((android.widget.LinearLayout) emptyView).getChildCount(); i++) {
                View child = ((android.widget.LinearLayout) emptyView).getChildAt(i);
                if (child instanceof android.widget.TextView) {
                    android.widget.TextView tv = (android.widget.TextView) child;
                    String currentText = tv.getText().toString();
                    if (currentText.contains("No")) {
                        tv.setText(text);
                        break;
                    }
                }
            }
        }
    }
    
    private void loadVideos() {
        if (apiService == null) {
            SnackbarUtils.showError(findViewById(android.R.id.content), "API service not available");
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        showLoading(true);
        
        apiService.getHelpVideos().enqueue(new Callback<com.geo.enterprises.models.HelpVideosResponse>() {
            @Override
            public void onResponse(Call<com.geo.enterprises.models.HelpVideosResponse> call, Response<com.geo.enterprises.models.HelpVideosResponse> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<HelpVideo> videos = response.body().getVideos();
                    
                    if (videos != null && !videos.isEmpty()) {
                        videosList.clear();
                        videosList.addAll(videos);
                        
                        // Clear search and reset filtered list
                        if (etSearch != null) {
                            etSearch.setText("");
                        }
                        filteredVideosList.clear();
                        filteredVideosList.addAll(videosList);
                        
                        adapter.notifyDataSetChanged();
                        
                        rvVideos.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                        
                        android.util.Log.d("HelpVideos", "Loaded " + videos.size() + " videos");
                    } else {
                        android.util.Log.w("HelpVideos", "Empty video list");
                        showEmptyView();
                    }
                } else {
                    String errorMsg = "Failed to load videos";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += ": " + response.errorBody().string();
                            android.util.Log.e("HelpVideos", errorMsg);
                        } catch (Exception e) {
                            android.util.Log.e("HelpVideos", "Error reading response: " + e.getMessage());
                        }
                    }
                    SnackbarUtils.showError(findViewById(android.R.id.content), "Failed to load videos");
                    showEmptyView();
                }
            }
            
            @Override
            public void onFailure(Call<com.geo.enterprises.models.HelpVideosResponse> call, Throwable t) {
                showLoading(false);
                android.util.Log.e("HelpVideos", "Network error: " + t.getMessage(), t);
                SnackbarUtils.showNetworkError(findViewById(android.R.id.content));
                showEmptyView();
            }
        });
    }
    
    private void showLoading(boolean show) {
        if (show) {
            if (rvShimmerLoading != null) {
                ShimmerAdapter shimmerAdapter = new ShimmerAdapter(5);
                rvShimmerLoading.setAdapter(shimmerAdapter);
                rvShimmerLoading.setVisibility(View.VISIBLE);
            }
            if (rvVideos != null) {
                rvVideos.setVisibility(View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        } else {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            if (rvShimmerLoading != null) {
                rvShimmerLoading.setVisibility(View.GONE);
            }
        }
    }
    
    private void showEmptyView() {
        rvVideos.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }
    
    private void openVideoPlayer(HelpVideo video) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra("video_id", video.getId());
        intent.putExtra("video_title", video.getTitle());
        intent.putExtra("video_url", video.getVideoUrl());
        intent.putExtra("embed_url", video.getEmbedUrl());
        intent.putExtra("video_type", video.getVideoType());
        startActivity(intent);
    }
}
