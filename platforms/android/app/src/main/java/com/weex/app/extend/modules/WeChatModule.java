package com.weex.app.extend.modules;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;

import android.support.v4.app.ActivityCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;
import com.taobao.weex.devtools.common.LogUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.weex.app.R;
import com.weex.app.WXApplication;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;
import com.weex.app.util.Util;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import okhttp3.Call;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;


public class WeChatModule extends WXModule {
    public static final int WXSceneSession = 0;
    public static final int WXSceneTimeline = 1;
    public static final int WXSceneFavorite = 2;
    private static final int THUMB_SIZE = 150;
    private static final int THUMB_WIDTH = 60;
    private static final int SAVECOURSEPIC_REQUEST_EXTERNAL_STORAGE_CODE = 1001;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private JSONObject savePicParams;

    @JSMethod(uiThread = true)
    public void shareTextWX(JSONObject params) {
        WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
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
        application.mWxApi.sendReq(req);
    }


    @JSMethod(uiThread = true)
    public void shareImageWx(JSONObject params) {
        WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
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
            application.mWxApi.sendReq(req);
        }
    }

    @JSMethod(uiThread = true)
    public void shareUrlWx(JSONObject params) {
        WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
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
        application.mWxApi.sendReq(req);
    }


    @JSMethod(uiThread = true)
    public void shareCourse(JSONObject params) {
        genShareCoursePic(params);
    }

    @JSMethod(uiThread = true)
    public void saveCoursePic(JSONObject params) {
        int permission = ActivityCompat.checkSelfPermission(mWXSDKInstance.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            savePicParams = params;
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) mWXSDKInstance.getContext(),
                    PERMISSIONS_STORAGE,
                    SAVECOURSEPIC_REQUEST_EXTERNAL_STORAGE_CODE
            );
        } else {
            saveShareCoursePic(params);
        }
    }

    @JSMethod(uiThread = true)
    public void shareScreenWx(JSONObject params) {
        WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
        int mTargetScene = params.getIntValue("wxscene");// 0 1 2
        Bitmap screen = takeScreenShot((Activity) mWXSDKInstance.getContext());
        Bitmap orcode = BitmapFactory.decodeResource(application.getResources(), R.drawable.orcode);
        Bitmap sharePic = addBitmap(screen, orcode);
        WXImageObject imgObj = new WXImageObject(sharePic);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(sharePic, THUMB_WIDTH, THUMB_SIZE, true);
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        screen.recycle();
        orcode.recycle();
        sharePic.recycle();
        thumbBmp.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = mTargetScene;
        application.mWxApi.sendReq(req);
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
        WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
        application.mWxApi.sendReq(request);//发送调起微信的请求
    }

    @JSMethod(uiThread = true)
    public void weChatPay2(JSONObject params) {
        String url = WXApplication.SERVER_URL + "/edu/wechat/unifiedorder";
        WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
        application.setCourseid(params.getString("cid"));
        String urlParams = url + "?body=" + params.getString("body")
                + "&totalFee=" + params.getString("totalFee")
                + "&cid=" + params.getString("cid")
                + "&uid=" + params.getString("uid")
                + "&billno=" + params.getString("billno");
        if (params.getString("titleId") != null) {
            if (!params.getString("titleId").equals("0")) {
                urlParams = urlParams + "&titleId=" + params.getString("titleId");
            }
        }
        OkhttpUtil.okHttpPost(urlParams, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                CommonUtils.showShort(mWXSDKInstance.getContext(), "获取订单信息失败");
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
                            CommonUtils.showShort(mWXSDKInstance.getContext(), "获取订单信息失败：" + err.getString("msg"));
                        } catch (Exception e) {
                            CommonUtils.showShort(mWXSDKInstance.getContext(), "获取订单信息失败");
                            Logger.e("zhihui", e.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonUtils.showShort(mWXSDKInstance.getContext(), "获取订单信息失败");
                    Logger.e("zhihui", e.toString());
                }
            }
        });
    }

    public static Bitmap takeScreenShot1(Activity act) {
        if (act == null || act.isFinishing()) {
            LogUtil.d("act参数为空.");
            return null;
        }
        // 获取当前视图的view
        View scrView = act.getWindow().getDecorView();
        scrView.setDrawingCacheEnabled(true);
        scrView.buildDrawingCache(true);

        // 获取状态栏高度
        Rect statuBarRect = new Rect();
        scrView.getWindowVisibleDisplayFrame(statuBarRect);
        int statusBarHeight = statuBarRect.top;
        int width = act.getWindowManager().getDefaultDisplay().getWidth();
        int height = act.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap scrBmp = null;
        try {
            // 去掉标题栏的截图
            scrBmp = Bitmap.createBitmap(scrView.getDrawingCache(), 0, statusBarHeight,
                    width, height - statusBarHeight);
        } catch (IllegalArgumentException e) {
            LogUtil.d("#### 旋转屏幕导致去掉状态栏失败");
        }
        scrView.setDrawingCacheEnabled(false);
        scrView.destroyDrawingCache();
        return scrBmp;
    }

    public static Bitmap addBitmap(Bitmap first, Bitmap second) {
        int width = first.getWidth();
        int width2 = second.getWidth();
        int height = first.getHeight() + second.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(255, 255, 255, 255);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, (width - width2) / 2, first.getHeight(), null);
        return result;
    }

    public static Bitmap takeScreenShot(Activity act) {
        if (act == null || act.isFinishing()) {
            LogUtil.d("act参数为空.");
            return null;
        }
        // 获取当前视图的view
        View scrView = act.getWindow().getDecorView().getRootView();
        scrView.setDrawingCacheEnabled(true);
        scrView.buildDrawingCache(true);

        // 获取状态栏高度
        Rect statuBarRect = new Rect();
        scrView.getWindowVisibleDisplayFrame(statuBarRect);
        int statusBarHeight = statuBarRect.top;
        int width = act.getWindowManager().getDefaultDisplay().getWidth();
        int height = act.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap scrBmp = null;
        try {
            // 去掉标题栏的截图
//            scrBmp = Bitmap.createBitmap( scrView.getDrawingCache(), 0, statusBarHeight,
//                    width, height - statusBarHeight);
//            scrBmp = Bitmap.createBitmap( scrView.getDrawingCache());
            scrBmp = Bitmap.createBitmap(scrView.getDrawingCache(), 0, 0, scrView.getMeasuredWidth(),
                    scrView.getMeasuredHeight());
        } catch (IllegalArgumentException e) {
            LogUtil.d("#### 旋转屏幕导致去掉状态栏失败");
        }
        scrView.setDrawingCacheEnabled(false);
        scrView.destroyDrawingCache();
        return scrBmp;
    }

    public void genShareCoursePic(final JSONObject params) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
                URL TopUrl = null;
                URL AvatarUrl = null;
                URL ORUrl = null;

                String imageUrl = params.getString("image");
                String title = params.getString("name");
                String background = params.getString("background");
                String audiences = params.getString("audiences");
                String gains = params.getString("gains");
                String other = params.getString("other");
                String headimgurl = params.getString("headimgurl");
                String teachername = params.getString("teachername");
                String price = params.getString("price");
                String tintroduction = params.getString("tintroduction");
                String time = params.getString("time");
                String area = params.getString("area");
                String address = params.getString("address");
                String orcodeUrl = params.getString("orcodeurl");

