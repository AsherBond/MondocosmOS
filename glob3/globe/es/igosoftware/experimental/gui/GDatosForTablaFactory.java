

package es.igosoftware.experimental.gui;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import es.igosoftware.experimental.wms.GWMSLayerData;


public class GDatosForTablaFactory {

   //private final GDatosForTabla datosForTabla;
   private static final String unselectedStyle = "             ...";


   //private static final String unselectedStyle = "    (double click)";


   public GDatosForTablaFactory() {
      //datosForTabla = null;
   }


   //@SuppressWarnings("unchecked")
   //   public GDatosForTabla getDatosForTabla(final List list,
   //                                          final Object obj) {
   //      if (obj instanceof GWMSLayerData) {
   //         datosForTabla = getWMSLayerData(list);
   //      }
   //
   //      return datosForTabla;
   //   }


   /**********************************************************************************/

   /** Pareja de Métodos necesaria para generar los datos obligatorios que tiene que tener una tabla de objetos */

   public static GDatosForTabla getWMSLayerData(final List<GWMSLayerData> lista) {

      final Vector<String> nomColumnas = new Vector<String>();
      nomColumnas.add(" Id ");
      nomColumnas.add(" Name ");
      nomColumnas.add(" Title ");
      nomColumnas.add(" Style ");
      nomColumnas.add(" Abstract ");

      final int[] tamColumnas = {
                        60,
                        180,
                        200,
                        140,
                        -1
      };

      final GDatosForTabla dat = new GDatosForTabla(lista, getCollectionWMSLayerData(lista.iterator()), nomColumnas, tamColumnas);

      return dat;
   }


   /**
    * 
    * @param it
    * @return
    */

   private static Vector<Vector<?>> getCollectionWMSLayerData(final Iterator<GWMSLayerData> it) {

      final Vector<Vector<?>> coleccion = new Vector<Vector<?>>();
      while (it.hasNext()) {
         final GWMSLayerData aux = it.next();
         final Vector<Object> v = new Vector<Object>();
         v.add(aux.getId());
         v.add(aux.getName());
         if (aux.getTitle() != null) {
            v.add(aux.getTitle());
         }
         else {
            v.add("");
         }
         v.add(unselectedStyle);
         //v.add("(double click)");
         //         final String[] stylesNames = aux.getStylesNames();
         //         if (stylesNames.length > 0) {
         //            v.add(stylesNames[0]);
         //         }
         //         else {
         //            v.add("");
         //         }

         if (aux.getAbstract() != null) {
            v.add(aux.getAbstract());
         }
         else {
            v.add("");
         }

         coleccion.add(v);
      }

      return coleccion;
   }


   public static String getUnselectedStyle() {
      return unselectedStyle;
   }

   /**********************************************************************************/


   public static class CenterTableCellRenderer
            extends
               DefaultTableCellRenderer {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;


      public CenterTableCellRenderer() {
         setHorizontalAlignment(SwingConstants.CENTER);
      }
   }

   public static class RightTableCellRenderer
            extends
               DefaultTableCellRenderer {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;


      public RightTableCellRenderer() {
         setHorizontalAlignment(SwingConstants.RIGHT);
      }
   }

}
