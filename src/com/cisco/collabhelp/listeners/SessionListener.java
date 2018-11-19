package com.cisco.collabhelp.listeners;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.cisco.collabhelp.dao.ApplicationDao;
import com.cisco.collabhelp.dao.DBConnection;

/**
 * Project Name: WebexDocsWeb
 * Title: SessionListener.java
 * Description: Listeners
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 22 Jul 2018
 * @version 1.0
 */
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {
	
	@Override
	public void attributeRemoved(HttpSessionBindingEvent se) {
		
		if("username".equals(se.getName())) {
			String username = se.getValue().toString();
			
			// debug code
				System.out.println("username: " + username);
			// debug code
			
			ServletContext application = se.getSession().getServletContext();
			ArrayList<String> userArray = new ArrayList<String>();
			if(application.getAttribute("loggedInUserList") != null) {
				userArray = (ArrayList<String>) application.getAttribute("loggedInUserList");
			}

			// debug code
				if(userArray != null) {
					System.out.println("userArray.size():" + userArray.size());
				}
			// debug code
			
			if(userArray.contains(username)) {
				userArray.remove(username);
				application.setAttribute("loggedInUserList", userArray);
			}		
		
			
			
			// debug code
				ArrayList<String> userArray0 = (ArrayList<String>) application.getAttribute("loggedInUserList");
				if(userArray0 != null) {
					System.out.println("userArray0.size(): " + userArray0.size());			
					for(int i=0; i<userArray0.size(); i++) {
						System.out.println(i +":" + userArray0.get(i));
					}	
				
				}	
			// debug code
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		
		Connection conn = DBConnection.getConnectionToDatabase();
		if(conn != null) {
			try {
				conn.close();
				System.out.println("DB Connection Destroyed!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	

}