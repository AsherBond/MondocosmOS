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
        <title>Edit Placemarks</title>
    </head>
    <body>
        <h2>Edit Placemarks</h2>
        <table class="installed" id="runnerTable">
            <caption>
                <span class="heading">Server Placemarks</span>
            </caption>
            <tr class="header">
                <td class="installed">Name</td>
                <td class="installed">Server URL</td>
                <td class="installed">Transport Location</td>
                <td class="installed">Look Direction</td>
                <td class="installed">Actions</td>
            </tr>
            <c:forEach var="entry" items="${requestScope['entries']}">
                <tr>
                    <td class="installed">${entry.name}</td>
                    <td class="installed">${entry.url}</td>
                    <td class="installed">(${entry.x}, ${entry.y}, ${entry.z})</td>
                    <td class="installed">${entry.angle} degrees</td>
                    <td class="installed">
                <c:forEach var="action" items="${entry.actions}">
                    <a href="${pageContext.servletContext.contextPath}/browse?action=${action.url}">${action.name}</a>
                </c:forEach>
                </td>
                </tr>
            </c:forEach>
        </table>
        <div id="actionLinks">
            <a href="/placemarks/wonderland-placemarks/add.jsp">Add Placemark</a>
        </div>
    </body>
</html>
