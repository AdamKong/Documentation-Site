# Cisco Webex Teams Docs

This is a simple usage introduction to this Cisco Webex Teams Docs Web Site!

## Front End:

1. The latest article in "Hot Article" category (at the bottom of the side menu) will be showed up in the home page. That means if you create an article in the category, the home page will be updated to reflect the new hot article.
	
2. If you create a new category, it will be showed up above the "Hot Article" category. The "Hot Article" category will be always at the bottom of the side menu.

3. For the "Search" in the home page, if you leave the keywords field as empty, it will show up all the articles in the category. If you put the category as "All" as well, it will show up all of the articles in all categories in the web site.
	 
4. Clicking the "more" button after a category will list out all of the articles in the category.


## Back End:

1. The admin home page shows some basic information of the hosting web server and the user (administrator).

2. The admin portal allows administrators to create/search/modify/disable-enable/delete articles.

3. The admin portal allows administrators to create/search/modify/delete categories. A category can not be deleted unless all of the articles (including the ones disabled) in it are moved away.

4. The admin portal allows super administrators to create/search/modify/disable-enable/delete sub administrator. A super administrator can NOT operate another super administrator account, and it can only operate sub administrators.
   
5. The admin portal allows administrators to view and change their own account details, i.e changing password. Super administrators have the privileges to change the profiles of any sub administrators, including password.

6. Any changes to articles/categories will refresh the home page and relevant article pages. They're all HTML page, so clients can fetch them directly (no executing time in servers side compared to dynamic pages, i.e .jsp .php pages).

7. An admin account can only be used to log in once a time. The people who use an already logged-in account will not be able to log in. If an admin finishes their operation, the best practice would be clicking the "sign out" button to quit. If you just close the window tags, you won't be able to sign in from another computer until the session expires. (Session Expiration time is set to 20 minutes currently).
