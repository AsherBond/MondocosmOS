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
package org.jdesktop.wonderland.modules.audiomanager.client;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.Vector3f;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.audiomanager.common.ConeOfSilenceComponentServerState;
import org.jdesktop.wonderland.modules.audiomanager.common.ConeOfSilenceComponentServerState.COSBoundsType;
import org.jdesktop.wonderland.modules.audiomanager.common.VolumeConverter;

/**
 * Properties panel for the cone of silence component.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@PropertiesFactory(ConeOfSilenceComponentServerState.class)
public class ConeOfSilenceComponentProperties extends javax.swing.JPanel
        implements PropertiesFactorySPI {

    private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle");
    private CellPropertiesEditor editor = null;
    private String originalName = null;
    private COSBoundsType originalBoundsType = COSBoundsType.CELL_BOUNDS;
    private Vector3f originalBounds = new Vector3f();
    private boolean originalShowBounds = false;
    private int originalOutsideAudioVolume = 0;
    private SpinnerNumberModel fullVolumeRadiusModel;
    private SpinnerNumberModel xExtentModel;
    private SpinnerNumberModel yExtentModel;
    private SpinnerNumberModel zExtentModel;

    private COSBoundsType boundsType = COSBoundsType.CELL_BOUNDS;

    private BoundsViewerEntity boundsViewerEntity;

    private VolumeConverter volumeConverter;

    /** Creates new form ConeOfSilenceComponentProperties */
    public ConeOfSilenceComponentProperties() {
        initComponents();

	volumeConverter = new VolumeConverter(outsideVolumeSlider.getMaximum());

        // Set the maximum and minimum values for the volume radius spinner
        fullVolumeRadiusModel = new SpinnerNumberModel(new Float(1), new Float(0), 
	    new Float(100), new Float(.1));
        fullVolumeRadiusSpinner.setModel(fullVolumeRadiusModel);

        xExtentModel = new SpinnerNumberModel(new Float(1), new Float(0), 
	    new Float(100), new Float(.1));
        xExtentSpinner.setModel(xExtentModel);

        yExtentModel = new SpinnerNumberModel(new Float(1), new Float(0), 
	    new Float(100), new Float(.1));
        yExtentSpinner.setModel(yExtentModel);

        zExtentModel = new SpinnerNumberModel(new Float(1), new Float(0), 
	    new Float(100), new Float(.1));
        zExtentSpinner.setModel(zExtentModel);

        // Listen for changes to the text field and spinner
        nameTextField.getDocument().addDocumentListener(
                new NameTextFieldListener());

        fullVolumeRadiusModel.addChangeListener(new RadiusChangeListener());
	xExtentModel.addChangeListener(new XExtentChangeListener());
	yExtentModel.addChangeListener(new YExtentChangeListener());
	zExtentModel.addChangeListener(new ZExtentChangeListener());
    }

    /**
     * @{inheritDoc}
     */
    public String getDisplayName() {
        return BUNDLE.getString("Cone_of_Silence");
    }

    /**
     * @{inheritDoc}
     */
    public JPanel getPropertiesJPanel() {
        return this;
    }

    /**
     * @{inheritDoc}
     */
    public void setCellPropertiesEditor(CellPropertiesEditor editor) {
        this.editor = editor;
    }

    /**
     * @{inheritDoc}
     */
    public void open() {
        CellServerState cellServerState = editor.getCellServerState();
        ConeOfSilenceComponentServerState state =
                (ConeOfSilenceComponentServerState) cellServerState.getComponentServerState(
                ConeOfSilenceComponentServerState.class);

        if (state == null) {
            return;
        }

        originalName = state.getName();
	originalBoundsType = state.getBoundsType();
	boundsType = originalBoundsType;
	originalBounds = state.getBounds();
	originalShowBounds = state.getShowBounds();

        originalOutsideAudioVolume = volumeConverter.getVolume((float) state.getOutsideAudioVolume());

	restore();
    }

    private void showBoxBounds(String s, Vector3f extent) {
	float x = Math.round(extent.getX() * 10) / 10f;
	float y = Math.round(extent.getY() * 10) / 10f;
	float z = Math.round(extent.getZ() * 10) / 10f;

	boundsLabel.setText(s + " (" + x + ", " + y + ", " + z + ")");
    }

    /**
     * @{inheritDoc}
     */
    public void close() {
	if (boundsViewerEntity != null) {
	    boundsViewerEntity.dispose();
	    boundsViewerEntity = null;
	}
    }

    /**
     * @{inheritDoc}
     */
    public void apply() {
        // Figure out whether there already exists a server state for the
        // component. If it does not exist, then return, but we could always
        // create a new one really.
        CellServerState cellServerState = editor.getCellServerState();
        ConeOfSilenceComponentServerState state =
                (ConeOfSilenceComponentServerState) cellServerState.getComponentServerState(
                ConeOfSilenceComponentServerState.class);
        if (state == null) {
            return;
        }

        state.setName(nameTextField.getText());

	if (useCellBoundsRadioButton.isSelected()) {
	    state.setBoundsType(COSBoundsType.CELL_BOUNDS);
	} else if (specifyRadiusRadioButton.isSelected()) {
	    state.setBoundsType(COSBoundsType.SPHERE);
	    state.setBounds(new Vector3f((Float) fullVolumeRadiusModel.getValue(), 0f, 0));
	} else {
	    state.setBoundsType(COSBoundsType.BOX);
	    state.setBounds(new Vector3f((Float) xExtentSpinner.getValue(), 
		(Float) yExtentSpinner.getValue(), (Float) zExtentSpinner.getValue())); 
	}
	
        state.setOutsideAudioVolume(volumeConverter.getVolume(outsideVolumeSlider.getValue()));

	state.setShowBounds(showBoundsCheckBox.isSelected());

        editor.addToUpdateList(state);
    }

    /**
     * @{inheritDoc}
     */
    private boolean inRestore;

    public void restore() {
        // Reset the original values to the GUI

	inRestore = true;
        nameTextField.setText(originalName);

	fullVolumeRadiusSpinner.setEnabled(false);
	xExtentSpinner.setEnabled(false);
	yExtentSpinner.setEnabled(false);
	zExtentSpinner.setEnabled(false);

	if (originalBoundsType.equals(COSBoundsType.CELL_BOUNDS)) {
	    useCellBoundsRadioButton.setSelected(true);
	} else if (originalBoundsType.equals(COSBoundsType.SPHERE)) {
	    specifyRadiusRadioButton.setSelected(true);
		
	    fullVolumeRadiusSpinner.setEnabled(true);
	    fullVolumeRadiusSpinner.setValue(originalBounds.getX());
	} else {
	    specifyBoxRadioButton.setSelected(true);
	    fullVolumeRadiusSpinner.setEnabled(false);
	    xExtentSpinner.setEnabled(true);
	    yExtentSpinner.setEnabled(true);
	    zExtentSpinner.setEnabled(true);
	    xExtentSpinner.setValue(originalBounds.getX());
	    yExtentSpinner.setValue(originalBounds.getY());
	    zExtentSpinner.setValue(originalBounds.getZ());
	}
	
        outsideVolumeSlider.setValue(originalOutsideAudioVolume);
        fullVolumeRadiusSpinner.setValue(originalBounds.getX());

	showBoundsCheckBox.setSelected(originalShowBounds);

	BoundingVolume bounds = editor.getCell().getLocalBounds();

	if (bounds instanceof BoundingSphere) {
	    float radius = ((BoundingSphere) bounds).getRadius();
	    
            String text = BUNDLE.getString("Sphere_With_Radius");
            text = MessageFormat.format(text, (Math.round(radius * 10) / 10f));
            boundsLabel.setText(text);
	} else if (bounds instanceof BoundingBox) {
	    Vector3f extent = new Vector3f();
	    extent = ((BoundingBox) bounds).getExtent(extent);
	    showBoxBounds("Box", extent);
	} else if (bounds instanceof OrientedBoundingBox) {
	    Vector3f extent = ((OrientedBoundingBox) bounds).getExtent();
	    showBoxBounds("OrientedBox", extent);
	} else {
	    boundsLabel.setText(bounds.toString());
	}

	showBounds();
	inRestore = false;
    }

    private void showBounds() {
	if (boundsViewerEntity != null) {
	    boundsViewerEntity.dispose();
	    boundsViewerEntity = null;
	}

	if (showBoundsCheckBox.isSelected() == false) {
	    return;
	}

	boundsViewerEntity = new BoundsViewerEntity(editor.getCell());

	if (useCellBoundsRadioButton.isSelected()) {
	    boundsViewerEntity.showBounds(editor.getCell().getLocalBounds());
	} else if (specifyRadiusRadioButton.isSelected()) {
	    boundsViewerEntity.showBounds(new BoundingSphere(
		(Float) fullVolumeRadiusModel.getValue(), new Vector3f()));
	} else {
	    boundsViewerEntity.showBounds(new BoundingBox(new Vector3f(), 
		(Float) xExtentModel.getValue(), 
		(Float) yExtentModel.getValue(), 
		(Float) zExtentModel.getValue()));
	}
    }

    private boolean isDirty() {
	if (inRestore) {
	    return false;
	}

	if (nameTextField.getText().equals(originalName) == false) {
	    return true;
	}

	if (boundsType.equals(COSBoundsType.CELL_BOUNDS)) {
	    if (originalBoundsType.equals(COSBoundsType.CELL_BOUNDS) == false) {
		return true;
	    }
	} else if (boundsType.equals(COSBoundsType.SPHERE)) {
	    if (originalBoundsType.equals(COSBoundsType.SPHERE) == false) {
		return true;
	    }

	    Float radius = (Float) fullVolumeRadiusModel.getValue();

            if (radius != originalBounds.getX()) {
	        return true;
	    }
	} else {
            if (originalBoundsType.equals(COSBoundsType.BOX) == false) {
		return true;
	    }

	    Float xExtent = (Float) xExtentModel.getValue();

	    if (xExtent != originalBounds.getX()) {
	        return true;
	    }

	    Float yExtent = (Float) yExtentModel.getValue();

	    if (yExtent != originalBounds.getY()) {
	        return true;
	    }

	    Float zExtent = (Float) zExtentModel.getValue();

	    if (zExtent != originalBounds.getZ()) {
	        return true;
	    }
	}

        if (originalOutsideAudioVolume != outsideVolumeSlider.getValue()) {
	    return true;
	}

	if (originalShowBounds != showBoundsCheckBox.isSelected()) {
	    return true;
	}

	return false;
    }

    /**
     * Inner class to listen for changes to the spinner and fire off dirty
     * or clean indications to the cell properties editor
     */
    class RadiusChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (editor != null) {
                editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());

		showBounds();
            }
        }
    }

    /**
     * Inner class to listen for changes to the spinner and fire off dirty
     * or clean indications to the cell properties editor
     */
    class XExtentChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (editor != null) {
                editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());

		showBounds();
            }
        }
    }

    /**
     * Inner class to listen for changes to the spinner and fire off dirty
     * or clean indications to the cell properties editor
     */
    class YExtentChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (editor != null) {
                editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());

		showBounds();
            }
        }
    }

    /**
     * Inner class to listen for changes to the spinner and fire off dirty
     * or clean indications to the cell properties editor
     */
    class ZExtentChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (editor != null) {
                editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());

		showBounds();
            }
        }
    }

    /**
     * Inner class to listen for changes to the text field and fire off dirty
     * or clean indications to the cell properties editor.
     */
    class NameTextFieldListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            checkDirty();
        }

        public void removeUpdate(DocumentEvent e) {
            checkDirty();
        }

        public void changedUpdate(DocumentEvent e) {
            checkDirty();
        }

        private void checkDirty() {
            if (editor != null) {
                editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());
            }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        fullVolumeRadiusSpinner = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        useCellBoundsRadioButton = new javax.swing.JRadioButton();
        specifyRadiusRadioButton = new javax.swing.JRadioButton();
        specifyBoxRadioButton = new javax.swing.JRadioButton();
        xExtentSpinner = new javax.swing.JSpinner();
        yExtentSpinner = new javax.swing.JSpinner();
        zExtentSpinner = new javax.swing.JSpinner();
        boundsLabel = new javax.swing.JLabel();
        showBoundsCheckBox = new javax.swing.JCheckBox();
        outsideVolumeSlider = new javax.swing.JSlider();

        setPreferredSize(new java.awt.Dimension(475, 567));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("ConeOfSilenceComponentProperties.jLabel1.text")); // NOI18N

        fullVolumeRadiusSpinner.setEnabled(false);

        jLabel3.setText(bundle.getString("ConeOfSilenceComponentProperties.jLabel3.text")); // NOI18N

        jLabel4.setText(bundle.getString("ConeOfSilenceComponentProperties.jLabel4.text")); // NOI18N

        buttonGroup1.add(useCellBoundsRadioButton);
        useCellBoundsRadioButton.setSelected(true);
        useCellBoundsRadioButton.setText(bundle.getString("ConeOfSilenceComponentProperties.useCellBoundsRadioButton.text")); // NOI18N
        useCellBoundsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCellBoundsRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(specifyRadiusRadioButton);
        specifyRadiusRadioButton.setText(bundle.getString("ConeOfSilenceComponentProperties.specifyRadiusRadioButton.text")); // NOI18N
        specifyRadiusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specifyRadiusRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(specifyBoxRadioButton);
        specifyBoxRadioButton.setText(bundle.getString("ConeOfSilenceComponentProperties.specifyBoxRadioButton.text")); // NOI18N
        specifyBoxRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specifyBoxRadioButtonActionPerformed(evt);
            }
        });

        xExtentSpinner.setEnabled(false);

        yExtentSpinner.setEnabled(false);

        zExtentSpinner.setEnabled(false);

        showBoundsCheckBox.setText(bundle.getString("ConeOfSilenceComponentProperties.showBoundsCheckBox.text")); // NOI18N
        showBoundsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBoundsCheckBoxActionPerformed(evt);
            }
        });

        outsideVolumeSlider.setMinorTickSpacing(10);
        outsideVolumeSlider.setPaintTicks(true);
        outsideVolumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                outsideVolumeSliderStateChanged(evt);
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
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(useCellBoundsRadioButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(boundsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 245, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(showBoundsCheckBox)
                                .add(290, 290, 290))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(specifyRadiusRadioButton)
                                    .add(specifyBoxRadioButton))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(xExtentSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                                        .add(9, 9, 9)
                                        .add(yExtentSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(zExtentSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                        .add(fullVolumeRadiusSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(218, 218, 218))))
                            .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(outsideVolumeSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {fullVolumeRadiusSpinner, xExtentSpinner, yExtentSpinner, zExtentSpinner}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel1)
                    .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(useCellBoundsRadioButton)
                    .add(boundsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(specifyRadiusRadioButton)
                    .add(fullVolumeRadiusSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(specifyBoxRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(showBoundsCheckBox))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(xExtentSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(yExtentSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(zExtentSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(7, 7, 7)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(outsideVolumeSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(352, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void useCellBoundsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCellBoundsRadioButtonActionPerformed
	if (useCellBoundsRadioButton.isSelected() == false) {
	    return;
	}

	fullVolumeRadiusSpinner.setEnabled(false);
	xExtentSpinner.setEnabled(false);
	yExtentSpinner.setEnabled(false);
	zExtentSpinner.setEnabled(false);
	boundsType = COSBoundsType.CELL_BOUNDS;

	fullVolumeRadiusSpinner.setEnabled(false);
	xExtentSpinner.setEnabled(false);
	yExtentSpinner.setEnabled(false);
	zExtentSpinner.setEnabled(false);

        if (editor != null) {
            editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());

	    showBounds();
	}
    }//GEN-LAST:event_useCellBoundsRadioButtonActionPerformed

    private void specifyRadiusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specifyRadiusRadioButtonActionPerformed
	if (specifyRadiusRadioButton.isSelected() == false) {
	    return;
	}

	fullVolumeRadiusSpinner.setEnabled(true);
	xExtentSpinner.setEnabled(false);
	yExtentSpinner.setEnabled(false);
	zExtentSpinner.setEnabled(false);
	boundsType = COSBoundsType.SPHERE;

        if (editor != null) {
            editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());

	    showBounds();
	}
    }//GEN-LAST:event_specifyRadiusRadioButtonActionPerformed

    private void specifyBoxRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specifyBoxRadioButtonActionPerformed
	if (specifyBoxRadioButton.isSelected() == false) {
	    return;
	}

	fullVolumeRadiusSpinner.setEnabled(false);
	xExtentSpinner.setEnabled(true);
	yExtentSpinner.setEnabled(true);
	zExtentSpinner.setEnabled(true);
	boundsType = COSBoundsType.BOX;

        if (editor != null) {
            editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());

	    showBounds();
	}
    }//GEN-LAST:event_specifyBoxRadioButtonActionPerformed

    private void showBoundsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showBoundsCheckBoxActionPerformed
	if (editor == null) {
	    return;
	}

        editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());

	showBounds();
    }//GEN-LAST:event_showBoundsCheckBoxActionPerformed

    private void outsideVolumeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_outsideVolumeSliderStateChanged
        if (editor == null) {
            return;
        }

        editor.setPanelDirty(ConeOfSilenceComponentProperties.class, isDirty());
}//GEN-LAST:event_outsideVolumeSliderStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel boundsLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JSpinner fullVolumeRadiusSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JSlider outsideVolumeSlider;
    private javax.swing.JCheckBox showBoundsCheckBox;
    private javax.swing.JRadioButton specifyBoxRadioButton;
    private javax.swing.JRadioButton specifyRadiusRadioButton;
    private javax.swing.JRadioButton useCellBoundsRadioButton;
    private javax.swing.JSpinner xExtentSpinner;
    private javax.swing.JSpinner yExtentSpinner;
    private javax.swing.JSpinner zExtentSpinner;
    // End of variables declaration//GEN-END:variables
}
