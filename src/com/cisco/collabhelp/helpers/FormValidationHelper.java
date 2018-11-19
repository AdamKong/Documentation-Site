package com.cisco.collabhelp.helpers;

import com.cisco.collabhelp.beans.Administrator;
import com.cisco.collabhelp.beans.Category;
import com.cisco.collabhelp.dao.ApplicationDao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project Name: WebexDocsWeb
 * Title: FormValidationHelper.java
 * Description: helper of validating forms.
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 1 May 2018
 * @version 1.0
 */
public class FormValidationHelper {

	static ApplicationDao dao = ApplicationDao.getApplicationDao();
	
	// Validate the article form data
	public static String validateArticleFormData(String subject, String author, String category, String tags, String content) {
		
		// validate the validity of the parameters.
		if((subject == null) || (author == null) || (category == null) || (tags == null) || (content == null)) {
			return "Article Item can not be null!";
		}
		
		subject = subject.trim();
		author = author.trim();
		category = category.trim();
		tags = tags.trim();
		content = content.trim();

		if ((subject.length() < 5) || (subject.length() > 100)) {
			return "The length of the subject is invalid!";
		}
		
		if ((author.length() < 5) || (author.length() > 25)) {
			return "The length of the author name is invalid!";
		}
		
//		if ((tags.length() < 5) || (tags.length() > 50)) {
//			return "The length of the tags is invalid";
//		}
		
		if (content.length() == 0) {
			return "The length of the content can not be empty";
		}
		
		List<Category> categories = dao.listCategories();
		List<String> categoryNames = new ArrayList<String>();
		for (int i = 0; i < categories.size(); i++) {
			categoryNames.add(((Category)categories.get(i)).getCategoryName());
		}
		if (!categoryNames.contains(category)) {
			return "The category is invalid";
		}
    
		return "Good";
	}
  
