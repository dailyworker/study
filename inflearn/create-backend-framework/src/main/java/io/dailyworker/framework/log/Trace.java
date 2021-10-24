package io.dailyworker.framework.log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Trace {
    private TraceRunner traceRunner = TraceRunner.of();
    private String className = null;

    @SuppressWarnings("rawtypes")
    public Trace(Object obj) {
        if(obj instanceof String) {
            this.className = (String) obj;
        }
        if (obj instanceof Class) {
            this.className = ((Class) obj).getName();
            return;
        }

        this.className = obj.getClass()
                .getName();
    }

    public void write(String log) {
        traceRunner.write("out", this.className, log);
    }

    public void writeError(String log) {
        traceRunner.write("err", this.className, log);
        traceRunner.write("out", this.className, log);
    }

    public void writeError(Exception e) {
        if(e == null) {
            return;
        }

        StringWriter sw = null;
        PrintWriter pw = null;

        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            traceRunner.write("err", this.className, sw.toString());
            traceRunner.write("out", this.className, sw.toString());

        } catch (Exception ex) {
            checkNotClose(sw, pw, ex);
        }
    }

    public void write(Exception e) {
        if (e == null) {
            return;
        }

        StringWriter sw = null;
        PrintWriter pw = null;

        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            traceRunner.write("err", this.className, sw.toString());

        } catch (Exception ex) {
            checkNotClose(sw, pw, ex);
        }
    }

    private void checkNotClose(StringWriter sw, PrintWriter pw, Exception ex) {
        checkStringWriterResource(sw);
        checkPrintWriterResource(pw);
        ex.printStackTrace();
    }

    private void checkStringWriterResource(StringWriter sw) {
        try {
            if (sw != null) {
                sw.close();
            }
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }

    private void checkPrintWriterResource(PrintWriter pw) {
        try {
            if (pw != null) {
                pw.close();
            }
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }
}
