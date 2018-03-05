/*
 * Copyright (c) 2017. Oath.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
