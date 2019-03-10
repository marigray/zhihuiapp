package com.weex.app.extend.modules;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.weex.app.WXApplication;
import com.weex.app.activities.StudentMainActivity;
import com.weex.app.activities.SurfaceActivity;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;
import com.weex.app.util.upgrade.DownloadService;

import java.io.File;

import id.zelory.compressor.Compressor;
import okhttp3.Call;


public class NativeUtilsModule extends WXModule {
    private final String TAG = "FModule";
    private String downloadApkUrl;
    private String apkName;
    private String newApkVersionName;

    @JSMethod(uiThread = true)
    public void checkVersion() {
        getApkVersion();
    }

    @JSMethod(uiThread = false)
    public String getVersionName() {
        String localVersionName = "";
        try {
            localVersionName = WXApplication.getMyApplication().getPackageManager().getPackageInfo(WXApplication.getMyApplication().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersionName;
    }

    public void getApkVersion() {
        String url = WXApplication.SERVER_URL + "/edu/apk/getApk?source=android&type=1";
        OkhttpUtil.okHttpGet(url, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Logger.e("zhihui", e.toString());
            }

            @Override
            public void onResponse(final String response) {
                try {
                    JSONObject result = JSON.parseObject(response);
                    if (result.getInteger("httpCode") == 200) {
                        int serverApkVersioncode = result.getJSONObject("content").getInteger("versioncode");
                        downloadApkUrl = result.getJSONObject("content").getString("downloadurl");
                        newApkVersionName = result.getJSONObject("content").getString("versionName");
                        apkName = "zhihuis" + serverApkVersioncode + ".apk";
                        /** * 获取到当前的本地版本 */
                        int localVersioncode = WXApplication.getMyApplication().getPackageManager().getPackageInfo(WXApplication.getMyApplication().getPackageName(), 0).versionCode;
                        if (localVersioncode < serverApkVersioncode) {
                            normalUpdate(mWXSDKInstance.getContext());
                        } else {
                            CommonUtils.showShort(mWXSDKInstance.getContext(), "当前已经是最新版本！");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("zhihui", e.toString());
                }
            }
        });
    }

    private AlertDialog.Builder mDialog;

    private void normalUpdate(final Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新");
        mDialog.setMessage("新版本" + newApkVersionName + "已经可以下载，是否更新？");
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(mWXSDKInstance.getContext(), DownloadService.class);
                intent.putExtra("APK_DOWNLOAD_URL", downloadApkUrl);
                intent.putExtra("APK_NAME", apkName);
                mWXSDKInstance.getContext().startService(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }

}