//                imageUrl = "http://zh.zhihui-app.com/edu/imag/courseIntroduceImag/_1545018640662.jpg";
//                headimgurl = "http://zh.zhihui-app.com/edu/imag/courseIntroduceImag/_1543930814537.jpg";
//                orcodeUrl = "http://zh.zhihui-app.com/edu/imag/courseIntroduceImag/_1543930814537.jpg";
//                title = "淘宝热卖,淘!你喜欢!";
//                teachername ="谢骄宁";
//                price = "1800";
//                tintroduction = "海关贸易专家";
//                time = "2019-12-18 09:00-12:00";
//                area = "上海";
//                address = "上海梅隆证广场";
//                background = "课程背景课程背景课程背景课程背景课程背景课程背景课程背景课程背景课程背景课程背景课程背景课程背景课程背景课程背景课程背景";
//                audiences = "课程受众课程受众课程受众课程受众课程受众课程受众课程受众课程受众课程受众课程受众";
//                gains = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//                other = "其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍其他介绍";


                try {
                    TopUrl = new URL(imageUrl);
                    AvatarUrl = new URL(headimgurl);
                    ORUrl = new URL(orcodeUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    Bitmap bitTopImage = null;
                    Bitmap bitHeadImage = null;
                    Bitmap orcodeImage = null;
                    if (TopUrl != null) {
                        HttpURLConnection conn = (HttpURLConnection) TopUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitTopImage = BitmapFactory.decodeStream(is);
                        is.close();
                    }
                    if (AvatarUrl != null) {
                        HttpURLConnection conn2 = (HttpURLConnection) AvatarUrl.openConnection();
                        conn2.setDoInput(true);
                        conn2.connect();
                        InputStream is2 = conn2.getInputStream();
                        bitHeadImage = BitmapFactory.decodeStream(is2);
                        is2.close();
                    }
                    if (ORUrl != null) {
                        HttpURLConnection conn3 = (HttpURLConnection) ORUrl.openConnection();
                        conn3.setDoInput(true);
                        conn3.connect();
                        InputStream is3 = conn3.getInputStream();
                        orcodeImage = BitmapFactory.decodeStream(is3);
                        is3.close();
                    }

                    Bitmap shareback = BitmapFactory.decodeResource(application.getResources(), R.drawable.shareback);
                    Bitmap sharePic = Bitmap.createBitmap(1194, 1600, Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(sharePic);

                    Rect srcRect1 = new Rect(0, 0, shareback.getWidth(), shareback.getHeight());
                    Rect dst1 = new Rect(0, 0, 1194, 1600);
                    canvas.drawBitmap(shareback, srcRect1, dst1, null);

                    if (bitTopImage != null) {
                        Rect srcRect2 = new Rect(0, 0, bitTopImage.getWidth(), bitTopImage.getHeight());
                        Rect dst2 = new Rect(0, 0, 1200, 580);
                        canvas.drawBitmap(bitTopImage, srcRect2, dst2, null);
                    }

                    if (orcodeImage != null) {
                        orcodeImage = BitmapFactory.decodeResource(application.getResources(), R.drawable.code);
                        Rect srcRect3 = new Rect(0, 0, orcodeImage.getWidth(), orcodeImage.getHeight());
                        Rect dst3 = new Rect(47, 1432, 195, 1578);
                        canvas.drawBitmap(orcodeImage, srcRect3, dst3, null);
                    }

                    if (bitHeadImage != null) {

                        float scale = 1.0f;//缩放比例
                        int mRadius = 0;//圆的半径
                        //获取bitmap
                        Matrix matrix = new Matrix();
                        Paint paint = new Paint();
                        // 获取bitmap宽高中的小值
                        int minBitMap = Math.min(bitHeadImage.getWidth(), bitHeadImage.getHeight());
                        //取header view宽高中的小值 尽量保证图片内容的显示
                        int minValue = 190;
                        //设置半径
                        mRadius = minValue / 2;
                        //计算缩放比例 一定要*1.0f 因为int之间的计算结果会四舍五入0或1 效果就不美丽了
                        scale = minValue * 1.0f / minBitMap;
                        //设置缩放比例
                        matrix.setScale(scale, scale);
                        matrix.postTranslate(204 - mRadius, 850 - mRadius);
                        /**
                         * 创建着色器 设置着色模式
                         * TileMode的取值有三种：
                         * CLAMP 拉伸 REPEAT 重复  MIRROR 镜像
                         */
                        BitmapShader shader = new BitmapShader(bitHeadImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        //设置矩阵
                        shader.setLocalMatrix(matrix);
                        paint.setShader(shader);
//                    canvas.drawCircle(mRadius, mRadius, mRadius, paint);
                        canvas.drawCircle(204, 850, mRadius, paint);
                    }

                    StaticLayout sl;
                    TextPaint textPaint = new TextPaint();
                    textPaint.setAntiAlias(true);
                    Typeface fontBold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
                    textPaint.setTypeface(fontBold);
                    textPaint.setTextSize(70.0F);

                    if (title != null) {
                        sl = new StaticLayout(title, textPaint, 900, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(150, 620);
                        sl.draw(canvas);
                        canvas.restore();
                    }


                    TextPaint textPaint2 = new TextPaint();
                    textPaint2.setAntiAlias(true);
                    Typeface fontBold2 = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
                    textPaint2.setTypeface(fontBold2);
                    textPaint2.setTextSize(28.0F);

                    if (teachername != null) {
                        sl = new StaticLayout(teachername, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(450, 773);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (price != null) {
                        sl = new StaticLayout(price, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(850, 773);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (tintroduction != null) {
                        sl = new StaticLayout(tintroduction, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(320, 822);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (time != null) {
                        sl = new StaticLayout(time, textPaint2, 500, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(765, 822);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (area != null) {
                        sl = new StaticLayout(area, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(390, 873);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (address != null) {
                        sl = new StaticLayout(address, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(760, 873);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (background != null) {
                        if(background.length()<=70){
                            sl = new StaticLayout(background, textPaint2, 230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        }else {
                            sl = new StaticLayout(background, 0,70,textPaint2,230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END,6);
                        }
                        canvas.save();
                        canvas.translate(110, 1070);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (audiences != null) {
                        if(audiences.length()<=70){
                            sl = new StaticLayout(audiences, textPaint2, 230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        }else {
                            sl = new StaticLayout(audiences, 0,70,textPaint2,230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END,6);
                        }
                        canvas.save();
                        canvas.translate(368, 1070);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (gains != null) {
                        if(gains.length()<=70){
                            sl = new StaticLayout(gains, textPaint2, 230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        }else{
                            sl = new StaticLayout(gains, 0,70,textPaint2,230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END,6);
                        }
                        canvas.save();
                        canvas.translate(626, 1070);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (other != null) {
                        if(other.length()<=70){
                            sl = new StaticLayout(other, textPaint2, 230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        }else{
                            sl = new StaticLayout(other, 0,70,textPaint2,230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END,6);
                        }
                        canvas.save();
                        canvas.translate(884, 1070);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    int mTargetScene = params.getIntValue("wxscene");// 0 1 2
                    WXImageObject imgObj = new WXImageObject(sharePic);
                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = imgObj;
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(sharePic, 110, THUMB_SIZE, true);
                    msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                    sharePic.recycle();
                    thumbBmp.recycle();
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("img");
                    req.message = msg;
                    req.scene = mTargetScene;
                    application.mWxApi.sendReq(req);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void saveShareCoursePic(final JSONObject params) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                WXApplication application = (WXApplication) mWXSDKInstance.getContext().getApplicationContext();
                URL TopUrl = null;
                URL AvatarUrl = null;
                URL ORUrl = null;
                String imageUrl = params.getString("image");
                String title = params.getString("name");
                String background = params.getString("background");
                String audiences = params.getString("audiences");
                String gains = params.getString("gains");
                String other = params.getString("other");
                String headimgurl = params.getString("headimgurl");
                String teachername = params.getString("teachername");
                String price = params.getString("price");
                String tintroduction = params.getString("tintroduction");
                String time = params.getString("time");
                String area = params.getString("area");
                String address = params.getString("address");
                String orcodeUrl = params.getString("orcodeurl");

                try {
                    TopUrl = new URL(imageUrl);
                    AvatarUrl = new URL(headimgurl);
                    ORUrl = new URL(orcodeUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    Bitmap bitTopImage = null;
                    Bitmap bitHeadImage = null;
                    Bitmap orcodeImage = null;
                    if (TopUrl != null) {
                        HttpURLConnection conn = (HttpURLConnection) TopUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitTopImage = BitmapFactory.decodeStream(is);
                        is.close();
                    }
                    if (AvatarUrl != null) {
                        HttpURLConnection conn2 = (HttpURLConnection) AvatarUrl.openConnection();
                        conn2.setDoInput(true);
                        conn2.connect();
                        InputStream is2 = conn2.getInputStream();
                        bitHeadImage = BitmapFactory.decodeStream(is2);
                        is2.close();
                    }
                    if (ORUrl != null) {
                        HttpURLConnection conn3 = (HttpURLConnection) ORUrl.openConnection();
                        conn3.setDoInput(true);
                        conn3.connect();
                        InputStream is3 = conn3.getInputStream();
                        orcodeImage = BitmapFactory.decodeStream(is3);
                        is3.close();
                    }

                    Bitmap shareback = BitmapFactory.decodeResource(application.getResources(), R.drawable.shareback);
                    Bitmap sharePic = Bitmap.createBitmap(1194, 1600, Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(sharePic);

                    Rect srcRect1 = new Rect(0, 0, shareback.getWidth(), shareback.getHeight());
                    Rect dst1 = new Rect(0, 0, 1194, 1600);
                    canvas.drawBitmap(shareback, srcRect1, dst1, null);

                    if (bitTopImage != null) {
                        Rect srcRect2 = new Rect(0, 0, bitTopImage.getWidth(), bitTopImage.getHeight());
                        Rect dst2 = new Rect(0, 0, 1200, 580);
                        canvas.drawBitmap(bitTopImage, srcRect2, dst2, null);
                    }

                    if (orcodeImage != null) {
                        orcodeImage = BitmapFactory.decodeResource(application.getResources(), R.drawable.code);
                        Rect srcRect3 = new Rect(0, 0, orcodeImage.getWidth(), orcodeImage.getHeight());
                        Rect dst3 = new Rect(47, 1432, 195, 1578);
                        canvas.drawBitmap(orcodeImage, srcRect3, dst3, null);
                    }

                    if (bitHeadImage != null) {

                        float scale = 1.0f;//缩放比例
                        int mRadius = 0;//圆的半径
                        //获取bitmap
                        Matrix matrix = new Matrix();
                        Paint paint = new Paint();
                        // 获取bitmap宽高中的小值
                        int minBitMap = Math.min(bitHeadImage.getWidth(), bitHeadImage.getHeight());
                        //取header view宽高中的小值 尽量保证图片内容的显示
                        int minValue = 190;
                        //设置半径
                        mRadius = minValue / 2;
                        //计算缩放比例 一定要*1.0f 因为int之间的计算结果会四舍五入0或1 效果就不美丽了
                        scale = minValue * 1.0f / minBitMap;
                        //设置缩放比例
                        matrix.setScale(scale, scale);
                        matrix.postTranslate(204 - mRadius, 850 - mRadius);
                        /**
                         * 创建着色器 设置着色模式
                         * TileMode的取值有三种：
                         * CLAMP 拉伸 REPEAT 重复  MIRROR 镜像
                         */
                        BitmapShader shader = new BitmapShader(bitHeadImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        //设置矩阵
                        shader.setLocalMatrix(matrix);
                        paint.setShader(shader);
//                    canvas.drawCircle(mRadius, mRadius, mRadius, paint);
                        canvas.drawCircle(204, 850, mRadius, paint);
                    }

                    StaticLayout sl;
                    TextPaint textPaint = new TextPaint();
                    textPaint.setAntiAlias(true);
                    Typeface fontBold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
                    textPaint.setTypeface(fontBold);
                    textPaint.setTextSize(70.0F);

                    if (title != null) {
                        sl = new StaticLayout(title, textPaint, 900, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(150, 620);
                        sl.draw(canvas);
                        canvas.restore();
                    }


                    TextPaint textPaint2 = new TextPaint();
                    textPaint2.setAntiAlias(true);
                    Typeface fontBold2 = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
                    textPaint2.setTypeface(fontBold2);
                    textPaint2.setTextSize(28.0F);

                    if (teachername != null) {
                        sl = new StaticLayout(teachername, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(450, 773);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (price != null) {
                        sl = new StaticLayout(price, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(850, 773);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (tintroduction != null) {
                        sl = new StaticLayout(tintroduction, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(320, 822);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (time != null) {
                        sl = new StaticLayout(time, textPaint2, 500, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(765, 822);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (area != null) {
                        sl = new StaticLayout(area, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(390, 873);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (address != null) {
                        sl = new StaticLayout(address, textPaint2, 300, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(760, 873);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (background != null) {
                        sl = new StaticLayout(background, textPaint2, 230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(110, 1070);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (audiences != null) {
                        sl = new StaticLayout(audiences, textPaint2, 230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(368, 1070);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (gains != null) {
                        sl = new StaticLayout(gains, textPaint2, 230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(626, 1070);
                        sl.draw(canvas);
                        canvas.restore();
                    }

                    if (other != null) {
                        sl = new StaticLayout(other, textPaint2, 230, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        canvas.save();
                        canvas.translate(884, 1070);
                        sl.draw(canvas);
                        canvas.restore();
                    }


                    String fileName = null;
                    //系统图库
                    String galleryPath = Environment.getExternalStorageDirectory()
                            + File.separator + Environment.DIRECTORY_PICTURES
                            + File.separator;
                    // 声明文件对象
                    File file = null;
                    // 声明输出流
                    FileOutputStream outStream = null;
                    Date date = new Date(System.currentTimeMillis());
                    String picName = String.valueOf(date.getTime());
                    try {
                        // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
                        file = new File(galleryPath, picName + ".jpg");

                        // 获得文件相对路径
                        fileName = file.toString();
                        // 获得输出流，如果文件中有内容，追加内容
                        outStream = new FileOutputStream(fileName);
                        if (null != outStream) {
                            sharePic.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                        }

                    } catch (Exception e) {
                        e.getStackTrace();
                    } finally {
                        try {
                            if (outStream != null) {
                                outStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //通知相册更新
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    Uri uri = mWXSDKInstance.getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    mWXSDKInstance.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

                    Looper.prepare();
                    CommonUtils.showShort(mWXSDKInstance.getContext(), "图片已成功保存至图库");
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SAVECOURSEPIC_REQUEST_EXTERNAL_STORAGE_CODE: {

                // 用户取消了权限弹窗
                if (grantResults.length == 0) {
                    CommonUtils.showShort(mWXSDKInstance.getContext(), "无法获取文件存储权限, 请到系统设置开启");
                    return;
                }

                // 用户拒绝了某些权限
                for (int x : grantResults) {
                    if (x == PackageManager.PERMISSION_DENIED) {
                        CommonUtils.showShort(mWXSDKInstance.getContext(), "无法获取文件存储权限, 请到系统设置开启");
                        return;
                    }
                }
                saveShareCoursePic(savePicParams);
            }
        }

    }
}
