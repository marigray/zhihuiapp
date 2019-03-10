package com.weex.app.extend.modules;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.weex.app.R;
import com.weex.app.WXApplication;
import com.weex.app.util.ActivityManagerTool;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;
import com.weex.app.util.StringUtils;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;

import java.util.HashMap;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;


public class UsersModule extends WXModule {
    private final String TAG = "UsersModule";
    public static int sequence = 0;

    @JSMethod(uiThread = true)
    public void saveUserid(JSONObject params) {
//        Log.d("zhihui",params.getString("userid"));
//        WXApplication application = (WXApplication)mWXSDKInstance.getContext().getApplicationContext();
//        application.setUserid(params.getString("userid"));
        String userid = params.getString("userid");
        CommonUtils.saveSharedPreferences(mWXSDKInstance.getContext().getApplicationContext(), "userid", userid);
    }

//    @JSMethod(uiThread = true)
//    public void getUserid(final JSCallback callback) {
////        WXApplication application = (WXApplication)mWXSDKInstance.getContext().getApplicationContext();
//        String id = CommonUtils.getSharedPreferencesString(mWXSDKInstance.getContext().getApplicationContext(), "userid");
//        if(id==null){
//            id = "0";
//        }
//        JSONObject result = new JSONObject();
//        result.put("userid", id);
//        callback.invoke(result);
//    }

    @JSMethod(uiThread = true)
    public void logout() {
        CommonUtils.saveSharedPreferences(mWXSDKInstance.getContext().getApplicationContext(), "userid", null);
    }

    @JSMethod(uiThread = false)
    public String getUserid() {
//        WXApplication application = (WXApplication)mWXSDKInstance.getContext().getApplicationContext();
        String id = CommonUtils.getSharedPreferencesString(mWXSDKInstance.getContext().getApplicationContext(), "userid");
        if(id==null){
            id = "0";
        }
        return id;
    }

    @JSMethod(uiThread = true)
    public void login(JSONObject params,final JSCallback callback) {
        String url = WXApplication.SERVER_URL+"/edu/user/login";
        HashMap<String, String> paramsMap = new HashMap<>();
//        paramsMap.put("account","admin@admin.com");
//        paramsMap.put("password","admin");
        paramsMap.put("account",params.getString("account"));
        paramsMap.put("password",params.getString("password"));
        paramsMap.put("platform",params.getString("2"));
        OkhttpUtil.okHttpPost(url, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Logger.e("zhihui",e.toString());;
                callback.invoke(e.toString());
            }

            @Override
            public void onResponse(String response) {
                Logger.e("zhihui",response.toString());
                callback.invoke(response);
            }
        });
    }

    @JSMethod(uiThread = true)
    public void signIn(JSONObject params,final JSCallback callback) {
        String url = WXApplication.SERVER_URL+"/edu/user/signIn";
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("phone",params.getString("account"));
        paramsMap.put("email",params.getString("password"));
        paramsMap.put("password",params.getString("account"));
        paramsMap.put("code",params.getString("password"));
        paramsMap.put("roleCode",params.getString("password"));
        OkhttpUtil.okHttpPost(url, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
//                Logger.e("zhihui",e.toString());
                callback.invoke(e.toString());
            }

            @Override
            public void onResponse(String response) {
//                Logger.e("zhihui",response.toString());
                try {
                    JSONObject result = JSON.parseObject(response);
                    if(result.getInteger("httpCode")==200){
                        String userInfo = result.getJSONObject("content").getJSONObject("userInfo").toJSONString();
                        String userid = result.getJSONObject("content").getJSONObject("userInfo").getString("id");
                        CommonUtils.saveSharedPreferences(mWXSDKInstance.getContext().getApplicationContext(), "userInfo", userInfo);
                        CommonUtils.saveSharedPreferences(mWXSDKInstance.getContext().getApplicationContext(), "userid", userid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callback.invoke(response);
            }
        });
    }

    @JSMethod(uiThread = true)
    public void getProfile(final JSCallback callback) {
        String userInfo = CommonUtils.getSharedPreferencesString(mWXSDKInstance.getContext().getApplicationContext(), "userInfo");
//        JSONObject result = new JSONObject();
//        result.put("userinfo", userinfo);
        callback.invoke(userInfo);
    }

    @JSMethod(uiThread = true)
    public void weChatLogin(){
        WXApplication application = (WXApplication)mWXSDKInstance.getContext().getApplicationContext();
        if (!application.mWxApi.isWXAppInstalled()) {
            Toast.makeText(application, R.string.nowechat, Toast.LENGTH_SHORT).show();
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "zhihui_wx_login";
        WXApplication.mWxApi.sendReq(req);
    }

    @JSMethod(uiThread = true)
    public void exitWXPages(){
        ActivityManagerTool.getActivityManager().exit();
    }


    @JSMethod(uiThread = true)
    public void getJPushAlias(final JSCallback callback) {
        String jpushAlias = CommonUtils.getSharedPreferencesString(mWXSDKInstance.getContext(),"jpushAlias");
        if (StringUtils.isNullOrEmpty(jpushAlias)) {
            String rid = JPushInterface.getRegistrationID(mWXSDKInstance.getContext());
            if(StringUtils.isNullOrEmpty(rid)){
                jpushAlias = UUID.randomUUID().toString().replace("-", "");
            }else{
                jpushAlias = rid;
            }
            CommonUtils.saveSharedPreferences(mWXSDKInstance.getContext().getApplicationContext(), "jpushAlias", jpushAlias);
        }
        sequence++;
        JPushInterface.setAlias(mWXSDKInstance.getContext(),sequence,jpushAlias);
        callback.invoke(jpushAlias);
    }

    @JSMethod(uiThread = true)
    public void JSLog(String params) {
        Logger.d("streamlog",params);
    }

}
