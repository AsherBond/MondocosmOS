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
package org.jdesktop.wonderland.testharness.manager.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.testharness.manager.common.CommsHandler;
import org.jdesktop.wonderland.testharness.manager.common.ManagerMessage;
import org.jdesktop.wonderland.testharness.manager.common.CommsHandler.MessageListener;
import org.jdesktop.wonderland.testharness.manager.common.SimpleTestDirectorMessage;
import org.jdesktop.wonderland.testharness.manager.common.SimpleTestDirectorMessage.UserActionType;
import org.jdesktop.wonderland.testharness.master.UsernameManager;

/**
 *
 * @author  paulby
 */
public class SimpleTestDirectorUI extends javax.swing.JPanel {

    private CommsHandler commsHandler;
    private final HashMap<String, UserJPanel> users = new HashMap();
    
    /** Creates new form SimpleDirectorUI */
    public SimpleTestDirectorUI(CommsHandler commsHandlerIn) {
        this.commsHandler = commsHandlerIn;
        initComponents();
        
        commsHandler.addMessageListener(SimpleTestDirectorMessage.class, new MessageListener()  {

            public void messageReceived(ManagerMessage msg) {
                assert(msg instanceof SimpleTestDirectorMessage);
                
                SimpleTestDirectorMessage message = (SimpleTestDirectorMessage) msg;
                
                System.err.println("Got message "+msg);
                switch(message.getMessageType()) {
                    case UI_UPDATE :
                        actualUsersTF.setText(Integer.toString(message.getUserCount()));
                        desiredUserCount.getModel().setValue(new Integer(message.getDesiredUserCount()));
                        break;
                    case USER_ADDED :
                        synchronized(users) {
                            final UserJPanel p = new UserJPanel(message.getUsername(), commsHandler);
                            users.put(p.getUsername(), p);
                            System.err.println("Addded USER "+p.getUsername());
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    usersPanel.add(p);
                                    usersPanel.invalidate();
                                    usersPanel.validate();
                                }
                            });
                        }
                        break;
                    case USER_REMOVED :
                        synchronized(users) {
                            final UserJPanel p1 = users.remove(message.getUsername());
                            System.err.append("USER REMOVED "+p1+"  "+message.getUsername());
                            if (p1!=null) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        usersPanel.remove(p1);
                                        usersPanel.invalidate();
                                        usersPanel.validate();
                                        usersPanel.repaint();
                                        System.err.println("UsersPanel children "+usersPanel.getComponents().length);
                                    }
                                });
                            }
                        }
                        break;
                    case CLIENT_QUIT :
                        synchronized(users) {
                            UserJPanel p2 = users.get(message.getUsername());
                            if (p2!=null) {
                                // setClientQuit is Swing thread safe
                                p2.setClientQuit();
                            } else {
                                System.err.println("Got Quit message for unknown user "+message.getUsername());
                            }
                        }
                        break;
                    case USER_LIST :
                        synchronized(users) {
                            String[] allUsernames = message.getAllUsernames();
                            UserActionType[] allUserActions = message.getAllUserActions();
                            final ArrayList<UserJPanel> newPanels = new ArrayList();
                            for(int i=0; i<allUsernames.length; i++) {
                                UserJPanel up = users.get(allUsernames[i]);
                                if (up==null) {
                                    up = new UserJPanel(allUsernames[i], commsHandler);
                                    newPanels.add(up);
                                    up.setCurrentAction(allUserActions[i]);
                                } else {
                                    // TODO this needs to be an invokeLater
                                    up.setCurrentAction(allUserActions[i]);
                                }
                            }

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    for(UserJPanel p : newPanels) {
                                        usersPanel.add(p);
                                    }
                                    usersPanel.invalidate();
                                    usersPanel.validate();
                                }
                            });
                        }
                        break;
                    default :
                        System.err.println("Unexpected message "+message.getMessageType());
                }
            }
            
        });
        
        try {
            commsHandler.send(SimpleTestDirectorMessage.newRequestStatusMessage());   // Request current status
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        actualUsersTF = new javax.swing.JTextField();
        desiredUserCount = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usersPanel = new javax.swing.JPanel();
        applyB = new javax.swing.JButton();

        jLabel1.setText("Desired Users :");
        jLabel1.setFocusable(false);

        actualUsersTF.setEditable(false);
        actualUsersTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        actualUsersTF.setText("0");
        actualUsersTF.setFocusable(false);

        jLabel2.setText("Actual Users :");
        jLabel2.setFocusable(false);

        usersPanel.setLayout(new java.awt.GridLayout(3, 6, 3, 3));
        jScrollPane1.setViewportView(usersPanel);

        applyB.setText("Apply");
        applyB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyBActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(desiredUserCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 153, Short.MAX_VALUE)
                                .add(applyB))
                            .add(actualUsersTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(applyB)
                        .add(desiredUserCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(actualUsersTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .add(41, 41, 41))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void applyBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBActionPerformed
        int userCount = ((Integer) desiredUserCount.getModel().getValue()).intValue();
        System.out.println("State changed: " + userCount);
        try {
            commsHandler.send(SimpleTestDirectorMessage.newDesiredUserCountMessage(userCount));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_applyBActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField actualUsersTF;
    private javax.swing.JButton applyB;
    private javax.swing.JSpinner desiredUserCount;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel usersPanel;
    // End of variables declaration//GEN-END:variables

}
