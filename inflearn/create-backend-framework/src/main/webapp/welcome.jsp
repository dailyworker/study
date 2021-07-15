<%@ page import="io.dailyworker.framework.controller.CustomRequestContext" %>
<%@ page import="io.dailyworker.framework.aop.CustomRequest" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
    CustomRequest customRequest = CustomRequestContext.get();
    String msg = customRequest.getString("msg");
%>
<html>
<head>
</head>
<body>
    <%=msg%>
</body>
</html>