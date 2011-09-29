<%-- 
    Document   : index
    Created on : Aug 7, 2008, 4:31:15 PM
    Author     : jkaplan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>

<html>
    <head>
        <link href="/wonderland-web-front/css/base.css" rel="stylesheet" type="text/css" media="screen" />
        <link href="/wonderland-web-front/css/module.css" rel="stylesheet" type="text/css" media="screen" />
    </head>
    <body>
        <h2>Edit Server Components</h2>
        <form action="run">
            <input type="hidden" name="action" value="editRunnersForm"/>
            <table class="installed">
                <caption>
                    <span class="heading">Server Components</span>
                </caption>
                <tr class="header">
                    <td class="installed">Name</td>
                    <td class="installed">Class</td>
                    <td class="installed">Location</td>
                    <td class="installed">Actions</td>
                </tr>
                <c:forEach var="entry" items="${requestScope['entries']}">
                    <tr>
                        <td>${entry.runnerName}</td>
                        <td>${entry.runnerClass}</td>
                        <td>${entry.location}</td>
                        <td><a href="/wonderland-web-front/admin?pageURL=/wonderland-web-runner/run%3faction=removeRunner%26name=${entry.runnerName}" target="_top">remove</a></td>
                    </tr>
                </c:forEach>
            </table>
            <div id="actionLinks">
                <a href="/wonderland-web-front/admin?pageURL=/wonderland-web-runner/run%3faction=addRunner" target="_top">Add component</a>
                <div style="float: right;">
                    <input type="submit" name="button" value="Save"/>
                    <input type="submit" name="button" value="Restore Defaults"/>
                    <input type="submit" name="button" value="Cancel"/>
                </div>
            </div>
        </form>
    </body>
</html>