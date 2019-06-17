package com.nkhatho.leole.passwordgenerator;

import android.app.Application;

import com.backendless.Backendless;

/**
 * Created by labl2 on 2017/08/28.
 */

public class BackendApplication extends Application {

    public static final String APP_ID = "8FF00D0C-E520-FDEA-FF5F-E1ACDE1DEB00";
    public static final String API_KEY = "4D32934D-2AC4-FBCB-FF34-1A95A3AD9200";
    public static final String SERVER_URL = "https://api.backendless.com";

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(getApplicationContext(), APP_ID, API_KEY);
    }
}
