package com.aol.mobile.sdk.controls;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

public interface ImageLoader {
    void load(@Nullable String url, @NonNull ImageView imageView);

    void cancelLoad(@NonNull ImageView imageView);
}
