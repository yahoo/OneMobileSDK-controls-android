/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view

import android.app.Activity
import android.widget.*
import com.aol.mobile.sdk.controls.ContentControls
import com.aol.mobile.sdk.controls.R
import org.assertj.android.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class DefaultControlsViewTest {
    private lateinit var controller: ActivityController<Activity>
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var replayButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var seekerContainer: RelativeLayout
    private lateinit var progressbar: ProgressBar
    private lateinit var seeker: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var duration: TextView
    private lateinit var props: ContentControls.ViewModel
    private lateinit var controlsView: ContentControlsView
    private lateinit var listener: ContentControls.Listener

    @Before
    fun tearUp() {
        listener = mock(ContentControls.Listener::class.java)
        controller = Robolectric.buildActivity(Activity::class.java).create()
        val activity = controller.start().resume().get()
        controlsView = ContentControlsView(activity)
        activity.setContentView(controlsView)

        playButton = controlsView.findViewById(R.id.play_button)
        pauseButton = controlsView.findViewById(R.id.pause_button)
        replayButton = controlsView.findViewById(R.id.replay_button)
        prevButton = controlsView.findViewById(R.id.prev_button)
        nextButton = controlsView.findViewById(R.id.next_button)
        seekerContainer = controlsView.findViewById(R.id.seekbar_container)
        progressbar = controlsView.findViewById(R.id.progressbar)
        seeker = controlsView.findViewById(R.id.seekbar)
        currentTime = controlsView.findViewById(R.id.current_time)
        duration = controlsView.findViewById(R.id.duration)

        props = ContentControls.ViewModel()
    }

    @After
    fun tearDown() {
        controller.stop()
    }

    @Test
    fun testInitState() {
        Assertions.assertThat(playButton).isNotVisible
        Assertions.assertThat(pauseButton).isNotVisible
        Assertions.assertThat(replayButton).isNotVisible
        Assertions.assertThat(seekerContainer).isNotVisible
        Assertions.assertThat(progressbar).isNotVisible

        Assertions.assertThat(prevButton).isNotVisible
        Assertions.assertThat(prevButton).isDisabled
        Assertions.assertThat(prevButton).hasAlpha(.3f)

        Assertions.assertThat(nextButton).isNotVisible
        Assertions.assertThat(nextButton).isDisabled
        Assertions.assertThat(nextButton).hasAlpha(.3f)
    }

    @Test
    fun testShowProgressbar() {
        props.isLoading = false
        controlsView.render(props)
        Assertions.assertThat(progressbar).isNotVisible

        props.isLoading = true
        controlsView.render(props)
        Assertions.assertThat(progressbar).isVisible
    }

    @Test
    fun testShowSeeker() {
        props.isSeekerVisible = false
        controlsView.render(props)
        Assertions.assertThat(seekerContainer).isNotVisible

        props.isSeekerVisible = true
        controlsView.render(props)
        Assertions.assertThat(seekerContainer).isVisible
        Assertions.assertThat(currentTime).isVisible
        Assertions.assertThat(duration).isVisible
    }

    @Test
    fun testShowPlayButton() {
        props.isPlayButtonVisible = false
        controlsView.render(props)
        Assertions.assertThat(playButton).isNotVisible

        props.isPlayButtonVisible = true
        controlsView.render(props)
        Assertions.assertThat(playButton).isVisible
    }

    @Test
    fun testShowPauseButton() {
        props.isPauseButtonVisible = false
        controlsView.render(props)
        Assertions.assertThat(pauseButton).isNotVisible

        props.isPauseButtonVisible = true
        controlsView.render(props)
        Assertions.assertThat(pauseButton).isVisible
    }

    @Test
    fun testShowReplayButton() {
        props.isReplayButtonVisible = false
        controlsView.render(props)
        Assertions.assertThat(replayButton).isNotVisible

        props.isReplayButtonVisible = true
        controlsView.render(props)
        Assertions.assertThat(replayButton).isVisible
    }

    @Test
    fun testShowNextButton() {
        props.isNextButtonEnabled = false
        controlsView.render(props)
        Assertions.assertThat(nextButton).isNotVisible
        Assertions.assertThat(nextButton).isDisabled
        Assertions.assertThat(nextButton).hasAlpha(.3f)

        props.isNextButtonEnabled = true
        controlsView.render(props)
        Assertions.assertThat(nextButton).isNotVisible
        Assertions.assertThat(nextButton).isEnabled
        Assertions.assertThat(nextButton).hasAlpha(1f)
    }

    @Test
    fun testShowPreviousButton() {
        props.isPrevButtonEnabled = false
        controlsView.render(props)
        Assertions.assertThat(prevButton).isNotVisible
        Assertions.assertThat(prevButton).isDisabled
        Assertions.assertThat(prevButton).hasAlpha(.3f)

        props.isPrevButtonEnabled = true
        controlsView.render(props)
        Assertions.assertThat(prevButton).isNotVisible
        Assertions.assertThat(prevButton).isEnabled
        Assertions.assertThat(prevButton).hasAlpha(1f)
    }

    @Test
    fun testShowDuration() {
        props.isSeekerVisible = true
        props.seekerDurationText = "00:00"
        controlsView.render(props)
        Assertions.assertThat(duration).isVisible.hasText("00:00")

        props.seekerDurationText = "100:00"
        controlsView.render(props)
        Assertions.assertThat(duration).isVisible.hasText("100:00")
    }

    @Test
    fun testShowCurrentTime() {
        props.isSeekerVisible = true
        props.seekerCurrentTimeText = "0e0:00"
        controlsView.render(props)
        Assertions.assertThat(currentTime).isVisible.hasText("0e0:00")

        props.seekerCurrentTimeText = "--00:00"
        controlsView.render(props)
        Assertions.assertThat(currentTime).isVisible.hasText("--00:00")
    }

    @Test
    fun testSetSeekerProgress() {
        props.isSeekerVisible = true
        props.seekerProgress = .4
        props.seekerMaxValue = 100
        controlsView.render(props)

        Assertions.assertThat(seeker).isVisible.hasProgress(40)
    }

    @Test
    fun playButtonShouldRequestFocusAfterPause() {
        controlsView.setListener(listener)

        val spyPlay = spy(controlsView.playButton.view)
        controlsView.playButton.view = spyPlay
        pauseButton.callOnClick()

        Mockito.verify(spyPlay, Mockito.times(1)).requestFocus()
    }

    @Test
    fun pauseButtonShouldRequestFocusAfterPlay() {
        controlsView.setListener(listener)

        val spyPause = spy(controlsView.pauseButton.view)
        controlsView.pauseButton.view = spyPause
        playButton.callOnClick()

        Mockito.verify(spyPause, Mockito.times(1)).requestFocus()
    }
}
