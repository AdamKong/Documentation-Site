<%--   
date: 11 Jun 2018  
auth: Adam Kong
description: for search and list articles.
--%>
<%@ include file="isLoggedIn.jsp" %>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Category" %>
<%@page import="com.cisco.collabhelp.beans.Article" %>
<%@page import="com.cisco.collabhelp.helpers.FormValidationHelper" %>
<%@page import="com.cisco.collabhelp.helpers.Utility"%>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<header>
    Searching / Modifying Articles 
</header>
<%
	String categoryName = request.getParameter("category");
	String keywords = request.getParameter("keywords");
%>
<form action="index.jsp" method="post" class="art_backend_search_form">
    <p>
        <span>Category:</span>
        <select name="category">
        	<option value ="All" selected="selected">All</option> 
        	<%			
	        	ApplicationDao dao = ApplicationDao.getApplicationDao();
	        	List<Category> categories = dao.listCategories();
	    		Category category = null;
	    		String categoryOptionName = "";
	        	for(int i=0; i<categories.size(); i++) {
	        		category = categories.get(i);
	        		categoryOptionName = category.getCategoryName();
	        		if(categoryOptionName.equals(categoryName)) {
	        			out.println("<option value =\"" + categoryOptionName + "\" selected=\"selected\">" + categoryOptionName + "</option>");
	        		} else {
	        			out.println("<option value =\"" + categoryOptionName + "\">" + categoryOptionName + "</option>");
	        		}	
	        	}
        	%>
        </select>
    </p>
	    <%
			String keywordsField = "";
			if(keywords != null) {
				keywordsField = keywords;
			}
	    %>
    <p><span>Keywords: </span><input type="text" name="keywords" value="<%=keywordsField %>" placeholder="Keywords" class="art_backend_search_keywords"/><br/>(0~30 chars, separated by space. no keywords means all articles in the category.)</p>
    <input name="requestIdentifier_bke_art" type="hidden" value="" />
    <input type="hidden" name="uri" value="art_sl.jsp" />
    <p class="form_button">
        <input type="reset" value="Reset"/>
        <input type="submit" value="Submit"/>
    </p>
</form>
<%
String requestIdentifier_bke_art = "";
List<Article> articles = new ArrayList<Article>();
boolean fromInitialPage = false;

if(request.getParameter("requestIdentifier_bke_art") != null){
	requestIdentifier_bke_art = request.getParameter("requestIdentifier_bke_art");
	if(session.getAttribute(requestIdentifier_bke_art) != null){
		// from pagination link
		articles = (List<Article>)session.getAttribute(requestIdentifier_bke_art);
	}else{
		// from searching form		
		// from validation
		if((categoryName != null) || (keywords != null)) {
			String searchFormValidateResult = FormValidationHelper.validateSearchForm(keywords, categoryName);
			if(!"Good".equals(searchFormValidateResult)) { 
				out.println("<table><tr><td><h2>Server side form validation fails. How do you bypass the client side validation? Maybe you input too many chars in keywords field? please fix it then try again!</h2></td></tr></table>");
				return;
			}
		}
		// get articles
		articles = dao.getAllArticlesByKeywordsInCategories(keywords, categoryName, false, 2);	
	}
}else{
	requestIdentifier_bke_art = Utility.getUUID32();
	fromInitialPage = true;
	// from get
	// doing nothing, just left articles as empty
}

session.setAttribute(requestIdentifier_bke_art, articles);
%>
<table>
<%	
	List<Article> articles_in_currentPage = new ArrayList<Article>();
	
	int currentPage = 1;		    
	String p = request.getParameter("currentPage");
	if(p!=null && Utility.isNumeric(p)){
		currentPage = Integer.parseInt(p);
	}
	int articlesPerPage = Utility.PAGE_SIZE;
	int totalArticles = articles.size();
	int totalPages = 1;
	if(totalArticles > 0){
		totalPages = totalArticles % articlesPerPage == 0 ? totalArticles / articlesPerPage : totalArticles / articlesPerPage + 1;
	}
	int beginIndex = (currentPage - 1) * articlesPerPage;
	int endIndex = beginIndex + articlesPerPage; 
	if (endIndex > totalArticles) endIndex = totalArticles;
	if (beginIndex > endIndex) beginIndex = endIndex;
	
	articles_in_currentPage = articles.subList(beginIndex, endIndex);	
	Article article = null;
	if(articles_in_currentPage.size() != 0){
%>
    <tr>
        <th>Subject</th>
        <th>Update</th>
        <th>Turn Off/On</th>
        <th>Delete</th>
    </tr>	   	
  	<%		
  	}
   	for(int i=0; i< articles_in_currentPage.size(); i++) {
   		article = articles_in_currentPage.get(i);	
   %>
    <tr class="article<%=article.getId()%>">
        <td>
        	<%
        		String displayedSubject ="";        	
        		int displayedSubjectLength = 100;
        		String complementString = "...";
        		String articleSubject = article.getSubject();        		
        		if(articleSubject.length() > displayedSubjectLength) {
        			displayedSubject = articleSubject.substring(0, displayedSubjectLength) + complementString;
        		}else{
        			displayedSubject = articleSubject;
        		}
        		displayedSubject = "<a href=\"../articles/" + article.getId() + ".html\" target=\"_blank\">" + displayedSubject + "</a>";       		
        		out.println(displayedSubject);
        		String articleState = "";
        		if(article.getState() == 1){
        			articleState = "Turn Off";
        		}else{
        			articleState = "Turn On";
        		}
        	%>
        </td>
        <td><a href="index.jsp?uri=art_cu.jsp&articleId=<%=article.getId() %>" target="_blank">Update</a></td>
        <td><span name="disable<%=article.getId() %>" class="disable"><%=articleState %></span></td>
        <td><span name="delete<%=article.getId() %>" class="delete">Delete</span> (<span class="permanentDel">irrevocable</span>)</td>
    </tr>
	    <%
	    }
	    %>
