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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class GlifDimensionTest {

    private static final String TAG = "GlifDimensionTest";

    private Context mContext;

    @Before
    public void setUp() {
        mContext = new ContextThemeWrapper(InstrumentationRegistry.getTargetContext(),
                R.style.SuwThemeGlif_Light);
    }

    @Test
    public void testDividerInsetPhone() {
        // The screen width APIs needed for this this were introduced in Honeycomb MR2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            setConfigurationScreenSize(480, 640);
            final TypedArray a = mContext.obtainStyledAttributes(new int[]{R.attr.suwMarginSides});
            final int marginSides = a.getDimensionPixelSize(0, 0);
            assertEquals("Margin sides for phones should be 24dp", dp2Px(24), marginSides);
            a.recycle();

            final Resources res = mContext.getResources();
            final int dividerTextInset = res.getDimensionPixelSize(
                    R.dimen.suw_items_glif_text_divider_inset);
            assertEquals(marginSides, dividerTextInset);
            final int dividerIconInset = res.getDimensionPixelSize(
                    R.dimen.suw_items_glif_icon_divider_inset);
            final int iconWidth = res.getDimensionPixelSize(R.dimen.suw_items_icon_container_width);
            assertEquals(marginSides + iconWidth, dividerIconInset);
        } else {
            Log.d(TAG, "Cannot test divider inset because SDK version < 13");
        }
    }

    @Test
    public void testDividerInsetSw600dp() {
        // The screen width APIs needed for this this were introduced in Honeycomb MR2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            setConfigurationScreenSize(800, 1024);
            final TypedArray a = mContext.obtainStyledAttributes(new int[]{R.attr.suwMarginSides});
            final int marginSides = a.getDimensionPixelSize(0, 0);
            assertEquals("Margin sides for sw600dp should be 40dp", dp2Px(40), marginSides);
            a.recycle();

            final Resources res = mContext.getResources();
            final int dividerTextInset = res.getDimensionPixelSize(
                    R.dimen.suw_items_glif_text_divider_inset);
            assertEquals(marginSides, dividerTextInset);

            final int dividerIconInset = res.getDimensionPixelSize(
                    R.dimen.suw_items_glif_icon_divider_inset);
            final int iconWidth = res.getDimensionPixelSize(R.dimen.suw_items_icon_container_width);
            assertEquals(marginSides + iconWidth, dividerIconInset);
        } else {
            Log.d(TAG, "Cannot test divider inset because SDK version < 13");
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void setConfigurationScreenSize(int width, int height) {
        final Resources res = mContext.getResources();
        final Configuration config = res.getConfiguration();
        config.screenWidthDp = width;
        config.screenHeightDp = height;
        config.smallestScreenWidthDp = Math.min(width, height);

        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private int dp2Px(float dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}
