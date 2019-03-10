package com.weex.app;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.jaeger.library.StatusBarUtil;
import com.qiniu.android.storage.UploadOptions;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.utils.WXFileUtils;
import com.weex.app.hotreload.HotReloadManager;
import com.weex.app.util.ActivityManagerTool;
import com.weex.app.util.AppConfig;
import com.weex.app.util.CommonUtils;
import com.weex.app.util.Constants;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXRenderErrorCode;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.ui.component.NestedContainer;
import com.taobao.weex.utils.WXLogUtils;
import com.taobao.weex.utils.WXSoInstallMgrSdk;
import com.weex.app.util.Logger;
import com.weex.app.util.PayResult;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.http.ResponseInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;


public class WXPageActivity extends AbsWeexActivity implements
    WXSDKInstance.NestedInstanceInterceptor {

  private static final String TAG = "WXPageActivity";
  private ProgressBar mProgressBar;
  private TextView mTipView;
  private boolean mFromSplash = false;
  private HotReloadManager mHotReloadManager;
  private String selectedVideoPath;
  private boolean isCancelled = false;
  private static final int REQUEST_CODE_PICK_VIDEO= 1;
  private ProgressDialog progressDialog;
  private AlertDialog alertDialog;
  private static String[] PERMISSIONS_STORAGE = {
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
  };
  private static final int PickVideo_REQUEST_EXTERNAL_STORAGE_CODE = 1001;
  public static String g_UploadVideoKey;
  private JSCallback mPickVideoCallback;
  private JSCallback mPickVideoThumbnailCallback;

  @Override
  public void onCreateNestInstance(WXSDKInstance instance, NestedContainer container) {
    Log.d(TAG, "Nested Instance created.");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityManagerTool.getActivityManager().add(this);
    setContentView(R.layout.activity_wxpage);
//    StatusBarUtil.setTranslucentForImageView(this, 0, null);
    mContainer = (ViewGroup) findViewById(R.id.container);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);
    mTipView = (TextView) findViewById(R.id.index_tip);

    com.alibaba.fastjson.JSONObject data = new com.alibaba.fastjson.JSONObject();

    Intent intent = getIntent();
    Uri uri = intent.getData();
    String from = intent.getStringExtra("from");
    mFromSplash = "splash".equals(from);
    if (uri != null && uri.getScheme().equals("zhihuit")){
      try {
        InputStream is = getResources().getAssets().open("dist/t-" + uri.getHost() + ".js");
        mUri = Uri.parse("file://assets/dist/t-" + uri.getHost() + ".js");
        is.close();
      } catch (IOException e) {
        mUri = Uri.parse("file://assets/dist/" + uri.getHost() + ".js");
        e.printStackTrace();
      }
      for (String key : uri.getQueryParameterNames()) {
        String value = uri.getQueryParameter(key);
        if ("TRUE".equals(value)) {
          data.put(key, Boolean.TRUE);
        } else if ("FALSE".equals(value)) {
          data.put(key, Boolean.FALSE);
        } else
          data.put(key, value);
      }
    }
    if (uri == null) {
      uri = Uri.parse("{}");
    }
    if (uri != null) {
      try {
        JSONObject initData = new JSONObject(uri.toString());
        String bundleUrl = initData.optString("WeexBundle", null);
        if (bundleUrl != null) {
          mUri = Uri.parse(bundleUrl);
        }

        String ws = initData.optString("Ws", null);
        if (!TextUtils.isEmpty(ws)) {
          mHotReloadManager = new HotReloadManager(ws, new HotReloadManager.ActionListener() {
            @Override
            public void reload() {
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(WXPageActivity.this, "Hot reload", Toast.LENGTH_SHORT).show();
                  createWeexInstance();
                  renderPage();
                }
              });
            }

            @Override
            public void render(final String bundleUrl) {
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(WXPageActivity.this, "Render: " + bundleUrl, Toast.LENGTH_SHORT).show();
                  createWeexInstance();
                  loadUrl(bundleUrl);
                }
              });
            }
          });
        } else {
          WXLogUtils.w("Weex", "can not get hot reload config");
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    if (mUri == null) {
      if ("file".equals(uri.getScheme())) mUri = uri;
      else if ("http".equals(uri.getScheme())) mUri = uri;
      else if ("https".equals(uri.getScheme())) mUri = uri;
    }
    if (mUri == null) {
      mUri = Uri.parse(AppConfig.getLaunchUrl());
    }

    if (!WXSoInstallMgrSdk.isCPUSupport()) {
      mProgressBar.setVisibility(View.INVISIBLE);
      mTipView.setText(R.string.cpu_not_support_tip);
      return;
    }

    String url = getUrl(mUri);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(url);
      getSupportActionBar().hide();
    }
