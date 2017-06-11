<%@page import="java.util.Enumeration"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.util.Locale"%>
<%@ page language="java" contentType="text/plain;charset=UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:forEach var="head" items="${header}">
${head.key}：${head.value}
</c:forEach>
<c:forEach var="para" items="${param}">
	${para.key}：${para.value}
</c:forEach>


<%
	Locale locale = request.getLocale();
	ResourceBundle rb = ResourceBundle.getBundle("buildinfo", locale);
	Enumeration<String> ite = rb.getKeys();
	while (ite.hasMoreElements()) {
		String key = ite.nextElement();
		out.println(key + "	:" + rb.getString(key));
	}
%>