<%-- 
    Document   : index
    Created on : Aug 7, 2008, 4:31:15 PM
    Author     : jkaplan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<body>
    <%@ page import="org.jdesktop.wonderland.web.asset.deployer.AssetDeployer" %>
    <%@ page import="org.jdesktop.wonderland.web.asset.deployer.AssetDeployer.DeployedAsset" %>
    <%@ page import="java.util.Map" %>
    <%@ page import="java.util.Iterator" %>
    <%@ page import="java.io.File" %>
    
    <h3>Installed Artwork</h3>
    <%
        Map<DeployedAsset, File> map = AssetDeployer.getFileMap();
        Iterator<Map.Entry<DeployedAsset, File>> it = map.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<DeployedAsset, File> entry = it.next();
            DeployedAsset asset = entry.getKey();
            File file = entry.getValue();
    %>
        <%= asset.moduleName%> <%= asset.assetType%> <%=file.getAbsolutePath()%><br>
        <%
        }
        %>
</body>
</html>
