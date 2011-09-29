/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.modules.audiomanager.client;

import java.awt.Color;
import java.awt.event.KeyEvent;
import org.jdesktop.wonderland.modules.audiomanager.common.VolumeConverter;

import java.io.IOException;
import java.util.ResourceBundle;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import org.jdesktop.wonderland.client.jme.VMeter;
import org.jdesktop.wonderland.client.softphone.MicrophoneInfoListener;
import org.jdesktop.wonderland.client.softphone.SoftphoneControl;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;
import org.jdesktop.wonderland.client.softphone.SoftphoneListener;
import org.jdesktop.wonderland.client.softphone.SpeakerInfoListener;
import org.jdesktop.wonderland.modules.audiomanager.client.VuMeterMiniPanel.DelayButton;

/**
 * A microphone level control panel.
 *
 * @author jp
 * @author nsimpson
 */
public class VuMeterPanel extends javax.swing.JPanel implements
        SoftphoneListener, MicrophoneInfoListener, SpeakerInfoListener, DisconnectListener {

    private static final Logger LOGGER =
            Logger.getLogger(VuMeterPanel.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle");

    private static final double DEFAULT_WARNING_LIMIT = 0.9d;
    private static final Color CONNECTED_COLOR = new Color(51, 204, 0);
    private static final Color DISCONNECTED_COLOR = new Color(255, 0, 0);
    private static final Color PROBLEM_COLOR = new Color(255, 255, 51);

    private AudioManagerClient client;
    private VMeter micMeter;
    private VMeter speakerMeter;
    private int count;
    private int speakerCount;
    private VolumeConverter volumeConverter;
    private Color micPanelBackground;
    private Color speakerPanelBackground;
    private Color overLimitColor = Color.RED;
    private double micWarningLimit = DEFAULT_WARNING_LIMIT;
    private double speakerWarningLimit = DEFAULT_WARNING_LIMIT;
    private ImageIcon micMutedIcon;
    private ImageIcon micUnmutedIcon;
    private ImageIcon speakerMutedIcon;
    private ImageIcon speakerUnmutedIcon;

    private static ThreadLocal<Boolean> localChange = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    public VuMeterPanel() {
        this(null);
    }

    public VuMeterPanel(AudioManagerClient client) {
        this.client = client;

        try {
            setLocalChange(true);
            initComponents();
        } finally {
            setLocalChange(false);
        }
        
        micPanelBackground = micMeterPanel.getBackground();
        speakerPanelBackground = speakerMeterPanel.getBackground();
        micMutedIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/UserListMicMuteOn24x24.png"));
        micUnmutedIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/UserListMicMuteOff24x24.png"));
        speakerMutedIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/UserListSpeakerMuteOn24x24.png"));
        speakerUnmutedIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/UserListSpeakerMuteOff24x24.png"));

        volumeConverter = new VolumeConverter(micVolumeSlider.getMaximum());

        if (client != null) {
            client.addDisconnectListener(this);
        }

        // microphone volume meter
        micMeter = new VMeter("");
        micMeter.setBackground(Color.WHITE);
        micMeter.setForeground(Color.DARK_GRAY);
        micMeter.setPreferredSize(micMeterPanel.getPreferredSize());
        micMeter.setShowValue(false);
        micMeter.setShowTicks(false);
        micMeter.setMaxValue(1D);
        micMeter.setWarningValue(micWarningLimit);
        micMeter.setVisible(true);
        micMeterPanel.add(micMeter);

        // speaker volume meter
        speakerMeter = new VMeter("");
        speakerMeter.setBackground(Color.WHITE);
        speakerMeter.setForeground(Color.DARK_GRAY);
        speakerMeter.setPreferredSize(speakerMeterPanel.getPreferredSize());
        speakerMeter.setShowValue(false);
        speakerMeter.setShowTicks(false);
        speakerMeter.setMaxValue(1D);
        speakerMeter.setWarningValue(speakerWarningLimit);
        speakerMeter.setVisible(true);
        speakerMeterPanel.add(speakerMeter);

        SoftphoneControl sc = SoftphoneControlImpl.getInstance();
        sc.addSoftphoneListener(this);
        sc.addMicrophoneInfoListener(this);
        sc.addSpeakerInfoListener(this);

        client.addDisconnectListener(this);
    }

    public void startVuMeter(boolean start) {
	boolean isConnected = false;

	try { 
	    isConnected = SoftphoneControlImpl.getInstance().isConnected();
	} catch (IOException e) {
	}

	startMicVuMeter(start);
	startSpeakerVuMeter(start);
    }

    public void disconnected() {
        startMicVuMeter(false);
        startSpeakerVuMeter(false);
    }

    public void startMicVuMeter(final boolean startVuMeter) {
        SoftphoneControl sc = SoftphoneControlImpl.getInstance();

	boolean isConnected = false;

	try {
	    isConnected = sc.isConnected();
	} catch (IOException e) {
	}

	if (isConnected) {
            if (startVuMeter) {
                try {
                    sc.sendCommandToSoftphone("getMicrophoneVolume");
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING,
                        "Unable to get Microphone volume", e);
                }
	    }

	    try {
                sc.startMicVuMeter(startVuMeter);
	    } catch (IOException e) {
	        LOGGER.log(Level.WARNING, 
		    "Unable to start mic VU meter:  " + e.getMessage());
	    }
	}

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                setVisible(startVuMeter);
            }
        });
    }

    public void startSpeakerVuMeter(final boolean startVuMeter) {
        SoftphoneControl sc = SoftphoneControlImpl.getInstance();

	try {
	    if (sc.isConnected() == false) {
		return;
	    }
	} catch (IOException e) {
	    return;
	}

        if (startVuMeter) {
            try {
                sc.sendCommandToSoftphone("getSpeakerVolume");
            } catch (IOException e) {
                LOGGER.log(Level.WARNING,
                        "Unable to get speaker volume", e);
            }
        }

	try {
	    sc.startSpeakerVuMeter(startVuMeter);
	} catch (IOException e) {
	    LOGGER.log(Level.WARNING,
		"Unable to start speaker VU Meter:  " + e.getMessage());
	}
    }

    public void softphoneVisible(boolean isVisible) {
    }

    private boolean muted;

    public void softphoneMuted(boolean muted) {
	if (this.muted == muted) {
	    return;
	}

	this.muted = muted;

        if (muted) {
            pttButton.setEnabled(true);
            pttButton.setText(BUNDLE.getString("Push_to_talk"));
        } else {
            pttButton.setEnabled(false);
            pttButton.setText(BUNDLE.getString("Talking"));
        }

	setMicVolumeSlider(muted);
    }

    public void softphoneConnected(boolean connected) {
	enableControls(connected);
	startVuMeter(connected);
    }

    public void softphoneExited() {
	enableControls(false);
	startVuMeter(false);
    }

    private void enableControls(boolean isEnabled) {
	micMuteButton.setEnabled(isEnabled);
	micVolumeSlider.setEnabled(isEnabled);
	speakerMuteButton.setEnabled(isEnabled);
	speakerVolumeSlider.setEnabled(isEnabled);
        pttButton.setEnabled(isEnabled);

        if (isEnabled) {
            pttButton.setText(BUNDLE.getString("Push_to_talk"));
        } else {
            pttButton.setText(BUNDLE.getString("Disconnected"));
        }
    }

    public void softphoneTestUDPPort(int port, int duration) {
    }

    public void microphoneGainTooHigh() {
    }

    public void softphoneProblem(String problem) {
    }

    public void microphoneVuMeterValue(String value) {
        double volume = Math.abs(Double.parseDouble(value));

        final double v = Math.round(Math.sqrt(volume) * 100) / 100D;

	//System.out.println("Mic value " + value + " volume " + v);

	java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                micMeter.setValue(v);

                if (v > micWarningLimit) {
                    micMeterPanel.setBackground(overLimitColor);
                } else {
                    micMeterPanel.setBackground(micPanelBackground);
                }
            }
	});
    }

    public void microphoneVolume(String data) {
        try {
            setLocalChange(true);

            micVolumeSlider.setValue(volumeConverter.getVolume((Float.parseFloat(data))));
            softphoneMuted(SoftphoneControlImpl.getInstance().isMuted());
        } finally {
            setLocalChange(false);
        }
    }

    private Timer speakerVuMeterTimer;

    public synchronized void speakerVuMeterValue(String value) {
        double volume = Math.abs(Double.parseDouble(value));

        final double v = Math.round(Math.sqrt(volume) * 100) / 100D;

	//System.out.println("Speaker value " + value + " volume " + v);

	java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                speakerMeter.setValue(v);

                if (v > speakerWarningLimit) {
                    speakerMeterPanel.setBackground(overLimitColor);
                } else {
                    speakerMeterPanel.setBackground(speakerPanelBackground);
                }
            }
        });

	if (speakerVuMeterTimer != null) {
	    speakerVuMeterTimer.cancel();
	}

	speakerVuMeterTimer = new Timer();

	speakerVuMeterTimer.schedule(new TimerTask() {
		
	    public void run() {
		java.awt.EventQueue.invokeLater(new Runnable() {

	            public void run() {
               		speakerMeter.setValue(0);
		    }

	        });
	    }

	}, 2000);
    }

    public void speakerVolume(String data) {
        speakerVolumeSlider.setValue(volumeConverter.getVolume((Float.parseFloat(data))));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        micVolumeSlider = new javax.swing.JSlider();
        micMeterPanel = new javax.swing.JPanel();
        micMuteButton = new javax.swing.JButton();
        speakerVolumeSlider = new javax.swing.JSlider();
        speakerMeterPanel = new javax.swing.JPanel();
        speakerMuteButton = new javax.swing.JButton();
        panelToggleButton = new javax.swing.JButton();
        pttButton = new DelayButton();

        setPreferredSize(new java.awt.Dimension(95, 244));
        setLayout(null);

        micVolumeSlider.setMinorTickSpacing(10);
        micVolumeSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        micVolumeSlider.setPaintTicks(true);
        micVolumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                micVolumeSliderStateChanged(evt);
            }
        });
        add(micVolumeSlider);
        micVolumeSlider.setBounds(5, 14, 20, 155);

        micMeterPanel.setMinimumSize(new java.awt.Dimension(30, 160));
        micMeterPanel.setPreferredSize(new java.awt.Dimension(30, 160));
        add(micMeterPanel);
        micMeterPanel.setBounds(20, 10, 20, 160);

        micMuteButton.setFont(new java.awt.Font("Arial", 1, 8));
        micMuteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListMicMuteOff24x24.png"))); // NOI18N
        micMuteButton.setFocusable(false);
        micMuteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                micMuteButtonActionPerformed(evt);
            }
        });
        add(micMuteButton);
        micMuteButton.setBounds(20, 175, 24, 24);

        speakerVolumeSlider.setMinorTickSpacing(10);
        speakerVolumeSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        speakerVolumeSlider.setPaintTicks(true);
        speakerVolumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                speakerVolumeSliderStateChanged(evt);
            }
        });
        add(speakerVolumeSlider);
        speakerVolumeSlider.setBounds(50, 14, 20, 155);

        speakerMeterPanel.setMinimumSize(new java.awt.Dimension(30, 160));
        speakerMeterPanel.setPreferredSize(new java.awt.Dimension(30, 160));
        add(speakerMeterPanel);
        speakerMeterPanel.setBounds(65, 10, 20, 160);

        speakerMuteButton.setFont(new java.awt.Font("Arial", 1, 8));
        speakerMuteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListSpeakerMuteOff24x24.png"))); // NOI18N
        speakerMuteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speakerMuteButtonActionPerformed(evt);
            }
        });
        add(speakerMuteButton);
        speakerMuteButton.setBounds(65, 175, 24, 24);

        panelToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/downArrow23x10.png"))); // NOI18N
        panelToggleButton.setBorder(null);
        panelToggleButton.setMaximumSize(new java.awt.Dimension(63, 14));
        panelToggleButton.setMinimumSize(new java.awt.Dimension(63, 14));
        panelToggleButton.setPreferredSize(new java.awt.Dimension(20, 14));
        panelToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                panelToggleButtonActionPerformed(evt);
            }
        });
        add(panelToggleButton);
        panelToggleButton.setBounds(70, 227, 20, 14);

        pttButton.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle"); // NOI18N
        pttButton.setText(bundle.getString("VuMeterPanel.pttButton.text")); // NOI18N
        pttButton.setPreferredSize(new java.awt.Dimension(92, 23));
        pttButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pttButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pttButtonMouseReleased(evt);
            }
        });
        pttButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pttButtonKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pttButtonKeyReleased(evt);
            }
        });
        add(pttButton);
        pttButton.setBounds(1, 202, 92, 23);
    }// </editor-fold>//GEN-END:initComponents

    private int micVolumeSliderValue = 50;
    private int previousMicVolumeSliderValue = 50;

    private void micVolumeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_micVolumeSliderStateChanged
	previousMicVolumeSliderValue = micVolumeSliderValue;
	micVolumeSliderValue = micVolumeSlider.getValue();

        // ignore changes that originated from this component (as opposed to
        // from the user)
        if (isLocalChange()) {
            return;
        }

        client.setMute(micVolumeSliderValue == 0);

	if (micVolumeSliderValue != 0) {
            try {
                SoftphoneControlImpl.getInstance().sendCommandToSoftphone(
		    "microphoneVolume=" + volumeConverter.getVolume(micVolumeSliderValue));
            } catch (IOException e) {
                LOGGER.log(Level.WARNING,
		    "Unable to send microphone volume command to softphone", e);
            }
	}
    }//GEN-LAST:event_micVolumeSliderStateChanged

    private boolean speakerMuted;
    private int speakerVolumeSliderValue = 50;
    private int previousSpeakerVolumeSliderValue = 50;

    private void speakerVolumeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_speakerVolumeSliderStateChanged
	previousSpeakerVolumeSliderValue = speakerVolumeSliderValue;
	speakerVolumeSliderValue = speakerVolumeSlider.getValue();

        double volume = volumeConverter.getVolume(speakerVolumeSliderValue);

	setSpeakerMutedIcon(speakerVolumeSliderValue == 0);

        try {
            SoftphoneControlImpl.getInstance().sendCommandToSoftphone("speakerVolume=" + volume);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,
		"Unable to send speaker volume command to softphone", e);
        }
    }//GEN-LAST:event_speakerVolumeSliderStateChanged

    private void micMuteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_micMuteButtonActionPerformed
        if (previousMicVolumeSliderValue != 0) {
            client.toggleMute();
        } else {
            client.setMute(true);
        }
    }//GEN-LAST:event_micMuteButtonActionPerformed

    private void setMicVolumeSlider(final boolean isMuted) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setMicVolumeSliderLater(isMuted);
            }
        });
    }

    private void setMicVolumeSliderLater(boolean isMuted) {
	try {
            setLocalChange(true);

            if (isMuted) {
                micVolumeSlider.setValue(0);
                micMuteButton.setIcon(micMutedIcon);
            } else {
                micVolumeSlider.setValue(previousMicVolumeSliderValue);
                micMuteButton.setIcon(micUnmutedIcon);
                micMuteButton.setIcon(micUnmutedIcon);
            }
        } finally {
            setLocalChange(false);
        }
    }

    private void speakerMuteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speakerMuteButtonActionPerformed
	boolean speakerMuted = !this.speakerMuted;

	if (previousSpeakerVolumeSliderValue == 0) {
	    speakerMuted = true;
	} else {
	    if (speakerMuted) {
	        speakerVolumeSlider.setValue(0);
	    } else {
	        speakerVolumeSlider.setValue(previousSpeakerVolumeSliderValue);
	    }
	}

	setSpeakerMutedIcon(speakerMuted);
    }//GEN-LAST:event_speakerMuteButtonActionPerformed

    private void panelToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_panelToggleButtonActionPerformed
        client.miniVUMeter();
}//GEN-LAST:event_panelToggleButtonActionPerformed

    private void pttButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pttButtonMousePressed
        client.setMute(false);
}//GEN-LAST:event_pttButtonMousePressed

    private void pttButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pttButtonMouseReleased
        client.setMute(true);
}//GEN-LAST:event_pttButtonMouseReleased

    private void pttButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pttButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            client.setMute(false);
        }
}//GEN-LAST:event_pttButtonKeyPressed

    private void pttButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pttButtonKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            client.setMute(true);
        }
}//GEN-LAST:event_pttButtonKeyReleased

    private void setSpeakerMutedIcon(boolean speakerMuted) {
	this.speakerMuted = speakerMuted;

	if (speakerMuted) {
	    speakerMuteButton.setIcon(speakerMutedIcon);
	} else {
	    speakerMuteButton.setIcon(speakerUnmutedIcon);
	}
    }

    private void setLocalChange(boolean local) {
        localChange.set(local);
    }

    private boolean isLocalChange() {
        return localChange.get();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel micMeterPanel;
    private javax.swing.JButton micMuteButton;
    private javax.swing.JSlider micVolumeSlider;
    private javax.swing.JButton panelToggleButton;
    private javax.swing.JButton pttButton;
    private javax.swing.JPanel speakerMeterPanel;
    private javax.swing.JButton speakerMuteButton;
    private javax.swing.JSlider speakerVolumeSlider;
    // End of variables declaration//GEN-END:variables
}
