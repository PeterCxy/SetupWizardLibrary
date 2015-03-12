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

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import android.test.mock.MockPackageManager;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.setupwizardlib.util.Partner;
import com.android.setupwizardlib.util.Partner.ResourceEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartnerTest extends InstrumentationTestCase {

    private TestContext mTestContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTestContext = new TestContext(getInstrumentation().getTargetContext());
        Partner.resetForTesting();
    }

    @SmallTest
    public void testLoadPartner() {
        mTestContext.partnerList = Arrays.asList(
                createResolveInfo("hocus.pocus", false),
                createResolveInfo("com.android.setupwizardlib.test", true)
        );

        Partner partner = Partner.get(mTestContext);
        assertNotNull("Partner should not be null", partner);
    }

    @SmallTest
    public void testLoadNoPartner() {
        mTestContext.partnerList = new ArrayList<>();

        Partner partner = Partner.get(mTestContext);
        assertNull("Partner should be null", partner);
    }

    @SmallTest
    public void testLoadNonSystemPartner() {
        mTestContext.partnerList = Arrays.asList(
                createResolveInfo("hocus.pocus", false),
                createResolveInfo("com.android.setupwizardlib.test", false)
        );

        Partner partner = Partner.get(mTestContext);
        assertNull("Partner should be null", partner);
    }

    public void testLoadPartnerValue() {
        mTestContext.partnerList = Arrays.asList(
                createResolveInfo("hocus.pocus", false),
                createResolveInfo("com.android.setupwizardlib.test", true)
        );

        ResourceEntry entry =
                Partner.getResourceEntry(mTestContext, R.integer.suwTransitionDuration);
        int partnerValue = entry.resources.getInteger(entry.id);
        assertEquals("Partner value should be overlaid to 5000", 5000, partnerValue);
    }

    public void testLoadDefaultValue() {
        mTestContext.partnerList = Arrays.asList(
                createResolveInfo("hocus.pocus", false),
                createResolveInfo("com.android.setupwizardlib.test", true)
        );

        ResourceEntry entry =
                Partner.getResourceEntry(mTestContext, R.color.suw_navbar_text_dark);
        int partnerValue = entry.resources.getColor(entry.id);
        assertEquals("Partner value should default to 0xdeffffff", 0xdeffffff, partnerValue);
    }

    private ResolveInfo createResolveInfo(String packageName, boolean isSystem) {
        ResolveInfo info = new ResolveInfo();
        info.resolvePackageName = packageName;
        ActivityInfo activityInfo = new ActivityInfo();
        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.flags = isSystem ? ApplicationInfo.FLAG_SYSTEM : 0;
        appInfo.packageName = packageName;
        activityInfo.applicationInfo = appInfo;
        activityInfo.packageName = packageName;
        activityInfo.name = packageName;
        info.activityInfo = activityInfo;
        return info;
    }

    private static class TestPackageManager extends MockPackageManager {

        private Context mTestContext;

        public TestPackageManager(Context testContext) {
            mTestContext = testContext;
        }

        @Override
        public Resources getResourcesForApplication(ApplicationInfo app) {
            if (app != null && "com.android.setupwizardlib.test".equals(app.packageName)) {
                return mTestContext.getResources();
            } else {
                return super.getResourcesForApplication(app);
            }
        }

        @Override
        public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
            if ("com.android.setupwizard.action.PARTNER_CUSTOMIZATION".equals(intent.getAction())) {
                return ((TestContext) mTestContext).partnerList;
            } else {
                return super.queryBroadcastReceivers(intent, flags);
            }
        }
    }

    private static class TestContext extends ContextWrapper {

        public List<ResolveInfo> partnerList;

        public TestContext(Context context) {
            super(context);
        }

        @Override
        public PackageManager getPackageManager() {
            return new TestPackageManager(this);
        }
    }
}
