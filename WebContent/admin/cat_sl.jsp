<%--   
date: 29 Jul 2018  
auth: Adam Kong
description: for search and list categories.
--%>
<%@include file="isLoggedIn.jsp" %>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Category" %>
<%@page import="com.cisco.collabhelp.helpers.Utility"%>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<header>
    Searching / Modifying Categories 
</header>
<%
	String keywords = request.getParameter("keywords");
	String keywordsField = "";
	if(keywords != null){
		keywordsField = keywords;
	}
%>
<form action="index.jsp" method="post" class="cat_search_form">
    <p><span>Keywords: </span><input type="text" name="keywords" value="<%=keywordsField %>" placeholder="Keywords" class="cat_search_keywords"/><br/>
    (0~30 chars, separated by space. no keywords means all categories)</p>
    <input name="requestIdentifier_bke_cat" type="hidden" value="" />
    <input type="hidden" name="uri" value="cat_sl.jsp" />
    <p class="form_button">
        <input type="reset" value="Reset"/>
        <input type="submit" value="Submit"/>
    </p>
</form>

<%
ApplicationDao dao = ApplicationDao.getApplicationDao();
String requestIdentifier_bke_cat = "";
List<Category> categories = new ArrayList<Category>();
boolean fromInitialPage = false;

if(request.getParameter("requestIdentifier_bke_cat") != null){
	requestIdentifier_bke_cat = request.getParameter("requestIdentifier_bke_cat");
	if(session.getAttribute(requestIdentifier_bke_cat) != null){
		// from pagination link
		categories = (List<Category>)session.getAttribute(requestIdentifier_bke_cat);
	}else{
		// from searching form		
		// from validation
		if(keywords != null) {
			// coz there is no field to verify, we just verify in this code directly. 
			// strictly speaking, this should be put into validation form.
			keywords = keywords.trim();
			if(keywords.length() < 0 || keywords.length() > 30){
				out.println("<table><tr><td><h2>Server side form validation fails. How do you bypass the client side validation? Maybe you input too many chars in keywords field? please fix it then try again!</h2></td></tr></table>");
				return;
			}
		}	
			// get categories
		categories = dao.getCategoriesByKeywords(keywords);
	}
}else{
	requestIdentifier_bke_cat = Utility.getUUID32();
	fromInitialPage = true;
	// from get
	// doing nothing, just left categories as empty
}

session.setAttribute(requestIdentifier_bke_cat, categories);
%>
<table>
<%	
	List<Category> categories_in_currentPage = new ArrayList<Category>();	
	int currentPage = 1;		    
	String p = request.getParameter("currentPage");
	if(p!=null && Utility.isNumeric(p)){
		currentPage = Integer.parseInt(p);
	}
	int categoriesPerPage = Utility.PAGE_SIZE;
	int totalCategories = categories.size();
	int totalPages = 1;
	if(totalCategories > 0){
		totalPages = totalCategories % categoriesPerPage == 0 ? totalCategories / categoriesPerPage : totalCategories / categoriesPerPage + 1;
	}
	int beginIndex = (currentPage - 1) * categoriesPerPage;
	int endIndex = beginIndex + categoriesPerPage; 
	if (endIndex > totalCategories) endIndex = totalCategories;
	if (beginIndex > endIndex) beginIndex = endIndex;
	
	categories_in_currentPage = categories.subList(beginIndex, endIndex);	
	Category category = null;
	if(categories_in_currentPage.size() != 0){
%>
    <tr>
        <th>Category Name</th>
        <th>Update</th>
        <th>Delete</th>
    </tr>	   	
   	<%		
   	}
   	for(int i=0; i< categories_in_currentPage.size(); i++) {
   		category = categories_in_currentPage.get(i);	
    %>
    <tr class="category<%=category.getId()%>">
        <td><%=category.getCategoryName()%></td>
        <td><a href="index.jsp?uri=cat_cu.jsp&categoryId=<%=category.getId() %>" target="_blank">Update</a></td>
        <td><span name="delete<%=category.getId() %>" class="deleteCat">Delete</span> (<span class="permanentDel">irrevocable</span>)</td>
    </tr>
    <%
    }
    %>
</table>
<nav class="backend_pagination">
	<ul>
			<%
			if(totalCategories > 0){
				out.println("<li style=\"width: 80px;margin-right:15px;\">" + currentPage + "/" + totalPages + " page</li>");
				// If it shows the "First"
				if(currentPage != 1){
					out.println("<a href=\"index.jsp?uri=cat_sl.jsp&currentPage=1&requestIdentifier_bke_cat=" + requestIdentifier_bke_cat + "&keywords=" + keywords + "\" ><li>First </li></a>");			
				}
				// If it shows the "Previous"
				if(currentPage > 1){
					out.println("<a href=\"index.jsp?uri=cat_sl.jsp&currentPage=" + (currentPage-1) + "&requestIdentifier_bke_cat=" + requestIdentifier_bke_cat + "&keywords=" + keywords + "\" ><li>Pre. </li></a>");			
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
						out.println("<a href=\"index.jsp?uri=cat_sl.jsp&currentPage=" + i + "&requestIdentifier_bke_cat=" + requestIdentifier_bke_cat + "&keywords=" + keywords + "\" ><li style=\"color:red; border:1px dashed red;\" >" + i + "</li></a>");
					}else{
						out.println("<a href=\"index.jsp?uri=cat_sl.jsp&currentPage=" + i + "&requestIdentifier_bke_cat=" + requestIdentifier_bke_cat + "&keywords=" + keywords + "\" ><li>" + i + "</li></a>");
					}
				}

				// If the number of total pages is greater than the number of page number size limit, 
				// and the current page totalPages-page_number_size/2, show a li with dots in it.
				if(totalPages>page_number_size && currentPage<totalPages-page_number_size/2){
					out.println("<li style=\"border: 0px;\">....</li>");
				}
				
				// Show the next page
				if(currentPage < totalPages){
					out.println("<a href=\"index.jsp?uri=cat_sl.jsp&currentPage=" + (currentPage+1) + "&requestIdentifier_bke_cat=" + requestIdentifier_bke_cat + "&keywords=" + keywords + "\" ><li>Next </li></a>");			
				}
				
				if(currentPage != totalPages){
					out.println("<a href=\"index.jsp?uri=cat_sl.jsp&currentPage=" + totalPages + "&requestIdentifier_bke_cat=" + requestIdentifier_bke_cat + "&keywords=" + keywords +"\" ><li>Last</li></a>");			
				}
			}else{
				if(!fromInitialPage){
					out.println("Sorry, there is no result!");
				}				
			}
		%>
	</ul>
</nav>