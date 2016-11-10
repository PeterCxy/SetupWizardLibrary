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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.setupwizardlib.GlifRecyclerLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class GlifRecyclerLayoutTest {

    private Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = new ContextThemeWrapper(InstrumentationRegistry.getContext(),
                R.style.SuwThemeGlif_Light);
    }

    @Test
    public void testDefaultTemplate() {
        GlifRecyclerLayout layout = new TestLayout(mContext);
        assertRecyclerTemplateInflated(layout);
    }

    @Test
    public void testInflateFromXml() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        GlifRecyclerLayout layout = (GlifRecyclerLayout)
                inflater.inflate(R.layout.test_glif_recycler_layout, null);
        assertRecyclerTemplateInflated(layout);
    }

    @Test
    public void testGetRecyclerView() {
        GlifRecyclerLayout layout = new TestLayout(mContext);
        assertRecyclerTemplateInflated(layout);
        assertNotNull("getRecyclerView should not be null", layout.getRecyclerView());
    }

    @Test
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
        // Note: The wrapped adapter should be returned, not the HeaderAdapter.
        assertSame("Adapter got from GlifRecyclerLayout should be same as set",
                adapter, gotAdapter);
    }

    @Test
    public void testDividerInset() {
        GlifRecyclerLayout layout = new TestLayout(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        assertRecyclerTemplateInflated(layout);

        layout.setDividerInset(10);
        assertEquals("Divider inset should be 10", 10, layout.getDividerInset());

        final Drawable divider = layout.getDivider();
        assertTrue("Divider should be instance of InsetDrawable", divider instanceof InsetDrawable);
    }

    private void assertRecyclerTemplateInflated(GlifRecyclerLayout layout) {
        View recyclerView = layout.findViewById(R.id.suw_recycler_view);
        assertTrue("@id/suw_recycler_view should be a RecyclerView",
                recyclerView instanceof RecyclerView);

        if (layout instanceof TestLayout) {
            assertNotNull("Header text view should not be null",
                    ((TestLayout) layout).findManagedViewById(R.id.suw_layout_title));
            assertNotNull("Icon view should not be null",
                    ((TestLayout) layout).findManagedViewById(R.id.suw_layout_icon));
        }
    }

    // Make some methods public for testing
    public static class TestLayout extends GlifRecyclerLayout {

        public TestLayout(Context context) {
            super(context);
        }

        @Override
        public View findManagedViewById(int id) {
            return super.findManagedViewById(id);
        }
    }
}
