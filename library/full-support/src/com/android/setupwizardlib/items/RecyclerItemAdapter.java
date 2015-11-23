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

package com.android.setupwizardlib.items;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.setupwizardlib.R;

/**
 * An adapter used with RecyclerView to display an {@link ItemHierarchy}. The item hierarchy used to
 * create this adapter can be inflated by {@link com.android.setupwizardlib.items.ItemInflater} from
 * XML.
 */
public class RecyclerItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemHierarchy.Observer {

    private static final int[] SELECTABLE_ITEM_BACKGROUND = new int[] {
            R.attr.selectableItemBackground
    };

    public interface OnItemSelectedListener {
        void onItemSelected(IItem item);
    }

    private static class GenericViewHolder extends RecyclerView.ViewHolder {

        public GenericViewHolder(View itemView) {
            super(itemView);
        }
    }

    private final ItemHierarchy mItemHierarchy;
    private OnItemSelectedListener mListener;

    public RecyclerItemAdapter(ItemHierarchy hierarchy) {
        mItemHierarchy = hierarchy;
        mItemHierarchy.registerObserver(this);
    }

    public IItem getItem(int position) {
        return mItemHierarchy.getItemAt(position);
    }

    @Override
    public long getItemId(int position) {
        IItem mItem = getItem(position);
        if (mItem instanceof AbstractItem) {
            final int id = ((AbstractItem) mItem).getId();
            return id > 0 ? id : RecyclerView.NO_ID;
        } else {
            return RecyclerView.NO_ID;
        }
    }

    @Override
    public int getItemCount() {
        return mItemHierarchy.getCount();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(viewType, parent, false);
        final GenericViewHolder viewHolder = new GenericViewHolder(view);

        final TypedArray typedArray = parent.getContext()
                .obtainStyledAttributes(SELECTABLE_ITEM_BACKGROUND);
        view.setBackgroundDrawable(typedArray.getDrawable(0));
        typedArray.recycle();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = viewHolder.getAdapterPosition();
                // Position can be NO_POSITION = -1 if the item is being removed asynchronously
                // from the RecyclerView.
                if (position != RecyclerView.NO_POSITION) {
                    final IItem item = getItem(position);
                    if (mListener != null && item.isEnabled()) {
                        mListener.onItemSelected(item);
                    }
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final IItem item = getItem(position);
        item.onBindView(holder.itemView);
        if (item.isEnabled()) {
            holder.itemView.setClickable(true);
            holder.itemView.setEnabled(true);
            holder.itemView.setFocusable(true);
        } else {
            holder.itemView.setClickable(false);
            holder.itemView.setEnabled(false);
            holder.itemView.setFocusable(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Use layout resource as item view type. RecyclerView item type does not have to be
        // contiguous.
        IItem item = getItem(position);
        return item.getLayoutResource();
    }

    @Override
    public void onChanged(ItemHierarchy hierarchy) {
        notifyDataSetChanged();
    }

    public ItemHierarchy findItemById(int id) {
        return mItemHierarchy.findItemById(id);
    }

    public ItemHierarchy getRootItemHierarchy() {
        return mItemHierarchy;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }
}
