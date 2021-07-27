package io.dailyworker.framework.view;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.CustomRequestContext;

public class Welcome {

  public String execute() {

    CustomRequest customRequest = CustomRequestContext.get();

    String name = customRequest.getString("name");
    String msg = "WELCOME = " + name;

    customRequest.put("msg", msg);
    return "/welcome.jsp";
  }
}
