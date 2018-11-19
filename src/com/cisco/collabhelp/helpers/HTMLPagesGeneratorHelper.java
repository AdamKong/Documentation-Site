package com.cisco.collabhelp.helpers;

import com.cisco.collabhelp.beans.Article;
import com.cisco.collabhelp.beans.Category;
import com.cisco.collabhelp.dao.ApplicationDao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Project Name: WebexDocsWeb
 * Title: HTMLPagesGeneratorHelper.java
 * Description: helpers of generating and destroying HTML pages
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 1 May 2018
 * @version 1.0
 */
public class HTMLPagesGeneratorHelper {
	
	// generate HTML Page for one article
	public static String generateHTMLPageForOneArticle(String modelPath, String outHTMLPath, Article article) {
			String modelContent = readModelString(modelPath);
			if(modelContent.startsWith("Failed to get the model content: ")) {
				return modelContent;
			}
			
			modelContent = modelContent.replaceAll("#####Subject#####", article.getSubject());
			modelContent = modelContent.replaceAll("#####AuthorName#####", article.getAuthor());
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E. yyyy-MM-dd HH:mm:ss z");
			TimeZone tz = TimeZone.getTimeZone("GMT+0");
			simpleDateFormat.setTimeZone(tz);
			Date articleDate = article.getTimestamp();
			String timestamp = simpleDateFormat.format(articleDate).toString();		

			modelContent = modelContent.replaceAll("#####Timestamp#####", timestamp);
			modelContent = modelContent.replaceAll("#####Article_content#####", article.getContent());			
			modelContent = modelContent.replaceAll("#####Tags#####", article.getTags());
			modelContent = modelContent.replaceAll("#####Creator#####", article.getCreator());
			modelContent = modelContent.replaceAll("#####Category#####", article.getCategory());
			
			Date lastModifiedDate = article.getModifytimestamp();
			String lastModifiedTimestamp = "Not Modified";
			if(lastModifiedDate != null) {
				lastModifiedTimestamp = simpleDateFormat.format(lastModifiedDate).toString();	
			}
			modelContent = modelContent.replaceAll("#####LastModifiedTimestamp#####", lastModifiedTimestamp);	
							
			String writeResult = writeToHTMLPage(modelContent, outHTMLPath);
			if(writeResult.startsWith("Failded to write to a HTML page: ")) {
				return writeResult;
			}
			
			if("Hot Article".equals(article.getCategory())) {
				String pathOfHotArticleModel = modelPath.substring(0, modelPath.length() - "article_content.html".length()) + "home_hotarticle_content.html";
				String pathOfHotArticleForHomeMainPart = modelPath.substring(0, modelPath.length() - "article_content.html".length()) + article.getId() + "-forMainPart.html";

				String hotArticleModelContent = readModelString(pathOfHotArticleModel);
				if(hotArticleModelContent.startsWith("Failed to get the model content: ")) {
					return hotArticleModelContent;
				}
				
				hotArticleModelContent = hotArticleModelContent.replaceAll("#####Subject#####", article.getSubject());
				hotArticleModelContent = hotArticleModelContent.replaceAll("#####AuthorName#####", article.getAuthor());				
				hotArticleModelContent = hotArticleModelContent.replaceAll("#####Timestamp#####", timestamp);
				hotArticleModelContent = hotArticleModelContent.replaceAll("#####Article_content#####", article.getContent());
				hotArticleModelContent = hotArticleModelContent.replaceAll("#####Tags#####", article.getTags());
				hotArticleModelContent = hotArticleModelContent.replaceAll("#####Creator#####", article.getCreator());
				hotArticleModelContent = hotArticleModelContent.replaceAll("#####Category#####", article.getCategory());
				hotArticleModelContent = hotArticleModelContent.replaceAll("#####LastModifiedTimestamp#####", lastModifiedTimestamp);
				
				String writeHotArticleForMainPartResult = writeToHTMLPage(hotArticleModelContent, pathOfHotArticleForHomeMainPart);
				if(writeHotArticleForMainPartResult.startsWith("Failded to write to a HTML page: ")) {
					return writeHotArticleForMainPartResult;
				}	
			}

			return "HTMLPageForOneArticleCreated";
	}
  
