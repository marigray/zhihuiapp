package com.weex.app.util;

import android.content.res.Resources;

import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;

public class WeexUtil {

    public static void appendDefaultMixinDataToJSONInitObject(com.alibaba.fastjson.JSONObject json) {
        json.put("mangatoonGray", "#9f9fac");
        json.put("mangatoonBlack", "#2d2d2d");
        json.put("mangatoonRed", "#fc5658");
        float statusBarViewHeight = CommonUtils.getStatusBarHeight(WXEnvironment.sApplication);
        float width = Resources.getSystem().getDisplayMetrics().widthPixels;
        float statusBarHeightInWeex = statusBarViewHeight * 750 / width;
        json.put("statusBarHeight", statusBarHeightInWeex);
        json.put("navBarHeight", 90);
        json.put("statusAndNavHeight", statusBarHeightInWeex + 90);
    }
}
