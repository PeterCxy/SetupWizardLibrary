/*
 * Copyright (C) 2016 The Android Open Source Project
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
import android.os.Build;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.setupwizardlib.util.ListViewRequireScrollHelper;
import com.android.setupwizardlib.view.NavigationBar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ListViewRequireScrollHelperTest {

    private TestListView mListView;
    private NavigationBar mNavigationBar;

    @Before
    public void setUp() throws Exception {
        mListView = new TestListView(InstrumentationRegistry.getTargetContext());
        mNavigationBar = new TestNavigationBar(InstrumentationRegistry.getTargetContext());

        mListView.layout(0, 0, 50, 50);
    }

    @Test
    public void testRequireScroll() throws Throwable {
        ListViewRequireScrollHelper.requireScroll(mNavigationBar, mListView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            assertEquals("More button should be shown initially", View.VISIBLE,
                    mNavigationBar.getMoreButton().getVisibility());
            assertEquals("Next button should be gone initially", View.GONE,
                    mNavigationBar.getNextButton().getVisibility());
        }
    }

    @Test
    public void testScrolledToBottom() throws Throwable {
        ListViewRequireScrollHelper.requireScroll(mNavigationBar, mListView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            SystemClock.sleep(500);
            assertEquals("More button should be shown when scroll is required", View.VISIBLE,
                    mNavigationBar.getMoreButton().getVisibility());
            assertEquals("Next button should not be shown when scroll is required", View.GONE,
                    mNavigationBar.getNextButton().getVisibility());

            InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    mListView.lastVisiblePosition = 20;
                    mListView.listener.onScroll(mListView, 2, 20, 20);
                }
            });
            SystemClock.sleep(500);
            assertEquals("More button should be hidden when scrolled to bottom", View.GONE,
                    mNavigationBar.getMoreButton().getVisibility());
            assertEquals("Next button should be shown when scrolled to bottom", View.VISIBLE,
                    mNavigationBar.getNextButton().getVisibility());
        }
    }

    @Test
    public void testClickScrollButton() throws Throwable {
        ListViewRequireScrollHelper.requireScroll(mNavigationBar, mListView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            assertEquals("ScrollView page should be initially 0", 0, mListView.scrollDistance);
            mNavigationBar.getMoreButton().performClick();
            assertEquals("ScrollView page should be scrolled by 50px",
                    50, mListView.scrollDistance);
        }
    }

    private static class TestListView extends ListView {

        public int lastVisiblePosition = 0;
        public int scrollDistance = 0;
        public OnScrollListener listener;

        public TestListView(Context context) {
            super(context);
            setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return 20;
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    return new View(parent.getContext());
                }
            });
        }

        @Override
        public int getLastVisiblePosition() {
            return lastVisiblePosition;
        }

        @Override
        public void smoothScrollBy(int distance, int duration) {
            super.smoothScrollBy(distance, duration);
            scrollDistance += distance;
        }

        @Override
        public void setOnScrollListener(OnScrollListener l) {
            super.setOnScrollListener(l);
            listener = l;
        }
    }

    private static class TestNavigationBar extends NavigationBar {

        public TestNavigationBar(Context context) {
            super(context);
        }

        @Override
        public boolean post(Runnable action) {
            // Make the post action synchronous
            action.run();
            return true;
        }
    }
}
