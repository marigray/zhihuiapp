package com.zhihuiapp.teacher.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.weex.app.WXApplication;
import com.weex.app.util.ActivityManagerTool;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;

import java.util.HashMap;

import okhttp3.Call;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WXApplication.mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WXApplication.mWxApi.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            if (resp.errCode == 0) {
                String url = WXApplication.SERVER_URL+"/edu/user/WXLogin";
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("code",((SendAuth.Resp) resp).code);
                paramsMap.put("role","teacher");
                OkhttpUtil.okHttpPost(url, paramsMap, new CallBackUtil.CallBackString() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        Logger.e("zhihui",e.toString());;
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = JSON.parseObject(response);
                            if(result.getInteger("httpCode")==200){
                                String userInfo = result.getJSONObject("content").getJSONObject("userInfo").toJSONString();
                                String userid = result.getJSONObject("content").getJSONObject("userInfo").getString("id");
                                CommonUtils.saveSharedPreferences(getApplicationContext(), "userInfo", userInfo);
                                CommonUtils.saveSharedPreferences(getApplicationContext(), "userid", userid);
                                CommonUtils.showShort(getApplicationContext(), "登录成功");
                                ActivityManagerTool.getActivityManager().exit();
                            }else{
                                Logger.e("zhihui",response.toString());
                                CommonUtils.showShort(getApplicationContext(), "登录失败:"+result.getString("msg"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("zhihui",e.toString());
                            CommonUtils.showShort(getApplicationContext(), "登录失败");
                        }
                    }
                });
            } else {
                Log.e("zhihui", "onResp: " + resp.errCode);
                CommonUtils.showShort(this, "登录失败");
            }
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }
}