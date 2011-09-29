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
        <link href="runner.css" rel="stylesheet" type="text/css" media="screen" />
        <script src="/wonderland-web-front/javascript/prototype-1.6.0.3.js" type="text/javascript"></script>
        <script type="text/javascript">
            function selectLocal() {
                var locationTF = $('locationField');
                locationTF.disable();
                locationTF.value = "localhost";
            }

            function selectRemote() {
                var locationTF = $('locationField');
                locationTF.enable();
                locationTF.value = "remote";
            }
        </script>
    </head>
    <body>
        <h3>Add Component</h3>
        <form action="run">
            <input type="hidden" name="action" value="addRunnerForm"/>

            <c:if test="${not empty requestScope['error']}">
                <font color="red">${requestScope['error']}"</font>
            </c:if>

            <table>
                <tr>
                    <td>Component Name:</td>
                    <td><input type="text" name="name" size="50" value="${requestScope['entry'].runnerName}"/></td>
                </tr>
                <tr>
                    <td>Component Class:</td>
                    <td><input type="text" name="class" size="50" value="${requestScope['entry'].runnerClass}"/></td>
                </tr>
                <tr>
                    <td>Location:</td>
                    <td><input type="radio" name="Location" value="Local" checked="true" onclick="javascript:selectLocal()">Local
                        <input type="radio" name="Location" value="Remote" onclick="javascript:selectRemote()">Remote</td>
                </tr>
                <tr>
                    <td></td>
                    <td><input id="locationField" disabled="true" type="text"
                               name="location" size="50" value="${requestScope['entry'].location}"/></td>
                </tr>
                <tr>
                    <td><input type="submit" name="button" value="OK"/>
                        <input type="submit" name="button" value="Cancel"/>
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>