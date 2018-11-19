<%--   
date: 11 May 2018  
auth: Adam Kong
description: the main page of the administration center.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="isLoggedIn.jsp" %>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Administrator" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge,chrome=1">
    <meta name="robots" content="noindex,nofollow,noarchive,nosnippet"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta name="theme-color" content="#ffffff" />
    <meta name="robots" content="noarchive">
    <link rel="shortcut icon" href="../images/favicon.ico" />
    <title>Webex Teams Docs Admin Center</title>

    <link rel="stylesheet" href="../css/backend.css">
    <script src="../js/jquery-3.3.1.js"></script>
    <script src="../js/md5-min.js"></script>

    <!-- include libraries(bootstrap) -->
    <link rel="stylesheet" href="../bootstrap-3.3.5/dist/css/bootstrap.min.css">
    <script type="text/javascript" src="../bootstrap-3.3.5/dist/js/bootstrap.min.js"></script>

    <!-- include summernote css/js-->
    <link rel="stylesheet" href="../summernote-master/dist/summernote.css">
    <script type="text/javascript" src="../summernote-master/dist/summernote.js"></script>
    <script src="../js/admin_main_panel.js"></script>

</head>
<body>
<div class="header">
    <div class="header_left">
        <img src="../images/ciscologo.svg" alter="Cisco Logo"/>
        <span id="colla_help">Collaboration Help</span>
        <span id="swj">Cisco Webex Teams, Meetings & Jabber</span>
    </div>

    <div class="header_middle">
        Webex Help Docs Administration Entry
    </div>
    
    <div class="header_right">
    	Admin: <%=session.getAttribute("username").toString() %>
    </div>
</div>

<div class="wrapper">

    <aside>
        <header>
            Webex Help Docs Admin
        </header>
        
        <nav>
        	<ul>
        		<li><a href="index.jsp?uri=main.jsp">Home</a></li>
        	</ul>
        </nav>

        <nav>
            <ul>
                <li> <a href="index.jsp?uri=art_cu.jsp">Create an article</a></li>
                <li> <a href="index.jsp?uri=art_sl.jsp">Search/Modify an article</a></li>
            </ul>
        </nav>

        <nav>
            <ul>
                <li> <a href="index.jsp?uri=cat_cu.jsp">Create a category</a></li>
                <li> <a href="index.jsp?uri=cat_sl.jsp">Search/Modify a category</a></li>
            </ul>
        </nav>
		<%
		String userInSession = session.getAttribute("username").toString();
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		Administrator administrator = dao.getAdministratorByUsername(userInSession);
		if(administrator.getIsSuperAdmin()) {
		%>
        <nav class="admin_only">
        	<span>Super Admin Only</span>
            <ul>
                <li> <a href="index.jsp?uri=adm_cu.jsp">Create a sub-admin account</a></li>
                <li> <a href="index.jsp?uri=adm_sl.jsp">Search/Modify a sub-admin account</a></li>
            </ul>
        </nav>
		<%
		}
		%>
		<nav>
        	<ul>
        		<li><a href="index.jsp?uri=acc_detail.jsp">Account Details</a></li>
        		<li><a href="adminlogout" class="logout">Log Out</a></li>
        	</ul>
        </nav>
    </aside>

    <main>
    	<%
    		String uri="main.jsp";
    		if(request.getParameter("uri") != null) {
    			uri = request.getParameter("uri");
    		}
    	%>
		<jsp:include page="<%=uri%>"/>
    </main>

</div>

</body>
</html>