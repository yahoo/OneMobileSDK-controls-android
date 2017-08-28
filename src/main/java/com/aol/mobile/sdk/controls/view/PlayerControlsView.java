package com.aol.mobile.sdk.controls.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aol.mobile.sdk.controls.ControlsButton;
import com.aol.mobile.sdk.controls.ImageLoader;
import com.aol.mobile.sdk.controls.PlayerControls;
import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;
import com.aol.mobile.sdk.controls.utils.AndroidHandlerTimer;
import com.aol.mobile.sdk.controls.utils.TracksChooserAdapter;
import com.aol.mobile.sdk.controls.utils.ViewUtils;
import com.aol.mobile.sdk.controls.utils.VisibilityModule;
import com.aol.mobile.sdk.controls.utils.VisibilityWrapper;
import com.aol.mobile.sdk.controls.viewmodel.PlayerControlsVM;
import com.aol.mobile.sdk.controls.viewmodel.TrackOptionVM;

import java.util.LinkedList;

import static com.aol.mobile.sdk.controls.utils.ViewUtils.findView;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.isVisible;

public class PlayerControlsView extends RelativeLayout implements PlayerControls, Themed {
    @NonNull
    private final RelativeLayout controlsContainer;
    @NonNull
    private final FrameLayout compassContainer;
    @NonNull
    private final FrameLayout subtitlesContainer;
    @NonNull
    private final ProgressBar progressView;
    @NonNull
    private final TextView subtitlesView;
    @NonNull
    private final TextView titleView;
    @NonNull
    private final VisibilityWrapper<TintableImageButton> playButton;
    @NonNull
    private final VisibilityWrapper<TintableImageButton> pauseButton;
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
    private final SeekBar seekbar;
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
    @Nullable
    private View focusedView;
    @Nullable
    private ValueAnimator animator;
    @Nullable
    private Listener listener;
    @Nullable
    private Dialog dialog;
    @NonNull
    private final OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (listener == null) return;

            visibilityModule.prolong();
            if (view == playButton.view) listener.onButtonClick(ControlsButton.PLAY);
            if (view == pauseButton.view) listener.onButtonClick(ControlsButton.PAUSE);
            if (view == replayButton.view) listener.onButtonClick(ControlsButton.REPLAY);
            if (view == playNextButton) listener.onButtonClick(ControlsButton.NEXT);
            if (view == playPreviousButton) listener.onButtonClick(ControlsButton.PREVIOUS);
            if (view == forwardSeekButton) listener.onButtonClick(ControlsButton.SEEK_FORWARD);
            if (view == backwardSeekButton) listener.onButtonClick(ControlsButton.SEEK_BACKWARD);
            if (view == compassView) listener.onButtonClick(ControlsButton.COMPASS);

