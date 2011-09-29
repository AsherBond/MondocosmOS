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
        <h2>Edit Client Settings</h2>
        <form action="/wonderland-web-front/config/settings">
            <c:set var="window_size" value="${requestScope['window_size']}"/>
            <c:set var="x" value="${requestScope['x']}"/>
            <c:set var="y" value="${requestScope['y']}"/>
            <c:set var="z" value="${requestScope['z']}"/>
            <c:set var="audio_type" value="${requestScope['audio_type']}"/>

            <table class="installed">
                <caption>
                    <span class="heading">Initial Settings</span>
                </caption>
                <tr class="installed">
                    <td class="installed">Window size:</td>
                    <td class="installed">
                        <select name="window_size">
                            <option value="320x200" 
                                    <c:if test="${window_size == '320x200'}">selected="selected"</c:if>>
                                320 x 200
                            </option>
                            
                            <option value="640x480" 
                                    <c:if test="${window_size == '640x480'}">selected="selected"</c:if>>
                                640 x 480
                            </option>
                                                               
                            <option value="800x600"
                                    <c:if test="${window_size == '800x600'}">selected="selected"</c:if>>
                                800 x 600
                            </option>
                            
                            <option value="1280x1024"
                                <c:if test="${window_size == '1280x1024'}">selected="selected"</c:if>>
                                1280 x 1024
                            </option>
                            
                            <option value="fullscreen"
                                <c:if test="${window_size == 'fullscreen'}">selected="selected"</c:if>>
                                fullscreen
                            </option>
                        </select>
                    </td>
                </tr>

                <tr class="installed">
                    <td class="installed">Initial location:</td>
                    <td class="installed">X:<input type="text" name="x" value="${x}">
                                          Y:<input type="text" name="y" value="${y}">
                                          Z:<input type="text" name="z" value="${z}">
                    </td>
                </tr>

                <tr class="installed">
                    <td class="installed">Audio mode:</td>
                    <td class="installed">
                        <select name="audio_type">
                            <option value="unmuted"
                                    <c:if test="${audio_type == 'unmuted'}">selected="selected"</c:if>>
                                Unmuted
                            </option>

                            <option value="muted"
                                    <c:if test="${audio_type == 'muted'}">selected="selected"</c:if>>
                                Muted
                            </option>
                        </select>
                    </td>
                </tr>
            </table>

            <div id="actionLinks">
                <input type="submit" name="button" value="Save"/>
                <input type="submit" name="button" value="Edit Properties"/>
                <input type="submit" name="button" value="Cancel"/>
            </div>
        </form>
    </body>
</html>