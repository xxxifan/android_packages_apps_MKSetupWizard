
package com.mokee.mksetupwizard.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.inputmethod.InputMethodInfo;

public class InputMethodItem {
    private String imLabel;
    private String imPackage;

    public InputMethodItem(Context context, InputMethodInfo info) {
        PackageManager pm = context.getPackageManager();
        imLabel = info.loadLabel(pm).toString();
        imPackage = getRealName(info);
    }

    private String getRealName(InputMethodInfo info) {
        String packageName = info.getPackageName();
        return packageName + "/" + info.getServiceName().replace(packageName, "");
    }

    public String getImLabel() {
        return imLabel;
    }

    public String getImPackage() {
        return imPackage;
    }

}
