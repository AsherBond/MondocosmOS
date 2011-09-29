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

/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.modules.artimport.client.jme;

import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import com.jme.image.Texture;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.resource.ResourceLocator;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.CellEditChannelConnection;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.ImportSettings;
import org.jdesktop.wonderland.client.jme.artimport.ImportedModel;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.client.jme.utils.traverser.ProcessNodeInterface;
import org.jdesktop.wonderland.client.jme.utils.traverser.TreeScan;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.modules.ModuleUtils;
import org.jdesktop.wonderland.common.FileUtils;
import org.jdesktop.wonderland.common.cell.CellEditConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.CellCreateMessage;
import org.jdesktop.wonderland.common.login.AuthenticationInfo;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleList;
import org.jdesktop.wonderland.common.modules.ModuleUploader;
import org.jdesktop.wonderland.common.modules.utils.ModuleJarWriter;

/**
 * Frame that provides the controls for the user to position and orient
 * a model instance in the world. Also allows configuration of other instance
 * data such as world name and texture directory.
 *
 * NOTE the strings in the targetModuleSelector are used directly to construct
 * the filename and URI, so they must be valid for both use cases. In particular
 * you must avoid characters that break URI parsing such as : _ /
 *
 * @author  paulby
 * @author  Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class ImportSessionFrame extends javax.swing.JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(ImportSessionFrame.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/artimport/client/jme/resources/Bundle");
    private ArrayList<ImportedModel> imports = new ArrayList();
    private DefaultTableModel tableModel = null;
    // Directory in which to store the compiled model
    private File compiledDir = null;
    // Directory we last loaded a model from
    private File lastModelDir = null;
    private ModelImporterFrame importFrame = null;
    private int editingRow = -1;

    /** Creates new form ImportSessionFrame */
    public ImportSessionFrame() {
        initComponents();
        tableModel = (DefaultTableModel) importTable.getModel();
        tableModel.setColumnIdentifiers(new Object[] {
                BUNDLE.getString("Wonderland_Name"),
                BUNDLE.getString("Original_Model_Name")
        });

        importTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        importTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        int row = importTable.getSelectedRow();
                        boolean validSelection = (row >= 0);
                        editB.setEnabled(validSelection);
                        removeB.setEnabled(validSelection);
                    }
                });

        importTable.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent tme) {
                boolean models = importTable.getModel().getRowCount() > 0;
            }
        });

        // Load the config file which contains the directory from which we last
        // loaded a model.
        try {
            File lastModelFile = getLastModelFile();
            if (lastModelFile.exists()) {
                DataInputStream in = new DataInputStream(
                        new FileInputStream(lastModelFile));
                String str;
                if (in.readBoolean()) {
                    str = in.readUTF();
                    lastModelDir = new File(str);
                } else {
                    lastModelDir = null;
                }

                if (in.readBoolean()) {
                    str = in.readUTF();
                    compiledDir = new File(str);
                } else {
                    compiledDir = null;
                }
                in.close();
            }
        } catch (Exception ex) {
            lastModelDir = null;
            LOGGER.log(Level.INFO, null, ex);
        }

        Collection<ServerSessionManager> servers = LoginManager.getAll();
        for (ServerSessionManager server : servers) {
            targetServerSelector.addItem(server);
        }

        importFrame = new ModelImporterFrame(this, lastModelDir);

    }

    /**
     * Write the defaults for this UI
     */
    void writeDefaultsConfig() {
        try {
            File lastModelFile = getLastModelFile();
            DataOutputStream out = new DataOutputStream(
                    new FileOutputStream(lastModelFile));
            out.writeBoolean(lastModelDir != null);
            if (lastModelDir != null) {
                out.writeUTF(lastModelDir.getAbsolutePath());
            }
            out.writeBoolean(compiledDir != null);
            if (compiledDir != null) {
                out.writeUTF(compiledDir.getAbsolutePath());
            }
            out.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    private File getLastModelFile() {
        File configDir = ClientContext.getUserDirectory("config");
        return new File(configDir, "last_model_dir");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        tablePopupMenu = new javax.swing.JPopupMenu();
        editPMI = new javax.swing.JMenuItem();
        removePMI = new javax.swing.JMenuItem();
        loadingDialogPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        eastP = new javax.swing.JPanel();
        editB = new javax.swing.JButton();
        removeB = new javax.swing.JButton();
        importModelB = new javax.swing.JButton();
        centerP = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        targetServerSelector = new javax.swing.JComboBox();
        modelListL = new javax.swing.JLabel();
        targetNameL = new javax.swing.JLabel();
        descriptionL = new javax.swing.JLabel();
        descriptionTF = new javax.swing.JTextField();
        targetModuleTF = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        importTable = new javax.swing.JTable();
        southP = new javax.swing.JPanel();
        deployToServerB = new javax.swing.JButton();
        saveAsSrcB = new javax.swing.JButton();
        saveAsModuleB = new javax.swing.JButton();
        okB = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        loadImportGroupMI = new javax.swing.JMenuItem();
        saveImportGroupMI = new javax.swing.JMenuItem();

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/artimport/client/jme/resources/Bundle"); // NOI18N
        editPMI.setText(bundle.getString("ImportSessionFrame.editPMI.text")); // NOI18N
        tablePopupMenu.add(editPMI);

        removePMI.setText(bundle.getString("ImportSessionFrame.removePMI.text")); // NOI18N
        tablePopupMenu.add(removePMI);

        loadingDialogPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loadingDialogPanel.setMinimumSize(new java.awt.Dimension(215, 100));
        loadingDialogPanel.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText(bundle.getString("ImportSessionFrame.jLabel4.text")); // NOI18N
        loadingDialogPanel.add(jLabel4, new java.awt.GridBagConstraints());

        setTitle(bundle.getString("ImportSessionFrame.title")); // NOI18N

        editB.setText(bundle.getString("ImportSessionFrame.editB.text")); // NOI18N
        editB.setEnabled(false);
        editB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBActionPerformed(evt);
            }
        });

        removeB.setText(bundle.getString("ImportSessionFrame.removeB.text")); // NOI18N
        removeB.setToolTipText(bundle.getString("ImportSessionFrame.removeB.toolTipText")); // NOI18N
        removeB.setEnabled(false);
        removeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBActionPerformed(evt);
            }
        });

        importModelB.setText(bundle.getString("ImportSessionFrame.importModelB.text")); // NOI18N
        importModelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importModelBActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout eastPLayout = new org.jdesktop.layout.GroupLayout(eastP);
        eastP.setLayout(eastPLayout);
        eastPLayout.setHorizontalGroup(
            eastPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(eastPLayout.createSequentialGroup()
                .addContainerGap()
                .add(eastPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(importModelB)
                    .add(editB)
                    .add(removeB))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        eastPLayout.linkSize(new java.awt.Component[] {editB, removeB}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        eastPLayout.setVerticalGroup(
            eastPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(eastPLayout.createSequentialGroup()
                .add(29, 29, 29)
                .add(importModelB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeB)
                .addContainerGap(183, Short.MAX_VALUE))
        );

        centerP.setOpaque(false);
        centerP.setVerifyInputWhenFocusTarget(false);

        jLabel5.setFont(jLabel5.getFont());
        jLabel5.setText(bundle.getString("ImportSessionFrame.jLabel5.text")); // NOI18N

        targetServerSelector.setRenderer(new LoginManagerRenderer());

        modelListL.setFont(modelListL.getFont().deriveFont(modelListL.getFont().getStyle() | java.awt.Font.BOLD));
        modelListL.setText(bundle.getString("ImportSessionFrame.modelListL.text")); // NOI18N

        targetNameL.setFont(targetNameL.getFont());
        targetNameL.setText(bundle.getString("ImportSessionFrame.targetNameL.text")); // NOI18N

        descriptionL.setFont(descriptionL.getFont());
        descriptionL.setText(bundle.getString("ImportSessionFrame.descriptionL.text")); // NOI18N

        targetModuleTF.setText(bundle.getString("ImportSessionFrame.targetModuleTF.text")); // NOI18N
        targetModuleTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetModuleTFActionPerformed(evt);
            }
        });

        importTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Wonderland Name", "Original Model File"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(importTable);

        org.jdesktop.layout.GroupLayout centerPLayout = new org.jdesktop.layout.GroupLayout(centerP);
        centerP.setLayout(centerPLayout);
        centerPLayout.setHorizontalGroup(
            centerPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(centerPLayout.createSequentialGroup()
                .addContainerGap()
                .add(centerPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                    .add(centerPLayout.createSequentialGroup()
                        .add(centerPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, descriptionL)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, targetNameL))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(centerPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(targetModuleTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 259, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(targetServerSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 266, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(descriptionTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 266, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(modelListL))
                .addContainerGap())
        );

        centerPLayout.linkSize(new java.awt.Component[] {jLabel5, targetNameL}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        centerPLayout.linkSize(new java.awt.Component[] {descriptionTF, targetModuleTF, targetServerSelector}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        centerPLayout.setVerticalGroup(
            centerPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(centerPLayout.createSequentialGroup()
                .addContainerGap()
                .add(modelListL)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(centerPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(targetNameL)
                    .add(targetModuleTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(centerPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(descriptionL)
                    .add(descriptionTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(centerPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(targetServerSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        deployToServerB.setText(bundle.getString("ImportSessionFrame.deployToServerB.text")); // NOI18N
        deployToServerB.setToolTipText(bundle.getString("ImportSessionFrame.deployToServerB.toolTipText")); // NOI18N
        deployToServerB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deployToServerBActionPerformed(evt);
            }
        });

        saveAsSrcB.setText(bundle.getString("ImportSessionFrame.saveAsSrcB.text")); // NOI18N
        saveAsSrcB.setToolTipText(bundle.getString("ImportSessionFrame.saveAsSrcB.toolTipText")); // NOI18N
        saveAsSrcB.setEnabled(false);
        saveAsSrcB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsSrcBActionPerformed(evt);
            }
        });

        saveAsModuleB.setText(bundle.getString("ImportSessionFrame.saveAsModuleB.text")); // NOI18N
        saveAsModuleB.setToolTipText(bundle.getString("ImportSessionFrame.saveAsModuleB.toolTipText")); // NOI18N
        saveAsModuleB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsModuleBActionPerformed(evt);
            }
        });

        okB.setText(bundle.getString("ImportSessionFrame.okB.text")); // NOI18N
        okB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("ImportSessionFrame.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout southPLayout = new org.jdesktop.layout.GroupLayout(southP);
        southP.setLayout(southPLayout);
        southPLayout.setHorizontalGroup(
            southPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(southPLayout.createSequentialGroup()
                .addContainerGap()
                .add(deployToServerB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveAsSrcB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveAsModuleB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(cancelButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(okB)
                .addContainerGap())
        );
        southPLayout.setVerticalGroup(
            southPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(southPLayout.createSequentialGroup()
                .addContainerGap()
                .add(southPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(deployToServerB)
                    .add(saveAsSrcB)
                    .add(saveAsModuleB)
                    .add(okB)
                    .add(cancelButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 351, Short.MAX_VALUE)
        );

        jMenu1.setText(bundle.getString("ImportSessionFrame.jMenu1.text")); // NOI18N
        jMenu1.setEnabled(false);

        loadImportGroupMI.setText(bundle.getString("ImportSessionFrame.loadImportGroupMI.text")); // NOI18N
        loadImportGroupMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadImportGroupMIActionPerformed(evt);
            }
        });
        jMenu1.add(loadImportGroupMI);

        saveImportGroupMI.setText(bundle.getString("ImportSessionFrame.saveImportGroupMI.text")); // NOI18N
        saveImportGroupMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImportGroupMIActionPerformed(evt);
            }
        });
        jMenu1.add(saveImportGroupMI);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(centerP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(0, 0, 0)
                                .add(eastP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel6))
                        .addContainerGap())
                    .add(southP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel6)
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 351, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(eastP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(centerP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(0, 0, 0)
                        .add(southP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importModelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importModelBActionPerformed
        editingRow = -1;            // Not editing
        importFrame.chooseFile();  // choosefile will set the frame to visible
        importFrame.setVisible(true);
    }//GEN-LAST:event_importModelBActionPerformed

    private void editBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBActionPerformed
        editingRow = importTable.getSelectedRow();
        importFrame.editModel(imports.get(editingRow));
        importFrame.setVisible(true);
    }//GEN-LAST:event_editBActionPerformed

    private void removeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBActionPerformed
        int row = importTable.getSelectedRow();
        if (row == -1) {
            LOGGER.warning("Remove with invalid row");
            return;
        }

        ImportedModel ic = imports.remove(row);
        ClientContextJME.getWorldManager().removeEntity(ic.getEntity());
        tableModel.removeRow(row);
    }//GEN-LAST:event_removeBActionPerformed

    private void deployToServerBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deployToServerBActionPerformed

        String moduleName = targetModuleTF.getText();
        final ArrayList<DeployedModel> deploymentInfo = new ArrayList();
        WorldManager wm = ClientContextJME.getWorldManager();
        final ServerSessionManager targetServer =
                (ServerSessionManager) targetServerSelector.getSelectedItem();

        // Check we are not about to overwrite an existing module
        String url = targetServer.getServerURL();
        ModuleList moduleList = ModuleUtils.fetchModuleList(url);
        ModuleInfo[] modules = moduleList.getModuleInfos();
        if (modules != null) {
            boolean conflict = false;
            for (int i = 0; i < modules.length && !conflict; i++) {
                if (moduleName.equals(modules[i].getName())) {
                    conflict = true;
                }
            }

            if (conflict) {
                int ret = JOptionPane.showConfirmDialog(this,
                        BUNDLE.getString("Module_Conflict_Message"),
                        BUNDLE.getString("Module_Conflict"),
                        JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.NO_OPTION) {
                    return;
                }
            }
        }

        final File moduleJar = createModuleJar(deploymentInfo, null);

        // Now deploy to server
        Thread t = new Thread() {
            private JDialog uploadingDialog;
            @Override
            public void run() {
                try {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            public void run() {
                                uploadingDialog = new JDialog(ImportSessionFrame.this);
                                    uploadingDialog.setLayout(new BorderLayout());
                                    uploadingDialog.add(loadingDialogPanel, BorderLayout.CENTER);
                                    uploadingDialog.pack();
                                    uploadingDialog.setSize(200, 100);
                                    uploadingDialog.setVisible(true);
                                    uploadingDialog.setAlwaysOnTop(true);
                            }
                        });
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ImportSessionFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(ImportSessionFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    ModuleUploader uploader = new ModuleUploader(new URL(targetServer.getServerURL()));

                    // if the authentication type is NONE, don't authenticate,
                    // since the upload will only accept modules from an
                    // administrator.
                    // XXX TODO: we should fix this so that upload writes to
                    // the content repository, so that non-admin users can
                    // upload art when authenication is turned on XXX
                    if (targetServer.getDetails().getAuthInfo().getType() !=
                            AuthenticationInfo.Type.NONE)
                    {
                        uploader.setAuthURL(targetServer.getCredentialManager().getAuthenticationURL());
                    }

                    uploader.upload(moduleJar);
                } catch (MalformedURLException ex) {
                    LOGGER.log(Level.SEVERE, "MalformedURL " + targetServer.getServerURL(), ex);
                    return;
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "IO Exception during upload", e);
                    return;
                } catch (Throwable t) {
                    LOGGER.log(Level.SEVERE, "Exception during upload", t);
                    return;
                }

                // Now create the cells for the new content
                WonderlandSession session =
                        LoginManager.getPrimary().getPrimarySession();
                CellEditChannelConnection connection =
                        (CellEditChannelConnection) session.getConnection(
                        CellEditConnectionType.CLIENT_TYPE);
                for (DeployedModel info : deploymentInfo) {
                    CellID parentCellID = null;
                    CellCreateMessage msg = new CellCreateMessage(
                            parentCellID, info.getCellServerState());
                    connection.send(msg);
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        uploadingDialog.setVisible(false);
                        uploadingDialog.dispose();
                    }
                });
            }
        };
        t.start();

        // Remove entities, once we create the cells on the server we
        // will be sent the client cells
        for (ImportedModel model : imports) {
            wm.removeEntity(model.getEntity());
        }

        tableModel.setRowCount(0);
        imports.clear();
    }

    private File createModuleJar(
            ArrayList<DeployedModel> deploymentInfo, File targetDir) {

        File moduleJar = null;
        String moduleName = targetModuleTF.getText();

        try {
            File tmpDir = File.createTempFile("wlart", null);
            if (tmpDir.isDirectory()) {
                FileUtils.deleteDirContents(tmpDir);
            } else {
                tmpDir.delete();
            }
            tmpDir.mkdir();
            tmpDir = new File(tmpDir, targetModuleTF.getText());

            for (ImportedModel model : imports) {
                try {
                    model.setDeploymentBaseURL("wla://"+moduleName+"/");
                    deploymentInfo.add(model.getModelLoader().deployToModule(
                            tmpDir, model));
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error deploying model " +
                            model.getOriginalURL().toExternalForm(), ex);
                }
            }

            ModuleJarWriter mjw = new ModuleJarWriter();
            File[] dirs = tmpDir.listFiles();
            if (dirs != null) {
                for (File f : dirs) {
                    if (f.isDirectory()) {
                        mjw.addDirectory(f);
                    }
                }
            }
            ModuleInfo mi =
                    new ModuleInfo(moduleName, 1, 0, 0, descriptionTF.getText());
            mjw.setModuleInfo(mi);
            try {
                if (targetDir == null) {
                    targetDir = tmpDir.getParentFile();
                }
                moduleJar = new File(targetDir, moduleName + ".jar");
                mjw.writeToJar(moduleJar);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

            if (moduleJar == null) {
                JOptionPane.showMessageDialog(this,
                        BUNDLE.getString("Module_JAR_Creation_Error"),
                        BUNDLE.getString("Error"),
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }

        return moduleJar;

}//GEN-LAST:event_deployToServerBActionPerformed

    private void saveImportGroupMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImportGroupMIActionPerformed
        File sessionFile = new File(
                ClientContext.getUserDirectory("config"), "import_session");
        saveImportSession(sessionFile);
}//GEN-LAST:event_saveImportGroupMIActionPerformed

    private void loadImportGroupMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadImportGroupMIActionPerformed
        File sessionFile = new File(
                ClientContext.getUserDirectory("config"), "import_session");
        for (ImportedModel m : imports) {
            m.getRootBG().getParent().detachChild(m.getRootBG());
        }

        imports.clear();
        tableModel.setRowCount(0);
        importTable.repaint();

        loadImportSession(sessionFile);
        for (ImportedModel m : imports) {
            addToTable(m);
            try {
                loadModel(m.getImportSettings());
            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "Unable to load model " +
                        m.getOriginalURL().toExternalForm(), ex);
            } catch (IOException ioe) {
                LOGGER.log(Level.SEVERE, "Unable to load model " +
                        m.getOriginalURL().toExternalForm(), ioe);
            }
        }
}//GEN-LAST:event_loadImportGroupMIActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    WorldManager wm = ClientContextJME.getWorldManager();
    for (ImportedModel model : imports) {
        wm.removeEntity(model.getEntity());
    }
    imports.clear();
    tableModel.setRowCount(0);
    importTable.repaint();
    setVisible(false);
}//GEN-LAST:event_cancelButtonActionPerformed

