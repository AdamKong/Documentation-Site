<%--   
date: 15 Jun 2018  
auth: Adam Kong
description: error page
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="isLoggedIn.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Error Occurred!</title>
<link rel="shortcut icon" href="../images/favicon.ico" />
</head>
<%
session.invalidate();
%>
<body>
<h2>
	Error occurred: <%=request.getAttribute("error").toString() %> </br>
</h2>
<h3>
	The current session has been destroyed! Please click <a href="login.jsp">here</a> to re-log in and operate!
</h3>
</body>
</html>