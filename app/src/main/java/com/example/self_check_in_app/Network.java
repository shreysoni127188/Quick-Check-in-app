package com.example.self_check_in_app;


import android.content.Context;
import android.os.AsyncTask;


import android.content.Context;
import android.net.ConnectivityManager;
/**
 * It check is device is connected to internet or wifi.
 */
public class Network {

    private Context mContext;

    public Network (Context context){
        this.mContext = context;
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() == true)
        {
            return true;
        }

        return false;

    }
}