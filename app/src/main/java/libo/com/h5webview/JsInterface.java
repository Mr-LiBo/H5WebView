package libo.com.h5webview;

import android.content.Context;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by liaodp on 2017/11/6.
 */
public class JsInterface {
    private Context mContext;

    public JsInterface(Context context) {
        this.mContext = context;
    }

    //在js中调用window.AndroidWebView.showInfoFromJs(name)，便会触发此方法。
    @JavascriptInterface
    public void showInfoFromJs(String name) {
        Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
    }
}