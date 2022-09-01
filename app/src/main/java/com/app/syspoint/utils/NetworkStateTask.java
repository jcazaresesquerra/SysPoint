package com.app.syspoint.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.app.syspoint.App;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkStateTask extends AsyncTask<String, Void, Boolean> {
    private final NetworkStateListener mNetworkStateListener;
    private final ConnectivityManager mConnectivityManager;

    public NetworkStateTask(NetworkStateListener networkStateListener) {
        mNetworkStateListener = networkStateListener;
        mConnectivityManager = (ConnectivityManager)
                App.Companion.getINSTANCE().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        if (mConnectivityManager != null) {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1000);
                    urlc.setReadTimeout(1000);
                    urlc.connect();
                    boolean connected = (urlc.getResponseCode() == 200);
                    return connected;
                } catch (Exception e) {
                    Log.e("TAG", "Error checking internet connection", e);
                }
            } else {
                Log.d("TAG", "No network available!");
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean connected) {
        super.onPostExecute(connected);
        if (mNetworkStateListener != null)
            mNetworkStateListener.onInternetConnected(connected);
    }
}
