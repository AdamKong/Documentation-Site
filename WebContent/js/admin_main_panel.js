/*
 * project name:WebexDocsWeb
 * description: javascript functions for the index.jsp (including the embedded create/search/update article/update article state/delete article pages).
 * @author Adam Kong
 * @date 21 May 2018
 * @version 1.0
 */
$(document).ready(function() {
	
	// Constant of cascading image names
	var image_separator = "---```---";
	
	var curWwwPath = window.document.location.href;  // get the www address of the current page
	var pathName = window.document.location.pathname; // get the path after the host:port - /projectname/...
	var pos = curWwwPath.indexOf(pathName);
	var localhostPath = curWwwPath.substring(0, pos); // get the host:port
	var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1); // projectname/...
	var realPath = localhostPath + projectName;

    alignLineBetweenAsideAndMain();

    // drag and resize the textarea can align the heights of aside and main frames.
    // this does not work with summernote.
    var $art_content_textareas = $('.art_content');
    $art_content_textareas.data('x', $art_content_textareas.outerWidth());
    $art_content_textareas.data('y', $art_content_textareas.outerHeight());
    $art_content_textareas.mouseup(function(){
        var $this = $(this);
        if (  $this.outerWidth() != $this.data('x') || $this.outerHeight() != $this.data('y') ){
            alignLineBetweenAsideAndMain();
        }
        // store new height/width
        $this.data('x', $this.outerWidth());
        $this.data('y', $this.outerHeight());
    });
    
    // embed summernote into my page.
    $art_content_textareas.summernote({
        height: 150,
        callbacks: {
            onImageUpload: function (files) {
            	for(var i=0; i< files.length; i++) {
            		sendFile(files[i]);
            	}
            }
        }
    });

  //upload pic by ajax
    function sendFile(file) {
        var formData = new FormData();
        formData.append("file", file);
        $.ajax({
            url: realPath + "/admin/uploadFile",
            data: formData,
            cache: false,
            contentType: false,
            processData: false,
            type: 'POST',
            success: function (imageName) {          	
            	$art_content_textareas.summernote('insertImage', realPath + "/articles/images/" + imageName, function ($image) {           		
                    $image.attr('src', realPath + "/articles/images/" + imageName);
                });
            }
        });
    }
    
    // form validation - article submission
    // do images check in article content.
    $("main .art_form").submit(function() {
        var isSubjectLengthValid = lengthRestriction("main .art_form .art_subject", 5, 100, "The length of article subject should be 5~100!");
        if (isSubjectLengthValid == false) {
        	return false;
        }
        
        var isAuthorNameValid = lengthRestriction("main .art_form .author_name", 5, 25, "The length of author name should be 5~25!");
        if (isAuthorNameValid == false) {
        	return false;
        }
        
//        var isTagLengthValid = lengthRestriction("main .art_form .art_tags", 5, 50, "The length of article tags should be 5~50!");
//        if (isTagLengthValid == false) {
//        	return false;
//        }
        
        // check the length of the article content
        var isArticleContentValid = lengthRestriction("main .art_form .art_content", 1, 65534, "The length of the article should be 1~65534!");
        if(isArticleContentValid == false){
        	return false;
        }
        
        // check images uploaded from content
        var imageNames = checkImagesInContent();
    	var imageNames_cascade = "";
    	for(var i=0; i<imageNames.length; i++){
        	if(i != (imageNames.length-1)){
        		imageNames_cascade += (imageNames[i] + image_separator);
        	}else{
        		imageNames_cascade += imageNames[i];
        	}       	
        }

        $(".imageNames").val(imageNames_cascade);
        
        return true;
    });   

    
    // form validation - category submission
    $("main .cat_form").submit(function() {
      // check category name length
      var isCategoryNameValid = lengthRestriction("main .cat_form .cat_name", 5, 25, "The length of category name should be 5~25!");
      if (isCategoryNameValid == false) {
      	return false;
      }
      
      // check category description length
      var isCategoryDesValid = lengthRestriction("main .cat_form .cat_des", 0, 200, "The length of category description should be 0~200!");
      if (isCategoryDesValid == false) {
        	return false;
      }
    });
    
    
    // form validation - search article form - backend
    $("main .art_backend_search_form").submit(function() {
        // check the length of the keywords field
    	var isSearchArticleInBackendKeywordsValid = lengthRestriction("main .art_backend_search_form .art_backend_search_keywords", 0, 30, "The length of the keywords should be 0~30!");
        if (isSearchArticleInBackendKeywordsValid == false) {
        	return false;
        }
        $(".art_backend_search_form input[name=requestIdentifier_bke_art]").val(uuid(32, 61));
    });
    
    // form validation - search category form - backend
    $("main .cat_search_form").submit(function() {
        // check the length of the keywords field
        var isSearchCategoryKeywordsValid = lengthRestriction("main .cat_search_form .cat_search_keywords", 0, 30, "The length of the keywords should be 0~30!");
        if (isSearchCategoryKeywordsValid == false) {
        	return false;
        }
        $(".cat_search_form input[name=requestIdentifier_bke_cat]").val(uuid(32, 61));
    });
    
    // form validation - search administrator form - backend
    $("main .adm_search_form").submit(function() {
        // check the length of the keywords field
        var isSearchAdministratorKeywordsValid = lengthRestriction("main .adm_search_form .adm_search_keywords", 0, 30, "The length of the keywords should be 0~30!");
        if (isSearchAdministratorKeywordsValid == false) {
        	return false;
        }
        $(".adm_search_form input[name=requestIdentifier_bke_adm]").val(uuid(32, 61));
    });
    
    // check images in content when submitting.
    function checkImagesInContent(){
    	var imageNames = new Array();
    	var imageURLs = $("main .art_form img");
    	for(var i=0; i< imageURLs.length; i++) {
    		var imageURL = imageURLs[i].src.trim();
    		var imageName = imageURL.substring(imageURL.lastIndexOf("/")+1, imageURL.length);
    		imageNames[i] = imageName;
    	}
    	return imageNames;
    }
    
    
    // for the pagination icon - change the background color if the mouse hover on it.
    $(".backend_pagination ul a li").hover(function(){
    	if($(this).attr("style") != "color:red; border:1px dashed red;"){
    		$(this).attr("style","background-color: silver");
    	}
    }, function(){
    	if($(this).attr("style") != "color:red; border:1px dashed red;"){
    		$(this).removeAttr("style");
    	}
    });
    
    
    // turn on and off an article.
    $(".disable").click(function(){
        if(confirm("Sure you want to change the state?")){      	
        	
        	var currentEle = $(this);
        	var stateText = currentEle.text();
        		stateText = stateText.trim();
        	var nameValue = currentEle.attr("name");
        	var articleId = nameValue.substr("disable".length);
        	var updateResult = false;
        	    	
        	$.post(realPath + "/admin/updateArticlesState", {
        		id: articleId,
        		command: stateText
        	}, function(data, status){      		
        		// trim() is important, cz there might be a hidden character added in transforming around "data". The hidden character can be seen in below way:
        		// alert(encodeURIComponent(data));
        		// alert(encodeURIComponent("All Done"));
        		if((status == "success") && (data.trim() == "All Done")) {
        			updateResult = true;       			
        			if(stateText == "Turn Off"){
        				currentEle.text("Turn On");
        			} else {
        				currentEle.text("Turn Off");
        			}
        		}else{
        			alert("Failed to change state: " + data.trim());
        		}
        	});     	
            return updateResult;
        } else {
            return false;
        } 
    });
    
    // delete an article.
    $(".delete").click(function(){    	    	
        if(confirm("Sure you want to delete this article, permanently?")){
        	
        	var nameValue = $(this).attr("name");
        	var articleId = nameValue.substr("delete".length);
        	var deleteResult = false;
        	
        	$.post(realPath + "/admin/deleteArticle", {
        		id: articleId
        	}, function(data, status){      		
        		// trim() is important, cz there might be a hidden character added in transforming around "data". The hidden character can be seen in below way:
        		// alert(encodeURIComponent(data));
        		// alert(encodeURIComponent("All Done"));
        		if((status == "success") && (data.trim() == "All Done")) {
        			deleteResult = true;       			
        			// delete the row of the article,
        			$(".article" + articleId).remove();
        		}else{
        			alert("Failed to delete the article: " + data.trim());
        		}
        	});     	
            return deleteResult;
        } else {
            return false;
        }
    });
    
 
    // delete an category.
    $(".deleteCat").click(function(){    	    	
        if(confirm("Sure you want to delete this category, permanently?")){
        	
        	var nameValue = $(this).attr("name");
        	var categoryId = nameValue.substr("delete".length);
        	var deleteResult = false;
        	
        	$.post(realPath + "/admin/deleteCategory", {
        		id: categoryId
        	}, function(data, status){      		
        		// trim() is important, cz there might be a hidden character added in transforming around "data". The hidden character can be seen in below way:
        		// alert(encodeURIComponent(data));
        		// alert(encodeURIComponent("All Done"));
        		if((status == "success") && (data.trim() == "All Done")) {
        			deleteResult = true;       			
        			// delete the row of the article,
        			$(".category" + categoryId).remove();
        		}else{
        			alert("Failed to delete this category: " + data.trim());
        		}
        	});     	
            return deleteResult;
        } else {
            return false;
        }
    });
    
    
    // Enable and disable administrator.
    $(".disableAdm").click(function(){
        if(confirm("Sure you want to change the state?")){      	
        	
        	var currentEle = $(this);
        	var stateText = currentEle.text();
        		stateText = stateText.trim();
        	var nameValue = currentEle.attr("name");
        	var adminId = nameValue.substr("disableAdm".length);
        	var updateResult = false;
        	    	
        	$.post(realPath + "/admin/updateAdminState", {
        		id: adminId,
        		command: stateText
        	}, function(data, status){      		
        		// trim() is important, cz there might be a hidden character added in transforming around "data". The hidden character can be seen in below way:
        		// alert(encodeURIComponent(data));
        		// alert(encodeURIComponent("All Done"));
        		if((status == "success") && (data.trim() == "Done")) {
        			updateResult = true;       			
        			if(stateText == "Disable"){
        				currentEle.text("Enable");
        			} else {
        				currentEle.text("Disable");
        			}
        		}else{
        			alert("Failed to change administrator state: " + data.trim());
        		}
        	});     	
            return updateResult;
        } else {
            return false;
        } 
    });
    
    // delete an administrator.
    $(".deleteAdm").click(function(){    	    	
        if(confirm("Sure you want to delete this administrator, permanently?")){
        	
        	var nameValue = $(this).attr("name");
        	var adminId = nameValue.substr("deleteAdm".length);
        	var deleteResult = false;
        	
        	$.post(realPath + "/admin/deleteAdministrator", {
        		id: adminId
        	}, function(data, status){      		
        		// trim() is important, cz there might be a hidden character added in transforming around "data". The hidden character can be seen in below way:
        		// alert(encodeURIComponent(data));
        		// alert(encodeURIComponent("All Done"));
        		if((status == "success") && (data.trim() == "deleteAdminDone")) {
        			deleteResult = true;       			
        			// delete the row of the administrator,
        			$(".administrator" + adminId).remove();
        		}else{
        			alert("Failed to remove the administrator: " + data.trim());
        		}
        	});     	
            return deleteResult;
        } else {
            return false;
        }
    });
    
    
    // logout
    $(".logout").click(function(){
        if(confirm("Sure you want to log out?")){
            return true;
        } else {
            return false;
        }
    });
    
    // set ajax to sync.
    $.ajaxSetup({   
        async : false  
    });

    
    // client side administrator form validation.
    var username_original = $.trim($("main .adm_form input[name=username]").val());
    $("main .adm_form").submit(function() {

    	var admFormAction = $("main .adm_form").attr("action");
    	
    	// Username validation - length and special char check.
        var usernameNode = $("main .adm_form input[name=username]");
        var username = $.trim(usernameNode.val());
        var username_pattern = /[a-zA-Z]\w{6,20}/;
        var matchResult = username.match(username_pattern);       	
    	if(matchResult != username) {
            alert("only letter, digit, underscore allowed. It must start with a leading letter. 6~20 chars!");
            usernameNode.focus();
            return false;
        }
    	
    	if((admFormAction == "createAdmin") || (admFormAction == "updateAdmin" && username_original != username)){  
	        //  check if the username has already been used.
    		var isAvailable = true;
	    	$.post(realPath + "/admin/checkUsername", {
	    		username: username,
	    	}, function(data, status){      		
	    		// trim() is important, cz there might be a hidden character added in transforming around "data". The hidden character can be seen in below way:
	    		// alert(encodeURIComponent(data));
	    		// alert(encodeURIComponent("All Done"));
	    		if((status == "success") && (data.trim() == "Available")) {
	    			isAvailable = true;
	    		}else{
	    			alert("username is occupied!");
	    			$("main .adm_form input[name=username]").focus();
	    			isAvailable = false;
	    		}
	    	});
	    	
	    	if(isAvailable == false){
	    		return false;
	    	}
    	}
    	
    	//  admin account creation - verify if the two input passwords are the same.
    	var passwords = new Array();
    	var passwordNodes = $("main .adm_form input[type=password]");
    	for(var i=0; i<passwordNodes.length; i++){
    		passwords[i] = passwordNodes[i].value;
    		if(admFormAction == "createAdmin"){       		
    			if (passwords[i].length < 6 || passwords[i].length > 20) {
        			alert("Password length should be 6~20!");
        			passwordNodes[i].value = "";
        			passwordNodes[i].focus();
        			return false;
        		}    			
        	} else {
        		if(passwords[i] != ""){
        			if (passwords[i].length < 6 || passwords[i].length > 20) {
            			alert("Password length should be 6~20!");
            			passwordNodes[i].value = "";
            			passwordNodes[i].focus();
            			return false;
            		}
        		}       		
        	}	
    	}    	
    	if(passwords[0] != passwords[1]){
    		alert("The two passwords do not match!");
    		return false;
    	}
    	
    	// email validation - length and email format check.
        var emailNode = $("main .adm_form input[name=email]");
        var email = $.trim(emailNode.val());
        var email_pattern = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
        if (email.length < 5 || email.length > 50) {
            alert("The length of email should be 5~50!");
            emailNode.focus();
            return false;
        } else {
        	if(!email_pattern.test(email)) {
                alert("Email format is not correct!");
                emailNode.focus();
                return false;
            } 
        }
    
    	// full name check
    	var fullname = $.trim($("main .adm_form input[name=fullname]").val());
    	var fullname_pattern = /[a-zA-Z. ]{0,30}/;
    	var matchResult = fullname.match(fullname_pattern);
    	if(matchResult != fullname){
    		alert("Full name should only contain letter, space, dot, with a length of 0~30");
    		return false;
    	}
    	
    	// phone number check
    	var phonenumber = $.trim($("main .adm_form input[name=phonenumber]").val());
    	var phonenumber_pattern = /[0-9]{0,15}/;
    	    matchResult = phonenumber.match(phonenumber_pattern);
    	if(matchResult != phonenumber){
    		alert("Phone number should be all digit, with a length of 0~15");
    		return false;
    	}
        
        // admin description check
        var isAdmDesLengthValid = lengthRestriction("main .adm_form textarea", 0, 200, "The length of admin description should be 0~200!");
        if (isAdmDesLengthValid == false) {
        	return false;
        }    
        
        // check if the current password is filled.
        if(passwordNodes.length==3 && passwords[2]=="" ){
        	alert("Please input the current password!");
        	return false;
        }
        
        if((admFormAction == "createAdmin") || (admFormAction=="updateAdmin" && passwords[0] != "") || (admFormAction=="changeProfile" && passwords[0] != "")){ 
        	getMD5EncryptedPassWordForNewPW();
        }
        
        getMD5EncryptedPassWordForCurrentPW();
        
        return true;
    });
    
    // generate a MD5 encrypted password for new passwords
    function getMD5EncryptedPassWordForNewPW(){
    	var password = $("main .adm_form input[name=password]").val();
    	var password2 = $("main .adm_form input[name=password2]").val();
    	
    	$("main .adm_form input[name=password]").val(hex_md5(password));
    	$("main .adm_form input[name=password2]").val(hex_md5(password2)); 	
    }
    
    // generate a MD5 encrypted password for current passwords
    function getMD5EncryptedPassWordForCurrentPW(){
    	if($("main .adm_form input[name=current_password]").length > 0){
    		var current_password = $("main .adm_form input[name=current_password]").val();
    		$("main .adm_form input[name=current_password]").val(hex_md5(current_password)); 
    	}
    }
    
    // change background color if mouse over to "change account profile"
    $(".change_acc_info").hover(function(){
        $(this).attr("style","background-color: silver");
    }, function(){
        $(this).attr("style","background-color: #moccasin");
    });

    // align the lines between the aside and main part.
    function alignLineBetweenAsideAndMain(){
        var aside_height = $("aside").height();
        var main_height  = $("main").height();
        if (main_height > aside_height) {
            $("aside").attr("style", "height:" + main_height + "px");
        } else {
            $("main").attr("style", "height:" + aside_height + "px");
        }
    }
    
    // form field length restriction
    function lengthRestriction(elementNode, minLength, maxLengh, alertMessage) {
        var elementName = $(elementNode);
        if(elementName == null){
        	return;
        }
        var lengthWithoutSpace = $.trim(elementName.val()).length;
               
        if(lengthWithoutSpace < minLength || lengthWithoutSpace > maxLengh) {
        	alert(alertMessage);
        	elementName.val("");
        	elementName.focus();
        	return false;
        }else{
        	return true;
        }          
    }

    // generate a random uuid.
    function uuid(len, radix) {
    	  var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    	  var uuid = [], i;
    	  radix = radix || chars.length;
    	 
    	  if (len) {
    	   // Compact form
    	   for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    	  } else {
    	   // rfc4122, version 4 form
    	   var r;
    	 
    	   // rfc4122 requires these characters
    	   uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
    	   uuid[14] = '4';
    	 
    	   // Fill in random data. At i==19 set the high bits of clock sequence as
    	   // per rfc4122, sec. 4.1.5
    	   for (i = 0; i < 36; i++) {
    	    if (!uuid[i]) {
    	     r = 0 | Math.random()*16;
    	     uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
    	    }
    	   }
    	  }
    	 
    	  return uuid.join('');
    }
    
});