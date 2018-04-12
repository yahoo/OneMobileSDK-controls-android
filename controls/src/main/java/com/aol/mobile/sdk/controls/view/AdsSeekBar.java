/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.aol.mobile.sdk.controls.R;

import java.util.HashSet;
import java.util.Set;

public class AdsSeekBar extends SeekBar {

    private Paint adCuesPaint = new Paint();
    private Set<Double> adCues;

    public AdsSeekBar(final Context context) {
        this(context, null);
    }

    public AdsSeekBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdsSeekBar(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        adCuesPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        adCuesPaint.setColor(getResources().getColor(R.color.default_accent_color));
    }

    public void renderAdCues(Set<Double> adCues) {
        if (!adCues.equals(this.adCues)) {
            this.adCues = new HashSet<>(adCues);
            drawMarks();
        }
    }

    private void drawMarks() {
        LayerDrawable progressDrawable = (LayerDrawable) getProgressDrawable();
        Drawable background = progressDrawable.findDrawableByLayerId(android.R.id.background);
        final int height = background.getBounds().height();
        final int width = background.getBounds().width();
        final float radius = getResources().getDimension(R.dimen.ad_mark_radius);
        if (width == 0 || height == 0) return;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        if (adCues != null && adCues.size() != 0) {
            for (double position : adCues) {
                c.drawCircle((float) (position * width), height / 2, radius, adCuesPaint);
            }
        }
        progressDrawable.setDrawableByLayerId(R.id.ad_marks, new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            drawMarks();
        }
    }
}