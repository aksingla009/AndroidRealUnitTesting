package com.aman.androidrealunittesting.unittesting.questions;

import com.aman.androidrealunittesting.unittesting.networking.StackoverflowApi;
import com.aman.androidrealunittesting.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.aman.androidrealunittesting.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentCaptor.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchLastActiveQuestionsUseCaseTest {
    //region Constants----------------------------------------------------

    //endregion Constants-------------------------------------------------

    //region Helper Fields------------------------------------------------
    FetchLastActiveQuestionsEndpointTD fetchLastActiveQuestionsEndpointTD;
    @Mock
    FetchLastActiveQuestionsUseCase.Listener mListener1;
    @Mock
    FetchLastActiveQuestionsUseCase.Listener mListener2;
    @Captor
    ArgumentCaptor<List<Question>> mQuestionsListArgumentCaptor;

    //endregion Helper Fields---------------------------------------------

    private FetchLastActiveQuestionsUseCase SUT;

    @Before
    public void setup() {
        fetchLastActiveQuestionsEndpointTD = new FetchLastActiveQuestionsEndpointTD();
        SUT = new FetchLastActiveQuestionsUseCase(fetchLastActiveQuestionsEndpointTD);

    }

    //If Successful endpoint response then need to notify all the Listeners about the success with correct data
    @Test
    public void fetchLastActiveQuestionsAndNotify_success_listenersNotifiedWithSuccessWithCorrectData () throws Exception {
        //Arrange
        success();
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        //Act
        SUT.fetchLastActiveQuestionsAndNotify();
        //Assert that listeners are notified with correct data
        verify(mListener1).onLastActiveQuestionsFetched(mQuestionsListArgumentCaptor.capture());
        verify(mListener2).onLastActiveQuestionsFetched(mQuestionsListArgumentCaptor.capture());
        //Ideally mQuestionsListArgumentCaptor should capture List<Question> twice so we should getAll the values from it
        List<List<Question>> listOfListOFQuestions = mQuestionsListArgumentCaptor.getAllValues();
        //on first index we should have List of Questions from Listener1
        //on second index we should have List of Questions from Listener2

        //We will compare that every list of Questions which listener got in there should be actually Equal to what was passed
        assertThat(listOfListOFQuestions.get(0),is(getFetchedQuestions()));//For Listener1
        assertThat(listOfListOFQuestions.get(1),is(getFetchedQuestions()));//For Listener2
    }

    //If Failed endpoint response then need to notify all the Listeners about the failure

    @Test
    public void fetchLastActiveQuestionsAndNotify_failure_listenersNotifiedWithFailure() throws Exception {
        //Arrange
        failure();
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        //Act
        SUT.fetchLastActiveQuestionsAndNotify();
        //Assert
        verify(mListener1).onLastActiveQuestionsFetchFailed();
        verify(mListener2).onLastActiveQuestionsFetchFailed();
    }

    //region Helper Methods-------------------------------------------------

    private void success() {
        //Currently no operation
    }

    private void failure() {
        fetchLastActiveQuestionsEndpointTD.mFailure = true;
    }

    private List<Question> getFetchedQuestions() {
        List<Question> questionList = new ArrayList<>();
        questionList.add(new Question("id1","title1"));
        questionList.add(new Question("id2","title2"));
        return questionList;

    }

    //endregion Helper Methods----------------------------------------------

    //region Helper Classes-------------------------------------------------
    private static class FetchLastActiveQuestionsEndpointTD extends FetchLastActiveQuestionsEndpoint{
        public boolean mFailure;

        public FetchLastActiveQuestionsEndpointTD() {
            super(null);
        }

        @Override
        public void fetchLastActiveQuestions(Listener listener) {
            if(mFailure){
                listener.onQuestionsFetchFailed();
            }else{
                //Return Some Fake data for test assuming that its a success
                List<QuestionSchema> questionSchemasList = new ArrayList<>();
                questionSchemasList.add(new QuestionSchema("title1","id1","body1"));
                questionSchemasList.add(new QuestionSchema("title2","id2","body2"));
                listener.onQuestionsFetched(questionSchemasList);
            }
        }
    }
    //endregion Helper Classes----------------------------------------------
}