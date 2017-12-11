/*
 * Copyright (c) 2016 One by Aol : Publishers. All rights reserved.
 */

package com.aol.mobile.sdk.controls.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public final class TargetUrlActivity extends Activity {
    @NonNull
    public final static String KEY_TARGET_URL = "KEY_TARGET_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(KEY_TARGET_URL);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
