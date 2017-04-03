/*
 * Copyright (c) 2016 One by Aol : Publishers. All rights reserved.
 */

package com.aol.mobile.sdk.controls;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.aol.mobile.sdk.controls.viewmodel.AdControlsVM;

public interface AdControls {
    void setListener(@Nullable Listener listener);

    @NonNull
    View getView();

    void render(@NonNull AdControlsVM adControlsVM);

    interface Listener {
        void onButtonClick(@NonNull AdControlsButton button);

        void onTap();
    }
}
