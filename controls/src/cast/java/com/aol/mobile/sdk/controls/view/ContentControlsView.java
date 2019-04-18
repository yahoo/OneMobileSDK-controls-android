/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.MediaRouteButton;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.aol.mobile.sdk.annotations.PublicApi;
import com.aol.mobile.sdk.controls.R;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;

import static com.aol.mobile.sdk.controls.utils.ViewUtils.findView;

@PublicApi
public class ContentControlsView extends AbstractContentControlsView {
    @NonNull
    private final MediaRouteButton castButton;
    @NonNull
    private final ViewGroup castHolder;
    @Nullable
    private SessionManagerListener<CastSession> sessionManagerListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ContentControlsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public ContentControlsView(@NonNull Context context) {
        this(context, null);
    }

    public ContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentControlsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Context appContext = context.getApplicationContext();
        CastContext castContext = CastContext.getSharedInstance(appContext);

        castHolder = findView(this, R.id.cast_placeholder);
        castButton = new MediaRouteButton(context);
        castButton.setRemoteIndicatorDrawable(null);
        castButton.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_cast_button));
        castHolder.addView(castButton);

        CastButtonFactory.setUpMediaRouteButton(appContext, castButton);

        SessionManager sessionManager = castContext.getSessionManager();

        if (sessionManagerListener != null)
            sessionManager.removeSessionManagerListener(sessionManagerListener, CastSession.class);

        sessionManagerListener = new SessionAdapter() {
            @Override
            public void onSessionStarted(CastSession session, String s) {
                if (listener != null) listener.onCastEnabled();
            }

            @Override
            public void onSessionEnded(CastSession session, int i) {
                if (listener != null) listener.onCastDisabled();
            }
        };
        sessionManager.addSessionManagerListener(sessionManagerListener, CastSession.class);

        CastSession session = sessionManager.getCurrentCastSession();
        if (session != null && session.isConnected()) {
            if (listener != null) listener.onCastEnabled();
        }

        updateColors();
    }

    @Override
    public void render(@NonNull ViewModel vm) {
        super.render(vm);
        castHolder.setVisibility(vm.isCastButtonVisible ? VISIBLE : GONE);
    }

    @Override
    protected void updateColors() {
        super.updateColors();
        castButton.getBackground().setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);
    }
}
