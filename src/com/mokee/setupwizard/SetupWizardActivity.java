/*
 * Copyright (C) 2013 The MoKee OpenSource Project
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

package com.mokee.setupwizard;

import com.mokee.setupwizard.setup.FinishPage;
import com.mokee.setupwizard.setup.InputMethodPage;
import com.mokee.setupwizard.setup.NetworkPage;
import com.mokee.setupwizard.setup.WelcomePage;
import com.mokee.setupwizard.widget.FixedSpeedScroller;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SetupWizardActivity extends Activity {

    private static final String TAG = "mokee_setupwizard";
    private static final String GOOGLE_SETUPWIZARD_PACKAGE = "com.google.android.setupwizard";

    private Interpolator sInterpolator;
    private StatusBarManager mStatusBarManager;

    private ViewPager mViewPager;
    private TabPagerListener mPagerListener;
    private List<Fragment> mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkInit();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mStatusBarManager = (StatusBarManager) getSystemService(Context.STATUS_BAR_SERVICE);
        disableStatusBar();

        sInterpolator = new LinearInterpolator();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        getPages();

        mViewPager.setAdapter(new MkFragmentAdapter(getFragmentManager()));
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(mPagerListener);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(),
                    sInterpolator);
            scroller.setFixedDuration(200);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private void checkInit() {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = SetupWizardActivity.this.getWindow();
        window.setFlags(flag, flag);
    }

    public boolean isFirstPage() {
        return mViewPager.getCurrentItem() == 0 ? true : false;

    }

    private void getPages() {
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new WelcomePage());
        mFragmentList.add(new NetworkPage());
        mFragmentList.add(new InputMethodPage());
        mFragmentList.add(new FinishPage());
    }

    public void goNextPage() {
        int next = mViewPager.getCurrentItem() + 1;
        if (next != mFragmentList.size()) {
            mViewPager.setCurrentItem(next);
        } else {
            finishSetup();
        }
    }

    public void goPreviousPage() {
        int previous = mViewPager.getCurrentItem() - 1;
        if (previous >= 0) {
            mViewPager.setCurrentItem(previous);
        }
    }

    public void disableStatusBar() {
        mStatusBarManager.disable(StatusBarManager.DISABLE_EXPAND
                | StatusBarManager.DISABLE_NOTIFICATION_ALERTS
                | StatusBarManager.DISABLE_NOTIFICATION_TICKER | StatusBarManager.DISABLE_RECENT
                | StatusBarManager.DISABLE_HOME
                | StatusBarManager.DISABLE_SEARCH);
    }

    public void enableStatusBar() {
        mStatusBarManager.disable(StatusBarManager.DISABLE_NONE);
    }

    private void finishSetup() {
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
        enableStatusBar();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        disableSetupWizards(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.getFlags());
        startActivity(intent);
        finish();
    }

    private void disableSetupWizards(Intent intent) {
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : resolveInfos) {
            if (GOOGLE_SETUPWIZARD_PACKAGE.equals(info.activityInfo.packageName)) {
                final ComponentName componentName = new ComponentName(
                        info.activityInfo.packageName, info.activityInfo.name);
                pm.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        }
        pm.setComponentEnabledSetting(getComponentName(),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onBackPressed() {
        goPreviousPage();
    }

    private class TabPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            goNextPage();
        }
    }

    public class MkFragmentAdapter extends FragmentPagerAdapter {

        public MkFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int id) {
            return mFragmentList.get(id);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }

    public static class DepthPageTransformer implements ViewPager.PageTransformer {
        private static float MIN_SCALE = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) {
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
