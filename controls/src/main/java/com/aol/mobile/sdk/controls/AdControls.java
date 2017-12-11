/*
 * Copyright (c) 2016 One by Aol : Publishers. All rights reserved.
 */

package com.aol.mobile.sdk.controls;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

@UiThread
public interface AdControls {
    void setListener(@Nullable Listener listener);

    void render(@NonNull ViewModel vm);

    enum Button {PLAY, PAUSE}

    @UiThread
    interface Listener {
        void onButtonClick(@NonNull Button button);

        void onAdClicked();
    }

    final class ViewModel {
        public boolean isProgressViewVisible;
        public boolean isPlayButtonVisible;
        public boolean isPauseButtonVisible;
        public boolean isAdTimeViewVisible;
        public double seekerProgress;
        public int seekerMaxValue;
        @Nullable
        public String currentTimeText;
        @Nullable
        public String timeLeftText;
        @Nullable
        public String durationText;
        @Nullable
        public String adUrl;
    }
}
