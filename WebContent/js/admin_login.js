/*
 * project name:WebexDocsWeb
 * description: javascript functions for the admin login page
 * @author Adam Kong
 * @date 21 May 2018
 * @version 1.0
 */
$(document).ready(function(){
	
	$.ajaxSetup({   
        async : false  
    });
	
	var curWwwPath = window.document.location.href;  // get the www address of the current page
	var pathName = window.document.location.pathname; // get the path after the host:port - /projectname/...
	var pos = curWwwPath.indexOf(pathName);
	var localhostPath = curWwwPath.substring(0, pos); // get the host:port
	var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1); // projectname/...
	var realPath = localhostPath + projectName;

    // Handle the focusin/focusout event.
    $(".login_window form input[name=username], .login_window form input[name=password]").focusin(function(){
        if($.trim($(this).val()).length === 0){
            $(this).attr("placeholder", "");
        }
    }).focusout(function(){
        if ($.trim($(this).val()).length === 0) {
            $(this).val("");
            if($(this).attr("type") == "text"){
                $(this).attr("placeholder", " username");
            } else {
                $(this).attr("placeholder", " password");
            }
        }
    });

    // Handle the login form validation.
    $(".login_window form").submit(function() {
    	
        // Username validation
        var usernameNode = $("form .username");
        var username = $.trim(usernameNode.val());
        var username_pattern = /[a-zA-Z]\w{6,20}/;
        var matchResult = username.match(username_pattern);       	
    	if(matchResult != username) {
            alert("only letter, digit, underscore allowed. It must start with a leading letter. 6~20 chars!");
            usernameNode.focus();
            return false;
        }
    	
        // password validation
        var password = $("form .password");
        if (password.val().length < 6 || password.val().length > 20) {
            alert("The length of password should be 6~20!");
            password.val("");
            password.focus();
            return false;
        } else {
        	getMD5EncryptedPassWord();
            return true;
        }
    });
    
    // generate a MD5 encrypted password
    function getMD5EncryptedPassWord(){
    	var password = $("form .password").val();   	
    	$("form .password").val(hex_md5(password));
    }
    
    // generate a new verification code every time the verification picture is clicked.
    $(".validationCode_img").click(function(){
    	$(".validationCode_img").attr("src","generateVerificationCodeAndBGImage?" + Math.random());
    });
    
    
    // Judge if the field of the verification code is empty or not.
    // Sending the request to server side to judge if the verification code is correct or not.
    $(".login_window form input[type=submit]").click(function() {
    	
    	// get the value of the verification field
    	var inputCode = $(".login_window form input[name=validationCode]").val();
    	inputCode = inputCode.trim();
    	
    	// check if the verification field is empty or not.
    	if(inputCode == null || inputCode == ""){
    		alert("Verification code is not allowed to be empty!");
    		$(".login_window form input[name=validationCode]").focus();
    		return false;
    	}
 
    	// send to server side for verification
    	var verifyResult = false;
    	$.post(realPath + "/admin/getVerificationCode", {
    		inputCode: inputCode,
    	}, function(data, status){      		
    		// trim() is important, cz there might be a hidden character added in transforming around "data". The hidden character can be seen in below way:
    		// alert(encodeURIComponent(data));
    		// alert(encodeURIComponent("All Done"));
    		if((status == "success") && (data.trim() == "Passed")) {
    			verifyResult = true;
    		}else{
    			alert(data.trim());
    			$(".validationCode_img").attr("src","generateVerificationCodeAndBGImage?" + Math.random());
    			$(".login_window form input[name=validationCode]").val("");
    			$(".login_window form input[name=validationCode]").focus();
    			verifyResult = false;
    		}
    	});
    	
    	return verifyResult;
    });
    
});