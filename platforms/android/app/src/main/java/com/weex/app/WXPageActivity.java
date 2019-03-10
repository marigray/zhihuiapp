package com.weex.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.jaeger.library.StatusBarUtil;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WXPageActivity extends AbsWeexActivity implements
    WXSDKInstance.NestedInstanceInterceptor {

  private static final String TAG = "WXPageActivity";
  private ProgressBar mProgressBar;
  private TextView mTipView;
  private boolean mFromSplash = false;
  private HotReloadManager mHotReloadManager;

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
    if (uri != null && uri.getScheme().equals("zhihui")){
      try {
        InputStream is = getResources().getAssets().open("dist/" + uri.getHost() + ".js");
        mUri = Uri.parse("file://assets/dist/" + uri.getHost() + ".js");
        is.close();
      } catch (IOException e) {
        mUri = Uri.parse("file://assets/dist/t-" + uri.getHost() + ".js");
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
    renderPageByURL(url, data.toJSONString());
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
