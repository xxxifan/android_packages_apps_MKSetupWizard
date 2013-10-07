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

import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocalePicker.LocaleInfo;
import com.mokee.setupwizard.R;
import com.mokee.setupwizard.SetupWizardActivity;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class WelcomePage extends Fragment {

    private TextView mTextView;
    private Spinner mSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_welcome, null);
        mTextView = (TextView) view.findViewById(R.id.welcome_summary);
        mSpinner = (Spinner) view.findViewById(R.id.local_spinner);

        initView();
        return view;
    }

    private void initView() {
        String model = Build.MODEL;
        String originText = " " + mTextView.getText().toString();
        mTextView.setText("Hi, " + model + originText);

        final ArrayAdapter<LocaleInfo> adapter = LocalePicker.constructAdapter(
                getActivity(), R.layout.locale_picker_item, R.id.locale);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (((SetupWizardActivity) getActivity()).isFirstPage()) {
                    Locale locale = adapter.getItem(position).getLocale();
                    LocalePicker.updateLocale(locale);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        // Select current locale by default
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int count = adapter.getCount();
                Locale current = Locale.getDefault();
                for (int i = 0; i < count; i++) {
                    Locale locale = adapter.getItem(i).getLocale();
                    if (current.equals(locale)) {
                        mSpinner.setSelection(i);
                        break;
                    }
                }
                mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Locale locale = adapter.getItem(i).getLocale();
                        LocalePicker.updateLocale(locale);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
        });
    }
}
