package com.cisco.collabhelp.beans;

import java.util.Date;

/**
 * Project Name: WebexDocsWeb
 * Title: Administrator.java
 * Description: administrator model
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 11 Apr 2018
 * @version 1.0
 */
public class Administrator {
	
	private int id;
	private String username;
	private String password;
	private String email;
	private String fullName;
	private String phoneNumber;
	private Boolean isSuperAdmin;
	private Boolean isActive;
	private String adminDes;
	private Date timestamp;
  
	public Administrator() {
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Boolean getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(Boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getAdminDes() {
		return adminDes;
	}

	public void setAdminDes(String adminDes) {
		this.adminDes = adminDes;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
  
}