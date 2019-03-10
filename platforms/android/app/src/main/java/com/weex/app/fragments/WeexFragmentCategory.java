package com.weex.app.fragments;

import android.view.ViewGroup;

import com.weex.app.R;
import com.weex.app.util.CommonUtils;

public class WeexFragmentCategory extends WeexFragmentHome {

    protected int getLayoutResource() {
        return R.layout.fragment_main;
    }

    protected void initWhenCreateView(ViewGroup viewGroup) {
//        viewGroup.setPadding(0, CommonUtils.getStatusBarHeight(getContext()), 0, 0);
        renderPageByURL("file://assets/dist/category.js");
    }
}
