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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.setupwizardlib.R;
import com.android.setupwizardlib.items.SwitchItem;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SwitchItemTest {

    private SwitchCompat mSwitch;

    @Test
    public void testChecked() {
        SwitchItem item = new SwitchItem();
        item.setTitle("TestTitle");
        item.setSummary("TestSummary");
        View view = createLayout();

        item.setChecked(true);

        item.onBindView(view);

        assertTrue("Switch should be checked", mSwitch.isChecked());
    }

    @Test
    public void testNotChecked() {
        SwitchItem item = new SwitchItem();
        item.setTitle("TestTitle");
        item.setSummary("TestSummary");
        View view = createLayout();

        item.setChecked(false);

        item.onBindView(view);

        assertFalse("Switch should be unchecked", mSwitch.isChecked());
    }

    @Test
    public void testListener() {
        SwitchItem item = new SwitchItem();
        item.setTitle("TestTitle");
        item.setSummary("TestSummary");
        View view = createLayout();

        item.setChecked(true);

        final TestOnCheckedChangeListener listener = new TestOnCheckedChangeListener();
        item.setOnCheckedChangeListener(listener);

        item.onBindView(view);

        assertTrue("Switch should be checked", mSwitch.isChecked());
        mSwitch.setChecked(false);

        assertTrue("Listener should be called", listener.called);
        assertFalse("Listener should not be checked", listener.checked);

        mSwitch.setChecked(true);

        assertTrue("Listener should be called", listener.called);
        assertTrue("Listener should be checked", listener.checked);
    }

    @Test
    public void testRebind() {
        SwitchItem item1 = new SwitchItem();
        item1.setTitle("TestTitle1");
        item1.setSummary("TestSummary1");
        item1.setChecked(false);

        SwitchItem item2 = new SwitchItem();
        item2.setTitle("TestTitle2");
        item2.setSummary("TestSummary2");
        item2.setChecked(true);

        View view = createLayout();

        item1.onBindView(view);
        item2.onBindView(view);

        // Switch should be bound to item2, and therefore checked
        assertTrue("Switch should be checked", mSwitch.isChecked());

        // Switching the switch to false should change the checked state of item 2 only
        mSwitch.setChecked(false);
        assertFalse("Item1 should still be unchecked", item1.isChecked());
        assertFalse("Item2 should not be checked", item2.isChecked());

        // Switching the switch to true should change the checked state of item 2 only
        mSwitch.setChecked(true);
        assertFalse("Item1 should still be unchecked", item1.isChecked());
        assertTrue("Item2 should be checked", item2.isChecked());
    }

    @Test
    public void testListenerSetChecked() {
        // Check that calling setChecked on the item will also call the listener.

        SwitchItem item = new SwitchItem();
        item.setTitle("TestTitle");
        item.setSummary("TestSummary");
        View view = createLayout();

        item.setChecked(true);

        final TestOnCheckedChangeListener listener = new TestOnCheckedChangeListener();
        item.setOnCheckedChangeListener(listener);

        item.onBindView(view);

        assertTrue("Switch should be checked", mSwitch.isChecked());
        item.setChecked(false);

        assertTrue("Listener should be called", listener.called);
        assertFalse("Listener should not be checked", listener.checked);

        item.setChecked(true);

        assertTrue("Listener should be called", listener.called);
        assertTrue("Listener should be checked", listener.checked);
    }

    @Test
    public void testToggle() {
        SwitchItem item = new SwitchItem();
        item.setTitle("TestTitle");
        item.setSummary("TestSummary");
        View view = createLayout();

        item.setChecked(true);
        item.onBindView(view);

        assertTrue("Switch should be checked", mSwitch.isChecked());

        item.toggle(view);

        assertFalse("Switch should be unchecked", mSwitch.isChecked());
    }

    private ViewGroup createLayout() {
        Context context = InstrumentationRegistry.getContext();
        ViewGroup root = new FrameLayout(context);

        TextView titleView = new TextView(context);
        titleView.setId(R.id.suw_items_title);
        root.addView(titleView);

        TextView summaryView = new TextView(context);
        summaryView.setId(R.id.suw_items_summary);
        root.addView(summaryView);

        FrameLayout iconContainer = new FrameLayout(context);
        iconContainer.setId(R.id.suw_items_icon_container);
        root.addView(iconContainer);

        ImageView iconView = new ImageView(context);
        iconView.setId(R.id.suw_items_icon);
        iconContainer.addView(iconView);

        mSwitch = new SwitchCompat(context);
        mSwitch.setId(R.id.suw_items_switch);
        root.addView(mSwitch);

        return root;
    }

    private static class TestOnCheckedChangeListener implements SwitchItem.OnCheckedChangeListener {

        public boolean called = false;
        public boolean checked = false;

        @Override
        public void onCheckedChange(SwitchItem item, boolean isChecked) {
            called = true;
            checked = isChecked;
        }
    }
}
