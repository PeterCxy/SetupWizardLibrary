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
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.setupwizardlib.GlifListLayout;

public class GlifListLayoutTest extends InstrumentationTestCase {

    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = new ContextThemeWrapper(getInstrumentation().getContext(),
                R.style.SuwThemeGlif_Light);
    }

    @SmallTest
    public void testDefaultTemplate() {
        GlifListLayout layout = new GlifListLayout(mContext);
        assertListTemplateInflated(layout);
    }

    @SmallTest
    public void testAddView() {
        GlifListLayout layout = new GlifListLayout(mContext);
        TextView tv = new TextView(mContext);
        try {
            layout.addView(tv);
            fail("Adding view to ListLayout should throw");
        } catch (UnsupportedOperationException e) {
            // Expected exception
        }
    }

    @SmallTest
    public void testInflateFromXml() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        GlifListLayout layout = (GlifListLayout)
                inflater.inflate(R.layout.test_glif_list_layout, null);
        assertListTemplateInflated(layout);
    }

    @SmallTest
    public void testGetListView() {
        GlifListLayout layout = new GlifListLayout(mContext);
        assertListTemplateInflated(layout);
        assertNotNull("getListView should not be null", layout.getListView());
    }

    @SmallTest
    public void testAdapter() {
        GlifListLayout layout = new GlifListLayout(mContext);
        assertListTemplateInflated(layout);

        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1);
        adapter.add("Abracadabra");
        layout.setAdapter(adapter);

        final ListAdapter gotAdapter = layout.getAdapter();
        if (gotAdapter instanceof HeaderViewListAdapter) {
            assertSame("Adapter got from GlifListLayout should be same as set",
                    adapter, ((HeaderViewListAdapter) gotAdapter).getWrappedAdapter());
        } else {
            assertSame("Adapter got from GlifListLayout should be same as set",
                    adapter, gotAdapter);
        }
    }

    private void assertListTemplateInflated(GlifListLayout layout) {
        View title = layout.findViewById(R.id.suw_layout_title);
        assertNotNull("@id/suw_layout_title should not be null", title);

        View icon = layout.findViewById(R.id.suw_layout_icon);
        assertNotNull("@id/suw_layout_icon should not be null", icon);

        View listView = layout.findViewById(android.R.id.list);
        assertTrue("@android:id/list should be a ListView", listView instanceof ListView);
    }
}
