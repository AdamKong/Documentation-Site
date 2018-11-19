package com.cisco.collabhelp.dao;

import java.util.Properties;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * Project Name: WebexDocsWeb
 * Title: DBConnection.java
 * Description: connect to DB
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 11 Apr 2018
 * @version 1.0
 */
public class DBConnection {
	
	private static Connection conn = null;
	
	public static Connection getConnectionToDatabase() {		
		boolean isConnValid = false;
		try {
			if((conn != null) && conn.isValid(5)) {
				isConnValid = true;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}		
		
		if(!isConnValid) {
			try{
				
				String DB_driver = "";
				String DB_connURL = "";
				String DB_user = "";
				String DB_password = ""; 
				
				Properties prop = new Properties();   
		        InputStream in = DBConnection.class.getResourceAsStream("/db.properties"); 
				prop.load(in);
				
				DB_driver = prop.getProperty("db_driver").trim();
				DB_connURL = prop.getProperty("db_connURL").trim();
				DB_user = prop.getProperty("db_user").trim();
				DB_password = prop.getProperty("db_password").trim();
				
				Class.forName(DB_driver);							
				conn = java.sql.DriverManager.getConnection(DB_connURL, DB_user, DB_password);
				System.out.println("DB Connection built!");				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {   
	            e.printStackTrace();   
	        } 
		}		
		return conn;
	}
	

		
}