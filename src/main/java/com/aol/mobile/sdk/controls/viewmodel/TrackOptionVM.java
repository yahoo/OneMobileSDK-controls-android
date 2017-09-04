package com.aol.mobile.sdk.controls.viewmodel;

import android.support.annotation.Nullable;

public class TrackOptionVM {
    @Nullable
    public String title;
    public boolean isSelected;

    public TrackOptionVM(@Nullable String title, boolean isSelected) {
        this.title = title;
        this.isSelected = isSelected;
    }
}
