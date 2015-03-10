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
import android.content.res.TypedArray;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.setupwizardlib.view.Illustration;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
        this(context, template, null, 0);
    }

    public SetupWizardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.suwLayoutTheme);
    }

    public SetupWizardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, 0, attrs, defStyleAttr);
    }

    public SetupWizardLayout(Context context, int template, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SuwSetupWizardLayout, defStyleAttr, 0);
        if (template == 0) {
            template = a.getResourceId(R.styleable.SuwSetupWizardLayout_android_layout, 0);
        }
        inflateTemplate(template);

        // Set the background from XML, either directly or built from a bitmap tile
        final Drawable background =
                a.getDrawable(R.styleable.SuwSetupWizardLayout_suwBackground);
        if (background != null) {
            setLayoutBackground(background);
        } else {
            final Drawable backgroundTile =
                    a.getDrawable(R.styleable.SuwSetupWizardLayout_suwBackgroundTile);
            if (backgroundTile != null) {
                setBackgroundTile(backgroundTile);
            }
        }

        // Set the illustration from XML, either directly or built from image + horizontal tile
        final Drawable illustration =
                a.getDrawable(R.styleable.SuwSetupWizardLayout_suwIllustration);
        if (illustration != null) {
            setIllustration(illustration);
        } else {
            final Drawable illustrationImage =
                    a.getDrawable(R.styleable.SuwSetupWizardLayout_suwIllustrationImage);
            final Drawable horizontalTile = a.getDrawable(
                    R.styleable.SuwSetupWizardLayout_suwIllustrationHorizontalTile);
            if (illustrationImage != null && horizontalTile != null) {
                setIllustration(illustrationImage, horizontalTile);
            }
        }

        // Set the header text
        final CharSequence headerText =
                a.getText(R.styleable.SuwSetupWizardLayout_suwHeaderText);
        if (headerText != null) {
            setHeaderText(headerText);
        }

        a.recycle();
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

    /**
     * Set the illustration of the layout. The drawable will be applied as is, and the bounds will
     * be set as implemented in {@link com.android.setupwizardlib.view.Illustration}. To create
     * a suitable drawable from an asset and a horizontal repeating tile, use
     * {@link #setIllustration(int, int)} instead.
     *
     * @param drawable The drawable specifying the illustration.
     */
    public void setIllustration(Drawable drawable) {
        final View view = findViewById(R.id.suw_layout_decor);
        if (view instanceof Illustration) {
            final Illustration illustration = (Illustration) view;
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
        final View view = findViewById(R.id.suw_layout_decor);
        if (view instanceof Illustration) {
            final Illustration illustration = (Illustration) view;
            final Drawable illustrationDrawable = getIllustration(asset, horizontalTile);
            illustration.setIllustration(illustrationDrawable);
        }
    }

    private void setIllustration(Drawable asset, Drawable horizontalTile) {
        final View view = findViewById(R.id.suw_layout_decor);
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
    public void setLayoutBackground(Drawable background) {
        final View view = findViewById(R.id.suw_layout_decor);
        if (view != null) {
            view.setBackground(background);
        }
    }

    /**
     * Set the background of the layout to a repeating bitmap tile. To use a different kind of
     * drawable, use {@link #setLayoutBackground(android.graphics.drawable.Drawable)} instead.
     */
    public void setBackgroundTile(int backgroundTile) {
        final Drawable backgroundTileDrawable = getContext().getDrawable(backgroundTile);
        setBackgroundTile(backgroundTileDrawable);
    }

    private void setBackgroundTile(Drawable backgroundTile) {
        if (backgroundTile instanceof BitmapDrawable) {
            ((BitmapDrawable) backgroundTile).setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        }
        setLayoutBackground(backgroundTile);
    }

    private Drawable getIllustration(int asset, int horizontalTile) {
        final Context context = getContext();
        final Drawable assetDrawable = context.getResources().getDrawable(asset);
        final Drawable tile = context.getResources().getDrawable(horizontalTile);
        return getIllustration(assetDrawable, tile);
    }

    @SuppressLint("RtlHardcoded")
    private Drawable getIllustration(Drawable asset, Drawable horizontalTile) {
        final Context context = getContext();
        if (context.getResources().getBoolean(R.bool.suwUseTabletLayout)) {
            // If it is a "tablet" (sw600dp), create a LayerDrawable with the horizontal tile.
            if (horizontalTile instanceof BitmapDrawable) {
                ((BitmapDrawable) horizontalTile).setTileModeX(TileMode.REPEAT);
                ((BitmapDrawable) horizontalTile).setGravity(Gravity.TOP);
            }
            if (asset instanceof BitmapDrawable) {
                // Always specify TOP | LEFT, Illustration will flip the entire LayerDrawable.
                ((BitmapDrawable) asset).setGravity(Gravity.TOP | Gravity.LEFT);
            }
            final LayerDrawable layers =
                    new LayerDrawable(new Drawable[] { horizontalTile, asset });
            layers.setAutoMirrored(true);
            return layers;
        } else {
            // If it is a "phone" (not sw600dp), simply return the illustration
            asset.setAutoMirrored(true);
            return asset;
        }
    }
}
