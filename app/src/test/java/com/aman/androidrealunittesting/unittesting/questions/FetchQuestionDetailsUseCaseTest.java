package com.aman.androidrealunittesting.unittesting.questions;

import com.aman.androidrealunittesting.unittesting.common.time.TimeProvider;
import com.aman.androidrealunittesting.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.aman.androidrealunittesting.unittesting.networking.questions.QuestionSchema;
import com.aman.androidrealunittesting.unittesting.testdata.QuestionDetailsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    // region constants ----------------------------------------------------------------------------
    private static final long CACHE_TIMEOUT = 60000;
    private static final QuestionDetails QUESTION_DETAILS_1 = QuestionDetailsTestData.getQuestionDetails1();
    private static final String QUESTION_ID_1 = QUESTION_DETAILS_1.getId();
    private static final QuestionDetails QUESTION_DETAILS_2 = QuestionDetailsTestData.getQuestionDetails2();
    private static final String QUESTION_ID_2 = QUESTION_DETAILS_2.getId();
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    @Mock
    FetchQuestionDetailsEndpoint mFetchQuestionDetailsEndpointMock;
    private ListenerTd mListener1;
    private ListenerTd mListener2;
    @Mock
    TimeProvider mTimeProviderMock;

    private int mEndpointCallsCount;
    // endregion helper fields ---------------------------------------------------------------------

    private FetchQuestionDetailsUseCase SUT;

    @Before
    public void setup() {
        mListener1 = new ListenerTd();
        mListener2 = new ListenerTd();
        SUT = new FetchQuestionDetailsUseCase(mFetchQuestionDetailsEndpointMock, mTimeProviderMock);

        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() {
        // Arrange
        success();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        mListener1.assertSuccessfulCalls(1);
        assertThat(mListener1.getLastData(), is(QUESTION_DETAILS_1));
        mListener2.assertSuccessfulCalls(1);
        assertThat(mListener2.getLastData(), is(QUESTION_DETAILS_1));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedOfFailure() {
        // Arrange
        failure();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        mListener1.assertOneFailingCall();
        mListener2.assertOneFailingCall();
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeImmediatelyAfterSuccess_listenersNotifiedWithDataFromCache() {
        // Arrange
        success();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        mListener1.assertSuccessfulCalls(2);
        assertThat(mListener1.getLastData(), is(QUESTION_DETAILS_1));
        mListener2.assertSuccessfulCalls(2);
        assertThat(mListener2.getLastData(), is(QUESTION_DETAILS_1));
        assertThat(mEndpointCallsCount, is(1));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeRightBeforeTimeoutAfterSuccess_listenersNotifiedWithDataFromCache() {
        // Arrange
        success();
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn((long) 0);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT - 1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        mListener1.assertSuccessfulCalls(2);
        assertThat(mListener1.getLastData(), is(QUESTION_DETAILS_1));
        mListener2.assertSuccessfulCalls(2);
        assertThat(mListener2.getLastData(), is(QUESTION_DETAILS_1));
        assertThat(mEndpointCallsCount, is(1));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeRightAfterTimeoutAfterSuccess_listenersNotifiedWithDataFromEndpoint() {
        // Arrange
        success();
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        mListener1.assertSuccessfulCalls(2);
        assertThat(mListener1.getLastData(), is(QUESTION_DETAILS_1));
        mListener2.assertSuccessfulCalls(2);
        assertThat(mListener2.getLastData(), is(QUESTION_DETAILS_1));
        assertThat(mEndpointCallsCount, is(2));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeWithDifferentIdAfterSuccess_listenersNotifiedWithDataFromEndpoint() {
        // Arrange
        success();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        // Assert
        mListener1.assertSuccessfulCalls(2);
        assertThat(mListener1.getLastData(), is(QUESTION_DETAILS_2));
        mListener2.assertSuccessfulCalls(2);
        assertThat(mListener2.getLastData(), is(QUESTION_DETAILS_2));
        assertThat(mEndpointCallsCount, is(2));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_afterTwoDifferentQuestionsAtDifferentTimesFirstQuestionRightBeforeTimeout_listenersNotifiedWithDataFromCache() {
        // Arrange
        success();
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT / 2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT - 1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        mListener1.assertSuccessfulCalls(3);
        assertThat(mListener1.getLastData(), is(QUESTION_DETAILS_1));
        mListener2.assertSuccessfulCalls(3);
        assertThat(mListener2.getLastData(), is(QUESTION_DETAILS_1));
        assertThat(mEndpointCallsCount, is(2));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_afterTwoDifferentQuestionsAtDifferentTimesSecondQuestionRightBeforeTimeout_listenersNotifiedWithDataFromCache() {
        // Arrange
        success();
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT / 2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT + (CACHE_TIMEOUT / 2) - 1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        // Assert
        mListener1.assertSuccessfulCalls(3);
        assertThat(mListener1.getLastData(), is(QUESTION_DETAILS_2));
        mListener2.assertSuccessfulCalls(3);
        assertThat(mListener2.getLastData(), is(QUESTION_DETAILS_2));
        assertThat(mEndpointCallsCount, is(2));
    }


    // region helper methods -----------------------------------------------------------------------

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                mEndpointCallsCount++;

                Object[] args = invocation.getArguments();
                String questionId = (String) args[0];
                FetchQuestionDetailsEndpoint.Listener listener =
                        (FetchQuestionDetailsEndpoint.Listener) args[1];

                QuestionSchema response;
                if (questionId.equals(QUESTION_ID_1)) {
                    response = new QuestionSchema(QUESTION_DETAILS_1.getTitle(), QUESTION_DETAILS_1.getId(), QUESTION_DETAILS_1.getBody());
                } else if (questionId.equals(QUESTION_ID_2)) {
                    response = new QuestionSchema(QUESTION_DETAILS_2.getTitle(), QUESTION_DETAILS_2.getId(), QUESTION_DETAILS_2.getBody());
                } else {
                    throw new RuntimeException("unhandled question id: " + questionId);
                }
                listener.onQuestionDetailsFetched(response);
                return null;
            }
        }).when(mFetchQuestionDetailsEndpointMock).fetchQuestionDetails(
                any(String.class),
                any(FetchQuestionDetailsEndpoint.Listener.class)
        );
    }

    private void failure() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                mEndpointCallsCount++;

                Object[] args = invocation.getArguments();
                FetchQuestionDetailsEndpoint.Listener listener =
                        (FetchQuestionDetailsEndpoint.Listener) args[1];

                listener.onQuestionDetailsFetchFailed();
                return null;
            }
        }).when(mFetchQuestionDetailsEndpointMock).fetchQuestionDetails(
                any(String.class),
                any(FetchQuestionDetailsEndpoint.Listener.class)
        );
    }

    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    private static class ListenerTd implements FetchQuestionDetailsUseCase.Listener {
        private int mCallCount;
        private int mSuccessCount;
        private QuestionDetails mData;

        @Override
        public void onQuestionDetailsFetched(QuestionDetails questionDetails) {
            mCallCount++;
            mSuccessCount++;
            mData = questionDetails;
        }

        @Override
        public void onQuestionDetailsFetchFailed() {
            mCallCount++;
        }

        void assertSuccessfulCalls(int count) {
            if (mCallCount != count || mCallCount != mSuccessCount) {
                throw new RuntimeException(count + " successful call(s) assertion failed; calls: " + mCallCount + "; successes: " + mSuccessCount);
            }
        }

        void assertOneFailingCall() {
            if (mCallCount != 1 || mSuccessCount > 0) {
                throw new RuntimeException("one failing call assertion failed");
            }
        }

        QuestionDetails getLastData() {
            return mData;
        }
    }

    // endregion helper classes --------------------------------------------------------------------

}