<%-- 
    Document   : index
    Created on : Aug 5, 2008, 5:48:24 PM
    Author     : jkaplan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h3>Wonderland File Systems (WFS)</h3>
        <table>
            <%@ page import="org.jdesktop.wonderland.service.wfs.WFSManager" %>
            <% String roots[] = WFSManager.getWFSManager().getWFSRoots(); %>
            <% for (String root : roots) { %>
            <tr>
                <td><%= root%></td>
            </tr>
            <% } %>
        </table>
    </body>
</html>
