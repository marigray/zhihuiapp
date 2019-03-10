package com.weex.app.extend.modules;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.weex.app.R;
import com.weex.app.WXApplication;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;
import com.weex.app.util.Util;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;

import java.io.File;
import java.util.HashMap;
import okhttp3.Call;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXEmojiObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;



public class WeChatModule extends WXModule {
    private IWXAPI api;
    public static final String APP_ID = "wx9cc4c9855efaf581";
    public static final int WXSceneSession = 0;
    public static final int WXSceneTimeline = 1;
    public static final int WXSceneFavorite = 2;
    private static final int THUMB_SIZE = 150;

    @JSMethod(uiThread = true)
    public void shareTextWX(JSONObject params) {
        WXApplication application = (WXApplication)mWXSDKInstance.getContext().getApplicationContext();
        api = WXAPIFactory.createWXAPI(application,APP_ID);
        String text = params.getString("text");
        int mTargetScene = params.getIntValue("wxscene"); // 0 1 2
        // 初始化一个WXTextObject对象,填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = mTargetScene;
        api.sendReq(req);
    }


    @JSMethod(uiThread = true)
    public void shareImageWx(JSONObject params) {
        WXApplication application = (WXApplication)mWXSDKInstance.getContext().getApplicationContext();
        api = WXAPIFactory.createWXAPI(application,APP_ID);
        String path = params.getString("filepath");
        int mTargetScene = params.getIntValue("wxscene");// 0 1 2
        File file = new File(path);
        if (file.exists()) {
            WXImageObject imgObj = new WXImageObject();
            imgObj.setImagePath(path);

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;

            Bitmap bmp = BitmapFactory.decodeFile(path);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = mTargetScene;
            api.sendReq(req);
        }
    }

    @JSMethod(uiThread = true)
    public void shareUrlWx(JSONObject params) {
        WXApplication application = (WXApplication)mWXSDKInstance.getContext().getApplicationContext();
        api = WXAPIFactory.createWXAPI(application,APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        int mTargetScene = params.getIntValue("wxscene");// 0 1 2
        webpage.webpageUrl = params.getString("url");
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = params.getString("title");
        msg.description = params.getString("description");
        Bitmap bmp = BitmapFactory.decodeResource(application.getResources(), R.drawable.ic_launcher);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = mTargetScene;
        api.sendReq(req);
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    @JSMethod(uiThread = true)
    public void wechatPay(JSONObject params) {
        PayReq request = new PayReq(); //调起微信APP的对象
        request.appId = params.getString("appId");
        request.partnerId = params.getString("partnerId");
        request.prepayId = params.getString("prepayId");
        request.packageValue = "Sign=WXPay";
        request.nonceStr = params.getString("nonceStr");
        request.timeStamp = params.getString("timeStamp");
        request.sign = params.getString("sign");
        WXApplication application = (WXApplication)mWXSDKInstance.getContext().getApplicationContext();
        application.mWxApi.sendReq(request);//发送调起微信的请求
    }

    @JSMethod(uiThread = true)
    public void weChatPay2(JSONObject params) {
        String url = WXApplication.SERVER_URL+"/edu/wechat/unifiedorder";
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("body", params.getString("body"));
        paramsMap.put("totalFee", params.getString("totalFee"));
        paramsMap.put("cid", params.getString("cid"));
        paramsMap.put("uid", params.getString("uid"));
        paramsMap.put("billno", params.getString("billno"));
        if(params.getString("titleId")!=null){
            if(!params.getString("titleId").equals("0")){
                paramsMap.put("titleId", params.getString("titleId"));
            }
        }
//        paramsMap.put("body", "zhihui-class");
//        paramsMap.put("totalFee", "1");
        OkhttpUtil.okHttpPost(url, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                CommonUtils.showShort(mWXSDKInstance.getContext(),"获取订单信息失败");
                Logger.e("zhihui", e.toString());
            }

            @Override
            public void onResponse(final String response) {
                try {
                    JSONObject result = JSON.parseObject(response);
                    if (result.getInteger("httpCode") == 200) {
                        final String appid = result.getJSONObject("content").getString("appid");
                        final String partnerId = result.getJSONObject("content").getString("partnerid");
                        final String prepayId = result.getJSONObject("content").getString("prepayid");
                        final String nonceStr = result.getJSONObject("content").getString("noncestr");
                        final String timeStamp = result.getJSONObject("content").getString("timestamp");
                        final String sign = result.getJSONObject("content").getString("sign");
                        PayReq request = new PayReq(); //调起微信APP的对象
                        request.appId = appid;
                        request.partnerId = partnerId;
                        request.prepayId = prepayId;
                        request.packageValue = "Sign=WXPay";
                        request.nonceStr = nonceStr;
                        request.timeStamp = timeStamp;
                        request.sign = sign;
                        WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
                        application.mWxApi.sendReq(request);//发送调起微信的请求
                    } else {
                        Logger.e("zhihui", response.toString());
                        try {
                            JSONObject err = JSON.parseObject(response);
                            CommonUtils.showShort(mWXSDKInstance.getContext(),"获取订单信息失败："+err.getString("msg"));
                        }catch (Exception e){
                            CommonUtils.showShort(mWXSDKInstance.getContext(),"获取订单信息失败");
                            Logger.e("zhihui", e.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonUtils.showShort(mWXSDKInstance.getContext(),"获取订单信息失败");
                    Logger.e("zhihui", e.toString());
                }
            }
        });
    }

}
