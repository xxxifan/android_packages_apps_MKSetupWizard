
package com.mokee.mksetupwizard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mokee.mksetupwizard.MainActivity;

public class NavButton extends Button {

    private Context mContext;

    public NavButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("xifan", "onclick");
                ((MainActivity) mContext).goNextPage();

            }
        });
    }

}
