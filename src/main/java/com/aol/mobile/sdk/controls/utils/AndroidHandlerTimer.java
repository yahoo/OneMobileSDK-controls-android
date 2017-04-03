/*
 * Copyright (c) 2016 One by Aol : Publishers. All rights reserved.
 */

package com.aol.mobile.sdk.controls.utils;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class AndroidHandlerTimer implements Timer {
    @NonNull
    private final Handler handler;
    @NonNull
    private final HashMap<Runnable, Long> tasks = new HashMap<>();

    public AndroidHandlerTimer(@NonNull Handler handler) {
        this.handler = handler;
    }

    @Override
    public void schedule(long timeout, @NonNull Runnable task) {
        tasks.put(task, timeout);
    }

    @Override
    public void start() {
        for (Map.Entry<Runnable, Long> taskEntry : tasks.entrySet()) {
            handler.postDelayed(taskEntry.getKey(), taskEntry.getValue());
        }
    }

    @Override
    public void reset() {
        for (Runnable task : tasks.keySet()) {
            handler.removeCallbacks(task);
        }

        tasks.clear();
    }
}
