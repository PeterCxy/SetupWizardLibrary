/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.setupwizardlib.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.robolectric.RuntimeEnvironment.application;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.android.setupwizardlib.GlifLayout;
import com.android.setupwizardlib.TemplateLayout;
import com.android.setupwizardlib.robolectric.SuwLibRobolectricTestRunner;
import com.android.setupwizardlib.template.RequireScrollMixin.OnRequireScrollStateChangedListener;
import com.android.setupwizardlib.template.RequireScrollMixin.ScrollHandlingDelegate;
import com.android.setupwizardlib.view.NavigationBar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK})
@RunWith(SuwLibRobolectricTestRunner.class)
public class RequireScrollMixinTest {

  @Mock private ScrollHandlingDelegate delegate;

  private RequireScrollMixin requireScrollMixin;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    TemplateLayout templateLayout = new GlifLayout(application);
    requireScrollMixin = new RequireScrollMixin(templateLayout);
    requireScrollMixin.setScrollHandlingDelegate(delegate);
  }

  @Test
  public void testRequireScroll() {
    requireScrollMixin.requireScroll();

    verify(delegate).startListening();
  }

  @Test
  public void testScrollStateChangedListener() {
    OnRequireScrollStateChangedListener listener = mock(OnRequireScrollStateChangedListener.class);
    requireScrollMixin.setOnRequireScrollStateChangedListener(listener);
    assertFalse(
        "Scrolling should not be required initially", requireScrollMixin.isScrollingRequired());

    requireScrollMixin.notifyScrollabilityChange(true);
    verify(listener).onRequireScrollStateChanged(true);
    assertTrue(
        "Scrolling should be required when there is more content below the fold",
        requireScrollMixin.isScrollingRequired());

    requireScrollMixin.notifyScrollabilityChange(false);
    verify(listener).onRequireScrollStateChanged(false);
    assertFalse(
        "Scrolling should not be required after scrolling to bottom",
        requireScrollMixin.isScrollingRequired());

    // Once the user has scrolled to the bottom, they should not be forced to scroll down again
    requireScrollMixin.notifyScrollabilityChange(true);
    verifyNoMoreInteractions(listener);

    assertFalse(
        "Scrolling should not be required after scrolling to bottom once",
        requireScrollMixin.isScrollingRequired());

    assertSame(listener, requireScrollMixin.getOnRequireScrollStateChangedListener());
  }

  @Test
  public void testCreateOnClickListener() {
    OnClickListener wrappedListener = mock(OnClickListener.class);
    final OnClickListener onClickListener =
        requireScrollMixin.createOnClickListener(wrappedListener);

    requireScrollMixin.notifyScrollabilityChange(true);
    onClickListener.onClick(null);

    verify(wrappedListener, never()).onClick(any(View.class));
    verify(delegate).pageScrollDown();

    requireScrollMixin.notifyScrollabilityChange(false);
    onClickListener.onClick(null);

    verify(wrappedListener).onClick(any(View.class));
  }

  @Test
  public void testRequireScrollWithNavigationBar() {
    final NavigationBar navigationBar = new NavigationBar(application);
    requireScrollMixin.requireScrollWithNavigationBar(navigationBar);

    requireScrollMixin.notifyScrollabilityChange(true);
    assertEquals(
        "More button should be visible",
        View.VISIBLE,
        navigationBar.getMoreButton().getVisibility());
    assertEquals(
        "Next button should be hidden", View.GONE, navigationBar.getNextButton().getVisibility());

    navigationBar.getMoreButton().performClick();
    verify(delegate).pageScrollDown();

    requireScrollMixin.notifyScrollabilityChange(false);
    assertEquals(
        "More button should be hidden", View.GONE, navigationBar.getMoreButton().getVisibility());
    assertEquals(
        "Next button should be visible",
        View.VISIBLE,
        navigationBar.getNextButton().getVisibility());
  }

  @SuppressLint("SetTextI18n") // It's OK for testing
  @Test
  public void testRequireScrollWithButton() {
    final Button button = new Button(application);
    button.setText("OriginalLabel");
    OnClickListener wrappedListener = mock(OnClickListener.class);
    requireScrollMixin.requireScrollWithButton(button, "TestMoreLabel", wrappedListener);

    assertEquals("Button label should be kept initially", "OriginalLabel", button.getText());

    requireScrollMixin.notifyScrollabilityChange(true);
    assertEquals("TestMoreLabel", button.getText());
    button.performClick();
    verify(wrappedListener, never()).onClick(eq(button));
    verify(delegate).pageScrollDown();

    requireScrollMixin.notifyScrollabilityChange(false);
    assertEquals("OriginalLabel", button.getText());
    button.performClick();
    verify(wrappedListener).onClick(eq(button));
  }
}
