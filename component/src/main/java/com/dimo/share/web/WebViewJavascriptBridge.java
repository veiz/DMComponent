package com.dimo.share.web;

import android.app.Activity;
import android.content.Context;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dimo.share.R;
import com.dimo.share.web.event.PageEvent;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA.
 * implements Serializable in case of javascript interface will be removed in obfuscated code.
 * <p/>
 * User: jack_fang
 * Date: 13-8-15
 * Time: 下午6:08
 */
public class WebViewJavascriptBridge implements Serializable {

    WebView mWebView;
    Context mContext;

    WVJBHandler _messageHandler;
    Map<String, WVJBHandler> _messageHandlers;
    Map<String, WVJBResponseCallback> _responseCallbacks;
    long _uniqueId;

    public WebViewJavascriptBridge(Context context, WebView webview, WVJBHandler handler) {
        this.mContext = context;
        this.mWebView = webview;
        this._messageHandler = handler;
        _messageHandlers = new HashMap<>();
        _responseCallbacks = new HashMap<>();
        _uniqueId = 0;
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "_WebViewJavascriptBridge");
        mWebView.setWebViewClient(new InterestWebViewClient());
        mWebView.setWebChromeClient(new InterestWebChromeClient());     //optional, for show console and alert
    }


    private void loadWebViewJavascriptBridgeJs(WebView webView) {
        InputStream is = mContext.getResources().openRawResource(R.raw.webviewjavascriptbridge);
        String script = convertStreamToString(is);
        webView.loadUrl("javascript:" + script);

    }

    public static String convertStreamToString(java.io.InputStream is) {
        String s = "";
        try {
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) s = scanner.next();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    private class InterestWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView webView, String url) {
            Timber.v("Web View onPageFinished");

            loadWebViewJavascriptBridgeJs(webView);

            EventBus.getDefault().post(new PageEvent(PageEvent.PageEventEnum.FINISHED, webView.getTitle()));
        }
    }

    private class InterestWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Timber.v("Web View " + cm.message() + " line:" + cm.lineNumber());
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            // if don't cancel the alert, webview after onJsAlert not responding taps
            // you can check this :
            // http://stackoverflow.com/questions/15892644/android-webview-after-onjsalert-not-responding-taps
            result.cancel();

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public interface WVJBHandler {
        void handle(String data, WVJBResponseCallback jsCallback);
    }

    public interface WVJBResponseCallback {
        void callback(String data);
    }

    public void registerHandler(String handlerName, WVJBHandler handler) {
        _messageHandlers.put(handlerName, handler);
    }

    private class CallbackJs implements WVJBResponseCallback {
        private final String callbackIdJs;

        public CallbackJs(String callbackIdJs) {
            this.callbackIdJs = callbackIdJs;
        }

        @Override
        public void callback(String data) {
            _callbackJs(callbackIdJs, data);
        }
    }


    private void _callbackJs(String callbackIdJs, String data) {
        //TODO: CALL js to call back;
        Map<String, String> message = new HashMap<>();
        message.put("responseId", callbackIdJs);
        message.put("responseData", data);
        _dispatchMessage(message);
    }

    @JavascriptInterface
    public void _handleMessageFromJs(final String data, String responseId,
                                     String responseData, String callbackId, String handlerName) {
        if (null != responseId) {
            WVJBResponseCallback responseCallback = _responseCallbacks.get(responseId);
            responseCallback.callback(responseData);
            _responseCallbacks.remove(responseId);
        } else {
            WVJBResponseCallback responseCallback = null;
            if (null != callbackId) {
                responseCallback = new CallbackJs(callbackId);
            }
            final WVJBHandler handler;
            if (null != handlerName) {
                handler = _messageHandlers.get(handlerName);
                if (null == handler) {
                    Timber.e("Web View WVJB Warning: No handler for " + handlerName);
                    return;
                }
            } else {
                handler = _messageHandler;
            }
            try {
                final WVJBResponseCallback finalResponseCallback = responseCallback;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.handle(data, finalResponseCallback);
                    }
                });
            } catch (Exception exception) {
                Timber.e("WebViewJavascriptBridge: WARNING: java handler threw. " + exception.getMessage());
            }
        }
    }

    public void send(String data) {
        send(data, null);
    }

    public void send(String data, WVJBResponseCallback responseCallback) {
        _sendData(data, responseCallback, null);
    }

    private void _sendData(String data, WVJBResponseCallback responseCallback, String handlerName) {
        Map<String, String> message = new HashMap<>();
        message.put("data", data);
        if (null != responseCallback) {
            String callbackId = "java_cb_" + (++_uniqueId);
            _responseCallbacks.put(callbackId, responseCallback);
            message.put("callbackId", callbackId);
        }
        if (null != handlerName) {
            message.put("handlerName", handlerName);
        }
        _dispatchMessage(message);
    }

    private void _dispatchMessage(Map<String, String> message) {
        String messageJSON = new JSONObject(message).toString();
        Timber.v("Web View sending:" + messageJSON);
        final String javascriptCommand =
                String.format("javascript:WebViewJavascriptBridge._handleMessageFromJava('%s');", doubleEscapeString(messageJSON));
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(javascriptCommand);
            }
        });
    }


    public void callHandler(String handlerName) {
        callHandler(handlerName, null, null);
    }

    public void callHandler(String handlerName, String data) {
        callHandler(handlerName, data, null);
    }

    public void callHandler(String handlerName, String data, WVJBResponseCallback responseCallback) {
        _sendData(data, responseCallback, handlerName);
    }

    /*
      * you must escape the char \ and  char ", or you will not recevie a correct json object in
      * your javascript which will cause a exception in chrome.
      *
      * please check this and you will know why.
      * http://stackoverflow.com/questions/5569794/escape-nsstring-for-javascript-input
      * http://www.json.org/
    */
    private String doubleEscapeString(String javascript) {
        String result;
        result = javascript.replace("\\", "\\\\");
        result = result.replace("\"", "\\\"");
        result = result.replace("\'", "\\\'");
        result = result.replace("\n", "\\n");
        result = result.replace("\r", "\\r");
        result = result.replace("\f", "\\f");
        return result;
    }

}
