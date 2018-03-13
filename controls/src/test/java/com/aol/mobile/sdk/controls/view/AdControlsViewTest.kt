/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.view

import com.aol.mobile.sdk.controls.AdControls
import com.aol.mobile.sdk.controls.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AdControlsViewTest {
    private lateinit var adControlsView: AdControlsView
    private lateinit var listener: AdControls.Listener
    private val vm = AdControls.ViewModel()

    @Before
    fun before() {
        adControlsView = AdControlsView(RuntimeEnvironment.application)
        listener = mock(AdControls.Listener::class.java)

        adControlsView.setListener(listener)
    }

    @Test
    fun sameAdUrlShouldNotTriggerAnyNewPresentation() {
        vm.adUrl = "some:url"
        vm.embedClickThroughUrl = false
        adControlsView.render(vm)
        verify(listener).onAdPresented()

        adControlsView.render(vm)
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun emptyAdUrlShouldNotTriggerAnyNewPresentation() {
        vm.adUrl = null
        vm.embedClickThroughUrl = false
        adControlsView.render(vm)
        verifyZeroInteractions(listener)
    }

    @Test
    fun listenerShouldBeNotifiedImmediatelyIfAdUrlIsPresentedInActivity() {
        vm.adUrl = "some:url"
        vm.embedClickThroughUrl = false
        adControlsView.render(vm)
        verify(listener).onAdPresented()
    }

    @Test
    fun listenerShouldBeNotifiedWhenAdUrlEmbeddedViewIsGone() {
        vm.adUrl = "some:url"
        vm.embedClickThroughUrl = true
        vm.isCloseButtonVisible = true
        adControlsView.render(vm)
        verifyZeroInteractions(listener)
        adControlsView.findViewById<TintableImageButton>(R.id.clickthrough_close).performClick()
        verify(listener).onAdPresented()
    }
}