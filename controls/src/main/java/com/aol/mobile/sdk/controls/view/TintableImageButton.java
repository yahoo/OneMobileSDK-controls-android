package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.widget.ImageButton;

import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;
import com.aol.mobile.sdk.controls.utils.ImageUtils;

import static com.aol.mobile.sdk.controls.utils.ImageUtils.loadRemoteResAsDrawable;

public class TintableImageButton extends ImageButton implements Themed {
    protected int mainColor;
    protected int accentColor;
    private String remoteSource;
    private String effectSource;
    private String pressSource;

    public TintableImageButton(@NonNull Context context) {
        this(context, null);
    }

    public TintableImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("deprecation")
    public TintableImageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mainColor = context.getResources().getColor(R.color.default_main_color);
        accentColor = context.getResources().getColor(R.color.default_accent_color);

        if (attrs != null) {
            TypedArray a = context
                    .obtainStyledAttributes(attrs, R.styleable.TintableImageButton, defStyle, 0);

            try {
                mainColor = a.getColor(R.styleable.TintableImageButton_mainColor, mainColor);
                accentColor = a.getColor(R.styleable.TintableImageButton_accentColor, accentColor);

                remoteSource = a.getString(R.styleable.TintableImageButton_remoteSource);
                pressSource = a.getString(R.styleable.TintableImageButton_remotePressSource);
                effectSource = a.getString(R.styleable.TintableImageButton_remotePressEffectSource);
            } finally {
                a.recycle();
            }
        }

        final Resources resources = getResources();

        if (remoteSource != null) {
            loadRemoteResAsDrawable(remoteSource, resources, new ImageUtils.LoadCallback() {
                @Override
                public void onDrawableLoaded(@NonNull final Drawable drawable) {
                    if (effectSource != null) {
                        loadRemoteResAsDrawable(effectSource, resources, new ImageUtils.LoadCallback() {
                            @Override
                            public void onDrawableLoaded(@NonNull Drawable effectDrawable) {
                                LayerDrawable withEffect = new LayerDrawable(new Drawable[]{
                                        effectDrawable, drawable
                                });

                                StateListDrawable withState = new StateListDrawable();
                                withState.addState(new int[]{android.R.attr.state_pressed}, withEffect);
                                withState.addState(StateSet.WILD_CARD, drawable);

                                setImageDrawable(withState);
                                drawableStateChanged();
                            }
                        });
                    } else {
                        if (pressSource != null) {
                            loadRemoteResAsDrawable(pressSource, resources, new ImageUtils.LoadCallback() {
                                @Override
                                public void onDrawableLoaded(@NonNull Drawable pressDrawable) {
                                    StateListDrawable withState = new StateListDrawable();
                                    withState.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
                                    withState.addState(StateSet.WILD_CARD, drawable);

                                    setImageDrawable(withState);
                                    drawableStateChanged();
                                }
                            });
                        } else {
                            setImageDrawable(drawable);
                            drawableStateChanged();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        setColorFilter(getColorFor(getDrawableState()), PorterDuff.Mode.MULTIPLY);
    }

    @ColorInt
    protected int getColorFor(@NonNull int[] drawableState) {
        for (int state : drawableState) {
            switch (state) {
                case android.R.attr.state_pressed:
                case android.R.attr.state_selected:
                case android.R.attr.state_focused:
                    return accentColor;
            }
        }

        return mainColor;
    }

    @Override
    public void setMainColor(@ColorInt int color) {
        this.mainColor = color;
        drawableStateChanged();
    }

    @Override
    public void setAccentColor(@ColorInt int color) {
        this.accentColor = color;
        drawableStateChanged();
    }
}
