package ru.otus.spring.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.service.IOService;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.otus.spring.service.impl.QuestionTestData.getQuestion1;
import static ru.otus.spring.service.impl.QuestionTestData.getQuestion2;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        given(questionDao.findAll())
        .willReturn(List.of(getQuestion1(), getQuestion2()));
    }

    @Test
    public void executeTest() {
        testService.executeTest();
        verify(ioService, times(1)).printLine("");
        verify(questionDao, times(1)).findAll();

    }

}