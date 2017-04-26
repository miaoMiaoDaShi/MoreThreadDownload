package com.lq.zcoder.morethreaddownload;

import android.app.Application;
import android.content.Context;

/**
 * Created by Zcoder on 2017/4/25.
 */

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
