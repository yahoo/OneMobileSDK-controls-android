/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
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
        if (tasks.isEmpty()) return;
        for (Runnable task : tasks.keySet()) {
            handler.removeCallbacks(task);
        }

        tasks.clear();
    }
}
