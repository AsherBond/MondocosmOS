

package es.igosoftware.experimental.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableCellRenderer;

import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;


public class GDatosForTabla {

   private final ILogger                    logger          = GLogger.instance();

   private List                             _listElements   = null;
   private Vector<Vector<?>>                _coleccion      = null;
   private Vector<String>                   _nomColumnas    = null;
   private int[]                            _tamColumnas    = null;
   private Vector<DefaultTableCellRenderer> _formatColmunas = null;


   public Vector<DefaultTableCellRenderer> getFormatColmunas() {
      return _formatColmunas;
   }


   public void setFormatColmunas(final Vector<DefaultTableCellRenderer> formatColmunas) {
      _formatColmunas = formatColmunas;
   }


   public GDatosForTabla() {
   }


   public GDatosForTabla(final List listElements,
                         final Vector<Vector<?>> coleccion,
                         final Vector<String> nomColumnas,
                         final int[] tamColumnas) {
      _listElements = listElements;
      _coleccion = coleccion;
      _nomColumnas = nomColumnas;
      _tamColumnas = tamColumnas;
   }


   public boolean isOk() {
      boolean isOk = true;
      if ((_tamColumnas == null) || (_nomColumnas == null) || (_coleccion == null) || (_listElements == null)) {
         logger.logInfo("Alguno de los elmentos est� a null");
         isOk = false;
      }
      else {
         if (isOk && (_listElements.size() != _coleccion.size())) {
            logger.logSevere("El n�mero de Elementos de la Lista (" + _listElements.size()
                             + ") no coincide con el n�mero de Elementos de la Colecci�n (" + _coleccion.size() + ")");
            isOk = false;
         }
         if (isOk && (_tamColumnas.length != _nomColumnas.size())) {
            logger.logSevere("El n�mero de Columnas (" + _nomColumnas.size()
                             + ") no coincide con el n�mero de tama�os especificados (" + _tamColumnas.length + ")");
            isOk = false;
         }
         if (isOk && (_listElements.size() > 0)) {
            if (_nomColumnas.size() != _coleccion.get(0).size()) {
               logger.logSevere("El n�mero de Columnas (" + _nomColumnas.size()
                                + ") no coincide con el n�mero de datos que se est�n pasando (" + _coleccion.get(0).size() + ")");
               isOk = false;
            }
         }
      }

      return isOk;
   }


   /**
    * 
    * @param _coleccion
    * @param element
    * @return
    */
   public void deleteElementSelected(final Vector<?> element) {
      final int tamCollection = _coleccion.size();
      for (int i = 0; i < tamCollection; i++) {
         final Vector<?> elementAux = _coleccion.get(i);
         if (((Integer) element.get(0)).intValue() == ((Integer) elementAux.get(0)).intValue()) {
            _coleccion.remove(i);
            i = tamCollection;
         }
      }
   }


   public void removeObjectByIndex(final int index) {
      try {
         _listElements.remove(index);
      }
      catch (final Exception ex) {
         logger.logSevere("ERROR" + ex.getCause());
      }
   }


   public void deleteElementByIndex(final int index) {
      try {
         _listElements.remove(index);
         _coleccion.remove(index);
      }
      catch (final Exception ex) {
         logger.logSevere("ERROR" + ex.getCause());
      }

   }


   public List getListElements() {
      return _listElements;
   }


   public void setListElements(final List listElements) {
      _listElements = listElements;
   }


   public Object getObjectByIndex(final int index) {
      if (index >= 0) {
         try {
            return _listElements.get(index);
         }
         catch (final Exception ex) {
            logger.logSevere("ERROR: " + ex.getCause());
            return null;
         }
      }
      logger.logInfo("INFO: INDEX < 0)");
      return null;

   }


   public Vector getRowDataObject(final Object obj) {
      for (int i = 0; i < _listElements.size(); i++) {
         final Object aux = _listElements.get(i);
         if (aux.equals(obj)) {
            return _coleccion.get(i);
         }
      }

      return null;
   }


   @SuppressWarnings("unchecked")
   public void updateObject(final Object newObj) {
      if ((newObj != null) && (_listElements != null)) {
         for (int i = 0; i < _listElements.size(); i++) {
            final Object oldObj = _listElements.get(i);
            if (oldObj.equals(newObj)) {
               _listElements.set(i, newObj);
               i = _listElements.size();
               //_coleccion = new GDatosForTablaFactory().getDatosForTabla(_listElements, newObj).getColeccion();
               _coleccion = GDatosForTablaFactory.getWMSLayerData(_listElements).getColeccion();
            }
         }
      }
      else {
         logger.logInfo("(public void updateObject(Object newObj))El objeto que se quiere actualizar no est� el la lista de datos ");
      }
   }


   @SuppressWarnings("unchecked")
   public void addObject(final Object newObj) {
      if (newObj != null) {
         _listElements.add(newObj);
         //_coleccion = new GDatosForTablaFactory().getDatosForTabla(_listElements, newObj).getColeccion();
         _coleccion = GDatosForTablaFactory.getWMSLayerData(_listElements).getColeccion();
      }
   }


   @SuppressWarnings("unchecked")
   public void addElementToList(final Object obj) {
      _listElements.add(obj);
   }


   public void removeAllObjects() {
      _listElements = new ArrayList();
   }


   public Vector<Vector<?>> getColeccion() {
      return _coleccion;
   }


   public void setColeccion(final Vector<Vector<?>> coleccion) {
      _coleccion = coleccion;
   }


   public Vector<String> getNomColumnas() {
      return _nomColumnas;
   }


   public void setNomColumnas(final Vector<String> nomColumnas) {
      _nomColumnas = nomColumnas;
   }


   public int[] getTamColumnas() {
      return _tamColumnas;
   }


   public void setTamColumnas(final int[] tamColumnas) {
      _tamColumnas = tamColumnas;
   }


}
