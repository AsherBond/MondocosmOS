<%-- 
    Document   : admin
    Created on : Fri Aug 28 11:40:20 EDT 2009 @694 /Internet Time/
    Author     : gritchie
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Open Wonderland Server Administration</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="keywords" content="Open Wonderland, Virtual World, Open Source" />
    <meta name="description" content="Open Wonderland Server Administration" />
    <link href="css/base.css" rel="stylesheet" type="text/css" media="screen" />
    <link href="css/admin.css" rel="stylesheet" type="text/css" media="screen" />
    <script type="text/javascript">
            function resizeIframe() {
                var height = document.documentElement.clientHeight;
                height -= document.getElementById('contentFrame').offsetTop;
                height -= 0; // bottom margin
    
                document.getElementById('contentFrame').style.height = height + "px"; 
            };
    
            window.onresize = resizeIframe;
    </script>
    <!--[if lt IE 7]>
	<link href="css/patch.css" rel="stylesheet" type="text/css" />
	<![endif]-->

  </head>

  <body onload="resizeIframe()">
    <div id="page">
      <div id="head">
        <a href="http://openwonderland.org"><img alt="Open Wonderland logo" src="images/idy_admin.png" /></a><h1>Server Admin</h1>
      </div>

        <div id="moduleMenu">
          <ul id="navlist">
            <c:forEach var="adminPage" items="${requestScope['adminPages']}">
                <c:choose>
                    <c:when test="${adminPage.absolute}">
                        <li><a href="${adminPage.url}">${adminPage.displayName}</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="admin?pageURL=${adminPage.url}">${adminPage.displayName}</a></li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
          </ul>

          <div id="footer">
            <p id="serverInfo">Server: <%= request.getLocalName()%>, Port: <%= request.getLocalPort()%><br />
             Version: ${requestScope['version'].version} (rev. ${requestScope['version'].revision})<br />
            </p>
          </div>
        </div>
		
      <div id="content">    
        <iframe id="contentFrame" frameborder="0" width="100%" height="100%" src="${requestScope['pageURL']}" name="content"/>
      </div>
    </div>
  </body>
</html>


