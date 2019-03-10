package com.weex.app.extend.modules;

import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyEventModule extends WXModule {

    @JSMethod(uiThread = true)
    public void sendGlobal(String eventName,JSONObject params) {
        Map<String,Object> ret_params=new HashMap<>();
        ret_params.put("result",params);
        List<WXSDKInstance> instances = WXSDKManager.getInstance().getWXRenderManager().getAllInstances();
        for (WXSDKInstance instance : instances) {
            instance.fireGlobalEventCallback(eventName, ret_params);
        }
    }

}
