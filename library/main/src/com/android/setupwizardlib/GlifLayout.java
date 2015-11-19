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
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Layout for the GLIF theme used in Setup Wizard for N.
 *
 * <p>Example usage:
 * <pre>{@code
 * &lt;com.android.setupwizardlib.GlifLayout
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     xmlns:app="http://schemas.android.com/apk/res-auto"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:icon="@drawable/my_icon"
 *     app:suwHeaderText="@string/my_title">
 *
 *     &lt;!-- Content here -->
 *
 * &lt;/com.android.setupwizardlib.GlifLayout>
 * }</pre>
 */
public class GlifLayout extends TemplateLayout {

    private static final String TAG = "GlifLayout";

    public GlifLayout(Context context) {
        this(context, 0, 0);
    }

    public GlifLayout(Context context, int template) {
        this(context, template, 0);
    }

    public GlifLayout(Context context, int template, int containerId) {
        super(context, template, containerId);
        init(null, R.attr.suwLayoutTheme);
    }

    public GlifLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.attr.suwLayoutTheme);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public GlifLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    // All the constructors delegate to this init method. The 3-argument constructor is not
    // available in LinearLayout before v11, so call super with the exact same arguments.
    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.SuwGlifLayout, defStyleAttr, 0);

        final Drawable icon = a.getDrawable(R.styleable.SuwGlifLayout_android_icon);
        if (icon != null) {
            setIcon(icon);
        }

        // Set the header color
        final ColorStateList headerColor =
                a.getColorStateList(R.styleable.SuwGlifLayout_suwHeaderColor);
        if (headerColor != null) {
            setHeaderColor(headerColor);
        }


        // Set the header text
        final CharSequence headerText =
                a.getText(R.styleable.SuwGlifLayout_suwHeaderText);
        if (headerText != null) {
            setHeaderText(headerText);
        }

        a.recycle();
    }

    @Override
    protected View onInflateTemplate(LayoutInflater inflater, int template) {
        if (template == 0) {
            template = R.layout.suw_glif_template;
        }
        return super.onInflateTemplate(inflater, template);
    }

    @Override
    protected ViewGroup findContainer(int containerId) {
        if (containerId == 0) {
            containerId = R.id.suw_layout_content;
        }
        return super.findContainer(containerId);
    }

    public ScrollView getScrollView() {
        final View view = findViewById(R.id.suw_scroll_view);
        return view instanceof ScrollView ? (ScrollView) view : null;
    }

    public void setHeaderText(int title) {
        final TextView titleView = (TextView) findViewById(R.id.suw_layout_title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public void setHeaderText(CharSequence title) {
        final TextView titleView = (TextView) findViewById(R.id.suw_layout_title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public CharSequence getHeaderText() {
        final TextView titleView = (TextView) findViewById(R.id.suw_layout_title);
        return titleView != null ? titleView.getText() : null;
    }

    public void setHeaderColor(ColorStateList color) {
        final TextView titleView = (TextView) findViewById(R.id.suw_layout_title);
        if (titleView != null) {
            titleView.setTextColor(color);
        }
    }

    public ColorStateList getHeaderColor() {
        final TextView titleView = (TextView) findViewById(R.id.suw_layout_title);
        return titleView != null ? titleView.getTextColors() : null;
    }

    public void setIcon(Drawable icon) {
        final ImageView iconView = getIconView();
        if (iconView != null) {
            iconView.setImageDrawable(icon);
        }
    }

    public Drawable getIcon() {
        final ImageView iconView = getIconView();
        return iconView != null ? iconView.getDrawable() : null;
    }

    private ImageView getIconView() {
        return (ImageView) findViewById(R.id.suw_layout_icon);
    }
}
