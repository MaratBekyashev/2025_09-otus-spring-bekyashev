package ru.otus.spring.service.impl;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import java.util.List;

public class QuestionTestData {

    public static List<Question> getQuestionList(){
        return List.of(
                new Question("Question №1 for unit test?",
                        List.of(
                                new Answer("Answer 1 on question 1 for unit test", false),
                                new Answer("Answer 2 on question 1 for unit test", true),
                                new Answer("Answer 3 on question 1 for unit test", false)
                                )),
                new Question("Question №2 for unit test?",
                        List.of(
                                new Answer("Answer 1 on question 2 for unit test", false),
                                new Answer("Answer 2 on question 2 for unit test", true),
                                new Answer("Answer 3 on question 2 for unit test", false),
                                new Answer("Answer 4 on question 2 for unit test", false)))
        );
    }
}
