package com.dimo.share.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Alsor Zhou on 15/4/24.
 */
public class RestClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String kServiceHost;

    public static void get(String relativeUrl, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        String rawUrl = null;
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            rawUrl = relativeUrl;
        } else {
            rawUrl = getAbsoluteUrl(relativeUrl);
        }

        client.get(rawUrl, params, responseHandler);
    }

    public static void post(String relativeUrl, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        String rawUrl = null;
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            rawUrl = relativeUrl;
        } else {
            rawUrl = getAbsoluteUrl(relativeUrl);
        }

        client.post(rawUrl, params, responseHandler);
    }

    public static void setServiceHost(String host) {
        RestClient.kServiceHost = host;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return kServiceHost + "?action=" + relativeUrl;
    }
}
