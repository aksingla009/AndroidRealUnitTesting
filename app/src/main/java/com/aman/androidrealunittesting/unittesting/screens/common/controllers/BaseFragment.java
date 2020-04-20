package com.aman.androidrealunittesting.unittesting.screens.common.controllers;

import androidx.fragment.app.Fragment;

import com.aman.androidrealunittesting.unittesting.common.CustomApplication;
import com.aman.androidrealunittesting.unittesting.common.dependencyinjection.ControllerCompositionRoot;

public class BaseFragment extends Fragment {

    private ControllerCompositionRoot mControllerCompositionRoot;

    protected ControllerCompositionRoot getCompositionRoot() {
        if (mControllerCompositionRoot == null) {
            mControllerCompositionRoot = new ControllerCompositionRoot(
                    ((CustomApplication) requireActivity().getApplication()).getCompositionRoot(),
                    requireActivity()
            );
        }
        return mControllerCompositionRoot;
    }
}
