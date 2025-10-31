package ru.otus.spring.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.spring.config.TestConfig;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.service.IOService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.otus.spring.dao.QuestionTestData.getQuestionList;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private TestConfig appProperties;
    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        given(questionDao.findAll())
        .willReturn(getQuestionList());

        given(appProperties.getRightAnswersCountToPass()).willReturn(1);

        given(ioService.getLine(">>> Please enter your name: \n")).willReturn("Marat Bekyashev");

        given(ioService.getLine(">>> Please enter the correct answer:")).willReturn("userAnswer");
    }

    @Test
    @DisplayName("Юнит тест метода тестирования")
    public void executeTest() {
        testService.executeTest();
        verify(ioService, times(1)).printLine("");
        verify(questionDao, times(1)).findAll();

    }

}