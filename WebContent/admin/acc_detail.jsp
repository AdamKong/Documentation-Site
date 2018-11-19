<%--   
date: 5 Oct 2018  
auth: Adam Kong
description: show admin's account info.
--%>
<%@include file="isLoggedIn.jsp" %>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Administrator" %>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<header>
<%
String admin_username = session.getAttribute("username").toString();
ApplicationDao dao = ApplicationDao.getApplicationDao(); 
Administrator administrator = dao.getAdministratorByUsername(admin_username);
session.setAttribute("adminObj", administrator);
out.println("Account Details");
%>
</header>
<table class="acc_details">
	<tr>
		<td>Acc. ID</td>
		<td><%=administrator.getId() %></td>
	</tr>
	<tr>
		<td>Username</td>
		<td><%=administrator.getUsername() %></td>
	</tr>
	<tr>
		<td>Email</td>
		<td><%=administrator.getEmail() %></td>
	</tr>
	<tr>
		<td>Full Name</td>
		<td><%=administrator.getFullName() %></td>
	</tr>
	<tr>
		<td>Phone Number</td>
		<td><%=administrator.getPhoneNumber() %></td>
	</tr>
	<tr>
		<td>Is Super Admin?</td>
		<td>
			<%
				if(administrator.getIsSuperAdmin() == true){
					out.println("Yes");
				}else{
					out.println("No");
				}
			%>		
		</td>
	</tr>
	<tr>
		<td>Is Active?</td>
		<td>
			<%
					if(administrator.getIsActive() == true){
						out.println("Yes");
					}else{
						out.println("No");
					}
			%>
		</td>
	</tr>
	<tr>
		<td>Account Description</td>
		<td><%=administrator.getAdminDes() %></td>
	</tr>
	<tr>
		<td>Registration Timestamp (UTC)</td>
		<td><%=administrator.getTimestamp() %></td>
	</tr>
</table>
<p>
<label class="change_acc_info"><a href="index.jsp?uri=change_profile.jsp">Change Account Profile</a></label>
</p>
