

package es.igosoftware.experimental.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;
import es.igosoftware.experimental.wms.GWMSLayerData;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;


public class GTablaIgoCusto
         extends
            JPanel {

   /**
	 * 
	 */
   private static final long    serialVersionUID = 2644357468902374987L;

   private static final int     stylesColumn     = 3;

   private final static ILogger logger           = GLogger.instance();


   private DefaultTableModel    model            = null;
   private JTable               table            = null;
   private JScrollPane          jsContenedor     = null;
   private GDatosForTabla       _datos           = null;

   private TableColumn          _styleColumn     = null;
   private JComboBox            _stylesComboBox  = null;


   public GTablaIgoCusto(final GDatosForTabla datos) {
      _datos = datos;

      initialize();
      final DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
      final Font originalFont = headerRenderer.getFont();
      final Font boldFont = new Font(originalFont.getName(), Font.BOLD, originalFont.getSize());
      table.getTableHeader().setFont(boldFont);
      table.getTableHeader().setBackground(new Color(78, 131, 223));
      table.getTableHeader().setForeground(Color.WHITE);
      table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 25));
      headerRenderer.setToolTipText("Double click for style selection");
      table.getTableHeader().setDefaultRenderer(headerRenderer);
   }


   private void initialize() {
      setLayout(new MigLayout("wrap 1", "[grow]", "[grow]"));
      model = new WMSTableModel(_datos.getColeccion(), _datos.getNomColumnas());
      table = new JTable(model);

      initializeColumnModel();

      final MousePressedListener mListener = new MousePressedListener();
      table.addMouseListener(mListener);

      jsContenedor = new JScrollPane(table);
      this.add(jsContenedor, "grow");

   }


   private void initializeColumnModel() {
      final DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
      final int numColumnas = table.getColumnCount();
      for (int i = 0; i < numColumnas; i++) {
         final TableColumn col = colModel.getColumn(i);
         if ((_datos.getFormatColmunas() != null) && (_datos.getFormatColmunas().size() == numColumnas)
             && (_datos.getFormatColmunas().get(i) != null)) {
            col.setCellRenderer(_datos.getFormatColmunas().get(i));
         }
         if (_datos.getTamColumnas()[i] != -1) {
            col.setPreferredWidth(_datos.getTamColumnas()[i]);
            col.setMaxWidth(_datos.getTamColumnas()[i]);
         }
      }

      _stylesComboBox = new JComboBox();
      _styleColumn = table.getColumnModel().getColumn(stylesColumn);

      final DefaultCellEditor editor = new DefaultCellEditor(_stylesComboBox);
      editor.setClickCountToStart(2);
      _styleColumn.setCellEditor(editor);

      final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
      renderer.setToolTipText("Double click for style selection");
      _styleColumn.setCellRenderer(renderer);

      table.setColumnModel(colModel);
   }


   public class MousePressedListener
            implements
               MouseListener {

      @Override
      public void mouseClicked(final MouseEvent e) {

         final GWMSLayerData selectedLayer = (GWMSLayerData) getSelectedObject();
         final int initialItemsCount = _stylesComboBox.getItemCount();
         for (final String style : selectedLayer.getStylesNames()) {
            _stylesComboBox.addItem(style);
         }
         for (int i = 0; i < initialItemsCount; i++) {
            _stylesComboBox.removeItemAt(0);
         }
         _stylesComboBox.paintImmediately(_stylesComboBox.getBounds());

      }


      @Override
      public void mousePressed(final MouseEvent e) {
      }


      @Override
      public void mouseReleased(final MouseEvent e) {
      }


      @Override
      public void mouseEntered(final MouseEvent e) {
      }


      @Override
      public void mouseExited(final MouseEvent e) {
      }

   }


   public void setShowVerticalLines(final boolean show) {
      table.setShowVerticalLines(show);
   }


   public void setShowHorizontalLines(final boolean show) {
      table.setShowHorizontalLines(show);
   }


   public void removeRowSelected() {
      try {
         final int rowIndex = table.getSelectedRow();
         //model.removeRow(rowIndex) implica el borrado en la coleccion de datos tambi�n (datos.getColeccion())
         model.removeRow(rowIndex);
         _datos.removeObjectByIndex(rowIndex);
      }
      catch (final ArrayIndexOutOfBoundsException ex) {
         logger.logSevere(ex.getCause());
      }
   }


   public Object getSelectedObject() {
      return _datos.getObjectByIndex(table.getSelectedRow());
   }


   public Object[] getSelectedObjects() {
      final int rowCount = table.getSelectedRowCount();
      final int[] indexList = table.getSelectedRows();
      final Object[] objectList = new Object[rowCount];
      int index = 0;
      for (final int rowIndex : indexList) {
         objectList[index] = _datos.getObjectByIndex(rowIndex);
         index++;
      }
      return objectList;
   }


   public String[] getSelectedStyles() {
      final int rowCount = table.getSelectedRowCount();
      final int[] indexList = table.getSelectedRows();
      final String[] stylesList = new String[rowCount];
      int index = 0;
      for (final int rowIndex : indexList) {
         final String styleSelection = (String) model.getValueAt(rowIndex, stylesColumn);
         stylesList[index] = styleSelection;
         index++;
      }
      return stylesList;
   }


   public List getListObjects() {
      return _datos.getListElements();
   }


   public void updateRow(final Object newObj) {
      if ((_datos != null) && (newObj != null)) {
         _datos.updateObject(newObj);
         updateModel();
      }

   }


   public void addRow(final Object newObj) {
      if ((_datos != null) && (newObj != null)) {
         _datos.addObject(newObj);
         model.addRow(_datos.getRowDataObject(newObj));
         table.setModel(model); //-- añadido fpulido
      }
   }


   public void addRow(final String[] stringList) {
      final Vector<Object> v = new Vector<Object>();
      for (final String element : stringList) {
         v.add(element);
      }
      model.addRow(v);
      table.setModel(model);
   }


   public void updateRow(final Object obj,
                         final String[] stringList) {
      _datos.updateObject(obj);
      final int pos = table.getSelectedRow();
      final Vector<Object> v = new Vector<Object>();
      for (final String element : stringList) {
         v.add(element);
      }
      model.removeRow(pos);
      model.insertRow(pos, v);
      table.setModel(model);
   }


   public void removeAllRow() {
      _datos.removeAllObjects();
      while (model.getRowCount() > 0) {
         model.removeRow(model.getRowCount() - 1);
      }
      table.setModel(model);
   }


   public void setDatosForTabla(final GDatosForTabla datos) {
      if ((datos != null) && datos.isOk()) {
         _datos = datos;
         updateModel();
      }
      else {
         logger.logInfo("Incorrect data. Not added to the table !");
      }

   }


   private void updateModel() {
      while (model.getRowCount() > 0) {
         model.removeRow(0);
      }
      for (int i = 0; i < _datos.getListElements().size(); i++) {
         model.addRow(_datos.getRowDataObject(_datos.getListElements().get(i)));
      }
   }


   public JTable getTable() {
      return table;
   }


   private static class WMSTableModel
            extends
               DefaultTableModel {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;


      public WMSTableModel(final Vector<Vector<?>> coleccion,
                           final Vector<String> nomColumnas) {
         super(coleccion, nomColumnas);
      }


      @Override
      public boolean isCellEditable(final int row,
                                    final int column) {

         if (column == stylesColumn) {
            return true;
         }

         return false;
      }
   }


}
