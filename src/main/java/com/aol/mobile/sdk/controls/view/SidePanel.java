/*
 * Copyright (c) 2016 One by Aol : Publishers. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

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
