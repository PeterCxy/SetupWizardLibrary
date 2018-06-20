/*
 * Copyright (C) 2017 The Android Open Source Project
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

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.robolectric.RuntimeEnvironment.application;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.view.Surface;
import android.view.View;

import androidx.annotation.RawRes;

import com.android.setupwizardlib.R;
import com.android.setupwizardlib.robolectric.SuwLibRobolectricTestRunner;
import com.android.setupwizardlib.shadow.ShadowLog;
import com.android.setupwizardlib.shadow.ShadowMediaPlayer;
import com.android.setupwizardlib.view.IllustrationVideoViewTest.ShadowSurface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowMediaPlayer.InvalidStateBehavior;
import org.robolectric.shadows.ShadowMediaPlayer.MediaInfo;
import org.robolectric.shadows.ShadowMediaPlayer.State;
import org.robolectric.shadows.util.DataSource;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

@RunWith(SuwLibRobolectricTestRunner.class)
@Config(
        sdk = Config.NEWEST_SDK,
        shadows = {
                ShadowLog.class,
                ShadowMediaPlayer.class,
                ShadowSurface.class
        })
public class IllustrationVideoViewTest {

    @Mock
    private SurfaceTexture mSurfaceTexture;

    private IllustrationVideoView mView;

    private ShadowMediaPlayer mShadowMediaPlayer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        addMediaInfo(android.R.color.white);
    }

    @Test
    public void onVisibilityChanged_notVisible_shouldRelease() {

        createDefaultView();
        mView.onWindowVisibilityChanged(View.GONE);

        assertThat(mShadowMediaPlayer.getState()).isEqualTo(State.END);
        assertThat(mView.mSurface).isNull();
        assertThat(mView.mMediaPlayer).isNull();
    }

    @Test
    public void onVisibilityChanged_visible_shouldPlay() {
        createDefaultView();

        mView.onWindowVisibilityChanged(View.GONE);
        assertThat(mView.mSurface).isNull();
        assertThat(mView.mMediaPlayer).isNull();

        mView.onWindowVisibilityChanged(View.VISIBLE);

        assertThat(mView.mSurface).isNotNull();
        assertThat(mView.mMediaPlayer).isNotNull();
    }

    @Test
    public void testPausedWhenWindowFocusLost() {
        createDefaultView();
        Robolectric.flushForegroundThreadScheduler();
        mView.start();

        assertNotNull(mView.mMediaPlayer);
        assertNotNull(mView.mSurface);

        mView.onWindowFocusChanged(false);
        assertThat(mShadowMediaPlayer.getState()).isEqualTo(State.PAUSED);
    }

    @Test
    public void testStartedWhenWindowFocusRegained() {
        testPausedWhenWindowFocusLost();

        mView.onWindowFocusChanged(true);
        assertThat(mShadowMediaPlayer.getState()).isEqualTo(State.STARTED);
    }

    @Test
    public void testSurfaceReleasedWhenTextureDestroyed() {
        createDefaultView();
        mView.start();

        assertNotNull(mView.mMediaPlayer);
        assertNotNull(mView.mSurface);

        mView.onSurfaceTextureDestroyed(mSurfaceTexture);
        assertThat(mShadowMediaPlayer.getState()).isEqualTo(State.END);
    }

    @Test
    public void testXmlSetVideoResId() {
        createDefaultView();
        assertThat(mShadowMediaPlayer.getSourceUri().toString())
                .isEqualTo("android.resource://com.android.setupwizardlib/"
                        + android.R.color.white);
    }

    @Test
    public void testSetVideoResId() {
        addMediaInfo(android.R.color.black);

        createDefaultView();

        @RawRes int black = android.R.color.black;
        mView.setVideoResource(black);

        mShadowMediaPlayer = (ShadowMediaPlayer) Shadows.shadowOf(mView.mMediaPlayer);

        assertThat(mShadowMediaPlayer.getSourceUri().toString())
                .isEqualTo("android.resource://com.android.setupwizardlib/"
                        + android.R.color.black);
    }

    @Test
    public void prepareVideo_shouldSetAspectRatio() {
        createDefaultView();

        mShadowMediaPlayer.setVideoSize(720, 1280);

        Robolectric.flushForegroundThreadScheduler();
        mView.start();

        mView.measure(View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY));

        final float aspectRatio = (float) mView.getMeasuredHeight() / mView.getMeasuredWidth();
        assertThat(aspectRatio).isWithin(0.001f).of(1280f / 720f);
    }

    @Test
    public void prepareVideo_zeroHeight_shouldSetAspectRatioToZero() {
        createDefaultView();

        mShadowMediaPlayer.setVideoSize(720, 0);

        Robolectric.flushForegroundThreadScheduler();
        mView.start();

        final float aspectRatio = (float) mView.getHeight() / mView.getWidth();
        assertThat(aspectRatio).isEqualTo(0.0f);
    }

    private void createDefaultView() {
        mView = new IllustrationVideoView(
                application,
                Robolectric.buildAttributeSet()
                        // Any resource attribute should work, since the DataSource is fake
                        .addAttribute(R.attr.suwVideo, "@android:color/white")
                        .build());

        Activity activity = Robolectric.setupActivity(Activity.class);
        activity.setContentView(mView);
        setWindowVisible();

        mView.setSurfaceTexture(mock(SurfaceTexture.class));
        mView.onSurfaceTextureAvailable(mSurfaceTexture, 500, 500);
        mShadowMediaPlayer = (ShadowMediaPlayer) Shadows.shadowOf(mView.mMediaPlayer);
        mShadowMediaPlayer.setInvalidStateBehavior(InvalidStateBehavior.EMULATE);
    }

    private void setWindowVisible() {
        Object viewRootImpl = ReflectionHelpers.callInstanceMethod(mView, "getViewRootImpl");
        ReflectionHelpers.callInstanceMethod(
                viewRootImpl,
                "handleAppVisibility",
                ClassParameter.from(boolean.class, true));
        assertThat(mView.isAttachedToWindow()).isTrue();
        assertThat(mView.getWindowVisibility()).isEqualTo(View.VISIBLE);
    }

    private void addMediaInfo(@RawRes int res) {
        ShadowMediaPlayer.addMediaInfo(
                DataSource.toDataSource(
                        application,
                        Uri.parse("android.resource://com.android.setupwizardlib/" + res),
                        null),
                new MediaInfo(5000, 1));
    }

    @Implements(Surface.class)
    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static class ShadowSurface extends org.robolectric.shadows.ShadowSurface {

        @RealObject
        private Surface mRealSurface;

        @Override
        public void __constructor__(SurfaceTexture surfaceTexture) {
            // Call the constructor on the real object, so that critical fields such as mLock is
            // initialized properly.
            Shadow.invokeConstructor(Surface.class, mRealSurface,
                    ReflectionHelpers.ClassParameter.from(SurfaceTexture.class, surfaceTexture));
            super.__constructor__(surfaceTexture);
        }
    }
}
