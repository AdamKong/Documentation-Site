package com.cisco.collabhelp.beans;

import java.util.Date;

/**
 * Project Name: WebexDocsWeb
 * Title: Image.java
 * Description: image model
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 1 Jul 2018
 * @version 1.0
 */
public class Image {
	private int id;
	private String imageName;
	private int articleId;
	private Date timestamp;
	private String uploader;
	
	public Image() {
		
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getUploader() {
		return uploader;
	}
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	
}
