package com.weex.app.fragments;

import android.view.ViewGroup;

import com.weex.app.R;
import com.weex.app.util.CommonUtils;

public class WeexFragmentCourseMng extends WeexFragmentHome {

    protected int getLayoutResource() {
        return R.layout.fragment_category;
    }

    protected void initWhenCreateView(ViewGroup viewGroup) {
//        viewGroup.setPadding(0, CommonUtils.getStatusBarHeight(getContext()), 0, 0);
        renderPageByURL("file://assets/dist/t-coursemanage.js");
    }
}
