package io.dailyworker.framework.controller;

import io.dailyworker.framework.aop.CustomRequestLocal;
import io.dailyworker.framework.aop.CustomRequest;

public class CustomHttpRequestContext {

  private static ThreadLocal<CustomRequest> threadLocal = new ThreadLocal<>();

  static void generateThread(CustomRequest customRequest) {
    threadLocal.set(customRequest);
  }

  static void removeThread() {
    threadLocal.remove();
  }

  public static CustomRequest getCustomServletHttpRequest() {
    CustomRequest customRequest = threadLocal.get();

    if(customRequest != null) {
      return customRequest;
    }
    CustomRequestLocal customRequestLocal = new CustomRequestLocal();
    generateThread(customRequestLocal);
    return customRequestLocal;
  }
}
