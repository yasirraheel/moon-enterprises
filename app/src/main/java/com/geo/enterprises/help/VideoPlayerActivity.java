package com.geo.enterprises.help;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.utils.WindowInsetsHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPlayerActivity extends AppCompatActivity {

    private WebView webviewPlayer;
    private ProgressBar progressBar;
    private ImageView ivBack;

    private int videoId;
    private String videoUrl;
    private String embedUrl;
    private String videoTitle;
    private String videoType;
    private ApiService apiService;
    private boolean viewIncremented = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Make fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        initViews();
        getIntentData();
        setupPlayer();
    }

    private void initViews() {
        webviewPlayer = findViewById(R.id.webview_player);
        progressBar = findViewById(R.id.progress_bar);
        ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void getIntentData() {
        Intent intent = getIntent();
        videoId = intent.getIntExtra("video_id", 0);
        videoUrl = intent.getStringExtra("video_url");
        embedUrl = intent.getStringExtra("embed_url");
        videoTitle = intent.getStringExtra("video_title");
        videoType = intent.getStringExtra("video_type");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupPlayer() {
        apiService = ApiClient.getInstance().getApiService();
        
        WebSettings webSettings = webviewPlayer.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webviewPlayer.setWebChromeClient(new WebChromeClient());
        webviewPlayer.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                // Increment view count when video page finishes loading
                if (!viewIncremented && videoId > 0) {
                    incrementVideoView();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Allow YouTube and video URLs to load in WebView
                if (url.contains("youtube.com") || url.contains("youtu.be") || url.contains("embed")) {
                    return false;
                }
                // Open external links in browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        loadVideo();
    }

    private void loadVideo() {
        progressBar.setVisibility(View.VISIBLE);

        // Check if it's a YouTube video - open in YouTube app
        if (isYouTubeVideo()) {
            // Increment view for YouTube videos
            if (!viewIncremented && videoId > 0) {
                incrementVideoView();
            }
            openInYouTubeApp();
            return;
        }

        // Direct video URL - use video tag in WebView
        if (videoUrl != null && !videoUrl.isEmpty()) {
            String html = getDirectVideoHtml(videoUrl);
            webviewPlayer.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            return;
        }

        progressBar.setVisibility(View.GONE);
    }

    private boolean isYouTubeVideo() {
        if (embedUrl != null && (embedUrl.contains("youtube.com") || embedUrl.contains("youtu.be"))) {
            return true;
        }
        if (videoUrl != null && (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be"))) {
            return true;
        }
        if ("youtube".equalsIgnoreCase(videoType)) {
            return true;
        }
        return false;
    }

    private void openInYouTubeApp() {
        String youtubeUrl = null;
        
        // Get the best URL to open
        if (videoUrl != null && !videoUrl.isEmpty()) {
            youtubeUrl = videoUrl;
        } else if (embedUrl != null && !embedUrl.isEmpty()) {
            // Convert embed URL to watch URL
            String videoId = extractYoutubeId(embedUrl);
            if (videoId != null) {
                youtubeUrl = "https://www.youtube.com/watch?v=" + videoId;
            } else {
                youtubeUrl = embedUrl;
            }
        }
        
        if (youtubeUrl != null) {
            try {
                // Try to open in YouTube app
                Intent ytIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                ytIntent.setPackage("com.google.android.youtube");
                startActivity(ytIntent);
            } catch (Exception e) {
                // YouTube app not installed, open in browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(browserIntent);
            }
        }
        
        // Close this activity since we're opening externally
        finish();
    }

    private String getDirectVideoHtml(String videoUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                "<style>" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                "html, body { width: 100%; height: 100%; background: #000; overflow: hidden; }" +
                "video { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); max-width: 100%; max-height: 100%; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<video controls autoplay playsinline>" +
                "<source src=\"" + videoUrl + "\" type=\"video/mp4\">" +
                "Your browser does not support the video tag." +
                "</video>" +
                "</body>" +
                "</html>";
    }

    private String extractYoutubeId(String url) {
        if (url == null) return null;
        
        // Handle youtu.be format
        if (url.contains("youtu.be/")) {
            int startIndex = url.indexOf("youtu.be/") + 9;
            int endIndex = url.indexOf("?", startIndex);
            if (endIndex == -1) endIndex = url.length();
            return url.substring(startIndex, endIndex);
        }
        
        // Handle youtube.com/watch?v= format
        if (url.contains("v=")) {
            int startIndex = url.indexOf("v=") + 2;
            int endIndex = url.indexOf("&", startIndex);
            if (endIndex == -1) endIndex = url.length();
            return url.substring(startIndex, endIndex);
        }
        
        // Handle youtube.com/embed/ format
        if (url.contains("/embed/")) {
            int startIndex = url.indexOf("/embed/") + 7;
            int endIndex = url.indexOf("?", startIndex);
            if (endIndex == -1) endIndex = url.length();
            return url.substring(startIndex, endIndex);
        }
        
        return null;
    }

    private void incrementVideoView() {
        if (apiService == null || videoId <= 0) {
            return;
        }

        viewIncremented = true;

        apiService.incrementVideoView(videoId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // View count incremented successfully
                android.util.Log.d("VideoPlayer", "Video view incremented for video ID: " + videoId);
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Log the error but don't interrupt video playback
                android.util.Log.e("VideoPlayer", "Failed to increment video view: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webviewPlayer.canGoBack()) {
            webviewPlayer.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webviewPlayer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webviewPlayer.onResume();
    }

    @Override
    protected void onDestroy() {
        if (webviewPlayer != null) {
            webviewPlayer.destroy();
        }
        super.onDestroy();
    }
}
