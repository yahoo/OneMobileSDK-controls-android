/*
 * Copyright 2019, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view;

import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;

class SessionAdapter implements SessionManagerListener<CastSession> {
    @Override
    public void onSessionStarting(CastSession castSession) {
    }

    @Override
    public void onSessionStarted(CastSession castSession, String s) {
    }

    @Override
    public void onSessionStartFailed(CastSession castSession, int i) {
    }

    @Override
    public void onSessionEnding(CastSession castSession) {
    }

    @Override
    public void onSessionEnded(CastSession castSession, int i) {
    }

    @Override
    public void onSessionResuming(CastSession castSession, String s) {
    }

    @Override
    public void onSessionResumed(CastSession castSession, boolean b) {
    }

    @Override
    public void onSessionResumeFailed(CastSession castSession, int i) {
    }

    @Override
    public void onSessionSuspended(CastSession castSession, int i) {
    }
}
