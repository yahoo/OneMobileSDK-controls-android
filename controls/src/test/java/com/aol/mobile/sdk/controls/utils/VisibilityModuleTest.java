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
