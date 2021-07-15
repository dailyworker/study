package io.dailyworker.framework.aop.ex;

import io.dailyworker.framework.aop.CustomHttpRequest;
import io.dailyworker.framework.aop.CustomHttpRequestLocal;
import io.dailyworker.framework.aop.CustomRequest;

public class AopExample {

  private CustomHttpRequestLocal customHttpRequestLocal = new CustomHttpRequestLocal(); // Spring의 @Autowired와 비슷함
  private static CustomHttpRequestLocal customStaticRequestLocal = new CustomHttpRequestLocal(); // Thread Safe 하지 않음.

  static ThreadLocal<CustomHttpRequest> th = new ThreadLocal<>();

  public void executeWithParam(CustomRequest request) {
    request.put("name", "ABCD");
  }

  public void executeWithObject() {
    customHttpRequestLocal.put("name", "ABCD");
    customStaticRequestLocal.put("name", "ABCD");
  }

  public void executeWithThreadLocal() {
    CustomHttpRequest customHttpRequest = th.get(); // Thread Safe 함
    customHttpRequest.put("name", "ABCD");
  }

  public static void executeStaticWithThreadLocal() {
    CustomHttpRequest customHttpRequest = th.get(); // static method여도 Thread Safe함을 보장
    customHttpRequest.put("name", "ABCD");
  }
}
