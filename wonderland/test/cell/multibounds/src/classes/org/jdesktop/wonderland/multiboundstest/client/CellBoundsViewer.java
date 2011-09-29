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
package org.jdesktop.wonderland.multiboundstest.client;

import org.jdesktop.wonderland.client.cell.CellChannelConnection;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveSource;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellCacheBasicImpl;
import org.jdesktop.wonderland.client.cell.CellCacheConnection;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.cell.RootCell;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.CellClientSession;
import org.jdesktop.wonderland.client.modules.ModulePluginList;
import org.jdesktop.wonderland.client.modules.ModuleUtils;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.config.WonderlandConfigUtil;
import sun.misc.Service;

/**
 *
 * @author  paulby
 */
public class CellBoundsViewer extends javax.swing.JFrame {
    
    private static final Logger logger = Logger.getLogger(CellBoundsViewer.class.getName());
    
    // properties
    private Properties props;
    
    // standard properties
    private static final String SERVER_NAME_PROP = "sgs.server";
    private static final String SERVER_PORT_PROP = "sgs.port";
    private static final String USER_NAME_PROP   = "cellboundsviewer.username";
    
    // default values
    private static final String SERVER_NAME_DEFAULT = "localhost";
    private static final String SERVER_PORT_DEFAULT = "1139";
    private static final String USER_NAME_DEFAULT   = "test";
   
    private CellClientSession session;
    
    private LocalAvatar localAvatar;
    
    private Vector3f location = new Vector3f();
    private static final float STEP = 2f;
    
    private ClassLoader loader;
    
