/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.aol.mobile.sdk.annotations.PublicApi;

@PublicApi
public interface ImageLoader {
    void load(@Nullable String url, @NonNull ImageView imageView);

    void cancelLoad(@NonNull ImageView imageView);
}
