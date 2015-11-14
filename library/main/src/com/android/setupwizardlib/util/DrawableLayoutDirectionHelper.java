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

package com.android.setupwizardlib.util;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.view.View;

/**
 * Provides convenience methods to handle drawable layout directions in different SDK versions.
 */
public class DrawableLayoutDirectionHelper {

    /**
     * Creates an {@link android.graphics.drawable.InsetDrawable} according to the layout direction
     * of {@code view}.
     */
    public static InsetDrawable createRelativeInsetDrawable(Drawable drawable,
            int insetStart, int insetTop, int insetEnd, int insetBottom, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            return new InsetDrawable(drawable, insetEnd, insetTop, insetStart, insetBottom);
        } else {
            return new InsetDrawable(drawable, insetStart, insetTop, insetEnd, insetBottom);
        }
    }

    /**
     * Creates an {@link android.graphics.drawable.InsetDrawable} according to
     * {@code layoutDirection}.
     */
    public static InsetDrawable createRelativeInsetDrawable(Drawable drawable,
            int insetStart, int insetTop, int insetEnd, int insetBottom, int layoutDirection) {
        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            return new InsetDrawable(drawable, insetEnd, insetTop, insetStart, insetBottom);
        } else {
            return new InsetDrawable(drawable, insetStart, insetTop, insetEnd, insetBottom);
        }
    }
}
