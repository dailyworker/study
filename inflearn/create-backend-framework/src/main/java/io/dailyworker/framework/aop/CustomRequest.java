package io.dailyworker.framework.aop;

import java.io.Serializable;

public abstract class CustomRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  public abstract void put(String key, Object object);

  public abstract Object get(String key);

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
}
