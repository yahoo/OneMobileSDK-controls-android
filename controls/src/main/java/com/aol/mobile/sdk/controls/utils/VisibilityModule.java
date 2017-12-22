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

import com.aol.mobile.sdk.controls.view.ContentControlsView;

public final class VisibilityModule {
    @NonNull
    private final ContentControlsView playerControlsView;
    @NonNull
    private Behaviour behaviour;
    private final Behaviour HIDDEN_PLAYING = new BehaviourAdapter() {
        @Override
        public void pause() {
            playerControlsView.cancelTimer();
            playerControlsView.show();
            behaviour = VISIBLE_PAUSED;
        }

        @Override
        public void tap() {
            playerControlsView.startTimer();
            playerControlsView.show();
            behaviour = VISIBLE_PLAYING;
        }
    };
    private final Behaviour VISIBLE_PLAYING = new BehaviourAdapter() {
        @Override
        public void pause() {
            playerControlsView.cancelTimer();
            playerControlsView.show();
            behaviour = VISIBLE_PAUSED;
        }

        @Override
        public void tap() {
            playerControlsView.cancelTimer();
            playerControlsView.hide();
            behaviour = HIDDEN_PLAYING;
        }

        @Override
        public void timeout() {
            playerControlsView.hide();
            behaviour = HIDDEN_PLAYING;
        }

        @Override
        public void prolong() {
            playerControlsView.cancelTimer();
            playerControlsView.startTimer();
        }
    };
    private final Behaviour HIDDEN_PAUSED = new BehaviourAdapter() {
        @Override
        public void play() {
            playerControlsView.startTimer();
            playerControlsView.show();
            behaviour = VISIBLE_PLAYING;
        }

        @Override
        public void tap() {
            playerControlsView.cancelTimer();
            playerControlsView.show();
            behaviour = VISIBLE_PAUSED;
        }
    };
    private final Behaviour VISIBLE_PAUSED = new BehaviourAdapter() {
        @Override
        public void play() {
            playerControlsView.startTimer();
            playerControlsView.show();
            behaviour = VISIBLE_PLAYING;
        }

        @Override
        public void tap() {
            playerControlsView.cancelTimer();
            playerControlsView.hide();
            behaviour = HIDDEN_PAUSED;
        }
    };

    public VisibilityModule(@NonNull ContentControlsView playerControlsView) {
        this.playerControlsView = playerControlsView;
        this.behaviour = VISIBLE_PAUSED;
    }

    public void play() {
        behaviour.play();
    }

    public void pause() {
        behaviour.pause();
    }

    public void tap() {
        behaviour.tap();
    }

    public void timeout() {
        behaviour.timeout();
    }

    public void prolong() {
        behaviour.prolong();
    }

    private interface Behaviour {
        void play();

        void pause();

        void tap();

        void timeout();

        void prolong();
    }

    private class BehaviourAdapter implements Behaviour {
        @Override
        public void play() {
        }

        @Override
        public void pause() {
        }

        @Override
        public void tap() {
        }

        @Override
        public void timeout() {
        }

        @Override
        public void prolong() {
        }
    }
}
