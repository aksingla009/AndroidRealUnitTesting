package com.aman.androidrealunittesting.unittesting.testdata;

import com.aman.androidrealunittesting.unittesting.questions.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionsTestData {
    public static Question getQuestion() {
        return new Question("id", "title");
    }

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("id1", "title1"));
        questions.add(new Question("id2", "title2"));
        return questions;
    }
}
