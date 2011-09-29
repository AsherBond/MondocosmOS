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
package org.jdesktop.wonderland.modules.affordances.client;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.scenemanager.SceneManager;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * A panel to display affordance items on the HUD.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class PositionHUDPanel extends javax.swing.JPanel {

    /* The currently selected Cell and its movable component */
    private Cell selectedCell;
    private MovableComponent movableComponent;

    /* Various listener on the Cell and Swing JSpinners */
    private ComponentChangeListener componentListener;
    private TransformChangeListener transformListener;
    private ChangeListener translationListener;
    private ChangeListener rotationListener;
    private ChangeListener scaleListener;

    /* Models for the Swing JSpinners */
    private SpinnerNumberModel xTranslationModel;
    private SpinnerNumberModel yTranslationModel;
    private SpinnerNumberModel zTranslationModel;
    private SpinnerNumberModel xScaleModel;
    private SpinnerNumberModel yScaleModel;
    private SpinnerNumberModel zScaleModel;
    private SpinnerNumberModel xRotationModel;
    private SpinnerNumberModel yRotationModel;
    private SpinnerNumberModel zRotationModel;

    /*
     * This boolean indicates whether the values of the spinners are being
     * set programmatically, e.g. when a transform changed event has been
     * received from the Cell. In such a case, we do not want to generate a
     * new message to the movable component
     */
    private boolean setLocal = false;

    /** Creates new form AffordanceHUDPanel */
    public PositionHUDPanel() {
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

        value = new Float(1);
        min = new Float(0);
        xScaleModel = new SpinnerNumberModel(value, min, max, step);
        yScaleModel = new SpinnerNumberModel(value, min, max, step);
        zScaleModel = new SpinnerNumberModel(value, min, max, step);
        scaleTF.setModel(xScaleModel);

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

        // Listen for changes, if there is a movable component added or removed
        // update the state of the fields
        componentListener = new ComponentChangeListener() {

            public void componentChanged(
                    Cell cell, ChangeType type, CellComponent component) {
                if ((type == ChangeType.ADDED) &&
                        component instanceof MovableComponent) {
                    movableComponent = (MovableComponent) component;

                    // We must enable the GUI components in the AWT Event
                    // Thread.
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            setGUIEnabled(true);
                        }
                    });
                }
            }
        };

        // Listen for changes to the cell transform that may be done by other
        // parts of this client or other clients.
        transformListener = new TransformChangeListener() {

            public void transformChanged(Cell cell, ChangeSource source) {
                // We must call this in the AWT Event Thread
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        updateGUI();
                    }
                });
            }
        };

        // Listen for changes to the translation values and update the cell as
        // a result. Only update the result if it doesn't happen because the
        // value in the spinner is changed programmatically. The value of
        // 'setLocal' is set always in the AWT Event Thread, the same thread
        // as this listener.
        translationListener = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (setLocal == false) {
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
                    updateScale();
                }
            }
        };
        xScaleModel.addChangeListener(scaleListener);
        yScaleModel.addChangeListener(scaleListener);
        zScaleModel.addChangeListener(scaleListener);

        // Listen for focus gained on the text field's of the spinners. Select
        // all of the text
        FocusListener focusListener = new FocusAdapter() {

            @Override
            public void focusGained(final FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        ((JFormattedTextField) e.getSource()).selectAll();
                    }
                });
            }
        };
        ((JSpinner.DefaultEditor) translationXTF.getEditor()).getTextField().
                addFocusListener(focusListener);
        ((JSpinner.DefaultEditor) translationYTF.getEditor()).getTextField().
                addFocusListener(focusListener);
        ((JSpinner.DefaultEditor) translationZTF.getEditor()).getTextField().
                addFocusListener(focusListener);
        ((JSpinner.DefaultEditor) rotationXTF.getEditor()).getTextField().
                addFocusListener(focusListener);
        ((JSpinner.DefaultEditor) rotationYTF.getEditor()).getTextField().
                addFocusListener(focusListener);
        ((JSpinner.DefaultEditor) rotationZTF.getEditor()).getTextField().
                addFocusListener(focusListener);
        ((JSpinner.DefaultEditor) scaleTF.getEditor()).getTextField().
                addFocusListener(focusListener);

        // Turn off the GUI initially, until we have a selected cell
        clearGUI();
        setGUIEnabled(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        translationPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        xTranslationPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        translationXTF = new javax.swing.JSpinner();
        yTranslationPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        translationYTF = new javax.swing.JSpinner();
        zTranslationPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        translationZTF = new javax.swing.JSpinner();
        rotationPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        xRotationPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        rotationXTF = new javax.swing.JSpinner();
        yRotationPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        rotationYTF = new javax.swing.JSpinner();
        zRotationPanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        rotationZTF = new javax.swing.JSpinner();
        resizePanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        resizeSubPanel = new javax.swing.JPanel();
        scaleTF = new javax.swing.JSpinner();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        setLayout(new java.awt.GridBagLayout());

        translationPanel.setLayout(new java.awt.GridLayout(4, 1));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/affordances/client/resources/Bundle"); // NOI18N
        jLabel4.setText(bundle.getString("PositionHUDPanel.jLabel4.text")); // NOI18N
        translationPanel.add(jLabel4);

        xTranslationPanel.setLayout(new javax.swing.BoxLayout(xTranslationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText(bundle.getString("PositionHUDPanel.jLabel1.text")); // NOI18N
        xTranslationPanel.add(jLabel1);

        translationXTF.setMinimumSize(new java.awt.Dimension(100, 30));
        translationXTF.setPreferredSize(new java.awt.Dimension(100, 30));
        xTranslationPanel.add(translationXTF);

        translationPanel.add(xTranslationPanel);

        yTranslationPanel.setLayout(new javax.swing.BoxLayout(yTranslationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setForeground(new java.awt.Color(0, 255, 0));
        jLabel2.setText(bundle.getString("PositionHUDPanel.jLabel2.text")); // NOI18N
        yTranslationPanel.add(jLabel2);

        translationYTF.setMinimumSize(new java.awt.Dimension(100, 30));
        translationYTF.setPreferredSize(new java.awt.Dimension(100, 30));
        yTranslationPanel.add(translationYTF);

        translationPanel.add(yTranslationPanel);

        zTranslationPanel.setLayout(new javax.swing.BoxLayout(zTranslationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setForeground(new java.awt.Color(0, 0, 255));
        jLabel3.setText(bundle.getString("PositionHUDPanel.jLabel3.text")); // NOI18N
        zTranslationPanel.add(jLabel3);

        translationZTF.setMinimumSize(new java.awt.Dimension(100, 30));
        translationZTF.setPreferredSize(new java.awt.Dimension(100, 30));
        zTranslationPanel.add(translationZTF);

        translationPanel.add(zTranslationPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(translationPanel, gridBagConstraints);

        rotationPanel.setLayout(new java.awt.GridLayout(4, 1));

        jLabel10.setText(bundle.getString("PositionHUDPanel.jLabel10.text")); // NOI18N
        rotationPanel.add(jLabel10);

        xRotationPanel.setLayout(new javax.swing.BoxLayout(xRotationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel11.setForeground(new java.awt.Color(255, 0, 0));
        jLabel11.setText(bundle.getString("PositionHUDPanel.jLabel11.text")); // NOI18N
        xRotationPanel.add(jLabel11);

        rotationXTF.setMinimumSize(new java.awt.Dimension(100, 30));
        rotationXTF.setPreferredSize(new java.awt.Dimension(100, 30));
        xRotationPanel.add(rotationXTF);

        rotationPanel.add(xRotationPanel);

        yRotationPanel.setLayout(new javax.swing.BoxLayout(yRotationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel12.setForeground(new java.awt.Color(0, 255, 0));
        jLabel12.setText(bundle.getString("PositionHUDPanel.jLabel12.text")); // NOI18N
        yRotationPanel.add(jLabel12);

        rotationYTF.setMinimumSize(new java.awt.Dimension(100, 30));
        rotationYTF.setPreferredSize(new java.awt.Dimension(100, 30));
        yRotationPanel.add(rotationYTF);

        rotationPanel.add(yRotationPanel);

        zRotationPanel.setLayout(new javax.swing.BoxLayout(zRotationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel13.setForeground(new java.awt.Color(0, 0, 255));
        jLabel13.setText(bundle.getString("PositionHUDPanel.jLabel13.text")); // NOI18N
        zRotationPanel.add(jLabel13);

        rotationZTF.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rotationZTF.setMinimumSize(new java.awt.Dimension(100, 30));
        rotationZTF.setPreferredSize(new java.awt.Dimension(100, 30));
        zRotationPanel.add(rotationZTF);

        rotationPanel.add(zRotationPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(rotationPanel, gridBagConstraints);

        resizePanel.setLayout(new java.awt.GridLayout(2, 0));

        jLabel14.setText(bundle.getString("PositionHUDPanel.jLabel14.text")); // NOI18N
        resizePanel.add(jLabel14);

        resizeSubPanel.setLayout(new java.awt.GridLayout(1, 0));

        scaleTF.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scaleTF.setMinimumSize(new java.awt.Dimension(100, 30));
        scaleTF.setPreferredSize(new java.awt.Dimension(100, 30));
        resizeSubPanel.add(scaleTF);

        resizePanel.add(resizeSubPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(resizePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Resets the values in the GUI back to 0.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     */
    private void clearGUI() {
        // Says that we are changing the values of the spinners
        // programmatically. This prevents the values from being sent
        // back to the Cell via the movable component.
        setLocalChanges(true);

        try {
            translationXTF.setValue(0.0f);
            translationYTF.setValue(0.0f);
            translationZTF.setValue(0.0f);

            rotationXTF.setValue(0.0f);
            rotationYTF.setValue(0.0f);
            rotationZTF.setValue(0.0f);

            scaleTF.setValue(1.0f);
        } finally {
            setLocalChanges(false);
        }
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
            CellTransform cellTransform = selectedCell.getLocalTransform();
            cellTransform.setTranslation(translation);
            movableComponent.localMoveRequest(cellTransform);
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

        Quaternion newRotation = new Quaternion(new float[]{x, y, z});
        if (movableComponent != null) {
            CellTransform cellTransform = selectedCell.getLocalTransform();
            cellTransform.setRotation(newRotation);
            movableComponent.localMoveRequest(cellTransform);
        }
    }

    /**
     * Updates the scale of the cell with the given values of the GUI.
     */
    private void updateScale() {
        float x = (Float) xScaleModel.getValue();

        if (movableComponent != null) {
            CellTransform cellTransform = selectedCell.getLocalTransform();
            cellTransform.setScaling(x);
            movableComponent.localMoveRequest(cellTransform);
        }
    }

    /**
     * Updates the GUI items in this panel for the currently selected cell. If
     * there is nothing selected, do nothing.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     */
    public void updateGUI() {
        // Fetch the currently selected Cell. If none, then do nothing
        setSelectedCell(getSelectedCell());
        if (selectedCell == null) {
            clearGUI();
            return;
        }

        // Fetch the current transform from the movable component
        CellTransform cellTransform = selectedCell.getLocalTransform();
        final Vector3f translation = cellTransform.getTranslation(null);
        Quaternion rotation = cellTransform.getRotation(null);
        final Vector3f scale = cellTransform.getScaling(null);
        final float[] angles = rotation.toAngles(new float[3]);

        // Says that we are changing the values of the spinners
        // programmatically. This prevents the values from being sent
        // back to the Cell via the movable component.
        setLocalChanges(true);

        try {
            // Do all actions to update the GUI in the AWT Event Thread.
            // Update the translation spinners
            translationXTF.setValue(translation.x);
            translationYTF.setValue(translation.y);
            translationZTF.setValue(translation.z);

            // Update the rotation spinners
            rotationXTF.setValue((float) Math.toDegrees(angles[0]));
            rotationYTF.setValue((float) Math.toDegrees(angles[1]));
            rotationZTF.setValue((float) Math.toDegrees(angles[2]));

            // Update the scale spinners only if they have changes
            scaleTF.setValue((float) scale.x);
        } finally {
            setLocalChanges(false);
        }
    }

    /**
     * Sets whether the GUI components are active (enabled).
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     * 
     * @param enabled True to make the GUI components enabled, false to not
     */
    private void setGUIEnabled(boolean enabled) {
        translationXTF.setEnabled(enabled);
        translationYTF.setEnabled(enabled);
        translationZTF.setEnabled(enabled);
        rotationXTF.setEnabled(enabled);
        rotationYTF.setEnabled(enabled);
        rotationZTF.setEnabled(enabled);
        scaleTF.setEnabled(enabled);
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
    private void setLocalChanges(boolean isLocal) {
        setLocal = isLocal;
    }

    /**
     * Handles when a new Cell is selected in the world. This removes the
     * listeners from the old Cell, adds the listeners to the new Cell and
     * updates the GUI.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     */
    private void setSelectedCell(Cell cell) {
        // First remove the listeners from the old Cell if such a Cell exists.
        if (selectedCell != null) {
            selectedCell.removeComponentChangeListener(componentListener);
            selectedCell.removeTransformChangeListener(transformListener);
        }
        selectedCell = cell;

        // If the newly selected Cell is null, we turn off the GUI and return
        if (cell == null) {
            setGUIEnabled(false);
            return;
        }

        // Listen for changes in the transform of the Cell and update the
        // values of the spinners when that happens.
        cell.addTransformChangeListener(transformListener);

        // Listen for when the movable component is added, in case it has not
        // already been added. Set the movable component member variable.
        cell.addComponentChangeListener(componentListener);

        // Fetch the movable component. For now, if it does not exist, then
        // turn off everything
        movableComponent = cell.getComponent(MovableComponent.class);
        if (movableComponent == null) {
            setGUIEnabled(false);
        } else {
            setGUIEnabled(true);
        }
    }

    /**
     * Returns the currently selected cell, null if no cell is currently
     * selected.
     */
    private Cell getSelectedCell() {
        SceneManager manager = SceneManager.getSceneManager();
        List<Entity> entityList = manager.getSelectedEntities();
        if (entityList != null && entityList.size() > 0) {
            return SceneManager.getCellForEntity(entityList.get(0));
        }
        return null;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel resizePanel;
    private javax.swing.JPanel resizeSubPanel;
    private javax.swing.JPanel rotationPanel;
    private javax.swing.JSpinner rotationXTF;
    private javax.swing.JSpinner rotationYTF;
    private javax.swing.JSpinner rotationZTF;
    private javax.swing.JSpinner scaleTF;
    private javax.swing.JPanel translationPanel;
    private javax.swing.JSpinner translationXTF;
    private javax.swing.JSpinner translationYTF;
    private javax.swing.JSpinner translationZTF;
    private javax.swing.JPanel xRotationPanel;
    private javax.swing.JPanel xTranslationPanel;
    private javax.swing.JPanel yRotationPanel;
    private javax.swing.JPanel yTranslationPanel;
    private javax.swing.JPanel zRotationPanel;
    private javax.swing.JPanel zTranslationPanel;
    // End of variables declaration//GEN-END:variables
}
