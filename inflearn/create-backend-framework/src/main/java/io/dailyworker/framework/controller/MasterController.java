package io.dailyworker.framework.controller;

import io.dailyworker.framework.aop.CustomHttpRequest;
import io.dailyworker.framework.db.SQLitePoolTransaction;
import io.dailyworker.framework.db.Transaction;
import io.dailyworker.framework.model.Welcome;
import java.io.IOException;
import java.sql.SQLException;
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

    Transaction transaction = new SQLitePoolTransaction();
    TransactionContext.load(transaction);

    try {
      Welcome welcome = new Welcome();
      String jspUrl = welcome.execute();

      TransactionContext.commit();

      RequestDispatcher dispatcher = req.getRequestDispatcher(jspUrl);
      dispatcher.forward(req, resp);

    } catch (Exception e) {
      e.printStackTrace();
      try {
        TransactionContext.rollback();
      } catch (SQLException e) {
        e.printStackTrace();
        throw new ServletException(e.getMessage());
      }
    } finally {
      TransactionContext.unload();
      CustomRequestContext.unload();
    }
  }
}
