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
package org.jdesktop.wonderland.modules.avatarbase.client.imi;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.LoadingInfo;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams.ColorConfigElement;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams.ConfigElement;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams.ConfigType;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams.GenderConfigElement;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams.HairColorConfigElement;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry.AvatarListener;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;

/**
 * A JFrame to configure the standard attributes of an avatar. There is a single
 * instance of this class that should be used on the system.
 * 
 * @author jkaplan
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class ImiAvatarDetailsJDialog extends javax.swing.JDialog {

    private static final Logger LOGGER =
            Logger.getLogger(ImiAvatarDetailsJDialog.class.getName());

    // The primary resource bundle for the GUI
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle");

    // The resource bundle for the combo boxes
    private static final ResourceBundle PRESETS_BUNDLE =
            ResourceBundle.getBundle("org/jdesktop/wonderland/modules/" +
            "avatarbase/client/imi/resources/Bundle");

    private final Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private final Cursor normalCursor = Cursor.getDefaultCursor();

    // The avatar we are currently configuring
    private ImiAvatar avatar = null;

    // The original avatar name when the dialog is first opened. This is used
    // to determine whether the avatar name has actually changed.
    private String originalAvatarName = null;

    // The current set of attributes for the avatar configuration
    private WonderlandCharacterParams currentParams = null;

    // This boolean indicates whether the values of the GUI components are being
    // set programmatically. In such a case, we do not want to generate calls
    // to the avatar system.
    private boolean setLocal = false;

    /** Creates new form AvatarDetailsFrame */
    private ImiAvatarDetailsJDialog() {

        // Initialize the GUI components
        initComponents();

        // Listen for changes in the value of Hair, and apply immediate
        hairComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (setLocal == false) {
                    comboBoxChanged(hairComboBox, ConfigType.HAIR);
                }
            }
        });

        // Listen for changes in the value of Head, and apply immediate
        headComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (setLocal == false) {
                    comboBoxChanged(headComboBox, ConfigType.HEAD);
                }
            }
        });

        // Listen for changes in the value of Torso, and apply immediate
        torsoComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (setLocal == false) {
                    comboBoxChanged(torsoComboBox, ConfigType.TORSO);
                }
            }
        });

        // Listen for changes in the value of Legs, and apply immediate
        legsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (setLocal == false) {
                    comboBoxChanged(legsComboBox, ConfigType.LEGS);
                }
            }
        });

        // Listen for changes in the value of Jacket, and apply immediate
        jacketComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (setLocal == false) {
                    comboBoxChanged(jacketComboBox, ConfigType.JACKET);
                }
            }
        });

        // Listen for changes in the value of Hands, and apply immediate
        handsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (setLocal == false) {
                    comboBoxChanged(handsComboBox, ConfigType.HANDS);
                }
            }
        });

        // Listen for changes in the value of Feet, and apply immediate
        feetComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (setLocal == false) {
                    comboBoxChanged(feetComboBox, ConfigType.FEET);
                }
            }
        });

        // Listen for the Hair Color.. button click to configure the its color
        hairButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configureColor(ConfigType.HAIR_COLOR, BUNDLE.getString("Hair_Color"));
            }
        });

        // For now, do not display the Skin Color button...
        skinButton.setVisible(false);
        
        // Listen for the Skin Color.. button click to configure the its color
        skinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configureColor(ConfigType.SKIN_COLOR, BUNDLE.getString("Skin_Color"));
            }
        });

        // Listen for the Shirt Color.. button click to configure the its color
        torsoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configureColor(ConfigType.SHIRT_COLOR, BUNDLE.getString("Shirt_Color"));
            }
        });

        // Listen for the Pants Color.. button click to configure the its color
        pantsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configureColor(ConfigType.PANTS_COLOR, BUNDLE.getString("Pants_Color"));
            }
        });

        // Listen for the Shoe Color.. button click to configure the its color
        shoeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configureColor(ConfigType.SHOE_COLOR, BUNDLE.getString("Shoe_Color"));
            }
        });

        // Listen when the gender radio buttons are selected. Update the gender
        // and apply
        maleRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reset the GUI with a new male avatar and apply the changes
                try {
                    if (setLocal == false) {
                        WonderlandCharacterParams params = WonderlandCharacterParams.loadMale();
                        setAttributes(params);
                        apply();
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Unable to load male avatar", ex);
                    return;
                }
            }
        });

        femaleRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reset the GUI with a new female avatar and apply the changes
                try {
                    if (setLocal == false) {
                        WonderlandCharacterParams params = WonderlandCharacterParams.loadFemale();
                        setAttributes(params);
                        apply();
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Unable to load male avatar", ex);
                    return;
                }
            }
        });

        // Upon the Cancel button, revert the avatar configuration back to the
        // original settings when the dialog was first opened and close the
        // dialog
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Revert to the avatar currently set and close the window
                AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
                registry.setAvatarInUse(registry.getAvatarInUse(), true);
                setVisible(false);
            }
        });

        // Upon the Use button, we need to set the avatar as the current
        // avatar. We do not need to apply() again, since that is done for
        // each change.
        useButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                use();
            }
        });

        // For the Randomize button, select a random set of attributes and
        // apply
        randomizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentParams.randomize();
                updateComboBoxes();
                apply();
            }
        });

        // Listen for when the window is close and do a cancel()
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Revert to the avatar currently set and close the window
                AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
                registry.setAvatarInUse(registry.getAvatarInUse(), true);
            }
        });

        // Listen to see if the avatar has been deleted. This can happen when
        // the window is up, but the Delete button has been pressed in the
        // main user list. We simply just close the window here.
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        registry.addAvatarListener(new AvatarListener() {

            public void avatarAdded(AvatarSPI added) {
                // We don't care if an avatar has been added.
            }

            public void avatarRemoved(AvatarSPI removed) {
                // If the avatar remove equals this avatar, then close this
                // dialog
                if (avatar != null && avatar.equals(removed) == true) {
                    setVisible(false);
                }
            }
        });
    }

    /**
     * Singleton to hold instance of this class. This holder class is loaded
     * on the first execution of getImiAvatarDetailsJDialog()
     */
    private static class DetailsDialogHolder {
        private final static ImiAvatarDetailsJDialog d = new ImiAvatarDetailsJDialog();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final ImiAvatarDetailsJDialog getImiAvatarDetailsJDialog() {
        return DetailsDialogHolder.d;
    }

    /**
     * Sets the current avatar in use and updates the GUI. This method is
     * thread safe, so only a single thread can set the avatar at once.
     * @param avatar the current avatar
     */
    public synchronized void setAvatar(ImiAvatar avatar) {
        this.avatar = avatar;
        this.originalAvatarName = avatar.getName();

        // Initialize an empty avatar to a default Male. Here, the original
        // and current are initialized to the same thing. Make sure the
        // call to setAttributes() happens in the AWT Event Thread.
        final WonderlandCharacterParams params = avatar.getAvatarParams(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setAttributes(params);
                apply();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean isVisible) {
        // If being made visible, then update the currently visible avatar to
        // the one we are configuring
        super.setVisible(isVisible);
    }

    /**
     * Sets the current attributes of the avatar and refreshes the GUI.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     * 
     * @param attributes The attribute of the avatar configuration
     */
    public void setAttributes(WonderlandCharacterParams attributes) {
        if (EventQueue.isDispatchThread() == false) {
            LOGGER.severe("SET ATTRIBUTES NOT IN AWT EVENT THREAD!");
        }

        // Make a copy of the attributes given, so that any changes we make
        // here will not be reflected in the original.
        currentParams = attributes.clone();

        // Initialize the values of the combo boxes
        populateComboBox(hairComboBox, ConfigType.HAIR);
        populateComboBox(headComboBox, ConfigType.HEAD);
        populateComboBox(torsoComboBox, ConfigType.TORSO);
        populateComboBox(legsComboBox, ConfigType.LEGS);
        populateComboBox(jacketComboBox, ConfigType.JACKET);
        populateComboBox(handsComboBox, ConfigType.HANDS);
        populateComboBox(feetComboBox, ConfigType.FEET);

        // Update the name, gender, and combox boxes of the avatar
        updateAvatarName();
        updateGender();
        updateComboBoxes();
    }

    /**
     * Initializes the given combo box with the elements from the given
     * configuration type (ConfigType).
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     */
    private void populateComboBox(JComboBox box, ConfigType type) {
        // Make sure we block out any events that happen because the elements
        // in the combo box are being updated
        setLocalChanges(true);
        try {
            // First remove all of the existing elements in the combo box.
            List<ConfigElement> elements = currentParams.getElements(type);
            DefaultComboBoxModel m = (DefaultComboBoxModel) box.getModel();
            m.removeAllElements();

            // Iterate through the list of presets given. From the description
            // look-up the resource bundle for the display string.
            int i = 0;
            for (ConfigElement ce : elements) {
                String description = ce.getDescription();
                m.insertElementAt(PRESETS_BUNDLE.getString(description), i);
                i++;
            }
        } finally {
            setLocalChanges(false);
        }
    }

    /**
     * Updates all of the combo boxes with the current settings.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     */
    private void updateComboBoxes() {
        // Update each of the combo boxes based upon the item selected
        updateComboBox(hairComboBox, ConfigType.HAIR);
        updateComboBox(headComboBox, ConfigType.HEAD);
        updateComboBox(torsoComboBox, ConfigType.TORSO);
        updateComboBox(legsComboBox, ConfigType.LEGS);
        updateComboBox(jacketComboBox, ConfigType.JACKET);
        updateComboBox(handsComboBox, ConfigType.HANDS);
        updateComboBox(feetComboBox, ConfigType.FEET);
    }

    /**
     * Updates the selected element of the given combo box with the chosen
     * element for the given configuration type (ConfigType). This simply takes
     * the position in the list of the configuration element and selects the
     * corresponding position in the combo box.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     */
    private void updateComboBox(JComboBox box, ConfigType type) {
        // Make sure we block out any events that happen because the elements
        // in the combo box are being updated
        setLocalChanges(true);
        try {
            int index = currentParams.getElementIndex(type);
            if (index == -1) {
                LOGGER.warning("Unable to find selected element for " + type);
                return;
            }
            box.setSelectedIndex(index);
        } finally {
            setLocalChanges(false);
        }
    }

    /**
     * Updates the name of the text with the current value in the avatar
     * configuration.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread
     */
    private void updateAvatarName() {
        nameTextField.setText(avatar.getName());
    }

    /**
     * Updates the selected gender radio button with the current value in the
     * avatar configuration attributes.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread
     */
    private void updateGender() {
        // Make sure we block out any events that happen because the elements
        // in the combo box are being updated
        setLocalChanges(true);
        try {
            GenderConfigElement gender =
                    (GenderConfigElement) currentParams.getElement(ConfigType.GENDER);
            if (gender.getGender() == GenderConfigElement.MALE) {
                maleRadioButton.setSelected(true);
                femaleRadioButton.setSelected(false);
            }
            else {
                maleRadioButton.setSelected(false);
                femaleRadioButton.setSelected(true);
            }
        } finally {
            setLocalChanges(false);
        }
    }

    /**
     * Handles when a selection on the given combo box is made. Updates the
     * configuration and applies to the avatar immediate.
     */
    private void comboBoxChanged(JComboBox box, ConfigType type) {
        // Fetch the currently selected item in the combo box and update the
        // settings.
        int index = box.getSelectedIndex();
        if (index == -1) {
            LOGGER.warning("No item is selected for " + type);
            return;
        }
        currentParams.setElement(type, index);

        // Apply the settings to the avatar
        apply();
    }

    /**
     * Configures a color property, takin the configuration type and a dialog
     * title.
     */
    private void configureColor(ConfigType type, String title) {
        // Fetch the current color given the configuration type. This assumes
        // it exists, otherwise, it's a bad error.
        ColorConfigElement config =
                (ColorConfigElement) currentParams.getElement(type);
        if (config == null) {
            LOGGER.info("Unable to find config element " + type);
            config = new HairColorConfigElement();
            config.setR(1.0f);
            config.setG(1.0f);
            config.setB(1.0f);
        }


        // Create the initial color, each color component is a floating point
        // value between 0.0 and 1.0, inclusive. Show the dialog.
        Color rgb = new Color(config.getR(), config.getG(), config.getB());
        Color hairColor = JColorChooser.showDialog(this, title, rgb);
        if (hairColor == null) {
            return;
        }

        // Take the new values from the dialog and set the configuration
        // element. We need to convert the integer values between 0 and 255 to
        // floating point values between 0.0 and 1.0.
        config.setR(hairColor.getRed() / 255.0f);
        config.setG(hairColor.getGreen() / 255.0f);
        config.setB(hairColor.getBlue() / 255.0f);
        currentParams.setElement(type, config);
        apply();
    }

    /**
     * Sets whether the changes being made to the GUI comopnents are doing so
     * programmatically, rather than via the users. This is used to make sure
     * that requests to the avatar system are not made at the wrong time.
     */
    private void setLocalChanges(boolean isLocal) {
        setLocal = isLocal;
    }

    /**
     * Attempt to use the current avatar. Close the window if so.
     */
    private void use() {
        final AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();

        // Make sure the name text field is not empty.
        final String newAvatarName = nameTextField.getText().trim();
        if (newAvatarName == null || newAvatarName.equals("") == true) {
            String msg = BUNDLE.getString("PLEASE_ENTER_AVATAR_NAME");
            String title = BUNDLE.getString("AVATAR_NAME");
            JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Make sure there are no spaces in the avatar name.
        // XXX Workaround for bug in content repo XXX
        if (newAvatarName.indexOf(" ") != -1) {
            String msg = BUNDLE.getString("AVATAR_NAME_SPACES");
            String title = BUNDLE.getString("AVATAR_NAME");
            JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check to see that the avatar name is not already taken. We only check
        // if the name has actually changed.
        AvatarSPI oldAvatar = registry.getAvatarByName(newAvatarName);
        if (newAvatarName.equals(originalAvatarName) == false && oldAvatar != null) {
            String msg = BUNDLE.getString("THE_AVATAR_NAME_TAKEN");
            msg = MessageFormat.format(msg, newAvatarName);
            String title = BUNDLE.getString("AVATAR_NAME");
            JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If we are not changing the name of the avatar, then we just save the
        // avatar, close the window and return. We do this in a thread to make
        // sure the UI does not block without indication.
        if (newAvatarName.equals(originalAvatarName) == true) {
            setBusy(true);
            new Thread() {
                @Override
                public void run() {
                    avatar.setAvatarParams(currentParams);
                    save(avatar);
                    registry.setAvatarInUse(avatar, false);

                    // Close the dialog in the AWT Event Thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setBusy(false);
                            setVisible(false);
                        }
                    });
                }
            }.start();
            return;
        }

        // If we are changing the name of the avatar, things get much more
        // complicated. We want to delete the old avatar, but we need to save
        // and use the new avatar first. (Because if we delete the old avatar
        // first, the system will default back to the "Default" avatar to use
        // perhaps). First, construct a new avatar, save it and use. We do
        // all of this in a thread so that we do not block the GUI
        setBusy(true);
        new Thread() {
            @Override
            public void run() {
                ImiAvatar newAvatar = ImiAvatar.createAvatar(newAvatarName);
                newAvatar.setAvatarParams(currentParams);
                save(newAvatar);
                registry.setAvatarInUse(newAvatar, false);

                // Next, delete the old avatar and close the dialog. We only
                // want to delete it if the old avatar is really new
                if (registry.getAvatarByName(originalAvatarName) != null) {
                    avatar.delete();
                }

                // Close the dialog in the AWT Event Thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setBusy(false);
                        setVisible(false);
                    }
                });
            }
        }.start();
    }

    /**
     * Saves the current avatar to the system.
     */
    private void save(ImiAvatar avatar) {

        // Talk to the IMI configuration manager and save it. It takes care
        // of adding it to the list of registered avatars.
        ImiAvatarConfigManager m = ImiAvatarConfigManager.getImiAvatarConfigManager();
        try {
            m.saveAvatar(avatar);
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.WARNING,
                    "Unable to save avatar " + avatar.getName(), excp);
        }
    }

    /**
     * Apply the properties of the current avatar settings.
     *
     * NOTE: This method assumes it is being called within the AWT Event Thread.
     */
    private void apply() {
        // Set the Wait cursor to give an indication of the update
        setBusy(true);

        // Update the avatar in the background thread.
        Runnable runner = new Runnable() {
            public void run() {
                // Fetch the name of the avatar from the text field first for
                // the loading messages and also the primary view cell
                ViewCell cell = ClientContextJME.getViewManager().getPrimaryViewCell();
                String name = nameTextField.getText().trim();

                // Ask the avatar to generate its character. If null, then
                // clean up the loading message and cursor and return.
                WlAvatarCharacter character = ImiAvatar.getAvatarCharacter(currentParams);
                if (character == null) {
                    LoadingInfo.finishedLoading(cell.getCellID(), name);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setBusy(false);
                        }
                    });
                    return;
                }

                // Change the avatar to the new character.
                AvatarImiJME renderer = AvatarImiJME.getPrimaryAvatarRenderer();
                renderer.changeAvatar(character);

                // Set the cursor in the AWT Event Thread.
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setBusy(false);
                    }
                });
            }
        };
        new Thread(runner).start();
    }

    /**
     * Sets whether the entire dialog is (busy) and should not be interacted
     * with or not
     */
    private void setBusy(boolean isBusy) {
        if (isBusy == true) {
            setCursor(waitCursor);
        }
        else {
            setCursor(normalCursor);
        }
        
        useButton.setEnabled(!isBusy);
        cancelButton.setEnabled(!isBusy);
        nameLabel.setEnabled(!isBusy);
        nameTextField.setEnabled(!isBusy);
        genderLabel.setEnabled(!isBusy);
        maleRadioButton.setEnabled(!isBusy);
        femaleRadioButton.setEnabled(!isBusy);
        hairComboBox.setEnabled(!isBusy);
        headComboBox.setEnabled(!isBusy);
        torsoComboBox.setEnabled(!isBusy);
        legsComboBox.setEnabled(!isBusy);
        jacketComboBox.setEnabled(!isBusy);
        handsComboBox.setEnabled(!isBusy);
        feetComboBox.setEnabled(!isBusy);
        hairLabel.setEnabled(!isBusy);
        headLabel.setEnabled(!isBusy);
        torsoLabel.setEnabled(!isBusy);
        legsLabel.setEnabled(!isBusy);
        jacketLabel.setEnabled(!isBusy);
        handsLabel.setEnabled(!isBusy);
        feetLabel.setEnabled(!isBusy);
        hairButton.setEnabled(!isBusy);
        skinButton.setEnabled(!isBusy);
        torsoButton.setEnabled(!isBusy);
        pantsButton.setEnabled(!isBusy);
        shoeButton.setEnabled(!isBusy);
        randomizeButton.setEnabled(!isBusy);
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

        namePanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        genderPanel = new javax.swing.JPanel();
        genderLabel = new javax.swing.JLabel();
        femaleRadioButton = new javax.swing.JRadioButton();
        maleRadioButton = new javax.swing.JRadioButton();
        mainConfigPanel = new javax.swing.JPanel();
        hairLabel = new javax.swing.JLabel();
        hairComboBox = new javax.swing.JComboBox();
        hairButton = new javax.swing.JButton();
        headLabel = new javax.swing.JLabel();
        headComboBox = new javax.swing.JComboBox();
        skinButton = new javax.swing.JButton();
        torsoLabel = new javax.swing.JLabel();
        torsoComboBox = new javax.swing.JComboBox();
        torsoButton = new javax.swing.JButton();
        jacketLabel = new javax.swing.JLabel();
        jacketComboBox = new javax.swing.JComboBox();
        jacketBlankPanel = new javax.swing.JPanel();
        handsLabel = new javax.swing.JLabel();
        handsComboBox = new javax.swing.JComboBox();
        handsBlankPanel = new javax.swing.JPanel();
        legsLabel = new javax.swing.JLabel();
        legsComboBox = new javax.swing.JComboBox();
        pantsButton = new javax.swing.JButton();
        feetLabel = new javax.swing.JLabel();
        feetComboBox = new javax.swing.JComboBox();
        shoeButton = new javax.swing.JButton();
        randomizeButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        useButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("ImiAvatarDetailsJDialog.title")); // NOI18N

        namePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        namePanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.nameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        namePanel.add(nameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        namePanel.add(nameTextField, gridBagConstraints);

        genderLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.genderLabel.text")); // NOI18N
        genderPanel.add(genderLabel);

        femaleRadioButton.setText(bundle.getString("ImiAvatarDetailsJDialog.femaleRadioButton.text")); // NOI18N
        genderPanel.add(femaleRadioButton);

        maleRadioButton.setText(bundle.getString("ImiAvatarDetailsJDialog.maleRadioButton.text")); // NOI18N
        genderPanel.add(maleRadioButton);

        mainConfigPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainConfigPanel.setLayout(new java.awt.GridBagLayout());

        hairLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.hairLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        mainConfigPanel.add(hairLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainConfigPanel.add(hairComboBox, gridBagConstraints);

        hairButton.setText(bundle.getString("ImiAvatarDetailsJDialog.hairButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainConfigPanel.add(hairButton, gridBagConstraints);

        headLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.headLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        mainConfigPanel.add(headLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainConfigPanel.add(headComboBox, gridBagConstraints);

        skinButton.setText(bundle.getString("ImiAvatarDetailsJDialog.skinButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainConfigPanel.add(skinButton, gridBagConstraints);

        torsoLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.torsoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        mainConfigPanel.add(torsoLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainConfigPanel.add(torsoComboBox, gridBagConstraints);

        torsoButton.setText(bundle.getString("ImiAvatarDetailsJDialog.torsoButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainConfigPanel.add(torsoButton, gridBagConstraints);

        jacketLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.jacketLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        mainConfigPanel.add(jacketLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainConfigPanel.add(jacketComboBox, gridBagConstraints);

        jacketBlankPanel.setLayout(new java.awt.GridLayout(1, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        mainConfigPanel.add(jacketBlankPanel, gridBagConstraints);

        handsLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.handsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        mainConfigPanel.add(handsLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainConfigPanel.add(handsComboBox, gridBagConstraints);

        handsBlankPanel.setLayout(new java.awt.GridLayout(1, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        mainConfigPanel.add(handsBlankPanel, gridBagConstraints);

        legsLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.legsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        mainConfigPanel.add(legsLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainConfigPanel.add(legsComboBox, gridBagConstraints);

        pantsButton.setText(bundle.getString("ImiAvatarDetailsJDialog.pantsButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainConfigPanel.add(pantsButton, gridBagConstraints);

        feetLabel.setText(bundle.getString("ImiAvatarDetailsJDialog.feetLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        mainConfigPanel.add(feetLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainConfigPanel.add(feetComboBox, gridBagConstraints);

        shoeButton.setText(bundle.getString("ImiAvatarDetailsJDialog.shoeButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainConfigPanel.add(shoeButton, gridBagConstraints);

        randomizeButton.setText(bundle.getString("ImiAvatarDetailsJDialog.randomizeButton.text")); // NOI18N

        cancelButton.setText(bundle.getString("ImiAvatarDetailsJDialog.cancelButton.text")); // NOI18N
        buttonPanel.add(cancelButton);

        useButton.setText(bundle.getString("ImiAvatarDetailsJDialog.useButton.text")); // NOI18N
        buttonPanel.add(useButton);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(namePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
            .add(genderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
            .add(mainConfigPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(randomizeButton)
                .addContainerGap(230, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(buttonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(namePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(genderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(mainConfigPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(randomizeButton)
                .add(9, 9, 9)
                .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox feetComboBox;
    private javax.swing.JLabel feetLabel;
    private javax.swing.JRadioButton femaleRadioButton;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JPanel genderPanel;
    private javax.swing.JButton hairButton;
    private javax.swing.JComboBox hairComboBox;
    private javax.swing.JLabel hairLabel;
    private javax.swing.JPanel handsBlankPanel;
    private javax.swing.JComboBox handsComboBox;
    private javax.swing.JLabel handsLabel;
    private javax.swing.JComboBox headComboBox;
    private javax.swing.JLabel headLabel;
    private javax.swing.JPanel jacketBlankPanel;
    private javax.swing.JComboBox jacketComboBox;
    private javax.swing.JLabel jacketLabel;
    private javax.swing.JComboBox legsComboBox;
    private javax.swing.JLabel legsLabel;
    private javax.swing.JPanel mainConfigPanel;
    private javax.swing.JRadioButton maleRadioButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel namePanel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton pantsButton;
    private javax.swing.JButton randomizeButton;
    private javax.swing.JButton shoeButton;
    private javax.swing.JButton skinButton;
    private javax.swing.JButton torsoButton;
    private javax.swing.JComboBox torsoComboBox;
    private javax.swing.JLabel torsoLabel;
    private javax.swing.JButton useButton;
    // End of variables declaration//GEN-END:variables
}
