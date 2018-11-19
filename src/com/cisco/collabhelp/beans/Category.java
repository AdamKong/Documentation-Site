package com.cisco.collabhelp.beans;

import java.util.Date;

/**
 * Project Name: WebexDocsWeb
 * Title: Category.java
 * Description: category model
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 1 May 2018
 * @version 1.0
 */
public class Category { 
	private int id;
	private String categoryName;
	private String categoryDes;
	private String categoryCreator;
	private Date timestamp;
  
	public Category() {
		
	}
  
	public int getId() { 
		return id; 
	}
  
	public void setId(int id) {
		this.id = id;
	}
  
	public String getCategoryName() { 
		return categoryName; 
	}
  
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
  
	public String getCategoryDes() { 
		return categoryDes; 
	}
  
	public void setCategoryDes(String categoryDes) {
		this.categoryDes = categoryDes;
	}	
  
	public String getCategoryCreator() {
		return categoryCreator;
	}

	public void setCategoryCreator(String categoryCreator) {
		this.categoryCreator = categoryCreator;
	}

	public Date getTimestamp() { 
		return timestamp; 
	}
  
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}