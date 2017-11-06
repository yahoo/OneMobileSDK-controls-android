/*
 * Copyright (c) 2016 One by Aol : Publishers. All rights reserved.
 */

package com.aol.mobile.sdk.controls.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aol.mobile.sdk.controls.AdControls;
import com.aol.mobile.sdk.controls.AdControlsButton;
import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;
import com.aol.mobile.sdk.controls.viewmodel.AdControlsVM;

import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderSeekerMaxValue;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderSeekerProgress;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderText;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.renderVisibility;

public final class AdControlsView extends RelativeLayout implements AdControls, Themed {
    @NonNull
    private final TintableImageButton bufferingView;
    @NonNull
    private final TintableImageButton playButton;
    @NonNull
    private final TintableImageButton pauseButton;
    @NonNull
    private final TintableSeekbar seekbar;
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

            if (view == playButton) listener.onButtonClick(AdControlsButton.PLAY);
            if (view == pauseButton) listener.onButtonClick(AdControlsButton.PAUSE);
        }
    };
    @ColorInt
    private int mainColor;
    @ColorInt
    private int accentColor;

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

        bufferingView = findViewById(R.id.ad_progress_bar);
        Animator animator = AnimatorInflater.loadAnimator(getContext(), R.animator.buffering_animator);
        animator.setTarget(bufferingView);
        animator.start();

        playButton = findViewById(R.id.ad_play_button);
        pauseButton = findViewById(R.id.ad_pause_button);
        seekbar = findViewById(R.id.ad_seekbar);
        timeLeftTextView = findViewById(R.id.ad_time_left);
        adTitleTextView = findViewById(R.id.ad_title);

        themedItems = new Themed[]{playButton, pauseButton, seekbar};

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
        bufferingView.setAccentColor(mainColor);
        bufferingView.setMainColor(mainColor);

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
                if (listener != null) listener.onTap();
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

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void render(@NonNull AdControlsVM adControlsVM) {
        renderVisibility(adControlsVM.isProgressViewVisible, bufferingView);
        renderVisibility(adControlsVM.isPlayButtonVisible, playButton);
        renderVisibility(adControlsVM.isPauseButtonVisible, pauseButton);
        renderVisibility(adControlsVM.isAdTimeViewVisible, timeLeftTextView);
        renderVisibility(adControlsVM.isAdTimeViewVisible, seekbar);
        renderSeekerMaxValue(adControlsVM.seekerMaxValue, seekbar);
        renderSeekerProgress(adControlsVM.seekerProgress, seekbar);
        renderText(adControlsVM.timeLeftText, timeLeftTextView);
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
