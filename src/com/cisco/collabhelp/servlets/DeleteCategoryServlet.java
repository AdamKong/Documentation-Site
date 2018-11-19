package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.beans.Category;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.HTMLPagesGeneratorHelper;

/**
 * Project Name: WebexDocsWeb
 * Title: DeleteCategoryServlet.java
 * Description: delete a category
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 29 Jul 2018
 * @version 1.0
 */
public class DeleteCategoryServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to delete a category. Please do it by clicking the 'Search/Modify a category' -> 'Delete'";
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(403);
		PrintWriter out = resp.getWriter();
		out.println(errorMessage);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String id = req.getParameter("id");
		int categoryId = Integer.parseInt(id);
		String deleteResult = "";		
		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		Category category = dao.getCategoryById(categoryId);
		
		List<Article> articles = new ArrayList<Article>();
		articles = dao.getAllArticlesByCategory(category.getCategoryName(), false, 2);
		if(articles.size() == 0) {
			String deleteCatInDBResult = dao.deleteCategory(categoryId);
			if("DeletedInDB".equals(deleteCatInDBResult)) {
				// re-generate the home index.html page
				String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
				String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
				String resultOfGeneratingFrontHomePage = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);			
				if("FrontHomePageReGenerated".equals(resultOfGeneratingFrontHomePage)) {
					deleteResult = "All Done";
				} else {
					deleteResult = "The category has been deleted. But the home page index.html has been not re-generated! Error message: " + resultOfGeneratingFrontHomePage;
				}			
			} else {
				deleteResult = deleteCatInDBResult;
			}
			
		} else {
			String subjects = "";
			Article article = new Article();
			for(int i=0; i< articles.size(); i++) {
				article = articles.get(i);
				subjects += article.getSubject();
				if(i != (articles.size()-1)) {
					subjects += ",\r\n";
				}
			}
			deleteResult = "There are/is still " + articles.size() + " article(s) in this category, please re-assign/delete the article(s) first then do this action. The article subject(s):\r\n \r\n" + subjects;
		}
		
		resp.setStatus(200);
		resp.setContentType("text/html;charset=UTF-8");		
		PrintWriter out = resp.getWriter();
		out.println(deleteResult);		
	}	
}
