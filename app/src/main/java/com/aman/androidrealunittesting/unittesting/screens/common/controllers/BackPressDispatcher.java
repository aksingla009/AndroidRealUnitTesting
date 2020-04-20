package com.aman.androidrealunittesting.unittesting.screens.common.controllers;

public interface BackPressDispatcher {
    void registerListener(BackPressedListener listener);

    void unregisterListener(BackPressedListener listener);
}
