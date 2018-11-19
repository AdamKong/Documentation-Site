package com.cisco.collabhelp.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cisco.collabhelp.beans.Administrator;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FormValidationHelper;
import com.cisco.collabhelp.helpers.Utility;

/**
 * Project Name: WebexDocsWeb
 * Title: CreateAdminServlet.java
 * Description: Create an administrator
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 27 Sep 2018
 * @version 1.0
 */

public class CreateAdminServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to create an admin. Please submit your request by clicking the 'Create a sub-admin account' link.";
		req.setAttribute("error", errorMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		String username = req.getParameter("username");
		String password_inMD5 = req.getParameter("password");
		String confirm_password_inMD5 = req.getParameter("password2");
		String email = req.getParameter("email");
		String fullname = req.getParameter("fullname");
		String phonenumber = req.getParameter("phonenumber");
		String adminType = req.getParameter("adminType");
		String active = req.getParameter("active");
		String adm_des = req.getParameter("adm_des");

		String verifyResult = FormValidationHelper.validateAdministratorFormData(-1, "", username, password_inMD5, confirm_password_inMD5, email, fullname, phonenumber, adminType, active, adm_des);
		if("Good".equals(verifyResult)) {
			Administrator administrator = new Administrator();
			administrator.setUsername(username);
			administrator.setPassword(password_inMD5);
			administrator.setEmail(email);
			administrator.setFullName(fullname);
			administrator.setPhoneNumber(phonenumber);
			if("1".equals(adminType)) {
				administrator.setIsSuperAdmin(true);
			}else {
				administrator.setIsSuperAdmin(false);
			}
			if("1".equals(active)) {
				administrator.setIsActive(true);
			}else {
				administrator.setIsActive(false);
			}
			administrator.setAdminDes(adm_des);
			
			administrator = dao.addAdministratorToDB(administrator);
			if(administrator.getTimestamp() != null) {				
				String addAdministratorToDBSuccessMessage = "The administrator has been added to DB!";
				req.setAttribute("success", addAdministratorToDBSuccessMessage);
				req.getRequestDispatcher("/admin/success.jsp").forward(req, resp);
				return;
			}
				
			String failedToAddAdministratorToDBMessage = "The administrator has been not added to DB, please contact admin to report the issue.";
			req.setAttribute("error", failedToAddAdministratorToDBMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
			
		}else {
			String failedToVerifyFormData = "The data from the administrator form (creating admin) is invalid. Error Message: " + verifyResult;
			req.setAttribute("error", failedToVerifyFormData);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
	
	}
	
}
