package com.cisco.collabhelp.beans;

import java.util.Date;

/**
 * Project Name: WebexDocsWeb
 * Title: Article.java
 * Description: article model
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 1 May 2018
 * @version 1.0
 */
public class Article {
	
	private int id;
	private String subject;
	private String author;
	private String category;
	private Date timestamp;
	private String tags;
	private int state;
	private String content;
	private String imagenames;
	private String creator;
	private Date modifytimestamp;

	public Article(int id, String subject, String author, String category, Date timestamp, String tags, int state, String content, String imagenames, String creator, Date modifytimestamp){
		this.id = id;
	    this.subject = subject;
	    this.author = author;
	    this.category = category;
	    this.timestamp = timestamp;
	    this.tags = tags;
	    this.state = state;
	    this.content = content;
	    this.imagenames = imagenames;
	    this.creator = creator;
	    this.modifytimestamp = modifytimestamp;
	}
	
	public Article() {
		  
	}
  
  	public int getId() {
	  	return id;
  	}
  
  	public void setId(int id) { 
	  	this.id = id; 
  	}
  
  	public String getSubject() {
	  	return subject;
  	}
  
  	public void setSubject(String subject) { 
	  	this.subject = subject;
  	}
  
  	public String getAuthor() {
	  	return author;
  	}
  
  	public void setAuthor(String author) { 
  		this.author = author; 
  	}
  
  	public String getCategory() {
	  	return category;
  	}
  
  	public void setCategory(String category) { 
  		this.category = category; 
  	}
  
  	public Date getTimestamp() {
  		return timestamp;
  	}
  
  	public void setTimestamp(Date timestamp) { 
  		this.timestamp = timestamp;
  	}
  
  	public String getTags() {
  		return tags;
  	}
  
  	public void setTags(String tags) { 
  		this.tags = tags;
  	}
  
  	public int getState() {
	  	return state;
  	}
  
	public void setState(int state) { 
		this.state = state;
  	}
  
  	public String getContent() {
  		return content;
  	}
  
  	public void setContent(String content) { 
  		this.content = content;
  	}
  
	public String getImagenames() {
		return imagenames;
	}
	
	public void setImagenames(String imagenames) {
		this.imagenames = imagenames;
	}
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public Date getModifytimestamp() {
		return modifytimestamp;
	}
	
	public void setModifytimestamp(Date modifytimestamp) {
		this.modifytimestamp = modifytimestamp;
	}
  
}