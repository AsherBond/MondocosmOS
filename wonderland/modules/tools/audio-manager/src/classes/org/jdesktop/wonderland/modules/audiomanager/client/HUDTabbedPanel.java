/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.modules.audiomanager.client;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.hud.HUDComponent;

/**
 *
 * @author Ryan Babiuch
 */
public class HUDTabbedPanel extends javax.swing.JPanel
    implements UserListPanel
{

    /** Creates new form HUDTabbedPanel */
    private PresenceControls controls;
    private Cell cell;
    private HUDComponent hudComponent;
    private UserListHUDPanel userListHUDPanel;
    private static boolean configured = false;
    private static HUDTabbedPanel instance;



    private HUDTabbedPanel() {
        initComponents();
    }
    private HUDTabbedPanel(PresenceControls controls, Cell cell) {
        initComponents();

        this.controls = controls;
        this.cell = cell;
        userListHUDPanel = new UserListHUDPanel(controls, cell);
        
        addTab("users", userListHUDPanel);
        if(instance == null) {
            instance = this;
        }


    }
    public void uninitialize() {
        instance = null;
    }

    public static HUDTabbedPanel getInstance() {
        if(instance == null) {            
            instance = new HUDTabbedPanel();

        }
        return instance;
    }

    public UserListHUDPanel getUserListHUDPanel() {
        return this.userListHUDPanel;
    }
    public boolean isConfigured() {
        return configured;
    }
    public void configInstance(PresenceControls controls, Cell cell) {
        this.controls = controls;
        this.cell = cell;
        userListHUDPanel = new UserListHUDPanel(controls, cell);
        this.getTabbedPanel().insertTab("users", null, userListHUDPanel, "Users list", 0);
        //addTab("users", userListHUDPanel);
        userListHUDPanel.setUserList();
        getTabbedPanel().setSelectedComponent(userListHUDPanel);
        configured = true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPanel = new javax.swing.JTabbedPane();

        setPreferredSize(new java.awt.Dimension(194, 300));

        tabbedPanel.setPreferredSize(new java.awt.Dimension(194, 300));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabbedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tabbedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public JTabbedPane getTabbedPanel() {
        return tabbedPanel;
    }


    public void addTab(String caption, JPanel newTab) {
        int index = tabbedPanel.getSelectedIndex();
        tabbedPanel.addTab(caption, newTab);
        tabbedPanel.setSelectedIndex(index);
    }

    public void setHUDComponent(HUDComponent hudComponent) {
        this.hudComponent = hudComponent;
        userListHUDPanel.setHUDComponent(hudComponent);
    }

    public HUDComponent getHUDComponent() {
        return this.hudComponent;
    }

    public void setUserList() {
        userListHUDPanel.setUserList();
    }

    public PresenceControls getPresenceControls() {
        return controls;
    }

    public Cell getCell() {
        return cell;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables

}