package ru.otus.spring.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

@Configuration
@Data
@PropertySource("classpath:application.properties")
@Component
public class AppProperties implements TestConfig, TestFileNameProvider {

    @Value("${test.rightAnswersCountToPass}")
    private int rightAnswersCountToPass;

    @Value("${test.fileName}")
    private String testFileName;

    @Bean
    public PrintStream systemOut() {
        return System.out;
    }

    @Bean
    public BufferedReader systemIn() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

}
