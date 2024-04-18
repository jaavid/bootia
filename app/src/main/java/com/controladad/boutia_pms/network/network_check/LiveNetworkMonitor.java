package com.controladad.boutia_pms.network.network_check;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class LiveNetworkMonitor implements NetworkMonitor{


    private final Context context;

    public LiveNetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
    }


    @Override
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
