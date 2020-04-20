package com.aman.androidrealunittesting.unittesting.screens.questiondetails;

import com.aman.androidrealunittesting.unittesting.questions.QuestionDetails;
import com.aman.androidrealunittesting.unittesting.screens.common.views.ObservableViewMvc;

public interface QuestionDetailsViewMvc extends ObservableViewMvc<QuestionDetailsViewMvc.Listener> {

    public interface Listener {
        void onNavigateUpClicked();
    }

    void bindQuestion(QuestionDetails question);

    void showProgressIndication();

    void hideProgressIndication();
}
