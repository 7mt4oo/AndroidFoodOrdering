package com.sushi.map.beans;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constant {
    private static final String DIRECTION_API = "AIzaSyATTwOsHz91rgAgzZI6ipxs8u0zAOb4fIk";
    public static final String API_KEY = "AIzaSyATTwOsHz91rgAgzZI6ipxs8u0zAOb4fIk";

    public static String getUrl(String originLat, String originLon, String destinationLat, String destinationLon){
        return DIRECTION_API + originLat+","+originLon+"&destination="+destinationLat+","+destinationLon+"&key="+API_KEY;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
