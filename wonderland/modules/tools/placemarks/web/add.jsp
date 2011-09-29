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
        <title>Add New Placemark</title>
    </head>
    <body>
        <h1>Add New Placemark</h1>

        <form id="nameForm" action="/placemarks/wonderland-placemarks/browse">
            <input type="hidden" name="action" value="add"/>

            <table>
                <tr>
                    <td>Name:</td>
                    <td><input type="text" name="name"/></td>
                </tr>
                <tr>
                    <td>Server URL:</td>
                    <td><input type="text" name="url"/></td>
                </tr>
                <tr>
                    <td>X:</td>
                    <td><input type="text" name="x"/> (meters)</td>
                </tr>
                <tr>
                    <td>Y:</td>
                    <td><input type="text" name="y"/> (meters)</td>
                </tr>
                <tr>
                    <td>Z:</td>
                    <td><input type="text" name="z"/> (meters)</td>
                </tr>
                <tr>
                    <td>Look Angle:</td>
                    <td><input type="text" name="angle"/> (degrees)</td>
                </tr>
            </table>
            <br><br>
            <a href="javascript:void(0)" onclick="$('nameForm').submit()">Ok</a>
            <a href="/placemarks/wonderland-placemarks/browse?action=view">Cancel</a>
        </form>
    </body>
</html>
