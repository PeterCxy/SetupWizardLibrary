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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * An {@link android.support.v7.widget.RecyclerView.ItemDecoration} for RecyclerView to draw
 * dividers between items. This ItemDecoration will draw the drawable specified by
 * {@link #setDivider(android.graphics.drawable.Drawable)} as the divider in between each item by
 * default, and the behavior of whether the divider is shown can be customized by subclassing
 * {@link com.android.setupwizardlib.DividerItemDecoration.DividedViewHolder}.
 *
 * <p>Modified from v14 PreferenceFragment.DividerDecoration, added with inset capabilities.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    /* static section */

    public interface DividedViewHolder {

        /**
         * Returns whether divider is allowed above this item. A divider will be shown only if both
         * items immediately above and below it allows this divider.
         */
        boolean isDividerAllowedAbove();

        /**
         * Returns whether divider is allowed below this item. A divider will be shown only if both
         * items immediately above and below it allows this divider.
         */
        boolean isDividerAllowedBelow();
    }

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider,
            android.R.attr.dividerHeight
    };

    /**
     * Creates a default instance of {@link DividerItemDecoration}, using
     * {@code android:attr/listDivider} as the divider and {@code android:attr/dividerHeight} as the
     * divider height.
     */
    public static DividerItemDecoration getDefault(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        final Drawable divider = a.getDrawable(0);
        final int dividerHeight = a.getDimensionPixelSize(1, 0);
        a.recycle();

        final DividerItemDecoration decoration = new DividerItemDecoration();
        decoration.setDivider(divider);
        decoration.setDividerHeight(dividerHeight);
        return decoration;
    }

    /* non-static section */

    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerIntrinsicHeight;

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mDivider == null) {
            return;
        }
        final int childCount = parent.getChildCount();
        final int width = parent.getWidth();
        final int dividerHeight = mDividerHeight != 0 ? mDividerHeight : mDividerIntrinsicHeight;
        for (int childViewIndex = 0; childViewIndex < childCount; childViewIndex++) {
            final View view = parent.getChildAt(childViewIndex);
            if (shouldDrawDividerBelow(view, parent)) {
                final int top = (int) ViewCompat.getY(view) + view.getHeight();
                mDivider.setBounds(0, top, width, top + dividerHeight);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {
        if (shouldDrawDividerBelow(view, parent)) {
            outRect.bottom = mDividerHeight != 0 ? mDividerHeight : mDividerIntrinsicHeight;
        }
    }

    private boolean shouldDrawDividerBelow(View view, RecyclerView parent) {
        final RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
        if ((holder instanceof DividedViewHolder)
                && !((DividedViewHolder) holder).isDividerAllowedBelow()) {
            // Don't draw if the current view holder doesn't allow drawing below
            return false;
        }
        final int index = parent.indexOfChild(view);
        final int lastItemIndex = parent.getChildCount() - 1;
        if (index == lastItemIndex) {
            return false;
        }
        if (index < lastItemIndex) {
            final View nextView = parent.getChildAt(index + 1);
            final RecyclerView.ViewHolder nextHolder = parent.getChildViewHolder(nextView);
            if ((nextHolder instanceof DividedViewHolder) &&
                    !((DividedViewHolder) nextHolder).isDividerAllowedAbove()) {
                // Don't draw if the next view holder doesn't allow drawing above
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the drawable to be used as the divider.
     */
    public void setDivider(Drawable divider) {
        if (divider != null) {
            mDividerIntrinsicHeight = divider.getIntrinsicHeight();
        } else {
            mDividerIntrinsicHeight = 0;
        }
        mDivider = divider;
    }

    /**
     * Gets the drawable currently used as the divider.
     */
    public Drawable getDivider() {
        return mDivider;
    }

    /**
     * Sets the divider height, in pixels.
     */
    public void setDividerHeight(int dividerHeight) {
        mDividerHeight = dividerHeight;
    }

    /**
     * Gets the divider height, in pixels.
     */
    public int getDividerHeight() {
        return mDividerHeight;
    }
}
