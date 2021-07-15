package io.dailyworker.framework.aop;

import java.util.HashMap;

public class CustomHttpRequestLocal extends CustomRequest {

  private static final long serialVersionUID = 1L;

  private HashMap<String, Object> map = new HashMap<>();

  public CustomHttpRequestLocal() {}

  @Override
  public void put(String key, Object object) {
    map.put(key, object);
  }

  @Override
  public Object get(String key) {
    return map.get(key);
  }
}
