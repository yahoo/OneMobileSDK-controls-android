/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.aol.mobile.sdk.annotations.PublicApi;
import com.aol.mobile.sdk.controls.ContentControls;
import com.aol.mobile.sdk.controls.Themed;

@PublicApi
public class ContentControlsView extends FrameLayout implements ContentControls, Themed {
    public ContentControlsView(@NonNull Context context) {
        super(context);
    }

    public ContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setListener(@Nullable Listener listener) {

    }

    @Override
    public void render(@NonNull ViewModel vm) {

    }

    @Override
    public void setMainColor(int color) {

    }

    @Override
    public void setAccentColor(int color) {

    }
}
