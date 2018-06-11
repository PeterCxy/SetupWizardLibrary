/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.setupwizardlib.shadow;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

@Implements(MediaPlayer.class)
public class ShadowMediaPlayer extends org.robolectric.shadows.ShadowMediaPlayer {

    @Implementation
    public void setDataSource(
            @NonNull Context context,
            @NonNull Uri uri,
            @Nullable Map<String, String> headers,
            @Nullable List<HttpCookie> cookies)
            throws IOException {
        setDataSource(context, uri, headers);
    }

    @Implementation
    public void seekTo(long msec, int mode) {
        seekTo((int) msec);
    }
}
