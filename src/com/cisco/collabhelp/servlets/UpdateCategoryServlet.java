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

import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.beans.Category;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FormValidationHelper;
import com.cisco.collabhelp.helpers.HTMLPagesGeneratorHelper;

/**
 * Project Name: WebexDocsWeb
 * Title: UpdateCategoryServlet.java
 * Description: update category
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 29 Jul 2018
 * @version 1.0
 */
public class UpdateCategoryServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to update a category. Please update your category by clicking the 'Search/Modify a category' link.";
		req.setAttribute("error", errorMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		int cat_Id = Integer.parseInt(req.getParameter("categoryId"));
		String cat_name = req.getParameter("cat_name");
		String cat_des = req.getParameter("cat_des");
		
		String validateResultMessage = FormValidationHelper.validateCategoryFormData(cat_name, cat_des);
		
		if (!"Good".equals(validateResultMessage)) {
			String errorMessage = validateResultMessage + ". How do you bypass the client side validation?";
			req.setAttribute("error", errorMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
		
		HttpSession session = req.getSession();
		String creator = session.getAttribute("username").toString();
		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		Category category = new Category();
		category.setId(cat_Id);
		category.setCategoryName(cat_name);
		category.setCategoryDes(cat_des);
		category.setCategoryCreator(creator);
		
		// get the old details of the category
		Category oldCategory = dao.getCategoryById(cat_Id);
		String oldCategoryName = oldCategory.getCategoryName();
		if("Hot Article".equals(oldCategoryName)) {
			String errorMessage = "The 'Hot Article' can not be modified. Please contact administrator if this is needed!";
			req.setAttribute("error", errorMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
		
		category = dao.updateCategory(category);
		if(category.getTimestamp() != null) {
			String statusOfChangeCategoryForAllArticle = "CategoryChangedForAllArticles";			
			String articleModelPath = req.getSession().getServletContext().getRealPath("/") + "articles/article_content.html";
			String outHTMLPath = "";
			List<Article> articlesInCategory = new ArrayList<Article>();
			Article article = new Article();
			articlesInCategory = dao.getAllArticlesByCategory(oldCategoryName, true, 2);
			// change the category for all of the HTML pages.
			for(int i=0; i< articlesInCategory.size(); i++) {
				article = articlesInCategory.get(i);
				// update the category name of the current article object
				article.setCategory(cat_name);
				// update the category name of the article in DB.
				String statusOfUpdateArticlesCategoryName = dao.updateCategoryNameForArticle(article.getId(), cat_name);
				if(!"CategoryUpdated".equals(statusOfUpdateArticlesCategoryName)) {
					String failedToUpdateCategoryNameForArticleInDBMessage = "Updating the category name for the article with id= " + article.getId() + " failed, please contact admin to report the issue!";
					req.setAttribute("error", failedToUpdateCategoryNameForArticleInDBMessage);
					req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
					return;
				}
				
				// if the article is on, generate the HTML page. Otherwise, do not generate one.
				if(article.getState() == 1) {
					outHTMLPath = req.getSession().getServletContext().getRealPath("/") + "articles/" + article.getId() + ".html";
					// override the html page for one article.
					String resultOfGeneratingHTMLPageForOneArticle = HTMLPagesGeneratorHelper.generateHTMLPageForOneArticle(articleModelPath, outHTMLPath, article);
					if (!"HTMLPageForOneArticleCreated".equals(resultOfGeneratingHTMLPageForOneArticle)) {
						statusOfChangeCategoryForAllArticle = "Failed to change category in HTML page for article with id=" + article.getId();
						String failedToGenerateHTMLPageForOneArticleMessage = "The category has been updated to DB, but the category of HTML page for the article with id=" + article.getId() + " is failed to be updated. Error message: " + resultOfGeneratingHTMLPageForOneArticle;
						req.setAttribute("error", failedToGenerateHTMLPageForOneArticleMessage);
						req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
						return;
					}
				}	
			}
			// change the home page
			if("CategoryChangedForAllArticles".equals(statusOfChangeCategoryForAllArticle)) {
				// re-build the index.html
				String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
				String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
				String resultOfGeneratingFrontHomePage = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);			
				if("FrontHomePageReGenerated".equals(resultOfGeneratingFrontHomePage)) {
					String reGenerateFromHomePageSuccessMessage = "The category has been changed. All of the articles' category have been updated in HTML pages. The home page index.html has been re-generated too!  Please close this page and go to the previous browser tab to update other category!";
					req.setAttribute("success", reGenerateFromHomePageSuccessMessage);
					req.getRequestDispatcher("/admin/success.jsp").forward(req, resp);
					return;
				}
				
				String failedToReGenerateFrontHomePageMessage = "The category has been changed. All of the articles' category have been updated in HTML pages. But the home page index.html has been not re-generated! Error message: " + resultOfGeneratingFrontHomePage;
				req.setAttribute("error", failedToReGenerateFrontHomePageMessage);
				req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
				return;	
			}
			
		}
		
		String failedToUpdateCategoryMessage = "The category has been not updated to DB, please contact admin to report the issue!";
		req.setAttribute("error", failedToUpdateCategoryMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
		return;
		
	}	
}