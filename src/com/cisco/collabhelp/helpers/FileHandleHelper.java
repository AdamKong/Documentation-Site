package com.cisco.collabhelp.helpers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Project Name: WebexDocsWeb
 * Title: FileHandleHelper.java
 * Description: file operations
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 12 Jun 2018
 * @version 1.0
 */
public class FileHandleHelper {
	
	// upload file
	public static String uploadFile(HttpServletRequest req, String savePath) {

		// prompt message
		String message = "";
		
		// if the request content is not a multipart content, then it's not a valid upload request.
		if(!ServletFileUpload.isMultipartContent(req)) {
			return "This is not a file upload request!";
		}
		
		File file = new File(savePath);		
		// if the directory of saving images exists or not. If not, create one.
		if(!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(20*1024*1024); 	// a pic can not be greater than 20M
		upload.setHeaderEncoding("UTF-8");
				
		try {
			List<FileItem> list = upload.parseRequest(req);
			for(FileItem item: list) {
				if(item.isFormField()) {
					String name = item.getFieldName();
					String value = item.getString("UTF-8");
					System.out.println(name + "=" + value);
				}else {
					String originalFileName = item.getName(); // the original file name
					String suffix = originalFileName.substring(originalFileName.lastIndexOf(".")); // the original suffix
					String newFileName = new Date().getTime() + suffix;
					
					// new file name and path
					file = new File(savePath + "/" + newFileName);
					item.write(file);
					item.delete();
					message = newFileName;
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
			message = "failed to get form upload data: " + e.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			message = "failed to get form upload data: " + e.toString();
		} catch (Exception e) {
			e.printStackTrace();
			message = "failed to get form upload data: " + e.toString();
		}
		
		return message;
	}

	// Destroy a file.
	public static String removeFile(String path) {
		String deleteResult = "";
		try {
			File file = new File(path);
			if (file.exists()) {
				if(file.delete()) {
					deleteResult = "fileDeletedInDisk";
				};
			}
		}catch (Exception e){
			e.printStackTrace();
			deleteResult = "Failed to delete an file in disk: " + e.toString();
		}
			
		return deleteResult;
	}
	
}