	// Validate the admin login form data.
	public static String validateLoginFormData(String username, String password) {
		
		if((username == null) || (password == null)) {
			return "username or password can not be null";
		}
		
		username = username.trim();
		password = password.trim();
		
		// username validation                                
//        if (username.length() < 6 || username.length() > 20) {        	
//        	return "The length of username should be 6~20!";   
//        } 
//               
//        String usernamePattern = "[<>\'\"]";
//        Pattern r = Pattern.compile(usernamePattern);
//        Matcher m = r.matcher(username);           
//        if(m.find()) {
//        	return "The username contains invalid characters:" + m.group(0);
//        }
        
        // username validation 
        String regex = "[a-zA-Z]\\w{6,20}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(username);
		if(!m.matches()) {
			return "username is not well formatted: only letter, digit, underscore allowed. It must start with a leading letter. 6~20 chars!";
		}
		          
        // password validation
        regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{32}$";
		p = Pattern.compile(regex);
		m = p.matcher(password);
		if(!m.matches()) {
			return "MD5 encoded password in password field is invalid. Maybe someone is trying to access the system maliciously!";
		}
    
		return "Good";
	}
	
	
	// Validate the search form in home page.
	// validate category, keywords.
	public static String validateSearchForm(String keywords, String category) {
		
		String validateResult = "";
		
		// validate the validity of the parameters.
		if((keywords == null) || (category == null)) {
			return "keywords or category can not be null!";
		}

		keywords = keywords.trim();
		category = category.trim();		
		
		String validateCategoryResult = "Category is invalid!";
		if("All".equals(category)) {
			validateCategoryResult = "Good";
		} else {
			List<Category> categories = dao.listCategories();
			String categoryName = "";
			for (int i = 0; i < categories.size(); i++) {
				categoryName = categories.get(i).getCategoryName();
				if (category.equals(categoryName)) {
					validateCategoryResult = "Good";
					break;
				}			
			}		
		}
		
		String validateKeywordsResult = "Good";
		if(keywords.length() < 0 || keywords.length() > 30) {
			validateKeywordsResult = "The length of keywords should be 0~30";
		}
		
		if(("Good".equals(validateCategoryResult)) && ("Good".equals(validateKeywordsResult))) {
			validateResult = "Good";
		}
			
		return validateResult;
	}
	
	
	// Validate the category form.
	public static String validateCategoryFormData(String cat_name, String cate_des) {
		
		if((cat_name == null) || (cate_des == null)) {
			return "category name or category description can not be null";
		}
		
		cat_name = cat_name.trim();
		cate_des = cate_des.trim();
		
		String validateResult = "Good";
		
		// category name validation                                
        if (cat_name.length() < 5 || cat_name.length() > 20) {        	
        	validateResult = "The length of category name should be 5~20!";   
        }
        
     // category description validation                                
        if (cate_des.length() < 0 || cate_des.length() > 200) {        	
        	validateResult =  "The length of category description should be 0~200!";   
        }
        
		return validateResult;
	}
	
	
	// Validate the administrator form
	public static String validateAdministratorFormData(
														int adminId,
														String username_original, 
														String username, 
														String password_inMD5, 
														String confirm_password_inMD5,
														String email, 
														String fullname, 
														String phonenumber,
														String adminType, 
														String active, 
														String adm_des) {
		
		if(
			(username_original == null) ||
			(username == null) ||
			(password_inMD5 == null) ||
			(confirm_password_inMD5 == null) ||
			(email == null) ||
			(fullname == null) ||
			(phonenumber == null) ||
			(adminType == null) ||
			(active == null) ||
			(adm_des == null)) {
			return "At least one of the registration items is null, and it's not allowed!";
		}
		
		username = username.trim();
		username_original = username_original.trim();
		password_inMD5 = password_inMD5.trim();
		confirm_password_inMD5 = confirm_password_inMD5.trim();
		email = email.trim();
		fullname = fullname.trim();
		phonenumber = phonenumber.trim();
		adminType = adminType.trim();
		active = active.trim();
		adm_des = adm_des.trim();
		
		// username check
		String regex = "[a-zA-Z]\\w{6,20}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(username);
		if(!m.matches()) {
			return "username is not well formatted: only letter, digit, underscore allowed. It must start with a leading letter. 6~20 chars!";
		}
		if(adminId<0 || (adminId>0 && !username_original.equals(username))) {
			Administrator administrator = dao.getAdministratorByUsername(username);
			if(administrator.getTimestamp() != null) {
				return "The username has already been used, please use another one!";
			}
		}	
		
		// password check
		// MD5 encoded password can not be all number.
		// MD5 encoded password can not be all letter.
		// MD5 encoded password must be number or letter.
		// MD5 encoded password must have a length of 32.
		if((adminId < 0) || (password_inMD5 != "") || (confirm_password_inMD5 != "")) {
			
			if(!password_inMD5.equals(confirm_password_inMD5)) {
				return "Two passwords do not match!";
			}
			regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{32}$";
			p = Pattern.compile(regex);
			m = p.matcher(password_inMD5);
			if(!m.matches()) {
				return "MD5 encoded password in password field is invalid. Maybe someone is trying to access the system maliciously!";
			}
			m = p.matcher(confirm_password_inMD5);
			if(!m.matches()) {
				return "MD5 encoded password in confirm password field is invalid. Maybe someone is trying to access the system maliciously!";
			}
		}		
		
		// email check.
		if(email.length()<5 || email.length()> 50) {
			return "Email length should be 5~50.";
		}
		regex = "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$";
		p = Pattern.compile(regex);
		m = p.matcher(email);
		if(!m.matches()) {
			return "Email format is not correct!";
		}
		
		// full name check
		regex = "[a-zA-Z. ]{0,30}";
		p = Pattern.compile(regex);
		m = p.matcher(fullname);
		if(!m.matches()) {
			return "Full name format is not correct. It should only contain letter, space, dot!";
		}
		
		// phone number check
		regex = "[0-9]{0,15}";
		p = Pattern.compile(regex);
		m = p.matcher(phonenumber);
		if(!m.matches()) {
			return "Phone number format is not correct. It should only contain digits!";
		}
		
		// admin type check
		if((!"1".equals(adminType)) && (!"0".equals(adminType))) {
			return "adminType is invalid";
		}
		
		// check the value of "if it's active when initially created!".
		if((!"1".equals(active)) && (!"0".equals(active))) {
			return "the value of the 'if it's active initially' question is invalid";
		}
		
		// admin description length check
		if(adm_des.length()<0 || adm_des.length()>200) {
			return "the length of the admin description should be 0~200";
		}

		return "Good";
	}
	
	
	
	
}



