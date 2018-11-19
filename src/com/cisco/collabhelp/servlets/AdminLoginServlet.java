package com.cisco.collabhelp.servlets;

import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FormValidationHelper;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Project Name: WebexDocsWeb
 * Title: AdminLoginServlet.java
 * Description: admin log in servlet.
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 21 May 2018
 * @version 1.0
 */
public class AdminLoginServlet extends HttpServlet {
 
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid Access!";
		req.setAttribute("promptMessage", errorMessage);
		req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);
	}
  
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String username = req.getParameter("username");
		String password = req.getParameter("password");

		// Server side login form validation
//		String formValidateResult = FormValidationHelper.validateLoginFormData(username, password);
//		if(!"Good".equals(formValidateResult)) {
//			String errorMessage = "Login form server side validate failure:" + formValidateResult;
//			req.setAttribute("promptMessage", errorMessage);
//			req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);
//			return;
//		}
		
		// username and password DB check.	
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		boolean isValidUser = dao.adminLogin(username, password);   
		if (isValidUser) {			
			// Check if the username has logged in (i.e. by someone else in a different location).
			ArrayList<String> userArray = new ArrayList<String>();
			// If there is a logged-in user list, retrieve out the array.
			ServletContext application = this.getServletContext();
			if(application.getAttribute("loggedInUserList") != null) {
				userArray = (ArrayList<String>) application.getAttribute("loggedInUserList");
			}

			if(userArray.contains(username)) {
				String errorMessage = "The account has been used. Please log out first or use a different account to log in!";
				req.setAttribute("promptMessage", errorMessage);
				req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);
				return;
			}
			
			// add the user to the logged-in user list, and re-save the list to the application
			userArray.add(username);
			application.setAttribute("loggedInUserList", userArray);
						
			// Add the user to the session.
			HttpSession session = req.getSession();      
			session.setAttribute("username", username);
      
			InetAddress ia = InetAddress.getLocalHost();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			TimeZone tz = TimeZone.getTimeZone("UTC+0");
			simpleDateFormat.setTimeZone(tz);
			Date currentTime = new Date();
			String time = simpleDateFormat.format(currentTime).toString();
      
			session.setAttribute("ia", ia);
			session.setAttribute("logintime", time);
      
			req.getRequestDispatcher("/admin/index.jsp").forward(req, resp);
		} else {
			String errorMessage = "Invalid credentials or the account is disabled!";
			req.setAttribute("promptMessage", errorMessage);
			req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);
		}
	}
}