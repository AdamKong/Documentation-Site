package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.dao.ApplicationDao;
/**
 * Project Name: WebexDocsWeb
 * Title: DeleteAdministratorStateServlet.java
 * Description: Delete an administrator
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 30 Sep 2018
 * @version 1.0
 */

public class DeleteAdministratorStateServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to delete an administrator. Please do it by clicking the 'Search/Modify a sub-admin account' -> 'Delete'";
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(403);
		PrintWriter out = resp.getWriter();
		out.println(errorMessage);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				
		req.setCharacterEncoding("UTF-8");
		String adminId = req.getParameter("id");
		String deleteResult = "";				

		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		String deleteAdminInDBResult = dao.deleteAdministratorInDB(Integer.parseInt(adminId));
		
		if("DeletedInDB".equals(deleteAdminInDBResult)) {
			deleteResult = "deleteAdminDone";
		}else {
			deleteResult = "Delete the administrator in DB failed: " + deleteAdminInDBResult;
		}
		
		resp.setStatus(200);
		resp.setContentType("text/html;charset=UTF-8");		
		PrintWriter out = resp.getWriter();
		out.println(deleteResult);		
		
	}
}
