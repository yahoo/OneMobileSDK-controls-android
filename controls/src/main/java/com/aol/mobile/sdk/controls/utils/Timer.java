/*
 * Copyright (c) 2016 One by Aol : Publishers. All rights reserved.
 */

package com.aol.mobile.sdk.controls.utils;

import android.support.annotation.NonNull;

public interface Timer {
    void schedule(long timeout, @NonNull Runnable task);

    void start();

    void reset();
}
