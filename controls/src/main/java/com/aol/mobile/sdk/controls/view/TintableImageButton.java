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
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.aol.mobile.sdk.annotations.PrivateApi;
import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;

@PrivateApi
public class TintableImageButton extends ImageButton implements Themed {
    protected int mainColor;
    protected int accentColor;

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
            } finally {
                a.recycle();
            }
        }

        drawableStateChanged();
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
