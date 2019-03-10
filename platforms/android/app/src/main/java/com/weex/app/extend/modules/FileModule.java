package com.weex.app.extend.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.weex.app.WXPageActivity;
import com.weex.app.activities.SurfaceActivity;
import com.weex.app.util.Logger;

import java.io.File;

import id.zelory.compressor.Compressor;


public class FileModule extends WXModule {
    private final String TAG = "FModule";

    @JSMethod(uiThread = true)
    public void playVideo(JSONObject params) {
        String url = params.getString("url");
        String isPlayall = params.getString("playall");//传"true"播放全部,不传值或者传"false"播放3分钟
        Intent intent = new Intent((Activity)mWXSDKInstance.getContext(), SurfaceActivity.class);
        intent.putExtra("VIDEO_PATH", url);
        if(isPlayall!=null&&isPlayall=="true"){
            intent.putExtra("PLAY_ALL", true);
        }else {
            intent.putExtra("PLAY_ALL", false);
        }

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
