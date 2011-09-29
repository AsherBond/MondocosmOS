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
<script src="/wonderland-web-front/javascript/prototype-1.6.0.3.js" type="text/javascript"></script>
<script type="text/javascript">
    function updateGroups() {
        new Ajax.Request('resources/groups?members=false', {
            method:'get', 
            requestHeaders: { Accept:'application/json' },
            onSuccess: function(response) {
                var groups = response.responseText.evalJSON(true);
                if (groups.groups.length > 1) {
                    for (var i = 0; i < groups.groups.length; i++) {
                        updateGroup(groups.groups[i], i);
                    }
                } else {
                    updateGroup(groups.groups, 0);
                }
            }
        });
    }
    
    function updateGroup(group, index) {
        processGroup(group);
        
        var row = $('groupTable').down('tr', index + 2);
        if (row == null) {
            row = new Element('tr');
            row.insert(new Element('td', { 'class': 'installed' }));
            row.insert(new Element('td', { 'class': 'installed' }));
            row.insert(new Element('td', { 'class': 'installed' }));
            $('groupTable').insert(row);
        }
        
        row.down('td', 0).update(group.id);
        row.down('td', 1).update(group.memberCount);

        var actions = row.down('td', 2);
        actions.update();
        for (var i = 0; i < group.link.length; i++) {
            actions.insert(group.link[i]);
            actions.insert(' ');
        }
    }
    
    function processGroup(group) {
        group.link = [];

        if (group.editable == "true") {
            group.link.push(new Element('a', { 'href': '/wonderland-web-front/admin?pageURL=/security-groups/security-groups/editor%3faction=edit%26id=' + group.id,
                                          'target': '_top'}).update("edit"));
            group.link.push(new Element('a', { 'href': 'javascript:void(0);',
                                          'onclick': 'removeGroup(\'' + group.id + '\')' }).update("delete"));
        }
    }

    function removeGroup(groupName) {
        new Ajax.Request('resources/groups/' + groupName, {
            method:'delete',
            onSuccess: function(response) {
                window.location.reload();
            }
        });
    }
</script>
</head>
<body onload="updateGroups();">
<h2>Manage Groups</h2>

<table class="installed" id="groupTable">
    <tr class="header">
        <td class="installed">Name</td>
        <td class="installed">Members</td>
        <td class="installed">Actions</td>
    </tr>
</table>
<div id="actionLinks">
     <a href="/security-groups/security-groups/editor?action=edit">Add group</a>
</div>
</body>
