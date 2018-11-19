package com.cisco.collabhelp.helpers;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 
 * Project Name: WebexDocsWeb
 * Title: Utility.java
 * Description: tools
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 1 Oct 2018
 * @version 1.0
 */
public class Utility {
	// The delimiter of connecting image names.
	public static final String IMAGEDELIMITER = "---```---";
	
	// Pagination - The maximum amount of articles in each page.
	public static final int PAGE_SIZE = 5;
	
	// Pagination - The maximum amount of page numbers in one page.
	public static final int PAGE_NUMBER_SIZE = 15;
	
	// A tool function to judge if a string is numeric.
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("^[1-9]\\d*");
	    return pattern.matcher(str).matches();   
	}
	
	// A tool function to get a UUID which has a length of 32.
	public static String getUUID32(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	

}
