<%--   
date: 11 May 2018  
auth: Adam Kong
description: the main page (show versions)
--%>
<%@ include file="isLoggedIn.jsp" %>
    	<header>
    		Welcome to Webex Help Docs Admin Center
    	</header>
    	<table class="web_info">
    		<tr>
    		 <td>Web Site Ver.</td>
    		 <td>
    		 	<%
    		 	ServletContext context = this.getServletContext(); 
    		 	String webversion = context.getInitParameter("webversion");
    		 	out.print(webversion);
				%>
			 </td>
    		</tr>
    		<tr>
    		 <td>Web Host Info.</td>
    		 <td>
    		 	<%=session.getAttribute("ia") %> <br/>
    		 	<%=request.getServerName() + ":" + request.getServerPort() %>
    		 </td>
    		</tr>
    		<tr>
    		 <td>Client Browser Info.</td>
    		 <td>
    		 	<%=request.getHeader("user-agent") %>
    		 </td>
    		</tr>
    		<tr>
    		 <td>Client OS Info.</td>
    		 <td><%=System.getProperty("os.name") + " " + System.getProperty("os.version") %>
    		 </td>
    		</tr>
    		<tr>
    		 <td>Client IP Address</td>
    		 <td>
    		 	<%
    		 	if (request.getHeader("x-forwarded-for") == null) {
    		 		out.println(request.getRemoteAddr());
    		    } else {
    		    	out.println(request.getHeader("x-forwarded-for"));
    		    }
    		    %>
    		 </td>
    		</tr>
    		<tr>
    		 <td>Login Time</td>
    		 <td><%=session.getAttribute("logintime") + " UTC+0"%></td>
    		</tr>  	
    	</table>