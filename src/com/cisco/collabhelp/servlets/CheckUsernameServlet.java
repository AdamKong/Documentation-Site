package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.beans.Administrator;

/**
 * Project Name: WebexDocsWeb
 * Title: CheckUsernameServlet.java
 * Description: Check if a username has existed or not before creating a new admin.
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 25 Sep 2018
 * @version 1.0
 */

public class CheckUsernameServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid Access To This Servlet!";
		req.setAttribute("promptMessage", errorMessage);
		req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(200);
		PrintWriter out = resp.getWriter();
		String outputMessage = "";
		
		String username = req.getParameter("username");
		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		Administrator administrator = dao.getAdministratorByUsername(username);
		if(administrator.getTimestamp() != null) {
			outputMessage = "The username has been already used, please use another one!";
		} else {
			outputMessage = "Available";
		}
		
		out.println(outputMessage);
	}

	

}
