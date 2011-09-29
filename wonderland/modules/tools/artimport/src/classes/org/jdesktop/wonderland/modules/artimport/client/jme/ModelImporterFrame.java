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

import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.artimport.ImportSettings;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.image.Texture;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.artimport.ImportedModel;
import org.jdesktop.wonderland.client.jme.utils.traverser.ProcessNodeInterface;
import org.jdesktop.wonderland.client.jme.utils.traverser.TreeScan;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 *
 * @author  paulby
 * @author  Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class ModelImporterFrame extends javax.swing.JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(ModelImporterFrame.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/artimport/client/jme/resources/Bundle");
    private File lastModelDir;
//    private GeometryStatsDialog geometryStatsDialog = null;
    private TransformChangeListener userMotionListener = null;
    private ChangeListener translationChangeListener = null;
    private ChangeListener rotationChangeListener = null;
    private ChangeListener scaleChangeListener = null;
    private Vector3f currentTranslation = new Vector3f();
    private Vector3f currentRotationValues = new Vector3f();
    private Vector3f currentScale = new Vector3f();
    private Matrix3f currentRotation = new Matrix3f();
    private ImportSessionFrame sessionFrame;
    private ImportSettings importSettings = null;
    private ImportedModel importedModel = null;
    private TransformProcessorComponent transformProcessor;

    /** Creates new form ModelImporterFrame */
    public ModelImporterFrame(ImportSessionFrame session, File lastModelDir) {
        this.lastModelDir = lastModelDir;
        sessionFrame = session;
        initComponents();

        textureTable.setModel(new DefaultTableModel() {

            String[] names = new String[]{
                BUNDLE.getString("Original_Filename"),
                BUNDLE.getString("Wonderland_Path"),
                BUNDLE.getString("Wonderland_Filename")
            };
            Class[] types = new Class[]{
                String.class, String.class, String.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false
            };

            @Override
            public String getColumnName(int column) {
                return names[column];
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        Float value = new Float(0);
        Float min = new Float(Float.NEGATIVE_INFINITY);
        Float max = new Float(Float.POSITIVE_INFINITY);
        Float step = new Float(0.1);
        SpinnerNumberModel translationX =
                new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel translationY =
                new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel translationZ =
                new SpinnerNumberModel(value, min, max, step);
        translationXTF.setModel(translationX);
        translationYTF.setModel(translationY);
        translationZTF.setModel(translationZ);

        value = new Float(1);
        SpinnerNumberModel scaleX =
                new SpinnerNumberModel(value, min, max, step);
        scaleTF.setModel(scaleX);

        value = new Float(0);
        min = new Float(-360);
        max = new Float(360);
        step = new Float(1);
        SpinnerNumberModel rotationX =
                new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel rotationY =
                new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel rotationZ =
                new SpinnerNumberModel(value, min, max, step);
        rotationXTF.setModel(rotationX);
        rotationYTF.setModel(rotationY);
        rotationZTF.setModel(rotationZ);
        currentRotation.loadIdentity();


        // TODO add Float editors to the spinners

        userMotionListener = new TransformChangeListener() {

            private Vector3f look = new Vector3f();
            private Vector3f pos = new Vector3f();

            public void transformChanged(Cell cell, ChangeSource source) {
                CellTransform t = cell.getWorldTransform();
                t.getLookAt(pos, look);

                look.mult(3);
                pos.addLocal(look);

                currentTranslation.set(pos);
                ((SpinnerNumberModel) translationXTF.getModel()).setValue(
                        new Float(pos.x));
                ((SpinnerNumberModel) translationYTF.getModel()).setValue(
                        new Float(pos.y));
                ((SpinnerNumberModel) translationZTF.getModel()).setValue(
                        new Float(pos.z));
                if (transformProcessor != null) {
                    transformProcessor.setTransform(
                            currentRotation, currentTranslation);
                }
            }
        };

        translationChangeListener = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                float x = (Float) ((SpinnerNumberModel) translationXTF.getModel()).getValue();
                float y = (Float) ((SpinnerNumberModel) translationYTF.getModel()).getValue();
                float z = (Float) ((SpinnerNumberModel) translationZTF.getModel()).getValue();

                if (x != currentTranslation.x ||
                        y != currentTranslation.y ||
                        z != currentTranslation.z) {
                    currentTranslation.set(x, y, z);
                    importedModel.setTranslation(currentTranslation);
                    if (transformProcessor != null) {
                        transformProcessor.setTransform(
                                currentRotation, currentTranslation);
                    }
                }
            }
        };

        rotationChangeListener = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                float x = (Float) ((SpinnerNumberModel) rotationXTF.getModel()).getValue();
                float y = (Float) ((SpinnerNumberModel) rotationYTF.getModel()).getValue();
                float z = (Float) ((SpinnerNumberModel) rotationZTF.getModel()).getValue();

                if (x != currentRotationValues.x ||
                        y != currentRotationValues.y ||
                        z != currentRotationValues.z) {
                    currentRotationValues.set(x, y, z);
                    importedModel.setOrientation(currentRotationValues);
                    calcCurrentRotationMatrix();
                    if (transformProcessor != null) {
                        transformProcessor.setTransform(
                                currentRotation, currentTranslation);
                    }
                }
            }
        };

        ((SpinnerNumberModel) rotationXTF.getModel()).addChangeListener(
                rotationChangeListener);
        ((SpinnerNumberModel) rotationYTF.getModel()).addChangeListener(
                rotationChangeListener);
        ((SpinnerNumberModel) rotationZTF.getModel()).addChangeListener(
                rotationChangeListener);

        scaleChangeListener = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                float x = (Float) ((SpinnerNumberModel) scaleTF.getModel()).getValue();
//                float y = (Float)((SpinnerNumberModel)translationYTF.getModel()).getValue();
//                float z = (Float)((SpinnerNumberModel)translationZTF.getModel()).getValue();

                if (x != currentScale.x) {
                    currentScale.set(x, x, x);
                    importedModel.setScale(currentScale);
                    if (transformProcessor != null) {
                        transformProcessor.setTransform(
                                currentRotation, currentTranslation, currentScale);
                    }
                }
            }
        };

        ((SpinnerNumberModel) scaleTF.getModel()).addChangeListener(
                scaleChangeListener);

        // Disable move with avatar
        avatarMoveCB.setSelected(false);
        enableSpinners(true);

    }

    /**
     * Set the spinners to the rotation, translation and scale local coords of
     * this node
     * @param node
     */
    private void setSpinners(Node modelBG, Node rootBG) {
        Vector3f translation = rootBG.getLocalTranslation();
        Quaternion quat = modelBG.getLocalRotation();
        float[] angles = quat.toAngles(new float[3]);
        Vector3f scale = modelBG.getLocalScale();

        translationXTF.setValue(translation.x);
        translationYTF.setValue(translation.y);
        translationZTF.setValue(translation.z);

        rotationXTF.setValue((float) Math.toDegrees(angles[0]));
        rotationYTF.setValue((float) Math.toDegrees(angles[1]));
        rotationZTF.setValue((float) Math.toDegrees(angles[2]));

        scaleTF.setValue(scale.x);

        importedModel.setTranslation(translation);
        importedModel.setOrientation(new Vector3f(
                (float) Math.toDegrees(angles[0]),
                (float) Math.toDegrees(angles[1]),
                (float) Math.toDegrees(angles[2])));
        importedModel.setScale(new Vector3f(scale.x, scale.x, scale.x));
    }

    private void calcCurrentRotationMatrix() {
        currentRotation = ImportSessionFrame.calcRotationMatrix(
                (float) Math.toRadians(currentRotationValues.x),
                (float) Math.toRadians(currentRotationValues.y),
                (float) Math.toRadians(currentRotationValues.z));
    }

    void chooseFile() {
        texturePrefixTF.setText("");
        modelNameTF.setText("");
        modelX3dTF.setText("");
        importedModel = null;

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        LoaderManager.getLoaderManager().getLoaderExtensions());
                chooser.setFileFilter(filter);
                if (lastModelDir != null) {
                    chooser.setCurrentDirectory(lastModelDir);
                }
                int returnVal = chooser.showOpenDialog(ModelImporterFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        importModel(chooser.getSelectedFile(), false);
                    } catch (FileNotFoundException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (IOException ioe) {
                        LOGGER.log(Level.SEVERE, null, ioe);
                    }
                    setVisible(true);
                    lastModelDir = chooser.getSelectedFile().getParentFile();
                }
            }
        });
    }

    /**
     * Edit a model that has already been imported
     * @param model
     */
    void editModel(ImportedModel model) {
//        texturePrefixTF.setText(model.getTexturePrefix());
        modelX3dTF.setText(model.getOriginalURL().toExternalForm());
        modelNameTF.setText(model.getWonderlandName());
        currentTranslation.set(model.getTranslation());
        currentRotationValues.set(model.getOrientation());
        calcCurrentRotationMatrix();
        ((SpinnerNumberModel) rotationXTF.getModel()).setValue(
                model.getOrientation().x);
        ((SpinnerNumberModel) rotationYTF.getModel()).setValue(
                model.getOrientation().y);
        ((SpinnerNumberModel) rotationZTF.getModel()).setValue(
                model.getOrientation().z);
        ((SpinnerNumberModel) translationXTF.getModel()).setValue(
                model.getTranslation().x);
        ((SpinnerNumberModel) translationYTF.getModel()).setValue(
                model.getTranslation().y);
        ((SpinnerNumberModel) translationZTF.getModel()).setValue(
                model.getTranslation().x);
        ((SpinnerNumberModel) scaleTF.getModel()).setValue(model.getScale().x);

        avatarMoveCB.setSelected(false);
        populateTextureList(model.getRootBG());

        processBounds(model.getModelBG());
    }

    /**
     * Import a model from a file
     * 
     * @param origFile
     */
    void importModel(final File origFile, boolean attachToAvatar)
            throws IOException {
        avatarMoveCB.setSelected(attachToAvatar);

        modelX3dTF.setText(origFile.getAbsolutePath());
        importSettings = new ImportSettings(origFile.toURI().toURL());

        sessionFrame.asyncLoadModel(
                importSettings, new ImportSessionFrame.LoadCompleteListener() {

            public void loadComplete(ImportedModel importedModel) {
                ModelImporterFrame.this.importedModel = importedModel;
                Entity entity = importedModel.getEntity();
                transformProcessor =
                        (TransformProcessorComponent) entity.getComponent(
                        TransformProcessorComponent.class);
                setSpinners(
                        importedModel.getModelBG(), importedModel.getRootBG());

                entity.addComponent(LoadCompleteProcessor.class,
                        new LoadCompleteProcessor(importedModel));

                String dir = origFile.getAbsolutePath();
                dir = dir.substring(0, dir.lastIndexOf(File.separatorChar));
                dir = dir.substring(dir.lastIndexOf(File.separatorChar) + 1);
                texturePrefixTF.setText(dir);

                String filename = origFile.getAbsolutePath();
                filename = filename.substring(
                        filename.lastIndexOf(File.separatorChar) + 1);
                filename = filename.substring(0, filename.lastIndexOf('.'));
                modelNameTF.setText(filename);

                if (avatarMoveCB.isSelected()) {
                    ViewManager viewManager = ViewManager.getViewManager();
                    ViewCell viewCell = viewManager.getPrimaryViewCell();
                    viewCell.addTransformChangeListener(userMotionListener);
                }
            }
        });

    }

    private void populateTextureList(Node bg) {
        final DefaultTableModel model =
                (DefaultTableModel) textureTable.getModel();
        while (model.getRowCount() != 0) {
            model.removeRow(0);
        }

        final String texturePath = texturePrefixTF.getText();
        final HashSet<String> textureSet = new HashSet();

        TreeScan.findNode(bg, Geometry.class, new ProcessNodeInterface() {

            public boolean processNode(Spatial node) {
                TextureState ts =
                        (TextureState) node.getRenderState(
                        TextureState.RS_TEXTURE);
                if (ts == null) {
                    return true;
                }

                Texture t = ts.getTexture();
                if (t != null) {
                    String tFile = t.getImageLocation();
                    if (textureSet.add(tFile)) {
                        model.addRow(new Object[]{new String(tFile),
                                    "not implemented",
                                    "not implemented"});
                    }
                }
                return true;
            }
        }, false, true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        basicPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        translationXTF = new javax.swing.JSpinner();
        translationYTF = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        translationZTF = new javax.swing.JSpinner();
        avatarMoveCB = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        rotationXTF = new javax.swing.JSpinner();
        rotationYTF = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        rotationZTF = new javax.swing.JSpinner();
        modelNameTF = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        modelX3dTF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        scaleTF = new javax.swing.JSpinner();
        advancedPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textureTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        texturePrefixTF = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        boundsCenterYTF = new javax.swing.JTextField();
        boundsCenterXTF = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        boundsCenterZTF = new javax.swing.JTextField();
        boundsSizeXTF = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        geometryStatsB = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        boundsSizeYTF = new javax.swing.JTextField();
        boundsSizeZTF = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        cancelB = new javax.swing.JButton();
        okB = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/artimport/client/jme/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("ModelImporterFrame.title")); // NOI18N

        jTabbedPane1.setFont(jTabbedPane1.getFont());
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(102, 167));

        basicPanel.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                basicPanelInputMethodTextChanged(evt);
            }
        });

        jLabel7.setText(bundle.getString("ModelImporterFrame.jLabel7.text")); // NOI18N

        jLabel6.setText(bundle.getString("ModelImporterFrame.jLabel6.text")); // NOI18N

        translationXTF.setEnabled(false);

        translationYTF.setEnabled(false);

        jLabel8.setText(bundle.getString("ModelImporterFrame.jLabel8.text")); // NOI18N

        jLabel9.setText(bundle.getString("ModelImporterFrame.jLabel9.text")); // NOI18N

        translationZTF.setEnabled(false);

        avatarMoveCB.setFont(avatarMoveCB.getFont());
        avatarMoveCB.setSelected(true);
        avatarMoveCB.setText(bundle.getString("ModelImporterFrame.avatarMoveCB.text")); // NOI18N
        avatarMoveCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                avatarMoveCBActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel8)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel9))
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(translationYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, translationXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(translationZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(78, Short.MAX_VALUE)
                .add(avatarMoveCB))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel6)
                    .add(translationXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(translationYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(translationZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(avatarMoveCB))
        );

        jLabel10.setText(bundle.getString("ModelImporterFrame.jLabel10.text")); // NOI18N

        jLabel11.setText(bundle.getString("ModelImporterFrame.jLabel11.text")); // NOI18N

        jLabel13.setText(bundle.getString("ModelImporterFrame.jLabel13.text")); // NOI18N

        jLabel12.setText(bundle.getString("ModelImporterFrame.jLabel12.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel10)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel11)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel13)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rotationYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rotationZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rotationXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(new java.awt.Component[] {jLabel11, jLabel12, jLabel13}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jLabel10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel11)
                    .add(rotationXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel13)
                    .add(rotationYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel12)
                    .add(rotationZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        modelNameTF.setToolTipText(bundle.getString("ModelImporterFrame.modelNameTF.toolTipText")); // NOI18N

        jLabel5.setText(bundle.getString("ModelImporterFrame.jLabel5.text")); // NOI18N

        modelX3dTF.setEditable(false);
        modelX3dTF.setToolTipText(bundle.getString("ModelImporterFrame.modelX3dTF.toolTipText")); // NOI18N
        modelX3dTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelX3dTFActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("ModelImporterFrame.jLabel1.text")); // NOI18N

        jLabel3.setText(bundle.getString("ModelImporterFrame.jLabel3.text")); // NOI18N

        org.jdesktop.layout.GroupLayout basicPanelLayout = new org.jdesktop.layout.GroupLayout(basicPanel);
        basicPanel.setLayout(basicPanelLayout);
        basicPanelLayout.setHorizontalGroup(
            basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(basicPanelLayout.createSequentialGroup()
                .add(basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(basicPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(scaleTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(modelX3dTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                            .add(modelNameTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)))
                    .add(basicPanelLayout.createSequentialGroup()
                        .add(31, 31, 31)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        basicPanelLayout.setVerticalGroup(
            basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(basicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(modelX3dTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(modelNameTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(basicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel3)
                    .add(scaleTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(64, 64, 64))
        );

        jTabbedPane1.addTab(bundle.getString("ModelImporterFrame.basicPanel.TabConstraints.tabTitle"), basicPanel); // NOI18N

        jScrollPane1.setViewportView(textureTable);

        jLabel2.setText(bundle.getString("ModelImporterFrame.jLabel2.text")); // NOI18N

        texturePrefixTF.setEditable(false);
        texturePrefixTF.setToolTipText(bundle.getString("ModelImporterFrame.texturePrefixTF.toolTipText")); // NOI18N

        jLabel25.setText(bundle.getString("ModelImporterFrame.jLabel25.text")); // NOI18N

        jLabel27.setText(bundle.getString("ModelImporterFrame.jLabel27.text")); // NOI18N

        boundsCenterYTF.setColumns(12);
        boundsCenterYTF.setEditable(false);
        boundsCenterYTF.setFont(boundsCenterYTF.getFont());

        boundsCenterXTF.setColumns(12);
        boundsCenterXTF.setEditable(false);
        boundsCenterXTF.setFont(boundsCenterXTF.getFont());
        boundsCenterXTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boundsCenterXTFActionPerformed(evt);
            }
        });

        jLabel28.setText(bundle.getString("ModelImporterFrame.jLabel28.text")); // NOI18N

        jLabel29.setText(bundle.getString("ModelImporterFrame.jLabel29.text")); // NOI18N

        boundsCenterZTF.setColumns(12);
        boundsCenterZTF.setEditable(false);
        boundsCenterZTF.setFont(boundsCenterZTF.getFont());

        boundsSizeXTF.setColumns(12);
        boundsSizeXTF.setEditable(false);
        boundsSizeXTF.setFont(boundsSizeXTF.getFont());

        jLabel26.setText(bundle.getString("ModelImporterFrame.jLabel26.text")); // NOI18N

        geometryStatsB.setText(bundle.getString("ModelImporterFrame.geometryStatsB.text")); // NOI18N
        geometryStatsB.setToolTipText(bundle.getString("ModelImporterFrame.geometryStatsB.toolTipText")); // NOI18N
        geometryStatsB.setEnabled(false);
        geometryStatsB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                geometryStatsBActionPerformed(evt);
            }
        });

        jLabel30.setText(bundle.getString("ModelImporterFrame.jLabel30.text")); // NOI18N

        boundsSizeYTF.setColumns(12);
        boundsSizeYTF.setEditable(false);
        boundsSizeYTF.setFont(boundsSizeYTF.getFont());

        boundsSizeZTF.setColumns(12);
        boundsSizeZTF.setEditable(false);
        boundsSizeZTF.setFont(boundsSizeZTF.getFont());

        jLabel31.setText(bundle.getString("ModelImporterFrame.jLabel31.text")); // NOI18N

        jLabel32.setText(bundle.getString("ModelImporterFrame.jLabel32.text")); // NOI18N

        jLabel33.setText(bundle.getString("ModelImporterFrame.jLabel33.text")); // NOI18N

        org.jdesktop.layout.GroupLayout advancedPanelLayout = new org.jdesktop.layout.GroupLayout(advancedPanel);
        advancedPanel.setLayout(advancedPanelLayout);
        advancedPanelLayout.setHorizontalGroup(
            advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(advancedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(advancedPanelLayout.createSequentialGroup()
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel25)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel27)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel28)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel29))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(boundsCenterYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(boundsCenterXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(boundsCenterZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(advancedPanelLayout.createSequentialGroup()
                                .add(71, 71, 71)
                                .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel31)
                                    .add(jLabel33)
                                    .add(jLabel32))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(boundsSizeXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(boundsSizeYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(boundsSizeZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jLabel26)))
                    .add(geometryStatsB)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, advancedPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
                    .add(advancedPanelLayout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texturePrefixTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                    .add(jLabel30))
                .addContainerGap())
        );

        advancedPanelLayout.linkSize(new java.awt.Component[] {boundsSizeXTF, boundsSizeYTF, boundsSizeZTF}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        advancedPanelLayout.linkSize(new java.awt.Component[] {boundsCenterXTF, boundsCenterYTF, boundsCenterZTF}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        advancedPanelLayout.setVerticalGroup(
            advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(advancedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(texturePrefixTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel30)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(advancedPanelLayout.createSequentialGroup()
                        .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel27)
                            .add(boundsCenterYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel28)
                            .add(boundsCenterXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel29)
                            .add(boundsCenterZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(advancedPanelLayout.createSequentialGroup()
                        .add(jLabel26)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel31)
                            .add(boundsSizeXTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel32)
                            .add(boundsSizeYTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(advancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel33)
                            .add(boundsSizeZTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(geometryStatsB)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("ModelImporterFrame.advancedPanel.TabConstraints.tabTitle"), advancedPanel); // NOI18N

        cancelB.setText(bundle.getString("ModelImporterFrame.cancelB.text")); // NOI18N
        cancelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBActionPerformed(evt);
            }
        });

        okB.setText(bundle.getString("ModelImporterFrame.okB.text")); // NOI18N
        okB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(334, Short.MAX_VALUE)
                .add(cancelB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(okB)
                .addContainerGap())
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 370, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelB)
                    .add(okB))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBActionPerformed
        if (avatarMoveCB.isSelected()) {
            ViewManager viewManager = ViewManager.getViewManager();
            ViewCell viewCell = viewManager.getPrimaryViewCell();
            viewCell.removeTransformChangeListener(userMotionListener);
        }
        setVisible(false);
        Vector3f translation = new Vector3f((Float) translationXTF.getValue(),
                (Float) translationYTF.getValue(),
                (Float) translationZTF.getValue());
        Vector3f orientation = new Vector3f((Float) rotationXTF.getValue(),
                (Float) rotationYTF.getValue(),
                (Float) rotationZTF.getValue());

        importedModel.setWonderlandName(modelNameTF.getText());
//        importedModel.setTexturePrefix(texturePrefixTF.getText());

        sessionFrame.loadCompleted(importedModel);
}//GEN-LAST:event_okBActionPerformed

    private void cancelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBActionPerformed
        this.setVisible(false);
        if (userMotionListener != null) {
            ViewManager viewManager = ViewManager.getViewManager();
            ViewCell viewCell = viewManager.getPrimaryViewCell();
            viewCell.removeTransformChangeListener(userMotionListener);
        }
        sessionFrame.loadCancelled(importedModel);
    }//GEN-LAST:event_cancelBActionPerformed

    private void geometryStatsBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_geometryStatsBActionPerformed
//        if (geometryStatsDialog==null) 
//            geometryStatsDialog = new GeometryStatsDialog(this);
//        geometryStatsDialog.calcGeometryStats(modelBG);
//        geometryStatsDialog.setVisible(true);
        System.err.println("geometryStats not implemented");
}//GEN-LAST:event_geometryStatsBActionPerformed

    private void avatarMoveCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_avatarMoveCBActionPerformed
        if (userMotionListener == null) {
            return;
        }

        ViewManager viewManager = ViewManager.getViewManager();
        ViewCell viewCell = viewManager.getPrimaryViewCell();
        if (avatarMoveCB.isSelected()) {
            enableSpinners(false);
            viewCell.addTransformChangeListener(userMotionListener);
        } else {
            enableSpinners(true);
            viewCell.removeTransformChangeListener(userMotionListener);
        }

    }//GEN-LAST:event_avatarMoveCBActionPerformed

    private void basicPanelInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_basicPanelInputMethodTextChanged
        // TODO add your handling code here:
        System.err.println(evt);
    }//GEN-LAST:event_basicPanelInputMethodTextChanged

    private void modelX3dTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelX3dTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_modelX3dTFActionPerformed

    private void boundsCenterXTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boundsCenterXTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_boundsCenterXTFActionPerformed

    private void enableSpinners(boolean enabled) {
        translationXTF.setEnabled(enabled);
        translationYTF.setEnabled(enabled);
        translationZTF.setEnabled(enabled);
        rotationXTF.setEnabled(enabled);
        rotationYTF.setEnabled(enabled);
        rotationZTF.setEnabled(enabled);

        if (enabled) {
            ((SpinnerNumberModel) translationXTF.getModel()).addChangeListener(
                    translationChangeListener);
            ((SpinnerNumberModel) translationYTF.getModel()).addChangeListener(
                    translationChangeListener);
            ((SpinnerNumberModel) translationZTF.getModel()).addChangeListener(
                    translationChangeListener);
            ((SpinnerNumberModel) rotationXTF.getModel()).addChangeListener(
                    rotationChangeListener);
            ((SpinnerNumberModel) rotationYTF.getModel()).addChangeListener(
                    rotationChangeListener);
            ((SpinnerNumberModel) rotationZTF.getModel()).addChangeListener(
                    rotationChangeListener);
        } else {
            ((SpinnerNumberModel) translationXTF.getModel()).removeChangeListener(
                    translationChangeListener);
            ((SpinnerNumberModel) translationYTF.getModel()).removeChangeListener(
                    translationChangeListener);
            ((SpinnerNumberModel) translationZTF.getModel()).removeChangeListener(
                    translationChangeListener);
            ((SpinnerNumberModel) rotationXTF.getModel()).removeChangeListener(
                    rotationChangeListener);
            ((SpinnerNumberModel) rotationYTF.getModel()).removeChangeListener(
                    rotationChangeListener);
            ((SpinnerNumberModel) rotationZTF.getModel()).removeChangeListener(
                    rotationChangeListener);
        }
    }

    /**
     * Process the bounds of the graph, updating the UI.
     */
    private void processBounds(Node bg) {
//        System.err.println("Model Node "+bg);

        if (bg == null) {
            return;
        }

        BoundingVolume bounds = bg.getWorldBound();

        if (bounds == null) {
            bounds = calcBounds(bg);
        }

        // Remove the rotation from the bounds because it will be reapplied by
        // the cell
//        Quaternion rot = bg.getWorldRotation();
//        rot.inverseLocal();
//        bounds = bounds.transform(rot, new Vector3f(), new Vector3f(1,1,1), bounds);
//
//        System.err.println("ROTATED "+bounds);
//        System.err.println(rot.toAngleAxis(null));

        if (bounds instanceof BoundingSphere) {
            BoundingSphere sphere = (BoundingSphere) bounds;
            Vector3f center = new Vector3f();
            sphere.getCenter(center);
            boundsCenterXTF.setText(Double.toString(center.x));
            boundsCenterYTF.setText(Double.toString(center.y));
            boundsCenterZTF.setText(Double.toString(center.z));
            boundsSizeXTF.setText(Double.toString(sphere.getRadius()));
            boundsSizeYTF.setText("N/A Sphere");
            boundsSizeZTF.setText("N/A Sphere");
        } else if (bounds instanceof BoundingBox) {
            BoundingBox box = (BoundingBox) bounds;
            Vector3f center = new Vector3f();
            box.getCenter();
            boundsCenterXTF.setText(Double.toString(center.x));
            boundsCenterYTF.setText(Double.toString(center.y));
            boundsCenterZTF.setText(Double.toString(center.z));

            boundsSizeXTF.setText(Float.toString(box.xExtent));
            boundsSizeYTF.setText(Float.toString(box.yExtent));
            boundsSizeZTF.setText(Float.toString(box.zExtent));
        }
    }

    /**
     * Traverse the graph, combining all the world bounds into bv
     * @param n
     * @param bv
     */
    BoundingVolume calcBounds(Spatial n) {
        BoundingVolume bounds = null;

        if (n instanceof Geometry) {
            bounds = new BoundingBox();
            bounds.computeFromPoints(((Geometry) n).getVertexBuffer());

            bounds.transform(
                    n.getLocalRotation(),
                    n.getLocalTranslation(),
                    n.getLocalScale());
        }

        if (n instanceof Node && ((Node) n).getQuantity() > 0) {
            for (Spatial child : ((Node) n).getChildren()) {
                BoundingVolume childB = calcBounds(child);
                if (bounds == null) {
                    bounds = childB;
                } else {
                    bounds.mergeLocal(childB);
                }
            }
        }

        if (bounds != null) {
            bounds.transform(
                    n.getLocalRotation(),
                    n.getLocalTranslation(),
                    n.getLocalScale(),
                    bounds);
        }
//        Vector3f axis = new Vector3f();
//        float angle = n.getLocalRotation().toAngleAxis(axis);
//        System.err.println("Applying transform "+n.getLocalTranslation()+"  "+angle+"  "+axis);
//        System.err.println("BOunds "+bounds);

        return bounds;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel advancedPanel;
    private javax.swing.JCheckBox avatarMoveCB;
    private javax.swing.JPanel basicPanel;
    private javax.swing.JTextField boundsCenterXTF;
    private javax.swing.JTextField boundsCenterYTF;
    private javax.swing.JTextField boundsCenterZTF;
    private javax.swing.JTextField boundsSizeXTF;
    private javax.swing.JTextField boundsSizeYTF;
    private javax.swing.JTextField boundsSizeZTF;
    private javax.swing.JButton cancelB;
    private javax.swing.JButton geometryStatsB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField modelNameTF;
    private javax.swing.JTextField modelX3dTF;
    private javax.swing.JButton okB;
    private javax.swing.JSpinner rotationXTF;
    private javax.swing.JSpinner rotationYTF;
    private javax.swing.JSpinner rotationZTF;
    private javax.swing.JSpinner scaleTF;
    private javax.swing.JTextField texturePrefixTF;
    private javax.swing.JTable textureTable;
    private javax.swing.JSpinner translationXTF;
    private javax.swing.JSpinner translationYTF;
    private javax.swing.JSpinner translationZTF;
    // End of variables declaration//GEN-END:variables

    /**
     * Case independent filename extension filter
     */
    class FileNameExtensionFilter extends FileFilter {

        private HashSet<String> extensions;
        private String description;

        public FileNameExtensionFilter(String ext) {
            extensions = new HashSet();
            extensions.add(ext);
            description = new String(ext);
        }

        public FileNameExtensionFilter(String[] ext) {
            extensions = new HashSet();
            StringBuffer desc = new StringBuffer();
            for (String e : ext) {
                extensions.add(e);
                desc.append(e + ", ");
            }
            description = desc.toString();
        }

        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }
            String e = pathname.getName();
            e = e.substring(e.lastIndexOf('.') + 1);
            if (extensions.contains(e.toLowerCase())) {
                return true;
            }

            return false;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

    class LoadCompleteProcessor extends ProcessorComponent {

        private ImportedModel importedModel;

        public LoadCompleteProcessor(ImportedModel importedModel) {
            this.importedModel = importedModel;

        }

        @Override
        public void compute(ProcessorArmingCollection arg0) {
            processBounds(importedModel.getModelBG());

            populateTextureList(importedModel.getModelBG());

            importedModel.getEntity().removeComponent(
                    LoadCompleteProcessor.class);
            setArmingCondition(null);
        }

        @Override
        public void commit(ProcessorArmingCollection arg0) {
        }

        @Override
        public void initialize() {
            setArmingCondition(new NewFrameCondition(this));
        }
    }
}
