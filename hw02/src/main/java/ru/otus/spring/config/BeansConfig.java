package ru.otus.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

@Configuration
@PropertySource("classpath:application.properties")
public class BeansConfig {

    @Bean
    public PrintStream systemOut() {
        return System.out;
    }

    @Bean
    public BufferedReader systemIn() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

}
