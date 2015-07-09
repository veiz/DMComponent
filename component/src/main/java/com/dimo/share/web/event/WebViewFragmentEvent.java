package com.dimo.share.web.event;

import android.webkit.WebView;

/**
 * Created by Alsor Zhou on 7/5/15.
 */
public class WebViewFragmentEvent {
    private WebView webView;

    public WebView getWebView() {
        return webView;
    }

    public WebViewFragmentEvent(WebView webView) {
        this.webView = webView;
    }
}
