package io.dailyworker.framework.log.exceptions;

import java.io.IOException;
import java.io.OutputStream;

public class TraceNotOutputStream extends OutputStream {
    @Override
    public void write(int b) throws IOException {
        return;
    }
}
