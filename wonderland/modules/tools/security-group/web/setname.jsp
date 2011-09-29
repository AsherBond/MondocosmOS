<%-- 
    Document   : setname
    Created on : Mar 17, 2009, 9:01:58 AM
    Author     : jkaplan
--%>

<%@page contentType="text/html" pageEncoding="MacRoman"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=MacRoman">
        <script src="/wonderland-web-front/javascript/prototype-1.6.0.3.js" type="text/javascript"></script>
        <link href="/wonderland-web-front/css/base.css" rel="stylesheet" type="text/css" media="screen" />
        <link href="/wonderland-web-front/css/module.css" rel="stylesheet" type="text/css" media="screen" />
        <title>Group Name</title>
    </head>
    <body>
        <h2>Group Name</h2>

        <form id="nameForm" action="/security-groups/security-groups/editor">
            <input type="hidden" name="action" value="edit"/>
            <input type="hidden" name="create" value="true"/>

            Group name: <input type="text" name="id"/>
            <div id="actionLinks">
                <a href="javascript:void(0)" onclick="$('nameForm').submit()">Edit Group Members</a>
            </div>
        </form>
    </body>
</html>
