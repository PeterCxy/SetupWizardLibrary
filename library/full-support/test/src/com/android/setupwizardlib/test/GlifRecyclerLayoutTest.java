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
import android.support.v7.widget.RecyclerView;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.setupwizardlib.GlifRecyclerLayout;
import com.android.setupwizardlib.view.HeaderRecyclerView;

public class GlifRecyclerLayoutTest extends InstrumentationTestCase {

    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = new ContextThemeWrapper(getInstrumentation().getContext(),
                R.style.SuwThemeGlif_Light);
    }

    @SmallTest
    public void testDefaultTemplate() {
        GlifRecyclerLayout layout = new TestLayout(mContext);
        assertRecyclerTemplateInflated(layout);
    }

    @SmallTest
    public void testInflateFromXml() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        GlifRecyclerLayout layout = (GlifRecyclerLayout)
                inflater.inflate(R.layout.test_glif_recycler_layout, null);
        assertRecyclerTemplateInflated(layout);
    }

    @SmallTest
    public void testGetRecyclerView() {
        GlifRecyclerLayout layout = new TestLayout(mContext);
        assertRecyclerTemplateInflated(layout);
        assertNotNull("getRecyclerView should not be null", layout.getRecyclerView());
    }

    @SmallTest
    public void testAdapter() {
        GlifRecyclerLayout layout = new TestLayout(mContext);
        assertRecyclerTemplateInflated(layout);

        final RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
                return new RecyclerView.ViewHolder(new View(parent.getContext())) {};
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
        layout.setAdapter(adapter);

        final RecyclerView.Adapter gotAdapter = layout.getAdapter();
        if (gotAdapter instanceof HeaderRecyclerView.HeaderAdapter) {
            assertSame("Adapter got from GlifRecyclerLayout should be same as set",
                    adapter, ((HeaderRecyclerView.HeaderAdapter) gotAdapter).getWrappedAdapter());
        } else {
            assertSame("Adapter got from GlifRecyclerLayout should be same as set",
                    adapter, gotAdapter);
        }
    }

    private void assertRecyclerTemplateInflated(GlifRecyclerLayout layout) {
        View recyclerView = layout.findViewById(R.id.suw_recycler_view);
        assertTrue("@id/suw_recycler_view should be a RecyclerView",
                recyclerView instanceof RecyclerView);

        if (layout instanceof TestLayout) {
            assertNotNull("Header text view should not be null",
                    ((TestLayout) layout).getHeaderTextView());
            assertNotNull("Icon view should not be null", ((TestLayout) layout).getIconView());
        }
    }

    // Make some methods public for testing
    public static class TestLayout extends GlifRecyclerLayout {

        public TestLayout(Context context) {
            super(context);
        }

        @Override
        public TextView getHeaderTextView() {
            return super.getHeaderTextView();
        }

        @Override
        public ImageView getIconView() {
            return super.getIconView();
        }
    }
}
