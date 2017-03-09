package com.example.admin.puresults.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by admin on 3/9/2017.
 */

public class DeviceUtils {

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        boolean isConnected = false;

        if (info != null && info.isConnectedOrConnecting()) {
            isConnected = true;
        }

        return isConnected;
    }
}
