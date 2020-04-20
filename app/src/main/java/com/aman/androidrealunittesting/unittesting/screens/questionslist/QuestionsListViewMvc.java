package com.aman.androidrealunittesting.unittesting.screens.questionslist;

import com.aman.androidrealunittesting.unittesting.questions.Question;
import com.aman.androidrealunittesting.unittesting.screens.common.views.ObservableViewMvc;

import java.util.List;

public interface QuestionsListViewMvc extends ObservableViewMvc<QuestionsListViewMvc.Listener> {

    public interface Listener {
        void onQuestionClicked(Question question);
    }

    void bindQuestions(List<Question> questions);

    void showProgressIndication();

    void hideProgressIndication();

}
