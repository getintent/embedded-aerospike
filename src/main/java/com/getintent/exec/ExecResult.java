package com.getintent.exec;


import java.util.List;

public class ExecResult {
    private final List<String> logLines;
    private final int exitCode;

    public ExecResult(int exitCode, List<String> logLines) {
        this.logLines = logLines;
        this.exitCode = exitCode;
    }

    public List<String> logLines() {
        return logLines;
    }

    public int getExitCode() {
        return exitCode;
    }
}
