package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cisco.collabhelp.beans.Administrator;
import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FormValidationHelper;
import com.cisco.collabhelp.helpers.HTMLPagesGeneratorHelper;
import com.cisco.collabhelp.helpers.Utility;

/**
 * Project Name: WebexDocsWeb
 * Title: UpdateAdminServlet.java
 * Description: Update an administrator
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 27 Sep 2018
 * @version 1.0
 */

public class UpdateAdminServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to update an admin. Please submit your request by clicking the 'Search/Modify a sub-admin account' link.";
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
		String username_original = req.getParameter("username_original");
		String username = req.getParameter("username");
		String password_inMD5 = req.getParameter("password");
		String confirm_password_inMD5 = req.getParameter("password2");
		String email = req.getParameter("email");
		String fullname = req.getParameter("fullname");
		String phonenumber = req.getParameter("phonenumber");
		String adminType = req.getParameter("adminType");
		String active = req.getParameter("active");
		String adm_des = req.getParameter("adm_des");
		
		String verifyResult = FormValidationHelper.validateAdministratorFormData(adminId, username_original, username, password_inMD5, confirm_password_inMD5, email, fullname, phonenumber, adminType, active, adm_des);
		if("Good".equals(verifyResult)) {

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
				if(!username_original.equals(username)) {
					
					String statusOfChangingCreatorOfArticles = "CreatorChangedForAllArticles";			
					String articleModelPath = req.getSession().getServletContext().getRealPath("/") + "articles/article_content.html";
					String outHTMLPath = "";
					List<Article> articlesOfOneCreator = new ArrayList<Article>();
					Article article = new Article();
					articlesOfOneCreator = dao.getAllArticlesByCreator(username_original, true, 2);					
					
					// change the creator for all of the HTML pages .
					for(int i=0; i< articlesOfOneCreator.size(); i++) {
						article = articlesOfOneCreator.get(i);
						// update the articleCreator of the current article object
						article.setCreator(username);
						// update the articleCreator of the article in DB.
						String statusOfUpdatingArticleCreator = dao.updateCreatorForArticle(article.getId(), username);
						if(!"CreatorUpdated".equals(statusOfUpdatingArticleCreator)) {
							String failedToUpdateCreatorForArticleInDBMessage = "The administrator's username has been updated to administrator talble in DB. Updating the creator for the article with id= " + article.getId() + " in article table in DB failed, please contact admin to report the issue!";
							req.setAttribute("error", failedToUpdateCreatorForArticleInDBMessage);
							req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
							return;
						}					
						// if the article is on, re-generate the HTML page. Otherwise, do not generate one.
						if(article.getState() == 1) {					
							outHTMLPath = req.getSession().getServletContext().getRealPath("/") + "articles/" + article.getId() + ".html";
							// override the html page for one article.
							String resultOfGeneratingHTMLPageForOneArticle = HTMLPagesGeneratorHelper.generateHTMLPageForOneArticle(articleModelPath, outHTMLPath, article);
							if (!"HTMLPageForOneArticleCreated".equals(resultOfGeneratingHTMLPageForOneArticle)) {
								statusOfChangingCreatorOfArticles = "Failed to update creator in HTML page for article with id=" + article.getId();
								String failedToGenerateHTMLPageForOneArticleMessage = "The administrator's username has been updated to administrator talble in DB. Updating the creator for the article with id= " + article.getId() + " in article table in DB succeeded. But the creator of HTML page for the article with id=" + article.getId() + " is failed to be updated. Error message: " + resultOfGeneratingHTMLPageForOneArticle;
								req.setAttribute("error", failedToGenerateHTMLPageForOneArticleMessage);
								req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
								return;
							}
						}
					}
					// change the home page
					if("CreatorChangedForAllArticles".equals(statusOfChangingCreatorOfArticles)) {
						// re-build the index.html
						String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
						String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
						String resultOfGeneratingFrontHomePage = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);			
						if("FrontHomePageReGenerated".equals(resultOfGeneratingFrontHomePage)) {
							String reGenerateFromHomePageSuccessMessage = "The administrator's username has been updated to administrator talble in DB. Updating the creator for the article with id= " + article.getId() + " in article table in DB succeeded. The creator of HTML page for the article with id=" + article.getId() + " succeeded to be updated. The home index.html is re-generated too.";
							req.setAttribute("success", reGenerateFromHomePageSuccessMessage);
							req.getRequestDispatcher("/admin/success.jsp").forward(req, resp);
							return;
						}
						
						String failedToReGenerateFrontHomePageMessage = "The administrator's username has been updated to administrator talble in DB. Updating the creator for the article with id= " + article.getId() + " in article table in DB succeeded. The creator of HTML page for the article with id=" + article.getId() + " succeeded to be updated. The home index.html is failed to be regenerated. Error message: " + resultOfGeneratingFrontHomePage;
						req.setAttribute("error", failedToReGenerateFrontHomePageMessage);
						req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
						return;	
					}
				}
				
				String updateAdministratorToDBSuccessMessage = "The administrator has been updated to DB! No HTML pages need to be re-generated! Please close this page and go to the previous browser tab to update other administrator if needed!";
				req.setAttribute("success", updateAdministratorToDBSuccessMessage);
				req.getRequestDispatcher("/admin/success.jsp").forward(req, resp);
				return;
			}
				
			String failedToUpdateAdministratorToDBMessage = "The administrator has been not updated to administrator table in DB, please contact admin to report the issue.";
			req.setAttribute("error", failedToUpdateAdministratorToDBMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}else {
			String failedToVerifyFormData = "The data from the administrator form (updating admin) is invalid. Error Message: " + verifyResult;
			req.setAttribute("error", failedToVerifyFormData);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}		
	}	
}