    /** Creates new form CellBoundsViewer */
    public CellBoundsViewer(String[] args) {
        // load properties from file
        if (args.length == 1) {
            props = loadProperties(args[0]);
        } else {
            props = loadProperties(null);
        }
   
        String serverName = props.getProperty(SERVER_NAME_PROP,
                                              SERVER_NAME_DEFAULT);
        String serverPort = props.getProperty(SERVER_PORT_PROP,
                                              SERVER_PORT_DEFAULT);
        String userName   = props.getProperty(USER_NAME_PROP,
                                              USER_NAME_DEFAULT);
        WonderlandConfigUtil.setUsername(userName);
        
        initComponents();
        
        final BoundsPanel boundsPanel = new BoundsPanel();
        centerPanel.add(boundsPanel, BorderLayout.CENTER);
        this.setSize(640,480);
        
        long userNum = System.currentTimeMillis();
        
        WonderlandServerInfo server = new WonderlandServerInfo(serverName,
                                                  Integer.parseInt(serverPort));
        
        LoginParameters loginParams = new LoginParameters(userName, 
                                                          "test".toCharArray());
        
        // setup a classloader with the module jars
        loader = setupClassLoader();
        
        // create a session
        session = new CellClientSession(server, loader) {
            @Override
            protected CellCache createCellCache() {
                getCellCacheConnection().addListener(boundsPanel);
                return boundsPanel;
            }
        };
        
        // load any client plugins from that class loader
        Iterator<ClientPlugin> it = Service.providers(ClientPlugin.class,
                                                      loader);
        while (it.hasNext()) {
            ClientPlugin plugin = it.next();
            plugin.initialize(session);
        }

        boundsPanel.setSession(session);
        
        localAvatar = session.getLocalAvatar();
                
        try {
            session.login(loginParams);
        } catch (LoginFailureException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private ClassLoader setupClassLoader() {
        ModulePluginList list = ModuleUtils.fetchPluginJars();
        List<URL> urls = new ArrayList<URL>();
        
        for (String uri : list.getJarURIs()) {
            try {
                urls.add(new URL(uri));
            } catch (Exception excp) {
                excp.printStackTrace();
           }
        }
        
        return new URLClassLoader(urls.toArray(new URL[0]));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        leftB = new javax.swing.JButton();
        rightB = new javax.swing.JButton();
        forwardB = new javax.swing.JButton();
        backwardB = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        exitMI = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(640, 480));

        centerPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        leftB.setText("Left");
        leftB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(leftB, gridBagConstraints);

        rightB.setText("Right");
        rightB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel1.add(rightB, gridBagConstraints);

        forwardB.setText("Forward");
        forwardB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        jPanel1.add(forwardB, gridBagConstraints);

        backwardB.setText("Backward");
        backwardB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backwardBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        jPanel1.add(backwardB, gridBagConstraints);

        centerPanel.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        exitMI.setText("Exit");
        exitMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMIActionPerformed(evt);
            }
        });
        jMenu1.add(exitMI);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMIActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMIActionPerformed

    private void leftBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftBActionPerformed
        location.x -= STEP;
        localAvatar.localMoveRequest(location, null);
}//GEN-LAST:event_leftBActionPerformed

    private void rightBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightBActionPerformed
        location.x += STEP;
        localAvatar.localMoveRequest(location, null);
    }//GEN-LAST:event_rightBActionPerformed

    private void backwardBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backwardBActionPerformed
        location.z += STEP;
        localAvatar.localMoveRequest(location, null);

    }//GEN-LAST:event_backwardBActionPerformed

    private void forwardBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardBActionPerformed
        location.z -=STEP;
        localAvatar.localMoveRequest(location, null);

    }//GEN-LAST:event_forwardBActionPerformed
    
    
    class BoundsPanel extends JPanel implements CellCacheConnection.CellCacheMessageListener, CellCache, MovableComponent.CellMoveListener {
        private Vector3f center = new Vector3f();  // Temporary variable
        private Vector3f extent = new Vector3f();   // Temporary variable
        private float scale = 20f;
        private float panelTranslationX = 0f;
        private float panelTranslationY = 0f;
        
        // BoundsPanel actually wraps the cacheImpl
        private CellCacheBasicImpl cacheImpl;
        private CellClientSession session;
        
        private Point mousePress = null;
        
        public BoundsPanel() {
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    panelTranslationX = e.getX() - mousePress.x;
                    panelTranslationY = e.getY() - mousePress.y;
                    repaint();
                }
            });
            
            addMouseListener(new MouseAdapter() {
                @Override
               public void mousePressed(MouseEvent e) {
                   mousePress = e.getPoint();
                   mousePress.x -= panelTranslationX;
                   mousePress.y -= panelTranslationY;
               } 
            });
            
            addMouseWheelListener(new MouseWheelListener() {

                public void mouseWheelMoved(MouseWheelEvent e) {
                    int rot = e.getWheelRotation();
                    if (rot>0)
                        scale *= rot*1.2;
                    else
                        scale /= -rot*1.2;
                    repaint();
                }
                
            });
        }
        
        public WonderlandSession getSession() {
            return session;
        }
        
        public void setSession(CellClientSession session) {
            this.session = session;
            
            // setup internal cache
            cacheImpl = new CellCacheBasicImpl(session,
                                               loader,
                                               session.getCellCacheConnection(), 
                                               session.getCellChannelConnection());
        }

        @Override
        public void paint(Graphics gr) {
            Graphics2D g = (Graphics2D)gr;
            g.clearRect(0, 0, getWidth(), getHeight());
            g.translate(panelTranslationX, panelTranslationY);
            
                for(Cell c : cacheImpl.getCells())
                    drawCell(c, g);
        }
        
        private void drawCell(Cell cell, Graphics2D g) {
            if (cell instanceof RootCell)
                return;

            if (cell instanceof ViewCell) {
                drawBounds(cell.getWorldBounds(), g, true, Color.RED);
            } else {
                drawBounds(cell.getWorldBounds(), g, false, Color.BLACK);
            }

            Vector3f cellPos = cell.getWorldTransform().getTranslation(null);
            g.drawString(cell.getName(), cellPos.x*scale, cellPos.z*scale);
        }
        
        private void drawBounds(BoundingVolume bounds, Graphics2D g, boolean fill, Color color) {
            Color current = g.getColor();
            if (bounds instanceof BoundingBox) {
                BoundingBox box = (BoundingBox)bounds;
                center = box.getCenter(center);
                extent = box.getExtent(extent);
                
                if (extent.x==Float.POSITIVE_INFINITY)
                    return;

                g.setColor(color);
                if (fill) {
                   g.fillRect((int)((center.x-extent.x)*scale),
                               (int)((center.z-extent.z)*scale),
                               (int)((extent.x*2)*scale),
                               (int)((extent.z*2)*scale));
                } else {
                    g.drawRect((int)((center.x-extent.x)*scale),
                               (int)((center.z-extent.z)*scale),
                               (int)((extent.x*2)*scale),
                               (int)((extent.z*2)*scale));
                }
                g.setColor(current);
            } else if (bounds instanceof BoundingSphere) {
                BoundingSphere sphere = (BoundingSphere)bounds;
                center = sphere.getCenter(center);
                float radius = sphere.getRadius();
                
                if (radius==Float.POSITIVE_INFINITY)
                    return;

                g.setColor(color);
                if (fill) {
                    g.fillOval((int)((center.x-radius)*scale),
                               (int)((center.z-radius)*scale),
                               (int)((radius*2)*scale),
                               (int)((radius*2)*scale));

                } else {
                    g.drawOval((int)((center.x-radius)*scale),
                               (int)((center.z-radius)*scale),
                               (int)((radius*2)*scale),
                               (int)((radius*2)*scale));
                }
                g.setColor(current);
            } else {
                logger.warning("Unsupported bounds type "+bounds);
            }
        }

        public Cell loadCell(CellID cellID, 
                String className, 
                BoundingVolume localBounds, 
                CellID parentCellID, 
                CellTransform cellTransform, 
                CellClientState setup,
                String cellName) {
            System.out.println("LOAD CELL "+cellID);
            Cell ret = cacheImpl.loadCell(cellID, 
                               className, 
                               localBounds, 
                               parentCellID, 
                               cellTransform, 
                               setup,
                               cellName);
            repaint();
            
            // add a move listener
            MovableComponent mc = ret.getComponent(MovableComponent.class);
            if (mc != null) {
                mc.addServerCellMoveListener(this);
            }
            
            
            return ret;
        }

        public void unloadCell(CellID cellID) {
            System.out.println("UNLOAD CELL "+cellID);
            cacheImpl.unloadCell(cellID);
            repaint();
        }

        public void deleteCell(CellID cellID) {
            cacheImpl.deleteCell(cellID);
            repaint();
        }

        /**
         * The cell has moved. If it's a movable cell the transform has already
         * been updated, so just process the cache update. If its not a
         * movable cell then update the transform and cache.
         * 
         * @param cellID
         * @param cellTransform
         */
        public void moveCell(CellID cellID, CellTransform cellTransform) {
            cacheImpl.moveCell(cellID, cellTransform);
//            System.out.println("Cell move "+cellID);
//            System.out.println("Cell move "+cellTransform.getTranslation(null));
            repaint();
        }
        
        /*************************************************
         * CellCache implementation
         *************************************************/
        public Cell getCell(CellID cellId) {
            return cacheImpl.getCell(cellId);
        }

        public Collection<Cell> getRootCells() {
            return cacheImpl.getRootCells();
        }
        
        public void setViewCell(ViewCell viewCell) {
            cacheImpl.setViewCell(viewCell);
        }

        public ViewCell getViewCell() {
            return cacheImpl.getViewCell();
        }
        
        /*************************************************
         * End CellCache implementation
         *************************************************/

        public void cellMoved(CellTransform transform, CellMoveSource source) {
            System.out.println("Cell moved "+transform.getTranslation(null)+"  "+source);
            repaint();
        }

        public CellChannelConnection getCellChannelConnection() {
            return session.getCellChannelConnection();
        }

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CellBoundsViewer(args).setVisible(true);
            }
        });
    }
    
    private static Properties loadProperties(String fileName) {
        // start with the system properties
        Properties props = new Properties(System.getProperties());
    
        // load the given file
        if (fileName != null) {
            try {
                props.load(new FileInputStream(fileName));
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error reading properties from " +
                           fileName, ioe);
            }
        }
        
        return props;
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backwardB;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JMenuItem exitMI;
    private javax.swing.JButton forwardB;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton leftB;
    private javax.swing.JButton rightB;
    // End of variables declaration//GEN-END:variables
    
}
