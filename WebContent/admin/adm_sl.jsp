<%--   
date: 18 Sep 2018  
auth: Adam Kong
description: the page for administrator searching/updating/enable~disable/deletion
--%>
<%@include file="isLoggedIn.jsp" %>
<%@page import="com.cisco.collabhelp.dao.ApplicationDao" %>
<%@page import="com.cisco.collabhelp.beans.Administrator" %>
<%@page import="com.cisco.collabhelp.helpers.Utility"%>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<header>
    Searching / Modifying Administrator 
</header>
<% 
	String keywords = request.getParameter("keywords");
	String keywordsField = "";
	if(keywords != null){
		keywordsField = keywords;
	}
%>
<form action="index.jsp" method="post" class="adm_search_form">
    <p><span>Keywords: </span><input type="text" name="keywords" value="<%=keywordsField %>" placeholder="Keywords" class="adm_search_keywords"/><br/>
    (0~30 chars, separated by space. no keywords means all administrators.)</p>
    <input name="requestIdentifier_bke_adm" type="hidden" value="" />
    <input type="hidden" name="uri" value="adm_sl.jsp" />
    <p class="form_button">
        <input type="reset" value="Reset"/>
        <input type="submit" value="Submit"/>
    </p>
</form>

<%
ApplicationDao dao = ApplicationDao.getApplicationDao();
String requestIdentifier_bke_adm = "";
List<Administrator> administrators = new ArrayList<Administrator>();
boolean fromInitialPage = false;

if(request.getParameter("requestIdentifier_bke_adm") != null){
	requestIdentifier_bke_adm = request.getParameter("requestIdentifier_bke_adm");
	if(session.getAttribute(requestIdentifier_bke_adm) != null){
		// from pagination link
		administrators = (List<Administrator>)session.getAttribute(requestIdentifier_bke_adm);
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
			// get administrators
		administrators = dao.getAdministratorsByKeywords(keywords);
	}
}else{
	requestIdentifier_bke_adm = Utility.getUUID32();
	fromInitialPage = true;
	// from get
	// doing nothing, just left administrators as empty
}

session.setAttribute(requestIdentifier_bke_adm, administrators);
%>

<table class="adminTable">
	<%	
		List<Administrator> administrators_in_currentPage = new ArrayList<Administrator>();
		
		int currentPage = 1;		    
		String p = request.getParameter("currentPage");
		if(p!=null && Utility.isNumeric(p)){
			currentPage = Integer.parseInt(p);
		}
		int administratorPerPage = Utility.PAGE_SIZE;
		int totalAdministrators = administrators.size();
		int totalPages = 1;
		if(totalAdministrators > 0){
			totalPages = totalAdministrators % administratorPerPage == 0 ? totalAdministrators / administratorPerPage : totalAdministrators / administratorPerPage + 1;
		}
		int beginIndex = (currentPage - 1) * administratorPerPage;
		int endIndex = beginIndex + administratorPerPage; 
		if (endIndex > totalAdministrators) endIndex = totalAdministrators;
		if (beginIndex > endIndex) beginIndex = endIndex;
		
		administrators_in_currentPage = administrators.subList(beginIndex, endIndex);	
		Administrator administrator = null;
		if(administrators_in_currentPage.size() != 0){
	%>
    <tr>
        <th>Administrator</th>
        <th>Type</th>
        <th>Update</th>
        <th>Enable/Disable</th>
        <th>Delete</th>
    </tr>	   	
   	<%		
   	}
   	for(int i=0; i< administrators_in_currentPage.size(); i++) {
   		administrator = administrators_in_currentPage.get(i);
   		if(administrator.getIsSuperAdmin() == false) {
    %>
	    <tr class="administrator<%=administrator.getId()%>">
	        <td><%=administrator.getUsername()%></td>
	        <td><span class="suberadmin">Sub Admin</span></td>
	        <td><a href="index.jsp?uri=adm_cu.jsp&adminId=<%=administrator.getId() %>" target="_blank">Update</a></td>    
	        <td><span name="disableAdm<%=administrator.getId() %>" class="disableAdm"><%
		        if(administrator.getIsActive() == true){
					out.println("Disable");
				}else{
					out.println("Enable");
				}
			%></span>
			</td>
	        <td><span name="deleteAdm<%=administrator.getId() %>" class="deleteAdm">Delete</span> (<span class="permanentDel">irrevocable</span>)</td>
	    </tr>
    <%    
   		}else{
   	%>		
   		<tr class="administrator<%=administrator.getId()%>">
   			<td><%=administrator.getUsername()%></td>
   			<td><span class="superadmin">Super Admin</span></td>
   			<td>X</td>
   			<td>X</td>
   			<td>X</td>
   		</tr>
   	<%		
   		}
    }
    %>
</table>

<nav class="backend_pagination">
	<ul>
			<%
			if(totalAdministrators > 0){
				out.println("<li style=\"width: 80px;margin-right:15px;\">" + currentPage + "/" + totalPages + " page</li>");
				// If it shows the "First"
				if(currentPage != 1){
					out.println("<a href=\"index.jsp?uri=adm_sl.jsp&currentPage=1&requestIdentifier_bke_adm=" + requestIdentifier_bke_adm + "&keywords=" + keywords + "\" ><li>First </li></a>");			
				}
				// If it shows the "Previous"
				if(currentPage > 1){
					out.println("<a href=\"index.jsp?uri=adm_sl.jsp&currentPage=" + (currentPage-1) + "&requestIdentifier_bke_adm=" + requestIdentifier_bke_adm + "&keywords=" + keywords + "\" ><li>Pre. </li></a>");			
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
						out.println("<a href=\"index.jsp?uri=adm_sl.jsp&currentPage=" + i + "&requestIdentifier_bke_adm=" + requestIdentifier_bke_adm + "&keywords=" + keywords + "\" ><li style=\"color:red; border:1px dashed red;\" >" + i + "</li></a>");
					}else{
						out.println("<a href=\"index.jsp?uri=adm_sl.jsp&currentPage=" + i + "&requestIdentifier_bke_adm=" + requestIdentifier_bke_adm + "&keywords=" + keywords + "\" ><li>" + i + "</li></a>");
					}
				}

				// If the number of total pages is greater than the number of page number size limit, 
				// and the current page totalPages-page_number_size/2, show a li with dots in it.
				if(totalPages>page_number_size && currentPage<totalPages-page_number_size/2){
					out.println("<li style=\"border: 0px;\">....</li>");
				}
				
				// Show the next page
				if(currentPage < totalPages){
					out.println("<a href=\"index.jsp?uri=adm_sl.jsp&currentPage=" + (currentPage+1) + "&requestIdentifier_bke_adm=" + requestIdentifier_bke_adm + "&keywords=" + keywords + "\" ><li>Next </li></a>");			
				}
				
				if(currentPage != totalPages){
					out.println("<a href=\"index.jsp?uri=adm_sl.jsp&currentPage=" + totalPages + "&requestIdentifier_bke_adm=" + requestIdentifier_bke_adm + "&keywords=" + keywords +"\" ><li>Last</li></a>");			
				}
			}else{
				if(!fromInitialPage){
					out.println("Sorry, there is no result!");
				}				
			}
		%>
	</ul>
</nav>