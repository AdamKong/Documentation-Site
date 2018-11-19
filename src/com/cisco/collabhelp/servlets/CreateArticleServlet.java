package com.cisco.collabhelp.servlets;

import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.beans.Image;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FormValidationHelper;
import com.cisco.collabhelp.helpers.HTMLPagesGeneratorHelper;
import com.cisco.collabhelp.helpers.FileHandleHelper;
import com.cisco.collabhelp.helpers.Utility;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Project Name: WebexDocsWeb
 * Title: CreateArticleServlet.java
 * Description: create article servlet
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 20 May 2018
 * @version 1.0
 */
public class CreateArticleServlet extends HttpServlet {
   
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to submit an article. Please submit your article by clicking the 'Create an article' link.";
		req.setAttribute("error", errorMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
	}
  
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String imagemames = req.getParameter("imageNames");
		String subject = req.getParameter("art_subject");
		String author = req.getParameter("author_name");
		String category = req.getParameter("category");
		String tags = req.getParameter("art_tags");
		String content = req.getParameter("content");
    
		String validateResultMessage = FormValidationHelper.validateArticleFormData(subject, author, category, tags, content);
		if (!"Good".equals(validateResultMessage)) {
			String errorMessage = validateResultMessage + ". How do you bypass the client side validation?";
			req.setAttribute("error", errorMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
    
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		HttpSession session = req.getSession();
		String loggedInAdmin = session.getAttribute("username").toString();
    
		Article article = new Article();		
		article.setSubject(subject);
		article.setAuthor(author);
		article.setCategory(category);
		article.setTags(tags);
		article.setState(1);
		article.setContent(content);
		article.setImagenames(imagemames);
		article.setCreator(loggedInAdmin);
		
		article = dao.addArticleToDB(article);
		
		//update image data to associate the image and article
		String[] imageNames = article.getImagenames().split(Utility.IMAGEDELIMITER);
		// The image array is not actually used at this time, but save it for later use.
		Image[] images = new Image[imageNames.length];
		for(int i=0; i< imageNames.length; i++) {
			images[i] = dao.associateImageToArticleByImageName(article.getId(), imageNames[i]);
		}
		
		//remove image from disk where article id is null(-1) and username is the current logged-in admin (deprecated image files).
		List<Image> imagesToDelete = dao.getImagesByArticleIdAndUploader(-1, loggedInAdmin);
		String imageLocation = req.getSession().getServletContext().getRealPath("/") + "articles/images/";
		boolean deleteImageFromDiskResultFlag = true;
		for(int i=0; i<imagesToDelete.size(); i++) {
			String deleteImageFromDiskResult = FileHandleHelper.removeFile(imageLocation + imagesToDelete.get(i).getImageName());
			if(!"fileDeletedInDisk".equals(deleteImageFromDiskResult)) {
				deleteImageFromDiskResultFlag = false;
			}
		}		
		
		// remove image from DB where article id is null(-1) and username is the current logged-in admin (deprecated image records in DB).
		if(deleteImageFromDiskResultFlag) {
			String imageDeleteInDBResult = dao.deleteImageByArticleIdAndUploader(-1, loggedInAdmin);
			if("imageDeletedInDB".equals(imageDeleteInDBResult)) {
				// deprecate images are deleted, do nothing.
			}
		}		
		
		// inserted the article to DB, and returned the article with ID and timestamp attributes set.
		if (article.getTimestamp() != null) {
			String modelPath = req.getSession().getServletContext().getRealPath("/") + "articles/article_content.html";
			String outHTMLPath = req.getSession().getServletContext().getRealPath("/") + "articles/" + article.getId() + ".html";
			// generate the html page for one article.
			String resultOfGeneratingHTMLPageForOneArticle = HTMLPagesGeneratorHelper.generateHTMLPageForOneArticle(modelPath, outHTMLPath, article);
			if ("HTMLPageForOneArticleCreated".equals(resultOfGeneratingHTMLPageForOneArticle)) {
				// build the index.html
				String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
				String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
				String resultOfGeneratingFrontHomePage = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);		
				if("FrontHomePageReGenerated".equals(resultOfGeneratingFrontHomePage)) {
					String reGenerateFromHomePageSuccessMessage = "The article has been added to DB and rendered as a HTML page. The home page index.html has been regenerated too!";
					req.setAttribute("success", reGenerateFromHomePageSuccessMessage);
					req.getRequestDispatcher("/admin/success.jsp").forward(req, resp);
					return;
				}
				
				String failedToReGenerateFrontHomePageMessage = "The article has been added to DB and rendered as a HTML page, but the home page index.html has been not regenerated! Error message: " + resultOfGeneratingFrontHomePage;
				req.setAttribute("error", failedToReGenerateFrontHomePageMessage);
				req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
				return;				
			}
			
			String failedToGenerateHTMLPageForOneArticleMessage = "The article has been added to DB, but failed to be rendered as a HTML page. Error message: " + resultOfGeneratingHTMLPageForOneArticle;
			req.setAttribute("error", failedToGenerateHTMLPageForOneArticleMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
    
		String failedToAddArticleToDBMessage = "The article has been not added to DB, please contact admin to report the issue!";
		req.setAttribute("error", failedToAddArticleToDBMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
		return;
		}
	
	}


