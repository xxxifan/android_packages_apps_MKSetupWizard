
package com.mokee.mksetupwizard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.mokee.mksetupwizard.setup.InputMethodPage;
import com.mokee.mksetupwizard.setup.WelcomePage;
import com.mokee.mksetupwizard.widget.FixedSpeedScroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final boolean isDebuging = true;

    private static final String TAG = "mokee_setupwizard";
    private static final String BOOT_KEY = "boot_key";

    private Interpolator sInterpolator;

    private ViewPager mViewPager;
    private TabPagerListener mPagerListener;
    private List<Fragment> mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkInit();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int version = prefs.getInt(BOOT_KEY, 0);
        // if (version == 0) { // First init,record it.
        if (isDebuging) {
            prefs.edit().putInt(BOOT_KEY, 1).commit();
            int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            Window window = MainActivity.this.getWindow();
            window.setFlags(flag, flag);
        } else { // Already run,shutdown
            Log.e(TAG, "Already setup,shutdown");
            MainActivity.this.finish();
        }
    }
    
    private void getPages() {
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new WelcomePage());
        mFragmentList.add(new InputMethodPage());
    }

    public void goNextPage() {
        int current = mViewPager.getCurrentItem();
        if (current != (mFragmentList.size() - 1)) {
            mViewPager.setCurrentItem(++current);
        }
    }

    public void goPreviousPage() {
        int current = mViewPager.getCurrentItem();
        if (current > 0) {
            mViewPager.setCurrentItem(--current);
        }
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
            mViewPager.setCurrentItem(position);
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
