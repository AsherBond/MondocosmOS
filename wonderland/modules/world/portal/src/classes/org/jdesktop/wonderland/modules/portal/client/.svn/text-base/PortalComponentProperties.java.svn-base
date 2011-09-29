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
package org.jdesktop.wonderland.modules.portal.client;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.client.content.ContentBrowserManager;
import org.jdesktop.wonderland.client.content.spi.ContentBrowserSPI;
import org.jdesktop.wonderland.client.content.spi.ContentBrowserSPI.ContentBrowserListener;
import org.jdesktop.wonderland.client.jme.MainFrame.PlacemarkType;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistry;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistryFactory;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.portal.common.PortalComponentServerState;
import org.jdesktop.wonderland.modules.portal.common.PortalComponentServerState.AudioSourceType;
import org.jdesktop.wonderland.modules.portal.common.VolumeConverter;

/**
 * A property sheet for the Portal component, allowing users to enter the
 * destination URL, location, and look direction.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@PropertiesFactory(PortalComponentServerState.class)
public class PortalComponentProperties extends JPanel
        implements PropertiesFactorySPI {
   
    // The I18N resource bundle
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/portal/client/resources/Bundle");
    
    private static final Logger LOGGER =
	Logger.getLogger(PortalComponentProperties.class.getName());

    // The main editor object for the Cell Editor
    private CellPropertiesEditor editor = null;

    private SpinnerNumberModel xSpinnerModel;
    private SpinnerNumberModel ySpinnerModel;
    private SpinnerNumberModel zSpinnerModel;

    private SpinnerNumberModel lookDirectionSpinnerModel;

    // The original values for all of the fields. We use the convention that
    // if empty, an empty string ("") is used, rather than null.
    private String origServerURL = "";

    private float origX;
    private float origY;
    private float origZ;
    private float origLookDirection;

    private AudioSourceType origAudioSourceType = AudioSourceType.FILE;
    private String origAudioSource = "";
    private float origVolume = 1F;

    private AudioSourceType audioSourceType = AudioSourceType.FILE;
    private VolumeConverter volumeConverter;
    private AudioCacheHandler audioCacheHandler;

    private static String defaultAudioSource;

    /** Creates new form PortalComponentProperties */
    public PortalComponentProperties() {
	this(false);
    }

    public PortalComponentProperties(boolean cacheResources) {
        // Initialize the GUI
        initComponents();

	audioCacheHandler = new AudioCacheHandler();

	try {
	    audioCacheHandler.initialize();

	    if (cacheResources) {
		cacheResource("resources/whatever.au");
		cacheResource("resources/teleport1.au");
		cacheResource("resources/Transporter_Passby.au");
		cacheResource("resources/teleport.au");
		    cacheResource("resources/weapAppear.au");
		defaultAudioSource = cacheResource("resources/disappear.au");

		System.out.println("defaultAudio SOurce " + defaultAudioSource);
	    }
	} catch (AudioCacheHandlerException e) {
	    errorMessage("Cache Resources", e.getMessage());
	}

	audioSourceTextField.setText(defaultAudioSource);

	Float value = new Float(0);
	Float min = null; //new Float(-Float.MIN_VALUE);
	Float max = null; //new Float(Float.MAX_VALUE);
	Float step = new Float(.1);

	xSpinnerModel = new SpinnerNumberModel(value, min, max, step);
	xSpinner.setModel(xSpinnerModel);

	value = new Float(0);
	//min = new Float(-Float.MIN_VALUE);
	//max = new Float(Float.MAX_VALUE);
	step = new Float(.1);

	ySpinnerModel = new SpinnerNumberModel(value, min, max, step);
	ySpinner.setModel(ySpinnerModel);

	value = new Float(0);
	//min = new Float(-Float.MIN_VALUE);
	//max = new Float(Float.MAX_VALUE);
	step = new Float(.1);

	zSpinnerModel = new SpinnerNumberModel(value, min, max, step);
	zSpinner.setModel(zSpinnerModel);

	value = new Float(0);
	min = new Float(0);
	max = new Float(360);
	step = new Float(1);
	
	lookDirectionSpinnerModel = new SpinnerNumberModel(value, min, max, step);
	lookDirectionSpinner.setModel(lookDirectionSpinnerModel);

        // Listen for changes to the text fields
        TextFieldListener listener = new TextFieldListener();
        urlTF.getDocument().addDocumentListener(listener);

	audioSourceTextField.getDocument().addDocumentListener(listener);

        volumeConverter = new VolumeConverter(volumeSlider.getMinimum(),
            volumeSlider.getMaximum());

	
        // set renderer for placemarks
        placemarkCB.setModel(new DefaultComboBoxModel());
        placemarkCB.setRenderer(new DefaultListCellRenderer() {
            private JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
            
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus)
            {
                Placemark placemark = (Placemark) value;

                if (placemark == null) {
                    return separator;
                }
                
                return super.getListCellRendererComponent(list, 
                        placemark.getName(), index, isSelected, cellHasFocus);
            }
        });
    }

    public static String getDefaultAudioSource() {
	return defaultAudioSource;
    }

    private String cacheResource(String resource) throws AudioCacheHandlerException {
	return audioCacheHandler.cacheURL(PortalCellFactory.class.getResource(resource));
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return BUNDLE.getString("Portal");
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
        // Fetch the current state from the Cell. If none exist, then just
        // return.
        CellServerState cellServerState = editor.getCellServerState();
        PortalComponentServerState state = (PortalComponentServerState)
                cellServerState.getComponentServerState(
                PortalComponentServerState.class);
        if (state == null) {
            return;
        }

        // Otherwise, update the values of the text fields and store away the
        // original values. We use the convention that an empty entry is
        // represented by an empty string ("") rather than null.

        // Fetch the destination URL from the server state. If the original
        // state is null, then convert it into an empty string and update the
        // text field.
        origServerURL = state.getServerURL();
        if (origServerURL == null || origServerURL.length() == 0) {
	    origServerURL = LoginManager.getPrimary().getServerURL();
	}

        // Fetch the destination location from the server state. If the value
        // is null, then set the original values and text fields to empty
        // strings.
        Vector3f origin = state.getLocation();
        if (origin != null) {
            origX = origin.x;
            origY = origin.y;
            origZ = origin.z;
        } else {
            origX = 0;
            origY = 0;
            origZ = 0;
        }

        // Fetc the destination look direction from the server state. If the
        // value is null, then set the original value and text field to an
        // empty string.
        Quaternion lookAt = state.getLook();
        if (lookAt != null) {
            float lookDirection = (float) Math.toDegrees(lookAt.toAngleAxis(new Vector3f()));
            origLookDirection = lookDirection;
        } else {
            origLookDirection = 0;
        }

	origVolume = state.getVolume();

	if (state.getAudioSourceType() != null) {
	    origAudioSourceType = state.getAudioSourceType();
	} else {
	    origAudioSourceType = AudioSourceType.FILE;
	}

	origAudioSource = state.getAudioSource();
        if (origAudioSource == null) {
	    origAudioSource = "";
	}

        // update list of placemarks
        PlacemarkRegistry reg = PlacemarkRegistryFactory.getInstance();
        
        List<Placemark> allPlacemarks = new ArrayList<Placemark>();
        Placemark firstEntry = new Placemark("Select Placemark", null, 0, 0, 0, 0);
        allPlacemarks.add(firstEntry);
        
        // separator
        allPlacemarks.add(null);
        
        // system placemarks
        List<Placemark> sysPlacemarks = new ArrayList<Placemark>();
        sysPlacemarks.addAll(reg.getAllPlacemarks(PlacemarkType.SYSTEM));
        Collections.sort(sysPlacemarks, new PlacemarkSorter());
        allPlacemarks.addAll(sysPlacemarks);
        
        // separator
        allPlacemarks.add(null);
        
        // user placemarks
        List<Placemark> userPlacemarks = new ArrayList<Placemark>();
        userPlacemarks.addAll(reg.getAllPlacemarks(PlacemarkType.USER));
        Collections.sort(userPlacemarks, new PlacemarkSorter());
        allPlacemarks.addAll(userPlacemarks);
        
        placemarkCB.setModel(new DefaultComboBoxModel(allPlacemarks.toArray()));
    
        // make UI up-to-date
        restore();
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
        // replace all original values with current values
        origServerURL = null; //urlTF.getText().trim();
        
        origX = (Float) xSpinnerModel.getValue();
        origY = (Float) ySpinnerModel.getValue();
        origZ = (Float) zSpinnerModel.getValue();
        origLookDirection = (Float) lookDirectionSpinnerModel.getValue();
        
        origAudioSource = audioSourceTextField.getText();
        origAudioSourceType = audioSourceType;
        origVolume = volumeConverter.getVolume(volumeSlider.getValue());
        
        // Figure out whether there already exists a server state for the
        // component. If not, then create one.
        CellServerState cellServerState = editor.getCellServerState();
        PortalComponentServerState state = 
                (PortalComponentServerState) cellServerState.getComponentServerState(
                PortalComponentServerState.class);
        if (state == null) {
            //state = new PortalComponentServerState();
	    return;
        }

        // Set the values in the server state from the text fields. If the text
        // fields are empty, they will return an empty string (""), this is
        // converted to null to set in the server state.

        state.setServerURL(origServerURL);

        // Set the location on the server state
        state.setLocation(new Vector3f(origX, origY, origZ));

        // Set the destination look direction from the text field. If the text
        // field is empty, then set the server state as a zero rotation.
        Quaternion look = new Quaternion();
        Vector3f axis = new Vector3f(0.0f, 1.0f, 0.0f);
        float angle = (float) Math.toRadians(origLookDirection);
	look.fromAngleAxis((float) angle, axis);
        state.setLook(look);

	state.setAudioSourceType(origAudioSourceType);
	state.setAudioSource(origAudioSource);
	state.setUploadFile(true);
	state.setVolume(origVolume);

        String cacheFilePath;

	switch (audioSourceType) {
	case FILE:
	    try {
	        cacheFilePath = audioCacheHandler.cacheFile(origAudioSource);
	    } catch (AudioCacheHandlerException e) {
		break;
            }


            try {
                audioCacheHandler.uploadFileAudioSource(origAudioSource);
            } catch (AudioCacheHandlerException e) {
            }
	    

	    state.setCachedAudioSource(cacheFilePath);
            break;

	case CONTENT_REPOSITORY:
	    try {
		cacheFilePath = 
		    audioCacheHandler.cacheContent(urlTF.getText().trim(), origAudioSource);
	    } catch (AudioCacheHandlerException e) {
		break;
	    }

	    state.setCachedAudioSource(cacheFilePath);
            break;

        case URL:
	    try {
                cacheFilePath = audioCacheHandler.cacheURL(new URL(origAudioSource));
	    } catch (Exception e) {
		errorMessage("Cache URL", "Unable to cache URL: " + e.getMessage());
		break;
	    }

	    state.setCachedAudioSource(cacheFilePath);
            break;
        }

        editor.addToUpdateList(state);
        setPanelDirty();
    }

    private void error(String title, String msg) throws IOException {
	errorMessage(title, msg);
	throw new IOException(msg);
    }

    private void errorMessage(final String title, final String msg) {
	final javax.swing.JPanel panel = this;

	java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
		System.out.println(msg);
		javax.swing.JOptionPane.showMessageDialog(
            	    panel, msg, title, javax.swing.JOptionPane.ERROR_MESSAGE);
	    }
	});
    }

    private boolean inRestore;

    /**
     * @inheritDoc()
     */
    public void restore() {
	inRestore = true;
        // Restore from the originally stored values.
        urlTF.setText(origServerURL);
	xSpinnerModel.setValue(origX);
	ySpinnerModel.setValue(origY);
	zSpinnerModel.setValue(origZ);
	lookDirectionSpinnerModel.setValue(origLookDirection);

	switch (origAudioSourceType) {
	case FILE:
	    fileRadioButton.setSelected(true);
	    browseButton.setEnabled(true);
	    break;
	
	case CONTENT_REPOSITORY:
	    contentRepositoryRadioButton.setSelected(true);
	    browseButton.setEnabled(true);
	    break;

	case URL:
	    URLRadioButton.setSelected(true);
	    browseButton.setEnabled(false);
	    break;
	}

	audioSourceTextField.setText(origAudioSource);
	enablePreviewButton();
//	uploadFileCheckBox.setSelected(origUploadFile);
	volumeSlider.setValue(volumeConverter.getVolume(origVolume));
	inRestore = false;
        
        setPanelDirty();
    }

    /**
     * Inner class to listen for changes to the text field and fire off dirty
     * or clean indications to the cell properties editor.
     */
    class TextFieldListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            setPanelDirty();
        }

        public void removeUpdate(DocumentEvent e) {
            setPanelDirty();
        }

        public void changedUpdate(DocumentEvent e) {
            setPanelDirty();
        }

    }

    private void setPanelDirty() {
	if (editor != null) {
            editor.setPanelDirty(PortalComponentProperties.class, isDirty());
	}
    }

    private boolean isDirty() {
	if (inRestore) {
	    return false;
	}

//	if (urlTF.getText().length() == 0) {
//	    return false;
//	}

	if (volumeSlider.getValue() == 0) {
	    return false;
	}

	boolean clean = urlTF.getText().equals(origServerURL);
	    clean &= ((Float) xSpinnerModel.getValue() == origX);
	    clean &= ((Float) ySpinnerModel.getValue() == origY);
	    clean &= ((Float) zSpinnerModel.getValue() == origZ);
	    clean &= ((Float) lookDirectionSpinnerModel.getValue() == origLookDirection);
            clean &= audioSourceType.equals(origAudioSourceType);
            clean &= audioSourceTextField.getText().equals(origAudioSource);
//	    clean &= uploadFileCheckBox.isSelected() == origUploadFile;
            clean &= (volumeConverter.getVolume(volumeSlider.getValue()) == origVolume);
	
	//System.out.println("url " + urlTF.getText() + " o " + origServerURL);
	//System.out.println("locX " + ((Float) xSpinnerModel.getValue()) + " o " + origX);
	//System.out.println("locY " + ((Float) ySpinnerModel.getValue()) + " o " + origY);
	//System.out.println("locZ " + ((Float) zSpinnerModel.getValue()) + " o " + origZ);
	//System.out.println("angle " + ((Float) lookDirectionSpinnerModel.getValue()) + " o " 
	//    + origLookDirection);
	//System.out.println("type " + audioSourceType + " o " + origAudioSourceType);
	//System.out.println("source " + audioSourceTextField.getText() + " o " + origAudioSource);
	//System.out.println("upload " + uploadFileCheckBox.isSelected() + " o " + origUploadFile);
	//System.out.println("v " + volumeConverter.getVolume(volumeSlider.getValue()) 
	//    + " o " + origVolume);
	return !clean;
    }

    private class PlacemarkSorter implements Comparator<Placemark> {
        public int compare(Placemark o1, Placemark o2) {
            return o1.getName().compareTo(o2.getName());
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

        audioButtonGroup = new javax.swing.ButtonGroup();
        locationButtonGroup = new javax.swing.ButtonGroup();
        placemarkCB = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        urlTF = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        fileRadioButton = new javax.swing.JRadioButton();
        contentRepositoryRadioButton = new javax.swing.JRadioButton();
        URLRadioButton = new javax.swing.JRadioButton();
        audioSourceTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        volumeSlider = new javax.swing.JSlider();
        jLabel8 = new javax.swing.JLabel();
        previewButton = new javax.swing.JButton();
        xSpinner = new javax.swing.JSpinner();
        zSpinner = new javax.swing.JSpinner();
        ySpinner = new javax.swing.JSpinner();
        lookDirectionSpinner = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        manualRadioButton = new javax.swing.JRadioButton();
        placemarkRadioButton = new javax.swing.JRadioButton();

        setMinimumSize(new java.awt.Dimension(575, 600));
        setPreferredSize(new java.awt.Dimension(800, 566));
        setRequestFocusEnabled(false);

        placemarkCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        placemarkCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placemarkCBActionPerformed(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/portal/client/resources/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("PortalComponentProperties.jLabel1.text")); // NOI18N

        urlTF.setEnabled(false);

        jLabel2.setText(bundle.getString("PortalComponentProperties.jLabel2.text")); // NOI18N

        jLabel4.setText(bundle.getString("PortalComponentProperties.jLabel4.text")); // NOI18N

        jLabel5.setText(bundle.getString("PortalComponentProperties.jLabel5.text")); // NOI18N

        jLabel10.setText(bundle.getString("PortalComponentProperties.jLabel10.text")); // NOI18N

        jLabel7.setText(bundle.getString("PortalComponentProperties.jLabel7.text")); // NOI18N

        audioButtonGroup.add(fileRadioButton);
        fileRadioButton.setSelected(true);
        fileRadioButton.setText(bundle.getString("PortalComponentProperties.fileRadioButton.text")); // NOI18N
        fileRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileRadioButtonActionPerformed(evt);
            }
        });

        audioButtonGroup.add(contentRepositoryRadioButton);
        contentRepositoryRadioButton.setText(bundle.getString("PortalComponentProperties.contentRepositoryRadioButton.text")); // NOI18N
        contentRepositoryRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentRepositoryRadioButtonActionPerformed(evt);
            }
        });

        audioButtonGroup.add(URLRadioButton);
        URLRadioButton.setText(bundle.getString("PortalComponentProperties.URLRadioButton.text")); // NOI18N
        URLRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                URLRadioButtonActionPerformed(evt);
            }
        });

        audioSourceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                audioSourceTextFieldKeyReleased(evt);
            }
        });

        browseButton.setText(bundle.getString("PortalComponentProperties.browseButton.text")); // NOI18N
        browseButton.setEnabled(false);
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volumeSliderStateChanged(evt);
            }
        });

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(bundle.getString("PortalComponentProperties.jLabel8.text")); // NOI18N

        previewButton.setText(bundle.getString("PortalComponentProperties.previewButton.text")); // NOI18N
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });

        xSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xSpinnerStateChanged(evt);
            }
        });

        zSpinner.setMaximumSize(new java.awt.Dimension(37, 28));
        zSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zSpinnerStateChanged(evt);
            }
        });

        ySpinner.setMaximumSize(new java.awt.Dimension(37, 28));
        ySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ySpinnerStateChanged(evt);
            }
        });

        lookDirectionSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lookDirectionSpinnerStateChanged(evt);
            }
        });

        jLabel9.setText(bundle.getString("PortalComponentProperties.jLabel9.text")); // NOI18N

        locationButtonGroup.add(manualRadioButton);
        manualRadioButton.setSelected(true);
        manualRadioButton.setText(bundle.getString("PortalComponentProperties.manualRadioButton.text")); // NOI18N
        manualRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualRadioButtonActionPerformed(evt);
            }
        });

        locationButtonGroup.add(placemarkRadioButton);
        placemarkRadioButton.setText(bundle.getString("PortalComponentProperties.placemarkRadioButton.text")); // NOI18N
        placemarkRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placemarkRadioButtonActionPerformed(evt);
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
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel5)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel9))
                            .add(jLabel10))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lookDirectionSpinner)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, zSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, ySpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, xSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(46, 46, 46)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel2)
                            .add(jLabel1))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(urlTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 310, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(manualRadioButton)
                                .add(34, 34, 34)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(placemarkCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(placemarkRadioButton)))))
                    .add(layout.createSequentialGroup()
                        .add(30, 30, 30)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel7)
                            .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(previewButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(audioSourceTextField)
                                        .add(volumeSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(browseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 107, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                    .add(fileRadioButton)
                                    .add(18, 18, 18)
                                    .add(contentRepositoryRadioButton)
                                    .add(18, 18, 18)
                                    .add(URLRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(338, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(46, 46, 46)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(urlTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, placemarkRadioButton, 0, 0, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, manualRadioButton)))
                .add(46, 46, 46)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel9)
                            .add(xSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(ySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(zSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lookDirectionSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel10)))
                    .add(placemarkCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(fileRadioButton)
                            .add(contentRepositoryRadioButton)
                            .add(URLRadioButton))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(audioSourceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(browseButton))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel8)
                    .add(volumeSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(previewButton)
                .addContainerGap(116, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void URLRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_URLRadioButtonActionPerformed
	if (URLRadioButton.isSelected() == false) {
	    return;
	}

        audioSourceType = AudioSourceType.URL;
	URLRadioButton.setSelected(true);
//        uploadFileCheckBox.setEnabled(false);
	browseButton.setEnabled(false);
	enablePreviewButton();

	setPanelDirty();
    }//GEN-LAST:event_URLRadioButtonActionPerformed

    private void fileRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileRadioButtonActionPerformed
	if (fileRadioButton.isSelected() == false) {
	    return;
        }

        audioSourceType = AudioSourceType.FILE;
	fileRadioButton.setSelected(true);
//        uploadFileCheckBox.setEnabled(true);
	browseButton.setEnabled(true);
	enablePreviewButton();

	setPanelDirty();
    }//GEN-LAST:event_fileRadioButtonActionPerformed

    private void contentRepositoryRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentRepositoryRadioButtonActionPerformed
	if (contentRepositoryRadioButton.isSelected() == false) {
	    return;
	}

        audioSourceType = AudioSourceType.CONTENT_REPOSITORY;
	contentRepositoryRadioButton.setSelected(true);
//        uploadFileCheckBox.setEnabled(false);
	browseButton.setEnabled(true);
	enablePreviewButton();

	setPanelDirty();
    }//GEN-LAST:event_contentRepositoryRadioButtonActionPerformed

    private void audioSourceTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_audioSourceTextFieldKeyReleased
	enablePreviewButton();
	browseButton.setEnabled(audioSourceTextField.getText().length() > 0);
	setPanelDirty();
    }//GEN-LAST:event_audioSourceTextFieldKeyReleased

    private void enablePreviewButton() {
	if (audioSourceTextField.getText().length() == 0) {
	    previewButton.setEnabled(false);
	    return;
	}

	if (audioSourceType.equals(AudioSourceType.URL)) {
	    previewButton.setEnabled(false);
	    return;
	}
	    
	if (audioSourceType.equals(AudioSourceType.CONTENT_REPOSITORY)) {
	    previewButton.setEnabled(true);
	    return;
	}
	    
	File file = new File(audioSourceTextField.getText());

	if (file.isFile() == true) {
	    previewButton.setEnabled(true);
	} else {
	    previewButton.setEnabled(false);
	}
    }

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        if (audioSourceType.equals(AudioSourceType.FILE)) {
            JFileChooser chooser = new JFileChooser(audioCacheHandler.getAudioCacheDir());

            int returnVal = chooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                audioSourceTextField.setText(chooser.getSelectedFile().getAbsolutePath());
		enablePreviewButton();
            }
        } else if (audioSourceType.equals(AudioSourceType.CONTENT_REPOSITORY)) {
	    // display a GUI to browser the content repository. Wait until OK has been
            // selected and fill in the text field with the URI
	    // Fetch the browser for the webdav protocol and display it.
            // Add a listener for the result and update the value of the
            // text field for the URI
            ContentBrowserManager manager = ContentBrowserManager.getContentBrowserManager();
	    final ContentBrowserSPI browser = manager.getDefaultContentBrowser();
	    browser.addContentBrowserListener(new ContentBrowserListener() {

                public void okAction(String uri) {
                    audioSourceTextField.setText(uri);
		    enablePreviewButton();
                    browser.removeContentBrowserListener(this);
                }

                public void cancelAction() {
                    browser.removeContentBrowserListener(this);
                }
            });
            browser.setVisible(true);
	}
	setPanelDirty();
    }//GEN-LAST:event_browseButtonActionPerformed

    private void volumeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volumeSliderStateChanged
	setPanelDirty();
    }//GEN-LAST:event_volumeSliderStateChanged

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
	try {
	    preview();
	} catch (Exception e) {
	}
    }//GEN-LAST:event_previewButtonActionPerformed

    private void preview() throws IOException {
	String cacheFilePath = null;

	String audioSource = audioSourceTextField.getText().trim();

	try {
	    switch (audioSourceType) {
	    case FILE:
	        cacheFilePath = audioCacheHandler.cacheFile(audioSource);
	        break;
	
	    case CONTENT_REPOSITORY:
	        cacheFilePath = audioCacheHandler.cacheContent(urlTF.getText().trim(), 
		    audioSource);
	        break;

	    case URL:
	        try {
	            cacheFilePath = audioCacheHandler.cacheURL(new URL(audioSource));
	        } catch (MalformedURLException e) {
		    throw new IOException("Bad URL: " + e.getMessage());
	        }
	        break;
	    }
	} catch (AudioCacheHandlerException e) {
	    throw new IOException(e.getMessage());
	}

	try {
            SoftphoneControlImpl.getInstance().sendCommandToSoftphone("playFile=" 
		+ cacheFilePath + "=" + volumeConverter.getVolume(volumeSlider.getValue()));
	} catch (IOException e) {
	    errorMessage("Preview Error", e.getMessage());
	}
    }

    private void xSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xSpinnerStateChanged
	setPanelDirty();
    }//GEN-LAST:event_xSpinnerStateChanged

    private void ySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ySpinnerStateChanged
	setPanelDirty();
    }//GEN-LAST:event_ySpinnerStateChanged

    private void zSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zSpinnerStateChanged
	setPanelDirty();
    }//GEN-LAST:event_zSpinnerStateChanged

    private void lookDirectionSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lookDirectionSpinnerStateChanged
	setPanelDirty();
    }//GEN-LAST:event_lookDirectionSpinnerStateChanged

    private void manualRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualRadioButtonActionPerformed
        // TODO add your handling code here:
        xSpinner.setEnabled(true);
        ySpinner.setEnabled(true);
        zSpinner.setEnabled(true);
        lookDirectionSpinner.setEnabled(true);
        
        placemarkCB.setEnabled(false);
    }//GEN-LAST:event_manualRadioButtonActionPerformed

    private void placemarkRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placemarkRadioButtonActionPerformed
        // TODO add your handling code here:
        placemarkCB.setEnabled(true);
        
        xSpinner.setEnabled(false);
        ySpinner.setEnabled(false);
        zSpinner.setEnabled(false);
        lookDirectionSpinner.setEnabled(false);
    }//GEN-LAST:event_placemarkRadioButtonActionPerformed

    private void placemarkCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placemarkCBActionPerformed
        // TODO add your handling code here:
        Placemark pm = (Placemark) placemarkCB.getSelectedItem();

        if (pm == null) {
            LOGGER.warning("null placemark selected!");
            return;
        }

        // set values
        urlTF.setText(pm.getUrl());
	xSpinnerModel.setValue(pm.getX());
	ySpinnerModel.setValue(pm.getY());
	zSpinnerModel.setValue(pm.getZ());
        // convert angle properly
//	lookDirectionSpinnerModel.setValue((float) Math.toDegrees(pm.getAngle()));
        lookDirectionSpinnerModel.setValue(pm.getAngle());
    }//GEN-LAST:event_placemarkCBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton URLRadioButton;
    private javax.swing.ButtonGroup audioButtonGroup;
    private javax.swing.JTextField audioSourceTextField;
    private javax.swing.JButton browseButton;
    private javax.swing.JRadioButton contentRepositoryRadioButton;
    private javax.swing.JRadioButton fileRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.ButtonGroup locationButtonGroup;
    private javax.swing.JSpinner lookDirectionSpinner;
    private javax.swing.JRadioButton manualRadioButton;
    private javax.swing.JComboBox placemarkCB;
    private javax.swing.JRadioButton placemarkRadioButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JTextField urlTF;
    private javax.swing.JSlider volumeSlider;
    private javax.swing.JSpinner xSpinner;
    private javax.swing.JSpinner ySpinner;
    private javax.swing.JSpinner zSpinner;
    // End of variables declaration//GEN-END:variables
}