private void targetModuleTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetModuleTFActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_targetModuleTFActionPerformed

private void saveAsModuleBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsModuleBActionPerformed

    ArrayList<DeployedModel> deploymentInfo = new ArrayList();

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(BUNDLE.getString("CHOOSE_DIRECTORY_FOR_MODULE_JAR"));
    chooser.setApproveButtonText(BUNDLE.getString("CHOOSE_DIRECTORY"));
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = chooser.showSaveDialog(ImportSessionFrame.this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        if (!f.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Please select a directory into which your module jar will be deployed");
            return;
        }
        if (!f.exists())
            f.mkdirs();
        createModuleJar(deploymentInfo, f);
    }
}//GEN-LAST:event_saveAsModuleBActionPerformed

private void saveAsSrcBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsSrcBActionPerformed
    String moduleName = targetModuleTF.getText();
    ArrayList<DeployedModel> deploymentInfo = new ArrayList();

    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = chooser.showOpenDialog(ImportSessionFrame.this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
    }

    File parentDir = chooser.getSelectedFile();
    File srcDir = new File(parentDir, moduleName);
    if (srcDir.exists()) {
        if (srcDir.listFiles().length > 0) {
            int res = JOptionPane.showConfirmDialog(this,
                    BUNDLE.getString("Module_Exists_Message"),
                    BUNDLE.getString("Module_Exists"),
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                return;
            }
            FileUtils.deleteDirContents(srcDir);
        }
    } else {
        srcDir.mkdir();
    }

    ModuleSourceManager moduleMgr = new ModuleSourceManager();
    moduleMgr.createModule(
            moduleName, "Art Module", parentDir, true, false, false);

    for (ImportedModel model : imports) {
        try {
            deploymentInfo.add(
                    model.getModelLoader().deployToModule(srcDir, model));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error deploying model " +
                    model.getOriginalURL().toExternalForm(), ex);
        }
    }

