package com.dimo.share.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Alsor Zhou on 2/19/15.
 */
public class Reachability {
    public boolean getInternetState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo == null) {
            return false;
        }
        if (nInfo.isConnected()) {
            // NetWork接続可
            if (nInfo.getTypeName().equals("WIFI")) {

            } else if (nInfo.getTypeName().equals("mobile")) {

            }
            return true;

        } else {
            // NetWork接続不可
            return false;
        }
    }

    /**
     * Detect connection to host if available
     * http://stackoverflow.com/a/17886807
     *
     * @return true / false
     */
    public static boolean isConnectedToHost(String hostUrl) {
        try {
            //make a URL to a known source
            URL url = new URL(hostUrl);

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
