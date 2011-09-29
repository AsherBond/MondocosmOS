/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.modules.artimport.client.jme;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import java.awt.Component;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellComponentServerState.TransparencyMode;

/**
 *
 * Properties editor for the ModelCellComponent.
 *
 * Note the GraphOptimizerEnabled option is a first pass, eventually this
 * will need expanding so users can express what transforms (and other nodes) they
 * need in a loaded model and then the optimizer can optimize everything while
 * preserving the required structures.
 *
 * @author paulby
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@PropertiesFactory(ModelCellComponentServerState.class)
public class ModelCellComponentProperties
        extends JPanel implements PropertiesFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/artimport/client/jme/resources/Bundle");
    private CellPropertiesEditor editor = null;
    private ModelCellComponentServerState origState = null;

    /** Creates new form SampleComponentProperties */
    public ModelCellComponentProperties() {
        // Initialize the GUI
        initComponents();

        // Listen for changes to the info text field
        deployedModelURLTF.getDocument().addDocumentListener(
                new InfoTextFieldListener());

        ComboBoxModel cb = new DefaultComboBoxModel(TransparencyMode.values());
        transparencyCB.setModel(cb);
        transparencyCB.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, 
                                                          int index, boolean isSelected, 
                                                          boolean cellHasFocus) 
            {
                TransparencyMode tv = (TransparencyMode) value;
                String valueString = BUNDLE.getString("TransparencyMode_" + tv.name());
                
                return super.getListCellRendererComponent(list, valueString, index, 
                                                          isSelected, cellHasFocus);
            }
        });
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return BUNDLE.getString("Model_Component");
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
        CellServerState state = editor.getCellServerState();
        CellComponentServerState compState = state.getComponentServerState(
                ModelCellComponentServerState.class);
        if (state != null) {
            ModelCellComponentServerState mState =
                    (ModelCellComponentServerState) compState;
            origState = (ModelCellComponentServerState) mState.clone(null);
            deployedModelURLTF.setText(mState.getDeployedModelURL());
            pickingEnabledCB.setSelected(mState.isPickingEnabled());
            lightingEnabledCB.setSelected(mState.isLightingEnabled());
            backfaceCullingEnabledCB.setSelected(mState.isBackfactCullingEnabled());
            graphOptimizationEnabledCB.setSelected(mState.isGraphOptimizationEnabled());
            transparencyCB.setSelectedItem(mState.getTransparencyMode());

            checkDirty();
        }
    }

    /**
     * @inheritDoc()
     */
    public void close() {
        // Do nothing for now.
    }

    /**
     * @inheritDoc()
     */
    public void apply() {
        // Fetch the latest from the info text field and set it.
        CellServerState state = editor.getCellServerState();
        ModelCellComponentServerState compState =
                (ModelCellComponentServerState) state.getComponentServerState(
                ModelCellComponentServerState.class);
        compState.setPickingEnable(pickingEnabledCB.isSelected());
        compState.setLightingEnabled(lightingEnabledCB.isSelected());
        compState.setBackfaceCullingEnabled(backfaceCullingEnabledCB.isSelected());
        compState.setGraphOptimizationEnabled(graphOptimizationEnabledCB.isSelected());
        compState.setTransparencyMode((TransparencyMode) transparencyCB.getSelectedItem());

        editor.addToUpdateList(compState);
    }

    /**
     * @inheritDoc()
     */
    public void restore() {
        // Restore from the original state stored.
        deployedModelURLTF.setText(origState.getDeployedModelURL());
        pickingEnabledCB.setSelected(origState.isPickingEnabled());
        lightingEnabledCB.setSelected(origState.isLightingEnabled());
        backfaceCullingEnabledCB.setSelected(origState.isBackfactCullingEnabled());
        graphOptimizationEnabledCB.setSelected(origState.isGraphOptimizationEnabled());
        transparencyCB.setSelectedItem(origState.getTransparencyMode());

        checkDirty();
    }

    /**
     * Inner class to listen for changes to the text field and fire off dirty
     * or clean indications to the cell properties editor.
     */
    class InfoTextFieldListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            checkDirty();
        }

        public void removeUpdate(DocumentEvent e) {
            checkDirty();
        }

        public void changedUpdate(DocumentEvent e) {
            checkDirty();
        }
    }

    private void checkDirty() {
        if (origState == null) {
            editor.setPanelDirty(ModelCellComponentProperties.class, true);
            return;
        }

        boolean dirty;

        dirty = !deployedModelURLTF.getText().equals(origState.getDeployedModelURL());
        dirty |= (pickingEnabledCB.isSelected() != origState.isPickingEnabled());
        dirty |= (lightingEnabledCB.isSelected() != origState.isLightingEnabled());
        dirty |= (backfaceCullingEnabledCB.isSelected() != origState.isBackfactCullingEnabled());
        dirty |= (graphOptimizationEnabledCB.isSelected() != origState.isGraphOptimizationEnabled());
        dirty |= (transparencyCB.getSelectedItem() != origState.getTransparencyMode());
        
        editor.setPanelDirty(ModelCellComponentProperties.class, dirty);
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
        deployedModelURLTF = new javax.swing.JTextField();
        pickingEnabledCB = new javax.swing.JCheckBox();
        lightingEnabledCB = new javax.swing.JCheckBox();
        backfaceCullingEnabledCB = new javax.swing.JCheckBox();
        graphOptimizationEnabledCB = new javax.swing.JCheckBox();
        printSceneGraphB = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        transparencyCB = new javax.swing.JComboBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/artimport/client/jme/resources/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("ModelCellComponentProperties.jLabel1.text")); // NOI18N

        deployedModelURLTF.setEditable(false);

        pickingEnabledCB.setText(bundle.getString("ModelCellComponentProperties.pickingEnabledCB.text")); // NOI18N
        pickingEnabledCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickingEnabledCBActionPerformed(evt);
            }
        });

        lightingEnabledCB.setText(bundle.getString("ModelCellComponentProperties.lightingEnabledCB.text")); // NOI18N
        lightingEnabledCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightingEnabledCBActionPerformed(evt);
            }
        });

        backfaceCullingEnabledCB.setText(bundle.getString("ModelCellComponentProperties.backfaceCullingEnabledCB.text")); // NOI18N
        backfaceCullingEnabledCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backfaceCullingEnabledCBActionPerformed(evt);
            }
        });

        graphOptimizationEnabledCB.setText(bundle.getString("ModelCellComponentProperties.graphOptimizationEnabledCB.text")); // NOI18N
        graphOptimizationEnabledCB.setToolTipText(bundle.getString("ModelCellComponentProperties.graphOptimizationEnabledCB.toolTipText")); // NOI18N
        graphOptimizationEnabledCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphOptimizationEnabledCBActionPerformed(evt);
            }
        });

        printSceneGraphB.setText(bundle.getString("ModelCellComponentProperties.printSceneGraphB.text")); // NOI18N
        printSceneGraphB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printSceneGraphBActionPerformed(evt);
            }
        });

        jLabel2.setText(bundle.getString("ModelCellComponentProperties.jLabel2.text")); // NOI18N

        transparencyCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "Inverse", "None" }));
        transparencyCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transparencyCBActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deployedModelURLTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
                    .add(printSceneGraphB)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(pickingEnabledCB))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(lightingEnabledCB))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(backfaceCullingEnabledCB))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(graphOptimizationEnabledCB))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(transparencyCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(deployedModelURLTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pickingEnabledCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lightingEnabledCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(backfaceCullingEnabledCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(graphOptimizationEnabledCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(transparencyCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 53, Short.MAX_VALUE)
                .add(printSceneGraphB)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pickingEnabledCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickingEnabledCBActionPerformed
        checkDirty();
    }//GEN-LAST:event_pickingEnabledCBActionPerformed

    private void lightingEnabledCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightingEnabledCBActionPerformed
        checkDirty();
    }//GEN-LAST:event_lightingEnabledCBActionPerformed

    private void printSceneGraphBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printSceneGraphBActionPerformed
        Entity entity = ((CellRendererJME)editor.getCell().getCellRenderer(RendererType.RENDERER_JME)).getEntity();
        RenderComponent rc = entity.getComponent(RenderComponent.class);
        Node root = rc.getSceneRoot();
        print(root, 0);
//        TreeScan.findNode(root, new ProcessNodeInterface() {
//
//            public boolean processNode(Spatial node) {
//                System.err.println(node+"  "+node.getLocalScale());
//                return true;
//            }
//        });
    }//GEN-LAST:event_printSceneGraphBActionPerformed

    private void backfaceCullingEnabledCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backfaceCullingEnabledCBActionPerformed
        checkDirty();
    }//GEN-LAST:event_backfaceCullingEnabledCBActionPerformed

    private void graphOptimizationEnabledCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphOptimizationEnabledCBActionPerformed
        checkDirty();
    }//GEN-LAST:event_graphOptimizationEnabledCBActionPerformed

    private void transparencyCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transparencyCBActionPerformed
        checkDirty();
    }//GEN-LAST:event_transparencyCBActionPerformed

    private void print(Spatial node, int level) {
        for(int i=0; i<level; i++)
            System.err.print(" ");
        System.err.println(node+"  "+node.getLocalRotation());

        if (node instanceof Node) {
            List<Spatial> children = ((Node)node).getChildren();
            if (children!=null)
                for(Spatial sp : children) {
                    print(sp, level+1);
                }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox backfaceCullingEnabledCB;
    private javax.swing.JTextField deployedModelURLTF;
    private javax.swing.JCheckBox graphOptimizationEnabledCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JCheckBox lightingEnabledCB;
    private javax.swing.JCheckBox pickingEnabledCB;
    private javax.swing.JButton printSceneGraphB;
    private javax.swing.JComboBox transparencyCB;
    // End of variables declaration//GEN-END:variables
}
