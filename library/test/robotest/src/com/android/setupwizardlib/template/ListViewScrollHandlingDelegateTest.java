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

package com.android.setupwizardlib.template;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.robolectric.RuntimeEnvironment.application;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.android.setupwizardlib.robolectric.SuwLibRobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowListView;

@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK})
@RunWith(SuwLibRobolectricTestRunner.class)
public class ListViewScrollHandlingDelegateTest {

  @Mock private RequireScrollMixin requireScrollMixin;

  private ListView listView;
  private ShadowListView shadowListView;
  private ListViewScrollHandlingDelegate delegate;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    listView = new TestListView(application);
    delegate = new ListViewScrollHandlingDelegate(requireScrollMixin, listView);
    shadowListView = Shadows.shadowOf(listView);

    listView.layout(0, 0, 50, 50);
  }

  @Test
  public void testRequireScroll() throws Throwable {
    delegate.startListening();

    verify(requireScrollMixin).notifyScrollabilityChange(true);
  }

  @Test
  public void testScrolledToBottom() throws Throwable {
    delegate.startListening();

    verify(requireScrollMixin).notifyScrollabilityChange(true);

    shadowListView.getOnScrollListener().onScroll(listView, 2, 20, 20);

    verify(requireScrollMixin).notifyScrollabilityChange(false);
  }

  @Test
  public void testPageScrollDown() throws Throwable {
    delegate.pageScrollDown();
    assertThat(shadowListView.getLastSmoothScrollByDistance()).isEqualTo(50);
  }

  private static class TestListView extends ListView {

    TestListView(Context context) {
      super(context);
      setAdapter(
          new BaseAdapter() {
            @Override
            public int getCount() {
              return 20;
            }

            @Override
            public Object getItem(int position) {
              return null;
            }

            @Override
            public long getItemId(int position) {
              return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
              return new View(parent.getContext());
            }
          });
    }
  }
}
