package com.aman.androidrealunittesting.unittesting.common.dependencyinjection;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;

import androidx.fragment.app.FragmentActivity;

import com.aman.androidrealunittesting.unittesting.common.time.TimeProvider;
import com.aman.androidrealunittesting.unittesting.networking.StackoverflowApi;
import com.aman.androidrealunittesting.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.aman.androidrealunittesting.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.aman.androidrealunittesting.unittesting.questions.FetchLastActiveQuestionsUseCase;
import com.aman.androidrealunittesting.unittesting.questions.FetchQuestionDetailsUseCase;
import com.aman.androidrealunittesting.unittesting.screens.common.ViewMvcFactory;
import com.aman.androidrealunittesting.unittesting.screens.common.controllers.BackPressDispatcher;
import com.aman.androidrealunittesting.unittesting.screens.common.fragmentframehelper.FragmentFrameHelper;
import com.aman.androidrealunittesting.unittesting.screens.common.fragmentframehelper.FragmentFrameWrapper;
import com.aman.androidrealunittesting.unittesting.screens.common.navdrawer.NavDrawerHelper;
import com.aman.androidrealunittesting.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.aman.androidrealunittesting.unittesting.screens.common.toastshelper.ToastsHelper;
import com.aman.androidrealunittesting.unittesting.screens.questiondetails.QuestionDetailsController;
import com.aman.androidrealunittesting.unittesting.screens.questionslist.QuestionsListController;

public class ControllerCompositionRoot {

    private final CompositionRoot mCompositionRoot;
    private final FragmentActivity mActivity;

    public ControllerCompositionRoot(CompositionRoot compositionRoot, FragmentActivity activity) {
        mCompositionRoot = compositionRoot;
        mActivity = activity;
    }

    private FragmentActivity getActivity() {
        return mActivity;
    }

    private Context getContext() {
        return mActivity;
    }

    private FragmentManager getFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }

    private StackoverflowApi getStackoverflowApi() {
        return mCompositionRoot.getStackoverflowApi();
    }

    private FetchLastActiveQuestionsEndpoint getFetchLastActiveQuestionsEndpoint() {
        return new FetchLastActiveQuestionsEndpoint(getStackoverflowApi());
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    public ViewMvcFactory getViewMvcFactory() {
        return new ViewMvcFactory(getLayoutInflater(), getNavDrawerHelper());
    }

    private NavDrawerHelper getNavDrawerHelper() {
        return (NavDrawerHelper) getActivity();
    }

    public FetchQuestionDetailsUseCase getFetchQuestionDetailsUseCase() {
        return mCompositionRoot.getFetchQuestionDetailsUseCase();
    }

    public FetchLastActiveQuestionsUseCase getFetchLastActiveQuestionsUseCase() {
        return new FetchLastActiveQuestionsUseCase(getFetchLastActiveQuestionsEndpoint());
    }

    public TimeProvider getTimeProvider() {
        return mCompositionRoot.getTimeProvider();
    }

    public QuestionsListController getQuestionsListController() {
        return new QuestionsListController(
                getFetchLastActiveQuestionsUseCase(),
                getScreensNavigator(),
                getToastsHelper(),
                getTimeProvider());
    }

    public ToastsHelper getToastsHelper() {
        return new ToastsHelper(getContext());
    }

    public ScreensNavigator getScreensNavigator() {
        return new ScreensNavigator(getFragmentFrameHelper());
    }

    private FragmentFrameHelper getFragmentFrameHelper() {
        return new FragmentFrameHelper(getActivity(), getFragmentFrameWrapper(), getFragmentManager());
    }

    private FragmentFrameWrapper getFragmentFrameWrapper() {
        return (FragmentFrameWrapper) getActivity();
    }

    public BackPressDispatcher getBackPressDispatcher() {
        return (BackPressDispatcher) getActivity();
    }

    public QuestionDetailsController getQuestionDetailsController() {
        return new QuestionDetailsController(getFetchQuestionDetailsUseCase(), getScreensNavigator(), getToastsHelper());
    }
}
