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
import com.cisco.collabhelp.helpers.FileHandleHelper;
import com.cisco.collabhelp.helpers.Utility;

/**
 * Project Name: WebexDocsWeb
 * Title: DeleteArticleServlet.java
 * Description: delete article servlet
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 30 May 2018
 * @version 1.0
 */
public class DeleteArticleServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to delete an article. Please do it by clicking the 'Search/Modify an article' -> 'Delete'";
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(403);
		PrintWriter out = resp.getWriter();
		out.println(errorMessage);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String articleId = req.getParameter("id");
		String deleteResult = "";		
		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		Article article = dao.getArticleById(Integer.parseInt(articleId), false, 2);
		
		String deleteArticeInDB = dao.deleteArticle(Integer.parseInt(articleId));
		
		if("DeletedInDB".equals(deleteArticeInDB)) {
			//delete article page.
			String pathOfArticlePage = req.getSession().getServletContext().getRealPath("/") + "articles/" + articleId + ".html";
			String deleteArticlePagesResult = HTMLPagesGeneratorHelper.destoryHTMLPages(pathOfArticlePage);
			
			//destroy Hot Article for home page main part
			String deleteHotArticleForMainPartResult = "";
			if("Hot Article".equals(article.getCategory())) {
				String pathOfHotArticleForMainPart = req.getSession().getServletContext().getRealPath("/") + "articles/" + articleId + "-forMainPart.html";
				deleteHotArticleForMainPartResult = HTMLPagesGeneratorHelper.destoryHTMLPages(pathOfHotArticleForMainPart);
			}
		
			// re-generate home index.html page
			String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
			String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
			String refreshHomePageResult = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);
			
			if("Hot Article".equals(article.getCategory())) {
				if(("Destroyed".equals(deleteArticlePagesResult)) && 
				   ("Destroyed".equals(deleteHotArticleForMainPartResult)) && 
				   ("FrontHomePageReGenerated".equals(refreshHomePageResult))) {
					deleteResult = "All Done";
				}else {
					deleteResult = deleteArticeInDB + "|" + deleteArticlePagesResult + "|" + deleteHotArticleForMainPartResult + "|" + refreshHomePageResult;
				}					
			} else {
				if(("Destroyed".equals(deleteArticlePagesResult)) &&
				   ("FrontHomePageReGenerated".equals(refreshHomePageResult))) {
					deleteResult = "All Done";
				}else {
					deleteResult = deleteArticeInDB + "|" + deleteArticlePagesResult + "|" + refreshHomePageResult;
				}
			}
			
			if(article.getImagenames() != null) {		
				String[] imageNames = article.getImagenames().split(Utility.IMAGEDELIMITER);				
				// delete images in DB and article.			
				String imageLocation = req.getSession().getServletContext().getRealPath("/") + "articles/images/";
				String deleteImageInDBResult = "";
				String deleteImageInDisk = "";
				for(int i=0; i<imageNames.length; i++) {
					deleteImageInDBResult = dao.deleteImageByImageName(imageNames[i]);
					deleteImageInDisk = FileHandleHelper.removeFile(imageLocation + imageNames[i]);
					if("imageDeletedInDB".equals(deleteImageInDBResult) && "fileDeletedInDisk".equals(deleteImageInDisk)) {
						// System.out.println("Image: " + imageNames[i] + " is deleted successfully!");
					}else {
						// System.out.println("Delete Image Error: " + deleteImageInDBResult + ". " + deleteImageInDisk);
					}
				}			
			}
		}else {
			deleteResult = "Delete the artile in DB failed: " + deleteArticeInDB;
		}
		
		resp.setStatus(200);
		resp.setContentType("text/html;charset=UTF-8");		
		PrintWriter out = resp.getWriter();
		out.println(deleteResult);
	}

	
}
