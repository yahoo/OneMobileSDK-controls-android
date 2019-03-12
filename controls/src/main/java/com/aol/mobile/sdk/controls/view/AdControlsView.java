/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aol.mobile.sdk.annotations.PublicApi;
import com.aol.mobile.sdk.controls.AdControls;
import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;

import static android.widget.FrameLayout.LayoutParams.MATCH_PARENT;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderAvailability;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderSeekerMaxValue;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderSeekerProgress;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderText;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderVisibility;

@PublicApi
public class AdControlsView extends RelativeLayout implements AdControls, Themed {
    @NonNull
    private final FrameLayout clickthroughContainer;
    @NonNull
    private final ProgressBar progressView;
    @NonNull
    private final TintableImageButton playButton;
    @NonNull
    private final TintableImageButton pauseButton;
    @NonNull
    private final TintableImageButton clickthroughClose;
    @NonNull
    private final android.widget.Button skipButton;
    @NonNull
    private final SeekBar seekbar;
    @NonNull
    private final TextView timeLeftTextView;
    @NonNull
    private final TextView adTitleTextView;
    @NonNull
    private final Themed[] themedItems;
    @Nullable
    private Listener listener;
    @NonNull
    private final OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (listener == null) return;

            if (view == AdControlsView.this) listener.onAdClicked();
            if (view == playButton) listener.onButtonClick(Button.PLAY);
            if (view == pauseButton) listener.onButtonClick(Button.PAUSE);
            if (view == clickthroughClose) listener.onAdPresented();
            if (view == skipButton) listener.onButtonClick(Button.SKIP);
        }
    };
    @ColorInt
    private int mainColor;
    @ColorInt
    private int accentColor;
    @Nullable
    private String adUrl;
    private boolean isHandleTouchEvent;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return isHandleTouchEvent;
    }

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AdControlsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public AdControlsView(@NonNull Context context) {
        this(context, null);
    }

    public AdControlsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdControlsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttrs(context, attrs);

        inflate(context, R.layout.ad_controls_view, this);

        setClickable(true);

        progressView = findViewById(R.id.ad_progress_bar);
        playButton = findViewById(R.id.ad_play_button);
        pauseButton = findViewById(R.id.ad_pause_button);
        clickthroughContainer = findViewById(R.id.clickthrough_container);
        clickthroughClose = findViewById(R.id.clickthrough_close);
        skipButton = findViewById(R.id.skip_button);
        seekbar = findViewById(R.id.ad_seekbar);
        seekbar.setPadding(0, 0, 0, 0);
        timeLeftTextView = findViewById(R.id.ad_time_left);
        adTitleTextView = findViewById(R.id.ad_title);

        themedItems = new Themed[]{playButton, pauseButton, clickthroughClose};

        updateColors();
        setupListeners();
    }

    private void readAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        mainColor = context.getResources().getColor(R.color.default_main_color);
        accentColor = context.getResources().getColor(R.color.default_ad_accent_color);

        if (attrs == null) return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AdControlsView, 0, 0);

        try {
            mainColor = a.getColor(R.styleable.AdControlsView_mainColor, mainColor);
            accentColor = a.getInteger(R.styleable.AdControlsView_accentColor, accentColor);
        } finally {
            a.recycle();
        }
    }

    protected void updateColors() {
        progressView.getIndeterminateDrawable().setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);

        Drawable drawable = ((LayerDrawable) seekbar.getProgressDrawable())
                .findDrawableByLayerId(android.R.id.progress).mutate();
        drawable.setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);

        drawable = ((LayerDrawable) seekbar.getProgressDrawable())
                .findDrawableByLayerId(android.R.id.secondaryProgress).mutate();
        drawable.setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);

        drawable.setAlpha(120);

        Drawable thumb = seekbar.getThumb();
        if (thumb != null) {
            thumb.setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = ((LayerDrawable) seekbar.getProgressDrawable())
                    .findDrawableByLayerId(android.R.id.background).mutate();
            drawable.setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);
        }

        for (Themed item : themedItems) {
            item.setAccentColor(accentColor);
            item.setMainColor(mainColor);
        }

        timeLeftTextView.setTextColor(accentColor);
        adTitleTextView.setTextColor(mainColor);

        skipButton.setTextColor(mainColor);
        skipButton.getBackground().setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);
    }

    private void setupListeners() {
        playButton.setOnClickListener(clickListener);
        pauseButton.setOnClickListener(clickListener);
        clickthroughClose.setOnClickListener(clickListener);
        skipButton.setOnClickListener(clickListener);
        skipButton.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.setAlpha(0.75F);
                    break;
                case MotionEvent.ACTION_UP:
                    view.setAlpha(1);
                    break;
            }
            return false;
        });
        seekbar.setOnTouchListener((v, event) -> true);
        setOnClickListener(clickListener);
    }

    private void renderClickThrough(@Nullable String adUrl, boolean embedClickThroughUrl) {
        if ((adUrl != null && !adUrl.equals(this.adUrl)) || (adUrl == null && this.adUrl != null)) {
            this.adUrl = adUrl;

            if (embedClickThroughUrl) {
                renderEmbeddedClickthrough(adUrl);
            } else {
                renderClickthrough(adUrl, clickthroughClose.getVisibility() == VISIBLE);
            }
        }
    }

    void renderEmbeddedClickthrough(@Nullable String url) {
        if (listener == null) return;

        clickthroughContainer.removeAllViews();

        if (url == null) {
            clickthroughContainer.setVisibility(GONE);
        } else {
            WebView webView = new WebView(getContext());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            clickthroughContainer.addView(webView, MATCH_PARENT, MATCH_PARENT);
            clickthroughContainer.setVisibility(VISIBLE);
            webView.loadUrl(url);
        }
    }

    void renderClickthrough(@Nullable String url, boolean isCloseVisible) {
        if (listener == null || url == null) return;

        listener.onAdPresented();
        Context context = getContext();
        Intent intent = new Intent(context, TargetUrlActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(TargetUrlActivity.KEY_TARGET_URL, url)
                .putExtra(TargetUrlActivity.KEY_SHOW_CLOSE, isCloseVisible);
        context.startActivity(intent);
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    @Override
    public void render(@NonNull ViewModel vm) {
        Resources resources = getResources();
        renderVisibility(vm.isProgressViewVisible, progressView);
        renderVisibility(vm.isPlayButtonVisible, playButton);
        renderVisibility(vm.isPauseButtonVisible, pauseButton);
        renderVisibility(vm.isCloseButtonVisible, clickthroughClose);
        renderVisibility(vm.isSkipButtonVisible, skipButton);
        renderAvailability(vm.isSkipButtonEnabled, skipButton);
        String skipText = String.format(resources.getString(R.string.skip),
                "",
                "");
        int px32dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, resources.getDisplayMetrics());
        skipButton.setPadding(px32dp, 0, 0, 0);
        skipButton.setBackground(resources.getDrawable(R.drawable.skip_background_narrow));
        if (vm.skipCountdown != null) {
            int px22dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, resources.getDisplayMetrics());
            skipButton.setPadding(px22dp, 0, 0, 0);
            skipButton.setBackground(resources.getDrawable(R.drawable.skip_background_wide));
            skipText = String.format(resources.getString(R.string.skip),
                    resources.getString(R.string.in),
                    " " + vm.skipCountdown);
            skipButton.setContentDescription(String.format(resources.getString(R.string.disabled_skip_advertisement),
                    resources.getString(R.string.in),
                    " " + vm.skipCountdown));
        } else {
            skipButton.setContentDescription(resources.getString(R.string.skip_advertisement));
        }
        renderText(skipText, skipButton);
        renderVisibility(vm.isSeekbarVisible, seekbar);
        renderVisibility(vm.isTimeLeftTextVisible, timeLeftTextView);
        renderSeekerMaxValue(vm.seekerMaxValue, seekbar);
        renderSeekerProgress(vm.seekerProgress, seekbar);
        renderText(vm.timeLeftText, timeLeftTextView);
        renderClickThrough(vm.adUrl, vm.embedClickThroughUrl);
        isHandleTouchEvent = !vm.isVpaid;
    }

    @Override
    public void setMainColor(@ColorInt int color) {
        mainColor = color;
        updateColors();
    }

    @Override
    public void setAccentColor(@ColorInt int color) {
        accentColor = color;
        updateColors();
    }
}
