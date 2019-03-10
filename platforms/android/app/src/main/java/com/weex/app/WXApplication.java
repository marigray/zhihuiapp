package com.weex.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weex.app.extend.ImageAdapter;
import com.weex.app.extend.LoadingComponent;
import com.weex.app.extend.WXEventModule;
import com.alibaba.weex.plugin.loader.WeexPluginContainer;
import com.weex.app.util.AppConfig;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.weex.app.extend.modules.*;

import cn.jpush.android.api.JPushInterface;

public class WXApplication extends Application {
  private String userid = "";
  private static WXApplication instance;
  public static IWXAPI mWxApi;
  //  public static final String SERVER_URL = "http://116.62.23.7:3060";
  public static final String SERVER_URL = "  http://zh.zhihui-app.com";

  private String courseid = "";

  @Override
  public void onCreate() {
    if (Build.VERSION.SDK_INT>=18) {
      StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
      StrictMode.setVmPolicy(builder.build());
      builder.detectFileUriExposure();
    }
    super.onCreate();
    instance = this;
    WXSDKEngine.addCustomOptions("appName", "WXSample");
    WXSDKEngine.addCustomOptions("appGroup", "WXApp");
    WXSDKEngine.addCustomOptions("scheme", "zhihui");
    WXSDKEngine.initialize(this,
        new InitConfig.Builder().setImgAdapter(new ImageAdapter()).build()
    );
    try {
      WXSDKEngine.registerComponent("myloading", LoadingComponent.class);
      WXSDKEngine.registerModule("event", WXEventModule.class);
      WXSDKEngine.registerModule("user", UsersModule.class);
      WXSDKEngine.registerModule("share", WeChatModule.class);
      WXSDKEngine.registerModule("file", FileModule.class);
      WXSDKEngine.registerModule("myevent", MyEventModule.class);
      WXSDKEngine.registerModule("ali", AliModule.class);
      WXSDKEngine.registerModule("nativeUtils", NativeUtilsModule.class);
    } catch (WXException e) {
      e.printStackTrace();
    }
    AppConfig.init(this);
    WeexPluginContainer.loadAll(this);
    mWxApi = WXAPIFactory.createWXAPI(this, null);
    mWxApi.registerApp("wxf50814326cb86160");

    JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
    JPushInterface.init(this);     		// 初始化 JPush
  }

  public static Context getMyApplication() {
    return instance;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getCourseid() {
    return courseid;
  }

  public void setCourseid(String userid) {
    this.courseid = userid;
  }
}
