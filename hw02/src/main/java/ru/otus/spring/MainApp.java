package ru.otus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.spring.service.TestRunnerService;

@ComponentScan
public class MainApp {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainApp.class);
        TestRunnerService testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
    }
}
