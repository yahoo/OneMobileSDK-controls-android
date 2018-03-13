/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aol.mobile.sdk.annotations.PrivateApi;
import com.aol.mobile.sdk.controls.R;

@PrivateApi
public final class TargetUrlActivity extends Activity {
    @NonNull
    public final static String KEY_TARGET_URL = "KEY_TARGET_URL";
    public final static String KEY_SHOW_CLOSE = "KEY_SHOW_CLOSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.target_url_activity);

        String url = getIntent().getStringExtra(KEY_TARGET_URL);
        boolean isCloseVisible = getIntent().getBooleanExtra(KEY_SHOW_CLOSE, false);

        View closeBtn = findViewById(R.id.clickthrough_close);
        closeBtn.setVisibility(isCloseVisible ? View.VISIBLE : View.GONE);
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
