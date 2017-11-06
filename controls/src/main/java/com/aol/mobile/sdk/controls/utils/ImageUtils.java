package com.aol.mobile.sdk.controls.utils;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.aol.mobile.sdk.controls.BuildConfig;
import com.aol.mobile.sdk.controls.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.LEFT;

public class ImageUtils {
    public static void loadRemoteResAsDrawable(@NonNull final String remoteSource,
                                               @NonNull final Resources resources,
                                               @NonNull final LoadCallback callback) {
        loadRemoteResAsDrawable(remoteSource, resources, callback, resources.getBoolean(R.bool.isTablet));
    }

    private static void loadRemoteResAsDrawable(@NonNull final String remoteSource,
                                                @NonNull final Resources resources,
                                                @NonNull final LoadCallback callback,
                                                final boolean isTablet) {

        final String url = BuildConfig.REMOTE_CONTROLS_HOST + "drawable" + (isTablet ? "-sw600dp" : "") + "-xxxhdpi/" + remoteSource + ".png";

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Drawable drawable;

                try {
                    drawable = drawableFromUrl(resources, url);
                } catch (IOException e) {
                    if (isTablet) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadRemoteResAsDrawable(remoteSource, resources, callback, false);
                            }
                        });
                    }
                    return;
                }

                if (drawable != null) {
                    final Drawable finalDrawable = drawable;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDrawableLoaded(finalDrawable);
                        }
                    });
                }
            }
        }).start();

    }

    private static Drawable drawableFromUrl(@NonNull Resources resources, @NonNull String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, input);
        bitmapDrawable.setAntiAlias(true);
        bitmapDrawable.setFilterBitmap(true);
        return bitmapDrawable;
    }

    public static void loadSeekerDrawable(@NonNull final String seekerBg,
                                          @NonNull final String seekerProgress,
                                          @NonNull final Resources resources,
                                          @NonNull final LoadCallback loadCallback) {
        final boolean isTablet = resources.getBoolean(R.bool.isTablet);

        loadRemoteResAsDrawable(seekerBg, resources, new LoadCallback() {
            @Override
            public void onDrawableLoaded(@NonNull final Drawable bgDrawable) {
                loadRemoteResAsDrawable(seekerProgress, resources, new LoadCallback() {
                    @Override
                    public void onDrawableLoaded(@NonNull Drawable progressDrawable) {
                        @SuppressLint("RtlHardcoded")
                        LayerDrawable drawable = new LayerDrawable(new Drawable[]{
                                bgDrawable,
                                new ClipDrawable(progressDrawable, CENTER_VERTICAL | LEFT, HORIZONTAL),
                                new ClipDrawable(progressDrawable, CENTER_VERTICAL | LEFT, HORIZONTAL)
                        });
                        drawable.setId(0, android.R.id.background);
                        drawable.setId(1, android.R.id.secondaryProgress);
                        drawable.setId(2, android.R.id.progress);
                        loadCallback.onDrawableLoaded(drawable);
                    }
                }, isTablet);
            }
        }, isTablet);
    }

    public interface LoadCallback {
        void onDrawableLoaded(@NonNull Drawable drawable);
    }
}
