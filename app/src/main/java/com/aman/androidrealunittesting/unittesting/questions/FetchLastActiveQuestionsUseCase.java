package com.aman.androidrealunittesting.unittesting.questions;

import com.aman.androidrealunittesting.unittesting.common.BaseObservable;
import com.aman.androidrealunittesting.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.aman.androidrealunittesting.unittesting.networking.questions.QuestionSchema;

import java.util.ArrayList;
import java.util.List;

public class FetchLastActiveQuestionsUseCase extends BaseObservable<FetchLastActiveQuestionsUseCase.Listener> {

    public interface Listener {
        void onLastActiveQuestionsFetched(List<Question> questions);

        void onLastActiveQuestionsFetchFailed();
    }

    private final FetchLastActiveQuestionsEndpoint mFetchLastActiveQuestionsEndpoint;

    public FetchLastActiveQuestionsUseCase(FetchLastActiveQuestionsEndpoint fetchLastActiveQuestionsEndpoint) {
        mFetchLastActiveQuestionsEndpoint = fetchLastActiveQuestionsEndpoint;
    }

    public void fetchLastActiveQuestionsAndNotify() {
        mFetchLastActiveQuestionsEndpoint.fetchLastActiveQuestions(new FetchLastActiveQuestionsEndpoint.Listener() {
            @Override
            public void onQuestionsFetched(List<QuestionSchema> questions) {
                notifySuccess(questions);
            }

            @Override
            public void onQuestionsFetchFailed() {
                notifyFailure();
            }
        });
    }

    private void notifyFailure() {
        for (Listener listener : getListeners()) {
            listener.onLastActiveQuestionsFetchFailed();
        }
    }

    private void notifySuccess(List<QuestionSchema> questionSchemas) {
        List<Question> questions = new ArrayList<>(questionSchemas.size());
        for (QuestionSchema questionSchema : questionSchemas) {
            questions.add(new Question(questionSchema.getId(), questionSchema.getTitle()));
        }
        for (Listener listener : getListeners()) {
            listener.onLastActiveQuestionsFetched(questions);
        }
    }
}
