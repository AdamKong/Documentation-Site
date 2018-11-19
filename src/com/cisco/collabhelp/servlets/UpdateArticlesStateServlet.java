package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.HTMLPagesGeneratorHelper;

/**
 * Project Name: WebexDocsWeb
 * Title: UpdateArticlesStateServlet.java
 * Description: update article state servlet
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 10 Jun 2018
 * @version 1.0
 */
public class UpdateArticlesStateServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to update an article state. Please change article state by clicking the 'Search/Modify an article' -> 'Turn Off/On'";
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(403);
		PrintWriter out = resp.getWriter();
		out.println(errorMessage);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String articleId = req.getParameter("id");
		String command = req.getParameter("command");
		command = command.trim();
		String changeStateResult = "";
		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		if("Turn Off".equals(command)) {
			String executeResult = "";
			//update the article state in DB.
			executeResult = dao.changeStateForOneArticle(Integer.parseInt(articleId), 0);
			if("StateUpdated".equals(executeResult)) {
				
				Article article = dao.getArticleById(Integer.parseInt(articleId), false, 0);
				
				//destory article page.
				String pathOfArticlePage = req.getSession().getServletContext().getRealPath("/") + "articles/" + articleId + ".html";
				String destroyArticlePagesResult = HTMLPagesGeneratorHelper.destoryHTMLPages(pathOfArticlePage);
				
				//destory Hot Article for home page main part
				String destroyHotArticleForMainPartResult = "";
				if("Hot Article".equals(article.getCategory())) {
					String pathOfHotArticleForMainPart = req.getSession().getServletContext().getRealPath("/") + "articles/" + articleId + "-forMainPart.html";
					destroyHotArticleForMainPartResult = HTMLPagesGeneratorHelper.destoryHTMLPages(pathOfHotArticleForMainPart);
				}
				
				// re-generate home index.html page
				String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
				String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
				String refreshHomePageResult = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);
				
				if("Hot Article".equals(article.getCategory())) {
					if(("Destroyed".equals(destroyArticlePagesResult)) && 
					   ("Destroyed".equals(destroyHotArticleForMainPartResult)) && 
					   ("FrontHomePageReGenerated".equals(refreshHomePageResult))) {
						changeStateResult = "All Done";
					}else {
						changeStateResult = executeResult + "|" + destroyArticlePagesResult + "|" + destroyHotArticleForMainPartResult + "|" + refreshHomePageResult;
					}					
				} else {
					if(("Destroyed".equals(destroyArticlePagesResult)) &&
					   ("FrontHomePageReGenerated".equals(refreshHomePageResult))) {
						changeStateResult = "All Done";
					}else {
						changeStateResult = executeResult + "|" + destroyArticlePagesResult + "|" + refreshHomePageResult;
					}
				}
				
			}else {
				changeStateResult = "Update article state in DB failed: " + executeResult;
			}
			resp.setStatus(200);
			
		} else if("Turn On".equals(command)) {
			
			String executeResult = "";
			// update state to 1
			executeResult = dao.changeStateForOneArticle(Integer.parseInt(articleId), 1);
			if("StateUpdated".equals(executeResult)) {
				Article article = dao.getArticleById(Integer.parseInt(articleId), true, 1);
				
				String modelPath = req.getSession().getServletContext().getRealPath("/") + "articles/article_content.html";
				String outHTMLPath = req.getSession().getServletContext().getRealPath("/") + "articles/" + articleId + ".html";
				// re-generate the html page for one article.
				String resultOfGeneratingHTMLPageForOneArticle = HTMLPagesGeneratorHelper.generateHTMLPageForOneArticle(modelPath, outHTMLPath, article);
				if ("HTMLPageForOneArticleCreated".equals(resultOfGeneratingHTMLPageForOneArticle)) {
					// re-build the index.html
					String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
					String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
					String resultOfGeneratingFrontHomePage = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);			
					if("FrontHomePageReGenerated".equals(resultOfGeneratingFrontHomePage)) {
						String reGenerateFromHomePageSuccessMessage = "The state of the article has been updated to 1 and the article has been rendered as a new HTML page. The home page index.html has been re-generated too!";
						changeStateResult = "All Done";
					}else {					
						String failedToReGenerateFrontHomePageMessage = "The state of the article has been updated to 1 and it has rendered the article to a new HTML page, but the home page index.html has been not re-generated! Error message: " + resultOfGeneratingFrontHomePage;
						changeStateResult = failedToReGenerateFrontHomePageMessage;	
					}
				} else {				
					String failedToGenerateHTMLPageForOneArticleMessage = "The state of the article has been updated to 1, but it failed to render the article to a new HTML page. Error message: " + resultOfGeneratingHTMLPageForOneArticle;
					changeStateResult = failedToGenerateHTMLPageForOneArticleMessage;
				}		
			} else {
				changeStateResult = "Update state in DB failed: " + executeResult;
			}
			
			resp.setStatus(200);
		} else {			
			changeStateResult = "Invalid Action!";
			resp.setStatus(403);
		}		
		
		resp.setContentType("text/html;charset=UTF-8");		
		PrintWriter out = resp.getWriter();
		out.println(changeStateResult);
	}
	
}
