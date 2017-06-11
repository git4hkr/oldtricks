<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="buildinfo" var="resource" scope="application" />

<html>
<head>
<style type="text/css">
table {
	border-top: 1px solid navy;
	border-left: 1px solid navy;
	border-collapse: collapse;
	border-spacing: 0;
	background-color: #ffffff;
	empty-cells: show;
}

th {
	border-right: 1px solid navy;
	border-bottom: 1px solid navy;
	color: navy;
	background-color: skyblue;
	background-image: url(../img/table-back.gif);
	background-position: left top;
	padding: 0.3em 1em;
	text-align: center;
}

td {
	border-right: 1px solid navy;
	border-bottom: 1px solid navy;
	padding: 0.3em 1em;
}

DIV {
	float: none;
}
</style>
</head>
<body>
日本語
	<div>
		<table>
			<tr>
				<th colspan="2">sg1</th>
			</tr>
			<tr>
				<th>Key</th>
				<th>Value</th>
			</tr>
			<c:forEach var="key" items="${resource.resourceBundle.keys}">
				<tr>
					<td><c:out value="${key}" /></td>
					<td><fmt:message key="${key}" bundle="${resource}" /></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>

