/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.utils;

import android.support.annotation.NonNull;

public interface Timer {
    void schedule(long timeout, @NonNull Runnable task);

    void start();

    void reset();
}
