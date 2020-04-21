package com.aman.androidrealunittesting.unittesting.screens.questiondetails;

import com.aman.androidrealunittesting.unittesting.questions.FetchQuestionDetailsUseCase;
import com.aman.androidrealunittesting.unittesting.questions.QuestionDetails;
import com.aman.androidrealunittesting.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.aman.androidrealunittesting.unittesting.screens.common.toastshelper.ToastsHelper;
import com.aman.androidrealunittesting.unittesting.testdata.QuestionDetailsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {

    // region constants ----------------------------------------------------------------------------
    private static final QuestionDetails QUESTION_DETAILS = QuestionDetailsTestData.getQuestionDetails1();
    private static final String QUESTION_ID = QUESTION_DETAILS.getId();
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    private UseCaseTd mUseCaseTd;
    @Mock
    ScreensNavigator mScreensNavigatorMock;
    @Mock
    ToastsHelper mToastsHelperMock;
    @Mock
    QuestionDetailsViewMvc mQuestionDetailsViewMvcMock;
    // endregion helper fields ---------------------------------------------------------------------

    private QuestionDetailsController SUT;

    @Before
    public void setup() throws Exception {
        mUseCaseTd = new UseCaseTd();
        SUT = new QuestionDetailsController(mUseCaseTd, mScreensNavigatorMock, mToastsHelperMock);
        SUT.bindView(mQuestionDetailsViewMvcMock);
        SUT.bindQuestionId(QUESTION_ID);
    }

    @Test
    public void onStart_listenersRegistered() throws Exception {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionDetailsViewMvcMock).registerListener(SUT);
        mUseCaseTd.verifyListenerRegistered(SUT);
    }

    @Test
    public void onStop_listenersUnregistered() throws Exception {
        // Arrange
        SUT.onStart();
        // Act
        SUT.onStop();
        // Assert
        verify(mQuestionDetailsViewMvcMock).unregisterListener(SUT);
        mUseCaseTd.verifyListenerNotRegistered(SUT);
    }

    @Test
    public void onStart_success_questionDetailsBoundToView() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionDetailsViewMvcMock).bindQuestion(QUESTION_DETAILS);
    }

    @Test
    public void onStart_failure_errorToastShown() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(mToastsHelperMock).showUseCaseError();
    }

    @Test
    public void onStart_progressIndicationShown() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionDetailsViewMvcMock).showProgressIndication();
    }

    @Test
    public void onStart_success_progressIndicationHidden() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionDetailsViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onStart_failure_progressIndicationShown() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(mQuestionDetailsViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onNavigateUpClicked_navigatedUp() {
        // Arrange
        // Act
        SUT.onNavigateUpClicked();
        // Assert
        verify(mScreensNavigatorMock).navigateUp();
    }

    // region helper methods -----------------------------------------------------------------------

    private void success() {
        // currently no-op
    }

    private void failure() {
        mUseCaseTd.mFailure = true;
    }

    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------

    private static class UseCaseTd extends FetchQuestionDetailsUseCase {

        private boolean mFailure;

        UseCaseTd() {
            super(null, null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            if (!questionId.equals(QUESTION_ID)) {
                throw new RuntimeException("invalid question ID: " + questionId);
            }
            for (Listener listener : getListeners()) {
                if (mFailure) {
                    listener.onQuestionDetailsFetchFailed();
                } else {
                    listener.onQuestionDetailsFetched(QUESTION_DETAILS);
                }
            }
        }

        void verifyListenerRegistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }

        void verifyListenerNotRegistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    throw new RuntimeException("listener registered");
                }
            }
        }
    }

    // endregion helper classes --------------------------------------------------------------------

}