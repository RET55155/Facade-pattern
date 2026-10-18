package com.observatory.subsystem;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ObservationLogger {
    private final List<String> entries = new ArrayList<>();
    private final PrintStream out;

    public ObservationLogger(PrintStream out) {
        this.out = out;
    }

    public void log(String message) {
        String line = String.format("[%03d] %s", entries.size() + 1, message);
        entries.add(line);
        out.println(line);
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
