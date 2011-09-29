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
package org.jdesktop.wonderland.modules.palette.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry.CellRegistryListener;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.modules.palette.client.dnd.CellServerStateTransferable;

/**
 * A palette of cells to create in the world by drag and drop, as a HUD panel.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 * @author nsimpson
 */
public class HUDCellPalette extends javax.swing.JPanel {

    private Map<String, CellFactorySPI> cellFactoryMap = new HashMap();
    private List<Image> imageList;
    private DefaultListModel model;
    private Image noPreviewAvailableImage = null;
    private CellRegistryListener cellListener = null;
    private static final int SIZE = 48;

    public HUDCellPalette() {
        initComponents();

        imageList = new ArrayList();
        model = new DefaultListModel();
        componentList.setModel(model);
        componentList.setCellRenderer(new ListImageRenderer());
        componentList.setDragEnabled(true);

        // Create a generic image for cells that don't have a preview image
        URL url = CellPalette.class.getResource("resources/nopreview128x128.png");
        noPreviewAvailableImage = Toolkit.getDefaultToolkit().createImage(url);

        componentList.setTransferHandler(new ListTransferHandler());

        // Create a listener for changes to the list of registered Cell
        // factories, to be used in setVisible(). When the list changes we
        // simply do a fresh update of all values.
        cellListener = new CellRegistryListener() {

            public void cellRegistryChanged() {
                // Since this is not happening (necessarily) in the AWT Event
                // Thread, we should put it in one
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        updatePanelIcons();
                    }
                });
            }
        };
        CellRegistry.getCellRegistry().addCellRegistryListener(cellListener);
        updatePanelIcons();
    }

    public class ListTransferHandler extends TransferHandler {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
            // the cell palette doesn't support import
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Transferable createTransferable(JComponent c) {
            CellFactorySPI factory = cellFactoryMap.get((String) componentList.getSelectedValue());

            return new CellServerStateTransferable(factory);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getSourceActions(JComponent c) {
            // only support copying
            return TransferHandler.COPY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Icon getVisualRepresentation(Transferable t) {
            Image image = imageList.get(componentList.getSelectedIndex());
            ImageIcon icon = new ImageIcon(image);
            return (Icon) image;
        }
    }

    public class ListImageRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean hasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list,
                    value, index, isSelected, hasFocus);
            Image icon = imageList.get(index);
            label.setIcon(new ImageIcon(icon));

            label.setText("");
            label.setTransferHandler(new ListTransferHandler());
            CellFactorySPI factory = cellFactoryMap.get((String)value);
            label.setToolTipText(factory.getDisplayName());

//            // Set up the drag and drop support for the image
//            DragSource ds = DragSource.getDefaultDragSource();
//            PaletteDragGestureListener listener = new PaletteDragGestureListener();
//            listener.previewImage = icon;
//            listener.cellFactory = cellFactoryMap.get((String)value);
//            ds.createDefaultDragGestureRecognizer(componentList,
//                    DnDConstants.ACTION_COPY, listener);

            return (label);
        }
    }

    /**
     * Updates the list of values displayed from the CellRegistry.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     */
    public void updatePanelIcons() {
        // We synchronized around the cellFactoryMap so that this action does not
        // interfere with any changes in the map.
        synchronized (cellFactoryMap) {
            // First remove all of the entries in the map and the panel
            cellFactoryMap.clear();
            model.clear();
            componentList.removeAll();
            imageList.clear();

            // Fetch the registry of cells and for each, get the palette info and
            // populate the list.
            CellRegistry registry = CellRegistry.getCellRegistry();
            Set<CellFactorySPI> cellFactories = registry.getAllCellFactories();

            for (CellFactorySPI cellFactory : cellFactories) {
                try {
                    // We only add the entry if it has a non-null display name.
                    // Fetch the preview image (use the default if none exists
                    // and add to the panel
                    String name = cellFactory.getDisplayName();
                    Image preview = cellFactory.getPreviewImage();

                    if (name != null) {
                        model.addElement(name);
                        cellFactoryMap.put(name, cellFactory);
                        // Store the image for the list renderer
                        Image image = createScaledImage(preview, name, SIZE);

                        imageList.add(image);
                    }
                } catch (java.lang.Exception excp) {
                    // Just ignore, but log a message
                    Logger logger = Logger.getLogger(CellPalette.class.getName());
                    logger.log(Level.WARNING, "No Display Name for Cell Factory " +
                            cellFactory, excp);
                }
            }
            componentList.invalidate();
        }
    }

    /**
     * Creates a new label given the Image, the cell name, and the size to make
     * it.
     */
    private Image createScaledImage(Image image, String displayName, int size) {
        ImageIcon srcImage;
        boolean label = false;

        if (image == null) {
            // If the preview image is null, then use the default one
            srcImage = new ImageIcon(noPreviewAvailableImage);
            label = true;
        } else {
            srcImage = new ImageIcon(image);
        }

        // First resize the image. We use a trick to fetch the BufferedImage
        // from the given Image, by creating the ImageIcon and calling the
        // getImage() method. Then resize into a Buffered Image.
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImage.getImage(), 0, 0, size, size, null);
        g2.dispose();

        if (label) {
            labelImage(resizedImage, displayName);
        }

        return resizedImage;
    }

    private Image labelImage(BufferedImage image, String label) {
        if (label != null) {
            Graphics2D g2 = image.createGraphics();

            String[] words = label.split(" ");

            // find the longest word
            String longest = "";
            for (int i = 0; i < words.length; i++) {
                if (words[i].length() > longest.length()) {
                    longest = words[i];
                }
            }

            int rowGap = 3;
            int x;
            int y = rowGap;
            int iw = image.getWidth();
            int ih = image.getHeight();

            int fontSize = 10;
            Font font = new Font("SansSerif", Font.BOLD, fontSize);
            FontMetrics metrics = g2.getFontMetrics(font);

            // find the font required to fit the longest word in the width of
            // the image
            while (!(metrics.getStringBounds(longest, g2).getWidth() < iw - 2) &&
                    fontSize > 5) {
                fontSize--;
                font = new Font("SansSerif", Font.BOLD, fontSize);
                metrics = g2.getFontMetrics(font);
            }

            g2.setFont(font);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.DARK_GRAY);

            Rectangle2D maxBounds = metrics.getMaxCharBounds(g2);
            int wh = (int) maxBounds.getHeight();

            // draw each word in order from top to bottom, centered in row
            for (int i = 0; i < words.length; i++) {
                Rectangle2D stringBounds = metrics.getStringBounds(words[i], g2);
                x = (int) (iw / 2 - stringBounds.getWidth() / 2);
                g2.drawString(words[i], x, y + wh);
                y += wh + rowGap;
            }

            g2.dispose();
        }
        return image;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        componentScrollPane = new javax.swing.JScrollPane();
        componentList = new javax.swing.JList();

        setPreferredSize(new java.awt.Dimension(530, 75));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        componentScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        componentList.setBackground(new java.awt.Color(0, 0, 0));
        componentList.setDragEnabled(true);
        componentList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        componentList.setVisibleRowCount(1);
        componentScrollPane.setViewportView(componentList);

        add(componentScrollPane);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList componentList;
    private javax.swing.JScrollPane componentScrollPane;
    // End of variables declaration//GEN-END:variables
}