//    loadUrl(url);
    renderPageByURL(url, data.toJSONString());
    initAlertDialog();
    initProgressDialog();
  }

    protected void renderPageByURL(String url, String jsonInitData) {

        CommonUtils.throwIfNull(mContainer, new RuntimeException("Can't render page, container is null"));
        Map<String, Object> options = new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL, url);
        mInstance.renderByUrl(getPageName(), url, options, jsonInitData, WXRenderStrategy.APPEND_ASYNC);
    }


    private String getUrl(Uri uri) {
    String url = uri.toString();
    String scheme = uri.getScheme();
    if (uri.isHierarchical()) {
      if (TextUtils.equals(scheme, "http") || TextUtils.equals(scheme, "https")) {
        String weexTpl = uri.getQueryParameter(Constants.WEEX_TPL_KEY);
        if (!TextUtils.isEmpty(weexTpl)) {
          url = weexTpl;
        }
      }
    }
    return url;
  }

  protected void preRenderPage() {
    mProgressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Intent intent = new Intent("requestPermission");
    intent.putExtra("REQUEST_PERMISSION_CODE", requestCode);
    intent.putExtra("permissions", permissions);
    intent.putExtra("grantResults", grantResults);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    switch (requestCode) {
      case PickVideo_REQUEST_EXTERNAL_STORAGE_CODE: {

        // 用户取消了权限弹窗
        if (grantResults.length == 0) {
          CommonUtils.showShort(this, "无法获取文件上传所需的权限, 请到系统设置开启");
          return;
        }

        // 用户拒绝了某些权限
        for (int x : grantResults) {
          if (x == PackageManager.PERMISSION_DENIED) {
            CommonUtils.showShort(this, "无法获取文件上传所需的权限, 请到系统设置开启");
            return;
          }
        }

        //用户授权
        if(CommonUtils.isFlyme()){
          Intent intent_pickVideo = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("video/*");
          intent.addCategory(Intent.CATEGORY_OPENABLE);
          startActivityForResult(Intent.createChooser(intent, "请选择视频文件"), REQUEST_CODE_PICK_VIDEO);
        }else{
          Intent intent_pickVideo = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          intent_pickVideo.setType("video/*");
          startActivityForResult(Intent.createChooser(intent_pickVideo, "请选择视频文件"), REQUEST_CODE_PICK_VIDEO);
        }
      }
    }
  }


  @Override
  public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.GONE);
  }

  @Override
  public void onException(WXSDKInstance instance, String errCode, String msg) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.VISIBLE);
    if (TextUtils.equals(errCode, WXRenderErrorCode.WX_NETWORK_ERROR)) {
      mTipView.setText(R.string.index_tip);
    } else {
      mTipView.setText("render error:" + errCode);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(mFromSplash ? R.menu.main_scan : R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_refresh:
        createWeexInstance();
        renderPage();
        break;
      case R.id.action_scan:
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        //integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setPrompt(getString(R.string.capture_qrcode_prompt));
        integrator.initiateScan();
        break;
      case android.R.id.home:
        finish();
      default:
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if (result != null) {
      if (result.getContents() == null) {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
      } else {
        handleDecodeInternally(result.getContents());
      }
    }
      switch (requestCode) {
          case REQUEST_CODE_PICK_VIDEO:
              if (resultCode == RESULT_OK) { // Get the Uri of the selected file
                  Uri uri = data.getData();
                  Log.d(TAG, "File Uri: " + uri.toString());
                  // Get the path
                  try {
                    selectedVideoPath = getPath(this, uri);
                    alertDialog.show();
                  } catch (Exception e) {

                  }
                  // Get the file instance
                  // File file = new File(path);
                  // Initiate the upload
              }
              break;
      }
    super.onActivityResult(requestCode, resultCode, data);
  }

  // Put up our own UI for how to handle the decoded contents.
  private void handleDecodeInternally(String code) {

    if (!TextUtils.isEmpty(code)) {
      Uri uri = Uri.parse(code);
      if (uri.getQueryParameterNames().contains("bundle")) {
        WXEnvironment.sDynamicMode = uri.getBooleanQueryParameter("debug", false);
        WXEnvironment.sDynamicUrl = uri.getQueryParameter("bundle");
        String tip = WXEnvironment.sDynamicMode ? "Has switched to Dynamic Mode" : "Has switched to Normal Mode";
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
        finish();
        return;
      } else if (uri.getQueryParameterNames().contains("_wx_devtool")) {
        WXEnvironment.sRemoteDebugProxyUrl = uri.getQueryParameter("_wx_devtool");
        WXEnvironment.sDebugServerConnectable = true;
        WXSDKEngine.reload();
        Toast.makeText(this, "devtool", Toast.LENGTH_SHORT).show();
        return;
      } else if (code.contains("_wx_debug")) {
        uri = Uri.parse(code);
        String debug_url = uri.getQueryParameter("_wx_debug");
        WXSDKEngine.switchDebugModel(true, debug_url);
        finish();
      } else {
        JSONObject data = new JSONObject();
        try {
          data.put("WeexBundle", Uri.parse(code).toString());
          Intent intent = new Intent(WXPageActivity.this, WXPageActivity.class);
          intent.setData(Uri.parse(data.toString()));
          startActivity(intent);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mHotReloadManager != null) {
      mHotReloadManager.destroy();
    }
    ActivityManagerTool.getActivityManager().removeActivity(this);
  }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) { // Eat it Or Log it.
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

  /*
初始化AlertDialog
 */
  private void initAlertDialog() { //创建AlertDialog的构造器的对象
    AlertDialog.Builder builder = new AlertDialog.Builder(WXPageActivity.this);
    builder.setTitle("提示");
    builder.setIcon(R.mipmap.ic_launcher);
    builder.setMessage("是否要上传该文件？");
    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        isCancelled = false;
        progressDialog.show();
        progressDialog.setProgress(0);
        String url = WXApplication.SERVER_URL+"/edu/video/getUpToken";
        OkhttpUtil.okHttpPost(url, new CallBackUtil.CallBackString() {
          @Override
          public void onFailure(Call call, Exception e) {
            Logger.e("zhihui", e.toString());
            CommonUtils.showShort(WXPageActivity.this,"连接服务器失败");
            progressDialog.dismiss();
          }

          @Override
          public void onResponse(final String response) {
            try {
              com.alibaba.fastjson.JSONObject result = JSON.parseObject(response);
              if (result.getInteger("httpCode") == 200) {
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(selectedVideoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String thumbName = sdf.format(new Date())+".jpg";
                String filePath = getDiskCacheDir(WXPageActivity.this);
                saveImage(bitmap,filePath,thumbName);
                mPickVideoThumbnailCallback.invoke(filePath+"/"+thumbName);
                final String token = result.getString("content");
                uploadImageToQiniu(selectedVideoPath, token);
              } else {
                Logger.e("zhihui", response.toString());
                CommonUtils.showShort(WXPageActivity.this,"连接服务器失败");
              }
            } catch (Exception e) {
              e.printStackTrace();
              Logger.e("zhihui", e.toString());
              CommonUtils.showShort(WXPageActivity.this,"连接服务器失败");
            }
          }
        });
      }
    });
    builder.setNegativeButton("取消", null);
    alertDialog = builder.create();
  }

  private void initProgressDialog() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setCancelable(false);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setIcon(R.mipmap.ic_launcher);
    progressDialog.setTitle("提示");
    progressDialog.setMax(100);
    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                isCancelled = true;
              }
            });

    progressDialog.setMessage("正在上传");
  }


  /**
   * 上传图片到七牛
   *
   * @param filePath 要上传的图片路径
   * @param token    在七牛官网上注册的token
   */
  private void uploadImageToQiniu(String filePath, String token) {
    UploadManager uploadManager = new UploadManager();
    // 设置图片名字
    String key = g_UploadVideoKey+getExtension(filePath);
    uploadManager.put(filePath, key, token, new UpCompletionHandler() {
      @Override
      public void complete(String key, ResponseInfo info, org.json.JSONObject response) {
        // info.error中包含了错误信息，可打印调试
        // 上传成功后将key值上传到自己的服务器
        try{
          if(info.statusCode==200){
            CommonUtils.showShort(WXPageActivity.this, "上传成功");
            mPickVideoCallback.invoke(key);
          }else{
            mPickVideoCallback.invoke(null);
            CommonUtils.showShort(WXPageActivity.this, "上传失败："+info.error);
            Logger.e("zhihui",info.error);
          }
        }catch (Exception e){
          Logger.e("zhihui",e.toString());
        }
        progressDialog.dismiss();
      }
    }, new UploadOptions(null, null, false,
            progressHandler, cancellationSignal));
  }

  private UpProgressHandler progressHandler = new UpProgressHandler() {
    public void progress(String key, double percent) {
      int i = (int) Math.floor(percent * 100);
      progressDialog.setProgress(i);
    }
  };

  private UpCancellationSignal cancellationSignal = new UpCancellationSignal() {
    public boolean isCancelled() {
      return isCancelled;
    }
  };


  public static String getExtension(String filename) {
    if ((filename != null) && (filename.length() > 0)) {
      int dot = filename.lastIndexOf('.');
      if ((dot >-1) && (dot < (filename.length() - 1))) {
        return filename.substring(dot);
      }
    }
    return ".mp4";//default
  }


  public void pickVideo(final String key,final JSCallback thumbnailCallback,final JSCallback callback){
    g_UploadVideoKey = key;
    this.mPickVideoThumbnailCallback = thumbnailCallback;
    this.mPickVideoCallback = callback;
    int permission = ActivityCompat.checkSelfPermission(WXPageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if (permission != PackageManager.PERMISSION_GRANTED) {
      // We don't have permission so prompt the user
      ActivityCompat.requestPermissions(
              WXPageActivity.this,
              PERMISSIONS_STORAGE,
              PickVideo_REQUEST_EXTERNAL_STORAGE_CODE
      );
    }else{
      if(CommonUtils.isFlyme()){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择视频文件"), REQUEST_CODE_PICK_VIDEO);
      }else{
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "请选择视频文件"), REQUEST_CODE_PICK_VIDEO);
      }
    }
  }


  //保存图片到本地路径
  public File saveImage(Bitmap bmp,String path,String fileName) {
    File appDir = new File(path);
    if (!appDir.exists()) {
      boolean a = appDir.mkdir();
    }
    File file = new File(appDir, fileName);
    try {
      FileOutputStream fos = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.flush();
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
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



  /**
   * 支付宝支付
   */
  public void alipay(String orderInfo) {
    final String ORDER_INFO = orderInfo;
    Runnable payRunnable = new Runnable() {

      @Override
      public void run() {
        PayTask alipay = new PayTask(WXPageActivity.this);
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


  private static final int SDK_PAY_FLAG = 1;


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
            CommonUtils.showShort(WXPageActivity.this,"支付成功");
          } else {
            CommonUtils.showShort(WXPageActivity.this,"支付失败");
          }
          break;
        }
        default:
          break;
      }
    };
  };


}
