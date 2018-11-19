<%--   
date: 1 May 2018  
auth: Adam Kong
description: list articles in search function or "more" function.
--%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Category" %>
<%@page import="com.cisco.collabhelp.beans.Article" %>
<%@page import="com.cisco.collabhelp.helpers.FormValidationHelper" %>
<%@page import="com.cisco.collabhelp.helpers.Utility"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge,chrome=1">
    <meta name="robots" content="index,follow"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta name="theme-color" content="#ffffff" />
    <meta name="keywords" content="Spark Docs Messages Space Audio Video Call Meeting">
    <meta name="description" content="This is for Spark amazing docs. Users can check the docs for their usage">
    <title>The Docs of Cisco Webex Teams, Meetings & Jabber</title>
    <link rel="shortcut icon" href="images/favicon.ico" />
    <link rel="stylesheet" href="css/style.css">
	<script src="js/jquery-3.3.1.js"></script>
    <script src="js/home.js"></script>
</head>
<body>
    <div class="header">
        <div class="header_left">
            <img src="images/ciscologo.svg" alt="Cisco Logo"/>
            <span id="colla_help">Collaboration Help</span>
            <span id="swj">Cisco Webex Teams, Meetings & Jabber</span>
        </div>

        <div class="header_middle">
            <nav>
            <ul>
            	<li><a href="index.html">Home</a></li>
                <li><a href="#">Get Started</a></li>
                <li><a href="#">Manage Account</a></li>
                <li><a href="#">Community</a></li>
                <li><a href="#">Downloads</a></li>
                <li><a href="#">Contact Us</a></li>
            </ul>
            </nav>
        </div>

        <div class="header_right">
            <img src="images/webexteams.svg" alt="Cisco Webex Teams Logo"/>
        </div>
    </div>
    
    <div class="article_searchresult_epage_main">
		<div class="list_top">
		    <%
		    String requestIdentifier = "";
		    List<Article> articles = new ArrayList<Article>();		    
		    String searchingResultPageSubject = "";
		    String failureMessage = "";
	    	ApplicationDao dao = ApplicationDao.getApplicationDao();	    
		    
    		if(request.getParameter("requestIdentifier") != null){
    			requestIdentifier = request.getParameter("requestIdentifier");
    			if(session.getAttribute(requestIdentifier) != null){
    				searchingResultPageSubject = request.getParameter("searchingResultPageSubject");
    				// pagination page				
    		    	articles = (List<Article>) session.getAttribute(requestIdentifier);  				
    		    } else {
    		    	// form, re-searching
    		    	String keywords = request.getParameter("keywords");
    		    	String categoryName = request.getParameter("categoryName");   		    	
    		    	searchingResultPageSubject = "All Articles with the keywords:\'" + keywords + "\' in category \'" + categoryName + "\'";
    		    	  		    	
    		    	String searchFormValidateResult = FormValidationHelper.validateSearchForm(keywords, categoryName);
    				if(!"Good".equals(searchFormValidateResult)) { 
    					failureMessage = " <section><header style=\"text-align: center\">Search Form Validation Failed: " + searchFormValidateResult + "</header></section>";   	    	
    				} else {
    					articles = dao.getAllArticlesByKeywordsInCategories(keywords, categoryName, true, 1);					
    				}  		    		
    		    }		
    		}else{
    			requestIdentifier = Utility.getUUID32();
    			// From the "more" button.
    			String categoryId = request.getParameter("categoryId") == null? "1" : request.getParameter("categoryId");
    			int category_id = 1;
    			Category category = new Category();
    			if(Utility.isNumeric(categoryId)){
    				category_id = Integer.parseInt(categoryId); 
    			}else{
    				failureMessage = " <section><header style=\"text-align: center\">Category Id " + categoryId + " is invalid!</header></section>";
    			}
    	
    			if(category_id > 0){
    				category = dao.getCategoryById(category_id); 
    				searchingResultPageSubject = "All Articles In \'" + category.getCategoryName() + "\' Category";
    				articles = dao.getAllArticlesByCategory(category.getCategoryName(), true, 1); 
    			}	 			
    		}		    
		    
    		session.setAttribute(requestIdentifier, articles);
			out.println(searchingResultPageSubject);			
			%>
		</div>
			<% 
			out.println(failureMessage);

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
		    
		    // debugging purpose
		    /* System.out.println("articlesPerPage: " + articlesPerPage);
		    System.out.println("currentPage: " + currentPage);
		    System.out.println("totalArticles:" + totalArticles);
		    System.out.println("totalPages: " + totalPages);
		    System.out.println("beginIndex: " + beginIndex);
		    System.out.println("endIndex: " + endIndex); */
		    
		    
		    articles_in_currentPage = articles.subList(beginIndex, endIndex);		      
		    Article article = new Article();
			for(int i=0; i < articles_in_currentPage.size(); i++) {
    			article = articles_in_currentPage.get(i);
			%>
			<section>
			    <header>
			        <a href="articles/<%=article.getId()%>.html" target="_blank"><%=article.getSubject()%></a>
			    </header>
			    <div class="first_para">
			        <%
			        int displayContentLength = 250;
			        String articleContentWithHTML = article.getContent();
			        String articleContentWithoutHTML = articleContentWithHTML.replaceAll("<[.[^>]]*>","");
			        
					int length = articleContentWithoutHTML.length();
			        if(length < displayContentLength + 1) {
			        	out.println(articleContentWithoutHTML);
			        } else {
			        	out.println(articleContentWithoutHTML.substring(0, displayContentLength));
			        }
			        %>......
			    </div>
			</section>		
		<%
    		}	    	    
		%>
		<nav>
			<ul>
		<%
			if(totalArticles > 0){
				out.println("<li style=\"width: 80px;margin-right:15px;\">" + currentPage + "/" + totalPages + " page</li>");
				// If it shows the "First"
				if(currentPage != 1){
					out.println("<a href=\"list_articles.jsp?currentPage=1&requestIdentifier=" + requestIdentifier + "&searchingResultPageSubject=" + searchingResultPageSubject + "\" ><li>First </li></a>");			
				}
				// If it shows the "Previous"
				if(currentPage > 1){
					out.println("<a href=\"list_articles.jsp?currentPage=" + (currentPage-1) + "&requestIdentifier=" + requestIdentifier + "&searchingResultPageSubject=" + searchingResultPageSubject + "\" ><li>Pre. </li></a>");			
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
						out.println("<a href=\"list_articles.jsp?currentPage=" + i + "&requestIdentifier=" + requestIdentifier + "&searchingResultPageSubject=" + searchingResultPageSubject + "\" ><li style=\"color:red; border:1px dashed red;\" >" + i + "</li></a>");
					}else{
						out.println("<a href=\"list_articles.jsp?currentPage=" + i + "&requestIdentifier=" + requestIdentifier + "&searchingResultPageSubject=" + searchingResultPageSubject + "\" ><li>" + i + "</li></a>");
					}
				}

				// If the number of total pages is greater than the number of page number size limit, 
				// and the current page totalPages-page_number_size/2, show a li with dots in it.
				if(totalPages>page_number_size && currentPage<totalPages-page_number_size/2){
					out.println("<li style=\"border: 0px;\">....</li>");
				}
				
				// Show the next page
				if(currentPage < totalPages){
					out.println("<a href=\"list_articles.jsp?currentPage=" + (currentPage+1) + "&requestIdentifier=" + requestIdentifier + "&searchingResultPageSubject=" + searchingResultPageSubject + "\" ><li>Next </li></a>");			
				}
				
				if(currentPage != totalPages){
					out.println("<a href=\"list_articles.jsp?currentPage=" + totalPages + "&requestIdentifier=" + requestIdentifier + "&searchingResultPageSubject=" + searchingResultPageSubject + "\" ><li>Last</li></a>");			
				}
			}else{
				out.println("Sorry, there is no article.");
			}
			%>	
			</ul>
		</nav>
    </div>

<div class="footer">

    <span class="contact_us">Need help finding something? - <a href="#">Contact Us</a></span>

    <nav>
        <ul>
            <li><a href="#">Terms of Service</a></li>
            <li><a href="#">Privacy Policy</a></li>
            <li><a href="#">Cookie Policy</a></li>
            <li><a href="#">Trademarks</a></li>
            <li><a href="#">Feedback</a></li>
        </ul>
    </nav>

    <span class="rights">©2018 Cisco and/or its affiliates. All rights reserved.</span>
</div>
</body>