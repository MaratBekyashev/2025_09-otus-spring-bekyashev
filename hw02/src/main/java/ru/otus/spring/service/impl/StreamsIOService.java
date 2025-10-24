package ru.otus.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.spring.service.IOService;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StreamsIOService implements IOService {
    private final PrintStream printStream;

    private final BufferedReader reader;

    @Override
    public String getLine(String hint) {
        printLine(hint);
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }
}
