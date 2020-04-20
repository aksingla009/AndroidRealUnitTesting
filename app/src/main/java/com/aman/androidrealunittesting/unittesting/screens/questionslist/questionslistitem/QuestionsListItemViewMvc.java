package com.aman.androidrealunittesting.unittesting.screens.questionslist.questionslistitem;

import com.aman.androidrealunittesting.unittesting.questions.Question;
import com.aman.androidrealunittesting.unittesting.screens.common.views.ObservableViewMvc;

public interface QuestionsListItemViewMvc extends ObservableViewMvc<QuestionsListItemViewMvc.Listener> {

    public interface Listener {
        void onQuestionClicked(Question question);
    }

    void bindQuestion(Question question);
}
