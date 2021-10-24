package io.dailyworker.framework.log;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class TraceWriter {

    String logKey = null;
    String logFileUrl = null;

    boolean isMulti = false;

    int initCnt = 0;
    int errCnt = 0;

    private BufferedWriter out = null;


    public TraceWriter(String logKey, String logFileUrl, boolean isMulti) {
        this.logKey = logKey;
        this.logFileUrl = logFileUrl;
        this.isMulti = isMulti;

        try {
            this.out = new BufferedWriter(new FileWriter(this.logFileUrl, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void println(String log) {
        if(this.out == null) {
            return;
        }

        try {
            this.out.write(log);
            this.out.newLine();

            if(!isMulti) {
                this.out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if(this.out == null) {
            return;
        }
        try {
            this.out.flush();
            this.out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        if(this.out == null) {
            return;
        }
        try {
            this.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
