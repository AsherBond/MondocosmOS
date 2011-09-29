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

package org.jdesktop.wonderland.modules.contentrepo.client.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 *
 * @author jkaplan
 */
public class BrowserFrame extends javax.swing.JFrame {
    private static final Logger logger =
            Logger.getLogger(BrowserFrame.class.getName());

    private ContentRepository repo;
    private ContentCollection directory;

    /** Creates new form BrowserFrame */
    public BrowserFrame(ContentRepository repo) {
        this.repo = repo;
        
        initComponents();

        fileList.setCellRenderer(new ContentRenderer());
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        try {
            setCollection(repo.getUserRoot());
        } catch (ContentRepositoryException cce) {
            logger.log(Level.WARNING, "Error getting user root", cce);
        }

        fileList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                changeSelection();
            }
        });

        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    changeDirectory();
                }
            }
        });

        changeSelection();

        mkdirTF.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                mkdirButton.setEnabled(mkdirTF.getText().trim().length() > 0);
            }
        });
    
        fileTF.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }
            
            private void update() {
                String fileName = fileTF.getText().trim();
                boolean enabled = fileName.length() > 0 &&
                                  new File(fileName).exists();
                
                uploadButton.setEnabled(enabled);
            }
        });
    }

    private ContentNode getSelection() {
        ContentNode selected = null;

        Object selectedObj = fileList.getSelectedValue();
        if (selectedObj instanceof ParentHolder) {
            selected = ((ParentHolder) selectedObj).getParent();
        } else if (selectedObj != null) {
            selected = (ContentNode) selectedObj;
        }

        return selected;
    }

    private void changeSelection() {
        ContentNode selected = getSelection();
        
        boolean enableDownload = false;
        boolean enableDelete = false;
        if (selected == null) {
            typeLabel.setText("");
            sizeLabel.setText("");
            modifiedLabel.setText("");
            urlLabel.setText("");
        } else if (selected instanceof ContentCollection) {
            typeLabel.setText("Directory");
            sizeLabel.setText("");
            modifiedLabel.setText("");
            urlLabel.setText("");

            enableDelete = true;
        } else if (selected instanceof ContentResource) {
            ContentResource r = (ContentResource) selected;

            typeLabel.setText("File");
            sizeLabel.setText(String.valueOf(r.getSize()));
            
            DateFormat df = DateFormat.getDateInstance();
            modifiedLabel.setText(df.format(r.getLastModified()));

            try {
                urlLabel.setText(r.getURL().toExternalForm());
            } catch (ContentRepositoryException cre) {
                logger.log(Level.WARNING, "Unable to get URL for " + r, cre);
                urlLabel.setText("Error: " + cre.getMessage());
            }

            enableDownload = true;
            enableDelete = true;
        }

        downloadButton.setEnabled(enableDownload);
        deleteButton.setEnabled(enableDelete);
    }

    private void changeDirectory() {
        ContentNode selected = getSelection();
        if (selected instanceof ContentCollection) {
            setCollection((ContentCollection) selected);
        }
    }

    private void setCollection(ContentCollection collection) {
        directory = collection;
        dirNameLabel.setText(collection.getPath());
        fileList.setModel(new ContentListModel(collection));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        downloadButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        dirNameLabel = new javax.swing.JLabel();
        fileTF = new javax.swing.JTextField();
        uploadButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        modifiedLabel = new javax.swing.JLabel();
        sizeLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        urlLabel = new javax.swing.JLabel();
        mkdirTF = new javax.swing.JTextField();
        mkdirButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setTitle("Content Repository Browser");

        fileList.setFont(fileList.getFont());
        fileList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(fileList);

        downloadButton.setFont(downloadButton.getFont());
        downloadButton.setText("Download...");
        downloadButton.setEnabled(false);
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText("Directory:");

        dirNameLabel.setFont(dirNameLabel.getFont());
        dirNameLabel.setText("directory name");

        uploadButton.setFont(uploadButton.getFont());
        uploadButton.setText("Upload");
        uploadButton.setEnabled(false);
        uploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });

        browseButton.setFont(browseButton.getFont());
        browseButton.setText("Browse...");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected File"));
        jPanel1.setFont(jPanel1.getFont());

        jLabel2.setFont(jLabel2.getFont());
        jLabel2.setText("Type:");

        jLabel3.setFont(jLabel3.getFont());
        jLabel3.setText("Size:");

        jLabel4.setFont(jLabel4.getFont());
        jLabel4.setText("Modified:");

        modifiedLabel.setFont(modifiedLabel.getFont());
        modifiedLabel.setText("modified");

        sizeLabel.setFont(sizeLabel.getFont());
        sizeLabel.setText("size");

        typeLabel.setFont(typeLabel.getFont());
        typeLabel.setText("type");

        jLabel5.setFont(jLabel5.getFont());
        jLabel5.setText("URL:");

        urlLabel.setFont(urlLabel.getFont());
        urlLabel.setText("url");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(urlLabel)
                    .add(typeLabel)
                    .add(sizeLabel)
                    .add(modifiedLabel))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(typeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(sizeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(modifiedLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(urlLabel))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        mkdirButton.setFont(mkdirButton.getFont());
        mkdirButton.setText("New Directory");
        mkdirButton.setEnabled(false);
        mkdirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mkdirButtonActionPerformed(evt);
            }
        });

        deleteButton.setFont(deleteButton.getFont());
        deleteButton.setText("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dirNameLabel))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, mkdirTF)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, fileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mkdirButton)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(browseButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 15, Short.MAX_VALUE)
                                .add(uploadButton)
                                .add(3, 3, 3))
                            .add(layout.createSequentialGroup()
                                .add(deleteButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(downloadButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {fileTF, mkdirTF}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(dirNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(deleteButton)
                            .add(downloadButton)))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                .add(2, 2, 2)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mkdirTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mkdirButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(uploadButton)
                    .add(browseButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mkdirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mkdirButtonActionPerformed
        String name = mkdirTF.getText().trim();
        try {
            directory.createChild(name, ContentNode.Type.COLLECTION);
            setCollection(directory);
        } catch (ContentRepositoryException ex) {
            logger.log(Level.WARNING, "Unable to create directory", ex);
        }
    }//GEN-LAST:event_mkdirButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileTF.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadButtonActionPerformed
        File file = new File(fileTF.getText());
        if (file.exists()) {
            try {
                ContentResource r = (ContentResource)
                        directory.createChild(file.getName(), ContentNode.Type.RESOURCE);
                r.put(file);

                setCollection(directory);
            } catch (ContentRepositoryException cre) {
                logger.log(Level.WARNING, "Unable to upload " + file, cre);
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Unable to read " + file, ioe);
            }
        }
    }//GEN-LAST:event_uploadButtonActionPerformed

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        ContentResource selected = (ContentResource) getSelection();

        JFileChooser chooser = new JFileChooser("Choose a directory");
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Directories";
            }
        });
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File out = new File(chooser.getSelectedFile(), selected.getName());
            try {
                selected.get(out);
            } catch (ContentRepositoryException cre) {
                logger.log(Level.WARNING, "Unable to download " + out, cre);
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Unable to write " + out, ioe);
            }
        }
    }//GEN-LAST:event_downloadButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        ContentNode selected = getSelection();

        try {
            directory.removeChild(selected.getName());
        } catch (ContentRepositoryException cre) {
            logger.log(Level.WARNING, "Error removing " + selected.getPath(),
                       cre);
        }

        setCollection(directory);
    }//GEN-LAST:event_deleteButtonActionPerformed

    class ContentRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object obj,
                int index, boolean selected, boolean hasFocus)
        {
            String desc;
            if (obj instanceof ParentHolder) {
                desc = "..";
            } else if (obj instanceof ContentNode) {
                desc = ((ContentNode) obj).getName();
            } else {
                desc = obj.toString();
            }

            return super.getListCellRendererComponent(list, desc, index,
                                                      selected, hasFocus);
        }

    }

    class ContentListModel extends AbstractListModel {
        private ContentCollection dir;
        private boolean hasParent;

        public ContentListModel(ContentCollection dir) {
            this.dir = dir;

            hasParent = (dir.getParent() != null);
        }
        
        public int getSize() {
            try {
                int size = dir.getChildren().size();

                if (hasParent) {
                    size += 1;
                }

                return size;
            } catch (ContentRepositoryException cce) {
                logger.log(Level.WARNING, "Error getting size of " +
                           dir.getName(), cce);
                return 0;
            }
        }

        public Object getElementAt(int index) {
            if (index == 0 && hasParent) {
                return new ParentHolder(dir.getParent());
            } else if (hasParent) {
                index -= 1;
            }

            try {
                return dir.getChildren().get(index);
            } catch (ContentRepositoryException cce) {
                logger.log(Level.WARNING, "Error reading child from " +
                           dir.getName(), cce);
                return "Error: " + cce.getMessage();
            }
        }
    }

    class ParentHolder {
        private ContentCollection parent;

        public ParentHolder(ContentCollection parent) {
            this.parent = parent;
        }

        public ContentCollection getParent() {
            return parent;
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel dirNameLabel;
    private javax.swing.JButton downloadButton;
    private javax.swing.JList fileList;
    private javax.swing.JTextField fileTF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton mkdirButton;
    private javax.swing.JTextField mkdirTF;
    private javax.swing.JLabel modifiedLabel;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JButton uploadButton;
    private javax.swing.JLabel urlLabel;
    // End of variables declaration//GEN-END:variables

}
