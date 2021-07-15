package io.dailyworker.framework.controller;

import io.dailyworker.framework.aop.CustomHttpRequest;
import io.dailyworker.framework.model.Welcome;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MasterController extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    CustomHttpRequest customHttpRequest = new CustomHttpRequest(req);
    CustomRequestContext.load(customHttpRequest); // 이 부분을 안줄 시 CustomHttpRequestLocal로 동작한다.

    try {
      Welcome welcome = new Welcome();
      String jspUrl = welcome.execute();

      RequestDispatcher dispatcher = req.getRequestDispatcher(jspUrl);
      dispatcher.forward(req, resp);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      CustomRequestContext.unload();
    }
  }
}
