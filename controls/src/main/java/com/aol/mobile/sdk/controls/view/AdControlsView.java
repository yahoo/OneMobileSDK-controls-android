/*
 * Copyright (c) 2017. Oath.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aol.mobile.sdk.controls.AdControls;
import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;

import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderSeekerMaxValue;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderSeekerProgress;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderText;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderVisibility;

public final class AdControlsView extends RelativeLayout implements AdControls, Themed {
    @NonNull
    private final ProgressBar progressView;
    @NonNull
    private final TintableImageButton playButton;
    @NonNull
    private final TintableImageButton pauseButton;
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

            if (view == playButton) listener.onButtonClick(Button.PLAY);
            if (view == pauseButton) listener.onButtonClick(Button.PAUSE);
        }
    };
    @ColorInt
    private int mainColor;
    @ColorInt
    private int accentColor;
    @Nullable
    private String adUrl;

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
        seekbar = findViewById(R.id.ad_seekbar);
        timeLeftTextView = findViewById(R.id.ad_time_left);
        adTitleTextView = findViewById(R.id.ad_title);

        themedItems = new Themed[]{
                playButton,
                pauseButton};

        seekbar.setPadding(0, 0, 0, 0);

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
    }

    private void setupListeners() {
        playButton.setOnClickListener(clickListener);
        pauseButton.setOnClickListener(clickListener);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adUrl != null) {
                    Context context = getContext();

                    Intent intent = new Intent(context, TargetUrlActivity.class);
                    intent.putExtra(TargetUrlActivity.KEY_TARGET_URL, adUrl);
                    context.startActivity(intent);
                    if (listener != null) listener.onAdClicked();
                }
            }
        });
        seekbar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    @Override
    public void render(@NonNull ViewModel vm) {
        renderVisibility(vm.isProgressViewVisible, progressView);
        renderVisibility(vm.isPlayButtonVisible, playButton);
        renderVisibility(vm.isPauseButtonVisible, pauseButton);
        renderVisibility(vm.isAdTimeViewVisible, timeLeftTextView);
        renderVisibility(vm.isAdTimeViewVisible, seekbar);
        renderSeekerMaxValue(vm.seekerMaxValue, seekbar);
        renderSeekerProgress(vm.seekerProgress, seekbar);
        renderText(vm.timeLeftText, timeLeftTextView);
        adUrl = vm.adUrl;
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
