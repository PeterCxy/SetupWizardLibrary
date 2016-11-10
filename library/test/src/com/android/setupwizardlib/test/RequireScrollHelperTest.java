/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.setupwizardlib.test;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.android.setupwizardlib.util.RequireScrollHelper;
import com.android.setupwizardlib.view.BottomScrollView;
import com.android.setupwizardlib.view.NavigationBar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RequireScrollHelperTest {

    private TestBottomScrollView mScrollView;
    private NavigationBar mNavigationBar;

    @Before
    public void setUp() throws Exception {
        mScrollView = new TestBottomScrollView(InstrumentationRegistry.getContext());
        mNavigationBar = new TestNavigationBar(InstrumentationRegistry.getContext());
    }

    @Test
    public void testRequireScroll() {
        RequireScrollHelper.requireScroll(mNavigationBar, mScrollView);
        assertEquals("More button should be gone initially", View.GONE,
                mNavigationBar.getMoreButton().getVisibility());
        assertEquals("Next button should be shown", View.VISIBLE,
                mNavigationBar.getNextButton().getVisibility());

        mScrollView.listener.onRequiresScroll();
        assertEquals("More button should be shown when scroll is required", View.VISIBLE,
                mNavigationBar.getMoreButton().getVisibility());
        assertEquals("Next button should not be shown when scroll is required", View.GONE,
                mNavigationBar.getNextButton().getVisibility());
    }

    @Test
    public void testScrolledToBottom() {
        RequireScrollHelper.requireScroll(mNavigationBar, mScrollView);
        mScrollView.listener.onRequiresScroll();
        assertEquals("More button should be shown when scroll is required", View.VISIBLE,
                mNavigationBar.getMoreButton().getVisibility());
        assertEquals("Next button should not be shown when scroll is required", View.GONE,
                mNavigationBar.getNextButton().getVisibility());

        mScrollView.listener.onScrolledToBottom();
        assertEquals("More button should be hidden when scrolled to bottom", View.GONE,
                mNavigationBar.getMoreButton().getVisibility());
        assertEquals("Next button should be shown when scrolled to bottom", View.VISIBLE,
                mNavigationBar.getNextButton().getVisibility());
    }

    @Test
    public void testClickScrollButton() {
        RequireScrollHelper.requireScroll(mNavigationBar, mScrollView);
        assertEquals("ScrollView page should be initially 0", 0, mScrollView.page);
        mScrollView.listener.onRequiresScroll();
        mNavigationBar.getMoreButton().performClick();
        assertEquals("ScrollView page should be scrolled by 1", 1, mScrollView.page);
    }

    private static class TestBottomScrollView extends BottomScrollView {

        public BottomScrollListener listener;
        public int page = 0;

        public TestBottomScrollView(Context context) {
            super(context);
        }

        @Override
        public void setBottomScrollListener(BottomScrollListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean pageScroll(int direction) {
            if (direction == FOCUS_DOWN) {
                page++;
            } else if (direction == FOCUS_UP) {
                page--;
            }
            return super.pageScroll(direction);
        }
    }

    private static class TestNavigationBar extends NavigationBar {

        public TestNavigationBar(Context context) {
            super(context);
        }

        @Override
        public boolean post(Runnable action) {
            action.run();
            return true;
        }
    }
}
