/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.contentrepo.client.ui;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A table model used to display entries in a directory: an icon for the file
 * or directory, its name, its size, and its last modified date.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class AsyncTableModel extends AbstractTableModel {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/Bundle");
    private List<ContentNode> nodeList = null;
    protected Object dirIcon = null;
    protected Object fileIcon = null;

    /** Default constructor */
    public AsyncTableModel() {
        dirIcon = UIManager.get("FileChooser.directoryIcon");
        fileIcon = UIManager.get("FileChooser.fileIcon");
    }

    /**
     * Sets the current list of content nodes to display.
     *
     * @param nodeList A List of ContentNode objects
     */
    public void setNodeList(List<ContentNode> nodeList) {
        this.nodeList = nodeList;
        fireTableDataChanged();
    }

    /**
     * Returns the nth content node in the list.
     *
     * @param n The row number
     * @return The desired ContentNode
     */
    public ContentNode getContentNode(int n) {
        return nodeList.get(n);
    }

    /**
     * @inheritDoc()
     */
    public int getRowCount() {
        if (nodeList == null) {
            return 0;
        }
        return nodeList.size();
    }

    /**
     * @inheritDoc()
     */
    public int getColumnCount() {
        return 4;
    }

    /**
     * @inheritDoc()
     */
    public Object getValueAt(int row, int column) {
        // First fetch the content node given the row, it should be there
        ContentNode node = nodeList.get(row);

        // Then return the proper type depending upon the column
        switch (column) {
            case 0:
                return (node instanceof ContentCollection) ? dirIcon : fileIcon;

            case 1:
                return node.getName();

            case 2:
                if (node instanceof ContentResource) {
                    return "" + ((ContentResource) node).getSize();
                }
                return "";

            case 3:
                if (node instanceof ContentResource) {
                    Date lastModified =
                            ((ContentResource) node).getLastModified();
                    DateFormat df = DateFormat.getDateInstance();
                    return df.format(lastModified);
                }
                return "";

            default:
                break;
        }
        return null;
    }

    /**
     * @inheritDoc()
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "";
            case 1:
                return BUNDLE.getString("Name");
            case 2:
                return BUNDLE.getString("Size");
            case 3:
                return BUNDLE.getString("Last_Modified");
            default:
                return "unknown";
        }
    }

    /**
     * @inheritDoc()
     */
    @Override
    public Class getColumnClass(int column) {
        if (column == 0) {
            return getValueAt(0, column).getClass();
        } else {
            return super.getColumnClass(column);
        }
    }
}                   
