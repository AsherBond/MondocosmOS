<%-- 
    Document   : configRemove
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
        <title>Confirm Remove</title>
    </head>
    <body>
        <form id="removeForm" action="/wonderland-web-modules/editor">
            <%@ page import="java.util.Arrays" %>
            <input type="hidden" name="action" value="remove"/>
            <input type="hidden" name="confirm" value="false"/>
            <%
            String[] removed = request.getParameterValues("remove");
            for (String name : removed) {
            %>
            <input type="hidden" name="remove" value="<%=name%>"/>
            <%
            }
            %>
            <h2>Confirm Remove Modules</h2>
            <br>
            Are you sure you wish to remove the following modules? Some modules may
            not be removed until the next restart of the server.
            <br><br>
            <%
            for (String name : removed) {
            %>
            <%=name%><br>
            <%
            }
            %>
            <br>
            <div id="actionLinks">
                <a href="/wonderland-web-front/admin?pageURL=/wonderland-web-modules/editor" target="_top">Cancel</a>
                <a href="javascript:void(0)" onclick="$('removeForm').submit()">OK</a>
            </div>
        </form>
    </body>
</html>
