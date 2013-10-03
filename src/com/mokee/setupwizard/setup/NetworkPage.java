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

package com.mokee.setupwizard.setup;

import com.mokee.setupwizard.R;
import com.mokee.setupwizard.SetupWizardActivity;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NetworkPage extends Fragment {

    public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";

    private CheckBox mWifiCheckBox;
    private CheckBox mDataCheckBox;
    private CheckBox mGpsCheckBox;

    private WifiManager mWifiManager;
    private ConnectivityManager mConnManager;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.page_network, null);
        mWifiCheckBox = (CheckBox) view.findViewById(R.id.network_enable_wifi);
        mDataCheckBox = (CheckBox) view.findViewById(R.id.network_enable_data);
        mGpsCheckBox = (CheckBox) view.findViewById(R.id.network_enable_gps);

        MyOnCheckedChangeListener listener = new MyOnCheckedChangeListener();
        mWifiCheckBox.setOnCheckedChangeListener(listener);
        mDataCheckBox.setOnCheckedChangeListener(listener);
        mGpsCheckBox.setOnCheckedChangeListener(listener);

        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mConnManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        mGpsCheckBox.setChecked(getGpsState());
        updateWifiSettings();
        updateDataSettings();

        return view;
    }

    private boolean getGpsState() {
        ContentResolver cr = mContext.getContentResolver();
        return Settings.Secure.isLocationProviderEnabled(cr, LocationManager.GPS_PROVIDER);
    }

    private void setGpsState(boolean isChecked) {
        ContentResolver cr = mContext.getContentResolver();
        Settings.Secure.setLocationProviderEnabled(cr, LocationManager.GPS_PROVIDER, isChecked);
    }

    private void updateWifiSettings() {
        mWifiCheckBox.setChecked(mWifiManager.isWifiEnabled());
    }

    private void updateDataSettings() {
        mDataCheckBox.setEnabled(!mWifiCheckBox.isChecked());
        mDataCheckBox.setChecked(mConnManager.getMobileDataEnabled());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWifiSettings();
        updateDataSettings();
    }

    private class MyOnCheckedChangeListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (((SetupWizardActivity) getActivity()).isFirstPage()) {
                return;
            }
            if (buttonView == mWifiCheckBox) {
                if (isChecked) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$WifiSettingsActivity"));
                    startActivity(intent);
                } else {
                    mWifiManager.setWifiEnabled(false);
                    updateDataSettings();
                }
            } else if (buttonView == mDataCheckBox) {
                mConnManager.setMobileDataEnabled(isChecked);
            } else if (buttonView == mGpsCheckBox) {
                setGpsState(isChecked);
            }
        }
    }
}
