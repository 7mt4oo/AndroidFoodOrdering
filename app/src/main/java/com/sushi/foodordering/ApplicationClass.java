package com.sushi.foodordering;

import android.app.Application;

import com.sushi.foodordering.util.PrefUtils;

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PrefUtils.getInstance().init(this);
    }
}
