/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.aol.mobile.sdk.annotations.PublicApi;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@UiThread
@PublicApi
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

        void onPlaybackSpeedSelected(float speed);

        void onCastEnabled();

        void onCastDisabled();

        void onBrandedContentAdClicked();

        void onBrandedContentAdPresented();
    }

    final class ViewModel {
        @NonNull
        public final LinkedList<TrackOptionVM> audioTracks = new LinkedList<>();
        @NonNull
        public final LinkedList<TrackOptionVM> ccTracks = new LinkedList<>();
        @NonNull
        public final Set<Double> adCues = new HashSet<>();
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
        public String thumbnailImageUrl;
        public boolean isLiveIndicatorVisible;
        public boolean isOnLiveEdge;
        public boolean isCastButtonVisible;
        public boolean isCasting;
        @Nullable
        public String advertisementText;
        @Nullable
        public String advertisementClickUrl;
        public boolean isAdvertisementButtonVisible;
        public boolean isAdvertisementButtonClickable;

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
