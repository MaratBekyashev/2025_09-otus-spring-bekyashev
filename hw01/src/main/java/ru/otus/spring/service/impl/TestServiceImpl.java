package ru.otus.spring.service.impl;


import lombok.RequiredArgsConstructor;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import ru.otus.spring.service.IOService;
import ru.otus.spring.service.TestService;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        String name = ioService.getLine(">>> Please enter your Name: \n");
        ioService.printLine("");
        ioService.printFormattedLine("Dear " + name + ", please answer the questions below: %n");
        printQuestionsWithAnswers(questionDao.findAll());
    }

    public void printQuestionsWithAnswers(List<Question> questions) {
        for (Question question : questions) {
            StringBuilder questionBuilder = new StringBuilder(question.text());
            questionBuilder.append("%n").append("%s%n".repeat(question.answers().size()));
            var questionsToPrint = question
                    .answers()
                    .stream()
                    .map(Answer::text)
                    .toList()
                    .toArray();
            ioService.printFormattedLine(questionBuilder.toString(), questionsToPrint);
        }
    }
}
