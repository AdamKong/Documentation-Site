<%--   
date: 25 Jul 2018  
auth: Adam Kong
description: create and update categories.
--%>
<%@ include file="isLoggedIn.jsp"%>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Category" %>
<%@page import="java.util.List" %>
<header>
<%
int categoryId = -1;
ApplicationDao dao = ApplicationDao.getApplicationDao();
Category category;

String categoryName = "";
String categoryDes = "";

if (request.getParameter("categoryId") != null) {
	categoryId = Integer.parseInt(request.getParameter("categoryId"));
	category = dao.getCategoryById(categoryId);
	categoryName = category.getCategoryName();
	categoryDes = category.getCategoryDes();
	out.println("Update a category");
} else {
	out.println("Create a category");
}
%>
</header>
<%
if(categoryId < 0) {
	out.println("<form action=\"createCategory\" method=\"post\" class=\"cat_form\">");
} else {
	out.println("<form action=\"updateCategory\" method=\"post\" class=\"cat_form\">");
	out.println("<input type=\"hidden\" name=\"categoryId\" value=\"" + categoryId + "\" />");
}
%>
    <p><span>Name:</span><input type="text" name="cat_name" value="<%=categoryName %>" class="cat_name"/> (* 5~25 chars)</p>
    <p><span>Description:</span><textarea name="cat_des" class="cat_des"><%=categoryDes %></textarea> (200 chars - Maximum)</p>
    <p class="cat_form_button">
        <input type="reset" value="Reset"/>
        <input type="submit" value="Submit"/>
    </p>
</form>