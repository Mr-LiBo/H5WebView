package libo.com.h5webview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Android WebView 与 Javascript 交互。
 */
public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private static final String TAG = "MainActivity";

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

//        settings.setBuiltInZoomControls(true);
//        settings.setDisplayZoomControls(false);
//        settings.setLoadWithOverviewMode(true);
//        settings.setUseWideViewPort(true);

        registerForContextMenu(webView);


        //在js中调用本地java方法
        webView.addJavascriptInterface(new JsInterface(this), "AndroidWebView");

        // 否则页面右边有白色边框
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.requestFocus();

        //添加客户端支持
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message,final JsResult result) {
                Log.e(TAG,"onJsAlert..."+message);
//                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                builder.setTitle("对话框").setMessage(message).setPositiveButton("确定", new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        result.confirm();
//                    }
//                }).setNeutralButton("取消", new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        result.cancel();
//                    }
//                });
//                builder.setOnCancelListener(new DialogInterface.OnCancelListener()
//                {
//                    @Override
//                    public void onCancel(DialogInterface dialog)
//                    {
//                        result.cancel();
//                    }
//                });
//
//                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
//                builder.setOnKeyListener(new DialogInterface.OnKeyListener()
//                {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
//                    {
//                        Log.e("onJsConfirm", "keyCode==" + keyCode + "event=" + event);
//                        return true;
//                    }
//                });
//
//                // 禁止响应按back键的事件
//                // builder.setCancelable(false);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//                return true;
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                Log.e(TAG,"onJsBeforeUnload...");
                return super.onJsBeforeUnload(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("对话框").setMessage(message).setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        result.confirm();
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        result.cancel();
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        result.cancel();
                    }
                });

                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                builder.setOnKeyListener(new DialogInterface.OnKeyListener()
                {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                    {
                        Log.e("onJsConfirm", "keyCode==" + keyCode + "event=" + event);
                        return true;
                    }
                });

                // 禁止响应按back键的事件
                // builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
//                return super.onJsConfirm(view, url, message, result);
            }


            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,final JsPromptResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("对话框").setMessage(message);

                final EditText et = new EditText(view.getContext());
                et.setSingleLine();
                et.setText(defaultValue);
                builder.setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        result.confirm(et.getText().toString());
                    }

                }).setNeutralButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        result.cancel();
                    }
                });

                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                builder.setOnKeyListener(new DialogInterface.OnKeyListener()
                {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                    {
                        Log.v("onJsPrompt", "keyCode==" + keyCode + "event=" + event);
                        return true;
                    }
                });

                // 禁止响应按back键的事件
                // builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
                // return super.onJsPrompt(view, url, message, defaultValue,
                // result);
            }


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.e(TAG, "onProgressChanged---progress--" + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.e(TAG, "onReceivedTitle: " + title);

            }


            @Override
            public Bitmap getDefaultVideoPoster() {
                //            	webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);//这个主要处理部分机型要不点击闪屏，要不部分机型正常播放视频（有声音无图像）
                return super.getDefaultVideoPoster();
            }

            @Override
            public View getVideoLoadingProgressView() {
                // TODO Auto-generated method stub
//            	webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                return super.getVideoLoadingProgressView();
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.e(TAG,"WebResourceResponse");
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://wwww.baidu"))
                {
                    Log.e(TAG,"shouldOverrideUrlLoading"+url);
                    return true;
                }
                if (url.startsWith("http://www"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return  true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e(TAG,"onPageStarted"+url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e(TAG,"onPageFinished"+url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG,"onReceivedError  errorCode"+errorCode);
//                webView.loadUrl(ERROR_URL);//加载本地出错的js
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }
        });

        String url = "file:///android_asset/index.html";

        url="http://192.168.200.88:8090/chinauip.cfc5/weixin/external/getVerificationToken.xhtml?idCard=44082419551119031X&token=0619499B3E4EB3342E5C4E531FE90959FEFCE1DC5085DF756E20CA23&code=JCXXCXYXG";

        webView.loadUrl(url);
    }


    //在java中调用js代码
    public void sendInfoToJs(View view) {
        String msg = ((EditText) findViewById(R.id.input_et)).getText().toString();
        //调用js中的函数：showInfoFromJava(msg)
        webView.loadUrl("javascript:showInfoFromJava('" + msg + "')");
    }


}
