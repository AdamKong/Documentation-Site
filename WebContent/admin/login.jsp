<%--   
date: 11 May 2018  
auth: Adam Kong
description: the login portal
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="isLoggedIn.jsp" %>
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
    <title>Webex Teams Docs Admin Portal</title>
    <link rel="shortcut icon" href="../images/favicon.ico" />
    <link rel="stylesheet" href="../css/backend.css">
    <script src="../js/jquery-3.3.1.js"></script>    
    <script src="../js/md5-min.js"></script>
    <script src="../js/admin_login.js"></script>
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
    	
    </div>
</div>
<div class="login_window">
    <header>
        Webex Help Docs Admin
    </header>
    <label>
	<%
	   	if (request.getAttribute("promptMessage") != null) {
	   		out.print(request.getAttribute("promptMessage").toString());
	   	}	   	
	%>
    </label>
    <form name="login" action="adminlogin" method="post">
        Username:  <input name="username" type="text" placeholder=" username" class="username"/>(6~20 chars, no special chars)
        <br/>
        Password:  <input name="password" type="password" placeholder=" password" class="password"/>(6~20 chars)
        <br/>
        Verification Code: <input type="text" name="validationCode"/> <img class="validationCode_img"  src="generateVerificationCodeAndBGImage">
        <br/>
        <input type="submit" value="Log In"/>
    </form>
</div>
</body>
</html>