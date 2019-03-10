package com.weex.app.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.IWXDebugProxy;
import com.taobao.weex.common.WXRenderStrategy;
import com.weex.app.AbsWeexActivity;
import com.weex.app.R;
import com.weex.app.WXApplication;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.WeexUtil;

import java.util.HashMap;
import java.util.Map;


public class WeexFragmentHome extends Fragment implements IWXRenderListener {
    private static final String TAG = "WeexFragmentHome";
    protected ViewGroup mFragmentView;
    protected WXSDKInstance mInstance;
    private String mPageName = TAG;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(getLayoutResource(), container, true);
        mFragmentView = viewGroup.findViewById(R.id.container);
        createWeexInstance();
        this.initWhenCreateView(viewGroup);
        return viewGroup;
    }

    protected int getLayoutResource() {
        return R.layout.fragment_main;
    }

    protected void initWhenCreateView(ViewGroup viewGroup) {
        renderPageByURL("file://assets/dist/home.js");
    }


    protected void createWeexInstance() {
        destoryWeexInstance();
        mInstance = new WXSDKInstance(getActivity());
        mInstance.registerRenderListener(this);
    }

    protected void destoryWeexInstance() {
        if (mInstance != null) {
            mInstance.onActivityStop();
            mInstance.onActivityDestroy();
            mInstance.registerRenderListener(null);
            mInstance.destroy();
            mInstance = null;
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.destoryWeexInstance();
    }

    protected void renderPageByURL(String url) {
        com.alibaba.fastjson.JSONObject data = new com.alibaba.fastjson.JSONObject();
        WeexUtil.appendDefaultMixinDataToJSONInitObject(data);
        renderPageByURL(url, data.toJSONString());
    }

    protected void renderPageByURL(String url, String jsonInitData) {
        CommonUtils.throwIfNull(mFragmentView, new RuntimeException("Can't render page, container is null"));
        Map<String, Object> options = new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL, url);
        mInstance.renderByUrl(getPageName(), url, options, jsonInitData,
            CommonUtils.getDisplayWidth(getActivity()),
            CommonUtils.getDisplayHeight(getActivity()),
            WXRenderStrategy.APPEND_ASYNC);
    }

    public String getPageName() {
        return mPageName;
    }


    @Override
    public void onViewCreated(WXSDKInstance wxsdkInstance, View view) {
        if (mFragmentView != null) {
            mFragmentView.removeAllViews();
            mFragmentView.addView(view);
        }
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance wxsdkInstance, int width, int height) {

    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
    }

//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if (isVisibleToUser) {
//            final Tracker tracker = ((WXApplication) getActivity().getApplication()).getDefaultTracker();
//            if (tracker != null) {
//                tracker.setScreenName(getClass().getSimpleName());
//                tracker.send(new HitBuilders.ScreenViewBuilder().build());
//            }
//        }
//    }

    public void onActivityResume() {
        if (mInstance != null) mInstance.onActivityResume();
    }

    public void onActivityStop() {
        if (mInstance != null) mInstance.onActivityStop();
    }

    public void onActivityDestroy() {
        if (mInstance != null) mInstance.onActivityDestroy();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mInstance != null) mInstance.onActivityResult(requestCode, resultCode, data);
    }
}
