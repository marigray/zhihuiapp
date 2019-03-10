package com.weex.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jaeger.library.StatusBarUtil;

import com.taobao.weex.devtools.common.LogUtil;
import com.weex.app.R;
import com.weex.app.WXApplication;
import com.weex.app.WXPageActivity;
import com.weex.app.fragments.WeexFragmentHome;

import com.weex.app.util.CommonUtils;
import com.weex.app.util.Logger;
import com.weex.app.util.okhttp.CallBackUtil;
import com.weex.app.util.okhttp.OkhttpUtil;
import com.weex.app.util.upgrade.CommonProgressDialog;
import com.weex.app.util.upgrade.DownloadService;
import com.weex.app.util.upgrade.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Call;

public class StudentMainActivity extends BaseFragmentActivity {
    private View mTabHome;
    private View mTabCategory;
    private View mTabStudy;
    private View mTabMy;
    private static StudentMainActivity sharedInstance;
    private String downloadApkUrl;
    private String apkName;
    private String newApkVersionName;
    private CommonProgressDialog pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedInstance = this;
        // 尝试修复按下 Home 键重新启动 APP 的问题
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_main_student);
//        StatusBarUtil.setTranslucentForImageView(this, 0, null);
        final TabHost tabHost = getTabHost();
        tabHost.setup();
        tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        this.addTab(R.string.tab_home, R.drawable.ic_tab_home, R.id.tabHome);
        this.addTab(R.string.tab_category, R.drawable.ic_tab_category, R.id.tabCategory);
        this.addTab(R.string.tab_study, R.drawable.ic_tab_study, R.id.tabStudy);
        this.addTab(R.string.tab_my, R.drawable.ic_tab_my, R.id.tabMy);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            private Fragment previousFragment = null;

            public void onTabChanged(String tabId) {
                // 给子fragment发送显示还是隐藏状态的切换事件
                if (previousFragment != null) previousFragment.setUserVisibleHint(false);
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    switch (Integer.parseInt(tabId)) {
                        case R.string.tab_home:
                            manager.findFragmentById(R.id.tabHome).setUserVisibleHint(true);
                            break;
                        case R.string.tab_category:
                            manager.findFragmentById(R.id.tabCategory).setUserVisibleHint(true);
                            break;
                        case R.string.tab_study:
                            manager.findFragmentById(R.id.tabStudy).setUserVisibleHint(true);
                            break;
                        case R.string.tab_my:
                            manager.findFragmentById(R.id.tabMy).setUserVisibleHint(true);
                            break;
                    }
                }
            }
        });
        tabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = CommonUtils.getSharedPreferencesString(getApplicationContext(), "userid");
                if(id==null||id=="0"){
                    CommonUtils.showShort(view.getContext(), "请先登录");
                    Intent intent = new Intent(StudentMainActivity.this, WXPageActivity.class);
                    intent.setData(Uri.parse("zhihuit://login"));
                    startActivity(intent);
                }else{
                    tabHost.setCurrentTab(1);
                }
            }
        });
        tabHost.getTabWidget().getChildTabViewAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = CommonUtils.getSharedPreferencesString(getApplicationContext(), "userid");
                if(id==null||id=="0"){
                    CommonUtils.showShort(view.getContext(), "请先登录");
                    Intent intent = new Intent(StudentMainActivity.this, WXPageActivity.class);
                    intent.setData(Uri.parse("zhihuit://login"));
                    startActivity(intent);
                }else{
                    tabHost.setCurrentTab(2);
                }
            }
        });
        getApkVersion();
    }


    private TabHost getTabHost() {
        return (TabHost) this.findViewById(android.R.id.tabhost);
    }

    private void addTab(int labelId, int iconResourceId, int fragmentId) {
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec = tabHost.newTabSpec("" + labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabHost().getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
//        title.setTypeface(CommonUtils.getAppTypeface(this));
        title.setText(labelId);
        ImageView iconImageView = tabIndicator.findViewById(R.id.iconImageView);
        iconImageView.setImageResource(iconResourceId);
        spec.setIndicator(tabIndicator);
        spec.setContent(fragmentId);
        tabHost.addTab(spec);
        switch (labelId) {
            case R.string.tab_home:
                mTabHome = tabIndicator;
                break;
            case R.string.tab_category:
                mTabCategory = tabIndicator;
                break;
            case R.string.tab_study:
                mTabStudy = tabIndicator;
                break;
            case R.string.tab_my:
                mTabMy = tabIndicator;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof WeexFragmentHome) {
                ((WeexFragmentHome) fragment).onActivityResume();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof WeexFragmentHome) {
                ((WeexFragmentHome) fragment).onActivityStop();
            }
        }
    }

    public void onDestroy() {
        sharedInstance = null;
        super.onDestroy();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof WeexFragmentHome) {
                ((WeexFragmentHome) fragment).onActivityDestroy();
            }
        }
    }

    public static StudentMainActivity getSharedInstance() {
        return sharedInstance;
    }

    public void getApkVersion(){
        String url = WXApplication.SERVER_URL + "/edu/apk/getApk?source=android&type=2";
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
                        int apkstate = result.getJSONObject("content").getInteger("apkstate");
                        apkName = "zhihuis"+serverApkVersioncode+".apk";
                        /** * 获取到当前的本地版本 */
                        int localVersioncode = WXApplication.getMyApplication().getPackageManager().getPackageInfo(WXApplication.getMyApplication().getPackageName(),0).versionCode;
                        if(localVersioncode<serverApkVersioncode){
                            if(apkstate==1){
                                normalUpdate(StudentMainActivity.this);
                            }else if(apkstate==2){
                                forceUpdate(StudentMainActivity.this);
                            }
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
        mDialog.setMessage("新版本"+newApkVersionName+"已经可以下载，是否更新？");
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendBroadcast(new Intent("ZHIHUI_APK_UPDATE_ACTION"));
                Intent intent = new Intent(StudentMainActivity.this, DownloadService.class);
                intent.putExtra("APK_DOWNLOAD_URL", downloadApkUrl);
                intent.putExtra("APK_NAME", apkName);
                StudentMainActivity.this.startService(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }

    /**
     * 强制升级 ，如果不点击确定升级，直接退出应用
     *
     * @param context
     */
    private void forceUpdate(final Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新");
        mDialog.setMessage("\n请升级到版本"+newApkVersionName);
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pBar = new CommonProgressDialog(StudentMainActivity.this);
                pBar.setCanceledOnTouchOutside(false);
                pBar.setTitle("正在下载");
                pBar.setCustomTitle(LayoutInflater.from(
                        StudentMainActivity.this).inflate(
                        R.layout.title_dialog, null));
                pBar.setMessage("正在下载");
                pBar.setIndeterminate(true);
                pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pBar.setCancelable(false);
                // downFile(URLData.DOWNLOAD_URL);
                final DownloadTask downloadTask = new DownloadTask(
                        StudentMainActivity.this);
                downloadTask.execute(downloadApkUrl);
                pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });
            }
        }).setNegativeButton("退出应用", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 直接退出应用
                System.exit(0);
            }
        }).setCancelable(false).create().show();
    }


    /**
     * 下载应用
     *
     * @author Administrator
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        File apkFile;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream in = null;
            FileOutputStream out = null;
            try {
                URL url = new URL(downloadApkUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(false);
                urlConnection.setConnectTimeout(10 * 1000);
                urlConnection.setReadTimeout(10 * 1000);
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Charset", "UTF-8");
                urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                urlConnection.connect();
                long bytetotal = urlConnection.getContentLength();
                long bytesum = 0;
                int byteread = 0;
                in = urlConnection.getInputStream();
                File dir = StorageUtils.getCacheDirectory(StudentMainActivity.this);
                apkFile = new File(dir, apkName);
                out = new FileOutputStream(apkFile);
                byte[] buffer = new byte[10 * 1024];

                int oldProgress = 0;

                while ((byteread = in.read(buffer)) != -1) {
                    bytesum += byteread;
                    out.write(buffer, 0, byteread);

                    int progress = (int) (bytesum * 100L / bytetotal);
                    // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                    if (progress != oldProgress) {
                        pBar.setProgress(progress);
                    }
                    oldProgress = progress;
                }
                // 下载完成
                installApk(apkFile);
                pBar.dismiss();
            } catch (Exception e) {
                LogUtil.e("download apk file error");
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ignored) {

                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {

                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
//            pBar.setIndeterminate(false);
//            pBar.setMax(100);
//            pBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
        }
    }

    private void installApk(File apkFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = getPackageName() + ".updateFileProvider";
            Uri apkUri = FileProvider.getUriForFile(this, authority, apkFile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
            try {
                String[] command = {"chmod", "777", apkFile.toString()};
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.start();
            } catch (IOException ignored) {
            }
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
        System.exit(0);
    }
}
