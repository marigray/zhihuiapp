package com.weex.app.extend.modules;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.weex.app.WXApplication;
import com.weex.app.WXPageActivity;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;
import com.weex.app.util.PayResult;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class AliModule extends WXModule {
    private JSCallback mAlipayCallback;

    @JSMethod(uiThread = true)
    public void alipay(JSONObject params,JSCallback callback) {
        this.mAlipayCallback = callback;
        String url = WXApplication.SERVER_URL+"/edu/alipay/getOrderString";
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("body", params.getString("body"));
        paramsMap.put("subject", params.getString("subject"));
        paramsMap.put("totalAmount", params.getString("totalAmount"));
        paramsMap.put("cid", params.getString("cid"));
        paramsMap.put("uid", params.getString("uid"));
        paramsMap.put("billno", params.getString("billno"));
        if(params.getString("titleId")!=null){
            if(!params.getString("titleId").equals("0")){
                paramsMap.put("titleId", params.getString("titleId"));
            }
        }
        OkhttpUtil.okHttpGet(url, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Logger.e("zhihui", e.toString());
                CommonUtils.showShort(mWXSDKInstance.getContext(),"获取订单信息失败");
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG2;
                msg.obj = false;
                mHandler.sendMessage(msg);
            }
            @Override
            public void onResponse(final String response) {
                try {
                    JSONObject result = JSON.parseObject(response);
                    if (result.getInteger("httpCode") == 200) {
                        final String orderInfo = result.getString("content");
//                        WXPageActivity activity = (WXPageActivity) mWXSDKInstance.getContext();
//                        activity.alipay(orderInfo);
                        alipay(orderInfo);
                    } else {
                        Logger.e("zhihui", response.toString());
                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG2;
                        msg.obj = false;
                        mHandler.sendMessage(msg);
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
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG2;
                    msg.obj = false;
                    mHandler.sendMessage(msg);
                    CommonUtils.showShort(mWXSDKInstance.getContext(),"获取订单信息失败");
                    Logger.e("zhihui", e.toString());
                }
            }
        });
    }



    /**
     * 支付宝支付
     */
    public void alipay(String orderInfo) {
        final String ORDER_INFO = orderInfo;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((WXPageActivity)mWXSDKInstance.getContext());
                Map<String, String> result = alipay.payV2(ORDER_INFO, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private static final int SDK_PAY_FLAG = 1001;
    private static final int SDK_PAY_FLAG2 = 1002;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        CommonUtils.showShort((WXPageActivity)mWXSDKInstance.getContext(),"支付成功");
                        mAlipayCallback.invoke(true);
                    } else {
                        CommonUtils.showShort((WXPageActivity)mWXSDKInstance.getContext(),"支付失败");
                        mAlipayCallback.invoke(false);
                    }
                    break;
                }
                case SDK_PAY_FLAG2: {
                    Boolean result = (Boolean)msg.obj;
                    if(result){
                        mAlipayCallback.invoke(true);
                    }else {
                        mAlipayCallback.invoke(false);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
}
