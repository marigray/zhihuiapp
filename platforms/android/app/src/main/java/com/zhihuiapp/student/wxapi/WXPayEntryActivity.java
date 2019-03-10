package com.zhihuiapp.student.wxapi;


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
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weex.app.WXApplication;
import com.weex.app.extend.modules.MyEventModule;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WXApplication.mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WXApplication.mWxApi.handleIntent(getIntent(), this);
    }


    @Override
    public void onResp(BaseResp resp) {
        MyEventModule globalEvent = new MyEventModule();
        WXApplication application = (WXApplication)this.getApplicationContext();
        String courseid = application.getCourseid();
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                CommonUtils.showShort(this, "支付成功");
                JSONObject result = new JSONObject();
                result.put("type",1);
                result.put("courseid",courseid);
                result.put("result",true);
                globalEvent.sendGlobal("pay-result",result);
            } else if(resp.errCode == -2) {
                Logger.e("java", "onResp: " + resp.errCode);
                CommonUtils.showShort(this, "用户取消");
                JSONObject result = new JSONObject();
                result.put("type",1);
                result.put("courseid",courseid);
                result.put("result",false);
                globalEvent.sendGlobal("pay-result",result);
            }else{
                Logger.e("java", "onResp: " + resp.errCode);
                CommonUtils.showShort(this, "支付失败");
                JSONObject result = new JSONObject();
                result.put("type",1);
                result.put("courseid",courseid);
                result.put("result",false);
                globalEvent.sendGlobal("pay-result",result);
            }
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }
}