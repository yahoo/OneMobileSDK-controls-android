/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewDebug;

public final class VisibilityWrapper<T extends View> {
    @NonNull
    /*package private*/ T view;
    @Nullable
    private VisibilityListener visibilityListener;

    /*package private*/  VisibilityWrapper(@NonNull T view) {
        this.view = view;
    }

    public void setOnClickListener(@NonNull View.OnClickListener clickListener) {
        view.setOnClickListener(clickListener);
    }

    public void setVisibilityListener(@Nullable VisibilityListener visibilityListener) {
        this.visibilityListener = visibilityListener;
    }

    public boolean isFocused() {
        return view.isFocused();
    }

    @ViewDebug.ExportedProperty(
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "VISIBLE"
            ), @ViewDebug.IntToString(
                    from = 4,
                    to = "INVISIBLE"
            ), @ViewDebug.IntToString(
                    from = 8,
                    to = "GONE"
            )}
    )
    public int getVisibility() {
        return view.getVisibility();
    }

    public void setVisibility(int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
            if (visibilityListener != null) visibilityListener.onVisibilityChanged(visibility);
        }
    }

    public interface VisibilityListener {
        void onVisibilityChanged(int visibility);
    }
}
