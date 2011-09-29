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
package org.jdesktop.wonderland.modules.security.client;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.modules.security.common.ActionDTO;
import org.jdesktop.wonderland.modules.security.common.CellPermissions;
import org.jdesktop.wonderland.modules.security.common.Permission;
import org.jdesktop.wonderland.modules.security.common.Principal;
import org.jdesktop.wonderland.modules.security.common.Principal.Type;
import org.jdesktop.wonderland.modules.security.common.SecurityComponentServerState;

/**
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch> 
 */
@PropertiesFactory(SecurityComponentServerState.class)
public class SecurityComponentProperties extends JPanel
        implements PropertiesFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/security/client/Bundle");
    private CellPropertiesEditor editor = null;
    private PermTableModel perms = new PermTableModel();
    private DefaultMutableTreeNode edit =
            new DefaultMutableTreeNode("Permissions");
    // The original permissions before any editing took place
    private CellPermissions originalCellPermissions = null;

    /** Creates new form SecurityComponentProperties */
    public SecurityComponentProperties() {
        // Initialize the GUI
        initComponents();

        editPermPermCombo.setModel(new DefaultComboBoxModel(new String[]{
                    BUNDLE.getString("Unspecified"),
                    BUNDLE.getString("Granted"),
                    BUNDLE.getString("Denied")}));

        permsTable.setModel(perms);
        permsTable.getSelectionModel().addListSelectionListener(
                new RemoveButtonSelectionListener());
        permsTable.getSelectionModel().addListSelectionListener(
                new EditButtonSelectionListener());
        perms.addTableModelListener(new TableDirtyListener());

        // resize columns optimally
        for (int i = 0; i < permsTable.getColumnCount(); i++) {
            TableColumn tc = permsTable.getColumnModel().getColumn(i);
            switch (i) {
                case 1:
                    tc.setPreferredWidth(120);
                    break;
                case 2:
                    tc.setPreferredWidth(80);
                    break;
                default:
                    tc.setPreferredWidth(100);
                    break;
            }
        }

        editPermsTree.setModel(new DefaultTreeModel(edit));
        editPermsTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                if (path == null) {
                    editPermPermCombo.setEnabled(false);
                    editPermDescription.setText(
                            BUNDLE.getString("Choose_Permission"));
                    return;
                }

                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) path.getLastPathComponent();
                ActionHolder action = (ActionHolder) node.getUserObject();
                if (action.getAccess() == null) {
                    editPermPermCombo.setSelectedIndex(0);
                } else if (action.getAccess() == Permission.Access.GRANT) {
                    editPermPermCombo.setSelectedIndex(1);
                } else if (action.getAccess() == Permission.Access.DENY) {
                    editPermPermCombo.setSelectedIndex(2);
                }

                editPermPermCombo.setEnabled(true);
                editPermDescription.setText(action.getAction().getToolTip());
            }
        });

        addNameTF.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                checkButton();
            }

            public void removeUpdate(DocumentEvent e) {
                checkButton();
            }

            public void changedUpdate(DocumentEvent e) {
                checkButton();
            }

            private void checkButton() {
                String text = addNameTF.getText();
                addOKButton.setEnabled(text != null && text.length() > 0);
            }
        });
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return BUNDLE.getString("Security");
    }

    /**
     * @inheritDoc()
     */
    public JPanel getPropertiesJPanel() {
        return this;
    }

    /**
     * @inheritDoc()
     */
    public void setCellPropertiesEditor(CellPropertiesEditor editor) {
        this.editor = editor;
    }

    /**
     * @inheritDoc()
     */
    public void open() {
        CellServerState cellServerState = editor.getCellServerState();
        SecurityComponentServerState state =
                (SecurityComponentServerState) cellServerState.getComponentServerState(
                SecurityComponentServerState.class);

        // set the lists up based on the model
        originalCellPermissions = state.getPermissions();
        perms.fromPermissions(originalCellPermissions);
        permsTable.repaint();
    }

    /**
     * @inheritDoc()
     */
    public void close() {
        // Do nothing
    }

    /**
     * @inheritDoc()
     */
    public void apply() {
        // Figure out whether there already exists a server state for the
        // component.
        CellServerState cellServerState = editor.getCellServerState();
        SecurityComponentServerState state =
                (SecurityComponentServerState) cellServerState.getComponentServerState(
                SecurityComponentServerState.class);
        if (state == null) {
            state = new SecurityComponentServerState();
        }

        // Update the permissions state and add to the update list
        CellPermissions out = perms.toPermissions();
        state.setPermissions(out);
        editor.addToUpdateList(state);
    }

    /**
     * @inheritDoc()
     */
    public void restore() {
        // Restore the GUI to the original values
        perms.fromPermissions(originalCellPermissions);
        permsTable.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addPrincipalDialog = new javax.swing.JDialog();
        jLabel2 = new javax.swing.JLabel();
        addUserRB = new javax.swing.JRadioButton();
        addGroupRB = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        addNameTF = new javax.swing.JTextField();
        addCancelButton = new javax.swing.JButton();
        addOKButton = new javax.swing.JButton();
        addSearchButton = new javax.swing.JButton();
        addBG = new javax.swing.ButtonGroup();
        editPermsDialog = new javax.swing.JDialog();
        editPermsOKButton = new javax.swing.JButton();
        editPermsCancelButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        editPermsTree = new javax.swing.JTree();
        editPermPermCombo = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        editPermDescription = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        editPermsPrincipalLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        permsTable = new javax.swing.JTable();
        editButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/security/client/Bundle"); // NOI18N
        jLabel2.setText(bundle.getString("SecurityComponentProperties.jLabel2.text")); // NOI18N

        addBG.add(addUserRB);
        addUserRB.setSelected(true);
        addUserRB.setText(bundle.getString("SecurityComponentProperties.addUserRB.text")); // NOI18N

        addBG.add(addGroupRB);
        addGroupRB.setText(bundle.getString("SecurityComponentProperties.addGroupRB.text")); // NOI18N

        jLabel3.setText(bundle.getString("SecurityComponentProperties.jLabel3.text")); // NOI18N

        addCancelButton.setText(bundle.getString("SecurityComponentProperties.addCancelButton.text")); // NOI18N
        addCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCancelButtonActionPerformed(evt);
            }
        });

        addOKButton.setText(bundle.getString("SecurityComponentProperties.addOKButton.text")); // NOI18N
        addOKButton.setEnabled(false);
        addOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOKButtonActionPerformed(evt);
            }
        });

        addSearchButton.setText(bundle.getString("SecurityComponentProperties.addSearchButton.text")); // NOI18N
        addSearchButton.setEnabled(false);

        org.jdesktop.layout.GroupLayout addPrincipalDialogLayout = new org.jdesktop.layout.GroupLayout(addPrincipalDialog.getContentPane());
        addPrincipalDialog.getContentPane().setLayout(addPrincipalDialogLayout);
        addPrincipalDialogLayout.setHorizontalGroup(
            addPrincipalDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(addPrincipalDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(addPrincipalDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(addPrincipalDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(addPrincipalDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(addPrincipalDialogLayout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(addUserRB)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(addGroupRB)
                                .add(178, 178, 178))
                            .add(addPrincipalDialogLayout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(addNameTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(addSearchButton)
                                .addContainerGap()))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, addPrincipalDialogLayout.createSequentialGroup()
                            .add(addCancelButton)
                            .addContainerGap()))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, addPrincipalDialogLayout.createSequentialGroup()
                        .add(addOKButton)
                        .add(99, 99, 99))))
        );
        addPrincipalDialogLayout.setVerticalGroup(
            addPrincipalDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(addPrincipalDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(addPrincipalDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(addNameTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addSearchButton))
                .add(addPrincipalDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, addPrincipalDialogLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(addPrincipalDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(addUserRB)
                            .add(addGroupRB))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(addOKButton))
                    .add(addCancelButton))
                .addContainerGap())
        );

        editPermsOKButton.setText(bundle.getString("SecurityComponentProperties.editPermsOKButton.text")); // NOI18N
        editPermsOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPermsOKButtonActionPerformed(evt);
            }
        });

        editPermsCancelButton.setText(bundle.getString("SecurityComponentProperties.editPermsCancelButton.text")); // NOI18N
        editPermsCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPermsCancelButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(editPermsTree);

        editPermPermCombo.setEnabled(false);
        editPermPermCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPermPermComboActionPerformed(evt);
            }
        });

        editPermDescription.setColumns(20);
        editPermDescription.setEditable(false);
        editPermDescription.setLineWrap(true);
        editPermDescription.setRows(5);
        editPermDescription.setWrapStyleWord(true);
        jScrollPane3.setViewportView(editPermDescription);

        jLabel4.setText(bundle.getString("SecurityComponentProperties.jLabel4.text")); // NOI18N

        jLabel5.setText(bundle.getString("SecurityComponentProperties.jLabel5.text")); // NOI18N

        editPermsPrincipalLabel.setText(bundle.getString("SecurityComponentProperties.editPermsPrincipalLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout editPermsDialogLayout = new org.jdesktop.layout.GroupLayout(editPermsDialog.getContentPane());
        editPermsDialog.getContentPane().setLayout(editPermsDialogLayout);
        editPermsDialogLayout.setHorizontalGroup(
            editPermsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editPermsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(editPermsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, editPermsDialogLayout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editPermsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(editPermsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jLabel5)
                                .add(editPermPermCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 201, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel4))
                            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(editPermsPrincipalLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, editPermsDialogLayout.createSequentialGroup()
                        .add(editPermsCancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editPermsOKButton)))
                .addContainerGap())
        );
        editPermsDialogLayout.setVerticalGroup(
            editPermsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editPermsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(editPermsPrincipalLabel)
                .add(6, 6, 6)
                .add(editPermsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(editPermsDialogLayout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editPermPermCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 209, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editPermsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(editPermsOKButton)
                    .add(editPermsCancelButton))
                .addContainerGap())
        );

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText(bundle.getString("SecurityComponentProperties.jLabel1.text")); // NOI18N

        addButton.setText(bundle.getString("SecurityComponentProperties.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText(bundle.getString("SecurityComponentProperties.removeButton.text")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setFont(jScrollPane1.getFont());

        permsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Name", "Owner?", "Permissions"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        permsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(permsTable);

        editButton.setText(bundle.getString("SecurityComponentProperties.editButton.text")); // NOI18N
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(editButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, removeButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(addButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {addButton, editButton, removeButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editButton))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        addNameTF.setText("");
        addUserRB.setSelected(true);
        addPrincipalDialog.pack();
        addPrincipalDialog.setVisible(true);
}//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int curRow = permsTable.getSelectedRow();
        perms.removeRow(curRow);
}//GEN-LAST:event_removeButtonActionPerformed

    private void addCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCancelButtonActionPerformed
        addPrincipalDialog.setVisible(false);
    }//GEN-LAST:event_addCancelButtonActionPerformed

    private void addOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOKButtonActionPerformed
        addPrincipalDialog.setVisible(false);

        Type type;
        if (addUserRB.isSelected()) {
            type = Type.USER;
        } else {
            type = Type.GROUP;
        }

        perms.addRow(new Principal(addNameTF.getText(), type));
    }//GEN-LAST:event_addOKButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        int curRow = permsTable.getSelectedRow();

        Principal p = perms.getPrincipal(curRow);
        SortedSet<Permission> ps = perms.getPerms(curRow);
        Set<ActionDTO> aps = new HashSet<ActionDTO>(perms.getAllPerms());

        edit.removeAllChildren();
        edit.setUserObject(new PrincipalHolder(p));

        // build the tree of actions
        Map<Action, DefaultMutableTreeNode> actions =
                new HashMap<Action, DefaultMutableTreeNode>();
        while (!aps.isEmpty()) {
            for (Iterator<ActionDTO> i = aps.iterator(); i.hasNext();) {
                ActionDTO actionDTO = i.next();
                Action action = actionDTO.getAction();
                DefaultMutableTreeNode node = null;

                if (action.getParent() == null) {
                    // top level action
                    node = new DefaultMutableTreeNode(new ActionHolder(action));
                    edit.add(node);
                } else if (actions.containsKey(action.getParent())) {
                    // we found the parent of this action -- add it to
                    // the tree
                    node = new DefaultMutableTreeNode(new ActionHolder(action));
                    actions.get(action.getParent()).add(node);
                }

                if (node != null) {
                    i.remove();
                    actions.put(action, node);

                    // find the associated permission, if any
                    Permission search = new Permission(p, actionDTO, null);
                    SortedSet<Permission> tail = ps.tailSet(search);
                    if (!tail.isEmpty() && tail.first().equals(search)) {
                        ActionHolder ah = (ActionHolder) node.getUserObject();
                        ah.setAccess(tail.first().getAccess());
                    }
                }
            }
        }

        // reset the model on the tree
        editPermsTree.setModel(new DefaultTreeModel(edit));
        editPermPermCombo.setEnabled(false);
        editPermPermCombo.setSelectedIndex(0);
        editPermDescription.setText(BUNDLE.getString("Choose_Permission"));

        // expand the tree
        int row = 0;
        while (row < editPermsTree.getRowCount()) {
            editPermsTree.expandRow(row);
            row++;
        }

        editPermsTree.setRootVisible(false);
        String text = BUNDLE.getString("Edit_Permission_For");
        text = MessageFormat.format(text, p.getId());
        editPermsPrincipalLabel.setText(text);
        editPermsDialog.pack();
        editPermsDialog.setVisible(true);

        editPermsTree.invalidate();
        editPermPermCombo.invalidate();
        editPermDescription.invalidate();
        editPermsDialog.repaint();
    }//GEN-LAST:event_editButtonActionPerformed

    private void editPermsOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPermsOKButtonActionPerformed
        editPermsDialog.setVisible(false);

        // create permissions
        SortedSet<Permission> res = new TreeSet<Permission>();
        DefaultMutableTreeNode root =
                (DefaultMutableTreeNode) editPermsTree.getModel().getRoot();
        PrincipalHolder p = (PrincipalHolder) root.getUserObject();

        // walk each child and record its permission
        for (Enumeration e = root.depthFirstEnumeration(); e.hasMoreElements();) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) e.nextElement();
            if (!(node.getUserObject() instanceof ActionHolder)) {
                continue;
            }

            ActionHolder ah = (ActionHolder) node.getUserObject();

            if (ah.getAccess() != null) {
                res.add(new Permission(p.getPrincipal(),
                        new ActionDTO(ah.getAction()),
                        ah.getAccess()));
            }
        }

        perms.setPerms(p.getPrincipal(), res);
    }//GEN-LAST:event_editPermsOKButtonActionPerformed

    private void editPermsCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPermsCancelButtonActionPerformed
        editPermsDialog.setVisible(false);
    }//GEN-LAST:event_editPermsCancelButtonActionPerformed

    private void editPermPermComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPermPermComboActionPerformed
        TreePath path = editPermsTree.getSelectionPath();
        if (path == null) {
            return;
        }

        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) path.getLastPathComponent();
        ActionHolder ah = (ActionHolder) node.getUserObject();

        switch (editPermPermCombo.getSelectedIndex()) {
            case 0:
                ah.setAccess(null);
                break;
            case 1:
                ah.setAccess(Permission.Access.GRANT);
                break;
            case 2:
                ah.setAccess(Permission.Access.DENY);
                break;
        }
    }//GEN-LAST:event_editPermPermComboActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup addBG;
    private javax.swing.JButton addButton;
    private javax.swing.JButton addCancelButton;
    private javax.swing.JRadioButton addGroupRB;
    private javax.swing.JTextField addNameTF;
    private javax.swing.JButton addOKButton;
    private javax.swing.JDialog addPrincipalDialog;
    private javax.swing.JButton addSearchButton;
    private javax.swing.JRadioButton addUserRB;
    private javax.swing.JButton editButton;
    private javax.swing.JTextArea editPermDescription;
    private javax.swing.JComboBox editPermPermCombo;
    private javax.swing.JButton editPermsCancelButton;
    private javax.swing.JDialog editPermsDialog;
    private javax.swing.JButton editPermsOKButton;
    private javax.swing.JLabel editPermsPrincipalLabel;
    private javax.swing.JTree editPermsTree;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable permsTable;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

    class PermTableModel extends AbstractTableModel {

        private List<Principal> principals = new LinkedList<Principal>();
        private List<Boolean> owner = new LinkedList<Boolean>();
        private List<SortedSet<Permission>> perms =
                new LinkedList<SortedSet<Permission>>();
        private Set<ActionDTO> allPerms = new LinkedHashSet<ActionDTO>();

        public void addRow(Principal p) {
            principals.add(p);
            owner.add(false);
            perms.add(new TreeSet<Permission>());

            this.fireTableRowsInserted(principals.size() - 1,
                    principals.size() - 1);
        }

        public void removeRow(int index) {
            principals.remove(index);
            owner.remove(index);
            perms.remove(index);

            this.fireTableRowsDeleted(index, index);
        }

        public Principal getPrincipal(int index) {
            return principals.get(index);
        }

        public boolean isOwner(int index) {
            return owner.get(index);
        }

        public Set<ActionDTO> getAllPerms() {
            return allPerms;
        }

        public SortedSet<Permission> getPerms(int index) {
            return perms.get(index);
        }

        public void setPerms(Principal p, SortedSet<Permission> ps) {
            int index = principals.indexOf(p);
            if (index == -1) {
                return;
            }

            perms.set(index, ps);
            fireTableCellUpdated(index, 3);
        }

        public CellPermissions toPermissions() {
            CellPermissions out = new CellPermissions();

            for (int i = 0; i < principals.size(); i++) {
                Principal p = principals.get(i);
                if (owner.get(i)) {
                    out.getOwners().add(p);
                } else {
                    out.getPermissions().addAll(perms.get(i));
                }
            }

            return out;
        }

        public void fromPermissions(CellPermissions in) {
            clear();

            for (Principal p : in.getOwners()) {
                principals.add(p);
                owner.add(true);
                perms.add(new TreeSet<Permission>());
            }

            Map<Principal, SortedSet<Permission>> pm =
                    new LinkedHashMap<Principal, SortedSet<Permission>>();
            for (Permission p : in.getPermissions()) {
                SortedSet<Permission> ps = pm.get(p.getPrincipal());
                if (ps == null) {
                    ps = new TreeSet<Permission>();
                    pm.put(p.getPrincipal(), ps);
                }
                ps.add(p);
            }

            for (Entry<Principal, SortedSet<Permission>> e : pm.entrySet()) {
                principals.add(e.getKey());
                owner.add(false);
                perms.add(e.getValue());
            }

            allPerms = in.getAllActions();

            fireTableRowsInserted(0, principals.size());
            fireTableDataChanged();
        }

        public void clear() {
            int size = principals.size();

            principals.clear();
            owner.clear();
            perms.clear();

            fireTableRowsDeleted(0, size);
            fireTableDataChanged();
        }

        public int getRowCount() {
            return principals.size();
        }

        public int getColumnCount() {
            return 4;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Principal p;

            switch (columnIndex) {
                case 0:
                    p = principals.get(rowIndex);
                    return p.getType().toString();
                case 1:
                    p = principals.get(rowIndex);

                    // special case -- if the princpal is an everybody
                    // principal (the "users" group), display the name as
                    // "everyone else"
                    String name = p.getId();
                    if (p.getType() == Principal.Type.EVERYBODY) {
                        name = BUNDLE.getString("Everybody");
                    }

                    return name;
                case 2:
                    return owner.get(rowIndex);
                case 3:
                    return perms.get(rowIndex).size();
                default:
                    throw new IllegalStateException("Request for unknown " +
                            "column " + columnIndex);
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 2:
                    owner.set(rowIndex, (Boolean) aValue);
                    fireTableCellUpdated(rowIndex, columnIndex);
                    return;
                default:
                    throw new IllegalStateException("Column " + columnIndex +
                            " not editable.");
            }
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return Boolean.class;
                case 3:
                    return String.class;
                default:
                    throw new IllegalStateException("Unknown column " + column);
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return BUNDLE.getString("Type");
                case 1:
                    return BUNDLE.getString("Name");
                case 2:
                    return BUNDLE.getString("Owner");
                case 3:
                    return BUNDLE.getString("Permissions");
                default:
                    throw new IllegalStateException("Unknown column " + column);
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 2);
        }
    }

    class PrincipalHolder {

        private Principal p;

        public PrincipalHolder(Principal p) {
            this.p = p;
        }

        public Principal getPrincipal() {
            return p;
        }

        @Override
        public String toString() {
            return "Permssions";
        }
    }

    class ActionHolder {

        private Action action;
        private Permission.Access access;

        public ActionHolder(Action action) {
            this.action = action;
        }

        public Action getAction() {
            return action;
        }

        public Permission.Access getAccess() {
            return access;
        }

        public void setAccess(Permission.Access access) {
            this.access = access;
        }

        @Override
        public String toString() {
            return action.getDisplayName();
        }
    }

    static class AccessCBRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected,
                boolean cellHasFocus) {
            Permission.Access a = (Permission.Access) value;
            String name = BUNDLE.getString("Unspecified");

            if (a != null) {
                switch (a) {
                    case GRANT:
                        name = BUNDLE.getString("Granted");
                        break;
                    case DENY:
                        name = BUNDLE.getString("Denied");
                        break;
                }
            }

            return super.getListCellRendererComponent(list, name, index,
                    isSelected, cellHasFocus);
        }
    }

    class RemoveButtonSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            boolean enabled = false;

            if (!e.getValueIsAdjusting()) {
                enabled = (permsTable.getSelectedRow() >= 0);
            }

            removeButton.setEnabled(enabled);
        }
    }

    class EditButtonSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            boolean enabled = false;

            if (!e.getValueIsAdjusting()) {
                int row = permsTable.getSelectedRow();
                enabled = (row >= 0 && !perms.isOwner(row));
            }

            editButton.setEnabled(enabled);
        }
    }

    class TableDirtyListener implements TableModelListener {

        public void tableChanged(TableModelEvent tme) {
            CellPermissions currentCellPermissions = perms.toPermissions();

            // compare the owners array
            boolean equal = currentCellPermissions.getOwners().equals(
                    originalCellPermissions.getOwners());

            // compare the size of the permissions sets
            Set<Permission> currentPermissions =
                    currentCellPermissions.getPermissions();
            Set<Permission> originalPermissions =
                    originalCellPermissions.getPermissions();
            equal &= currentPermissions.size() == originalPermissions.size();

            // compare each permission -- these have to be compared manually,
            // since the equals method on Permission doesn't take the access
            // into account
            if (equal) {
                Iterator<Permission> c = currentPermissions.iterator();
                Iterator<Permission> o = originalPermissions.iterator();

                while (c.hasNext()) {
                    Permission cp = c.next();
                    Permission op = o.next();

                    if (!cp.equals(op) || cp.getAccess() != op.getAccess()) {
                        equal = false;
                        break;
                    }
                }
            }

            editor.setPanelDirty(SecurityComponentProperties.class, !equal);
        }
    }
}