	// generate index.html page
	public static String generateFrontHomePage(String modelPath, String outHTMLPath) {	
				
		String modelContent = readModelString(modelPath);
		if(modelContent.startsWith("Failed to get the model content: ")) {
			return modelContent;
		}
		
		// generate category options in search form		
		// generate the side menu
		int numberOfSubjectsInOneCategoryInLeftMenu = 5;
		String leftMenuContent = "";
		String leftMenuTemplate = "";
		String mainPartContent = "";		
		ApplicationDao dao = ApplicationDao.getApplicationDao();
		List<Article> articles = null;
		Article articleInMenu = null;
		List<Category> categories = dao.listCategories();
		Category category = null;
		String categoryOptionsInSearchForm = "";
		String displaySubject = "";
		int displaySubjectLength = 40;
		String complementingString = "...";
				
		for(int i=0; i<categories.size(); i++) {
			category = categories.get(i);
			categoryOptionsInSearchForm += "<option value =\"" + category.getCategoryName() + "\">" + category.getCategoryName() + "</option>";
								
			leftMenuTemplate = "                <section>\n" + 
					"                    <div class=\"section_header\">\n" + 
					"                        <span>-</span> #####CategoryName##### <span class=\"more\"><a href=\"list_articles.jsp?categoryId=#####CategoryId#####\" target=\"_blank\">more</a></span>\n" + 
					"                    </div>\n" + 
					"                    <div class=\"section_list\">\n" + 
					"                        <ul>\n" + 
					"                            <li><a href=\"articles/#####ArticleId0#####.html\" target=\"_blank\">#####ArticleSubject0#####</a></li>\n" + 
					"                            <li><a href=\"articles/#####ArticleId1#####.html\" target=\"_blank\">#####ArticleSubject1#####</a></li>\n" + 
					"                            <li><a href=\"articles/#####ArticleId2#####.html\" target=\"_blank\">#####ArticleSubject2#####</a></li>\n" + 
					"                            <li><a href=\"articles/#####ArticleId3#####.html\" target=\"_blank\">#####ArticleSubject3#####</a></li>\n" + 
					"                            <li><a href=\"articles/#####ArticleId4#####.html\" target=\"_blank\">#####ArticleSubject4#####</a></li>\n" + 
					"                        </ul>\n" + 
					"                    </div>\n" + 
					"                </section>";				
			leftMenuTemplate = leftMenuTemplate.replaceAll("#####CategoryName#####", category.getCategoryName());
			leftMenuTemplate = leftMenuTemplate.replaceAll("#####CategoryId#####", category.getId() + "");
			articles = dao.getTopXArticlesInOneCategory(category.getCategoryName(), numberOfSubjectsInOneCategoryInLeftMenu, false, 1);
			for(int j=0; j<articles.size(); j++) {
				articleInMenu = articles.get(j);
				leftMenuTemplate = leftMenuTemplate.replaceAll("#####ArticleId" + j + "#####", articleInMenu.getId() + "");
				if(articleInMenu.getSubject().length() < displaySubjectLength) {
					displaySubject = articleInMenu.getSubject();
				}else {
					displaySubject = articleInMenu.getSubject().substring(0, displaySubjectLength) + complementingString;
				}
				leftMenuTemplate = leftMenuTemplate.replaceAll("#####ArticleSubject" + j + "#####", displaySubject);
			}
			
			leftMenuContent += leftMenuTemplate;						
		}
		
		// Use the latest Hot Article to fill the main part.
		String newHotArticleForMainPartLocation  = "";
		String baseURL = modelPath.substring(0, modelPath.length() - "index-template.html".length());
		String pathOfTheLatestHotArticleForMainPart = "";
		
		List<Article> hotArticles = dao.getTopXArticlesInOneCategory("Hot Article", 1, false, 1);
		Article theLatestHotArticle = null;
		if(!hotArticles.isEmpty()) {
			theLatestHotArticle = hotArticles.get(0);	
			newHotArticleForMainPartLocation = "articles/" + theLatestHotArticle.getId() + "-forMainPart.html";				
			pathOfTheLatestHotArticleForMainPart = baseURL + newHotArticleForMainPartLocation;			
			mainPartContent = readModelString(pathOfTheLatestHotArticleForMainPart);
			if(mainPartContent.startsWith("Failed to get the model content: ")) {
				return mainPartContent;
			}			
		}				
							
		modelContent = modelContent.replaceAll("#####SearchCategoryOptions#####", categoryOptionsInSearchForm);	
		modelContent = modelContent.replaceAll("#####LeftMenu#####", leftMenuContent);	
		modelContent = modelContent.replaceAll("#####ArticlePage#####", mainPartContent);
		String writeResult = writeToHTMLPage(modelContent, outHTMLPath);
		if(writeResult.startsWith("Failded to write to a HTML page: ")) {
			return writeResult;
		}
				
		return "FrontHomePageReGenerated";
	}
	
	
	// Destory a HTML page.
	public static String destoryHTMLPages(String path) {
		File file = new File(path);
		if(file.exists()) {
			file.delete();
		}	
		return "Destroyed";
	}
	
	// Read Model into memory
	private static String readModelString(String modelPath) {
		String modelContent = "";		
		FileInputStream fileInputStream = null;
		InputStreamReader reader = null;
		BufferedReader br = null;
		
		File file=new File(modelPath);
		if (!file.exists()) {
			System.out.println("Can not get the model content: The file at path " + modelPath + " does not exist.");
			int indexOfLastSlash = modelPath.lastIndexOf("/");
			String path = modelPath.substring(0, indexOfLastSlash);
			String pathOfEmptyFile = path + "/empty.html";
			modelPath = pathOfEmptyFile;
		}
		try {
			fileInputStream = new FileInputStream(modelPath);
            reader = new InputStreamReader(fileInputStream, "UTF-8");
            br = new BufferedReader(reader);
            String line = "";
            while ((line = br.readLine()) != null) {
            	modelContent += line + "\n";
            }
        } catch (FileNotFoundException e) {
        	modelContent = "Failed to get the model content: " + e.toString();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			modelContent = "Failed to get the model content: " + e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			modelContent = "Failed to get the model content: " + e.toString();
			e.printStackTrace();
		} finally {
			if(br != null ) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
            if (fileInputStream != null) {
                try {
                	fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

		return modelContent;
	}
	
	// Write the modified string to disk as html page.
	private static String writeToHTMLPage(String modifiedString, String outHTMLPath) {		
		
		String writeResult = "Failded to write to a HTML page: ";	
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter writer = null;
		try {
			fileOutputStream = new FileOutputStream(outHTMLPath);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");			
			writer = new BufferedWriter(outputStreamWriter);
			writer.write(modifiedString);
			writeResult = "Wrote to HTML page";
		} catch (FileNotFoundException e) {
			writeResult += e.toString();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			writeResult += e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			writeResult += e.toString();
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
            if (fileOutputStream != null) {
                try {
                	fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

		return writeResult;	
	}

}