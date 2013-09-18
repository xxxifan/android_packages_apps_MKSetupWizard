
package com.mokee.mksetupwizard.setup;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.mokee.mksetupwizard.R;

import java.util.ArrayList;
import java.util.List;

public class InputMethodPage extends Fragment {

    private Context mContext;
    private List<InputMethodInfo> mImList;
    private String mDefaultId;
    
    private ListView mListView;

    public InputMethodPage(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Should be ignored if only one input method exists. 
        View view = inflater.inflate(R.layout.page_input, null);

        mListView = (ListView) view.findViewById(R.id.input_method_list);
        mImList = new ArrayList<InputMethodInfo>();
        mListView.setAdapter(null);
        // get IM
        InputMethodManager manager = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mImList = manager.getInputMethodList();
        mDefaultId = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        
        int count = (mImList == null ? 0 : mImList
                .size());
//        for (int i = 0; i < count; ++i) {
//            InputMethodInfo property = mImList.get(i);
//            String prefKey = property.getId();
//
//            CharSequence label = property.loadLabel(mContext.getPackageManager());
//            boolean systemIME = isSystemIme(property);
//            // Add a check box.
//            // Don't show the toggle if it's the only keyboard in the system, or
//            // it's a system IME.
//            if (mHaveHardKeyboard || (count > 1 && !isSystemIme(property))) {
//                CheckBoxPreference chkbxPref = new CheckBoxPreference(this);
//                chkbxPref.setKey(prefKey);
//                chkbxPref.setTitle(label);
//                textCategory.addPreference(chkbxPref);
//                mCheckboxes.add(chkbxPref);
//            }
//
//            // If setting activity is available, add a setting screen entry.
//            if (null != property.getSettingsActivity()) {
//                PreferenceScreen prefScreen = new PreferenceScreen(this, null);
//                String settingsActivity = property.getSettingsActivity();
//                if (settingsActivity.lastIndexOf("/") < 0) {
//                    settingsActivity = property.getPackageName() + "/" + settingsActivity;
//                }
//                prefScreen.setKey(settingsActivity);
//                prefScreen.setTitle(label);
//                if (N == 1) {
//                    prefScreen.setSummary(getString(R.string.onscreen_keyboard_settings_summary));
//                } else {
//                    CharSequence settingsLabel = getResources().getString(
//                            R.string.input_methods_settings_label_format, label);
//                    prefScreen.setSummary(settingsLabel);
//                }
//                textCategory.addPreference(prefScreen);
//            }
//        }

        return view;
    }
    private class ImAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    
}
