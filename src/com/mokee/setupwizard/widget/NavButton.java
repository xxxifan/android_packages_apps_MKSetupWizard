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

package com.mokee.setupwizard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.mokee.setupwizard.SetupWizardActivity;

public class NavButton extends Button {

    private Context mContext;

    public NavButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((SetupWizardActivity) mContext).goNextPage();
            }
        });
    }

}
