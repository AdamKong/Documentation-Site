/*
 * project name:WebexDocsWeb
 * description: javascript functions for the front end pages
 * @author Adam Kong
 * @date 21 May 2018
 * @version 1.0
 */
$(document).ready(function(){

    $(".section_header span:first-child").click(function(){
        if($(this).text() == '+') {
            $(this).text("-");
        } else {
            $(this).text("+");
        }
        $(this).parent().next().slideToggle();

        alignLineBetweenAsideAndMain();
    });

    $(".header_middle").find("li").hover(function(){
        $(this).attr("style","background-color: silver");
    }, function(){
        $(this).attr("style","background-color: #049FD9");
    });

    $(".searchBox").focusin(function(){
        if($.trim($(this).val()).length === 0){
            $(this).attr("placeholder", "");
        }
    }).focusout(function(){
        if ($.trim($(this).val()).length === 0) {
            $(this).val("");
            $(this).attr("placeholder", "  keywords, 30 chars max, separated by space");
        }
    });
    
    // in home page.
    // form validation - search article form - forefront
    $(".search_form_in_homepage").submit(function() {
        // check the length of the keywords field
    	var isSearchArticleInForefrontKeywordsValid = lengthRestriction(".search_form_in_homepage .searchBox", 0, 30, "The length of the keywords should be 0~30!");
        if (isSearchArticleInForefrontKeywordsValid == false) {
        	return false;
        }
       
        // generate a random value for "requestIdentifier" input
        $(".search_form_in_homepage input[name=requestIdentifier]").val(uuid(32, 61));
    });

    $(".left_menu").find("li").hover(function(){
        $(this).attr("style","background-color: silver");
    }, function () {
        $(this).attr("style","background-color: lightskyblue");
    });

    $(".left_menu").find(".more").hover(function(){
        $(this).attr("style","background-color: #FFFFFF");
    }, function () {
        $(this).attr("style","background-color: lightskyblue");
    });
    
    // for the pagination icon - change the background color if the mouse hover on it.
    $(".article_searchresult_epage_main nav ul a li").hover(function(){
    	if($(this).attr("style") != "color:red; border:1px dashed red;"){
    		$(this).attr("style","background-color: silver");
    	}
    }, function(){
    	if($(this).attr("style") != "color:red; border:1px dashed red;"){
    		$(this).removeAttr("style");
    	}
    });
    

    function alignLineBetweenAsideAndMain() {
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
    
    // For "Read All Article" feature    
    // var widHeight = $(window).height();
    var widHeight = $(window).height();
    var multiples = 2.1;
    if($("aside").length>0){
    	widHeight = $("aside").height();
    	multiples = 1.0;
    }
	var artHeight = $('.content').height();
	
	if(artHeight>(widHeight*multiples)){
		// $('.content').height(widHeight*multiples-300).css({'overflow':'hidden'});
		$('.content').height(widHeight*multiples-450).css({'overflow':'hidden'});
		var article_show = true;
		// $('.read_more_btn').on('click',bindRead_more);
		$('.read_more_btn').click(bindRead_more);
	}else{
		article_show = true;
		$('.readall_box').hide().addClass('readall_box_nobg');
	}
	function bindRead_more(){
		if(!article_show){
			$('.content').height(widHeight*multiples).css({'overflow':'hidden'});
			$('.readall_box').show().removeClass('readall_box_nobg');
			article_show = true;
		}else{
			$('.content').height("").css({'overflow':'hidden'});
			$('.readall_box').show().addClass('readall_box_nobg');
			$('.readall_box').hide().addClass('readall_box_nobg');
			article_show = false;
		}
	}
	
	$(".read_more_btn").click(function(){
	    alignLineBetweenAsideAndMain();
	});
    
});