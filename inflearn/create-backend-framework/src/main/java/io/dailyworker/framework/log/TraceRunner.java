package io.dailyworker.framework.log;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.aop.Session;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.log.exceptions.TraceNotPrintStream;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class TraceRunner {
    private static final TraceRunner TRACE = new TraceRunner();

    private String logDir = null;
    private String cntr = "notCntr";
    private String host = "notHost";
    private String rnd = makeRnd(8);

    private boolean isWriteLog = InitProperty.isWriteLog();
    private boolean isInit = false;
    private boolean isMulti = false;

    private final long logFileSizeLimit = 1024 * 1000 * 50;
    private ConcurrentHashMap<String, TraceWriter> traceWriteMap = new ConcurrentHashMap<String, TraceWriter>();
    private ConcurrentHashMap<String, String> noLogMap = new ConcurrentHashMap<String, String>();

    private TraceRunner() {
        readTraceProperties();
    }

    public void init(HttpServletRequest request) {
        if(this.isInit) {
            return;
        }

        this.isMulti = true;
        this.cntr = "p" + request.getServerPort();

        init();
    }

    public void init(String cntr) {
        if(this.isInit) {
            return;
        }

        this.isMulti = false;
        this.cntr = cntr;

        init();
    }

    public void init(String cntr, boolean isMulti) {
        if(this.isInit) {
            return;
        }

        this.isMulti = isMulti;
        this.cntr = cntr;

        init();
    }

    private synchronized void init() {
        try {
            if(this.isInit) {
                return;
            }
            this.isInit = true;

            if(this.isWriteLog) {
                if(this.isMulti) {
                    System.setOut(new TraceNotPrintStream());
                }
            }

            String logDirPrefix = InitProperty.logDirPattern();

            try {
                String hostName = InetAddress.getLocalHost()
                        .getHostName();
                if(hostName.contains(".")) {
                    throw new Exception("error hostName [" + hostName + "]");
                }

                this.host = hostName;
            } catch (Exception e) {
                e.printStackTrace();
            }

            logDirPrefix = logDirPrefix.replace("[host]", this.host);
            logDirPrefix = logDirPrefix.replace("[rnd]", this.rnd);
            logDirPrefix = logDirPrefix.replace("[cntr]", this.cntr);

            (new File(logDirPrefix)).mkdir();

            if(!(new File(logDirPrefix)).isDirectory()) {
                throw new Exception("not Directory [" + logDirPrefix + "]");
            }

            this.logDir = logDirPrefix;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String makeLogFileUrl(String logKey) {
        if(this.logDir == null) {
            return null;
        }

        if(logKey.contains(".")) {
            (new RuntimeException("no logKey [" + logKey + "]")).printStackTrace();
            return null;
        }

        String logFilePattern = InitProperty.logFilePattern();
        logFilePattern = logFilePattern.replace("[rnd]", this.rnd);
        logFilePattern = logFilePattern.replace("[name]", logKey);

        return this.logDir + File.separatorChar + logFilePattern;
    }

    private String makeLogStr(String className, String log, CustomRequest customRequest) {
        String classNameShort = makeClassNameShort(className);

        String dtm = getLogDateTimeByFormat();
        int hashCode = customRequest.hashCode();

        StringBuffer sb = new StringBuffer();
        sb.append("[" + dtm + "]");
        sb.append("[" + hashCode + "]");
        sb.append("[" + classNameShort + "]");

        String serviceKey = customRequest.getString(CustomRequest.KEY_SERVICE_KEY);
        if(!"".equals(serviceKey)) {
            sb.append("[" + serviceKey + "]");
        }

        Session user = customRequest.getSession();
        if(user != null) {
            sb.append("[" + user.getId() + "]");
        }

        sb.append(log);
        return sb.toString();
    }

    private void rollingLogFile(String logFileUrl) {
        File file = new File(logFileUrl);

        String renameLogFileUrl = logFileUrl + "." + getLogDateTime();
        File renameFile = new File(renameLogFileUrl);
        file.renameTo(renameFile);
    }

    private boolean isRolling(String logFileUrl) {
        File file = new File(logFileUrl);

        if(!file.isFile()) {
            return false;
        }

        return file.length() > this.logFileSizeLimit;
    }

    void write(String logKey, String className, String log) {
        CustomRequest customRequest = CustomRequestContext.get();

        boolean isNotLogWrite = isNotLogWrite(className, customRequest);
        if(isNotLogWrite) {
            return;
        }

        log = makeLogStr(className, log, customRequest);

        if(!this.isWriteLog) {
            System.out.println(log);
            return;
        }

        if(!this.isMulti) {
            System.out.println(log);
        }

        synchronized (logKey.intern()) {
            TraceWriter traceWriter = traceWriteMap.get(logKey);
            if(traceWriter == null) {
                String logFileUrl = makeLogFileUrl(logKey);
                if(logFileUrl == null) {
                    return;
                }
                if(isRolling(logFileUrl)) {
                    rollingLogFile(logFileUrl);
                }

                traceWriter = new TraceWriter(logKey, logFileUrl, isMulti);
                traceWriteMap.put(logKey, traceWriter);
            }

            traceWriter.println(log);
            traceWriter.initCnt++;

            if(traceWriter.initCnt < 5000) {
                return;
            }

            traceWriter.initCnt = 0;

            if(!isRolling(traceWriter.logFileUrl)) {
                return;
            }

            traceWriter.close();
            rollingLogFile(traceWriter.logFileUrl);

            traceWriter = new TraceWriter(logKey, traceWriter.logFileUrl, this.isMulti);
            traceWriteMap.put(logKey, traceWriter);
        }
    }

    public void flush() {
        Iterator<String> iterator = traceWriteMap.keySet()
                .iterator();

        while(iterator.hasNext()) {
            String key = iterator.next();
            TraceWriter traceWriter = traceWriteMap.get(key);
            traceWriter.flush();
        }
    }

    boolean isNotLogWrite(String className, CustomRequest customRequest) {
        String serviceKey = customRequest.getString(CustomRequest.KEY_SERVICE_KEY);

        if(noLogMap.containsKey("noLog.serviceKey." + serviceKey)) {
            return true;
        }

        return noLogMap.containsKey("noLog.className." + className);
    }

    private void readTraceProperties() {
        BufferedReader br = null;

        try {
            String url = InitProperty.traceConfigUri();
            br = new BufferedReader(new InputStreamReader(new FileInputStream(url)));

            for(int i = 0; i < 10000; i++) {
                String str = br.readLine();
                if(str == null) {
                    break;
                }
                str = str.trim();

                if("".equals(str)) {
                    continue;
                }
                if(str.startsWith("#")) {
                    continue;
                }
                if(str.startsWith("noLog")) {
                    noLogMap.put(str, "true");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        noLogMap.clear();
        readTraceProperties();
    }

    private String makeClassNameShort(String className) {
        int fqcnLastClassName = className.lastIndexOf(".");
        if(fqcnLastClassName >= 1) {
            return className.substring(fqcnLastClassName + 1);
        }
        return className;
    }

    private String getLogDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
        return simpleDateFormat.format(new Date());
    }

    private String getLogDateTimeByFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA);
        return simpleDateFormat.format(new Date());
    }

    private String makeRnd(int length) {
        StringBuffer temp = new StringBuffer();
        SecureRandom rnd = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = rnd.nextInt(3);
            switch (randomIndex) {
                case 0:
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    temp.append(rnd.nextInt(10));
                    break;
            }
        }
        return temp.toString();
    }


    public static TraceRunner of() {
        return TRACE;
    }
}
