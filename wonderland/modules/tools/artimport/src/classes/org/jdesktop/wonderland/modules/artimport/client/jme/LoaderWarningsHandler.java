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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFrame;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDDialog.BUTTONS;
import org.jdesktop.wonderland.client.hud.HUDDialog.MESSAGE_TYPE;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.hud.HUDMessage;
import org.jdesktop.wonderland.client.jme.artimport.ImportedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderListener;

/**
 * Class that listens for, and keeps a record of, any warnings generated during
 * Model import. The class also manages the UI panel and Frame that users can use
 * to review the data, the ArtToolsPlugin adds the necessary menu items to the
 * system menus to show the frame.
 *
 * @author paulby
 */
public class LoaderWarningsHandler implements LoaderListener {

    private final HashMap<ImportedModel, ModelErrors> errorMap = new HashMap();
    private final Object panelLock = new Object();
    private LoaderWarningsPanel panel = null;
    private JFrame frame=null;

    LoaderWarningsHandler() {
    }

    void showJFrame(boolean visible) {
        synchronized(panelLock) {
            if (visible) {
                if (frame==null) {
                    frame = new JFrame(java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/artimport/client/jme/resources/Bundle").getString("MODEL_IMPORT_WARNINGS"));
                    panel = new LoaderWarningsPanel(this);
                    frame.getContentPane().add(panel);
                    frame.pack();
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            showJFrame(false);
                        }
                    });
                    synchronized(errorMap) {
                        for(ModelErrors me : errorMap.values()) {
                            panel.addModel(me.model);
                        }
                    }
                }
                frame.setVisible(true);
                frame.toFront();
            } else {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
                panel = null;
            }
        }
    }

    public void modelImportErrors(ImportedModel model,
                                  Level level,
                                  String msg,
                                  Throwable throwable) {
        ModelErrors me;

        synchronized(errorMap) {
            me = errorMap.get(model);
            if (me==null) {
                // First time we've seen an error for this model
                me = new ModelErrors(model);
                errorMap.put(model, me);

                // Notify the user via the HUD.
                HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
                HUDMessage message = mainHUD.createMessage(java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/artimport/client/jme/resources/Bundle").getString("MODEL_LOADING_GENERATED_WARNINGS"), MESSAGE_TYPE.WARNING, BUTTONS.NONE);
                message.setPreferredLocation(Layout.NORTH);
                mainHUD.addComponent(message);
                message.setVisible(true);
                message.setVisible(false, 5000);
            }
            me.addError(level, msg);
        }
    }

    ModelErrors getModelErrors(ImportedModel selection) {
        synchronized(errorMap) {
            return errorMap.get(selection);
        }
    }

    void clearAll() {
        synchronized(errorMap) {
            errorMap.clear();
        }
    }

    /**
     * Container for errors for a give model
     */
    class ModelErrors {
        private ImportedModel model;
        private List<String> errors = new ArrayList();

        public ModelErrors(ImportedModel model) {
            this.model = model;
            synchronized(panelLock) {
                if (panel!=null) {
                    panel.addModel(model);
                }
            }
        }

        public void addError(Level level, String msg) {
            String e = level.toString()+"  "+msg;
            errors.add(e);
            synchronized(panelLock) {
                if (panel!=null) {
                    panel.addError(model, e);
                }
            }
        }

        Iterable<String> getErrors() {
            return errors;
        }
    }

}
