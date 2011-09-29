<%-- 
    Document   : snapshots
    Created on : Jan 4, 2009, 11:33:13 AM
    Author     : jkaplan
--%>

<%@page contentType="text/html" pageEncoding="MacRoman"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/fmt.tld" prefix="fmt" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=MacRoman">
        <link href="/wonderland-web-front/css/base.css" rel="stylesheet" type="text/css" media="screen" />
        <link href="/wonderland-web-front/css/module.css" rel="stylesheet" type="text/css" media="screen" />
        <title>Edit Snapshot</title>
    </head>
    <body>
        <h3>Edit Snapshot</h3>

        <c:set var="snapshot" value="${requestScope['snapshot']}"/>
        <form action="SnapshotManager" method="post">
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="root" value="snapshots/${snapshot.name}"/>

            <table>
                <tr>
                    <td><b>Name</b></td>
                    <td><input type="text" name="name" value="${snapshot.name}"/></td>
                </tr>
                <tr>
                    <td><b>Description</b></td>
                    <td><input type="text" name="description" value="${snapshot.description}"/></td>
                </tr>
                <tr>
                    <td colspan="2" align="right"><input type="submit" value="Save" name="save"/>
                                                  <input type="submit" value="Cancel" name="cancel"/>
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>
