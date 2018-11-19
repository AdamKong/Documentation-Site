<%--   
date: 11 May 2018  
auth: Adam Kong
description: the page for successful operation.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="isLoggedIn.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Operation Succeeded!</title>
<link rel="shortcut icon" href="../images/favicon.ico" />
</head>
<body>
<h2>
The operation has been done successfully: <%=request.getAttribute("success").toString() %><br/>
</h2>
<h3>
Please click <a href="index.jsp?uri=art_cu.jsp">here</a> to continue creating articles!<br/>
</h3>
<h3>
Please click <a href="index.jsp?uri=cat_cu.jsp">here</a> to continue creating categories!<br/>
</h3>
<h3>
Please click <a href="index.jsp?uri=adm_cu.jsp">here</a> to continue creating administrator!<br/>
</h3>
<h3>
Please click <a href="index.jsp">here</a> to go to the admin home page!
</h3>
</body>
</html>