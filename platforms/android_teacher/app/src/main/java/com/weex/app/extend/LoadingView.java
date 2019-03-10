package com.weex.app.extend;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.weex.app.R;

public class LoadingView extends LinearLayout {


    public LoadingView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.loading, this);
    }

}
