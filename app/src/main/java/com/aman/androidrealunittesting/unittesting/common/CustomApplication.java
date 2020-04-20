package com.aman.androidrealunittesting.unittesting.common;

import android.app.Application;

import com.aman.androidrealunittesting.unittesting.common.dependencyinjection.CompositionRoot;

public class CustomApplication extends Application {

    private CompositionRoot mCompositionRoot;

    @Override
    public void onCreate() {
        super.onCreate();
        mCompositionRoot = new CompositionRoot();
    }

    public CompositionRoot getCompositionRoot() {
        return mCompositionRoot;
    }
}
