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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

import com.android.setupwizardlib.R;
import com.android.setupwizardlib.annotations.VisibleForTesting;

/**
 * A RecyclerView that can display a header item at the start of the list. The header can be set by
 * {@code app:suwHeader} in XML. Note that the header will not be inflated until a layout manager
 * is set.
 */
public class HeaderRecyclerView extends RecyclerView {

    private static class HeaderViewHolder extends ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * An adapter that can optionally add one header item to the RecyclerView.
     */
    public static class HeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private RecyclerView.Adapter mAdapter;
        private View mHeader;
        private static final int HEADER_VIEW_TYPE = Integer.MAX_VALUE;

        public HeaderAdapter(RecyclerView.Adapter adapter) {
            mAdapter = adapter;
            setHasStableIds(mAdapter.hasStableIds());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER_VIEW_TYPE) {
                return new HeaderViewHolder(mHeader);
            } else {
                return mAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (mHeader != null) {
                position--;
            }
            if (position >= 0) {
                mAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mHeader != null) {
                position--;
            }
            if (position < 0) {
                return HEADER_VIEW_TYPE;
            }
            return mAdapter.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            int count = mAdapter.getItemCount();
            if (mHeader != null) {
                count++;
            }
            return count;
        }

        @Override
        public long getItemId(int position) {
            if (mHeader != null) {
                position--;
            }
            if (position < 0) {
                return Long.MAX_VALUE;
            }
            return mAdapter.getItemId(position);
        }

        public void setHeader(View header) {
            mHeader = header;
        }

        @VisibleForTesting
        public RecyclerView.Adapter getWrappedAdapter() {
            return mAdapter;
        }
    }

    private View mHeader;
    private int mHeaderRes;

    public HeaderRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.SuwHeaderRecyclerView, defStyleAttr, 0);
        mHeaderRes = a.getResourceId(R.styleable.SuwHeaderRecyclerView_suwHeader, 0);
        a.recycle();
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);

        // Decoration-only headers should not count as an item for accessibility, adjust the
        // accessibility event to account for that.
        final int numberOfHeaders = mHeader != null ? 1 : 0;
        event.setItemCount(event.getItemCount() - numberOfHeaders);
        event.setFromIndex(Math.max(event.getFromIndex() - numberOfHeaders, 0));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            event.setToIndex(Math.max(event.getToIndex() - numberOfHeaders, 0));
        }
    }

    /**
     * Gets the header view of this RecyclerView, or {@code null} if there are no headers.
     */
    public View getHeader() {
        return mHeader;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout != null && mHeader == null && mHeaderRes != 0) {
            // Inflating a child view requires the layout manager to be set. Check here to see if
            // any header item is specified in XML and inflate them.
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            mHeader = inflater.inflate(mHeaderRes, this, false);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeader != null && adapter != null) {
            final HeaderAdapter headerAdapter = new HeaderAdapter(adapter);
            headerAdapter.setHeader(mHeader);
            adapter = headerAdapter;
        }
        super.setAdapter(adapter);
    }
}