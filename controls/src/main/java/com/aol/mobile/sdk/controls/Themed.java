/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls;


import android.support.annotation.ColorInt;

import com.aol.mobile.sdk.annotations.PublicApi;

@PublicApi
public interface Themed {
    void setMainColor(@ColorInt int color);

    void setAccentColor(@ColorInt int color);
}
