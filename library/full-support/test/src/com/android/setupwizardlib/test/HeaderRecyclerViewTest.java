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

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.android.setupwizardlib.view.HeaderRecyclerView.HeaderAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for {@link com.android.setupwizardlib.view.HeaderRecyclerView}
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class HeaderRecyclerViewTest {

    private TestAdapter mWrappedAdapter;
    private HeaderAdapter mHeaderAdapter;

    private TestDataObserver mObserver;

    @Before
    public void setUp() {
        mWrappedAdapter = new TestAdapter();
        mObserver = new TestDataObserver();

        mHeaderAdapter = new HeaderAdapter(mWrappedAdapter);
        mHeaderAdapter.registerAdapterDataObserver(mObserver);
    }

    /**
     * Test that notifyDataSetChanged gets propagated by HeaderRecyclerView's adapter.
     */
    @Test
    public void testNotifyChanged() {
        mWrappedAdapter.notifyDataSetChanged();

        assertEquals("onChanged", mObserver.lastNotification);
    }

    /**
     * Test that notifyItemChanged gets propagated by HeaderRecyclerView's adapter.
     */
    @Test
    public void testNotifyItemChangedNoHeader() {
        mWrappedAdapter.notifyItemChanged(12);

        assertEquals("onItemRangeChanged", mObserver.lastNotification);
        assertEquals(12, mObserver.lastArg1);
        assertEquals(1, mObserver.lastArg2);
    }

    /**
     * Test that notifyItemChanged gets propagated by HeaderRecyclerView's adapter and adds 1 to the
     * position for the extra header items.
     */
    @Test
    public void testNotifyItemChangedWithHeader() {
        mHeaderAdapter.setHeader(new View(InstrumentationRegistry.getTargetContext()));
        mWrappedAdapter.notifyItemChanged(12);

        assertEquals("onItemRangeChanged", mObserver.lastNotification);
        assertEquals(13, mObserver.lastArg1);
        assertEquals(1, mObserver.lastArg2);
    }

    /**
     * Test that notifyItemInserted gets propagated by HeaderRecyclerView's adapter.
     */
    @Test
    public void testNotifyItemInsertedNoHeader() {
        mWrappedAdapter.notifyItemInserted(12);

        assertEquals("onItemRangeInserted", mObserver.lastNotification);
        assertEquals(12, mObserver.lastArg1);
        assertEquals(1, mObserver.lastArg2);
    }

    /**
     * Test that notifyItemInserted gets propagated by HeaderRecyclerView's adapter and adds 1 to
     * the position for the extra header item.
     */
    @Test
    public void testNotifyItemInsertedWithHeader() {
        mHeaderAdapter.setHeader(new View(InstrumentationRegistry.getTargetContext()));
        mWrappedAdapter.notifyItemInserted(12);

        assertEquals("onItemRangeInserted", mObserver.lastNotification);
        assertEquals(13, mObserver.lastArg1);
        assertEquals(1, mObserver.lastArg2);
    }

    /**
     * Test that notifyItemRemoved gets propagated by HeaderRecyclerView's adapter.
     */
    @Test
    public void testNotifyItemRemovedNoHeader() {
        mWrappedAdapter.notifyItemRemoved(12);

        assertEquals("onItemRangeRemoved", mObserver.lastNotification);
        assertEquals(12, mObserver.lastArg1);
        assertEquals(1, mObserver.lastArg2);
    }

    /**
     * Test that notifyItemRemoved gets propagated by HeaderRecyclerView's adapter and adds 1 to
     * the position for the extra header item.
     */
    @Test
    public void testNotifyItemRemovedWithHeader() {
        mHeaderAdapter.setHeader(new View(InstrumentationRegistry.getTargetContext()));
        mWrappedAdapter.notifyItemRemoved(12);

        assertEquals("onItemRangeRemoved", mObserver.lastNotification);
        assertEquals(13, mObserver.lastArg1);
        assertEquals(1, mObserver.lastArg2);
    }

    /**
     * Test that notifyItemMoved gets propagated by HeaderRecyclerView's adapter.
     */
    @Test
    public void testNotifyItemMovedNoHeader() {
        mWrappedAdapter.notifyItemMoved(12, 18);

        assertEquals("onItemRangeMoved", mObserver.lastNotification);
        assertEquals(12, mObserver.lastArg1);
        assertEquals(18, mObserver.lastArg2);
        assertEquals(1, mObserver.lastArg3);
    }

    /**
     * Test that notifyItemMoved gets propagated by HeaderRecyclerView's adapter and adds 1 to
     * the position for the extra header item.
     */
    @Test
    public void testNotifyItemMovedWithHeader() {
        mHeaderAdapter.setHeader(new View(InstrumentationRegistry.getTargetContext()));
        mWrappedAdapter.notifyItemMoved(12, 18);

        assertEquals("onItemRangeMoved", mObserver.lastNotification);
        assertEquals(13, mObserver.lastArg1);
        assertEquals(19, mObserver.lastArg2);
        assertEquals(1, mObserver.lastArg3);
    }

    /**
     * Test adapter to be wrapped inside {@link HeaderAdapter} to to send item change notifications.
     */
    public static class TestAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    /**
     * Test observer which stores the last observed notification.
     */
    // TODO: set up mockito for this instead
    public static class TestDataObserver extends RecyclerView.AdapterDataObserver {

        public String lastNotification;
        public int lastArg1 = -1;
        public int lastArg2 = -1;
        public int lastArg3 = -1;

        @Override
        public void onChanged() {
            super.onChanged();
            lastNotification = "onChanged";
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            lastNotification = "onItemRangeChanged";
            lastArg1 = positionStart;
            lastArg2 = itemCount;
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            lastNotification = "onItemRangeInserted";
            lastArg1 = positionStart;
            lastArg2 = itemCount;
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            lastNotification = "onItemRangeMoved";
            lastArg1 = fromPosition;
            lastArg2 = toPosition;
            lastArg3 = itemCount;
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            lastNotification = "onItemRangeRemoved";
            lastArg1 = positionStart;
            lastArg2 = itemCount;
        }
    }
}
