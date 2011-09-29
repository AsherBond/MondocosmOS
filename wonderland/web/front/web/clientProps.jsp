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
        <h2>Edit Client Properties</h2>
        <form action="/wonderland-web-front/config/edit">
            <table class="installed">
                <caption>
                    <span class="heading">Properties</span>
                </caption>
                <c:forEach var="prop" items="${requestScope['props']}"
                           varStatus="propStat">
                    <tr class="installed">
                        <td class="installed"><input type="text" size="35" name="key-${propStat.count}" value="${prop.key}"/></td>
                        <td class="installed"><input type="text" size="35" name="value-${propStat.count}" value="${prop.value}"/></td>
                    </tr>
                </c:forEach>
                    <tr class="installed">
                        <td class="installed"><input type="text" size="35" name="key-new"/></td>
                        <td class="installed"><input type="text" size="35" name="value-new"/></td>
                    </tr>
            </table>

            <div id="actionLinks">
                <input type="submit" name="button" value="Add Property"/>
                <input type="submit" name="button" value="Save"/>
                <input type="submit" name="button" value="Cancel"/>
            </div>
        </form>
    </body>
</html>