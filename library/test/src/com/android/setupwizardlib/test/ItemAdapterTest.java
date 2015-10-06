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

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.setupwizardlib.R;
import com.android.setupwizardlib.items.Item;
import com.android.setupwizardlib.items.ItemAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ItemAdapterTest extends AndroidTestCase {

    private Item[] mItems;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mItems = new Item[]{
                new TestItem(mContext, 1),
                new TestItem(mContext, 2),
                new TestItem(mContext, 3)
        };
    }

    @SmallTest
    public void testAdapter() {
        ItemAdapter adapter = new ItemAdapter(mItems);
        assertEquals("Adapter should have 3 items", 3, adapter.getCount());
        assertEquals("Adapter should return the first item", mItems[0], adapter.getItem(0));
        assertEquals("ID should be same as position", 2, adapter.getItemId(2));

        // Each test item has its own layout resource, and therefore its own view type
        assertEquals("Should have 3 different view types", 3, adapter.getViewTypeCount());
        HashSet<Integer> viewTypes = new HashSet<>(3);
        viewTypes.add(adapter.getItemViewType(0));
        viewTypes.add(adapter.getItemViewType(1));
        viewTypes.add(adapter.getItemViewType(2));

        assertEquals("View types should be 0, 1, 2", new HashSet<>(Arrays.asList(0, 1, 2)), viewTypes);
    }

    private static class TestItem extends Item {

        private int mNum;

        public TestItem(Context context, int num) {
            super(context, null);
            mNum = num;
        }

        @Override
        public int getLayoutResource() {
            return mNum * 10;
        }

        @Override
        public CharSequence getTitle() {
            return "TestTitle" + mNum;
        }

        @Override
        public CharSequence getSummary() {
            return "TestSummary" + mNum;
        }
    }
}
