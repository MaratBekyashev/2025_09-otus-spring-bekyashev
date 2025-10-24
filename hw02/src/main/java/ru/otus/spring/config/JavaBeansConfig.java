package ru.otus.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.spring.dao.CsvQuestionDao;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

@Configuration
public class JavaBeansConfig {

    @Bean
    public AppConfig TestFileNameProvider(){
        return new AppConfig("testQuestions.csv");
    }

    @Bean
    public CsvQuestionDao questionDao (AppConfig testFileNameProvider) {
        return new CsvQuestionDao(testFileNameProvider);
    }

    @Bean
    public PrintStream systemOut() {
        return System.out;
    }

    @Bean
    public BufferedReader systemIn() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

}
