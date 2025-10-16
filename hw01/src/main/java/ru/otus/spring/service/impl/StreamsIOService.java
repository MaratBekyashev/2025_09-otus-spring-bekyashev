package ru.otus.spring.service.impl;

import ru.otus.spring.service.IOService;
import java.io.*;

public class StreamsIOService implements IOService {
    private final PrintStream printStream;

    private final BufferedReader reader;

    public StreamsIOService(PrintStream printStream, InputStream inputStream) {

        this.printStream = printStream;
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public String getLine(String hint) {
        printLine(hint);
        try {
            String readedLine = reader.readLine();
            return readedLine;
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
