package com.example.umik.easyspot;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by anhtuan.nguyen on 09.01.2016.
 */
public class EasySpotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this);
    }
}
