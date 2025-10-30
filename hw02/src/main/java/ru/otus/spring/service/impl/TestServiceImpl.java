package ru.otus.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.spring.config.TestConfig;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import ru.otus.spring.service.IOService;
import ru.otus.spring.service.TestService;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final TestConfig appProperties;

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        String name = ioService.getLine(">>> Please enter your name: \n");
        ioService.printLine("");
        ioService.printFormattedLine("Dear " + name + ", please answer the questions below: %n");
        List<Question> questions = questionDao.findAll();
        doTesting(questions);
    }

    private void doTesting(List<Question> questions) {
        int totalQuestionCounter = questions.size();
        int totalCorrectAnswersCounter = 0;
        for (Question question: questions) {
            StringBuilder questionBuilder = new StringBuilder(question.text());
            questionBuilder.append("%n").append("%s%n".repeat(question.answers().size()));
            String[] questionsToPrint = question
                    .answers()
                    .stream()
                    .map(Answer::text)
                    .toList()
                    .toArray(new String[0]);
            ioService.printFormattedLine(questionBuilder.toString(), questionsToPrint);
            String userAnswer = ioService.getLine(">>> Please enter the correct answer:");
            List<String> correctAnswersList = getCorrectAnswersForQuestion(question);
            if (correctAnswersList.contains(userAnswer.toLowerCase())) {
                totalCorrectAnswersCounter++;
            }
        }
        showTestResults(totalQuestionCounter, totalCorrectAnswersCounter);
    }

    private void showTestResults (int totalQuestionQty, int rightAnswersQty) {
        ioService.printFormattedLine("Testing finished. You answered " + rightAnswersQty +
                " out of " + totalQuestionQty + " questions correctly.");
        if (rightAnswersQty < this.appProperties.getRightAnswersCountToPass()) {
            ioService.printFormattedLine("Ups! You failed the test");
        } else {
            ioService.printFormattedLine("Congratulations! You passed the test");
        }
    }

    private List<String> getCorrectAnswersForQuestion(Question question) {
        return question
                .answers()
                .stream()
                .filter(Answer::isCorrect)
                .map(e -> e.text().toLowerCase())
                .toList();
    }
}
