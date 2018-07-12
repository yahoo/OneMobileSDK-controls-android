/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.aol.mobile.sdk.annotations.PublicApi;

@UiThread
@PublicApi
public interface AdControls {
    void setListener(@Nullable Listener listener);

    void render(@NonNull ViewModel vm);

    enum Button {PLAY, PAUSE}

    @UiThread
    interface Listener {
        void onButtonClick(@NonNull Button button);

        void onAdClicked();

        void onAdPresented();
    }

    final class ViewModel {
        public boolean embedClickThroughUrl;
        public boolean isCloseButtonVisible;
        public boolean isProgressViewVisible;
        public boolean isPlayButtonVisible;
        public boolean isPauseButtonVisible;
        public boolean isSeekbarVisible;
        public boolean isTimeLeftTextVisible;
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