            if (view == trackChooserButton) {
                dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        PlayerControlsView.this.dialog = null;
                    }
                });
                ListView listView = new ListView(getContext());
                listView.setDivider(null);
                listView.setAdapter(adapter);
                adapter.updateData(getContext(), audioTracks, ccTracks);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TracksChooserAdapter.Item item = (TracksChooserAdapter.Item) parent.getAdapter().getItem(position);

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
                    }
                });
                dialog.setContentView(listView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                Window window = dialog.getWindow();
                window.getAttributes().windowAnimations = R.style.TracksDialogAnimation;
                window.getAttributes().gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        }
    };

    @NonNull
    private final SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
            if (listener == null) return;

            if (fromUser) {
                visibilityModule.prolong();
            }
            if (fromUser) listener.onSeekTo(progress / (float) seekBar.getMax());


            int thumbWidth = getResources().getDimensionPixelSize(R.dimen.thumb_size);
            int val = progress * (seekBar.getWidth() - thumbWidth) / seekBar.getMax();
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
    @Nullable
    private String thumbUrl;
    @Nullable
    private ImageLoader imageLoader;
    private boolean isPlaying = false;
    private double longitude;
    @ColorInt
    private int mainColor;
    @ColorInt
    private int accentColor;
    @ColorInt
    private int liveDotColor;
    @NonNull
    private final LinkedList<TrackOptionVM> audioTracks = new LinkedList<>();
    private final LinkedList<TrackOptionVM> ccTracks = new LinkedList<>();

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerControlsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public PlayerControlsView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerControlsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttrs(context, attrs);

        inflate(getContext(), R.layout.player_controls_view, this);

        controlsContainer = findView(this, R.id.controls_container);
        progressView = findView(this, R.id.progressbar);
        playButton = new VisibilityWrapper<>((TintableImageButton) findView(this, R.id.play_button));
        pauseButton = new VisibilityWrapper<>((TintableImageButton) findView(this, R.id.pause_button));
        replayButton = new VisibilityWrapper<>((TintableImageButton) findView(this, R.id.replay_button));
        playNextButton = findView(this, R.id.next_button);
        playPreviousButton = findView(this, R.id.prev_button);
        trackChooserButton = findView(this, R.id.tracks_button);
        forwardSeekButton = findView(this, R.id.forward_seek_button);
        backwardSeekButton = findView(this, R.id.backward_seek_button);
        liveIndicatorLayout = findView(this, R.id.live_indicator);
        subtitlesContainer = findView(this, R.id.subtitles_container);
        subtitlesView = findView(this, R.id.subtitles_view);
        seekerContainer = findView(this, R.id.seekbar_container);
        seekbar = findView(this, R.id.seekbar);
        titleView = findView(this, R.id.title_view);
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
        seekbar.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                seekbarListener.onProgressChanged(seekbar, seekbar.getProgress(), false);
            }
        });

        subtitlesContainer.setPadding(0, 0, 0, (int) (getResources().getDimension(R.dimen.seeker_height) + getResources().getDimension(R.dimen.seekbar_bottom_padding)));

        setupListeners();
        initFocusIssue();
        updateColors();

        visibilityModule = new VisibilityModule(this);
    }

    private void updateColors() {
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

        durationView.setTextColor(mainColor);
        titleView.setTextColor(mainColor);
        currentTimeView.setTextColor(accentColor);
    }

    @SuppressWarnings("deprecation")
    private void readAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        mainColor = context.getResources().getColor(R.color.default_main_color);
        accentColor = context.getResources().getColor(R.color.default_accent_color);
        liveDotColor = Color.RED;

        if (attrs == null) return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ControlsAttrs, 0, 0);

        try {
            mainColor = a.getColor(R.styleable.ControlsAttrs_mainColor, mainColor);
            accentColor = a.getInteger(R.styleable.ControlsAttrs_accentColor, accentColor);
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
        seekbar.setOnSeekBarChangeListener(seekbarListener);
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    @Override
    public void render(@NonNull PlayerControlsVM viewModel) {
        renderControlsVisibility(viewModel.isStreamPlaying);
        ViewUtils.renderVisibility(viewModel.isLoading, progressView);
        ViewUtils.renderVisibility(viewModel.isPlayButtonVisible, playButton.view);
        ViewUtils.renderVisibility(viewModel.isPauseButtonVisible, pauseButton.view);
        ViewUtils.renderVisibility(viewModel.isReplayButtonVisible, replayButton.view);
        ViewUtils.renderVisibility(viewModel.isNextButtonVisible, playNextButton);
        ViewUtils.renderVisibility(viewModel.isPrevButtonVisible, playPreviousButton);
        ViewUtils.renderVisibility(viewModel.isSeekForwardButtonVisible, forwardSeekButton);
        ViewUtils.renderVisibility(viewModel.isSeekBackButtonVisible, backwardSeekButton);
        ViewUtils.renderVisibility(viewModel.isSeekerVisible, seekerContainer);
        ViewUtils.renderVisibility(viewModel.isCompassViewVisible, compassContainer);
        ViewUtils.renderVisibility(viewModel.isTitleVisible, titleView);
        ViewUtils.renderVisibility(viewModel.isSubtitlesTextVisible, subtitlesContainer);
        ViewUtils.renderVisibility(viewModel.isThumbnailImageVisible, thumbnailView);
        ViewUtils.renderVisibility(viewModel.isTrackChooserButtonVisible, trackChooserButton);

        ViewUtils.renderAvailability(viewModel.isNextButtonEnabled, playNextButton);
        ViewUtils.renderAvailability(viewModel.isPrevButtonEnabled, playPreviousButton);
        ViewUtils.renderAvailability(viewModel.isTrackChooserButtonEnabled, trackChooserButton);

        ViewUtils.renderText(viewModel.titleText, titleView);
        ViewUtils.renderText(viewModel.subtitlesText, subtitlesView);
        ViewUtils.renderText(viewModel.seekerCurrentTimeText, currentTimeView);
        ViewUtils.renderText(viewModel.seekerDurationText, durationView);

        ViewUtils.renderSeekerMaxValue(viewModel.seekerMaxValue, seekbar);
        ViewUtils.renderSeekerProgress(viewModel.seekerProgress, seekbar);
        ViewUtils.renderSeekerBufferProgress(viewModel.seekerBufferedProgress, seekbar);

        renderThumbnail(viewModel.thumbnailImageUrl);
        renderCompassDirection(viewModel.compassLongitude);
        renderAudioAndCcList(viewModel.audioTracks, viewModel.ccTracks);

        RelativeLayout.LayoutParams titleLayoutParams = (RelativeLayout.LayoutParams) titleView.getLayoutParams();
        if (viewModel.isLiveIndicatorVisible) {
            titleLayoutParams.setMargins(liveIndicatorLayout.getWidth(), 0, 0, 0);
        } else {
            titleLayoutParams.setMargins(0, 0, 0, 0);
        }
        titleView.setLayoutParams(titleLayoutParams);
        liveIndicatorLayout.setVisibility(viewModel.isLiveIndicatorVisible ? VISIBLE : GONE);

        Drawable liveDot = liveIndicatorLayout.getChildAt(0).getBackground();
        if (viewModel.isOnLiveEdge) {
            liveDot.setColorFilter(liveDotColor, PorterDuff.Mode.MULTIPLY);
        } else {
            liveDot.setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);
        }

        isPlaying = viewModel.isStreamPlaying;
        thumbUrl = viewModel.thumbnailImageUrl;
        longitude = viewModel.compassLongitude;
    }

    private void renderAudioAndCcList(@NonNull LinkedList<TrackOptionVM> audioTracks, @NonNull LinkedList<TrackOptionVM> ccTracks) {
        this.audioTracks.clear();
        this.audioTracks.addAll(audioTracks);

        this.ccTracks.clear();
        this.ccTracks.addAll(ccTracks);

        if (dialog != null && dialog.isShowing()) {
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
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
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
        playButton.setVisibilityListener(new VisibilityWrapper.VisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
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
            }
        });

        pauseButton.setVisibilityListener(new VisibilityWrapper.VisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
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
            }
        });

        replayButton.setVisibilityListener(new VisibilityWrapper.VisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
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
            }
        });
        OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && (playButton.getVisibility() == VISIBLE || pauseButton.getVisibility() == VISIBLE || replayButton.getVisibility() == VISIBLE)) {
                    focusedView = null;
                }
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
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                controlsContainer.setAlpha(value);
                subtitlesContainer.setPadding(0, 0, 0, (int) ((seekerContainer.getHeight() + getResources().getDimensionPixelSize(R.dimen.seekbar_bottom_padding)) * value));
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                subtitlesContainer.setPadding(0, 0, 0, (seekerContainer.getHeight() + getResources().getDimensionPixelSize(R.dimen.seekbar_bottom_padding)));
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
        animator.start();
        if (focusedView != null) {
            requestFocus(focusedView);
        }
        sidePanel.show();
        subtitlesContainer.setPadding(0, 0, 0, seekerContainer.getHeight() + getResources().getDimensionPixelSize(R.dimen.seekbar_bottom_padding));
    }

    public void hide() {
        resetVisibilityState();
        focusedView = controlsContainer.findFocus();
        setFocusable(true);
        animator = ValueAnimator.ofFloat(1, 0);
        assert animator != null;
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                controlsContainer.setVisibility(INVISIBLE);
                subtitlesContainer.setPadding(0, 0, 0, 0);
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
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                controlsContainer.setAlpha(value);
                subtitlesContainer.setPadding(0, 0, 0, (int) ((seekerContainer.getHeight() + getResources().getDimensionPixelSize(R.dimen.seekbar_bottom_padding)) * value));
            }
        });
        animator.start();
        sidePanel.hide();
        subtitlesContainer.setPadding(0, 0, 0, 0);
    }

    public void startTimer() {
        timer.schedule(3000L, new Runnable() {
            @Override
            public void run() {
                visibilityModule.timeout();
            }
        });
        timer.start();
    }

    public void cancelTimer() {
        timer.reset();
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

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

    public void setLiveDotColor(@ColorInt int color) {
        liveDotColor = color;
    }
}
