package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cisco.collabhelp.dao.ApplicationDao;

/**
 * Project Name: WebexDocsWeb
 * Title: UpdateAdminStateServlet.java
 * Description: Change administrator state.
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 30 Sep 2018
 * @version 1.0
 */

public class UpdateAdminStateServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to update an administrator state. Please change administrator state by clicking the 'Search/Modify a sub-admin account' -> 'Enable/Disable'";
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(403);
		PrintWriter out = resp.getWriter();
		out.println(errorMessage);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String adminId = req.getParameter("id");
		String command = req.getParameter("command");
		command = command.trim();
		String changeStateResult = "";
		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		if("Disable".equals(command) || "Enable".equals(command)) {
			String executeResult = "";
			//update the administrator state in DB.
			if("Disable".equals(command)) {
				executeResult = dao.changeStateForAdministrator(Integer.parseInt(adminId), 0);
			}else {
				executeResult = dao.changeStateForAdministrator(Integer.parseInt(adminId), 1);
			}			
			
			if("StateUpdated".equals(executeResult)) {
				changeStateResult = "Done";
			}else {
				changeStateResult = "Update administrator state in DB failed: " + executeResult;
			}
			resp.setStatus(200);
		}else {
			changeStateResult = "Invalid Action!";
			resp.setStatus(403);
		}
		
		resp.setContentType("text/html;charset=UTF-8");		
		PrintWriter out = resp.getWriter();
		out.println(changeStateResult);
	}

}
