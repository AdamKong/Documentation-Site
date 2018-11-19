<%--   
date: 5 Oct 2018  
auth: Adam Kong
description: for administrator to change their own profile.
--%>
<%@ include file="isLoggedIn.jsp"%>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Administrator" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Date" %>
<header>
<%
Administrator administrator = null;
if(session.getAttribute("adminObj") != null){
	administrator = (Administrator)session.getAttribute("adminObj");
	out.println("Change Admin Profile");
}else{
	request.setAttribute("error", "Failed to get your account info. Please re-log in and try again!");
	request.getRequestDispatcher("error.jsp").forward(request, response);
	return;
}
if(administrator == null){
	request.setAttribute("error", "Failed to get your objectify your administrator info. Please re-log in and try again!");
	request.getRequestDispatcher("error.jsp").forward(request, response);
	return;
}
%>
</header>
<form action="changeProfile" method="post" class="adm_form">
	<input type="hidden" name="adminId" value="<%=administrator.getId() %>" />
    <p><span>Username (username can NOT be changed, only super admin can do it for you):</span><br/>
    	<input type="text" name="username" value="<%=administrator.getUsername() %>" readonly="readonly" class="adm_general_input"/>
    </p>
    <p><span>New Password (* 6~20 chars, leave it blank if no password change):</span><br/>
    	<input type="password" name="password" value="" class="adm_general_input"/></p>
    <p><span>Confirm New Password (* 6~20 chars):</span><br/>
    	<input type="password" name="password2" value="" class="adm_general_input"/></p>   
    <p><span>Email (* 5~50 chars):</span><br/>
    	<input type="text" name="email" value="<%=administrator.getEmail() %>" class="adm_general_input"/></p>    
    <p><span>Full Name (0~30 chars):</span><br/>
    	<input type="text" name="fullname" value="<%=administrator.getFullName() %>" class="adm_general_input"/></p>	
    <p><span>Phone (0~15 chars):</span><br/>
    	<input type="text" name="phonenumber" value="<%=administrator.getPhoneNumber() %>" class="adm_general_input"/></p>
    <p><span>Description (200 chars - Maximum):</span>
    	<textarea name="adm_des" class="adm_des"><%=administrator.getAdminDes() %></textarea></p>
    <p><span>Current Password (* 6~20 chars):</span><br/>
    	<input type="password" name="current_password" value="" class="adm_general_input"/></p>  
    </p>
    <p class="form_button">
        <input type="reset" value="Reset"/>
        <input type="submit" value="Submit"/>
    </p>
</form>