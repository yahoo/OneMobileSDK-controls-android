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

package com.aol.mobile.sdk.controls.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.aol.mobile.sdk.annotations.PublicApi;

import java.util.ArrayList;
import java.util.List;

@PublicApi
public class SidePanel extends LinearLayout {

    private final static int ANIMATION_DURATION = 200;
    private final static int ANIMATION_DELAY = 50;
    @NonNull
    private List<ValueAnimator> animators = new ArrayList<>();

    public SidePanel(@NonNull Context context) {
        this(context, null);
    }

    public SidePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void cleanAnimators() {
        if (animators.size() > 0) {
            for (ValueAnimator animator : animators) {
                animator.cancel();
            }
            animators.clear();
        }
    }

    public void show() {
        cleanAnimators();
        for (int i = 0; i < getChildCount(); i++) {
            final View view = getChildAt(i);
            ValueAnimator animator = ValueAnimator.ofFloat(getWidth(), 0);
            animators.add(animator);
            animator.setDuration(ANIMATION_DURATION);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    view.setX((Float) animation.getAnimatedValue());
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    view.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            animator.setStartDelay(i * ANIMATION_DELAY);
            animator.start();
        }
    }

    public void hide() {
        cleanAnimators();
        for (int i = 0; i < getChildCount(); i++) {
            final View view = getChildAt(i);
            ValueAnimator animator = ValueAnimator.ofFloat(0, getWidth());
            animators.add(animator);
            animator.setDuration(ANIMATION_DURATION);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    view.setX((Float) animation.getAnimatedValue());
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setVisibility(INVISIBLE);
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    view.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            animator.setStartDelay(i * ANIMATION_DELAY);
            animator.start();
        }
    }
}
