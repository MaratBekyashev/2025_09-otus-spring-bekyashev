package ru.otus.spring.dao;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import java.util.List;

public class QuestionTestData {

    public static List<Question> getQuestionList(){
        return List.of(
                new Question("How should resources be loaded form jar in Java?",
                        List.of(
                                new Answer("ClassLoader#getResourceAsStream or ClassPathResource#getInputStream", true),
                                new Answer("ClassLoader#getResource#getFile + FileReader", false),
                                new Answer("Wingardium Leviosa", false)
                                )),
                new Question("Which option is a good way to handle the exception?",
                        List.of(
                                new Answer("@SneakyThrow", false),
                                new Answer("e.printStackTrace()", false),
                                new Answer("Rethrow with wrapping in business exception (for example, QuestionReadException)", true),
                                new Answer("Ignoring exception", false))),
                new Question("What is the deadline for completing homework on the course \"Developer on Spring Framework\" in OTUS?",
                        List.of(
                                new Answer("One week", false),
                                new Answer("Two months", false),
                                new Answer("Before the end of the course", true),
                                new Answer("Three days", false)))
        );
    }
}
