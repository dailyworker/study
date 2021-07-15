package io.dailyworker.framework.controller;

import io.dailyworker.framework.aop.CustomHttpRequestLocal;
import io.dailyworker.framework.aop.CustomRequest;

public class CustomRequestContext {

  private static final ThreadLocal<CustomRequest> threadLocal = new ThreadLocal<>();

  static void load(CustomRequest customRequest) {
    threadLocal.set(customRequest);
  }

  static void unload() {
    threadLocal.remove();
  }

  public static CustomRequest get() {
    CustomRequest customRequest = threadLocal.get();

    if(customRequest != null) {
      return customRequest;
    }
    CustomHttpRequestLocal customHttpRequestLocal = new CustomHttpRequestLocal();
    load(customHttpRequestLocal);
    return customHttpRequestLocal;
  }
}
