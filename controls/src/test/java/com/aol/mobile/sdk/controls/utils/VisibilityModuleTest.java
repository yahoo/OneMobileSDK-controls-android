/*
 * Copyright (c) 2016 One by Aol : Publishers. All rights reserved.
 */

package com.aol.mobile.sdk.controls.utils;

import com.aol.mobile.sdk.controls.view.ContentControlsView;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VisibilityModuleTest {
    @Test
    public void testStateTravel() throws Exception {
        ContentControlsView playerControlsView = Mockito.mock(ContentControlsView.class);
        VisibilityModule visibilityModule = new VisibilityModule(playerControlsView);
        verify(playerControlsView, times(0)).show();
        verify(playerControlsView, times(0)).hide();

        visibilityModule.play();
        verify(playerControlsView, times(1)).startTimer();
        verify(playerControlsView, times(1)).show();

        visibilityModule.tap();
        verify(playerControlsView, times(1)).cancelTimer();
        verify(playerControlsView, times(1)).hide();

        visibilityModule.tap();
        verify(playerControlsView, times(2)).startTimer();
        verify(playerControlsView, times(2)).show();

        visibilityModule.timeout();
        verify(playerControlsView, times(2)).hide();

        visibilityModule.tap();
        verify(playerControlsView, times(3)).startTimer();
        verify(playerControlsView, times(3)).show();

        visibilityModule.pause();
        verify(playerControlsView, times(2)).cancelTimer();
        verify(playerControlsView, times(4)).show();

        visibilityModule.tap();
        verify(playerControlsView, times(3)).cancelTimer();
        verify(playerControlsView, times(3)).hide();

        visibilityModule.tap();
        verify(playerControlsView, times(4)).cancelTimer();
        verify(playerControlsView, times(5)).show();

        visibilityModule.play();
        verify(playerControlsView, times(4)).startTimer();
        verify(playerControlsView, times(6)).show();

        visibilityModule.tap();
        verify(playerControlsView, times(5)).cancelTimer();
        verify(playerControlsView, times(4)).hide();

        visibilityModule.pause();
        verify(playerControlsView, times(6)).cancelTimer();
        verify(playerControlsView, times(7)).show();
    }
}
