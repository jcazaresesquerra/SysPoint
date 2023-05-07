package com.app.syspoint.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import com.app.syspoint.App;
import java.net.HttpURLConnection;
import java.net.URL;

import timber.log.Timber;

public class NetworkStateTask extends AsyncTask<String, Void, Boolean> {
    private final static String TAG = "NetworkStateTask";
    private final NetworkStateListener mNetworkStateListener;
    private ConnectivityManager mConnectivityManager;

    public NetworkStateTask(NetworkStateListener networkStateListener) {
        mNetworkStateListener = networkStateListener;
        try {
            mConnectivityManager = (ConnectivityManager)
                    App.Companion.getINSTANCE().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e ) {
            mConnectivityManager = null;
            Timber.tag(TAG).e("Fail to init mConnectivityManager" + e);
        }
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        if (mConnectivityManager != null) {
            try {
                NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
                        HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                        urlc.setRequestProperty("User-Agent", "Test");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(1000);
                        urlc.setReadTimeout(1000);
                        urlc.connect();
                        return urlc.getResponseCode() == 200;
                } else {
                    Timber.tag(TAG).d("No network available!");
                    return false;
                }
            } catch (Exception e) {
                Timber.tag(TAG).e("Error checking internet connection" + e);
                return false;
            }
        } else
            Timber.tag(TAG).d("mConnectivityManager is null");
        return false;
    }

    @Override
    protected void onPostExecute(Boolean connected) {
        super.onPostExecute(connected);
        if (mNetworkStateListener != null)
            mNetworkStateListener.onInternetConnected(connected);
    }
}
