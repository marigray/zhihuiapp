package com.weex.app.extend.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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
import com.weex.app.WXPageActivity;
import com.weex.app.activities.SurfaceActivity;
import com.weex.app.util.ActivityManagerTool;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;

import java.io.File;
import java.util.HashMap;

import id.zelory.compressor.Compressor;
import okhttp3.Call;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class FileModule extends WXModule {
    private final String TAG = "FModule";
    private static final int REQUEST_CODE_PICK_VIDEO= 1;

//    @JSMethod(uiThread = true)
//    public void pickVideo(JSONObject params) {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("video/*");
//        WXPageActivity.g_UploadVideoKey = params.getString("name");
//        ((Activity)mWXSDKInstance.getContext()).startActivityForResult(Intent.createChooser(intent, "请选择视频文件"), REQUEST_CODE_PICK_VIDEO);
//    }

    @JSMethod(uiThread = true)
    public void pickVideo(JSONObject params, JSCallback thumbnailCallback,JSCallback callback) {
        WXPageActivity activity = (WXPageActivity) mWXSDKInstance.getContext();
        activity.pickVideo(params.getString("name"), thumbnailCallback,callback);
    }


    @JSMethod(uiThread = true)
    public void playVideo(JSONObject params) {
        String url = params.getString("url");
        Intent intent = new Intent((Activity)mWXSDKInstance.getContext(), SurfaceActivity.class);
        intent.putExtra("VIDEO_PATH", url);
        ((Activity)mWXSDKInstance.getContext()).startActivity(intent);
    }

    @JSMethod(uiThread = true)
    public void compressor(String filePath,JSCallback callback) {
        try {
            File actualImageFile = new File(filePath);
            File compressedImageFile = new Compressor(mWXSDKInstance.getContext())
                    .setDestinationDirectoryPath(getDiskCacheDir(mWXSDKInstance.getContext()))
                    .compressToFile(actualImageFile);
            String retPath = compressedImageFile.getPath();
            callback.invoke(retPath);
        }catch (Exception e){
            Logger.e(TAG,e.toString());
            callback.invoke("");
        }

    }

    public String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

}
