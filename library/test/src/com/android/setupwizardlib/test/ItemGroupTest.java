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

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.setupwizardlib.items.Item;
import com.android.setupwizardlib.items.ItemGroup;

import java.util.Arrays;

public class ItemGroupTest extends AndroidTestCase {

    @SmallTest
    public void testOnBindView() {
        ItemGroup itemGroup = new ItemGroup(mContext, null);
        Item child1 = new Item(mContext, null);
        Item child2 = new Item(mContext, null);
        itemGroup.addChild(child1);
        itemGroup.addChild(child2);

        assertEquals("Item group should contain children in the order they were added",
                Arrays.asList(child1, child2), Arrays.asList(itemGroup.getChildren()));
    }
}
