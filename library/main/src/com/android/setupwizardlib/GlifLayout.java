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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.setupwizardlib.template.ButtonFooterMixin;
import com.android.setupwizardlib.template.ColoredHeaderMixin;
import com.android.setupwizardlib.template.HeaderMixin;
import com.android.setupwizardlib.template.IconMixin;
import com.android.setupwizardlib.template.ProgressBarMixin;
import com.android.setupwizardlib.view.StatusBarBackgroundLayout;

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

    private ColorStateList mPrimaryColor;

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
        registerMixin(HeaderMixin.class, new ColoredHeaderMixin(this, attrs, defStyleAttr));
        registerMixin(IconMixin.class, new IconMixin(this, attrs, defStyleAttr));
        registerMixin(ProgressBarMixin.class, new ProgressBarMixin(this));
        registerMixin(ButtonFooterMixin.class, new ButtonFooterMixin(this));

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.SuwGlifLayout, defStyleAttr, 0);

        ColorStateList primaryColor =
                a.getColorStateList(R.styleable.SuwGlifLayout_suwColorPrimary);

        a.recycle();

        if (primaryColor == null && Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.SuwGlifLayoutV21, defStyleAttr, 0);
            primaryColor = a.getColorStateList(R.styleable.SuwGlifLayoutV21_android_colorPrimary);
            a.recycle();
        }

        setPrimaryColor(primaryColor);
    }

    @Override
    protected View onInflateTemplate(LayoutInflater inflater, int template) {
        if (template == 0) {
            template = R.layout.suw_glif_template;
        }
        try {
            return super.onInflateTemplate(inflater, template);
        } catch (RuntimeException e) {
            // Versions before M throws RuntimeException for unsuccessful attribute resolution
            // Versions M+ will throw an InflateException (which extends from RuntimeException)
            throw new InflateException("Unable to inflate layout. Are you using "
                    + "@style/SuwThemeGlif (or its descendant) as your theme?", e);
        }
    }

    @Override
    protected ViewGroup findContainer(int containerId) {
        if (containerId == 0) {
            containerId = R.id.suw_layout_content;
        }
        return super.findContainer(containerId);
    }

    public ScrollView getScrollView() {
        final View view = findManagedViewById(R.id.suw_scroll_view);
        return view instanceof ScrollView ? (ScrollView) view : null;
    }

    public TextView getHeaderTextView() {
        return getMixin(HeaderMixin.class).getTextView();
    }

    public void setHeaderText(int title) {
        getMixin(HeaderMixin.class).setText(title);
    }

    public void setHeaderText(CharSequence title) {
        getMixin(HeaderMixin.class).setText(title);
    }

    public CharSequence getHeaderText() {
        return getMixin(HeaderMixin.class).getText();
    }

    public void setHeaderColor(ColorStateList color) {
        final ColoredHeaderMixin mixin = (ColoredHeaderMixin) getMixin(HeaderMixin.class);
        mixin.setColor(color);
    }

    public ColorStateList getHeaderColor() {
        final ColoredHeaderMixin mixin = (ColoredHeaderMixin) getMixin(HeaderMixin.class);
        return mixin.getColor();
    }

    public void setIcon(Drawable icon) {
        getMixin(IconMixin.class).setIcon(icon);
    }

    public Drawable getIcon() {
        return getMixin(IconMixin.class).getIcon();
    }

    public void setPrimaryColor(ColorStateList color) {
        mPrimaryColor = color;
        setGlifPatternColor(color);
        getMixin(ProgressBarMixin.class).setColor(color);
    }

    public ColorStateList getPrimaryColor() {
        return mPrimaryColor;
    }

    @SuppressLint("InlinedApi")  // View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN is guarded by SDK check
    private void setGlifPatternColor(ColorStateList color) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            final View patternBg = findManagedViewById(R.id.suw_pattern_bg);
            if (patternBg != null) {
                final GlifPatternDrawable background =
                        new GlifPatternDrawable(color.getDefaultColor());
                if (patternBg instanceof StatusBarBackgroundLayout) {
                    ((StatusBarBackgroundLayout) patternBg).setStatusBarBackground(background);
                } else {
                    patternBg.setBackground(background);
                }
            }
        }
    }

    public boolean isProgressBarShown() {
        return getMixin(ProgressBarMixin.class).isShown();
    }

    public void setProgressBarShown(boolean shown) {
        getMixin(ProgressBarMixin.class).setShown(shown);
    }

    public ProgressBar peekProgressBar() {
        return getMixin(ProgressBarMixin.class).peekProgressBar();
    }
}
