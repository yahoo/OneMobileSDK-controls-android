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

package com.aol.mobile.sdk.controls.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewDebug;

public final class VisibilityWrapper<T extends View> {
    @NonNull
    public final T view;
    @Nullable
    private VisibilityListener visibilityListener;

    public VisibilityWrapper(@NonNull T view) {
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
