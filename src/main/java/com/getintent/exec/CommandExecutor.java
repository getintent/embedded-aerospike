package com.getintent.exec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.IOException;

public class CommandExecutor {
    private final String binaryLocation;
    private final int[] exitValues;

    public CommandExecutor(String binaryLocation, int[] exitValues) {
        this.binaryLocation = binaryLocation;
        this.exitValues = exitValues;
    }

    public ExecResult execute(long timeOut, String... args) throws IOException {
        CommandLine cmdLine = new CommandLine(binaryLocation);
        cmdLine.addArguments(args);
        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeOut);
        executor.setWatchdog(watchdog);
        executor.setExitValues(exitValues);
        CollectingLogOutputStream collectingLogOutputStream = new CollectingLogOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(collectingLogOutputStream);
        executor.setStreamHandler(psh);
        int exitCode = executor.execute(cmdLine);
        return new ExecResult(exitCode, collectingLogOutputStream.getLines());
    }
}