</table>

<nav class="backend_pagination">
	<ul>
		<%
			if(totalArticles > 0){
				out.println("<li style=\"width: 80px;margin-right:15px;\">" + currentPage + "/" + totalPages + " page</li>");
				// If it shows the "First"
				if(currentPage != 1){
					out.println("<a href=\"index.jsp?uri=art_sl.jsp&currentPage=1&requestIdentifier_bke_art=" + requestIdentifier_bke_art + "&keywords=" + keywords + "&category=" + categoryName + "\" ><li>First </li></a>");			
				}
				// If it shows the "Previous"
				if(currentPage > 1){
					out.println("<a href=\"index.jsp?uri=art_sl.jsp&currentPage=" + (currentPage-1) + "&requestIdentifier_bke_art=" + requestIdentifier_bke_art + "&keywords=" + keywords + "&category=" + categoryName + "\" ><li>Pre. </li></a>");			
				}
				
				int page_number_size = Utility.PAGE_NUMBER_SIZE;
				
				// If the number of total pages is greater than the number of page number size limit, 
				// and the current page >=page_number_size/2, show a li with dots in it.
				if(totalPages>page_number_size && currentPage>(page_number_size/2+1)){
					out.println("<li style=\"border: 0px;\">....</li>");
				}				

				// display the page numbers
 				int lowerLimit = 1;
				int upperLimit = 1;				
				
				if(totalPages<=page_number_size){
					// if the number of total pages <= the number of page number size limit
					lowerLimit = 1;
					upperLimit = totalPages;
				} else if(currentPage<=page_number_size/2){
					lowerLimit = 1;
					upperLimit = page_number_size;
				} else {
					lowerLimit = currentPage - page_number_size/2;
					upperLimit = currentPage + page_number_size/2;
				}
				if(upperLimit>totalPages){
					upperLimit = totalPages;
				}
				
				// debugging purpose
				/* System.out.println("lowerLimit:" + lowerLimit);
				System.out.println("upperLimit:" + upperLimit);	 */			
				
				for(int i=lowerLimit; i<=upperLimit; i++ ){
					if(i == currentPage){
						out.println("<a href=\"index.jsp?uri=art_sl.jsp&currentPage=" + i + "&requestIdentifier_bke_art=" + requestIdentifier_bke_art + "&keywords=" + keywords + "&category=" + categoryName + "\" ><li style=\"color:red; border:1px dashed red;\" >" + i + "</li></a>");
					}else{
						out.println("<a href=\"index.jsp?uri=art_sl.jsp&currentPage=" + i + "&requestIdentifier_bke_art=" + requestIdentifier_bke_art + "&keywords=" + keywords + "&category=" + categoryName + "\" ><li>" + i + "</li></a>");
					}
				}

				// If the number of total pages is greater than the number of page number size limit, 
				// and the current page totalPages-page_number_size/2, show a li with dots in it.
				if(totalPages>page_number_size && currentPage<totalPages-page_number_size/2){
					out.println("<li style=\"border: 0px;\">....</li>");
				}
				
				// Show the next page
				if(currentPage < totalPages){
					out.println("<a href=\"index.jsp?uri=art_sl.jsp&currentPage=" + (currentPage+1) + "&requestIdentifier_bke_art=" + requestIdentifier_bke_art + "&keywords=" + keywords + "&category=" + categoryName + "\" ><li>Next </li></a>");			
				}
				
				if(currentPage != totalPages){
					out.println("<a href=\"index.jsp?uri=art_sl.jsp&currentPage=" + totalPages + "&requestIdentifier_bke_art=" + requestIdentifier_bke_art + "&keywords=" + keywords + "&category=" + categoryName +"\" ><li>Last</li></a>");			
				}
			}else{
				if(!fromInitialPage){
					out.println("Sorry, there is no result!");
				}				
			}
		%>
	</ul>
</nav>
