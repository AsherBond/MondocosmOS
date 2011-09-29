<%-- 
    Document   : browse.jsp
    Created on : Dec 14, 2008, 12:11:54 PM
    Author     : jkaplan
--%>

<%@page contentType="text/html" pageEncoding="MacRoman"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <link href="/wonderland-web-front/css/base.css" rel="stylesheet" type="text/css" media="screen" />
        <link href="/wonderland-web-front/css/module.css" rel="stylesheet" type="text/css" media="screen" />
        <title>Manage Apps</title>
    </head>
    <body>
        <h2>Manage Apps</h2>
        <p>Use this page to configure desktop (X11) applications (such as Firefox) that will be shared by all users.</p>
        <table class="installed" id="runnerTable">
            <tr class="header">
                <td class="installed">App Name</td>
                <td class="installed">Command</td>
                <td class="installed">Actions</td>
            </tr>
            <c:forEach var="entry" items="${requestScope['entries']}">
                <tr>
                    <td class="installed">${entry.appName}</td>
                    <td class="installed">${entry.command}</td>
                    <td class="installed">
                <c:forEach var="action" items="${entry.actions}">
                    <a href="${pageContext.servletContext.contextPath}/browse${entry.path}?action=${action.url}">${action.name}</a>
                </c:forEach>
                </td>
                </tr>
            </c:forEach>
        </table>
        <div id="actionLinks">
            <a href="/xapps-config/wonderland-xapps-config/add.jsp">Add App</a>
        <div>
    </body>
</html>
