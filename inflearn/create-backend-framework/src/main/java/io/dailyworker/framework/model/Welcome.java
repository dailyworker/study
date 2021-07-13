package io.dailyworker.framework.model;

import javax.servlet.http.HttpServletRequest;

public class Welcome {

  public String execute(HttpServletRequest req) {
    String name = req.getParameter("name");
    String msg = "WELCOME = " + name;
    req.setAttribute("msg", msg);
    return "/welcome.jsp";
  }
}
