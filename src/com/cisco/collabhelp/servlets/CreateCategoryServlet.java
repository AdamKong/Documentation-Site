package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cisco.collabhelp.beans.Category;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FormValidationHelper;
import com.cisco.collabhelp.helpers.HTMLPagesGeneratorHelper;

/**
 * Project Name: WebexDocsWeb
 * Title: CreateCategoryServlet.java
 * Description: create category
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 29 Jul 2018
 * @version 1.0
 */
public class CreateCategoryServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to submit a category. Please submit your article by clicking the 'Create a category' link.";
		req.setAttribute("error", errorMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");		 
		String cat_name = req.getParameter("cat_name");
		String cat_des = req.getParameter("cat_des");
    
		String validateResultMessage = FormValidationHelper.validateCategoryFormData(cat_name, cat_des);
		
		if (!"Good".equals(validateResultMessage)) {
			String errorMessage = validateResultMessage + ". How do you bypass the client side validation?";
			req.setAttribute("error", errorMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
		
		cat_name = cat_name.trim();
		cat_des = cat_des.trim();
		
		HttpSession session = req.getSession();
		String creator = session.getAttribute("username").toString();
		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		Category category = new Category();
		category.setCategoryName(cat_name);
		category.setCategoryDes(cat_des);
		category.setCategoryCreator(creator);
		
		// check if the new category name has already existed or not.
		List<Category> existingCategories = new ArrayList<Category>();
		Category existingCategory = new Category();
		boolean isExisted = false;
		existingCategories = dao.listCategories();
		for(int i=0; i<existingCategories.size(); i++) {
			existingCategory = existingCategories.get(i);
			if(cat_name.equals(existingCategory.getCategoryName())) {
				isExisted = true;
				break;
			}
		}
		if(isExisted) {
			String errorMessage = "The category has already existed, please use another category name!";
			req.setAttribute("error", errorMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
		
		// createa a new category.
		category = dao.createCategory(category);
		if(category.getTimestamp() != null) {			
			// build the index.html
			String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
			String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
			String resultOfGeneratingFrontHomePage = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);		
			if("FrontHomePageReGenerated".equals(resultOfGeneratingFrontHomePage)) {				
				String createCategorySuccessMessage = "The category has been created successfully. The home page index.html has been regenerated too!";
				req.setAttribute("success", createCategorySuccessMessage);
				req.getRequestDispatcher("/admin/success.jsp").forward(req, resp);
				return;
			} 

			String failedToGenerateHTMLPageForOneArticleMessage = "The category has been added to DB, but failed to build the home page. Error message: " + resultOfGeneratingFrontHomePage;
			req.setAttribute("error", failedToGenerateHTMLPageForOneArticleMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;			
		}	
		
		String failedToAddCategoryToDBMessage = "The category has been not added to DB, please contact admin to report the issue!";
		req.setAttribute("error", failedToAddCategoryToDBMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
		return;
	}

	
}
