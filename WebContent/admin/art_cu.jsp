<%--   
date: 11 Jun 2018  
auth: Adam Kong
description: create and update articles.
--%>
<%@ include file="isLoggedIn.jsp"%>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Category" %>
<%@page import="com.cisco.collabhelp.beans.Article" %>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<header>
<% 
int articleId = -1;
ApplicationDao dao = ApplicationDao.getApplicationDao();
Article article;
String articleImageNames = "";
String articleSubject = "";
String articleAuthor = "";
String articleCategory = "";
String articleTags = "";
String articleContent = "";
if (request.getParameter("articleId") != null) {
	articleId = Integer.parseInt(request.getParameter("articleId"));
	article = dao.getArticleById(articleId, true, 1);
	articleImageNames = article.getImagenames();
	articleSubject = article.getSubject();
	articleAuthor = article.getAuthor();
	articleCategory = article.getCategory();
	articleTags = article.getTags();
	articleContent = article.getContent();	
	out.println("Update an article");
} else {
	out.println("Create an article");
}
%>
</header>
<%
if(articleId < 0) {
	out.println("<form action=\"createArticle\" method=\"post\" class=\"art_form\">");
} else {
	out.println("<form action=\"updateArticle\" method=\"post\" class=\"art_form\">");
	out.println("<input type=\"hidden\" name=\"articleId\" value=\"" + articleId + "\" />");
	out.println("<input type=\"hidden\" name=\"oldImageNames\" value=\"" + articleImageNames + "\"/>");
}
%>
	<input type="hidden" name="imageNames" class="imageNames" value=""/>
    <p><span>Subject:</span><input type="text" name="art_subject" value="<%=articleSubject %>" class="art_subject"/> (* 5~100 chars)</p>
    <p><span>Author:</span><input type="text" name="author_name" value="<%=articleAuthor %>" class="author_name"/> (* 5~25 chars)</p>
    <p>
        <span>Category:</span>
        <select name="category">      
        	<%
        	List<Category> categories = new ArrayList<Category>();
        	categories = dao.listCategories();
    		Category category = null;
    		String categoryName = "";
        	for(int i=0; i<categories.size(); i++) {
        		category = categories.get(i);
        		categoryName = category.getCategoryName();
        		if(categoryName.equals(articleCategory)) {
        			out.println("<option value =\"" + categoryName + "\" selected=\"selected\">" + categoryName + "</option>");
        		} else {
        			out.println("<option value =\"" + categoryName + "\">" + categoryName + "</option>");
        		}        		
        	}
        	%>       
        </select>
    </p>
    <p><span>Tags:</span><input type="text" name="art_tags" value="<%=articleTags %>" class="art_tags"/> (Optional, Separated by space)</p>
    <p><span>Content:</span><textarea name="content" class="art_content"><%=articleContent %></textarea></p>
    <p class="form_button">
        <input type="reset" value="Reset"/>
        <input type="submit" value="Submit"/>
    </p>
</form>