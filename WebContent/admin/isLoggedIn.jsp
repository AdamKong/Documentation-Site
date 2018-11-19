<%--   
date: 11 May 2018  
auth: Adam Kong
description: Judge if an admin is logged in or not.
--%>
<% 
String URI = request.getRequestURI();
if (URI.endsWith("login.jsp")) {
	if (session.getAttribute("username") != null) {
		request.getRequestDispatcher("/admin/index.jsp").forward(request, response);
		return;
	}
} else if(URI.endsWith("index.jsp")){
	if(session.getAttribute("username") == null) {
		request.setAttribute("promptMessage", "");
		request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
		return;
	}
} else if(URI.endsWith("/admin/") || URI.endsWith("/admin")){
	if(session.getAttribute("username") == null) {
		request.setAttribute("promptMessage", "");
		request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
		return;
	}
} else {
 	if(session.getAttribute("username") == null) {
 		String errorMessage="Session Timed Out, or Invalid Access!";
		request.setAttribute("promptMessage", errorMessage);
		request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
		return;
	}
}
%>