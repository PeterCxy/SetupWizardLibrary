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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.setupwizardlib.items.ItemAdapter;
import com.android.setupwizardlib.items.ItemGroup;
import com.android.setupwizardlib.items.ItemInflater;

/**
 * A GLIF themed layout with a ListView. {@code android:entries} can also be used to specify an
 * {@link com.android.setupwizardlib.items.ItemHierarchy} to be used with this layout in XML.
 */
public class GlifListLayout extends GlifLayout {

    private static final String TAG = "GlifListLayout";
    private ListView mListView;

    public GlifListLayout(Context context) {
        this(context, 0, 0);
    }

    public GlifListLayout(Context context, int template) {
        this(context, template, 0);
    }

    public GlifListLayout(Context context, int template, int containerId) {
        super(context, template, containerId);
        init(context, null, 0);
    }

    public GlifListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public GlifListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuwGlifListLayout,
                defStyleAttr, 0);
        final int xml = a.getResourceId(R.styleable.SuwGlifListLayout_android_entries, 0);
        if (xml != 0) {
            final ItemGroup inflated = (ItemGroup) new ItemInflater(context).inflate(xml);
            setAdapter(new ItemAdapter(inflated));
        }
        a.recycle();
    }

    @Override
    protected View onInflateTemplate(LayoutInflater inflater, int template) {
        if (template == 0) {
            template = R.layout.suw_glif_list_template;
        }
        return super.onInflateTemplate(inflater, template);
    }

    @Override
    protected ViewGroup findContainer(int containerId) {
        if (containerId == 0) {
            containerId = android.R.id.list;
        }
        return super.findContainer(containerId);
    }

    @Override
    protected void onTemplateInflated() {
        mListView = (ListView) findViewById(android.R.id.list);
    }

    public ListView getListView() {
        return mListView;
    }

    public void setAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    public ListAdapter getAdapter() {
        return getListView().getAdapter();
    }
}
