package io.dailyworker.framework.controller;

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

    Welcome welcome = new Welcome();
    String jspUrl = welcome.execute(req);

    RequestDispatcher dispatcher = req.getRequestDispatcher(jspUrl);
    dispatcher.forward(req, resp);
  }
}
