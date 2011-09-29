/*
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
package org.jdesktop.wonderland.modules.hud.client;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jdesktop.swingworker.SwingWorker;

/**
 * The 2D frame header Swing implementation.
 * 
 * @author nsimpson
 */
public class HUDFrameHeader2DImpl extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(HUDFrameHeader2DImpl.class.getName());
    private Color gradientStartColor = new Color(137, 137, 137); // blue: new Color(2, 28, 109);
    private Color gradientEndColor = new Color(180, 180, 180);   // blue: new Color(134, 169, 254);
    private List<ActionListener> actionListeners;
    private GradientPaint paint;

    public HUDFrameHeader2DImpl() {
        initComponents();
        addListeners();
        paint = new GradientPaint(0, 0, gradientStartColor,
                0, (int) getPreferredSize().getHeight(), gradientEndColor);
    }

    private void addListeners() {
        hudButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                logger.info("hud action performed");
                (new SwingWorker<String, Object>() {

                    @Override
                    public String doInBackground() {
                        notifyActionListeners(new ActionEvent(HUDFrameHeader2DImpl.this, e.getID(), "hud"));
                        return null;
                    }
                }).execute();
            }
        });

        minimizeButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                logger.info("minimize action performed");
                (new SwingWorker<String, Object>() {

                    @Override
                    public String doInBackground() {
                        notifyActionListeners(new ActionEvent(HUDFrameHeader2DImpl.this, e.getID(), "minimize"));
                        return null;
                    }
                }).execute();
            }
        });

        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                logger.info("close action performed");
                (new SwingWorker<String, Object>() {

                    @Override
                    public String doInBackground() {
                        notifyActionListeners(new ActionEvent(HUDFrameHeader2DImpl.this, e.getID(), "close"));
                        return null;
                    }
                }).execute();
            }
        });
    }

    public void addActionListener(ActionListener listener) {
        if (actionListeners == null) {
            actionListeners = Collections.synchronizedList(new ArrayList());
        }
        actionListeners.add(listener);
    }

    public void notifyActionListeners(ActionEvent e) {
        if (actionListeners != null) {
            ListIterator<ActionListener> iter = actionListeners.listIterator();
            while (iter.hasNext()) {
                ActionListener listener = iter.next();
                listener.actionPerformed(e);
            }
            iter = null;
        }
    }

    public void setFrameColor(Color color) {
        setFrameColor(color, color);
    }

    public void setFrameColor(Color startColor, Color endColor) {
        setGradientStartColor(startColor);
        setGradientEndColor(endColor);
        repaint();
    }

    public void setTextColor(Color textColor) {
        titleLabel.setForeground(textColor);
    }

    public void setGradientStartColor(Color gradientStartColor) {
        this.gradientStartColor = gradientStartColor;
        paint = new GradientPaint(0, 0, gradientStartColor,
                0, (int) getPreferredSize().getHeight(), gradientEndColor);
    }

    public Color getGradientStartColor() {
        return gradientStartColor;
    }

    public void setGradientEndColor(Color gradientEndColor) {
        this.gradientEndColor = gradientEndColor;
        paint = new GradientPaint(0, 0, gradientStartColor,
                0, (int) getPreferredSize().getHeight(), gradientEndColor);
    }

    public Color getGradientEndColor() {
        return gradientEndColor;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void showHUDButton(boolean show) {
        hudButton.setVisible(show);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setPaint(paint);
        g2.fill(g2.getClip());
        paintChildren(g);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        minimizeButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        hudButton = new javax.swing.JButton();

        minimizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/minimize16x16.png"))); // NOI18N
        minimizeButton.setBorderPainted(false);
        minimizeButton.setIconTextGap(0);
        minimizeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        minimizeButton.setMaximumSize(new java.awt.Dimension(16, 16));
        minimizeButton.setMinimumSize(new java.awt.Dimension(2, 2));

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/close16x16.png"))); // NOI18N
        closeButton.setBorderPainted(false);
        closeButton.setIconTextGap(0);
        closeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        closeButton.setMaximumSize(new java.awt.Dimension(16, 16));
        closeButton.setMinimumSize(new java.awt.Dimension(2, 2));

        titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | java.awt.Font.BOLD, titleLabel.getFont().getSize()+1));
        titleLabel.setForeground(new java.awt.Color(255, 255, 255));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setMinimumSize(new java.awt.Dimension(0, 17));

        hudButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/hideHUD16x16.png"))); // NOI18N
        hudButton.setBorderPainted(false);
        hudButton.setIconTextGap(0);
        hudButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        hudButton.setMaximumSize(new java.awt.Dimension(16, 16));
        hudButton.setMinimumSize(new java.awt.Dimension(2, 2));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hudButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(minimizeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(minimizeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(layout.createSequentialGroup()
                .add(hudButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0))
            .add(titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton hudButton;
    private javax.swing.JButton minimizeButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame(java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/hud/client/resources/Bundle").getString("HUD_FRAME_TEST"));
                frame.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                    }
                });
                frame.add(new HUDFrameHeader2DImpl());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
