package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Project Name: WebexDocsWeb
 * Title: AdminLogoutServlet.java
 * Description:  Log out servlet
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 22 May 2018
 * @version 1.0
 */
public class AdminLogoutServlet extends HttpServlet {
  
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession();		
		if(session != null) {
			// session.invalidate();
			session.removeAttribute("username");	
		}
		String logoutMessage = "You have logged out successfully. Good Bye!";
		req.setAttribute("promptMessage", logoutMessage);
		req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);
	}
  
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		doGet(req, resp);
	}
}