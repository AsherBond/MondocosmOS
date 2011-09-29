/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.globe.modules.view;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;

import es.igosoftware.util.GPair;


public class GInfoToolDialog
         extends
            JDialog
         implements
            WindowListener {
   private static final long      serialVersionUID = 1L;

   private static GInfoToolDialog _dialog;

   private JScrollPane            jScrollPaneList;
   private JScrollPane            jScrollPaneTable;
   private JTable                 jTable;
   private JList                  jList;


   public GInfoToolDialog(final Frame parent,
                          final GPointInfo[] info) {

      super(parent, "Info", false);

      initGUI(info);
      setLocationRelativeTo(null);

      _dialog = this;

   }


   private void initGUI(final GPointInfo[] info) {

      final TableLayout thisLayout = new TableLayout(new double[][] {
                        {
                                          3.0,
                                          TableLayoutConstants.FILL,
                                          TableLayoutConstants.FILL,
                                          7.0,
                                          TableLayoutConstants.FILL,
                                          TableLayoutConstants.FILL,
                                          TableLayoutConstants.FILL,
                                          3.0
                        },
                        {
                                          3.0,
                                          TableLayoutConstants.FILL,
                                          3.0
                        }
      });
      thisLayout.setHGap(5);
      thisLayout.setVGap(5);
      getContentPane().setLayout(thisLayout);
      setAlwaysOnTop(true);
      {
         jScrollPaneList = new JScrollPane();
         getContentPane().add(jScrollPaneList, "1, 1, 2, 1");
         {
            final ListModel jListModel = new DefaultComboBoxModel(info);
            jList = new JList();
            jScrollPaneList.setViewportView(jList);
            jList.setModel(jListModel);
            jList.addMouseListener(new MouseAdapter() {
               @Override
               public void mouseClicked(final MouseEvent e) {
                  if (e.getClickCount() == 1) {
                     final int iIndex = jList.locationToIndex(e.getPoint());
                     final ListModel dlm = jList.getModel();
                     final Object item = dlm.getElementAt(iIndex);
                     updateTable((GPointInfo) item);
                     jList.ensureIndexIsVisible(iIndex);
                  }
               }

            });
         }
      }
      {
         jScrollPaneTable = new JScrollPane();
         getContentPane().add(jScrollPaneTable, "4, 1, 6, 1");
         {
            final DefaultTableModel jTableModel = new DefaultTableModel();
            jTableModel.setColumnIdentifiers(new String[] {
                              "Parameter",
                              "Value"
            });
            jTable = new JTable();
            jScrollPaneTable.setViewportView(jTable);
            jTable.setModel(jTableModel);
         }
         updateTable(info[0]);
      }
      this.setSize(613, 392);

      addWindowListener(this);

   }


   public void updateInfo(final GPointInfo[] info) {

      if (info.length != 0) {
         final ListModel jListModel = new DefaultComboBoxModel(info);
         jList.setModel(jListModel);
         updateTable(info[0]);
      }

   }


   protected void updateTable(final GPointInfo info) {

      final DefaultTableModel model = new DefaultTableModel();
      model.setColumnIdentifiers(new String[] {
                        "Parameter",
                        "Value"
      });
      for (final GPair<String, Object> element : info._info) {
         model.addRow(new Object[] {
                           element._first,
                           element._second
         });
      }
      jTable.setModel(model);

   }


   public static GInfoToolDialog getCurrentInfoDialog() {

      return _dialog;

   }


   @Override
   public void windowActivated(final WindowEvent e) {

   }


   @Override
   public void windowClosed(final WindowEvent e) {

      _dialog = null;

   }


   @Override
   public void windowClosing(final WindowEvent e) {
   }


   @Override
   public void windowDeactivated(final WindowEvent e) {
   }


   @Override
   public void windowDeiconified(final WindowEvent e) {
   }


   @Override
   public void windowIconified(final WindowEvent e) {
   }


   @Override
   public void windowOpened(final WindowEvent e) {
   }

}
