package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class TintableToggleImageButton extends TintableImageButton {

    public TintableToggleImageButton(@NonNull Context context) {
        this(context, null);
    }

    public TintableToggleImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("deprecation")
    public TintableToggleImageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
                case android.R.attr.state_selected:
                    return accentColor;
            }
        }

        return mainColor;
    }
}
