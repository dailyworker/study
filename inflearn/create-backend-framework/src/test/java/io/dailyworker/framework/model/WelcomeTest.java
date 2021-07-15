package io.dailyworker.framework.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.CustomRequestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WelcomeTest {
  @Test
  @DisplayName("AOP를 활용한 방법으로 테스트를 성공시킬 수 있다.")
  public void test() throws Exception {
    String name = "abcd";
    CustomRequest customRequest = CustomRequestContext.get();
    customRequest.put("name", name);

    Welcome welcome = new Welcome();
    welcome.execute();

    Object msg = customRequest.get("msg");

    assertEquals("WELCOME = " + name, msg);
  }
}
