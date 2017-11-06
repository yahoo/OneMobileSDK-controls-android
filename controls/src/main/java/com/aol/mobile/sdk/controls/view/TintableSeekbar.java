package com.aol.mobile.sdk.controls.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;
import com.aol.mobile.sdk.controls.utils.ImageUtils;

import static com.aol.mobile.sdk.controls.utils.ImageUtils.loadRemoteResAsDrawable;

public class TintableSeekbar extends SeekBar implements Themed {
    private final Path p = new Path();
    private Drawable overlayThumb;
    private int mainColor;
    private int accentColor;

    public TintableSeekbar(Context context) {
        this(context, null);
    }

    public TintableSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintableSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        String seekerBg;
        String seekerProgress;
        String thumbSrc;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TintableSeekbar, 0, 0);
        try {
            seekerBg = a.getString(R.styleable.TintableSeekbar_remoteSeekBg);
            seekerProgress = a.getString(R.styleable.TintableSeekbar_remoteSeekProgress);
            thumbSrc = a.getString(R.styleable.TintableSeekbar_remoteSeekThumb);
        } finally {
            a.recycle();
        }

        if (seekerBg != null && seekerProgress != null) {
            ImageUtils.loadSeekerDrawable(seekerBg, seekerProgress, getResources(), new ImageUtils.LoadCallback() {
                @Override
                public void onDrawableLoaded(@NonNull Drawable drawable) {
                    int progress = getProgress();
                    setProgress(0);
                    setSecondaryProgress(0);
                    drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    setProgressDrawable(drawable);
                    setMainColor(mainColor);
                    setAccentColor(accentColor);
                    setProgress(progress);
                }
            });
        }

        if (thumbSrc != null) {
            loadRemoteResAsDrawable(thumbSrc, getResources(), new ImageUtils.LoadCallback() {
                @Override
                public void onDrawableLoaded(@NonNull Drawable drawable) {
                    Drawable thumbHolder = getResources().getDrawable(R.drawable.thumb_container);
                    LayerDrawable thumb = new LayerDrawable(new Drawable[]{thumbHolder, drawable});

                    thumb.setBounds(0, 0, thumbHolder.getIntrinsicWidth(), thumb.getIntrinsicHeight());
                    setThumb(thumb);
                    setThumbOffset(0);
                    setAccentColor(accentColor);
                    postInvalidate();
                }
            });
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (overlayThumb != null) {
            int width = overlayThumb.getIntrinsicWidth();
            int height = overlayThumb.getIntrinsicHeight();
            float progressWidth = canvas.getWidth() - width;
            progressWidth = progressWidth * getProgress() / getMax();
            float startXpos = Math.max(0, progressWidth - getThumbOffset());
            float startYpos = (canvas.getHeight() - height) / 2f;

            p.reset();
            p.addRect(0, 0, startXpos, (float) canvas.getHeight(), Path.Direction.CW);
            p.addRect(Math.min(progressWidth + width - getThumbOffset(), canvas.getWidth()), 0, canvas.getWidth(), canvas.getHeight(), Path.Direction.CW);

            int save = canvas.save();
            canvas.clipPath(p);
            super.onDraw(canvas);
            canvas.restoreToCount(save);

            save = canvas.save();
            canvas.translate(startXpos, startYpos);
            overlayThumb.draw(canvas);
            canvas.restoreToCount(save);
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    public Drawable getThumb() {
        return overlayThumb;
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(null);
        overlayThumb = thumb;
    }

    @Override
    public void setMainColor(int color) {
        mainColor = color;
        LayerDrawable seekerDrawable = (LayerDrawable) getProgressDrawable();
        if (seekerDrawable != null) {
            Drawable drawable = seekerDrawable.getDrawable(0);
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void setAccentColor(int color) {
        accentColor = color;
        LayerDrawable seekerDrawable = (LayerDrawable) getProgressDrawable();
        if (seekerDrawable != null) {
            Drawable drawable = seekerDrawable.getDrawable(2);
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

            drawable = seekerDrawable.getDrawable(1);
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }

        if (overlayThumb != null) {
            overlayThumb.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }
}
