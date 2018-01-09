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

import java.util.LinkedList;

@UiThread
public interface ContentControls {
    void setListener(@Nullable Listener listener);

    void render(@NonNull ViewModel vm);

    enum Button {PLAY, PAUSE, REPLAY, NEXT, PREVIOUS, SEEK_FORWARD, SEEK_BACKWARD, COMPASS}

    @UiThread
    interface Listener {
        void onButtonClick(@NonNull Button button);

        void onScroll(float distanceX, float distanceY);

        void onSeekStarted();

        void onSeekTo(double position);

        void onSeekStopped();

        void onAudioTrackSelected(int index);

        void onCcTrackSelected(int index);

        void onCastEnabled();

        void onCastDisabled();
    }

    final class ViewModel {
        @NonNull
        public final LinkedList<TrackOptionVM> audioTracks = new LinkedList<>();
        @NonNull
        public final LinkedList<TrackOptionVM> ccTracks = new LinkedList<>();
        public boolean isLoading;
        public boolean isPlayButtonVisible;
        public boolean isPauseButtonVisible;
        public boolean isReplayButtonVisible;
        public boolean isNextButtonVisible;
        public boolean isNextButtonEnabled;
        public boolean isPrevButtonVisible;
        public boolean isPrevButtonEnabled;
        public boolean isSeekerVisible;
        public boolean isSeekForwardButtonVisible;
        public boolean isSeekBackButtonVisible;
        public boolean isTitleVisible;
        public boolean isSubtitlesTextVisible;
        public boolean isCompassViewVisible;
        public boolean isThumbnailImageVisible;
        public boolean isStreamPlaying;
        public boolean isTrackChooserButtonEnabled;
        public boolean isTrackChooserButtonVisible;
        public int seekerBufferedProgress;
        public int seekerMaxValue;
        public double seekerProgress;
        public double compassLatitude;
        public double compassLongitude;
        @Nullable
        public String seekerCurrentTimeText;
        @Nullable
        public String seekerTimeLeftText;
        @Nullable
        public String seekerDurationText;
        @Nullable
        public String titleText;
        @Nullable
        public CharSequence subtitlesText;
        @Nullable
        public String thumbnailImageUrl;
        public boolean isLiveIndicatorVisible;
        public boolean isOnLiveEdge;
        public boolean isCastButtonVisible;
        public boolean isCasting;

        public final static class TrackOptionVM {
            @Nullable
            public String title;
            public boolean isSelected;

            public TrackOptionVM(@Nullable String title, boolean isSelected) {
                this.title = title;
                this.isSelected = isSelected;
            }
        }
    }
}
