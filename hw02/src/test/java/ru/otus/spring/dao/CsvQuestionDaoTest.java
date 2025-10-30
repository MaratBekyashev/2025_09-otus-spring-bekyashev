package ru.otus.spring.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spring.config.TestFileNameProvider;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static ru.otus.spring.dao.QuestionTestData.getQuestionList;

@DisplayName("Репозиторий для чтения вопросов с ответами из CSV файла ")
class CsvQuestionDaoTest {

    private QuestionDao questionDao;

    @BeforeEach
    void setUp() {
        TestFileNameProvider testFileNameProvider = mock(TestFileNameProvider.class);
        doReturn("questions.csv").when(testFileNameProvider).getTestFileName();
        questionDao = new CsvQuestionDao(testFileNameProvider);
    }

    @Test
    @DisplayName("Должен загружать список вопросов с ответами из CSV файла")
    void shouldReturnQuestionsList() {
        var actualQuestions = questionDao.findAll();
        var expectedQuestions = getQuestionList();
        assertThat(actualQuestions)
                .usingRecursiveComparison()
                .isEqualTo(expectedQuestions);
    }
}