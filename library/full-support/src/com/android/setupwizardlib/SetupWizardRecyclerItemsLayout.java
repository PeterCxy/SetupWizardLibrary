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

package com.android.setupwizardlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.android.setupwizardlib.items.ItemGroup;
import com.android.setupwizardlib.items.ItemInflater;
import com.android.setupwizardlib.items.RecyclerItemAdapter;

/**
 * A SetupWizardLayout for use with {@link com.android.setupwizardlib.items.RecyclerItemAdapter},
 * which displays a list of items using a RecyclerView. The items XML file can be specified through
 * {@code android:entries} attribute in the layout.
 *
 * @see com.android.setupwizardlib.SetupWizardItemsLayout
 */
public class SetupWizardRecyclerItemsLayout extends SetupWizardLayout {

    private RecyclerItemAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public SetupWizardRecyclerItemsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SetupWizardRecyclerItemsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SuwSetupWizardItemsLayout, defStyleAttr, 0);
        final int xml = a.getResourceId(R.styleable.SuwSetupWizardItemsLayout_android_entries, 0);
        if (xml != 0) {
            final ItemGroup inflated = (ItemGroup) new ItemInflater(context).inflate(xml);
            mAdapter = new RecyclerItemAdapter(inflated);
            setAdapter(mAdapter);
        }
        a.recycle();
    }

    public RecyclerItemAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecyclerItemAdapter adapter) {
        mAdapter = adapter;
        getRecyclerView().setAdapter(adapter);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected void onTemplateInflated() {
        mRecyclerView = (RecyclerView) findViewById(R.id.suw_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    protected View onInflateTemplate(LayoutInflater inflater, int template) {
        if (template == 0) {
            template = R.layout.suw_recycler_template;
        }
        return super.onInflateTemplate(inflater, template);
    }
}
