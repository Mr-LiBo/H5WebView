package libo.com.h5webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;


import java.util.Timer;
import java.util.TimerTask;

/**
 * @Example
 * @Auth : LiBo on 2018/12/13 0013
 * @Describe : 启动页
 */
public class LogoActivity extends AppCompatActivity {

    private int recLen = 4;//跳过倒计时提示5秒
    private TextView tv;
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;


    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    recLen--;
                    tv.setText("跳过 " + recLen);
                    tv.setVisibility(View.VISIBLE);
                    if (recLen < 1) {
                        startActivity();
                        timer.cancel();
                        tv.setVisibility(View.GONE);//倒计时到0隐藏字体

                    }
                }
            });
        }
    };


    private WebView webView;
    private static final String TAG = "MainActivity";

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        tv = findViewById(R.id.tv);//跳过
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity();
            }
        });//跳过监听


        webView = (WebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(appCachePath);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        registerForContextMenu(webView);


        //在js中调用本地java方法
        webView.addJavascriptInterface(new JsInterface(this), "AndroidWebView");

        // 否则页面右边有白色边框
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.requestFocus();
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings set = webView.getSettings();
        //设置webview支持js
        set.setJavaScriptEnabled(true);
        String   url="https://farm.ly5000.net";

        webView.loadUrl(url);
    }


    @Override
    protected void onResume() {
        super.onResume();

        timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒
        /**
         * 正常情况下不点击跳过
         */
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                startActivity();

            }
        }, 4000);//延迟5S后发送handler信息
    }

    public void startActivity() {
        webView.setVisibility(View.VISIBLE);


    }







}
