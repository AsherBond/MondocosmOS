<%-- 
    Document   : installFailed.jsp
    Created on : Sep 23, 2008, 1:32:21 PM
    Author     : jordanslott
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="/wonderland-web-front/javascript/prototype-1.6.0.3.js" type="text/javascript"></script>
        <link href="/wonderland-web-front/css/base.css" rel="stylesheet" type="text/css" media="screen" />
        <link href="/wonderland-web-front/css/module.css" rel="stylesheet" type="text/css" media="screen" />
        <title>Install Failed</title>
    </head>
    <body>
        <h2>Module Install Failed</h2>
        The module installation failed for the following reason:
        <br><br>
        <%= request.getAttribute("errorMessage")%>
        <br><br>
        Please consult the server logs for more information.
        <br>
        <div id="actionLinks">
            <a href="/wonderland-web-front/admin?pageURL=/wonderland-web-modules/editor" target="_top">OK</a>
        </div>
    </body>
</html>
