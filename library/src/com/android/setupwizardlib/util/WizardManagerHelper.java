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

import android.content.Intent;

public class WizardManagerHelper {

    private static final String ACTION_NEXT = "com.android.wizard.NEXT";

    private static final String EXTRA_SCRIPT_URI = "scriptUri";
    private static final String EXTRA_ACTION_ID = "actionId";
    private static final String EXTRA_RESULT_CODE = "com.android.setupwizard.ResultCode";

    public static final String EXTRA_THEME = "theme";
    public static final String EXTRA_USE_IMMERSIVE_MODE = "useImmersiveMode";

    public static final String THEME_MATERIAL = "material";
    public static final String THEME_MATERIAL_LIGHT = "material_light";

    /**
     * Get an intent that will invoke the next step of setup wizard.
     *
     * @param originalIntent The original intent that was used to start the step, usually via
     *                       Activity.getIntent().
     * @param resultCode The result code of the step. See {@link ResultCodes}.
     * @return A new intent that can be used with startActivityForResult() to start the next step of
     *         the setup flow.
     */
    public static Intent getNextIntent(Intent originalIntent, int resultCode) {
        return getNextIntent(originalIntent, resultCode, null);
    }

    /**
     * Get an intent that will invoke the next step of setup wizard.
     *
     * @param originalIntent The original intent that was used to start the step, usually via
     *                       Activity.getIntent().
     * @param resultCode The result code of the step. See {@link ResultCodes}.
     * @param data An intent containing extra result data.
     * @return A new intent that can be used with startActivityForResult() to start the next step of
     *         the setup flow.
     */
    public static Intent getNextIntent(Intent originalIntent, int resultCode, Intent data) {
        Intent intent = new Intent(ACTION_NEXT);
        intent.putExtra(EXTRA_SCRIPT_URI, originalIntent.getStringExtra(EXTRA_SCRIPT_URI));
        intent.putExtra(EXTRA_ACTION_ID, originalIntent.getStringExtra(EXTRA_ACTION_ID));
        intent.putExtra(EXTRA_RESULT_CODE, resultCode);
        if (data != null && data.getExtras() != null) {
            intent.putExtras(data.getExtras());
        }
        intent.putExtra(EXTRA_THEME, originalIntent.getStringExtra(EXTRA_THEME));
        return intent;
    }
}
