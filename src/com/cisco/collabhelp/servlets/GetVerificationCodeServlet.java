package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Project Name: WebexDocsWeb
 * Title: GetVerificationCodeServlet.java
 * Description: get the verification code
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 19 Sep 2018
 * @version 1.0
 */
public class GetVerificationCodeServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid Access!";
		req.setAttribute("promptMessage", errorMessage);
		req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);		
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(200);
		PrintWriter out = resp.getWriter();
		
		String inputCode = req.getParameter("inputCode");
		HttpSession session = req.getSession();
		String verificationCode = "";
		if(session.getAttribute("verifyCode") != null) {
			verificationCode = session.getAttribute("verifyCode").toString();
		}		
		if(verificationCode.equalsIgnoreCase(inputCode)) {
			out.println("Passed");	
		}else {
			out.println("The verification code is not correct or the session expired, please try again!");	
		}
		
	}
}
