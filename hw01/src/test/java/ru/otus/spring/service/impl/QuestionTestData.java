package ru.otus.spring.service.impl;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionTestData {
    public static Question getQuestion1() {
        String text = "Question №1 for unit test?";
        List<Answer> answerList = new ArrayList<>();
        answerList.add(new Answer("Answer 1 on question 1 for unit test", false));
        answerList.add(new Answer("Answer 2 on question 1 for unit test", true));
        answerList.add(new Answer("Answer 3 on question 1 for unit test", false));
        return new Question(text, answerList);
    }

    public static Question getQuestion2() {
        String text = "Question №2 for unit test?";
        List<Answer> answerList = new ArrayList<>();
        answerList.add(new Answer("Answer 1 on question 2 for unit test", false));
        answerList.add(new Answer("Answer 2 on question 2 for unit test", true));
        answerList.add(new Answer("Answer 3 on question 2 for unit test", false));
        answerList.add(new Answer("Answer 4 on question 2 for unit test", false));

        return new Question(text, answerList);
    }
}
