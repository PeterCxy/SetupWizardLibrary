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

public class ItemTest extends AndroidTestCase {

    private TextView mTitleView;
    private TextView mSummaryView;

    @SmallTest
    public void testOnBindView() {
        Item item = new TestItem(mContext);
        View view = createLayout();

        item.onBindView(view);

        assertEquals("Title should be \"TestTitle\"", "TestTitle", mTitleView.getText().toString());
        assertEquals("Summary should be \"TestSummary\"", "TestSummary",
                mSummaryView.getText().toString());
    }

    @SmallTest
    public void testSingleLineItem() {
        Item item = new SingleLineTestItem(mContext);
        View view = createLayout();

        item.onBindView(view);

        assertEquals("Title should be \"TestTitle\"", "TestTitle", mTitleView.getText().toString());
        assertEquals("Summary should be gone", View.GONE, mSummaryView.getVisibility());
    }

    private ViewGroup createLayout() {
        ViewGroup root = new FrameLayout(mContext);

        mTitleView = new TextView(mContext);
        mTitleView.setId(R.id.suw_items_title);
        root.addView(mTitleView);
        mSummaryView = new TextView(mContext);
        mSummaryView.setId(R.id.suw_items_summary);
        root.addView(mSummaryView);

        return root;
    }

    private static class TestItem extends Item {

        public TestItem(Context context) {
            super(context, null);
        }

        @Override
        public CharSequence getTitle() {
            return "TestTitle";
        }

        @Override
        public CharSequence getSummary() {
            return "TestSummary";
        }
    }

    private static class SingleLineTestItem extends Item {

        public SingleLineTestItem(Context context) {
            super(context, null);
        }

        @Override
        public CharSequence getTitle() {
            return "TestTitle";
        }
    }
}