//        NoExitAnt ant = new NoExitAnt();
//        ant.startAnt(new String[] {"-f", targetModuleDir+File.separator+"build.xml", "dist"}, null, this.getClass().getClassLoader());

//        String modulename = targetModuleDir.substring(targetModuleDir.lastIndexOf(File.separatorChar)+1);
//        File distJar = new File(targetModuleDir+File.separator+"dist"+File.separator+modulename+".jar");
////        System.err.println("DEPLOYING "+distJar.getAbsolutePath());
//        if (!distJar.exists()) {
//            JOptionPane.showMessageDialog(this, "Module failed to compile\nSee console for error messages", "Deployment Failed", JOptionPane.ERROR_MESSAGE);
//            return;
//        }

}//GEN-LAST:event_saveAsSrcBActionPerformed

private void okBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBActionPerformed
    setVisible(false);
    imports.clear();
    tableModel.setRowCount(0);
}//GEN-LAST:event_okBActionPerformed

    synchronized void asyncLoadModel(final ImportSettings settings,
            final LoadCompleteListener listener) {
        final JDialog loadingDialog = new JDialog(importFrame);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.add(loadingDialogPanel, BorderLayout.CENTER);
        loadingDialog.pack();
        loadingDialog.setSize(200, 100);
        loadingDialog.setVisible(true);
        loadingDialog.setAlwaysOnTop(true);

        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    ImportedModel loadedModel = loadModel(settings);
                    listener.loadComplete(loadedModel);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } finally {
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
                }
            }
        });
        t.start();
    }

    /**
     * Load model from file
     * 
     * @param origFile
     */
    ImportedModel loadModel(ImportSettings settings) throws IOException {
        Node rootBG = new Node();

        URL url = settings.getModelURL();
        if (url.getProtocol().equalsIgnoreCase("file")) {
            lastModelDir = new File(url.getFile()).getParentFile();
        }

        Node modelBG = null;

        ModelLoader modelLoader =
                LoaderManager.getLoaderManager().getLoader(url);

        LOGGER.fine("Using model loader " + modelLoader);

        if (modelLoader == null) {
            String urlString = url.toExternalForm();
            String fileExtension = FileUtils.getFileExtension(urlString);
            String message = BUNDLE.getString("No_Loader_For");
            message = MessageFormat.format(message, fileExtension);
            JOptionPane.showMessageDialog(null, message);
            return null;
        }

        ImportedModel loadedModel = modelLoader.importModel(settings);
        modelBG = loadedModel.getModelBG();

        rootBG.attachChild(modelBG);

        WorldManager wm = ClientContextJME.getWorldManager();

        RenderManager renderManager = wm.getRenderManager();
        ZBufferState buf = (ZBufferState) renderManager.createRendererState(
                RenderState.RS_ZBUFFER);
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

        MaterialState matState =
                (MaterialState) renderManager.createRendererState(
                RenderState.RS_MATERIAL);
//        matState.setDiffuse(color);
        rootBG.setRenderState(matState);
        rootBG.setRenderState(buf);

        Entity entity = new Entity(loadedModel.getWonderlandName());
        RenderComponent scene = renderManager.createRenderComponent(rootBG);
        entity.addComponent(RenderComponent.class, scene);

        scene.setLightingEnabled(loadedModel.getImportSettings().isLightingEnabled());

        entity.addComponent(TransformProcessorComponent.class,
                new TransformProcessorComponent(wm, modelBG, rootBG));

        wm.addEntity(entity);
        loadedModel.setEntity(entity);

//        findTextures(modelBG);

        return loadedModel;
    }

    // This gimble locks, but good enough for now...
    public static Matrix3f calcRotationMatrix(float x, float y, float z) {
        Matrix3f m3f = new Matrix3f();
        m3f.loadIdentity();
        m3f.fromAngleAxis(x, new Vector3f(1f, 0f, 0f));
        Matrix3f rotY = new Matrix3f();
        rotY.loadIdentity();
        rotY.fromAngleAxis(y, new Vector3f(0f, 1f, 0f));
        Matrix3f rotZ = new Matrix3f();
        rotZ.loadIdentity();
        rotZ.fromAngleAxis(z, new Vector3f(0f, 0f, 1f));

        m3f.multLocal(rotY);
        m3f.multLocal(rotZ);

        return m3f;
    }

    /**
     * Notification from ImporterFrame that a model has been loaded. 
     * This method is called at the end of both load and edit 
     * 
     * @param origModel original model absolute filename
     * @param wonderlandName name of model in wonderland
     * @param translation model translation
     * @param orientation model orientation
     */
    void loadCompleted(ImportedModel imp) {

        if (editingRow >= 0) {
            setRow(editingRow, imp);
        } else {
            imports.add(imp);
            addToTable(imp);
        }

        writeDefaultsConfig();
    }

    void findTextures(Node root) {
        TreeScan.findNode(root, new ProcessNodeInterface() {

            public boolean processNode(Spatial node) {
                TextureState ts =
                        (TextureState) node.getRenderState(
                        TextureState.RS_TEXTURE);
                if (ts != null) {
                    Texture t = ts.getTexture();
                    if (t != null) {
//                        System.out.println("Texture "+t.getImageLocation());
                    }
                }
                return true;
            }
        });
    }

    /**
     * Called when the user cancels the load
     */
    void loadCancelled(ImportedModel model) {
        if (editingRow >= 0 && imports.contains(model)) {
            // Restore Position of model
            final ImportedModel imp = model; //imports.get(editingRow);
            final Node tg = imp.getRootBG();
            final Vector3f rot = imp.getOrientation();
            RenderUpdater renderUpdater = new RenderUpdater() {

                public void update(Object arg0) {
                    tg.setLocalRotation(
                            calcRotationMatrix(rot.x, rot.y, rot.z));
                    tg.setLocalTranslation(imp.getTranslation());
                    ClientContextJME.getWorldManager().addToUpdateList(tg);
                }
            };
            ClientContextJME.getWorldManager().addRenderUpdater(
                    renderUpdater, null);
        }

        if (model != null) {
            ClientContextJME.getWorldManager().removeEntity(model.getEntity());
        }
    }

    void addToTable(ImportedModel config) {
        tableModel.addRow(new Object[]{
                    config.getWonderlandName(),
                    config.getOriginalURL().toExternalForm()
                });
    }

    void setRow(int row, ImportedModel config) {
        tableModel.setValueAt(config.getWonderlandName(), row, 0);
        tableModel.setValueAt(config.getOriginalURL().toExternalForm(), row, 1);
    }

    /**
     * Save the current import session to the specified file
     * @param file
     */
    private void saveImportSession(File file) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(file)));
            out.writeObject(imports);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    private void loadImportSession(File file) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(file)));
            imports = (ArrayList<ImportedModel>) in.readObject();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
        }

    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message,
                BUNDLE.getString("Error_Compiling_Model"),
                JOptionPane.ERROR_MESSAGE);
    }

    public boolean requestConfirmation(String message) {
        int answer = JOptionPane.showConfirmDialog(this, message,
                BUNDLE.getString("Select_An_Option"),
                JOptionPane.YES_NO_OPTION);
        return (answer == JOptionPane.YES_OPTION);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerP;
    private javax.swing.JButton deployToServerB;
    private javax.swing.JLabel descriptionL;
    private javax.swing.JTextField descriptionTF;
    private javax.swing.JPanel eastP;
    private javax.swing.JButton editB;
    private javax.swing.JMenuItem editPMI;
    private javax.swing.JButton importModelB;
    private javax.swing.JTable importTable;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem loadImportGroupMI;
    private javax.swing.JPanel loadingDialogPanel;
    private javax.swing.JLabel modelListL;
    private javax.swing.JButton okB;
    private javax.swing.JButton removeB;
    private javax.swing.JMenuItem removePMI;
    private javax.swing.JButton saveAsModuleB;
    private javax.swing.JButton saveAsSrcB;
    private javax.swing.JMenuItem saveImportGroupMI;
    private javax.swing.JPanel southP;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JTextField targetModuleTF;
    private javax.swing.JLabel targetNameL;
    private javax.swing.JComboBox targetServerSelector;
    // End of variables declaration//GEN-END:variables

    /**
     * Filter that only accepts directories
     */
    class DirExtensionFilter extends FileFilter {

        public DirExtensionFilter() {
        }

        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }

            return false;
        }

        @Override
        public String getDescription() {
            return BUNDLE.getString("Directory");
        }
    }

    class ImporterResourceLocator implements ResourceLocator {

        private String baseURL;

        public ImporterResourceLocator(URI baseURI) {
            try {
                this.baseURL = baseURI.toURL().toExternalForm();
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        public URL locateResource(String resource) {
//            System.out.println("*************** Looking for texture "+resource);
            try {
                URL ret = new URL(baseURL + "/" + removePath(resource));
//                System.out.println("Resource URL "+ret.toExternalForm());

                return ret;
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            return null;
        }

        private String removePath(String filename) {
            int i = filename.lastIndexOf(File.separatorChar);
            if (i < 0) {
                return filename;
            }
            return filename.substring(i + 1);
        }
    }

    class LoginManagerRenderer extends JLabel implements ListCellRenderer {

        public LoginManagerRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            if (value instanceof ServerSessionManager) {
                setText(((ServerSessionManager) value).getServerNameAndPort());
            }
//             setBackground(isSelected ? Color.red : Color.white);
//             setForeground(isSelected ? Color.white : Color.black);
            return this;
        }
    }

    class NoExitAnt extends org.apache.tools.ant.Main {

        @Override
        public void exit(int exitCode) {
            System.err.println("Ignoring " + exitCode);
        }
    }

    interface LoadCompleteListener {

        /**
         * Notificaton that the load is complete. Provides the entity
         * that was loaded
         * @param entity
         */
        public void loadComplete(ImportedModel importedModel);
    }
}
