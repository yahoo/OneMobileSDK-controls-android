/*
 * Copyright 2019, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aol.mobile.sdk.controls.ContentControls;
import com.aol.mobile.sdk.controls.ImageLoader;
import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;
import com.aol.mobile.sdk.controls.utils.AndroidHandlerTimer;
import com.aol.mobile.sdk.controls.utils.TracksChooserAdapter;
import com.aol.mobile.sdk.controls.utils.ViewUtils;

import java.util.LinkedList;

import static com.aol.mobile.sdk.controls.utils.ViewUtils.findView;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.isVisible;

public class AbstractContentControlsView extends RelativeLayout implements ContentControls, Themed {
    @NonNull
    private final RelativeLayout controlsContainer;
    @NonNull
    private final FrameLayout compassContainer;
    @NonNull
    private final ProgressBar progressView;
    @NonNull
    private final TextView titleView;
    @NonNull
    private final TextView advertisementButton;
    @NonNull
    private final VisibilityWrapper<TintableImageButton> replayButton;
    @NonNull
    private final TintableImageButton trackChooserButton;
    @NonNull
    private final TintableImageButton backwardSeekButton;
    @NonNull
    private final TintableImageButton forwardSeekButton;
    @NonNull
    private final LinearLayout liveIndicatorLayout;
    @NonNull
    private final TintableImageButton playNextButton;
    @NonNull
    private final TintableImageButton playPreviousButton;
    @NonNull
    private final ImageView thumbnailView;
    @NonNull
    private final ImageView compassView;
    @NonNull
    private final SidePanel sidePanel;
    @NonNull
    private final RelativeLayout seekerContainer;
    @NonNull
    private final AdsSeekBar seekbar;
    @NonNull
    private final TextView currentTimeView;
    @NonNull
    private final TextView durationView;
    @NonNull
    private final AndroidHandlerTimer timer = new AndroidHandlerTimer(new Handler());
    @NonNull
    private final VisibilityModule visibilityModule;
    @NonNull
    private final Themed[] themedItems;
    @NonNull
    private final TracksChooserAdapter adapter = new TracksChooserAdapter();
    @NonNull
    private final LinkedList<ViewModel.TrackOptionVM> audioTracks = new LinkedList<>();
    private final LinkedList<ViewModel.TrackOptionVM> ccTracks = new LinkedList<>();
    @Nullable
    protected Listener listener;
    @NonNull
    private final SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
            if (listener == null) return;

            if (fromUser) {
                visibilityModule.prolong();
            }
            int max = seekBar.getMax();
            if (fromUser) listener.onSeekTo(max == 0 ? 0f : progress / (float) max);


            int thumbWidth = getResources().getDimensionPixelSize(R.dimen.thumb_size);
            int val = max == 0 ? 0 : progress * (seekBar.getWidth() - thumbWidth) / max;
            if (val < (currentTimeView.getWidth() - thumbWidth) / 2) {
                val = 0;
            } else if (val > seekBar.getWidth() - (currentTimeView.getWidth() + thumbWidth) / 2) {
                val = seekBar.getWidth() - currentTimeView.getWidth();
            } else {
                val = val - (currentTimeView.getWidth() - thumbWidth) / 2;
            }

            currentTimeView.setX(val);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (listener == null) return;

            listener.onSeekStarted();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (listener == null) return;

            listener.onSeekStopped();
        }
    };
    @NonNull
    private final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (listener == null) return false;
            visibilityModule.tap();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (listener == null) return false;
            listener.onScroll(distanceX, distanceY);
            return true;
        }
    });
    @ColorInt
    protected int mainColor;
    @NonNull
    /*package private*/ VisibilityWrapper<TintableImageButton> playButton;
    @NonNull
    /*package private*/ VisibilityWrapper<TintableImageButton> pauseButton;
    @Nullable
    private View focusedView;
    @Nullable
    private ValueAnimator animator;
    @Nullable
    private String advertisementClickUrl;
    private boolean shouldHideControls = true;
    @Nullable
    private Dialog dialog;
    @NonNull
    private final OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (listener == null) return;

            visibilityModule.prolong();
            if (view == playButton.view) {
                listener.onButtonClick(Button.PLAY);
                pauseButton.view.requestFocus();
            }
            if (view == pauseButton.view) {
                listener.onButtonClick(Button.PAUSE);
                playButton.view.requestFocus();
            }
            if (view == replayButton.view) listener.onButtonClick(Button.REPLAY);
            if (view == playNextButton) listener.onButtonClick(Button.NEXT);
            if (view == playPreviousButton) listener.onButtonClick(Button.PREVIOUS);
            if (view == forwardSeekButton) listener.onButtonClick(Button.SEEK_FORWARD);
            if (view == backwardSeekButton) listener.onButtonClick(Button.SEEK_BACKWARD);
            if (view == compassView) listener.onButtonClick(Button.COMPASS);
            if (view == advertisementButton) listener.onBrandedContentAdClicked();

            if (view == trackChooserButton) {
                adapter.updateData(getContext(), audioTracks, ccTracks);
                dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(dialog -> AbstractContentControlsView.this.dialog = null);
                ListView listView = new ListView(getContext());
                listView.setDivider(null);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    adapter.select(position);

                    TracksChooserAdapter.Item item = adapter.getItem(position);

                    switch (item.type) {
                        case CC:
                            listener.onCcTrackSelected(item.index);
                            break;

                        case AUDIO:
                            listener.onAudioTrackSelected(item.index);
                            break;

                        case CLOSE:
                            dialog.dismiss();
                            break;
                    }
                });
                dialog.setContentView(listView);
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    window.getAttributes().windowAnimations = R.style.TracksDialogAnimation;
                    window.getAttributes().gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                dialog.show();
            }
        }
    };
    @Nullable
    private String thumbUrl;
    @Nullable
    private ImageLoader imageLoader;
    private boolean isPlaying = false;
    private double longitude;
    @ColorInt
    private int accentColor;
    @ColorInt
    private int liveDotColor;
    @ColorInt
    private int durationColor;
    @ColorInt
    private int titleColor;
    @ColorInt
    private int currentTimeColor;
    @Nullable
    private AccessibilityManager accessibilityManager;

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AbstractContentControlsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public AbstractContentControlsView(@NonNull Context context) {
        this(context, null);
    }

    public AbstractContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttrs(context, attrs);

        inflate(getContext(), R.layout.content_controls_view, this);
        accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);

        controlsContainer = findView(this, R.id.controls_container);
        progressView = findView(this, R.id.progressbar);
        playButton = new VisibilityWrapper<>(findView(this, R.id.play_button));
        pauseButton = new VisibilityWrapper<>(findView(this, R.id.pause_button));
        replayButton = new VisibilityWrapper<>(findView(this, R.id.replay_button));
        playNextButton = findView(this, R.id.next_button);
        playPreviousButton = findView(this, R.id.prev_button);
        trackChooserButton = findView(this, R.id.tracks_button);
        forwardSeekButton = findView(this, R.id.forward_seek_button);
        backwardSeekButton = findView(this, R.id.backward_seek_button);
        liveIndicatorLayout = findView(this, R.id.live_indicator);
        seekerContainer = findView(this, R.id.seekbar_container);
        seekbar = findView(this, R.id.seekbar);
        titleView = findView(this, R.id.title_view);
        advertisementButton = findView(this, R.id.advertisement_button);
        compassContainer = findView(this, R.id.compass_container);
        compassView = findView(this, R.id.compass_view);
        thumbnailView = findView(this, R.id.thumbnail);
        sidePanel = findView(this, R.id.side_panel);
        durationView = findView(this, R.id.duration);
        currentTimeView = findView(this, R.id.current_time);

        themedItems = new Themed[]{
                playButton.view,
                pauseButton.view,
                replayButton.view,
                trackChooserButton,
                backwardSeekButton,
                forwardSeekButton,
                playPreviousButton,
                playNextButton
        };

        ViewUtils.renderAvailability(false, playNextButton);
        ViewUtils.renderAvailability(false, playPreviousButton);

        seekbar.setPadding(0, 0, 0, 0);
        seekbar.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                seekbarListener.onProgressChanged(seekbar, seekbar.getProgress(), false));

        setupListeners();
        initFocusIssue();

        visibilityModule = new VisibilityModule(this);
    }

    protected void updateColors() {
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

        TextView liveText = (TextView) liveIndicatorLayout.getChildAt(1);
        liveText.setTextColor(mainColor);

        progressView.getIndeterminateDrawable().setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);
    }

    @SuppressWarnings("deprecation")
    private void readAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        mainColor = context.getResources().getColor(R.color.default_main_color);
        accentColor = context.getResources().getColor(R.color.default_accent_color);
        liveDotColor = Color.RED;
        durationColor = mainColor;
        titleColor = mainColor;
        currentTimeColor = accentColor;

        if (attrs == null) return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ContentControlsView, 0, 0);

        try {
            mainColor = a.getColor(R.styleable.ContentControlsView_mainColor, mainColor);
            accentColor = a.getInteger(R.styleable.ContentControlsView_accentColor, accentColor);
        } finally {
            a.recycle();
        }
    }

    private void setupListeners() {
        playButton.setOnClickListener(clickListener);
        pauseButton.setOnClickListener(clickListener);
        replayButton.setOnClickListener(clickListener);
        playNextButton.setOnClickListener(clickListener);
        playPreviousButton.setOnClickListener(clickListener);
        forwardSeekButton.setOnClickListener(clickListener);
        backwardSeekButton.setOnClickListener(clickListener);
        compassView.setOnClickListener(clickListener);
        trackChooserButton.setOnClickListener(clickListener);
        advertisementButton.setOnClickListener(clickListener);
        seekbar.setOnSeekBarChangeListener(seekbarListener);
    }

    @Override
    public void setListener(final @Nullable Listener listener) {
        this.listener = listener;
    }

    @Override
    public void render(@NonNull ViewModel vm) {
        renderControlsVisibility(vm.isStreamPlaying);
        ViewUtils.renderVisibility(vm.isLoading, progressView);
        ViewUtils.renderVisibility(vm.isPlayButtonVisible, playButton.view);
        ViewUtils.renderVisibility(vm.isPauseButtonVisible, pauseButton.view);
        ViewUtils.renderVisibility(vm.isReplayButtonVisible, replayButton.view);
        ViewUtils.renderVisibility(vm.isNextButtonVisible, playNextButton);
        ViewUtils.renderVisibility(vm.isPrevButtonVisible, playPreviousButton);
        ViewUtils.renderVisibility(vm.isSeekForwardButtonVisible, forwardSeekButton);
        ViewUtils.renderVisibility(vm.isSeekBackButtonVisible, backwardSeekButton);
        ViewUtils.renderVisibility(vm.isSeekerVisible, seekerContainer);
        ViewUtils.renderVisibility(vm.isCompassViewVisible, compassContainer);
        ViewUtils.renderVisibility(vm.isTitleVisible, titleView);
        ViewUtils.renderVisibility(vm.isAdvertisementButtonVisible, advertisementButton);
        ViewUtils.renderVisibility(vm.isThumbnailImageVisible, thumbnailView);
        ViewUtils.renderVisibility(vm.isTrackChooserButtonVisible, trackChooserButton);

        ViewUtils.renderAvailability(vm.isNextButtonEnabled, playNextButton);
        ViewUtils.renderAvailability(vm.isPrevButtonEnabled, playPreviousButton);
        ViewUtils.renderAvailability(vm.isTrackChooserButtonEnabled, trackChooserButton);

        ViewUtils.renderText(vm.titleText, titleView);
        ViewUtils.renderText(vm.advertisementText, advertisementButton);
        ViewUtils.renderText(vm.seekerCurrentTimeText, currentTimeView);
        ViewUtils.renderText(vm.seekerDurationText, durationView);

        ViewUtils.renderSeekerMaxValue(vm.seekerMaxValue, seekbar);
        ViewUtils.renderSeekerProgress(vm.seekerProgress, seekbar);
        ViewUtils.renderSeekerBufferProgress(vm.seekerBufferedProgress, seekbar);

        renderThumbnail(vm.thumbnailImageUrl);
        renderCompassDirection(vm.compassLongitude);
        renderAudioAndCcList(vm.audioTracks, vm.ccTracks);

        liveIndicatorLayout.setVisibility(vm.isLiveIndicatorVisible ? VISIBLE : GONE);

        Drawable liveDot = liveIndicatorLayout.getChildAt(0).getBackground();
        if (vm.isOnLiveEdge) {
            liveDot.setColorFilter(liveDotColor, PorterDuff.Mode.MULTIPLY);
        } else {
            liveDot.setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);
        }
        durationView.setTextColor(durationColor);
        titleView.setTextColor(titleColor);
        currentTimeView.setTextColor(currentTimeColor);

        isPlaying = vm.isStreamPlaying;
        thumbUrl = vm.thumbnailImageUrl;
        longitude = vm.compassLongitude;
        if (vm.isCasting || (accessibilityManager != null && accessibilityManager.isEnabled())) {
            if (shouldHideControls) {
                show();
            }
            shouldHideControls = false;
        } else {
            if (!shouldHideControls) {
                if (isPlaying) {
                    shouldHideControls = true;
                    visibilityModule.play();
                } else {
                    shouldHideControls = true;
                    visibilityModule.pause();
                }
            }
        }
        seekbar.renderAdCues(vm.adCues);

        advertisementButton.setClickable(vm.isAdvertisementButtonClickable);
        int linkDrawable = vm.isAdvertisementButtonClickable ? R.drawable.ic_link : 0;
        advertisementButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, linkDrawable, 0);
        renderClickThrough(vm.advertisementClickUrl);
    }

    private void renderAudioAndCcList(@NonNull LinkedList<ViewModel.TrackOptionVM> audioTracks, @NonNull LinkedList<ViewModel.TrackOptionVM> ccTracks) {
        if (!this.audioTracks.equals(audioTracks) || !this.ccTracks.equals(ccTracks)) {
            this.audioTracks.clear();
            this.audioTracks.addAll(audioTracks);

            this.ccTracks.clear();
            this.ccTracks.addAll(ccTracks);

            adapter.updateData(getContext(), audioTracks, ccTracks);
        }

        if (audioTracks.isEmpty() && ccTracks.isEmpty()) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == INVISIBLE) {
            visibilityModule.timeout();
        }
    }

    private void renderControlsVisibility(boolean isStreamPlaying) {
        if (isPlaying != isStreamPlaying) {
            if (isStreamPlaying) {
                visibilityModule.play();
            } else {
                visibilityModule.pause();
            }
        }
    }

    private void requestFocus(View view) {
        UiModeManager uiModeManager = (UiModeManager) getContext().getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null && uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            view.requestFocusFromTouch();
        } else {
            view.requestFocus();
        }
    }

    private void renderCompassDirection(double compassLongitude) {
        if (Math.abs(this.longitude - compassLongitude) > .00000000001f) {
            compassView.setRotation((float) Math.toDegrees(-compassLongitude));
        }
    }

    private void renderThumbnail(@Nullable String url) {
        if (url == null && thumbUrl != null || url != null && !url.equals(thumbUrl)) {
            if (imageLoader != null) {
                imageLoader.cancelLoad(thumbnailView);
                imageLoader.load(url, thumbnailView);
            }
        }
    }

    private void initFocusIssue() {
        playButton.setVisibilityListener(visibility -> {
            if (visibility == INVISIBLE && playButton.isFocused()) {
                focusedView = playButton.view;
            }
            if (visibility == VISIBLE &&
                    (focusedView == pauseButton.view || focusedView == replayButton.view || focusedView == playButton.view
                            || pauseButton.isFocused() || replayButton.isFocused())) {
                playButton.setVisibility(visibility);
                focusedView = playButton.view;
                requestFocus(playButton.view);
            }
        });

        pauseButton.setVisibilityListener(visibility -> {
            if (visibility == INVISIBLE && pauseButton.isFocused()) {
                focusedView = pauseButton.view;
            }
            if (visibility == VISIBLE &&
                    (focusedView == playButton.view || focusedView == replayButton.view || focusedView == pauseButton.view
                            || playButton.isFocused() || replayButton.isFocused())) {
                pauseButton.setVisibility(visibility);
                focusedView = pauseButton.view;
                requestFocus(pauseButton.view);
            }
        });

        replayButton.setVisibilityListener(visibility -> {
            if (visibility == INVISIBLE && replayButton.isFocused()) {
                focusedView = replayButton.view;
            }
            if (visibility == VISIBLE &&
                    (focusedView == playButton.view || focusedView == pauseButton.view || focusedView == replayButton.view
                            || playButton.isFocused() || pauseButton.isFocused())) {
                replayButton.setVisibility(visibility);
                focusedView = replayButton.view;
                requestFocus(replayButton.view);
            }
        });
        OnFocusChangeListener focusChangeListener = (view, b) -> {
            if (b && (playButton.getVisibility() == VISIBLE || pauseButton.getVisibility() == VISIBLE || replayButton.getVisibility() == VISIBLE)) {
                focusedView = null;
            }
        };
        playNextButton.setOnFocusChangeListener(focusChangeListener);
        playPreviousButton.setOnFocusChangeListener(focusChangeListener);
        backwardSeekButton.setOnFocusChangeListener(focusChangeListener);
        forwardSeekButton.setOnFocusChangeListener(focusChangeListener);
        trackChooserButton.setOnFocusChangeListener(focusChangeListener);
        seekbar.setOnFocusChangeListener(focusChangeListener);
        focusedView = replayButton.view;
    }

    private void resetVisibilityState() {
        if (animator != null) {
            animator.cancel();
        }
        controlsContainer.setVisibility(VISIBLE);
    }

    public void show() {
        if (isVisible(controlsContainer)) {
            return;
        }
        View focusedView = this.focusedView;
        resetVisibilityState();
        setFocusable(false);
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(400);
        animator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            controlsContainer.setAlpha(value);
        });
        animator.start();
        if (focusedView != null) {
            requestFocus(focusedView);
        }
        sidePanel.show();
    }

    public void hide() {
        if (!shouldHideControls) return;
        resetVisibilityState();
        focusedView = controlsContainer.findFocus();
        setFocusable(true);
        animator = ValueAnimator.ofFloat(1, 0);
        assert animator != null;
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                controlsContainer.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.setDuration(400);
        animator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            controlsContainer.setAlpha(value);
        });
        animator.start();
        sidePanel.hide();
    }

    public void startTimer() {
        timer.schedule(3000L, visibilityModule::timeout);
        timer.start();
    }

    public void cancelTimer() {
        timer.reset();
    }

    @SuppressWarnings("unused")
    @NonNull
    public SidePanel getSidePanel() {
        return sidePanel;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        visibilityModule.prolong();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (listener != null) {
            if (controlsContainer.getVisibility() == VISIBLE) {
                visibilityModule.prolong();
            } else {
                visibilityModule.tap();
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private void renderClickThrough(@Nullable String advertisementClickUrl) {
        if ((advertisementClickUrl != null && !advertisementClickUrl.equals(this.advertisementClickUrl)) ||
                (advertisementClickUrl == null && this.advertisementClickUrl != null)) {
            this.advertisementClickUrl = advertisementClickUrl;
            if (listener == null || advertisementClickUrl == null) return;

            listener.onBrandedContentAdPresented();
            Context context = getContext();
            Intent intent = new Intent(context, TargetUrlActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(TargetUrlActivity.KEY_TARGET_URL, advertisementClickUrl);
            context.startActivity(intent);
        }
    }

    @SuppressWarnings("unused")
    public void setImageLoader(@NonNull ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
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

    @SuppressWarnings("unused")
    public void setLiveDotColor(@ColorInt int color) {
        liveDotColor = color;
    }

    @SuppressWarnings("unused")
    public void setDurationColor(@ColorInt int color) {
        durationColor = color;
    }

    @SuppressWarnings("unused")
    public void setTitleColor(@ColorInt int color) {
        titleColor = color;
    }

    @SuppressWarnings("unused")
    public void setCurrentTimeColor(@ColorInt int color) {
        currentTimeColor = color;
    }
}
