
package com.mokee.mksetupwizard.setup;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocalePicker.LocaleInfo;
import com.mokee.mksetupwizard.MainActivity;
import com.mokee.mksetupwizard.R;

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
        mTextView.setText("Hi! " + model + originText);

//        mButton.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                ((MainActivity) getActivity()).goNextPage();
//            }
//        });

        final ArrayAdapter<LocaleInfo> adapter = LocalePicker.constructAdapter(
                getActivity(), R.layout.locale_picker_item, R.id.locale);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Locale locale = adapter.getItem(position).getLocale();
                LocalePicker.updateLocale(locale);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        // Select current locale by default
        Locale current = Locale.getDefault();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            Locale locale = adapter.getItem(i).getLocale();
            if (current.equals(locale)) {
                mSpinner.setSelection(i);
                break;
            }
        }
    }
}
