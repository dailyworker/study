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
}
