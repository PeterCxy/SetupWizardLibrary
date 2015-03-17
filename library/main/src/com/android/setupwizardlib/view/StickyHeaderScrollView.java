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

package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

/**
 * This class provides sticky header functionality in a scroll view, to use with
 * SetupWizardIllustration. To use this, add a subview tagged with "sticky", or a subview tagged
 * with "stickyContainer" and one of its child tagged as "sticky". The sticky container will be
 * drawn when the sticky element hits the top of the view.
 *
 * There are a few things to note:
 * 1. The two supported scenarios are StickyHeaderScrollView -> subview (stickyContainer) -> sticky,
 *    and StickyHeaderScrollView -> container -> subview (sticky).
 *    The arrow (->) represents parent/child relationship and must be immediate child.
 * 2. The view does not work well with padding. b/16190933
 * 3. If fitsSystemWindows is true, then this will offset the sticking position by the height of
 *    the system decorations at the top of the screen.
 *
 * @see StickyHeaderListView
 */
public class StickyHeaderScrollView extends BottomScrollView {

    private View mSticky;
    private View mStickyContainer;
    private int mStatusBarInset = 0;
    private RectF mStickyRect = new RectF();

    public StickyHeaderScrollView(Context context) {
        super(context);
    }

    public StickyHeaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyHeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mSticky == null) {
            updateStickyView();
        }
    }

    public void updateStickyView() {
        mSticky = findViewWithTag("sticky");
        mStickyContainer = findViewWithTag("stickyContainer");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mStickyRect.contains(ev.getX(), ev.getY())) {
            ev.offsetLocation(-mStickyRect.left, -mStickyRect.top);
            return mStickyContainer.dispatchTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mSticky != null) {
            final int saveCount = canvas.save();
            // The view to draw when sticking to the top
            final View drawTarget = mStickyContainer != null ? mStickyContainer : mSticky;
            // The offset to draw the view at when sticky
            final int drawOffset = mStickyContainer != null ? mSticky.getTop() : 0;
            // Position of the draw target, relative to the outside of the scrollView
            final int drawTop = drawTarget.getTop() - getScrollY();
            if (drawTop + drawOffset < mStatusBarInset || !drawTarget.isShown()) {
                // ScrollView translates the whole canvas so we have to compensate for that
                mStickyRect.set(0, -drawOffset + mStatusBarInset, drawTarget.getWidth(),
                        drawTarget.getHeight() - drawOffset + mStatusBarInset);
                canvas.translate(0, -drawTop + mStickyRect.top);
                canvas.clipRect(0, 0, drawTarget.getWidth(), drawTarget.getHeight());
                drawTarget.draw(canvas);
            } else {
                mStickyRect.setEmpty();
            }
            canvas.restoreToCount(saveCount);
        }
        onDrawScrollBars(canvas);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (getFitsSystemWindows()) {
            mStatusBarInset = insets.getSystemWindowInsetTop();
            insets = insets.replaceSystemWindowInsets(
                    insets.getSystemWindowInsetLeft(),
                    0, /* top */
                    insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom()
            );
        }
        return insets;
    }
}
