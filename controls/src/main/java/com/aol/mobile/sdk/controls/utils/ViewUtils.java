/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.utils;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.content.Context.WINDOW_SERVICE;

public final class ViewUtils {
    @SuppressWarnings("unchecked")
    public static <T extends View> T findView(@NonNull View src, @IdRes int resId) {
        return (T) src.findViewById(resId);
    }

    @CheckResult
    public static boolean isVisible(@NonNull View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public static void set(@NonNull View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public static void renderSeekerBufferProgress(int bufferedProgress, @NonNull SeekBar seekBar) {
        int progress = bufferedProgress * seekBar.getMax() / 100;
        if (seekBar.getSecondaryProgress() != progress) seekBar.setSecondaryProgress(progress);
    }

    public static void renderSeekerProgress(double seekerProgress, @NonNull SeekBar seekBar) {
        int progress = (int) Math.round(seekBar.getMax() * seekerProgress);
        if (seekBar.getProgress() != progress) seekBar.setProgress(progress);
    }

    public static void renderSeekerMaxValue(int maxValue, @NonNull SeekBar seekBar) {
        if (seekBar.getMax() != maxValue) seekBar.setMax(maxValue);
    }

    public static void renderText(@Nullable CharSequence text, @NonNull TextView view) {
        CharSequence viewText = view.getText();

        if (text == null && viewText != null || text != null && !text.equals(viewText))
            view.setText(text);
    }

    public static void renderSelectivity(boolean isSelected, @NonNull View view) {
        if (isSelected != view.isSelected()) view.setSelected(isSelected);
    }

    public static void renderAvailability(boolean isEnabled, @NonNull View view) {
        if (isEnabled != view.isEnabled()) {
            view.setAlpha(isEnabled ? 1f : .3f);
            view.setEnabled(isEnabled);
            view.setFocusable(isEnabled);
        }
    }

    public static void renderVisibility(boolean isVisible, @NonNull View view) {
        if (isVisible != isVisible(view)) set(view, isVisible);
    }

    @NonNull
    @CheckResult
    public static Point getDisplaySize(@NonNull Context context) {
        Point displaySize = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(displaySize);

        return displaySize;
    }
}
