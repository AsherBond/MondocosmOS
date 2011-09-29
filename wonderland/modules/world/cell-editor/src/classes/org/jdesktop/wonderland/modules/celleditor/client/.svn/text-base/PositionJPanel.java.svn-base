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
package org.jdesktop.wonderland.modules.celleditor.client;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentMessage;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * A special properties editor panel that edits the transform of the cell. It
 * interacts with the Movable component directly.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class PositionJPanel extends JPanel implements PropertiesFactorySPI {

    private static Logger logger = Logger.getLogger(PositionJPanel.class.getName());

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle");

    private CellPropertiesEditor editor = null;
    private MovableComponent movableComponent = null;

    /* Various listener on the Cell and Swing JSpinners */
    private ComponentChangeListener componentListener = null;
    private TransformChangeListener transformListener = null;
    private ChangeListener translationListener = null;
    private ChangeListener rotationListener = null;
    private ChangeListener scaleListener = null;

    /* Models for the Swing JSpinners */
    private SpinnerNumberModel xTranslationModel = null;
    private SpinnerNumberModel yTranslationModel = null;
    private SpinnerNumberModel zTranslationModel = null;
    private SpinnerNumberModel xScaleModel = null;
    private SpinnerNumberModel yScaleModel = null;
    private SpinnerNumberModel zScaleModel = null;
    private SpinnerNumberModel xRotationModel = null;
    private SpinnerNumberModel yRotationModel = null;
    private SpinnerNumberModel zRotationModel = null;

    /*
     * This boolean indicates whether the values of the spinners are being
     * set programmatically, e.g. when a transform changed event has been
     * received from the Cell. In such a case, we do not want to generate a
     * new message to the movable component
     */
    private boolean setLocal = false;

    // True if changes have actually been made in this GUI. Initially this
    // value is false. If the values change by an external source, it remains
    // null.
    private boolean isLocalChangesMade = false;

    /*
     * The original values when the properties sheet is first set to a Cell.
     * These original values will be used when the cancel() method is invoked,
     * to revert any changes.
     */
    private Vector3f originalTranslation = null;
    private Quaternion originalRotation = null;
    private Vector3f originalScaling = null;

    /**
     * Default constructor, creates the GUI and sets up the JSpinners with the
     * proper model and editor.
     */
    public PositionJPanel() {
        // Initialize the GUI components
        initComponents();

        // Set the maximum and minimum values for each
        Float value = new Float(0);
        Float min = new Float(Float.NEGATIVE_INFINITY);
        Float max = new Float(Float.POSITIVE_INFINITY);
        Float step = new Float(0.1);
        xTranslationModel = new SpinnerNumberModel(value, min, max, step);
        yTranslationModel = new SpinnerNumberModel(value, min, max, step);
        zTranslationModel = new SpinnerNumberModel(value, min, max, step);
        translationXTF.setModel(xTranslationModel);
        translationYTF.setModel(yTranslationModel);
        translationZTF.setModel(zTranslationModel);
        translationXTF.setEditor(
                new JSpinner.NumberEditor(translationXTF, "########0.00"));
        translationYTF.setEditor(
                new JSpinner.NumberEditor(translationYTF, "########0.00"));
        translationZTF.setEditor(
                new JSpinner.NumberEditor(translationZTF, "########0.00"));

        value = new Float(1);
        min = new Float(0);
        xScaleModel = new SpinnerNumberModel(value, min, max, step);
        yScaleModel = new SpinnerNumberModel(value, min, max, step);
        zScaleModel = new SpinnerNumberModel(value, min, max, step);
        scaleXTF.setModel(xScaleModel);
        scaleXTF.setEditor(new JSpinner.NumberEditor(scaleXTF, "########0.00"));


        value = new Float(0);
        min = new Float(-360);
        max = new Float(360);
        step = new Float(1);
        xRotationModel = new SpinnerNumberModel(value, min, max, step);
        yRotationModel = new SpinnerNumberModel(value, min, max, step);
        zRotationModel = new SpinnerNumberModel(value, min, max, step);
        rotationXTF.setModel(xRotationModel);
        rotationYTF.setModel(yRotationModel);
        rotationZTF.setModel(zRotationModel);
        rotationXTF.setEditor(
                new JSpinner.NumberEditor(rotationXTF, "########0.00"));
        rotationYTF.setEditor(
                new JSpinner.NumberEditor(rotationYTF, "########0.00"));
        rotationZTF.setEditor(
                new JSpinner.NumberEditor(rotationZTF, "########0.00"));

        // Listen for changes to the translation values and update the cell as
        // a result. Only update the result if it doesn't happen because the
        // value in the spinner is changed programmatically. The value of
        // 'setLocal' is set always in the AWT Event Thread, the same thread
        // as this listener.
        translationListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (setLocal == false) {
                    isLocalChangesMade = true;
                    updateTranslation();
                }
            }
        };
        xTranslationModel.addChangeListener(translationListener);
        yTranslationModel.addChangeListener(translationListener);
        zTranslationModel.addChangeListener(translationListener);

        // Listen for changes to the rotation values and update the cell as a
        // result. See the comments above for 'translationListener' too.
        rotationListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (setLocal == false) {
                    isLocalChangesMade = true;
                    updateRotation();
                }
            }
        };
        xRotationModel.addChangeListener(rotationListener);
        yRotationModel.addChangeListener(rotationListener);
        zRotationModel.addChangeListener(rotationListener);

        // Listen for changes to the scale values and update the cell as a
        // result. See the comments above for 'translationListener' too.
        scaleListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (setLocal == false) {
                    isLocalChangesMade = true;
                    updateScale();
                }
            }
        };
        xScaleModel.addChangeListener(scaleListener);
        yScaleModel.addChangeListener(scaleListener);
        zScaleModel.addChangeListener(scaleListener);

        // Create a listener for changes in the components on a Cell. This is
        // used to fetch the movable component if it is added.
        componentListener = new ComponentChangeListener() {
            public void componentChanged(Cell cell, ChangeType type, CellComponent component) {
                if (type == ChangeType.ADDED && component instanceof MovableComponent) {
                    movableComponent = (MovableComponent) component;
                    translationXTF.setEnabled(true);
                    translationYTF.setEnabled(true);
                    translationZTF.setEnabled(true);
                    rotationXTF.setEnabled(true);
                    rotationYTF.setEnabled(true);
                    rotationZTF.setEnabled(true);
                    scaleXTF.setEnabled(true);
                }
            }
        };

        // Create a listener for changes in the Cell's transform and update
        // the GUI appropriately. We need to do this updating of the GUI in the
        // AWT Event Thread. We also want to make a note that the values are
        // being set programmatically so they do not spawn extra messages to the
        // movable component.
        transformListener = new TransformChangeListener() {
            public void transformChanged(Cell cell, ChangeSource source) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            setLocalChanges(true);
                            updateGUI();
                        } finally {
                            setLocalChanges(false);
                        }
                    }
                });
            }
        };
    }

    /**
     * Sets whether the changes being made to the JSpinners are doing so
     * programmatically, rather than via a movable event. This is used to
     * make sure that requests to the movable component are not made at the
     * wrong time.
     *
     * @param isLocal True to indicate the JSpinner values are being set
     * programmatically.
     */
    void setLocalChanges(boolean isLocal) {
        setLocal = isLocal;
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return BUNDLE.getString("Position");
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
        // Fetch the current Cell, make sure it has the movable component and
        // turn everything on.
        Cell cell = editor.getCell();
        if (cell == null) {
            return;
        }

        // Store the values currently on the Cell. This will be used when we
        // go to restore the values later
        CellTransform transform = cell.getLocalTransform();
        originalTranslation = transform.getTranslation(null);
        originalRotation = transform.getRotation(null);
        originalScaling = transform.getScaling(null);

        // OWL issue #159: we are now up to date, no local changes have been made
        isLocalChangesMade = false;

        movableComponent = cell.getComponent(MovableComponent.class);
        if (movableComponent == null) {
            translationXTF.setEnabled(false);
            translationYTF.setEnabled(false);
            translationZTF.setEnabled(false);
            rotationXTF.setEnabled(false);
            rotationYTF.setEnabled(false);
            rotationZTF.setEnabled(false);
            scaleXTF.setEnabled(false);
        }

        // Listen for changes, if there is a movable component added or removed
        // update the state of the fields. It is ok if open() is called more
        // than once, this method call will not add duplicate listeners.
        cell.addComponentChangeListener(componentListener);

        // If it does not exist, attempt to add the movable component. Create
        // a suitable message using only the server-side movable component
        // class name and send over the cell channel.
        if (movableComponent == null) {
            String className = "org.jdesktop.wonderland.server.cell." +
                    "MovableComponentMO";
            CellServerComponentMessage cscm = 
                    CellServerComponentMessage.newAddMessage(
                    cell.getCellID(), className);
            ResponseMessage response = cell.sendCellMessageAndWait(cscm);
            if (response instanceof ErrorMessage) {
                logger.log(Level.WARNING, "Unable to add movable component " +
                        "for Cell " + cell.getName() + " with ID " + 
                        cell.getCellID(),
                        ((ErrorMessage) response).getErrorCause());
            }
        }

        // Listen for changes in the Cell's transform. It is ok if open() is
        // called more than once, this method call will not add duplicate
        // listeners.
        cell.addTransformChangeListener(transformListener);

        // Update the GUI, set local changes to true so that messages to the
        // movable component are NOT generated.
        setLocalChanges(true);
        try {
            updateGUI();
        } finally {
            setLocalChanges(false);
        }
    }

    /**
     * @inheritDoc()
     */
    public void apply() {
        // OWL issue #159: we no longer want to revert back to old values
        // on restore, so make sure that we have the latest values on record
        CellTransform transform = editor.getCell().getLocalTransform();
        originalTranslation = transform.getTranslation(null);
        originalRotation = transform.getRotation(null);
        originalScaling = transform.getScaling(null);

        // OWL issue #159: also mark that there are currently no changes 
        isLocalChangesMade = false;
    }

    /**
     * @inheritDoc()
     */
    public void restore() {
        // Fetch the current Cell, there should be one, although there may not
        // be a movable component.
        Cell cell = editor.getCell();
        if (movableComponent == null) {
            logger.warning("Unable to find movable component on Cell " +
                    cell.getName());
            return;
        }

        // We revert the values to when this property sheet was originally open
        // for the Cell. We only do this if we can find a movable component
        // (which should be the case if we made any changes) AND if we have
        // explictly made changes via the GUI. This second check is necessary
        // because if a user edits the position via the affordances, and clicks
        // off the Cell in the Cell Editor, it will revert the position, when
        // not desired. (See Issue #688).
        if (isLocalChangesMade == true) {
            CellTransform transform = cell.getLocalTransform();
            transform.setTranslation(originalTranslation);
            transform.setScaling(originalScaling.x);
            transform.setRotation(originalRotation);
            movableComponent.localMoveRequest(transform);

            // OWL issue #159: local changes have now been restored, so we set this
            // variable back to false
            isLocalChangesMade = false;
        }
    }

    /**
     * @inheritDoc()
     */
    public void close() {
        // First restore any existing changes
        restore();

        // Finally remove any existing listeners from the Cell
        Cell cell = editor.getCell();
        cell.removeComponentChangeListener(componentListener);
        cell.removeTransformChangeListener(transformListener);
    }

    /**
     * Updates the GUI based upon the given CellTransform
     */
    public void updateGUI() {
        // Fetch the current transform from the movable component
        Cell cell = editor.getCell();
        CellTransform cellTransform = cell.getLocalTransform();
        Vector3f translation = cellTransform.getTranslation(null);
        Quaternion rotation = cellTransform.getRotation(null);
        Vector3f scale = cellTransform.getScaling(null);
        float[] angles = rotation.toAngles(new float[3]);

        // Update the translation spinners
        translationXTF.setValue(translation.x);
        translationYTF.setValue(translation.y);
        translationZTF.setValue(translation.z);

        rotationXTF.setValue((float) Math.toDegrees(angles[0]));
        rotationYTF.setValue((float) Math.toDegrees(angles[1]));
        rotationZTF.setValue((float) Math.toDegrees(angles[2]));

        // Update the scale spinners only if they have changes
        scaleXTF.setValue((float) scale.x);
    }

    /**
     * Updates the translation of the cell with the given values of the GUI.
     */
    private void updateTranslation() {
        float x = (Float) xTranslationModel.getValue();
        float y = (Float) yTranslationModel.getValue();
        float z = (Float) zTranslationModel.getValue();

        Vector3f translation = new Vector3f(x, y, z);
        if (movableComponent != null) {
            Cell cell = editor.getCell();
            CellTransform cellTransform = cell.getLocalTransform();
            cellTransform.setTranslation(translation);
            movableComponent.localMoveRequest(cellTransform);
            editor.setPanelDirty(PositionJPanel.class, true);
        }
    }

    /**
     * Updates the rotation of the cell with the given values of the GUI.
     */
    private void updateRotation() {
        // Fetch the x, y, z rotation values from the GUI in degrees
        float x = (Float) xRotationModel.getValue();
        float y = (Float) yRotationModel.getValue();
        float z = (Float) zRotationModel.getValue();

        // Convert to radians
        x = (float) Math.toRadians(x);
        y = (float) Math.toRadians(y);
        z = (float) Math.toRadians(z);

        Quaternion newRotation = new Quaternion(new float[] { x, y, z });
        if (movableComponent != null) {
            Cell cell = editor.getCell();
            CellTransform cellTransform = cell.getLocalTransform();
            cellTransform.setRotation(newRotation);
            movableComponent.localMoveRequest(cellTransform);
            editor.setPanelDirty(PositionJPanel.class, true);
        }
    }

    /**
     * Updates the scale of the cell with the given values of the GUI.
     */
    private void updateScale() {
        float x = (Float) xScaleModel.getValue();

        if (movableComponent != null) {
            Cell cell = editor.getCell();
            CellTransform cellTransform = cell.getLocalTransform();
            cellTransform.setScaling(x);
            movableComponent.localMoveRequest(cellTransform);
            editor.setPanelDirty(PositionJPanel.class, true);
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

        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        translationXTF = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        translationYTF = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        translationZTF = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        rotationYTF = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        rotationXTF = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        rotationZTF = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        scaleXTF = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(342, 427));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle"); // NOI18N
        jLabel7.setText(bundle.getString("PositionJPanel.jLabel7.text")); // NOI18N

        jLabel6.setText(bundle.getString("PositionJPanel.jLabel6.text")); // NOI18N

        jLabel8.setText(bundle.getString("PositionJPanel.jLabel8.text")); // NOI18N

        jLabel9.setText(bundle.getString("PositionJPanel.jLabel9.text")); // NOI18N

        jLabel10.setText(bundle.getString("PositionJPanel.jLabel10.text")); // NOI18N

        jLabel13.setText(bundle.getString("PositionJPanel.jLabel13.text")); // NOI18N

        jLabel11.setText(bundle.getString("PositionJPanel.jLabel11.text")); // NOI18N

        jLabel12.setText(bundle.getString("PositionJPanel.jLabel12.text")); // NOI18N

        jLabel14.setText(bundle.getString("PositionJPanel.jLabel14.text")); // NOI18N

        jLabel1.setText(bundle.getString("PositionJPanel.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("PositionJPanel.jLabel2.text")); // NOI18N

        jLabel3.setText(bundle.getString("PositionJPanel.jLabel3.text")); // NOI18N

        jLabel4.setText(bundle.getString("PositionJPanel.jLabel4.text")); // NOI18N

        jLabel5.setText(bundle.getString("PositionJPanel.jLabel5.text")); // NOI18N

        jLabel15.setText(bundle.getString("PositionJPanel.jLabel15.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(translationXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel8)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(translationYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel9)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(translationZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel2)
                            .add(jLabel1)))
                    .add(jLabel7)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel13)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(rotationYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel5))
                            .add(layout.createSequentialGroup()
                                .add(jLabel11)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(rotationXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel4))
                            .add(layout.createSequentialGroup()
                                .add(jLabel12)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(scaleXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(layout.createSequentialGroup()
                                        .add(rotationZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel15))))))
                    .add(jLabel10)
                    .add(jLabel14))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(translationXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(translationYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(translationZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9)
                    .add(jLabel3))
                .add(18, 18, 18)
                .add(jLabel10)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(rotationXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rotationYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rotationZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel12)
                    .add(jLabel15))
                .add(18, 18, 18)
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scaleXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSpinner rotationXTF;
    private javax.swing.JSpinner rotationYTF;
    private javax.swing.JSpinner rotationZTF;
    private javax.swing.JSpinner scaleXTF;
    private javax.swing.JSpinner translationXTF;
    private javax.swing.JSpinner translationYTF;
    private javax.swing.JSpinner translationZTF;
    // End of variables declaration//GEN-END:variables
}
