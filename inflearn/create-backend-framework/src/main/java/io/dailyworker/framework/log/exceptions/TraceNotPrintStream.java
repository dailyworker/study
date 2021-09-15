package io.dailyworker.framework.log.exceptions;

import java.io.OutputStream;
import java.io.PrintStream;

public class TraceNotPrintStream extends PrintStream {
    public TraceNotPrintStream() {
        super(new TraceNotOutputStream());
    }
}
