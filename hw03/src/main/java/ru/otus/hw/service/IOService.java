package ru.otus.spring.service;

public interface IOService {

    String getLine(String hint);

    void printLine(String s);

    void printFormattedLine(String s, Object ...args);
}
