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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SetupWizardLayout extends FrameLayout {

    private static final String TAG = "SetupWizardLayout";

    /**
     * The container of the actual content. This will be a view in the template, which child views
     * will be added to when {@link #addView(android.view.View)} is called. This will be the layout
     * in the template that has the ID of {@link #getContainerId()}. For the default implementation
     * of SetupWizardLayout, that would be @id/suw_layout_content.
     */
    private ViewGroup mContainer;

    public SetupWizardLayout(Context context) {
        this(context, 0);
    }

    public SetupWizardLayout(Context context, int template) {
        super(context);
        inflateTemplate(template);
    }

    public SetupWizardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SetupWizardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.SuwSetupWizardLayout, defStyleAttr, 0);
            final int template = a.getResourceId(
                    R.styleable.SuwSetupWizardLayout_android_layout, 0);
            inflateTemplate(template);
            a.recycle();
        } else {
            inflateTemplate(0);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        mContainer.addView(child, index, params);
    }

    private void addViewInternal(View child) {
        super.addView(child, -1, generateDefaultLayoutParams());
    }

    private void inflateTemplate(int templateResource) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View templateRoot = onInflateTemplate(inflater, templateResource);
        addViewInternal(templateRoot);

        mContainer = (ViewGroup) findViewById(getContainerId());
        onTemplateInflated();
    }

    /**
     * This method inflates the template. Subclasses can override this method to customize the
     * template inflation, or change to a different default template. The root of the inflated
     * layout should be returned, and not added to the view hierarchy.
     *
     * @param inflater A LayoutInflater to inflate the template.
     * @param template The resource ID of the template to be inflated, or 0 if no template is
     *                 specified.
     * @return Root of the inflated layout.
     */
    protected View onInflateTemplate(LayoutInflater inflater, int template) {
        if (template == 0) {
            template = R.layout.suw_template;
        }
        return inflater.inflate(template, this, false);
    }

    /**
     * This is called after the template has been inflated and added to the view hierarchy.
     * Subclasses can implement this method to modify the template as necessary, such as caching
     * views retrieved from findViewById, or other view operations that need to be done in code.
     * You can think of this as {@link android.view.View#onFinishInflate()} but for inflation of the
     * template instead of for child views.
     */
    protected void onTemplateInflated() {
    }

    protected int getContainerId() {
        return R.id.suw_layout_content;
    }
}
