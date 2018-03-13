/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls;

import android.app.Activity;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aol.mobile.sdk.controls.view.ContentControlsView;

import org.assertj.android.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class DefaultControlsViewTest {
    private ActivityController<Activity> controller;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton replayButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private RelativeLayout seekerContainer;
    private ProgressBar progressbar;
    private SeekBar seeker;
    private TextView currentTime;
    private TextView duration;
    private ContentControls.ViewModel props;
    private ContentControlsView controlsView;

    @Before
    public void tearUp() {
        controller = Robolectric.buildActivity(Activity.class).create();
        Activity activity = controller.start().resume().get();
        controlsView = new ContentControlsView(activity);
        activity.setContentView(controlsView);

        playButton = controlsView.findViewById(R.id.play_button);
        pauseButton = controlsView.findViewById(R.id.pause_button);
        replayButton = controlsView.findViewById(R.id.replay_button);
        prevButton = controlsView.findViewById(R.id.prev_button);
        nextButton = controlsView.findViewById(R.id.next_button);
        seekerContainer = controlsView.findViewById(R.id.seekbar_container);
        progressbar = controlsView.findViewById(R.id.progressbar);
        seeker = controlsView.findViewById(R.id.seekbar);
        currentTime = controlsView.findViewById(R.id.current_time);
        duration = controlsView.findViewById(R.id.duration);

        props = new ContentControls.ViewModel();
    }

    @After
    public void tearDown() {
        controller.stop();
    }

    @Test
    public void testInitState() throws Exception {
        Assertions.assertThat(playButton).isNotVisible();
        Assertions.assertThat(pauseButton).isNotVisible();
        Assertions.assertThat(replayButton).isNotVisible();
        Assertions.assertThat(seekerContainer).isNotVisible();
        Assertions.assertThat(progressbar).isNotVisible();

        Assertions.assertThat(prevButton).isNotVisible();
        Assertions.assertThat(prevButton).isDisabled();
        Assertions.assertThat(prevButton).hasAlpha(.3f);

        Assertions.assertThat(nextButton).isNotVisible();
        Assertions.assertThat(nextButton).isDisabled();
        Assertions.assertThat(nextButton).hasAlpha(.3f);
    }

    @Test
    public void testShowProgressbar() throws Exception {
        props.isLoading = false;
        controlsView.render(props);
        Assertions.assertThat(progressbar).isNotVisible();

        props.isLoading = true;
        controlsView.render(props);
        Assertions.assertThat(progressbar).isVisible();
    }

    @Test
    public void testShowSeeker() throws Exception {
        props.isSeekerVisible = false;
        controlsView.render(props);
        Assertions.assertThat(seekerContainer).isNotVisible();

        props.isSeekerVisible = true;
        controlsView.render(props);
        Assertions.assertThat(seekerContainer).isVisible();
        Assertions.assertThat(currentTime).isVisible();
        Assertions.assertThat(duration).isVisible();
    }

    @Test
    public void testShowPlayButton() throws Exception {
        props.isPlayButtonVisible = false;
        controlsView.render(props);
        Assertions.assertThat(playButton).isNotVisible();

        props.isPlayButtonVisible = true;
        controlsView.render(props);
        Assertions.assertThat(playButton).isVisible();
    }

    @Test
    public void testShowPauseButton() throws Exception {
        props.isPauseButtonVisible = false;
        controlsView.render(props);
        Assertions.assertThat(pauseButton).isNotVisible();

        props.isPauseButtonVisible = true;
        controlsView.render(props);
        Assertions.assertThat(pauseButton).isVisible();
    }

    @Test
    public void testShowReplayButton() throws Exception {
        props.isReplayButtonVisible = false;
        controlsView.render(props);
        Assertions.assertThat(replayButton).isNotVisible();

        props.isReplayButtonVisible = true;
        controlsView.render(props);
        Assertions.assertThat(replayButton).isVisible();
    }

    @Test
    public void testShowNextButton() throws Exception {
        props.isNextButtonEnabled = false;
        controlsView.render(props);
        Assertions.assertThat(nextButton).isNotVisible();
        Assertions.assertThat(nextButton).isDisabled();
        Assertions.assertThat(nextButton).hasAlpha(.3f);

        props.isNextButtonEnabled = true;
        controlsView.render(props);
        Assertions.assertThat(nextButton).isNotVisible();
        Assertions.assertThat(nextButton).isEnabled();
        Assertions.assertThat(nextButton).hasAlpha(1f);
    }

    @Test
    public void testShowPreviousButton() throws Exception {
        props.isPrevButtonEnabled = false;
        controlsView.render(props);
        Assertions.assertThat(prevButton).isNotVisible();
        Assertions.assertThat(prevButton).isDisabled();
        Assertions.assertThat(prevButton).hasAlpha(.3f);

        props.isPrevButtonEnabled = true;
        controlsView.render(props);
        Assertions.assertThat(prevButton).isNotVisible();
        Assertions.assertThat(prevButton).isEnabled();
        Assertions.assertThat(prevButton).hasAlpha(1f);
    }

    @Test
    public void testShowDuration() throws Exception {
        props.isSeekerVisible = true;
        props.seekerDurationText = "00:00";
        controlsView.render(props);
        Assertions.assertThat(duration).isVisible().hasText("00:00");

        props.seekerDurationText = "100:00";
        controlsView.render(props);
        Assertions.assertThat(duration).isVisible().hasText("100:00");
    }

    @Test
    public void testShowCurrentTime() throws Exception {
        props.isSeekerVisible = true;
        props.seekerCurrentTimeText = "0e0:00";
        controlsView.render(props);
        Assertions.assertThat(currentTime).isVisible().hasText("0e0:00");

        props.seekerCurrentTimeText = "--00:00";
        controlsView.render(props);
        Assertions.assertThat(currentTime).isVisible().hasText("--00:00");
    }

    @Test
    public void testSetSeekerProgress() throws Exception {
        props.isSeekerVisible = true;
        props.seekerProgress = .4;
        props.seekerMaxValue = 100;
        controlsView.render(props);

        Assertions.assertThat(seeker).isVisible().hasProgress(40);
    }
}