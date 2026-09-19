package com.geo.enterprises.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geo.enterprises.R;

public class LoadingDialog extends Dialog {

    private String loadingText;
    private ProgressBar progressBar;
    private TextView tvLoadingText;

    public LoadingDialog(Context context) {
        super(context);
        this.loadingText = "Loading...";
    }

    public LoadingDialog(Context context, String loadingText) {
        super(context);
        this.loadingText = loadingText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        
        // Make dialog background transparent
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        // Make dialog not cancelable
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        
        initializeViews();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progress_loading);
        tvLoadingText = findViewById(R.id.tv_loading_text);
        
        if (tvLoadingText != null) {
            tvLoadingText.setText(loadingText);
        }
    }

    public void setLoadingText(String text) {
        this.loadingText = text;
        if (tvLoadingText != null) {
            tvLoadingText.setText(text);
        }
    }

    public static LoadingDialog show(Context context) {
        return show(context, "Loading...");
    }

    public static LoadingDialog show(Context context, String loadingText) {
        LoadingDialog dialog = new LoadingDialog(context, loadingText);
        dialog.show();
        return dialog;
    }
}
