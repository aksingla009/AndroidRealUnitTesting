package com.aman.androidrealunittesting.unittesting.screens.common.navdrawer;

import android.widget.FrameLayout;

import com.aman.androidrealunittesting.unittesting.screens.common.views.ObservableViewMvc;

public interface NavDrawerViewMvc extends ObservableViewMvc<NavDrawerViewMvc.Listener> {

    interface Listener {

        void onQuestionsListClicked();
    }

    FrameLayout getFragmentFrame();

    boolean isDrawerOpen();

    void openDrawer();

    void closeDrawer();

}
