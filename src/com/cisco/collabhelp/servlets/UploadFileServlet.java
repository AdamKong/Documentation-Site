package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cisco.collabhelp.beans.Image;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FileHandleHelper;

/**
 * Project Name: WebexDocsWeb
 * Title: UploadFileServlet.java
 * Description: upload file(image) servlet
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 1 Jun 2018
 * @version 1.0
 */
public class UploadFileServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to access this resource: wrong method!";
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(403);
		PrintWriter out = resp.getWriter();
		out.println(errorMessage);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		
		HttpSession session = req.getSession();
		if(session.getAttribute("username") != null) {
			String saveDirectoryPath = this.getServletContext().getRealPath("/articles/images");
			String uploadedImageName = FileHandleHelper.uploadFile(req, saveDirectoryPath);
			Image image = new Image();
			image.setImageName(uploadedImageName);
			String loggedInAdmin = session.getAttribute("username").toString();		
			image.setUploader(loggedInAdmin);
			
			ApplicationDao dao = ApplicationDao.getApplicationDao();
			
			image = dao.addImageToDB(image);
			resp.setStatus(200);
			out.println(image.getImageName());
		}else {
			String errorMessage = "Invalid way to access this resource: no access!";			
			resp.setStatus(403);			
			out.println(errorMessage);
		}
	}
	
}
