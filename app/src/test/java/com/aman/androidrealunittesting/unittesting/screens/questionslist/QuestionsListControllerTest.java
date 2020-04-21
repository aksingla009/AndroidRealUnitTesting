package com.aman.androidrealunittesting.unittesting.screens.questionslist;

import com.aman.androidrealunittesting.unittesting.common.time.TimeProvider;
import com.aman.androidrealunittesting.unittesting.questions.FetchLastActiveQuestionsUseCase;
import com.aman.androidrealunittesting.unittesting.questions.Question;
import com.aman.androidrealunittesting.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.aman.androidrealunittesting.unittesting.screens.common.toastshelper.ToastsHelper;
import com.aman.androidrealunittesting.unittesting.testdata.QuestionsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionsListControllerTest {

    // region constants ----------------------------------------------------------------------------
    private static final List<Question> QUESTIONS = QuestionsTestData.getQuestions();
    private static final Question QUESTION = QuestionsTestData.getQuestion();
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    private UseCaseTd mUseCaseTd;
    @Mock
    ScreensNavigator mScreensNavigator;
    @Mock
    ToastsHelper mToastsHelper;
    @Mock QuestionsListViewMvc mQuestionsListViewMvc;
    @Mock
    TimeProvider mTimeProviderMock;
    // endregion helper fields ---------------------------------------------------------------------

    private QuestionsListController SUT;

    @Before
    public void setup() {
        mUseCaseTd = new UseCaseTd();
        SUT = new QuestionsListController(mUseCaseTd, mScreensNavigator, mToastsHelper, mTimeProviderMock);
        SUT.bindView(mQuestionsListViewMvc);
    }

    @Test
    public void onStart_progressIndicationShown() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc).showProgressIndication();
    }

    @Test
    public void onStart_successfulResponse_progressIndicationHidden() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc).hideProgressIndication();
    }

    @Test
    public void onStart_failure_progressIndicationHidden() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc).hideProgressIndication();
    }

    @Test
    public void onStart_successfulResponse_questionsBoundToView() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc).bindQuestions(QUESTIONS);
    }

    @Test
    public void onStart_secondTimeAfterSuccessfulResponse_questionsBoundToTheViewFromCache() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc, times(2)).bindQuestions(QUESTIONS);
        assertThat(mUseCaseTd.getCallCount(), is(1));
    }

    @Test
    public void onStart_failure_errorToastShown() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(mToastsHelper).showUseCaseError();
    }

    @Test
    public void onStart_failure_questionsNotBoundToView() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc, never()).bindQuestions(any(List.class));
    }

    @Test
    public void onStart_listenersRegistered() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc).registerListener(SUT);
        mUseCaseTd.verifyListenerRegistered(SUT);
    }

    @Test
    public void onStop_listenersUnregistered() {
        // Arrange
        // Act
        SUT.onStop();
        // Assert
        verify(mQuestionsListViewMvc).unregisterListener(SUT);
        mUseCaseTd.verifyListenerNotRegistered(SUT);
    }

    @Test
    public void onQuestionClicked_navigatedToQuestionDetailsScreen() {
        // Arrange
        // Act
        SUT.onQuestionClicked(QUESTION);
        // Assert
        verify(mScreensNavigator).toQuestionDetails(QUESTION.getId());
    }

    @Test
    public void onStart_secondTimeAfterCachingTimeout_questionsBoundToViewFromUseCase() {
        // Arrange
        emptyQuestionsListOnFirstCall();
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.onStart();
        SUT.onStop();
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(10000L);
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc).bindQuestions(QUESTIONS);
    }

    @Test
    public void onStart_secondTimeRightBeforeCachingTimeout_questionsBoundToViewFromCache() {
        // Arrange
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.onStart();
        SUT.onStop();
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(9999L);
        SUT.onStart();
        // Assert
        verify(mQuestionsListViewMvc, times(2)).bindQuestions(QUESTIONS);
        assertThat(mUseCaseTd.getCallCount(), is(1));
    }


    // region helper methods -----------------------------------------------------------------------

    private void success() {
        // currently no-op
    }

    private void failure() {
        mUseCaseTd.mFailure = true;
    }

    private void emptyQuestionsListOnFirstCall() {
        mUseCaseTd.mEmptyListOnFirstCall = true;
    }

    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    private static class UseCaseTd extends FetchLastActiveQuestionsUseCase {

        boolean mEmptyListOnFirstCall;
        private boolean mFailure;
        private int mCallCount;

        UseCaseTd() {
            super(null);
        }

        @Override
        public void fetchLastActiveQuestionsAndNotify() {
            mCallCount++;
            for (FetchLastActiveQuestionsUseCase.Listener listener : getListeners()) {
                if (mFailure) {
                    listener.onLastActiveQuestionsFetchFailed();
                } else {
                    if (mEmptyListOnFirstCall && mCallCount == 1) {
                        listener.onLastActiveQuestionsFetched(new ArrayList<Question>());
                    } else {
                        listener.onLastActiveQuestionsFetched(QUESTIONS);
                    }
                }
            }
        }

        void verifyListenerRegistered(QuestionsListController candidate) {
            for (FetchLastActiveQuestionsUseCase.Listener listener : getListeners()) {
                if (listener == candidate) {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }

        void verifyListenerNotRegistered(QuestionsListController candidate) {
            for (FetchLastActiveQuestionsUseCase.Listener listener : getListeners()) {
                if (listener == candidate) {
                    throw new RuntimeException("listener not registered");
                }
            }
        }

        int getCallCount() {
            return mCallCount;
        }
    }
    // endregion helper classes --------------------------------------------------------------------

}