<%--   
date: 18 Sep 2018  
auth: Adam Kong
description: create and update administrator.
--%>
<%@ include file="isLoggedIn.jsp"%>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Administrator" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Date" %>
<header>
<%
int adminId = -1;
ApplicationDao dao = ApplicationDao.getApplicationDao();
Administrator administrator;
 
String username = "";
String password = "";
String email = "";
String fullname = "";
String phonenumber = "";
boolean isSuperAdmin = false;
boolean isActive = true;
String adminDes = "";
Date timestamp = null;

if (request.getParameter("adminId") != null) {
	adminId = Integer.parseInt(request.getParameter("adminId"));
	administrator = dao.getAdministratorById(adminId);
	
	username = administrator.getUsername();
	password = administrator.getPassword();
	email = administrator.getEmail();	
	fullname = administrator.getFullName();
	phonenumber = administrator.getPhoneNumber();
	isSuperAdmin = administrator.getIsSuperAdmin();
	isActive = administrator.getIsActive();
	adminDes = administrator.getAdminDes();
	timestamp = administrator.getTimestamp();

	out.println("Update a sub-administrator");
} else {
	out.println("Create a sub-administrator");
}
%>
</header>
<%
if(adminId < 0) {
	out.println("<form action=\"createAdmin\" method=\"post\" class=\"adm_form\">");
} else {
	out.println("<form action=\"updateAdmin\" method=\"post\" class=\"adm_form\">");
	out.println("<input type=\"hidden\" name=\"username_original\" value=\"" + username + "\" />");
	out.println("<input type=\"hidden\" name=\"adminId\" value=\"" + adminId + "\" />");
}
%>
    <p><span>Username (* 6~20 chars, only letter, digit, underscore allowed):</span><br/>
    	<input type="text" name="username" value="<%=username %>" class="adm_general_input"/>
    </p>
    <p><span>Password (* 6~20 chars<%
    			if(adminId > 0){
    				out.println(", leave it blank if no password change");
    			}
    		%>):</span><br/>
    	<input type="password" name="password" value="" class="adm_general_input"/></p>
    <p><span>Confirm Password  (* 6~20 chars):</span><br/>
    	<input type="password" name="password2" value="" class="adm_general_input"/></p>
    <p><span>Email (* 5~50 chars):</span><br/>
    	<input type="text" name="email" value="<%=email %>" class="adm_general_input"/></p>    
    <p><span>Full Name (0~30 chars):</span><br/>
    	<input type="text" name="fullname" value="<%=fullname %>" class="adm_general_input"/></p>	
    <p><span>Phone (0~15 chars):</span><br/>
    	<input type="text" name="phonenumber" value="<%=phonenumber %>" class="adm_general_input"/></p> 
    <p><span>Is Super Admin?</span><br/>
       (<label class="caution">Caution:</label> if you create a super admin account or change an account to a super admin account, it will have the same privileges as your super admin account and you can NOT operate it unless you contact the dev team.)</br>
      <% 
    	if(isSuperAdmin == true){
      %>
      		<input name="adminType" type="radio" value='1' checked/> Super Admin <br/> <input name="adminType" type="radio" value="0"/> Sub Admin 
      <%
    	}else{
      %>	
      		<input name="adminType" type="radio" value='1'/> Super Admin <br/> <input name="adminType" type="radio" value="0" checked/> Sub Admin
      <%  
    	}
      %>   	
    </p>
    <p><span>Active when created/updated?</span><br/>
      <% 
    	if(isActive == true){
      %>
      		<input name="active" type="radio" value='1' checked/> Active <br/> <input name="active" type="radio" value="0"/> Inactive 
      <%
    	}else{
      %>	
      		<input name="active" type="radio" value='1'/> Active <br/> <input name="active" type="radio" value="0" checked/> Inactive
      <%  
    	}
      %>   	
    </p>
    <p><span>Description (200 chars - Maximum):</span>
    	<textarea name="adm_des" class="adm_des"><%=adminDes %></textarea></p>
    <p class="form_button">
        <input type="reset" value="Reset"/>
        <input type="submit" value="Submit"/>
    </p>
</form>