package io.dailyworker.framework.aop;

import java.io.Serializable;

public class Session implements Serializable {
    // 직렬화를 위해서 버전 UID를 설정
    private static final long serialVersionUID = 1L;

    private String id = null;
    private String auth = null;
    private String ip = null;

    public Session(String id, String auth, String ip) {
        this.id = id;
        this.auth = auth;
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public String getAuth() {
        return auth;
    }

    public String getIp() {
        return ip;
    }
}
