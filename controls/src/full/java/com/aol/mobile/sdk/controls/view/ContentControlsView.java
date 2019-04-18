/*
 * Copyright 2019, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.aol.mobile.sdk.annotations.PublicApi;

@PublicApi
public class ContentControlsView extends AbstractContentControlsView {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ContentControlsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        updateColors();
    }

    public ContentControlsView(@NonNull Context context) {
        super(context);

        updateColors();
    }

    public ContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        updateColors();
    }

    public ContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        updateColors();
    }
}
