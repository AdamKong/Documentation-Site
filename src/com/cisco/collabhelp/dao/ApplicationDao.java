package com.cisco.collabhelp.dao;

import com.cisco.collabhelp.beans.Administrator;
import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.beans.Category;
import com.cisco.collabhelp.beans.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project Name: WebexDocsWeb
 * Title: ApplicationDao.java
 * Description: DB operations
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 11 Apr 2018
 * @version 1.0
 */
public class ApplicationDao {
	
	private Connection connection = null;	
	public static ApplicationDao dao;
	private ApplicationDao() {
		this.connection = DBConnection.getConnectionToDatabase();
	}
	public static ApplicationDao getApplicationDao() {
		if(dao == null) {
			dao = new ApplicationDao();
		}
		dao.connection = DBConnection.getConnectionToDatabase();
		return dao;
	}

	// user login
	public boolean adminLogin(String username, String password) {
		
		if((username == null ) || (password == null)) {
			return false;
		}
		
		boolean isValidAdmin = false;
		PreparedStatement statement = null;
		ResultSet set = null;
		try {
			String sql = "select id from administrator where username=? and password=? and isActive=1";
			statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			statement.setString(2, password);     

			set = statement.executeQuery();
			while (set.next()) {
				isValidAdmin = true;
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(set != null) {
				try {
					set.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return isValidAdmin;
	}
 
	// add an article to DB, and set the id and time stamp to the article object and return it.
	public Article addArticleToDB(Article article) {
		if(article == null) {
			return article;
		}
		
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "insert into article(subject, author, category, timestamp, tags, state, content, imagenames, articleCreator) values (?, ?, ?, now(), ?, ?, ?, ?, ?)";
			statement = connection.prepareStatement(sql);
      
			statement.setString(1, article.getSubject());
			statement.setString(2, article.getAuthor());
			statement.setString(3, article.getCategory());
			statement.setString(4, article.getTags());
			statement.setInt(5, article.getState());
			statement.setString(6, article.getContent());
			statement.setString(7, article.getImagenames());
			statement.setString(8, article.getCreator());
			
			// System.out.println("article.getImagenames():" + article.getImagenames());
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				sql = "select * from article where id=LAST_INSERT_ID()";
				statement.close();
				statement = connection.prepareStatement(sql);
				rs = statement.executeQuery();
				while (rs.next()) {
					article.setId(rs.getInt("id"));
					article.setTimestamp(rs.getTimestamp("timestamp"));
					article.setModifytimestamp(rs.getTimestamp("modifytimestamp"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return article;
	}
	
	// add a record in image table
	public Image addImageToDB(Image image) {
		if(image == null) {
			return image;
		}
		
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "insert into image(imageName, timestamp, uploader) values (?, now(), ?)";
			statement = connection.prepareStatement(sql);    
			statement.setString(1, image.getImageName());
			statement.setString(2, image.getUploader());
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				sql = "select * from image where id=LAST_INSERT_ID()";
				statement.close();
				statement = connection.prepareStatement(sql);
				rs = statement.executeQuery();
				while (rs.next()) {
					image.setId(rs.getInt("id"));
					image.setId(rs.getInt("articleId"));
					image.setTimestamp(rs.getTimestamp("timestamp"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return image;
	}
	
	// update the article id in image table by image name
	public Image associateImageToArticleByImageName(int articleId, String imageName) {
		Image image = new Image();
		if((imageName == null) || (articleId < 0)) {
			return image;
		}
		
		image.setArticleId(articleId);
		image.setImageName(imageName);
		
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "update image set articleId=? where imageName=?";			
			statement = connection.prepareStatement(sql);
			statement.setInt(1, articleId);
			statement.setString(2, imageName);
			
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				sql = "select id, timestamp, uploader from image where imageName=?";
				statement.close();
				statement = connection.prepareStatement(sql);
				statement.setString(1, imageName);
				rs = statement.executeQuery();
				while (rs.next()) {
					image.setId(rs.getInt("id"));
					image.setTimestamp(rs.getTimestamp("timestamp"));
					image.setUploader(rs.getString("uploader"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return image;
	}
	
	// get images where articleId==-1 and uploader == adminName
	public List<Image> getImagesByArticleIdAndUploader(int articleId, String uploader) {
		
		Image image = null;
		List<Image> images = new ArrayList<Image>();
		if(uploader == null) {
			return images;
		}
		
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			String sql = "select * from image where articleId=? and uploader=?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, articleId);
			statement.setString(2, uploader);			
			rs = statement.executeQuery();
			while (rs.next()) {
				image = new Image();
				image.setId(rs.getInt("id"));
				image.setImageName(rs.getString("imageName"));
				image.setArticleId(articleId);
				image.setTimestamp(rs.getTimestamp("timestamp"));
				image.setUploader(uploader);

				images.add(image);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return images;		
	}
	
	// remove an image where articeId==-1 and uploader==adminName
	public String deleteImageByArticleIdAndUploader(int articleId, String uploader) {
		if(uploader == null) {
			return "uploader can not be null";
		}
		
		String imageDeleteResult = "";
		
		int rowsAffected = 0;
		PreparedStatement statement = null;
		try {
			String sql = "delete from image where articleId=? and uploader=?";			
			statement = connection.prepareStatement(sql);     
			statement.setInt(1, articleId);
			statement.setString(2, uploader);
			
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				imageDeleteResult = "imageDeletedInDB";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			imageDeleteResult = "Failed to delete an image in DB:" + e.toString();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return imageDeleteResult;
	}
	
	// delete an imaeg by image name
	public String deleteImageByImageName(String imageName) {
		if(imageName == null) {
			return "image name can not be null";
		}
		
		String imageDeleteResult = "";
		
		int rowsAffected = 0;
		PreparedStatement statement = null;
		try {
			String sql = "delete from image where imageName=?";			
			statement = connection.prepareStatement(sql);     
			statement.setString(1, imageName);
			
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				imageDeleteResult = "imageDeletedInDB";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			imageDeleteResult = "Failed to delete an image in DB:" + e.toString();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return imageDeleteResult;
	}
	
	// update an article to DB, and set the time stamp and state to the article object and return it. do not change article creator.
	public Article updateArticleToDB(Article article) {
		if(article == null) {
			return article;
		}
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "update article set subject=?, author=?, category=?, tags=?, content=?, modifytimestamp=now(), imagenames=? where id=?";			
			statement = connection.prepareStatement(sql);     
			statement.setString(1, article.getSubject());
			statement.setString(2, article.getAuthor());
			statement.setString(3, article.getCategory());
			statement.setString(4, article.getTags());
			statement.setString(5, article.getContent());
			statement.setString(6, article.getImagenames());		
			statement.setInt(7, article.getId());
			
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				sql = "select timestamp, state, modifytimestamp, articleCreator from article where id=" + article.getId();
				statement.close();
				statement = connection.prepareStatement(sql);
				rs = statement.executeQuery();
				while (rs.next()) {
					article.setTimestamp(rs.getTimestamp("timestamp"));
					article.setState(rs.getInt("state"));
					article.setModifytimestamp(rs.getTimestamp("modifytimestamp"));
					article.setCreator(rs.getString("articleCreator"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return article;
	}
	
	
	// create a category
	public Category createCategory(Category category) {
		if(category == null) {
			return category;
		}
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "insert into category(categoryName, categoryDes, timestamp, categoryCreator) values (?, ?, now(), ?)";
			statement = connection.prepareStatement(sql);
      
			statement.setString(1, category.getCategoryName());
			statement.setString(2, category.getCategoryDes());
			statement.setString(3, category.getCategoryCreator());
      
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				sql = "select * from category where id=LAST_INSERT_ID()";
				statement.close();
				statement = connection.prepareStatement(sql);
				rs = statement.executeQuery();
				while (rs.next()) {
					category.setId(rs.getInt("id"));
					category.setTimestamp(rs.getTimestamp("timestamp"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return category;
	}
	

	// update a category
	public Category updateCategory(Category category) {
		if(category == null) {
			return category;
		}
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "update category set categoryName=?, categoryDes=?, categoryCreator=? where id=?";
			statement = connection.prepareStatement(sql);
      
			statement.setString(1, category.getCategoryName());
			statement.setString(2, category.getCategoryDes());
			statement.setString(3, category.getCategoryCreator());
			statement.setInt(4, category.getId());
      
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				sql = "select * from category where id="+category.getId();
				statement.close();
				statement = connection.prepareStatement(sql);
				rs = statement.executeQuery();
				while (rs.next()) {
					category.setTimestamp(rs.getTimestamp("timestamp"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return category;
	}
	
	
	// update category name in article table when category name changes
	public String updateCategoryNameForArticle(int id, String newCategoryName) {
		String resultOfUpdatingCategory = "";
		if((id < 0) || (newCategoryName == null)) {
			return "id or new category name is invalid";
		}
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "update article set category=? where id=?";
			statement = connection.prepareStatement(sql);
      
			statement.setString(1, newCategoryName);
			statement.setInt(2, id);
      
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				resultOfUpdatingCategory = "CategoryUpdated";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resultOfUpdatingCategory;	
	}
	
	
	// update creator in article table when admin's username changes
	public String updateCreatorForArticle(int id, String creator) {
		String resultOfUpdatingCreator = "";
		if((id < 0) || (creator == null)) {
			return "id or new author is invalid";
		}
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "update article set articleCreator=? where id=?";
			statement = connection.prepareStatement(sql);
      
			statement.setString(1, creator);
			statement.setInt(2, id);
      
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				resultOfUpdatingCreator = "CreatorUpdated";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resultOfUpdatingCreator;	
	}
	
	// get categories by keywords
	public List<Category> getCategoriesByKeywords(String keywords){
		
		List<Category> categories = new ArrayList<Category>();
		if(keywords == null) {
			return categories;
		}
		
		keywords = keywords.trim();	
		String keyword[] = keywords.split(" "); // the length of the keyword array is at least 1.
		
		Category category = null;	
		String sql = "select * from category where (";		
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		for(int i=0; i < keyword.length; i++) {
			sql += "categoryName like ? ";
			if(i!=keyword.length-1) {
				sql  += "or ";
			}
		}
		sql += ")";

		try {			
			statement = connection.prepareStatement(sql);
			for(int i=1; i < keyword.length+1; i++) {
				statement.setString(i, "%" + keyword[i-1] + "%");
			}					
			
			rs = statement.executeQuery();
			while (rs.next()) {
				category = new Category();
				category.setId(rs.getInt("id"));
				category.setCategoryName(rs.getString("categoryName"));
				category.setCategoryDes(rs.getString("categoryDes"));
				category.setTimestamp(rs.getTimestamp("timestamp"));
				category.setCategoryCreator(rs.getString("categoryCreator"));
				categories.add(category);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		return categories;
	}
	
	
	// get the category by id
	public Category getCategoryById(int id) {

		Category category = new Category();
		if(id < 0) {
			return category;
		}
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			String sql = "";
			sql = "select * from category where id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
	
			rs = statement.executeQuery();
			while (rs.next()) {
				category = new Category();
				category.setId(rs.getInt("id"));
				category.setCategoryName(rs.getString("categoryName"));
				category.setCategoryDes(rs.getString("categoryDes"));	
				category.setTimestamp(rs.getTimestamp("timestamp"));
				category.setCategoryCreator(rs.getString("categoryCreator"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
				
		return category;
	}
	
 
	// list out all of the categories
	public List<Category> listCategories() {
		Category category = null;
		Category hotArticleCategory = null;
		String categoryName = "";
		List<Category> categories = new ArrayList<Category>();
		
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			String sql = "select * from category";
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			while (rs.next()) {
				categoryName = rs.getString("categoryName");
				if("Hot Article".equals(categoryName)) {
					hotArticleCategory = new Category();
					hotArticleCategory.setId(rs.getInt("id"));
					hotArticleCategory.setCategoryName(categoryName);
					hotArticleCategory.setCategoryDes(rs.getString("categoryDes"));
					hotArticleCategory.setTimestamp(rs.getTimestamp("timestamp"));
					hotArticleCategory.setCategoryCreator(rs.getString("categoryCreator"));
				} else {
					category = new Category();
					category.setId(rs.getInt("id"));
					category.setCategoryName(categoryName);
					category.setCategoryDes(rs.getString("categoryDes"));
					category.setTimestamp(rs.getTimestamp("timestamp"));
					category.setCategoryCreator(rs.getString("categoryCreator"));
					categories.add(category);
				}
			}
			categories.add(hotArticleCategory);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return categories;
	}
	
	
	// get the top 5 articles in a certain category
	// if category is null, then it selects in all the DB
	// needContent - do not retrieve the content if no need, to improve the performance.
	// needContent: true -> content included. needContent: false -> no content
	// state - to make sure that only display the article with state == 1
	public List<Article> getTopXArticlesInOneCategory(String category, int number, boolean needContent, int state) {
		
		Article article = null;
		List<Article> topXArticlesInOneCategory = new ArrayList<Article>();
		if((category == null) || (number < 0)) {
			return topXArticlesInOneCategory;
		}
		
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {			
			String sql = "";
			String outputFields = "id, subject, author, category, timestamp, tags, state, modifytimestamp, imagenames, articleCreator";
			if(needContent) {
				outputFields = "*";
			}
			if (category == "") {
				if(state == 2) {
					sql = "select " + outputFields + " from article order by id desc limit ?";
					statement = connection.prepareStatement(sql);
					statement.setInt(1, number);
				}else {
					sql = "select " + outputFields + " from article where state=? order by id desc limit ?";				
					statement = connection.prepareStatement(sql);
					statement.setInt(1, state);
					statement.setInt(2, number);
				}	
			} else {
				if(state == 2) {
					sql = "select " + outputFields + " from article where category=? order by id desc limit ?";
					statement = connection.prepareStatement(sql);
					statement.setString(1, category);
					statement.setInt(2,  number);
				}else {
					sql = "select " + outputFields + " from article where state=? and category=? order by id desc limit ?";
					statement = connection.prepareStatement(sql);				
					statement.setInt(1, state);
					statement.setString(2, category);
					statement.setInt(3,  number);
				}				
			}		
			rs = statement.executeQuery();
			while (rs.next()) {				
				article = new Article();
				article.setId(rs.getInt("id"));
				article.setSubject(rs.getString("subject"));
				article.setAuthor(rs.getString("author"));
				article.setCategory(rs.getString("category"));
				article.setTimestamp(rs.getTimestamp("timestamp"));
				article.setTags(rs.getString("tags"));
				article.setState(rs.getInt("state"));
				if(needContent) {
					article.setContent(rs.getString("content"));
				}
				article.setModifytimestamp(rs.getTimestamp("modifytimestamp"));
				article.setImagenames(rs.getString("imagenames"));
				article.setCreator(rs.getString("articleCreator"));
				
				topXArticlesInOneCategory.add(article);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return topXArticlesInOneCategory;
	}
	
	
	// get all the articles in one specific category
	// needContent - do not retrieve the content if no need, to improve the performance.
	// needContent: true -> content included. needContent: false -> no content
	// state - to make sure that only display the article with state == 1
	public List<Article> getAllArticlesByCategory(String category, boolean needContent, int state) {
		List<Article> articles = new ArrayList<Article>();
		if(category == null) {
			return articles;
		}
		Article article = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			String sql = "";
			String outputFields = "id, subject, author, category, timestamp, tags, state, modifytimestamp, imagenames, articleCreator";
			if(needContent) {
				outputFields = "*";
			}
			if(state == 2) {
				sql = "select " + outputFields + " from article where category=? order by id desc";
				statement = connection.prepareStatement(sql);
				statement.setString(1, category);
			}else {
				sql = "select " + outputFields + " from article where state=? and category=? order by id desc";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, state);
				statement.setString(2, category);
			}
			rs = statement.executeQuery();
			while (rs.next()) {				
				article = new Article();
				
				article.setId(rs.getInt("id"));
				article.setSubject(rs.getString("subject"));
				article.setAuthor(rs.getString("author"));
				article.setCategory(rs.getString("category"));
				article.setTimestamp(rs.getTimestamp("timestamp"));
				article.setTags(rs.getString("tags"));
				article.setState(rs.getInt("state"));
				if(needContent) {
					article.setContent(rs.getString("content"));
				}
				article.setModifytimestamp(rs.getTimestamp("modifytimestamp"));
				article.setImagenames(rs.getString("imagenames"));
				article.setCreator(rs.getString("articleCreator"));				
				
				articles.add(article);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return articles;
	}
	
	
	// get all the articles created by one specific admin
	// needContent - do not retrieve the content if no need, to improve the performance.
	// needContent: true -> content included. needContent: false -> no content
	// state - to make sure that only display the article with state == 1
	public List<Article> getAllArticlesByCreator(String creator, boolean needContent, int state) {
		List<Article> articles = new ArrayList<Article>();
		if(creator == null) {
			return articles;
		}
		Article article = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			String sql = "";
			String outputFields = "id, subject, author, category, timestamp, tags, state, modifytimestamp, imagenames";
			if(needContent) {
				outputFields = "*";
			}
			if(state == 2) {
				sql = "select " + outputFields + " from article where articleCreator=? order by id desc";
				statement = connection.prepareStatement(sql);
				statement.setString(1, creator);
			}else {
				sql = "select " + outputFields + " from article where state=? and articleCreator=? order by id desc";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, state);
				statement.setString(2, creator);
			}
			rs = statement.executeQuery();
			while (rs.next()) {				
				article = new Article();
				
				article.setId(rs.getInt("id"));
				article.setSubject(rs.getString("subject"));
				article.setAuthor(rs.getString("author"));
				article.setCategory(rs.getString("category"));
				article.setTimestamp(rs.getTimestamp("timestamp"));
				article.setTags(rs.getString("tags"));
				article.setState(rs.getInt("state"));
				if(needContent) {
					article.setContent(rs.getString("content"));
				}
				article.setModifytimestamp(rs.getTimestamp("modifytimestamp"));
				article.setImagenames(rs.getString("imagenames"));
				article.setCreator(creator);				
				
				articles.add(article);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return articles;
	}
	
	
	// get the search result according to the keywords
	// needContent - do not retrieve the content if no need, to improve the performance.
	// needContent: true -> content included. needContent: false -> no content
	// state - to make sure that only display the article with state == 1
	public List<Article> getAllArticlesByKeywordsInCategories(String keywords, String categoryName, boolean needContent, int state) {
		
		List<Article> articles = new ArrayList<Article>();		
		if((keywords == null) || (categoryName == null)) {
			return articles;
		}
		
		keywords = keywords.trim();
		categoryName = categoryName.trim();
		
		// if the keywords is empty, the length of str is 1, and it ensures there is a "where" in sql. 
		// and in this case, the like is '%%' (empty between two %) -> it does not affect the select result.
		String str[] = keywords.split(" "); 
		Article article = null;
	
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			String outputFields = "id, subject, author, category, timestamp, tags, state, modifytimestamp, imagenames, articleCreator";
			if(needContent) {
				outputFields = "*";
			}
			String sql = "select " + outputFields + " from article where (";
			for(int i=0; i < str.length; i++) {
				sql += "subject like ? ";
				if(i!=str.length-1) {
					sql  += "or ";
				}
			}
			sql += ")";			
			
			if(!"All".equals(categoryName)) {
				sql += " and category=?";
			}
			if(state == 2) {
				sql += " order by id desc";
			}else {
				sql += " and state=? order by id desc";
			}
			statement = connection.prepareStatement(sql);
			for(int i=1; i < str.length+1; i++) {
				statement.setString(i, "%" + str[i-1] + "%");
			}
			if(!"All".equals(categoryName)) {
				statement.setString(str.length+1, categoryName);
				if(state != 2) {
					statement.setInt(str.length+2, state);
				}				
			} else {
				if(state != 2) {
					statement.setInt(str.length+1, state);
				}				
			}
			rs = statement.executeQuery();
			while (rs.next()) {
				article = new Article();
				article.setId(rs.getInt("id"));
				article.setSubject(rs.getString("subject"));
				article.setAuthor(rs.getString("author"));
				article.setCategory(rs.getString("category"));
				article.setTimestamp(rs.getTimestamp("timestamp"));
				article.setTags(rs.getString("tags"));
				article.setState(rs.getInt("state"));
				if(needContent) {
					article.setContent(rs.getString("content"));
				}
				article.setModifytimestamp(rs.getTimestamp("modifytimestamp"));
				article.setImagenames(rs.getString("imagenames"));
				article.setCreator(rs.getString("articleCreator"));
				
				articles.add(article);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return articles;
	}
	
	
	// get article by id
	// state - to make sure that only display the article with state == 1
	public Article getArticleById(int id, boolean needContent, int state) {
		Article article = new Article();
		if(id < 0) {
			return article;
		}
		
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			String sql = "";
			String outputFields = "id, subject, author, category, timestamp, tags, state, modifytimestamp, imagenames, articleCreator";
			if(needContent) {
				outputFields = "*";
			}
			if(state == 2) {
				sql = "select " + outputFields + " from article where id = ?";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, id);
			}else {
				sql = "select " + outputFields + " from article where id = ? and state = ?";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, id);
				statement.setInt(2, state);
			}

			rs = statement.executeQuery();
			while (rs.next()) {
				article = new Article();
				article.setId(rs.getInt("id"));
				article.setSubject(rs.getString("subject"));
				article.setAuthor(rs.getString("author"));
				article.setCategory(rs.getString("category"));
				article.setTimestamp(rs.getTimestamp("timestamp"));
				article.setTags(rs.getString("tags"));
				article.setState(rs.getInt("state"));
				if(needContent) {
					article.setContent(rs.getString("content"));					
				}
				article.setModifytimestamp(rs.getTimestamp("modifytimestamp"));
				article.setImagenames(rs.getString("imagenames"));
				article.setCreator(rs.getString("articleCreator"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return article;
	}
	
	
	// change state of one article
	public String changeStateForOneArticle(int id, int state) {
		if(id < 0) {
			return "article id can not be less than 0";
		}
		String executeResult = "";
		int rowsAffected = 0;
		PreparedStatement statement = null;
		
		try {
			String sql = "update article set state=? where id=?";			
			statement = connection.prepareStatement(sql);     
			statement.setInt(1, state);
			statement.setInt(2, id);     
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				executeResult = "StateUpdated";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			executeResult = e.toString();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return executeResult;
	}
	
	
	// delete an article by Id.
	public String deleteArticle(int id) {
		if(id < 0) {
			return "article id can not be less than 0";
		}
		String deleteResult = "";
		int rowsAffected = 0;
		PreparedStatement statement = null;
		
		try {
			String sql = "delete from article where id=?";			
			statement = connection.prepareStatement(sql);     
			statement.setInt(1, id);
			
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				deleteResult = "DeletedInDB";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			deleteResult = "Failed to delete an article:" + e.toString();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return deleteResult;
	}
	
	
	// delete a category by Id.
	public String deleteCategory(int id) {
		if(id < 0) {
			return "category id can not be less than 0";
		}
		String deleteResult = "";
		int rowsAffected = 0;
		PreparedStatement statement = null;
		
		try {
			String sql = "delete from category where id=?";			
			statement = connection.prepareStatement(sql);     
			statement.setInt(1, id);
			
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				deleteResult = "DeletedInDB";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			deleteResult = "Failed to delete a category:" + e.toString();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return deleteResult;
	}
	
	
	// get the administrator by id
	public Administrator getAdministratorById(int id) {

		Administrator administrator = new Administrator();
		if(id < 0) {
			return administrator;
		}
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			String sql = "";
			sql = "select * from administrator where id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
	
			rs = statement.executeQuery();
			while (rs.next()) {
				administrator = new Administrator();
				administrator.setId(id);
				administrator.setUsername(rs.getString("username"));
				administrator.setPassword(rs.getString("password"));
				administrator.setEmail(rs.getString("email"));				
				administrator.setFullName(rs.getString("fullName"));
				administrator.setPhoneNumber(rs.getString("phoneNumber"));
				administrator.setIsSuperAdmin(rs.getBoolean("isSuperAdmin"));
				administrator.setIsActive(rs.getBoolean("isActive"));
				administrator.setAdminDes(rs.getString("adminDes"));
				administrator.setTimestamp(rs.getTimestamp("timestamp"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
				
		return administrator;
	}
	
	
	// get the administrator by username
	public Administrator getAdministratorByUsername(String username) {

		Administrator administrator = new Administrator();
		if(username == null) {
			return administrator;
		}
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			String sql = "";
			sql = "select * from administrator where username = ?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, username);
	
			rs = statement.executeQuery();
			while (rs.next()) {
				administrator = new Administrator();
				administrator.setId(rs.getInt("id"));
				administrator.setUsername(username);
				administrator.setPassword(rs.getString("password"));
				administrator.setEmail(rs.getString("email"));				
				administrator.setFullName(rs.getString("fullName"));
				administrator.setPhoneNumber(rs.getString("phoneNumber"));
				administrator.setIsSuperAdmin(rs.getBoolean("isSuperAdmin"));
				administrator.setIsActive(rs.getBoolean("isActive"));
				administrator.setAdminDes(rs.getString("adminDes"));
				administrator.setTimestamp(rs.getTimestamp("timestamp"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
				
		return administrator;
	}

	
	// Get administrators by keywords
	public List<Administrator> getAdministratorsByKeywords(String keywords){
		
		List<Administrator> administrators = new ArrayList<Administrator>();
		if(keywords == null) {
			return administrators;
		}
		
		keywords = keywords.trim();	
		String keyword[] = keywords.split(" "); // the length of the keyword array is at least 1.
		
		Administrator administrator = null;	
		String sql = "select * from administrator where (";		
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		for(int i=0; i < keyword.length; i++) {
			sql += "username like ? ";
			if(i!=keyword.length-1) {
				sql  += "or ";
			}
		}
		sql += ")";

		try {			
			statement = connection.prepareStatement(sql);
			for(int i=1; i < keyword.length+1; i++) {
				statement.setString(i, "%" + keyword[i-1] + "%");
			}					
			
			rs = statement.executeQuery();
			while (rs.next()) {
				administrator = new Administrator();

				administrator.setId(rs.getInt("id"));
				administrator.setUsername(rs.getString("username"));
				administrator.setEmail(rs.getString("email"));
				administrator.setFullName(rs.getString("fullName"));
				administrator.setPhoneNumber(rs.getString("phoneNumber"));
				if("1".equals(rs.getString("isSuperAdmin"))) {
					administrator.setIsSuperAdmin(true);
				}else {
					administrator.setIsSuperAdmin(false);
				}
				if("1".equals(rs.getString("isActive"))) {
					administrator.setIsActive(true);
				}else {
					administrator.setIsActive(false);
				}
				administrator.setAdminDes("adminDes");
				administrator.setTimestamp(rs.getTimestamp("timestamp"));
				
				administrators.add(administrator);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		return administrators;
	}
	
		
	// Add administrator into DB
	public Administrator addAdministratorToDB(Administrator administrator) {
		if(administrator == null) {
			return null;
		}
		
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "insert into administrator(username, password, email, fullName, phoneNumber, isSuperAdmin, isActive, adminDes, timestamp) values (?, ?, ?, ?, ?, ?, ?, ?, now())";
			statement = connection.prepareStatement(sql);
      
			statement.setString(1, administrator.getUsername());
			statement.setString(2, administrator.getPassword());
			statement.setString(3, administrator.getEmail());
			statement.setString(4, administrator.getFullName());
			statement.setString(5, administrator.getPhoneNumber());
			statement.setBoolean(6, administrator.getIsSuperAdmin());
			statement.setBoolean(7, administrator.getIsActive());
			statement.setString(8, administrator.getAdminDes());
			
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				sql = "select * from administrator where id=LAST_INSERT_ID()";
				statement.close();
				statement = connection.prepareStatement(sql);
				rs = statement.executeQuery();
				while (rs.next()) {
					administrator.setId(rs.getInt("id"));
					administrator.setTimestamp(rs.getTimestamp("timestamp"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return administrator;
	}
	
	
	// update a category
	public Administrator updateAdministratorInDB(Administrator administrator) {
		if(administrator == null) {
			return administrator;
		}
		int rowsAffected = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "";
			if(administrator.getPassword()==null || "".equals(administrator.getPassword())) {
				sql = "update administrator set username=?, email=?, fullName=?, phoneNumber=?, isSuperAdmin=?, isActive=?, adminDes=? where id=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, administrator.getUsername());
				statement.setString(2, administrator.getEmail());			
				statement.setString(3, administrator.getFullName());
				statement.setString(4, administrator.getPhoneNumber());
				statement.setBoolean(5, administrator.getIsSuperAdmin());
				statement.setBoolean(6, administrator.getIsActive());			
				statement.setString(7, administrator.getAdminDes());
				statement.setInt(8, administrator.getId());
			}else {
				sql = "update administrator set username=?, password=?, email=?, fullName=?, phoneNumber=?, isSuperAdmin=?, isActive=?, adminDes=? where id=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, administrator.getUsername());
				statement.setString(2, administrator.getPassword());
				statement.setString(3, administrator.getEmail());			
				statement.setString(4, administrator.getFullName());
				statement.setString(5, administrator.getPhoneNumber());
				statement.setBoolean(6, administrator.getIsSuperAdmin());
				statement.setBoolean(7, administrator.getIsActive());			
				statement.setString(8, administrator.getAdminDes());
				statement.setInt(9, administrator.getId());
			}
      
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				sql = "select * from administrator where id="+administrator.getId();
				statement.close();
				statement = connection.prepareStatement(sql);
				rs = statement.executeQuery();
				while (rs.next()) {
					administrator.setTimestamp(rs.getTimestamp("timestamp"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return administrator;
	}
	
	
	// change state of one administrator
	public String changeStateForAdministrator(int id, int state) {
		if(id < 0) {
			return "administrator id can not be less than 0";
		}
		String executeResult = "";
		int rowsAffected = 0;
		PreparedStatement statement = null;
		
		try {
			String sql = "update administrator set isActive=? where id=?";			
			statement = connection.prepareStatement(sql);     
			statement.setInt(1, state);
			statement.setInt(2, id);     
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				executeResult = "StateUpdated";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			executeResult = e.toString();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return executeResult;
	}	
	
	// delete an administrator from DB
	public String deleteAdministratorInDB(int id) { 
		if(id < 0) {
			return "administrator id can not be less than 0";
		}
		String deleteResult = "";
		int rowsAffected = 0;
		PreparedStatement statement = null;
		
		try {
			String sql = "delete from administrator where id=?";			
			statement = connection.prepareStatement(sql);     
			statement.setInt(1, id);
			
			rowsAffected = statement.executeUpdate();
			if (rowsAffected == 1) {
				deleteResult = "DeletedInDB";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			deleteResult = "Failed to delete an administrator:" + e.toString();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return deleteResult;
	}
	
}