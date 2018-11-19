package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cisco.collabhelp.beans.Administrator;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FormValidationHelper;
import com.cisco.collabhelp.helpers.Utility;
/**
 * Project Name: WebexDocsWeb
 * Title: ChangeProfileServlet.java
 * Description: Change admin own's profile
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 5 Oct 2018
 * @version 1.0
 */

public class ChangeProfileServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to change account profile. Please do it by clicking the 'Account Details'-->'Change Account Profile' link.";
		req.setAttribute("error", errorMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				
		req.setCharacterEncoding("UTF-8");
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		String adminIdInString = req.getParameter("adminId");
		int adminId = 0;
		Pattern pattern = Pattern.compile("[0-9]*");
		if(pattern.matcher(adminIdInString).matches()) {
			adminId = Integer.parseInt(adminIdInString);
		}
		
		String username = req.getParameter("username");
		String password_inMD5 = req.getParameter("password");
		String confirm_password_inMD5 = req.getParameter("password2");
		String email = req.getParameter("email");
		String fullname = req.getParameter("fullname");
		String phonenumber = req.getParameter("phonenumber");
		
		HttpSession session = req.getSession();
		Administrator adminInSession = null;
		adminInSession = (Administrator)session.getAttribute("adminObj");
		String adminType = "";
		String active = "";
		if(adminInSession != null) {
			if(adminInSession.getIsSuperAdmin() == true) {
				adminType = "1";
			}else {
				adminType = "0";
			}
			if(adminInSession.getIsActive() == true) {
				active = "1";
			}else {
				active = "0";
			}
		}		
		String adm_des = req.getParameter("adm_des");
		String current_password = req.getParameter("current_password");
		
		// verify the current password parameter separately, because others can be verified in the form verification function.
		String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{32}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(current_password);
		String verify_current_pw_field_result = "Good";
		if(!m.matches()) {
			verify_current_pw_field_result = "MD5 encoded password in current password field is invalid. Maybe someone is trying to access the system maliciously!";
		}
		
		// check if the password is correct or not.
		boolean isValidUser = dao.adminLogin(username, current_password);
		
		String verifyAdminProfileResult = FormValidationHelper.validateAdministratorFormData(adminId, username, username, password_inMD5, confirm_password_inMD5, email, fullname, phonenumber, adminType, active, adm_des);
		if(isValidUser && "Good".equals(verify_current_pw_field_result) && "Good".equals(verifyAdminProfileResult)) {
			
			Administrator administrator = new Administrator();
			administrator.setId(adminId);
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
			
			administrator = dao.updateAdministratorInDB(administrator);
			if(administrator.getTimestamp() != null) {
				// Succeeded to update profile
				String updateAdminProfileToDBSuccessMessage = "Succeeded to update profile";
				req.setAttribute("success", updateAdminProfileToDBSuccessMessage);
				req.getRequestDispatcher("/admin/success.jsp").forward(req, resp);
				return;
			}
				
			String failedToUpdateAdminProfileToDBMessage = "The profile has been not updated to administrator table in DB, please contact admin to report the issue.";
			req.setAttribute("error", failedToUpdateAdminProfileToDBMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}else {
			String failedToVerifyFormData = "The data from the administrator form (changing profile) is invalid. Error Message: ";
			if(!"Good".equals(verify_current_pw_field_result)) {
				failedToVerifyFormData += (verify_current_pw_field_result + ";");
			}
			if(!isValidUser) {
				failedToVerifyFormData += "The password is not correct;";
			}
			if(!"Good".equals(verifyAdminProfileResult)) {
				failedToVerifyFormData += verifyAdminProfileResult;
			}
			req.setAttribute("error", failedToVerifyFormData);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}		
	}
}
