package com.aol.mobile.sdk.controls;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;

import com.aol.mobile.sdk.controls.viewmodel.PlayerControlsVM;

@UiThread
public interface PlayerControls {
    void setListener(@Nullable Listener listener);

    void render(@NonNull PlayerControlsVM viewModel);

    @NonNull
    View getView();

    @UiThread
    interface Listener {
        void onButtonClick(@NonNull ControlsButton button);

        void onScroll(float distanceX, float distanceY);

        void onSeekStarted();

        void onSeekTo(double position);

        void onSeekStopped();

        void onAudioTrackSelected(int index);

        void onCcTrackSelected(int index);
    }
}
