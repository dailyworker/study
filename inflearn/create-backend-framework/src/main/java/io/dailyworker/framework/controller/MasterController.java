package io.dailyworker.framework.controller;

import io.dailyworker.framework.aop.CustomHttpRequest;
import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.aop.Session;
import io.dailyworker.framework.db.SQLiteJdbcTransaction;
import io.dailyworker.framework.db.Transaction;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class MasterController extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    CustomHttpRequest customRequest = createCustomRequest(req);
    CustomRequestContext.load(customRequest); // 이 부분을 안줄 시 CustomHttpRequestLocal로 동작한다.

    Transaction transaction = new SQLiteJdbcTransaction();
    TransactionContext.load(transaction);

    try {
      String key = req.getPathInfo().substring(1);
      String jspViewName = execute(key);

      MasterControllerDao dao = new MasterControllerDao();
      CustomRequest view = dao.view(jspViewName);
      String jsp = view.getString("JSP");

      TransactionContext.commit();

      RequestDispatcher dispatcher = req.getRequestDispatcher(jsp);
      dispatcher.forward(req, resp);

    } catch (Exception e) {
      e.printStackTrace();
      try {
        TransactionContext.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
      }
    } finally {
      TransactionContext.unload();
      CustomRequestContext.unload();
    }
  }

  public static String execute(String key) throws Exception {
    MasterControllerDao dao = new MasterControllerDao();

    CustomRequest controller = dao.controller(key);
    String className = controller.getString("CLASS_NAME");
    String methodName = controller.getString("METHOD_NAME");

    boolean checkUrlAuth = checkUrlAuth(controller);
    if(!checkUrlAuth) {
      throw new Exception("접근 제한된 서비스 입니다.");
    }

    @SuppressWarnings("rawtypes")
    Class clazz = Class.forName(className);

    @SuppressWarnings({"rawtypes", "unchecked" })
    Constructor constructor = clazz.getConstructor();

    Object instance = constructor.newInstance();

    if (!(instance instanceof BizController)) {
      throw new Exception();
    }

    Method declaredMethod = instance.getClass().getDeclaredMethod(methodName);

    return (String) declaredMethod.invoke(instance);
  }

  private static boolean checkUrlAuth(CustomRequest controller) throws Exception {
    String hasSession = controller.getString("SESSION_CHECK_YN");

    if(!"Y".equals(hasSession)) {
      return true;
    }

    CustomRequest customRequest = CustomRequestContext.get();
    Session session = customRequest.getSession();
    String auth = controller.getString("AUTH");

    if(session == null) {
      throw new Exception();
    }

    if("".equals(auth)) {
      return true;
    }

    return auth.contains(session.getAuth());
  }

  private CustomHttpRequest createCustomRequest(HttpServletRequest request) {
    CustomHttpRequest customHttpRequest = new CustomHttpRequest(request);

    String key = request.getPathInfo().substring(1);
    customHttpRequest.put(CustomRequest.KEY_SERVICE_KEY, key);

    customHttpRequest.put(CustomRequest.KEY_REMOTE_ADDR, request.getRemoteAddr());

    Object session = request.getSession().getAttribute(CustomRequest.KEY_SESSION);
    if(session instanceof Session) {
      customHttpRequest.put(CustomRequest.KEY_SESSION, session);
    }
    return customHttpRequest;
  }
}
