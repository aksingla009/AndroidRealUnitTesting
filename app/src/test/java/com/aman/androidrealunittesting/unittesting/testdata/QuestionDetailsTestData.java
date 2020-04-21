package com.aman.androidrealunittesting.unittesting.testdata;

import com.aman.androidrealunittesting.unittesting.questions.QuestionDetails;

public class QuestionDetailsTestData {
    public static QuestionDetails getQuestionDetails1() {
        return new QuestionDetails("id1", "title1", "body1");
    }

    public static QuestionDetails getQuestionDetails2() {
        return new QuestionDetails("id2", "title2", "body2");
    }
}
