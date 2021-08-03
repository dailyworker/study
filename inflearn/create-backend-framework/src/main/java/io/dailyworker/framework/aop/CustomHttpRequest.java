package io.dailyworker.framework.aop;

import javax.servlet.http.HttpServletRequest;

public class CustomHttpRequest extends CustomRequest {

  private HttpServletRequest request = null;

  public CustomHttpRequest(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public void put(String key, Object object) {
    request.setAttribute(key, object);
  }

  @Override
  public Object get(String key) {
    Object attribute = request.getAttribute(key);
    if(attribute != null) {
      return attribute;
    }
    return request.getParameter(key);
  }

  @Override
  public void setSession(Session session) {
    request.setAttribute(KEY_SESSION, session);
    request.getSession().setAttribute(KEY_SESSION, session);
  }
}
