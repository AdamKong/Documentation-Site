package com.cisco.collabhelp.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.beans.Image;
import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.helpers.FormValidationHelper;
import com.cisco.collabhelp.helpers.HTMLPagesGeneratorHelper;
import com.cisco.collabhelp.helpers.FileHandleHelper;
import com.cisco.collabhelp.helpers.Utility;

/**
 * Project Name: WebexDocsWeb
 * Title: UpdateArticleServlet.java
 * Description: update article servlet
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 30 May 2018
 * @version 1.0
 */
public class UpdateArticleServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String errorMessage = "Invalid way to update an article. Please update your article by clicking the 'Search/Modify an article' link.";
		req.setAttribute("error", errorMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String oldimagenames = req.getParameter("oldImageNames");
		String imagemames = req.getParameter("imageNames");
		String id = req.getParameter("articleId");
		String subject = req.getParameter("art_subject");
		String author = req.getParameter("author_name");
		String category = req.getParameter("category");
		String tags = req.getParameter("art_tags");
		String content = req.getParameter("content");
    
		String validateResultMessage = FormValidationHelper.validateArticleFormData(subject, author, category, tags, content);
		if (!"Good".equals(validateResultMessage)) {
			String errorMessage = validateResultMessage + ". How do you bypass the client side validation when modifying the article?";
			req.setAttribute("error", errorMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
    
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		
		HttpSession session = req.getSession();
		String loggedInAdmin = session.getAttribute("username").toString();
		
		Article article = new Article();
		article.setId(Integer.parseInt(id));
		article.setImagenames(imagemames);
		article.setSubject(subject);
		article.setAuthor(author);
		article.setCategory(category);
		article.setTags(tags);
		article.setContent(content);
		// do not set the current logged-in admin as creator, as the admin is updater. so do not change the creator.
		// article.setCreator(loggedInAdmin);
		
		String[] splitOldImageNames = oldimagenames.split(Utility.IMAGEDELIMITER);
		String[] splitNewImageNames = imagemames.split(Utility.IMAGEDELIMITER);
		String imageLocation = req.getSession().getServletContext().getRealPath("/") + "articles/images/";
		
		// If one or more old images are removed by one admin, the image need to be deleted from both DB and disk.
		for(int i=0; i<splitOldImageNames.length; i++) {
			boolean inNewImages = false;
			for(int j=0; j<splitNewImageNames.length; j++) {
				if(splitOldImageNames[i].equals(splitNewImageNames[j])) {
					inNewImages = true;
					break;
				}
			}
			if(!inNewImages) {
				// if the old image is not in new image list, remove the image from DB.
				String deleteImageFromDiskResult = FileHandleHelper.removeFile(imageLocation + splitOldImageNames[i]);
				// if the old image is not in new image list, remove the image from Disk.
				String deleteImaegFromDBResult = dao.deleteImageByImageName(splitOldImageNames[i]);
				if("fileDeletedInDisk".equals(deleteImageFromDiskResult) && "imageDeletedInDB".equals(deleteImaegFromDBResult)) {
					// the old image is deleted successfully. but do nothing.
				}
			}				
		}
		
		
		// Update image data to associate the image and article.
		// The image array is not actually used at this time, but save it for later use.
		Image[] images = new Image[splitNewImageNames.length];
		for(int i=0; i< splitNewImageNames.length; i++) {
			images[i] = dao.associateImageToArticleByImageName(article.getId(), splitNewImageNames[i]);
		}
		
		//remove image from disk where article id is null(-1) and username is the current logged-in admin (deprecated image files).
		List<Image> imagesToDelete = dao.getImagesByArticleIdAndUploader(-1, loggedInAdmin); // images those the current logged-in admin uploaded.
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
				
		// update the article, including the new image names.
		article = dao.updateArticleToDB(article);
		// update the article to DB, and returned the article with timestamp and state attributes set.
		// article.getTimestamp() != null --> this is to see if the article changes have been updated to DB.
		// article.getState() == 1  --> this is to see if the article is turned-off state, then no need to generate new HTML page.
		if ((article.getTimestamp() != null) && (article.getState() == 1)) {
			String modelPath = req.getSession().getServletContext().getRealPath("/") + "articles/article_content.html";
			String outHTMLPath = req.getSession().getServletContext().getRealPath("/") + "articles/" + article.getId() + ".html";
			// override the html page for one article.
			String resultOfGeneratingHTMLPageForOneArticle = HTMLPagesGeneratorHelper.generateHTMLPageForOneArticle(modelPath, outHTMLPath, article);
			if ("HTMLPageForOneArticleCreated".equals(resultOfGeneratingHTMLPageForOneArticle)) {
				// re-build the index.html
				String frontHomeModePath = req.getSession().getServletContext().getRealPath("/") + "index-template.html";
				String frontHomePagePath = req.getSession().getServletContext().getRealPath("/") + "index.html";
				String resultOfGeneratingFrontHomePage = HTMLPagesGeneratorHelper.generateFrontHomePage(frontHomeModePath, frontHomePagePath);			
				if("FrontHomePageReGenerated".equals(resultOfGeneratingFrontHomePage)) {
					String reGenerateFromHomePageSuccessMessage = "The article has been updated to DB and rendered as a new HTML page. The home page index.html has been re-generated too! Please close this page and go to the previous browser tab to update other article!";
					req.setAttribute("success", reGenerateFromHomePageSuccessMessage);
					req.getRequestDispatcher("/admin/success.jsp").forward(req, resp);
					return;
				}
				
				String failedToReGenerateFrontHomePageMessage = "The article has been updated to DB and rendered as a new HTML page, but the home page index.html has been not re-generated! Error message: " + resultOfGeneratingFrontHomePage;
				req.setAttribute("error", failedToReGenerateFrontHomePageMessage);
				req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
				return;				
			}
			
			String failedToGenerateHTMLPageForOneArticleMessage = "The article has been updated to DB, but failed to be rendered as a new HTML page. Error message: " + resultOfGeneratingHTMLPageForOneArticle;
			req.setAttribute("error", failedToGenerateHTMLPageForOneArticleMessage);
			req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
			return;
		}
    
		String failedToAddArticleToDBMessage = "The article has been not updated to DB, please contact admin to report the issue!";
		req.setAttribute("error", failedToAddArticleToDBMessage);
		req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
		return;
		}

}
