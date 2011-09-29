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
    var groupId = "${requestScope['id']}";
    var create = "${requestScope['create']}";
    var group;

    function updateMembers() {
        if (create == "true") {
            group = new Object();
            group.id = groupId;
            group.members = [];
            refresh();
        } else {
            new Ajax.Request('resources/groups/' + groupId, {
                method:'get',
                requestHeaders: { Accept:'application/json' },
                onSuccess: function(response) {
                    group = response.responseText.evalJSON(true);

                    if (group.members.length == undefined || 
                        group.members.length == 1)
                    {
                        var member = group.members;
                        group.members = [ member ];
                    }

                    refresh();
                }
            });
        }
    }
    
    function refresh() {
        for (var i = 0; i < group.members.length; i++) {
            updateMember(group.members[i], i);
        }
        
        var lastRow = $('memberTable').down('tr', group.members.length);
        while (lastRow.next() != null) {
            lastRow.next().remove();
        }
    }

    function addMember() {
        var newMember = new Object();
        newMember.id = "";
        newMember.owner = "false";
        group.members.push(newMember);
        refresh();
    }
    
    function updateMember(member, index) {
        var links = [];
        processMember(member, index, links);
        
        var row = $('memberTable').down('tr', index + 1);
        if (row == null) {
            row = new Element('tr');
            $('memberTable').insert(row);
        } else {
            row.update();
        }
        
        row.insert(new Element('td', { 'class': 'installed' }));
        row.insert(new Element('td', { 'class': 'installed' }));
        row.insert(new Element('td', { 'class': 'installed' }));
        
        var name = row.down('td', 0);
        name.update();
        name.insert(new Element('input', { 'type': 'text', 'name': 'id',
            'value': member.id,
            'onChange': 'changeMember(' + index + ')' }));

        var owner = row.down('td', 1);
        owner.update();
        owner.insert(new Element('input', { 'type': 'checkbox', 'name': 'owner',
            'checked': (member.owner == "true"),
            'onChange': 'changeMember(' + index + ')'}));

        var actions = row.down('td', 2);
        actions.update();
        for (var i = 0; i < links.length; i++) {
            actions.insert(links[i]);
            actions.insert(' ');
        }
    }

    function changeMember(index) {
        var row = $('memberTable').down('tr', index + 1);
        
        var id = row.down('td', 0).down('input', 0);
        group.members[index].id = id.getValue();

        var owner = row.down('td', 1).down('input', 0);
        if (owner.getValue() != null) {
            group.members[index].owner = "true";
        } else {
            group.members[index].owner = "false";
        }
    }

    function removeMember(index) {
        group.members.splice(index, 1);
        refresh();
    }

    function processMember(member, index, links) {
        links.push(new Element('a', { 'href': 'javascript:void(0);',
            'onclick': 'removeMember(\'' + index + '\')' }).update("delete"));
    }

    function submitForm() {
        // remove any groups with no name
        var remove = [];
        for (var i = 0; i < group.members.length; i++) {
            var id = group.members[i].id;
            if (id == null || id.empty()) {
                remove.push(i);
            }
        }

        for (var i = 0; i < remove.length; i++) {
            group.members.splice(remove[i], 1);
        }

        new Ajax.Request('resources/groups/' + groupId, {
            method: 'post',
            contentType: 'application/json',
            postBody: Object.toJSON(group),
            onSuccess: function(response) {
                parent.location.replace("/wonderland-web-front/admin?pageURL=/security-groups/security-groups/editor");
            },
            onFailure: function(response) {
                alert("Error " + response.statusText + "<br>" +
                    response.responseText);
            }
        });
    }
        </script>
    </head>
    <body onload="updateMembers();">
        <h2>Group Members for ${requestScope['id']}</h2>

        <form id="memberForm" action="javascript:void(0)">
            <table class="installed" id="memberTable">
                <tr class="header">
                    <td class="installed"><b>Name</b></td>
                    <td class="installed"><b>Owner</b></td>
                    <td class="installed"><b>Actions</b></td>
                </tr>
            </table>
            <div id="actionLinks">
                <a href="/wonderland-web-front/admin?pageURL=/security-groups/security-groups/editor" target="_top">Cancel</a>
                <a href="javascript:void(0)" onclick="addMember()">Add Group Member</a>
                <a href="javascript:void(0)" onclick="submitForm()">Save</a>
            </div>
        </form>
    </body>
