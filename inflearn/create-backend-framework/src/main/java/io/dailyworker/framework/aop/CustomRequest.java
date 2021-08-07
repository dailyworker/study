package io.dailyworker.framework.aop;

import io.dailyworker.framework.db.Table;

import java.io.Serializable;
import java.math.BigDecimal;

public abstract class CustomRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final String KEY_SESSION = "io.dailyworker.framework.controller.KEY_SESSION";
  public static final String KEY_REMOTE_ADDR = "io.dailyworker.framework.controller.KEY_REMOTE_ADDR";
  public static final String KEY_SERVICE_KEY = "io.dailyworker.framework.controller.KEY_SERVICE_KEY";

  public abstract void put(String key, Object object);
  public abstract void  setSession(Session session);
  public abstract Object get(String key);

  public Session getSession() {
    return (Session) get(KEY_SESSION);
  }

  public String getString(String key) {
    Object obj = get(key);

    if (obj == null) {
      return "";
    }
    if(!(obj instanceof String)) {
      return "";
    }
    return (String) obj;
  }

  public String getString(String key, String defaultValue) {
    String str = getString(key);

    if("".equals(str)) {
      return defaultValue;
    }
    return str;
  }

  public boolean getBoolean(String key) {
    Object obj = get(key);

    if (obj == null) {
      return false;
    }
    if(!(obj instanceof Boolean)) {
      return false;
    }
    return (Boolean) obj;
  }

  public int getInt(String key) {
    String str = getString(key);
    if ("".equals(str)) {
      return 0;
    }
    try {
      return Integer.parseInt(str);
    } catch (Exception ex) {
      return 0;
    }
  }

  public BigDecimal getBigDecimal(String key) {
    String str = getString(key);
    if("".equals(str)) {
      return BigDecimal.ZERO;
    }
    try {
      return new BigDecimal(str);
    } catch (Exception ex) {
      return  BigDecimal.ZERO;
    }
  }

  public Table getTable(String key) {
    Object obj = get(key);
    if (obj == null) {
      return new Table();
    }
    if (obj instanceof Table) {
      return (Table) obj;
    }
    return new Table();
  }
}
