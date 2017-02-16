package com.lizhe.devtools;

import android.app.Application;
import android.content.Context;

/**
 * Created by lz on 17/1/14.
 */

public class DevToolsApplication extends Application {

    private static DevToolsApplication instance;
    private static Context mContext;

    public DevToolsApplication() {
    }

    private static DevToolsApplication getInstance() {
        if(instance == null) {
            instance = new DevToolsApplication();
        }

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
