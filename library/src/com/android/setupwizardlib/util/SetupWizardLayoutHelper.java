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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.setupwizardlib.R;
import com.android.setupwizardlib.SetupWizardLayout;
import com.android.setupwizardlib.view.Illustration;

/**
 * This layout helper works with SetupWizardLayout and manages the templates for suw_template and
 * its variants to perform some common operations.
 *
 * Layouts using custom templates can implement their own LayoutHelpers to perform layout specific
 * tasks for the template.
 */
public class SetupWizardLayoutHelper {

    /**
     * Create a SetupWizardLayoutHelper from a view in the activity. If the viewId is not found, or
     * if that view is not an instance of SetupWizardLayout, this method will return null.
     *
     * @return An instance of SetupWizardLayoutHelper, or null if viewId is not found or is not an
     *         instance of SetupWizardLayout.
     */
    public static SetupWizardLayoutHelper from(Activity activity, int viewId) {
        View view = activity.findViewById(viewId);
        if (view instanceof SetupWizardLayout) {
            return new SetupWizardLayoutHelper((SetupWizardLayout) view);
        }
        return null;
    }

    private SetupWizardLayout mLayout;

    public SetupWizardLayoutHelper(SetupWizardLayout layout) {
        mLayout = layout;
    }

    public SetupWizardLayout getLayout() {
        return mLayout;
    }

    public void setHeaderText(int title) {
        TextView titleView = (TextView) mLayout.findViewById(R.id.suw_layout_title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public void setHeaderText(CharSequence title) {
        TextView titleView = (TextView) mLayout.findViewById(R.id.suw_layout_title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    /**
     * Set the illustration of the layout. The drawable will be applied as is, and the bounds will
     * be set as implemented in {@link com.android.setupwizardlib.view.Illustration}. To create
     * a suitable drawable from an asset and a horizontal repeating tile, use
     * {@link #setIllustration(int, int)} instead.
     *
     * @param drawable The drawable specifying the illustration.
     */
    public void setIllustration(Drawable drawable) {
        View view = mLayout.findViewById(R.id.suw_layout_decor);
        if (view instanceof Illustration) {
            Illustration illustration = (Illustration) view;
            illustration.setIllustration(drawable);
        }
    }

    /**
     * Set the illustration of the layout, which will be created asset and the horizontal tile as
     * suitable. On phone layouts (not sw600dp), the asset will be scaled, maintaining aspect ratio.
     * On tablets (sw600dp), the assets will always have 256dp height and the rest of the
     * illustration area that the asset doesn't fill will be covered by the horizontalTile.
     *
     * @param asset Resource ID of the illustration asset.
     * @param horizontalTile Resource ID of the horizontally repeating tile for tablet layout.
     */
    public void setIllustration(int asset, int horizontalTile) {
        View view = mLayout.findViewById(R.id.suw_layout_decor);
        if (view instanceof Illustration) {
            final Illustration illustration = (Illustration) view;
            final Drawable illustrationDrawable = getIllustration(asset, horizontalTile);
            illustration.setIllustration(illustrationDrawable);
        }
    }

    /**
     * Set the background of the layout, which is expected to be able to extend infinitely. If it is
     * a bitmap tile and you want it to repeat, use {@link #setBackgroundTile(int)} instead.
     */
    public void setBackground(Drawable background) {
        View view = mLayout.findViewById(R.id.suw_layout_decor);
        if (view != null) {
            view.setBackground(background);
        }
    }

    /**
     * Set the background of the layout to a repeating bitmap tile. To use a different kind of
     * drawable, use {@link #setBackground(android.graphics.drawable.Drawable)} instead.
     */
    public void setBackgroundTile(int backgroundTile) {
        Drawable background = mLayout.getContext().getDrawable(backgroundTile);
        if (background instanceof BitmapDrawable) {
            ((BitmapDrawable) background).setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        }
        View view = mLayout.findViewById(R.id.suw_layout_decor);
        if (view != null) {
            view.setBackground(background);
        }
    }

    @SuppressLint("RtlHardcoded")
    private Drawable getIllustration(int asset, int horizontalTile) {
        final Context context = mLayout.getContext();
        final Drawable illustration = context.getDrawable(asset);
        if (context.getResources().getBoolean(R.bool.suwUseTabletLayout)) {
            // If it is a "tablet" (sw600dp), create a LayerDrawable with the horizontal tile.
            final Drawable tile = context.getDrawable(horizontalTile);
            if (tile instanceof BitmapDrawable) {
                ((BitmapDrawable) tile).setTileModeX(TileMode.REPEAT);
                ((BitmapDrawable) tile).setGravity(Gravity.TOP);
            }
            if (illustration instanceof BitmapDrawable) {
                // Always specify TOP | LEFT, Illustration will flip the entire LayerDrawable.
                ((BitmapDrawable) illustration).setGravity(Gravity.TOP | Gravity.LEFT);
            }
            final LayerDrawable layers = new LayerDrawable(new Drawable[] { tile, illustration });
            layers.setAutoMirrored(true);
            return layers;
        } else {
            // If it is a "phone" (not sw600dp), simply return the illustration
            illustration.setAutoMirrored(true);
            return illustration;
        }
    }
}
