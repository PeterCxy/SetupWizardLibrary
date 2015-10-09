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

package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.setupwizardlib.R;

/**
 * Definition of an item in SetupWizardItemsLayout. An item is usually defined in XML and inflated
 * using {@link ItemInflater}.
 */
public class Item {

    private boolean mEnabled = true;
    private Drawable mIcon;
    private int mId = 0;
    private int mLayoutRes;
    private CharSequence mSummary;
    private CharSequence mTitle;

    public Item() {
        mLayoutRes = getDefaultLayoutResource();
    }

    public Item(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuwItem);
        mEnabled = a.getBoolean(R.styleable.SuwItem_android_enabled, true);
        mIcon = a.getDrawable(R.styleable.SuwItem_android_icon);
        mId = a.getResourceId(R.styleable.SuwItem_android_id, 0);
        mTitle = a.getText(R.styleable.SuwItem_android_title);
        mSummary = a.getText(R.styleable.SuwItem_android_summary);
        mLayoutRes = a.getResourceId(R.styleable.SuwItem_android_layout,
                getDefaultLayoutResource());
        a.recycle();
    }

    protected int getDefaultLayoutResource() {
        return R.layout.suw_items_text;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setLayoutResource(int layoutResource) {
        mLayoutRes = layoutResource;
    }

    public int getLayoutResource() {
        return mLayoutRes;
    }

    public void setSummary(CharSequence summary) {
        mSummary = summary;
    }

    public CharSequence getSummary() {
        return mSummary;
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public void onBindView(View view) {
        // TODO: Show icon if defined
        TextView label = (TextView) view.findViewById(R.id.suw_items_title);
        label.setText(getTitle());
        TextView summaryView = (TextView) view.findViewById(R.id.suw_items_summary);
        CharSequence summary = getSummary();
        if (summary != null && summary.length() > 0) {
            summaryView.setText(summary);
            summaryView.setVisibility(View.VISIBLE);
        } else {
            summaryView.setVisibility(View.GONE);
        }
    }
}
